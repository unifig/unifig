package etl.dispatch.gather.api.umeng.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.tools.plugin.utils.NewMapUtil;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.gather.api.umeng.base.UmengBase;
import etl.dispatch.gather.api.umeng.service.impl.UmengAppsServiceImpl;
import etl.dispatch.gather.api.umeng.service.impl.UmengAuthorizeServiceImpl;
import etl.dispatch.gather.api.umeng.service.impl.UmengEventsServiceImpl;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.domain.DimAppVersion;
import etl.dispatch.java.ods.service.OdsFullDimHolderService;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.JdbcUtil;
import etl.dispatch.script.util.ScriptTimeUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.Exceptions;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

/**
 * 事件统计接口访问
 *
 * 事件表：
	日期： 20171128 
	应用：多个（必选一个）(plat_id)
	版本：全部、具体版本，传值 (version_id)
	group_id：  58ed8f923eae2542f20006c7 
	事件ID：ZB_Search_Event (event_id)
	事件名称：搜索事件 (event_name)
	昨天天消息数 (message_count)
	昨天天用户数  (user_count)
 */
@Service
@SuppressWarnings("all")
public class Events_ApiEventsValServiceImpl extends AbstractScript implements ScheduledService {
	private static Logger logger = LoggerFactory.getLogger(Events_ApiEventsValServiceImpl.class);
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static DimAppPlat APPPLAT_NOT_PRESENT = new DimAppPlat();
	private final static DimAppVersion APPVERSION_NOT_PRESENT = new DimAppVersion();
	private String EVENT_URL = "";
	private String USER_NAME = "";
	private String PASS_WORD = "";
	private final String POSTURL = "http://103.6.222.234:8086/mail/send";
	private String receiver = "";
	private CountDownLatch countDownLatch;
	// 可用CUP核数
	private Integer availProcessors ; 
	// 控制流量，0.4 代表一秒最多多少个
	private final RateLimiter rateLimiter = RateLimiter.create(0.3);
	private final static String[] types = new String[] { "device", "count" };
	@Autowired
	private UmengBase umengBase ;
	@Autowired
	private DimDataSource dimDataSource;
	@Autowired
	private DataSourcePool dataSourcePool;
	@Autowired
	private OdsFullDimHolderService dimHolderService;
	@Autowired
	private UmengAuthorizeServiceImpl umengAuthService;
	@Autowired
	private UmengEventsServiceImpl umengEventsService;
	
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private final static int BATCH_INSERT_COUNT = 512;
	private ArrayBlockingQueue<Map<Object,Object>> messageQueueToFlush;
	
	private final int[] PARAM_TYPES = new int[] {
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
	};
	
	private String sqlCreate ="CREATE TABLE IF NOT EXISTS bi_st.st_event_analysis_dm_yyyymm (  `statis_date` int(9) NOT NULL,  `app_plat_id` int(4) DEFAULT NULL COMMENT '平台id',  `app_id` int(4) DEFAULT NULL COMMENT '应用id',  `version_id` int(4) DEFAULT NULL COMMENT '版本id',  `group_id` varchar(255) DEFAULT NULL COMMENT '事件id',  `event_name` varchar(255) DEFAULT NULL COMMENT '事件名称',  `event_id` varchar(255) DEFAULT NULL COMMENT '事件名称',  `yes_message_count` int(9) DEFAULT NULL COMMENT '消息数',  `yes_user_count` int(9) DEFAULT NULL COMMENT '用户数',  `lastyes_message_count` int(9) DEFAULT NULL COMMENT '前天消息数',  `lastyes_user_count` int(9) DEFAULT NULL COMMENT '前天用户数',  KEY `idx_st_event_analysis_dm` (`statis_date`) USING BTREE) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	private String sqlInsert ="INSERT INTO bi_st.st_event_analysis_dm_yyyymm (	`statis_date`,	 `app_plat_id` ,  `app_id` ,  `version_id` ,  `group_id`,	`event_name`,	`event_id`,	`yes_message_count`,	`yes_user_count`,	`lastyes_message_count`,	`lastyes_user_count`)VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlInsertWithoutValues;
	private String optime_month;

	public Events_ApiEventsValServiceImpl() {
		EVENT_URL = PropertiesHolder.getProperty("umeng.dailydataUrl");
		USER_NAME = PropertiesHolder.getProperty("umeng.username");
		PASS_WORD = PropertiesHolder.getProperty("umeng.password");
		receiver = PropertiesHolder.getProperty("umeng.email");
		availProcessors = Runtime.getRuntime().availableProcessors();
		this.messageQueueToFlush = new ArrayBlockingQueue<Map<Object, Object>>(DEFAULT_QUEUE_CAPACITY);
	}
	
	/**
	 * 执行令牌桶任务
	 * @author: ylc
	 */
	private void submitTasks(List<Runnable> tasks, Executor executor) {
		int i = 0;
		for (Runnable task : tasks) {
			i++;
			// 也许需要等待
			rateLimiter.acquire();
			executor.execute(task);
			logger.info("total：" + tasks.size() + " task , do------>" + i);
		}
		i = 0;
	}

	
	@Override
	public String getName() {
		return "st.umeng_events_api";
	}
	
	@Override
	public void stop() {
		this.flush();
	}

	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		DataSource dataSource = null;
		if (null != paramMap && !paramMap.isEmpty()) {
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_DATASOURCE);
			if (null != dataSourceMap && !dataSourceMap.isEmpty()) {
				dataSourcePool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				dataSource = dataSourcePool.getDataSource(dataSourceMap);
				if (null == dataSource) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				}else{
					logger.info(" Server Ip:"+IpUtils.getIPAddress()+"---> [" + this.getClass().getCanonicalName() + "]; dataSource url:"+ dataSourceMap.get("url"));
				}
			}
		}
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			optime_month = optime_yesday.substring(0, optime_yesday.length() - 2);

			// 创建st目标表
			String target_sql = sqlCreate;
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace("bi_st.st_event_analysis_dm_yyyymm", "bi_st.st_event_analysis_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			}
			// 获取token
			String token = umengAuthService.getAuthorize();
			if (StringUtil.isNullOrEmpty(token)) {
				super.callback(false, "友盟登录失败; 请检查友盟用户名和密码，以及接口是否可用!", scriptBean, callback);
				return;
			}
			//获取友盟接口Header
			Map<String, String> headerMap = umengBase.getAuthorizeHeader(USER_NAME, PASS_WORD);
			// 获取app列表
			List<Map> apps = new UmengAppsServiceImpl().getApps(token, headerMap);
			// 获取事件列表(其实为group_list，友盟接口命名有歧义导致)
			List<Map> eventList = new ArrayList<>();
			countDownLatch = new CountDownLatch(apps.size());
			List<Runnable> appJobList = new ArrayList<>();
			for (Map app : apps) {
				appJobList.add(() -> {
					try {
						List<Map> events = umengEventsService.getEvents(token, String.valueOf(app.get("appkey")), NumberUtils.intValue(app.get("platId")),NumberUtils.intValue(app.get("appId")));
						if (null != events && !events.isEmpty()) {
							eventList.addAll(events);
						}
						countDownLatch.countDown();
					} catch (Exception e) {
						// 一个线程抛出异常，发送邮件
						logger.error(Exceptions.getStackTraceAsString(e));
						// 发送邮件
						umengBase.sendMail(receiver, POSTURL, new NewMapUtil().set("error", Exceptions.getStackTraceAsString(e)).get());
					}
				});
			}
			// 执行任务
			ExecutorService appPool = Executors.newFixedThreadPool(availProcessors * 2);
			this.submitTasks(appJobList, appPool);
			countDownLatch.await();
			
		
			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
			if (!StringUtil.isNullOrEmpty(USER_NAME) && !StringUtil.isNullOrEmpty(PASS_WORD) && !StringUtil.isNullOrEmpty(EVENT_URL)) {
				//组装请求参数
				for (Map event : eventList) {
					if (StringUtil.isNullOrEmpty(event.get("appKey")) || StringUtil.isNullOrEmpty(event.get("group_id"))) {
						logger.error("[" + this.getClass().getCanonicalName() + "]; event Checkout failure; event: "+event==null?null: JSON.toJSONString(event));
						break;
					}
					// 事件所属App版本
					List<DimAppVersion> versions = (List<DimAppVersion>) event.get("versions");
					if(null== versions || versions.isEmpty()){
						break;
					}
					// 每个应用每个版本查询一次
					for (DimAppVersion dimAppVersion : versions) {
						String version = dimAppVersion.getAppVersion();
						// 如果版本包含中文
						if(isContainChinese(version)){
							continue;
						}
						Map<String, Object> parameter = new HashMap<String, Object>();
						parameter.put("appkey", event.get("appKey"));
						parameter.put("group_id", event.get("group_id"));
						parameter.put("start_date", DateUtil.getSysStrCurrentDate("yyyy-MM-dd", -2));
						parameter.put("end_date", DateUtil.getSysStrCurrentDate("yyyy-MM-dd", -1));
						parameter.put("period_type", "daily");
						parameter.put("auth_token", token);
						parameter.put("versions", dimAppVersion.getAppVersion());
						
						requestMapList.add(new NewMapUtil().set("event", event)
		                                                   .set("versionId", dimAppVersion.getId())
		                                                   .set("parameter", parameter)
		                                                   .get());
						
					}
				}
				
				// 查询事件统计
				if (headerMap != null && !headerMap.isEmpty() && !requestMapList.isEmpty()) {
					logger.info("[" + this.getClass().getCanonicalName() + "] total waite Access interface "+requestMapList.size()+" 次");
					for (Map<String, Object> para : requestMapList) {
						Map paraMap      = (Map) para.get("parameter");
						Map event        = (Map) para.get("event");
						int versionId    = NumberUtils.intValue(para.get("versionId"));
						try {
							Map<Object, Object> resMap = new HashMap<>();
							resMap.put("statis_date", String.valueOf(paraMap.get("end_date")).replace("-", ""));
							resMap.put("event_id", event.get("name"));
							resMap.put("event_name", event.get("display_name"));
							resMap.put("group_id", event.get("group_id"));
							resMap.put("app_plat_id", event.get("platId"));
							resMap.put("app_id", event.get("appId"));
							resMap.put("app_version_id", versionId);

							// 请求用户数
							paraMap.put("type", types[0]);
							rateLimiter.acquire();
							String deviceReport = umengBase.doGet(EVENT_URL, headerMap, paraMap);
							Map<String, Object> deviceMap = (Map<String, Object>) JSON.parse(deviceReport);
							List<Integer> deviceList = ((Map<String, List<Integer>>) deviceMap.get("data")).get("all");
							resMap.put("user_count", deviceList.get(1));
							resMap.put("lastyes_user_count", deviceList.get(0));

							// 请求访问次数
							paraMap.put("type", types[1]);
							rateLimiter.acquire();
							String countReport = umengBase.doGet(EVENT_URL, headerMap, paraMap);
							Map<String, Object> countMap = (Map<String, Object>) JSON.parse(countReport);
							List<Integer> countList = ((Map<String, List<Integer>>) countMap.get("data")).get("all");
							resMap.put("message_count", countList.get(1));
							resMap.put("lastyes_message_count", countList.get(0));

							offer(resMap);
						} catch (Exception ex) {
							logger.error(Exceptions.getStackTraceAsString(ex) + " request url:" + EVENT_URL + ", header:" + JSON.toJSONString(headerMap) + " , param:" + JSON.toJSONString(paraMap));
							umengBase.sendMail(receiver, POSTURL, new NewMapUtil("message", para.get("event")).get());
						}
					}
				}
			}
			// 睡眠两分钟，等待队列中的数据入库
			Thread.currentThread().sleep(2 * 60 * 1000);
			// 完成回调
			super.callback(true, null, scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + Exceptions.getStackTraceAsString(ex), scriptBean, callback);
		} catch (Exception e) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + Exceptions.getStackTraceAsString(e), scriptBean, callback);
		}
	}
	
	private synchronized void offer(Map rsMap){
		if (!this.messageQueueToFlush.offer(rsMap)) {
			flush();
			if (!this.messageQueueToFlush.offer(rsMap)) {
				logger.error("failed to add login ods info to flushing queue.");
			}
		}
	}
	
	/**
	 * 批量flush数据入库
	 */
	private void flush(){
		if (this.messageQueueToFlush.size() == 0) {
			return;
		}
		String batchSqlInsert = null;
		if (!StringUtil.isNullOrEmpty(this.sqlInsert)) {
			this.sqlInsertWithoutValues = this.sqlInsert;
			// 开启多values插入方式，准备手动构建多值插入的SQL
			int delimiter = this.sqlInsert.indexOf("VALUES ");
			if (delimiter == -1) {
				delimiter = this.sqlInsert.indexOf("values ");
			}
			if (delimiter != -1) {
				this.sqlInsertWithoutValues = this.sqlInsert.substring(0, delimiter);
			}
			batchSqlInsert = this.sqlInsertWithoutValues;
		}
		if (!StringUtil.isNullOrEmpty(batchSqlInsert)) {
			batchSqlInsert = batchSqlInsert.replace("st_event_analysis_dm_yyyymm", "st_event_analysis_dm_" + optime_month);
		}
		logger.info("flushing {}...", this.getName());
		List<Map<Object,Object>> messagesToFlush = new ArrayList<Map<Object,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<Object,Object>>> shardingFlushEntriesMap = new HashMap<Integer, List<Map<Object,Object>>>();
		for (Map<Object, Object> message : messagesToFlush) {
			Integer shardingKey = NumberUtils.intValue(message.get("statis_date"));
			List<Map<Object, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(shardingKey);
			if (shardingFlushEntries == null) {
				shardingFlushEntries = new java.util.ArrayList<Map<Object, Object>>(128);
				shardingFlushEntriesMap.put(shardingKey, shardingFlushEntries);
			}
			shardingFlushEntries.add(message);
		}
				
		for(List<Map<Object,Object>> shardingFlushEntries : shardingFlushEntriesMap.values()) {		
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = dimDataSource.clusterDataSource().getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<Object,Object> event : shardingFlushEntries) {
					Object[] params = new Object[] {
							NumberUtils.intValue(event.get("statis_date")),
							NumberUtils.intValue(event.get("app_plat_id")),
							NumberUtils.intValue(event.get("app_id")),
							NumberUtils.intValue(event.get("app_version_id")),
							event.get("group_id"),
							event.get("event_name"),
							event.get("event_id"),
							NumberUtils.intValue(event.get("message_count")),
							NumberUtils.intValue(event.get("user_count")),
							NumberUtils.intValue(event.get("lastyes_message_count")),
							NumberUtils.intValue(event.get("lastyes_user_count"))
					};
					
					SqlUtils.appendSqlValues(sqlBuilder, params, PARAM_TYPES);
					++count;
					if(count >= BATCH_INSERT_COUNT) {
						pstmt.executeUpdate(sqlBuilder.toString());
						if(!autoCommit0) connection.commit();
						count = 0;
						sqlBuilder = new StringBuilder(batchSqlInsert);
					}
				}
							
				if(count > 0) {
					pstmt.executeUpdate(sqlBuilder.toString());
					if(!autoCommit0) connection.commit();
				}
				pstmt.close();
			} catch (SQLException ex) {
				if (JdbcUtil.isHardError(ex)) {
					// 致命错误，可能数据库已经down掉或无法连接，取消flush，等待下次重试
					logger.error("fatal error while flushing " + this.getName() + ", message: " + ex.getMessage(), ex);
				} else {
					logger.error("SQL exception while flushing " + this.getName() + ": " + ex.getMessage(), ex);
					// 非致命错误（如字段值超过数据库定义等常规异常），尝试单条flush，尽量减少失败的影响
					try {
						if (!autoCommit0)
							connection.rollback();
						String singlSqlInsert = this.sqlInsert;
						if(!StringUtil.isNullOrEmpty(singlSqlInsert)){
							singlSqlInsert = singlSqlInsert.replace("st_event_analysis_dm_yyyymm", "st_event_analysis_dm_" + optime_month);
						}
						
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<Object, Object> event : shardingFlushEntries) {
							try {
								pstmt.setInt(1, NumberUtils.intValue(event.get("statis_date"), -9));
								pstmt.setInt(2, NumberUtils.intValue(event.get("app_plat_id"), -9));
								pstmt.setInt(3, NumberUtils.intValue(event.get("app_id"), -9));
								pstmt.setInt(4, NumberUtils.intValue(event.get("app_version_id"), -9));
								pstmt.setString(5, String.valueOf(event.get("group_id")));
								pstmt.setString(6, String.valueOf(event.get("event_name")));
								pstmt.setString(7, String.valueOf(event.get("event_id")));
								pstmt.setInt(8, NumberUtils.intValue(event.get("message_count"), -9));
								pstmt.setInt(9, NumberUtils.intValue(event.get("user_count"), -9));
								pstmt.setInt(10, NumberUtils.intValue(event.get("lastyes_message_count"), -9));
								pstmt.setInt(11, NumberUtils.intValue(event.get("lastyes_user_count"), -9));

								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + event.toString(), ex2);
							}
						}
						if (!autoCommit0)
							connection.commit();
						pstmt.close();
					} catch (SQLException e) {
						logger.error("error while rollback " + this.getName() + ": " + e.getMessage(), e);
					}
				}
			} finally {
				try {
					if (null != connection) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error("error while connection close " + this.getName() + ": " + e.getMessage(), e);
				}
			}
		}
		logger.debug("{} flushed with {} items.", this.getName(), messagesToFlush.size());
	}

	@Override
	public void schedule() {
		this.flush();
	}
	
	/**
	 * 判断版本是否包含中文，避免400
	 * @author: ylc
	 */
	private boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
}

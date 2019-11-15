package etl.dispatch.gather.spap.tables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.gather.spap.bean.BaseDate;
import etl.dispatch.gather.spap.bean.ClassData;
import etl.dispatch.gather.spap.bean.DataList;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.JdbcUtil;
import etl.dispatch.script.util.ScriptTimeUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

/**
 * 登录日志
 * 
 * @author liu
 *
 */
@Service
public class LoginInformation extends AbstractScript implements ScheduledService {

	private static Logger logger = LoggerFactory.getLogger(LoginInformation.class);
	private static final String configJsonPath = "classpath*:conf/spapjson/login_info.json";
	private final static int BATCH_INSERT_COUNT = 512;
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private AtomicBoolean isDbEmpty = new AtomicBoolean(false);
	public static final long oneDay = 86400000l;
	private ArrayBlockingQueue<Map<Object, Object>> messageQueueToFlush;
	private Optional<Integer> saveDays = Optional.empty();
	public String optime_yesday = "";

	private DataSourcePool sourcePool;
	private DataSourcePool targetPool;
	private DataSource target = null;
	private DataSource source = null;
	private String sqlInsert = "";
	private String sqlInsertWithoutValues;

	public LoginInformation() {
		messageQueueToFlush = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
	}

	private final int[] PARAM_TYPES = new int[] { 
			Types.BIGINT,
			Types.BIGINT,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
	};

	@Override
	public void stop() {

	}

	@Override
	public String getName() {
		return "loginInformation";
	}

	@Override
	public void schedule() {
		this.flush();
	}

	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		if (null != paramMap && !paramMap.isEmpty()) {
			Map<String, Object> dataTargetMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_TARGETDATA);
			if (null != dataTargetMap && !dataTargetMap.isEmpty()) {
				targetPool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				target = targetPool.getDataSource(dataTargetMap);

				if (null == target) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataTargetMap), scriptBean,
							callback);
				} else {
					logger.info(" Server Ip:" + IpUtils.getIPAddress() + "---> [" + this.getClass().getCanonicalName()+ "];dataTarget url:" + dataTargetMap.get("url"));
				}
			}
			saveDays = Optional.ofNullable((Integer) paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}

		try {
			optime_yesday = ScriptTimeUtil.optime_yesday();
			Date yesDate = DateUtil.formatStrToDate(optime_yesday, "yyyyMMdd");

			// 创建目标表
			String create_login_info_ds_log = super.getJsonConfigValue(configJsonPath, "create_login_info_ds_log");
			if (!StringUtil.isNullOrEmpty(create_login_info_ds_log)) {
				create_login_info_ds_log = create_login_info_ds_log.replace("spap_login_log_yyyymmdd","spap_login_log_" + optime_yesday);
				SqlUtils.sqlExecute(target, create_login_info_ds_log, this.getName());
			}

			// 删除昨日数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("bi_interface_spap.spap_login_log_yyyymmdd","bi_interface_spap.spap_login_log_" + optime_yesday);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(target, delete_yes_date, this.getName());
			}

			// 2:拉取数据
			this.sqlInsert = super.getJsonConfigValue(configJsonPath, "insert_login_info_ds_log");

				PrintWriter out = null;
				BufferedReader in = null;
				int next = 0;
				for (;;) {
					URL realUrl = new URL("http://125.208.1.66:10021/s160/spap/log/spap-login");
					// 打开和URL之间的连接
					URLConnection conn = realUrl.openConnection();
					// 设置超时时间
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(15000);
		
					// 设置不用缓存
		            conn.setUseCaches(false);
					// 发送POST请求必须设置如下两行
					conn.setDoOutput(true);
					conn.setDoInput(true);
					// 获取URLConnection对象对应的输出流
					out = new PrintWriter(conn.getOutputStream());
				
					String result = "";
					// 发送请求参数
					String param = "start=" + next + "&logToken=spap_log_center_2017616";
					out.print(param);
					// flush输出流的缓冲
					out.flush();
					// 定义BufferedReader输入流来读取URL的响应
					in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf8"));
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}
				
					JSONObject jsStr = JSONObject.parseObject(result);
					BaseDate baseDate = JSON.toJavaObject(jsStr, BaseDate.class);
	
					ClassData classData = baseDate.getData();
					next = classData.getNext();
					List<DataList> list = (List<DataList>) classData.getList();
					
					if ( list.isEmpty() ) {
						break;
					}
	
					List<Map> rslist = new ArrayList<>();
					Map map = new HashMap<>();
					for (DataList data : list) {
						map.put("user_id", data.getUserId());
						map.put("timestamp", data.getTimestamp());
						String appVersion = data.getAppVersion().substring(0, 3);
						map.put("app_version", appVersion);
						map.put("device_vendor", data.getDeviceVendor());
						map.put("channel", data.getChannel());
						map.put("device_Id", data.getDeviceId());
						map.put("type", data.getType());
						map.put("os_name", data.getOsName());
						map.put("os_version", data.getOsVersion());
						map.put("oper_ip", data.getOperIp());
						map.put("device_model", data.getDeviceModel());
						map.put("app_device_type", data.getAppDeviceType());
						map.put("network", data.getNetwork());
						rslist.add(map);
					}
					this.offer(rslist);
				}
				
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}

			/*// 删除历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			if (!StringUtil.isNullOrEmpty(delete_table) && saveDays.isPresent()) {
				delete_table = delete_table.replace("bi_interface_spap.spap_login_log_yyyymmdd","bi_interface_spap.spap_login_log_"+ DateUtil.getSysStrCurrentDate("yyyyMMdd", -saveDays.get()));
				SqlUtils.sqlExecute(target, delete_table, this.getName());
			} else {
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
			}*/

			for (;;) {
				// 数据库数据Empty;且队列已Empty
				if (messageQueueToFlush.isEmpty()) {
					// 完成回调
					super.callback(true, null, scriptBean, callback);
					return;
				}
				Thread.currentThread().sleep(10 * 1000);
			}
			// 完成回调
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean,callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :"+ super.getUrl(source) + ",message: " + ex.getMessage(), scriptBean, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void offer(List<Map> rslist) {
		for (Map rsMap : rslist) {
			// add to queue to wait for flushing
			if (!this.messageQueueToFlush.offer(rsMap)) {
				// the queue is full, flush first
				this.flush();
				if (!this.messageQueueToFlush.offer(rsMap)) {
					// fail again, maybe an error
					logger.error("failed to add user ods info to flushing queue.");
				}
			}
		}
	}

	private void flush() {
		if (this.messageQueueToFlush.size() == 0) {
			return;
		}
		logger.info("flushing {}...", this.getName());
		List<Map<Object, Object>> messagesToFlush = new ArrayList<Map<Object, Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);

		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<String, List<Map<Object, Object>>> shardingFlushEntriesMap = messagesToFlush.stream()
				.collect(Collectors.groupingBy(a -> {
					return String.valueOf(a.get("app_device_type"));
				}));

		for (String key : shardingFlushEntriesMap.keySet()) {
			List<Map<Object, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(key);
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
				batchSqlInsert = batchSqlInsert.replace("spap_login_log_yyyymmdd", "spap_login_log_" + optime_yesday);
			}
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = target.getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for (Map<Object, Object> log : shardingFlushEntries) {
					Long creatTime = NumberUtils.longValue(log.get("timestamp"));
					Date registTime = DateUtil.format2Date(creatTime);
					Object[] params = new Object[] { 
							log.get("user_id"),
							log.get("timestamp"),
							log.get("app_version"),
							log.get("device_vendor"),
							log.get("channel"),
							log.get("device_Id"),
							log.get("type"),
							log.get("os_name"),
							log.get("os_version"),
							log.get("oper_ip"),
							log.get("device_model"),
							log.get("app_device_type"),
							log.get("network"),

							NumberUtils.intValue(DateUtil.getYear(registTime), -9), 
							DateUtil.getIntMonth(registTime),
							DateUtil.getIntDay(registTime), 
							NumberUtils.intValue(DateUtil.getHour(registTime), -9),
							NumberUtils.intValue(DateUtil.formatDate(registTime, "yyyyMMdd"), -9) };

					SqlUtils.appendSqlValues(sqlBuilder, params, PARAM_TYPES);
					++count;
					if (count >= BATCH_INSERT_COUNT) {
						pstmt.executeUpdate(sqlBuilder.toString());
						if (!autoCommit0)
							connection.commit();
						count = 0;
						sqlBuilder = new StringBuilder(batchSqlInsert);
					}
				}

				if (count > 0) {
					pstmt.executeUpdate(sqlBuilder.toString());
					if (!autoCommit0)
						connection.commit();
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
						if (!StringUtil.isNullOrEmpty(singlSqlInsert)) {
							singlSqlInsert = singlSqlInsert.replace("spap_login_log_yyyymmdd","spap_login_log_" + optime_yesday);
						}
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<Object, Object> log : shardingFlushEntries) {
							try {
								Long creatTime = NumberUtils.longValue(log.get("timestamp"));
								Date registTime = DateUtil.format2Date(creatTime);

								pstmt.setLong(1, NumberUtils.longValue(log.get("user_id")));
								pstmt.setLong(2, NumberUtils.longValue(log.get("timestamp")));
								pstmt.setString(3, String.valueOf(log.get("app_version")));
								pstmt.setString(4, String.valueOf(log.get("device_vendor")));
								pstmt.setInt(5, NumberUtils.intValue(log.get("channel")));
								pstmt.setString(6, String.valueOf(log.get("device_Id")));
								pstmt.setString(7, String.valueOf(log.get("type")));
								pstmt.setString(8, String.valueOf(log.get("os_name")));
								pstmt.setString(9, String.valueOf(log.get("os_version")));
								pstmt.setString(10, String.valueOf(log.get("oper_ip")));
								pstmt.setString(11, String.valueOf(log.get("device_model")));
								pstmt.setString(12, String.valueOf(log.get("app_device_type")));
								pstmt.setString(13, String.valueOf(log.get("network")));

								pstmt.setInt(14, NumberUtils.intValue(DateUtil.getYear(registTime), -9));
								pstmt.setInt(15, DateUtil.getIntMonth(registTime));
								pstmt.setInt(16, DateUtil.getIntDay(registTime));
								pstmt.setInt(17, NumberUtils.intValue(DateUtil.getHour(registTime), -9));
								pstmt.setInt(18, NumberUtils.intValue(DateUtil.formatDate(registTime, "yyyyMMdd"), -9));

								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods user info : " + ex2.getMessage()
										+ ", failed message: \n\t" + log.toString(), ex2);
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

}
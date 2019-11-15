package etl.dispatch.gather.bgmgr.schedule.offline;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.gather.bgmgr.enums.ScheduleActionType;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.domain.DimAppVersion;
import etl.dispatch.java.ods.enums.ZBAppPlatAppIdEnum;
import etl.dispatch.java.ods.service.OdsFullDimHolderService;
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
 * 
 * @Description: 日程管理拉取清洗数据
 * @author: ylc
 */
@Service
public class ScheduleInfoGatherService extends AbstractScript implements ScheduledService{
	private static Logger logger = LoggerFactory.getLogger(ScheduleInfoGatherService.class);
	private static final String configJsonPath = "classpath*:conf/json/schedule_info_gather_service.json";
	private final static int BATCH_INSERT_COUNT = 512;
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static DimAppPlat APPPLAT_NOT_PRESENT = new DimAppPlat();
	private final static DimAppVersion APPVERSION_NOT_PRESENT = new DimAppVersion();
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private AtomicBoolean isDbEmpty = new AtomicBoolean(false);
	public static final long oneDay = 86400000l;
	private ArrayBlockingQueue<Map<Object, Object>> messageQueueToFlush;
	private Optional<Integer> saveDays = Optional.empty();
	
	private DataSourcePool sourcePool;
	private DataSourcePool targetPool;
	
	private DataSource target = null;
	private DataSource source = null;
	private String sqlInsert ="";
	private String sqlInsertWithoutValues;
	
	@Autowired
	private OdsFullDimHolderService dimHolderService;
	
	private final int[] PARAM_TYPES = new int[] {
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER
	};
	
	public String getName() {
		return "ScheduleInfoGatherService";
	}
	
	public ScheduleInfoGatherService() {
		this.messageQueueToFlush = new ArrayBlockingQueue<Map<Object, Object>>(DEFAULT_QUEUE_CAPACITY);
	}
	
	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		if (null != paramMap && !paramMap.isEmpty()) {
			//来源数据库
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_SOURCEDATA);
			//目标数据库
			Map<String, Object> dataTargetMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_TARGETDATA);
			if ((null != dataSourceMap && !dataSourceMap.isEmpty()) &&(null != dataTargetMap && !dataTargetMap.isEmpty())) {
				sourcePool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				source = sourcePool.getDataSource(dataSourceMap);
				targetPool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				target = targetPool.getDataSource(dataTargetMap);
				if (null == source || null == target) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				}else{
					logger.info(" Server Ip:"+IpUtils.getIPAddress()+"---> [" + this.getClass().getCanonicalName() + "]; dataSource url:"+ dataSourceMap.get("url")+" , dataTarget url:"+ dataTargetMap.get("url"));
				}
			}
			saveDays = Optional.ofNullable((Integer)paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}
		
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			String optime_month = optime_yesday.substring(0, optime_yesday.length() - 2);
			Date yesDate = DateUtil.formatStrToDate(optime_yesday, "yyyyMMdd");
			
			// 1:创建月表
			String create_dm_schedule_yyyymm = super.getJsonConfigValue(configJsonPath, "create_dm_schedule_yyyymm");
			if (!StringUtil.isNullOrEmpty(create_dm_schedule_yyyymm)) {
				create_dm_schedule_yyyymm = create_dm_schedule_yyyymm.replace("ods_schedule_dm_yyyymm", "ods_schedule_dm_" + optime_month);
				SqlUtils.sqlExecute(target, create_dm_schedule_yyyymm, this.getName());
			}
			
			// 支持重跑删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("ods_schedule_dm_yyyymm", "ods_schedule_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				if(!StringUtil.isNullOrEmpty(delete_yes_date)){
					SqlUtils.sqlExecute(target, delete_yes_date, this.getName());
				}
			}
			
			// 2:拉取数据
			this.sqlInsert = super.getJsonConfigValue(configJsonPath, "insert_bi_online_schedule");
			if(!StringUtil.isNullOrEmpty(this.sqlInsert)){
				int limit = 0;
				for (;;) {
					String selectSource = super.getJsonConfigValue(configJsonPath, "selectSource");
					if (!StringUtil.isNullOrEmpty(selectSource)) {
						selectSource = selectSource.replace("zuobiao_log.schedule_log_yyyy_mm", "zuobiao_log.schedule_log_" + DateUtil.getSysStrCurrentDate("yyyy_MM" , -1));
						selectSource = selectSource.replace("${startTime}", String.valueOf(yesDate.getTime()));
						selectSource = selectSource.replace("${endTime}", String.valueOf(yesDate.getTime() + oneDay));
						selectSource = selectSource.replace("${offset}", String.valueOf(limit));
						selectSource = selectSource.replace("${rows}", String.valueOf(DEFAULT_LIMIT_MAX_SIZE));
						List<Map> rslist = SqlUtils.querySqlList(source, selectSource, this.getName());
						if (null == rslist || rslist.isEmpty()) {
							isDbEmpty.compareAndSet(false, true);
							logger.debug("select sql result is Empty;" + ScheduleInfoGatherService.class.getCanonicalName() + "; Sql:" + selectSource);
							break;
						} else {
							limit = limit + DEFAULT_LIMIT_MAX_SIZE;
							this.offer(rslist);
						}
					} else {
						isDbEmpty.compareAndSet(false, true);
					}
				}
			}
			
			// 删除ods历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			String delete_date = super.getJsonConfigValue(configJsonPath, "delete_date");
			if (!StringUtil.isNullOrEmpty(delete_table) && !StringUtil.isNullOrEmpty(delete_date) && saveDays.isPresent()) {
				String delSQl = super.getDelHistorySql(delete_table, delete_date, "schedule_dm_", saveDays.get());
				if(!StringUtil.isNullOrEmpty(delSQl)){
					SqlUtils.sqlExecute(target, delSQl, this.getName());
				}
			}else{
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
			}
			
			for (;;) {
				//数据库数据Empty;且队列已Empty
				if (isDbEmpty.get() && messageQueueToFlush.isEmpty()) {
					// 完成回调
					super.callback(true, null, scriptBean, callback);
					return;
				}
				Thread.currentThread().sleep(10 * 1000);
			}
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(source) + ",message: " + ex.getMessage(), scriptBean, callback);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void offer(List<Map> rslist) {
		for (Map rsMap : rslist) {
			Map<String, Object> tmpCache = new HashMap<>();
			Iterator<Map.Entry<String, Object>> it = rsMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				String keyVal = String.valueOf(entry.getKey());
				String value = String.valueOf(entry.getValue());
				//-9转换为未知
				if("-9".equalsIgnoreCase(value)){
					value= "未知";
				}
				switch (keyVal) {
				case "app_plat_id":
					DimAppPlat dimAppPlat = dimHolderService.getAppPlatByName(value, true);
					if (null == dimAppPlat || dimAppPlat == APPPLAT_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("app_plat_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimAppPlat.getId());
						tmpCache.put("app_plat_id", dimAppPlat.getId());
					}
					break;
				case "app_version_id":
					int appPlatId = NumberUtils.intValue(tmpCache.get("app_plat_id"));
					//分析坐标应用时，由于一个平台仅有一个坐标应用，因此 平台类型一一映射坐标应用；后期多个应用需要传递应用ID
					ZBAppPlatAppIdEnum enumObj = ZBAppPlatAppIdEnum.lookup.get(appPlatId);
					int appId = -9;
					if(null !=enumObj){
						appId =enumObj.getAppId();
					}
					DimAppVersion dimAppVersion = dimHolderService.getAppVersionByName(NumberUtils.intValue(appPlatId), appId, value, true);
					if (null == dimAppVersion || dimAppVersion == APPVERSION_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("app_version_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimAppVersion.getId());
						tmpCache.put("app_version_id", dimAppVersion.getId());
					}
					break;
				case "action":
					ScheduleActionType actionType = ScheduleActionType.lookup.get(String.valueOf(value).toLowerCase());
					int actionId = -9;
					if(null !=actionType){
						actionId =actionType.getCode();
					}
					entry.setValue(actionId);
					break;
				default:
					break;
				}
			}
			
			// 添加app_id
			int appPlatId = NumberUtils.intValue(tmpCache.get("app_plat_id"));
			//分析坐标应用时，由于一个平台仅有一个坐标应用，因此 平台类型一一映射坐标应用；后期多个应用需要传递应用ID
			ZBAppPlatAppIdEnum enumObj = ZBAppPlatAppIdEnum.lookup.get(appPlatId);
			int appId = -9;
			if(null !=enumObj){
				appId =enumObj.getAppId();
			}
			rsMap.put("app_id", appId);
			
			// 添加时间
			long timestamp = NumberUtils.longValue(rsMap.get("timestamp"));
			Date time = DateUtil.format2Date(timestamp);
			rsMap.put("year",NumberUtils.intValue(DateUtil.getYear(time),-9)); 
			rsMap.put("month",DateUtil.getIntMonth(time));
			rsMap.put("day",DateUtil.getIntDay(time));
			rsMap.put("hour",NumberUtils.intValue(DateUtil.getHour(time),-9)); 
			rsMap.put("statis_date",NumberUtils.intValue(DateUtil.formatDate(time, "yyyyMMdd"),-9));
			
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
	
	private void flush(){
		if (this.messageQueueToFlush.size() == 0) {
			return;
		}
		logger.info("flushing {}...", this.getName());
		List< Map<Object, Object>> messagesToFlush = new ArrayList<Map<Object,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<Object, Object>>> shardingFlushEntriesMap = messagesToFlush.stream().collect(Collectors.groupingBy(a->{
			return NumberUtils.intValue(String.valueOf(a.get("statis_date")).substring(0,String.valueOf(a.get("statis_date")).length()-2));
		}));

		for(Integer key : shardingFlushEntriesMap.keySet()) {	
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
				batchSqlInsert = batchSqlInsert.replace("ods_schedule_dm_yyyymm", "ods_schedule_dm_" + key );
			}
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = target.getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<Object,Object> log : shardingFlushEntries) {
					Object[] params = new Object[] {
							NumberUtils.intValue(log.get("master"),-9),
							NumberUtils.intValue(log.get("sche_id"),-9),
							NumberUtils.intValue(log.get("status"),-9),
							NumberUtils.intValue(log.get("action"),-9),
							NumberUtils.intValue(log.get("app_plat_id"),-9),
							NumberUtils.intValue(log.get("app_version_id"),-9),
							NumberUtils.intValue(log.get("app_id"),-9),
							NumberUtils.longValue(log.get("create_timestamp"),-9),
							NumberUtils.longValue(log.get("start_timestamp"),-9),
							NumberUtils.longValue(log.get("timestamp"),-9),
							NumberUtils.longValue(log.get("end_timestamp"),-9),
							NumberUtils.intValue(log.get("year"),-9),
							NumberUtils.intValue(log.get("month"),-9),
							NumberUtils.intValue(log.get("day"),-9),
							NumberUtils.intValue(log.get("hour"),-9),
							NumberUtils.intValue(log.get("statis_date"),-9)
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
							singlSqlInsert = singlSqlInsert.replace("ods_schedule_dm_yyyymm", "ods_schedule_dm_" + key  );
						}
						
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<Object, Object> log : shardingFlushEntries) {
							try {
								pstmt.setInt(1, NumberUtils.intValue(log.get("master"), -9));
								pstmt.setInt(2, NumberUtils.intValue(log.get("sche_id"), -9));
								pstmt.setInt(3, NumberUtils.intValue(log.get("status"), -9));
								pstmt.setInt(4, NumberUtils.intValue(log.get("action"), -9));
								pstmt.setInt(5, NumberUtils.intValue(log.get("app_plat_id"), -9));
								pstmt.setInt(6, NumberUtils.intValue(log.get("app_version_id"), -9));
								pstmt.setInt(7, NumberUtils.intValue(log.get("app_id"), -9));
								pstmt.setLong(8, NumberUtils.longValue(log.get("create_timestamp"), -9));
								pstmt.setLong(9, NumberUtils.longValue(log.get("start_timestamp"), -9));
								pstmt.setLong(10, NumberUtils.longValue(log.get("timestamp"), -9));
								pstmt.setLong(11, NumberUtils.longValue(log.get("end_timestamp"), -9));
								pstmt.setInt(12, NumberUtils.intValue(log.get("year"), -9));
								pstmt.setInt(13, NumberUtils.intValue(log.get("month"), -9));
								pstmt.setInt(14, NumberUtils.intValue(log.get("day"), -9));
								pstmt.setInt(15, NumberUtils.intValue(log.get("hour"), -9));
								pstmt.setInt(16, NumberUtils.intValue(log.get("statis_date"), -9));

								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + log.toString(), ex2);
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
	public void stop() {
		this.flush();
	}

	@Override
	public void schedule() {
		this.flush();
	}

}

package etl.dispatch.java.ods;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.domain.DimAppVersion;
import etl.dispatch.java.ods.domain.DimNetWork;
import etl.dispatch.java.ods.enums.EngineMessageTypes;
import etl.dispatch.java.ods.enums.ZBAppPlatAppIdEnum;
import etl.dispatch.java.ods.service.OdsFullDimHolderService;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.JdbcUtil;
import etl.dispatch.script.util.ScriptTimeUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

@Service
public class Ods_EngineMessageLog extends AbstractScript implements ScheduledService {
	private static Logger logger = LoggerFactory.getLogger(Ods_EngineMessageLog.class);
	private static final String configJsonPath = "classpath*:conf/json/ods_EngineMessage.json";
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static DimNetWork NETWORK_NOT_PRESENT = new DimNetWork();
	private final static DimAppVersion APPVERSION_NOT_PRESENT = new DimAppVersion();
	private final static DimAppPlat APPPLAT_NOT_PRESENT = new DimAppPlat();
	
	
	@Autowired
	private DimDataSource dimDataSource;
	@Autowired
	private DataSourcePool dataSourcePool;
	@Autowired
	private OdsFullDimHolderService dimHolderService;
	
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private final static int BATCH_INSERT_COUNT = 512;
	private ArrayBlockingQueue<Map<Object,Object>> messageQueueToFlush;
	private AtomicBoolean isDbEmpty = new AtomicBoolean(false);
	
	private final int[] PARAM_TYPES = new int[] {
			Types.VARCHAR,
			Types.INTEGER,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.BIGINT,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
	};
	
	private String sqlInsert ="INSERT INTO bi_ods.ods_engine_message_log_dm_yyyymm  (`master`,`direct`,`timestamp`,`sn`,`content_bytes`,`entity_bytes`,`action`,`network_type`,`app_device_type`,`app_id`,`app_version`,`is_group`,`from`,`to`,`type`,`year`, `month`, `day`, `hour`, `store_id`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlInsertWithoutValues;
	private Optional<Integer> saveDays = Optional.empty();
	private String optime_month;
	
	public Ods_EngineMessageLog() {
		this.messageQueueToFlush = new ArrayBlockingQueue<Map<Object,Object>>(DEFAULT_QUEUE_CAPACITY);
	}

	@Override
	public String getName() {
		return "Ods.EngineMessage";
	}
	
	public String getDateBase(){
		return "bi_ods";
	}
	
	@Override
	public void schedule() {
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
			saveDays = Optional.ofNullable((Integer) paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			optime_month = optime_yesday.substring(0, optime_yesday.length() - 2);

			// 第1步 创建ODS目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "create_ods_messageLog");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace("ods_engine_message_log_dm_yyyymm", "ods_engine_message_log_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			} else {
				logger.error("path " + configJsonPath + "; create target table, get sql with key'create_ods_messageLog' value is null ");
			}

			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("ods_engine_message_log_dm_yyyymm", "ods_engine_message_log_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}

			// 第2步 limit分页查询记录
			int limit = 0;
			for (;;) {
				String limit_select_sql = super.getJsonConfigValue(configJsonPath, "selectMessageLogLimit");
				if (!StringUtil.isNullOrEmpty(limit_select_sql)) {
					limit_select_sql = limit_select_sql.replace("bi_interface.engine_message_log_yyyymmdd", "bi_interface.engine_message_log_" + ScriptTimeUtil.optime_yesday());
					limit_select_sql = limit_select_sql.replace("${offset}", String.valueOf(limit));
					limit_select_sql = limit_select_sql.replace("${rows}", String.valueOf(DEFAULT_LIMIT_MAX_SIZE));
					List<Map> rslist = SqlUtils.querySqlList(dataSource, limit_select_sql, this.getName());
					if (null == rslist || rslist.isEmpty()) {
						isDbEmpty.compareAndSet(false, true);
						logger.debug("select sql result is Empty;"+this.getClass().getCanonicalName()+"; Sql:"+ limit_select_sql); 
						break;
					} else {
						limit = limit + DEFAULT_LIMIT_MAX_SIZE;
						this.offer(rslist);
					}
				}else{
					isDbEmpty.compareAndSet(false, true);
				}
			}
			if (StringUtil.isNullOrEmpty(super.getJsonConfigValue(configJsonPath, "selectMessageLogLimit"))) {
				logger.error("path " + configJsonPath + "; select source table, get sql with key 'selectMessageLogLimit' config value is null ");
			}

			// 删除ods历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			String delete_date = super.getJsonConfigValue(configJsonPath, "delete_date");
			if (!StringUtil.isNullOrEmpty(delete_table) && !StringUtil.isNullOrEmpty(delete_date) && saveDays.isPresent()) {
				String delSQl = super.getDelHistorySql(delete_table, delete_date, "ods_engine_message_log_dm_", saveDays.get());
				if(delSQl != null){
					SqlUtils.sqlExecute(dataSource, delSQl, this.getName());
				}
			} else {
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
			}
			// 等待数据刷入数据库
			for (;;) {
				//数据库数据Empty;且队列已Empty
				if (isDbEmpty.get() && messageQueueToFlush.isEmpty()) {
					super.callback(true, null, scriptBean, callback);
					return;
				}
				Thread.currentThread().sleep(10 * 1000);
			}
			// 脚本结束回调状态
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + ex.getMessage(), scriptBean, callback);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void offer(List<Map> rslist) {
		for (Map<Object, Object> rsMap : rslist) {
			Map<String,Object> tmpCache = new HashMap<>();
			Iterator<Map.Entry<Object, Object>> it = rsMap.entrySet().iterator();
			int appId = -9;
			while (it.hasNext()) {
				Entry<Object, Object> entry = it.next();
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
					case "network_type":
						DimNetWork dimNetWork = dimHolderService.getNetWorkByName(value, true);
						if (null == dimNetWork || dimNetWork == NETWORK_NOT_PRESENT || "unknown".equals(value.toString())) {
							entry.setValue(UNKNOWN_VALUE);
							tmpCache.put("network_type", UNKNOWN_VALUE);
						} else {
							entry.setValue(dimNetWork.getId());
							tmpCache.put("network_type", dimNetWork.getId());
						}
						break;
					case "app_version_id":
						int appPlatId = NumberUtils.intValue(tmpCache.get("app_plat_id"));
						//分析坐标应用时，由于一个平台仅有一个坐标应用，因此 平台类型一一映射坐标应用；后期多个应用需要传递应用ID
						ZBAppPlatAppIdEnum enumObj = ZBAppPlatAppIdEnum.lookup.get(appPlatId);
						if(null !=enumObj){
							// appId赋值
							appId =enumObj.getAppId();
						}
						DimAppVersion dimAppVersion = dimHolderService.getAppVersionByName(NumberUtils.intValue(appPlatId), appId,  value, true);
						if (null == dimAppVersion || dimAppVersion == APPVERSION_NOT_PRESENT) {
							entry.setValue(UNKNOWN_VALUE);
							tmpCache.put("app_version_id", UNKNOWN_VALUE);
						} else {
							entry.setValue(dimAppVersion.getId());
							tmpCache.put("app_version_id", dimAppVersion.getId());
						}
						break;
					case "type":
						if(StringUtil.isNullOrEmpty(value)){
							value = "Text";
						}
						entry.setValue(messageType(value));
						break;
					default:
						break;
				}
			}
			// 设置app_id
			rsMap.put("app_id", appId);
			
			// offer清洗转换到队列
			// add to queue to wait for flushing
			if (!this.messageQueueToFlush.offer(rsMap)) {
				// the queue is full, flush first
				flush();
				if (!this.messageQueueToFlush.offer(rsMap)) {
					// fail again, maybe an error
					logger.error("failed to add message ods info to flushing queue.");
				}
			}
		}
	}
	
	private int messageType(String type) {
		if(type.equals(EngineMessageTypes.Text.getDesc()))return EngineMessageTypes.Text.getCode();
		if(type.equals(EngineMessageTypes.File.getDesc()))return EngineMessageTypes.File.getCode();
		if(type.equals(EngineMessageTypes.Image.getDesc()))return EngineMessageTypes.Image.getCode();
		if(type.equals(EngineMessageTypes.VoiceClip.getDesc()))return EngineMessageTypes.VoiceClip.getCode();
		if(type.equals(EngineMessageTypes.VideoClip.getDesc()))return EngineMessageTypes.VideoClip.getCode();
		if(type.equals(EngineMessageTypes.Card.getDesc()))return EngineMessageTypes.Card.getCode();
		if(type.equals(EngineMessageTypes.History.getDesc()))return EngineMessageTypes.History.getCode();
		if(type.equals(EngineMessageTypes.Rich.getDesc()))return EngineMessageTypes.Rich.getCode();
		return -9;
	}

	private void flush() {
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
			batchSqlInsert = batchSqlInsert.replace("ods_engine_message_log_dm_yyyymm", "ods_engine_message_log_dm_" +optime_month);
		}
		logger.info("flushing {}...", this.getName());
		List<Map<Object,Object>> messagesToFlush = new ArrayList<Map<Object,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<Object,Object>>> shardingFlushEntriesMap = new HashMap<Integer, List<Map<Object,Object>>>();
		for (Map<Object, Object> message : messagesToFlush) {
			Integer shardingKey = NumberUtils.intValue(message.get("direct"));
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
				for(Map<Object,Object> messageLog : shardingFlushEntries) {
					Object[] params = new Object[] { 
							messageLog.get("master").toString(),
							NumberUtils.intValue(messageLog.get("direct"),1),
							NumberUtils.longValue(messageLog.get("timestamp")),
							NumberUtils.longValue(messageLog.get("sn")),
							NumberUtils.longValue(messageLog.get("content_bytes")),
							NumberUtils.longValue(messageLog.get("entity_bytes")),
							messageLog.get("action").toString(),
							NumberUtils.intValue(messageLog.get("network_type"),-9),
							NumberUtils.intValue(messageLog.get("app_plat_id"),-9),
							NumberUtils.intValue(messageLog.get("app_id"),-9),
							NumberUtils.intValue(messageLog.get("app_version_id"),-9),
							NumberUtils.intValue(messageLog.get("is_group"),2),
							NumberUtils.longValue(messageLog.get("from")),
							messageLog.get("to").toString(),
							NumberUtils.intValue(messageLog.get("type"),-9),
							NumberUtils.intValue(messageLog.get("year")),
							NumberUtils.intValue(messageLog.get("month")),
							NumberUtils.intValue(messageLog.get("day")),
							NumberUtils.intValue(messageLog.get("hour")),
							NumberUtils.intValue(messageLog.get("store_id"))
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
							singlSqlInsert = singlSqlInsert.replace("ods_engine_message_log_dm_yyyymm", "ods_engine_message_log_dm_" + optime_month);
						}
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<Object, Object> messageLog : shardingFlushEntries) {
							try {
								pstmt.setString(1, messageLog.get("master").toString());
								pstmt.setInt(2, NumberUtils.intValue(messageLog.get("direct"), 1));
								pstmt.setLong(3, NumberUtils.longValue(messageLog.get("sn")));
								pstmt.setLong(4, NumberUtils.longValue(messageLog.get("timestamp")));
								pstmt.setLong(5, NumberUtils.longValue(messageLog.get("content_bytes")));
								pstmt.setLong(6, NumberUtils.longValue(messageLog.get("entity_bytes")));
								pstmt.setString(7, messageLog.get("action").toString());
								pstmt.setInt(8, NumberUtils.intValue(messageLog.get("network_type"), -9));
								pstmt.setInt(9, NumberUtils.intValue(messageLog.get("app_plat_id"), -9));
								pstmt.setInt(10, NumberUtils.intValue(messageLog.get("app_id"), -9));
								pstmt.setInt(11, NumberUtils.intValue(messageLog.get("app_version_id"), -9));
								pstmt.setInt(12, NumberUtils.intValue(messageLog.get("is_group"), 2));
								pstmt.setLong(13, NumberUtils.longValue(messageLog.get("from")));
								pstmt.setString(14, messageLog.get("to").toString());
								pstmt.setInt(15, NumberUtils.intValue(messageLog.get("type"), -9));
								pstmt.setInt(16, NumberUtils.intValue(messageLog.get("year")));
								pstmt.setInt(17, NumberUtils.intValue(messageLog.get("month")));
								pstmt.setInt(18, NumberUtils.intValue(messageLog.get("day")));
								pstmt.setInt(19, NumberUtils.intValue(messageLog.get("hour")));
								pstmt.setInt(20, NumberUtils.intValue(messageLog.get("store_id")));
								
								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + messageLog.toString(), ex2);
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
}

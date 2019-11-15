package etl.dispatch.java.spap.ods;

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
import java.util.Optional;
import java.util.Map.Entry;
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
import etl.dispatch.java.ods.domain.DimIp;
import etl.dispatch.java.ods.domain.DimManufacturer;
import etl.dispatch.java.ods.domain.DimManufacturerModel;
import etl.dispatch.java.ods.domain.DimNetWork;
import etl.dispatch.java.ods.domain.DimOs;
import etl.dispatch.java.ods.domain.DimOsVersion;
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
public class Ods_Operation_counts extends AbstractScript implements ScheduledService {

	private static Logger logger = LoggerFactory.getLogger(Ods_Operation_counts.class);
	private static final String configJsonPath = "classpath*:conf/spapjson/ods_Operation_count.json";
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static DimAppPlat APPPLAT_NOT_PRESENT = new DimAppPlat();
	private final static DimAppVersion APPVERSION_NOT_PRESENT = new DimAppVersion();
	private final static DimManufacturerModel MANUFACTURERMODEL_NOT_PRESENT = new DimManufacturerModel();
	private final static DimManufacturer MANUFACTURER_NOT_PRESENT = new DimManufacturer();
	private final static DimOs OS_NOT_PRESENT = new DimOs();
	private final static DimOsVersion OSVERSION_NOT_PRESENT = new DimOsVersion();
	private final static DimNetWork NETWORK_NOT_PRESENT = new DimNetWork();
	
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
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER
	};
	
	private String sqlInsert ="INSERT INTO bi_ods_spap.operation_count(`id` ,`uid` ,`start_num1`,`start_num2`,`start_num3`,`start_num4`,`start_num5`,`start_num6`,`start_num7`,`start_num8`,`start_num9`,`start_num10`,`start_num11`,`start_num12`,`start_num13`,`start_num14`,`start_num15`,`start_num16`,`start_num17`,`start_num18`,`start_num19`,`start_num20`,`start_num21`,`start_num22`,`start_num23`,`start_num24`,`create_time`,`app_plat_id`,`country_id`,`region_id`,`city_id`,channel,app_version_id,`year`,`month`,`day`,`hour`,`store_id`)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlInsertWithoutValues;
	private Optional<Integer> saveDays = Optional.empty();
	private String optime_month;
	public Ods_Operation_counts() {
		this.messageQueueToFlush = new ArrayBlockingQueue<Map<Object,Object>>(DEFAULT_QUEUE_CAPACITY);
	}
		
	public String getName() {
		return "Ods_Speration_counts";
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
			saveDays = Optional.ofNullable((Integer)paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			optime_month = optime_yesday.substring(0 , optime_yesday.length() - 2);
			
			// 第1步 创建ODS目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "create_ods_operation");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			}else{
				logger.error("path "+configJsonPath+"; create target table, get sql with key'create_ods_loginLog' value is null "); 
			}
			
			// 支持重跑删除数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
			}
			
			// 第2步 limit分页查询记录
			int offset = 0;
			for (;;) {
				String limit_select_sql = super.getJsonConfigValue(configJsonPath, "selectOperationLimit");
				if (!StringUtil.isNullOrEmpty(limit_select_sql)) {
					limit_select_sql = limit_select_sql.replace("${offset}", String.valueOf(offset));
					limit_select_sql = limit_select_sql.replace("${rows}", String.valueOf(DEFAULT_LIMIT_MAX_SIZE));
					List<Map> rslist = SqlUtils.querySqlList(dataSource, limit_select_sql, this.getName());
					if (null == rslist || rslist.isEmpty()) {
						isDbEmpty.compareAndSet(false, true);
						logger.debug("select sql result is Empty;"+this.getClass().getCanonicalName()+"; Sql:"+ limit_select_sql); 
						break;
					} else {
						offset = offset + DEFAULT_LIMIT_MAX_SIZE;
						this.offer(rslist);
					}
				}else{
					isDbEmpty.compareAndSet(false, true);
				}
			}
			
			if(StringUtil.isNullOrEmpty(super.getJsonConfigValue(configJsonPath, "selectOperationLimit"))){
				logger.error("path "+configJsonPath+"; select source table, get sql with key 'selectLoginLogLimit' config value is null "); 
			}
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

	/**
	 * 查询数据转换并offer
	 * @param rslist
	 */
	@SuppressWarnings("all")
	private void offer(List<Map> rslist) {
		for (Map<Object, Object> rsMap : rslist) {							
			// offer清洗转换到队列
			// add to queue to wait for flushing
			if (!this.messageQueueToFlush.offer(rsMap)) {
				// the queue is full, flush first
				flush();
				if (!this.messageQueueToFlush.offer(rsMap)) {
					// fail again, maybe an error
					logger.error("failed to add login ods info to flushing queue.");
				}
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
		
		logger.info("flushing {}...", this.getName());
		List<Map<Object,Object>> messagesToFlush = new ArrayList<Map<Object,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<Object,Object>>> shardingFlushEntriesMap = new HashMap<Integer, List<Map<Object,Object>>>();
		for (Map<Object, Object> message : messagesToFlush) {
			Integer shardingKey = NumberUtils.intValue(message.get("network_id"));
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
				for(Map<Object,Object> loginLog : shardingFlushEntries) {
					Object[] params = new Object[] {
							loginLog.get("id"),
							loginLog.get("uid"),
							loginLog.get("start_num1"),
							loginLog.get("start_num2"),
							loginLog.get("start_num3"),
							loginLog.get("start_num4"),
							loginLog.get("start_num5"),
							loginLog.get("start_num6"),
							loginLog.get("start_num7"),
							loginLog.get("start_num8"),
							loginLog.get("start_num9"),
							loginLog.get("start_num10"),
							loginLog.get("start_num11"),
							loginLog.get("start_num12"),
							loginLog.get("start_num13"),
							loginLog.get("start_num14"),
							loginLog.get("start_num15"),
							loginLog.get("start_num16"),
							loginLog.get("start_num17"),
							loginLog.get("start_num18"),
							loginLog.get("start_num19"),
							loginLog.get("start_num20"),
							loginLog.get("start_num21"),
							loginLog.get("start_num22"),
							loginLog.get("start_num23"),
							loginLog.get("start_num24"),
							loginLog.get("create_time"),
							loginLog.get("app_plat_id"),
							loginLog.get("country_id"),
							loginLog.get("region_id"),
							loginLog.get("city_id"),
							loginLog.get("channel"),
							loginLog.get("app_version_id"),
						
							loginLog.get("year"),
							loginLog.get("month"),
							loginLog.get("day"),
							loginLog.get("hour"),
							loginLog.get("store_id")
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
					
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<Object, Object> loginLog : shardingFlushEntries) {
							try {
								pstmt.setString(1, String.valueOf(loginLog.get("id")));
								pstmt.setLong(2, NumberUtils.longValue(loginLog.get("uid")));
								pstmt.setLong(3, NumberUtils.longValue(loginLog.get("start_num1")));
								pstmt.setLong(4, NumberUtils.longValue(loginLog.get("start_num2")));
								pstmt.setLong(5, NumberUtils.longValue(loginLog.get("start_num3")));
								pstmt.setLong(6, NumberUtils.longValue(loginLog.get("start_num4")));
								pstmt.setLong(7, NumberUtils.longValue(loginLog.get("start_num5")));
								pstmt.setLong(8, NumberUtils.longValue(loginLog.get("start_num6")));
								pstmt.setLong(9, NumberUtils.longValue(loginLog.get("start_num7")));
								pstmt.setLong(10, NumberUtils.longValue(loginLog.get("start_num8")));
								pstmt.setLong(11, NumberUtils.longValue(loginLog.get("start_num9")));
								pstmt.setLong(12, NumberUtils.longValue(loginLog.get("start_num10")));
								pstmt.setLong(13, NumberUtils.longValue(loginLog.get("start_num11")));
								pstmt.setLong(14, NumberUtils.longValue(loginLog.get("start_num12")));
								pstmt.setLong(15, NumberUtils.longValue(loginLog.get("start_num13")));
								pstmt.setLong(16, NumberUtils.longValue(loginLog.get("start_num14")));
								pstmt.setLong(17, NumberUtils.longValue(loginLog.get("start_num15")));
								pstmt.setLong(18, NumberUtils.longValue(loginLog.get("start_num16")));
								pstmt.setLong(19, NumberUtils.longValue(loginLog.get("start_num17")));
								pstmt.setLong(20, NumberUtils.longValue(loginLog.get("start_num18")));
								pstmt.setLong(21, NumberUtils.longValue(loginLog.get("start_num19")));
								pstmt.setLong(22, NumberUtils.longValue(loginLog.get("start_num20")));
								pstmt.setLong(23, NumberUtils.longValue(loginLog.get("start_num21")));
								pstmt.setLong(24, NumberUtils.longValue(loginLog.get("start_num22")));
								pstmt.setLong(25, NumberUtils.longValue(loginLog.get("start_num23")));
								pstmt.setLong(26, NumberUtils.longValue(loginLog.get("start_num24")));
								pstmt.setLong(27, NumberUtils.longValue(loginLog.get(loginLog.get("create_time"))));
							
								pstmt.setInt(28, NumberUtils.intValue(loginLog.get("app_plat_id")));
								pstmt.setInt(29, NumberUtils.intValue(loginLog.get("country_id")));
								pstmt.setInt(30, NumberUtils.intValue(loginLog.get("region_id")));
								pstmt.setInt(31, NumberUtils.intValue(loginLog.get("city_id")));
								
								pstmt.setInt(32, NumberUtils.intValue(loginLog.get("channel")));
								pstmt.setInt(33, NumberUtils.intValue(loginLog.get("app_version_id")));
								
								pstmt.setInt(34, NumberUtils.intValue(loginLog.get("year")));
								pstmt.setInt(35, NumberUtils.intValue(loginLog.get("month")));
								pstmt.setInt(36, NumberUtils.intValue(loginLog.get("day")));
								pstmt.setInt(37, NumberUtils.intValue(loginLog.get("hour")));
								pstmt.setInt(38, NumberUtils.intValue(loginLog.get("store_id")));

								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods login log: " + ex2.getMessage() + ", failed message: \n\t" + loginLog.toString(), ex2);
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
}

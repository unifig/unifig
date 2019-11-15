package etl.dispatch.java.spap.ods;

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
import etl.dispatch.java.spap.ods.domain.SpapDimAppPlat;
import etl.dispatch.java.spap.ods.domain.SpapDimAppVersion;
import etl.dispatch.java.spap.ods.domain.SpapDimIndustry;
import etl.dispatch.java.spap.ods.domain.SpapDimIp;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturer;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturerModel;
import etl.dispatch.java.spap.ods.domain.SpapDimNetWork;
import etl.dispatch.java.spap.ods.domain.SpapDimOs;
import etl.dispatch.java.spap.ods.domain.SpapDimOsVersion;
import etl.dispatch.java.spap.ods.enums.SpapAppPlatAppIdEnum;
import etl.dispatch.java.spap.ods.service.SpapOdsFullDimHolderService;
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

@Service
public class Ods_SpapUserInformation extends AbstractScript implements ScheduledService  {

	private static Logger logger = LoggerFactory.getLogger(Ods_SpapUserInformation.class);
	private static final String configJsonPath = "classpath*:conf/spapjson/ods_SpapUserInformation.json";
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static SpapDimAppPlat APPPLAT_NOT_PRESENT = new SpapDimAppPlat();
	private final static SpapDimAppVersion APPVERSION_NOT_PRESENT = new SpapDimAppVersion();
	private final static SpapDimIndustry INDUSTRY_NOT_PRESENT = new SpapDimIndustry();
	private final static SpapDimManufacturerModel MANUFACTURERMODEL_NOT_PRESENT = new SpapDimManufacturerModel();
	private final static SpapDimManufacturer MANUFACTURER_NOT_PRESENT = new SpapDimManufacturer();
	private final static SpapDimOs OS_NOT_PRESENT = new SpapDimOs();
	private final static SpapDimOsVersion OSVERSION_NOT_PRESENT = new SpapDimOsVersion();
	private final static SpapDimNetWork NETWORK_NOT_PRESENT = new SpapDimNetWork();
	@Autowired
	private DimDataSource dimDataSource;
	@Autowired
	private DataSourcePool dataSourcePool;
	@Autowired
	private SpapOdsFullDimHolderService dimHolderService;
	
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private final static int BATCH_INSERT_COUNT = 512;
	private ArrayBlockingQueue<Map<String,Object>> messageQueueToFlush;
	private AtomicBoolean isDbEmpty = new AtomicBoolean(false);
	
	private final int[] PARAM_TYPES = new int[] {
			Types.BIGINT,
			Types.INTEGER,
			Types.BIGINT,
			
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.BIGINT,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
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
			Types.INTEGER,
			Types.INTEGER
	};
	
	private String sqlInsert ="INSERT INTO bi_ods_spap.ods_user_info_dm_yyyymm  (`user_id`, `new_user`,`spap_id`,`cube`,`nickname`,`face_src`,`large_face_src`,`small_face_src`,`email`,`mobile`,`register_ip`,`sex`,`qr_code`,`update_time`,`create_time`,`nn_time`,`profile_birth_time`,`email_activation_time`,`guest`,`register_state`,`industry_code`,`industry_name`,`province`,`city`,`county`,`register_type`,`age`,`industry_id`,`app_plat_id`, `app_id`, `app_version_id`,`os_id`,`os_version_id`,`manufacturer_id`,`manufacturer_model_id`,`isp_id`,`country_id`,`region_id`,`city_id`,`channel`,`network`,`year`,`month`,`day`,`hour`,`register_date`,`insert_date`)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlInsertWithoutValues;
	private Optional<Integer> saveDays = Optional.empty();
	private String optime_month;
	public Ods_SpapUserInformation() {
		this.messageQueueToFlush = new ArrayBlockingQueue<Map<String,Object>>(DEFAULT_QUEUE_CAPACITY);
	}
	
	@Override
	public void stop() {
		this.flush();
	}

	@Override
	public String getName() {
		return "ods_SpapUserInformation";
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
			saveDays = Optional.ofNullable((Integer)paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}
		try {
			String optime_yesday = ScriptTimeUtil.optime_yesday();
			optime_month = optime_yesday.substring(0 , optime_yesday.length() - 2);
			
			// 第1步 创建ODS目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "create_ods_userInfo");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace("ods_user_info_dm_yyyymm", "ods_user_info_dm_" + optime_month);
				SqlUtils.sqlExecute(dataSource, target_sql, this.getName());
			}else{
				logger.error("path "+configJsonPath+"; create target table, get sql with key'create_ods_userInfo' value is null "); 
			}
			
			// 支持重跑，删除当天数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if(!StringUtil.isNullOrEmpty(delete_yes_date)){
				delete_yes_date = delete_yes_date.replace("ods_user_info_dm_yyyymm", "ods_user_info_dm_" + optime_month);
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				if(!StringUtil.isNullOrEmpty(delete_yes_date)){
					SqlUtils.sqlExecute(dataSource, delete_yes_date, this.getName());
				}
			}

			// 第2步 limit分页查询记录
			int limit = 0;
			for (;;) {
				String limit_select_sql = super.getJsonConfigValue(configJsonPath, "selectUserInfoLimit");
				if (!StringUtil.isNullOrEmpty(limit_select_sql)) {
					limit_select_sql = limit_select_sql.replace("user_info_yyyymmdd", "user_info_" + ScriptTimeUtil.optime_yesday());
					limit_select_sql = limit_select_sql.replace("spap_login_log_yyyymmdd", "spap_login_log_" + ScriptTimeUtil.optime_yesday());
					limit_select_sql = limit_select_sql.replace("${store_id}", ScriptTimeUtil.optime_yesday());
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
			/*// 删除ods历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			String delete_date = super.getJsonConfigValue(configJsonPath, "delete_date");
			if (!StringUtil.isNullOrEmpty(delete_table) && !StringUtil.isNullOrEmpty(delete_date) && saveDays.isPresent()) {
				String delSQl = super.getDelHistorySql(delete_table, delete_date, "ods_user_info_dm_", saveDays.get());
				if(!StringUtil.isNullOrEmpty(delSQl)){
					SqlUtils.sqlExecute(dataSource, delSQl, this.getName());
				}
			}else{
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
				return;
			}*/
			
			if(StringUtil.isNullOrEmpty(super.getJsonConfigValue(configJsonPath, "selectUserInfoLimit"))){
				logger.error("path "+configJsonPath+"; select source table, get sql with key 'selectUserInfoLimit' config value is null "); 
			}
			for (;;) {
				//数据库数据Empty;且队列已Empty
				if (isDbEmpty.get() && messageQueueToFlush.isEmpty()) {
					super.callback(true, null, scriptBean, callback);
					return;
				}
				Thread.currentThread().sleep(10 * 1000);
			}
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
//			ex.printStackTrace();
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + ex.getMessage(), scriptBean, callback);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询数据转换并offer
	 * @param rslist
	 */
	private void offer(List<Map> rslist) {
		for (Map<String, Object> rsMap : rslist) {
			Map<String, Object> tmpCache = new HashMap<>();
			Iterator<Map.Entry<String, Object>> it = rsMap.entrySet().iterator();
			int appId = -9;
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				String keyVal = String.valueOf(entry.getKey());
				String value = String.valueOf(entry.getValue());
				//-9转换为未知
				if("-9".equalsIgnoreCase(value)){
					value= "未知";
				}
				switch (keyVal) {
				case "industry_id":
					SpapDimIndustry dimIndustry = dimHolderService.getIndustryByName(value, true);
					if (null == dimIndustry || dimIndustry == INDUSTRY_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("industry_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimIndustry.getId());
						tmpCache.put("industry_id", dimIndustry.getId());
					}
					break;
				case "app_plat_id":
					SpapDimAppPlat dimAppPlat = dimHolderService.getAppPlatByName(value, true);
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
					SpapAppPlatAppIdEnum enumObj = SpapAppPlatAppIdEnum.lookup.get(appPlatId);
					if(null !=enumObj){
						// app_id 赋值
						appId =enumObj.getAppId();
					}
					SpapDimAppVersion dimAppVersion = dimHolderService.getAppVersionByName(NumberUtils.intValue(appPlatId), appId, value, true);
					if (null == dimAppVersion || dimAppVersion == APPVERSION_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("app_version_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimAppVersion.getId());
						tmpCache.put("app_version_id", dimAppVersion.getId());
					}
					break;
				case "manufacturer_id":
					SpapDimManufacturer dimManufacturer = dimHolderService.getManufacturerByName(value, true);
					if (null == dimManufacturer || dimManufacturer == MANUFACTURER_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("manufacturer_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimManufacturer.getId());
						tmpCache.put("manufacturer_id", dimManufacturer.getId());
					}
					break;
				case "manufacturer_model_id":
					String manufacturerId = String.valueOf(tmpCache.get("manufacturer_id"));
					SpapDimManufacturerModel dimManufacturerModel = dimHolderService.getManufacturerModelByName(Short.parseShort(manufacturerId), value, true);
					if (null == dimManufacturerModel || dimManufacturerModel == MANUFACTURERMODEL_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("manufacturer_model_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimManufacturerModel.getId());
						tmpCache.put("manufacturer_model_id", dimManufacturerModel.getId());
					}
					break;
				case "os_id":
					SpapDimOs dimOs = dimHolderService.getOsByName(value, true);
					if (null == dimOs || dimOs == OS_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("os_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimOs.getId());
						tmpCache.put("os_id", dimOs.getId());
					}
					break;
				case "os_version_id":
					String osId = String.valueOf(tmpCache.get("os_id"));
					SpapDimOsVersion dimOsVersion = dimHolderService.getOsVersionByName(Short.parseShort(osId), value, true);
					if (null == dimOsVersion || dimOsVersion == OSVERSION_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("os_version_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimOsVersion.getId());
						tmpCache.put("os_version_id", dimOsVersion.getId());
					}
					break;
				case "age":
					if (StringUtil.isNullOrEmpty(value) || value.equalsIgnoreCase("0")) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("age", UNKNOWN_VALUE);
					} else {
						Date birthday = DateUtil.format2Date(NumberUtils.longValue(value));
						int age = DateUtil.getAgeByBirth(birthday);
						entry.setValue(age);
						tmpCache.put("age", age);
					}
					break;
					
				case "network_id":
					SpapDimNetWork dimNetWork = dimHolderService.getNetWorkByName(value, true);
					if (null == dimNetWork || dimNetWork == NETWORK_NOT_PRESENT) {
						entry.setValue(UNKNOWN_VALUE);
						tmpCache.put("network_id", UNKNOWN_VALUE);
					} else {
						entry.setValue(dimNetWork.getId());
						tmpCache.put("network_id", dimNetWork.getId());
					}
					break;
				default:
					break;
				}
			}
			// 设置app_id
			rsMap.put("app_id", appId);
			
			// 新用户标记
			long registerTime = NumberUtils.longValue(rsMap.get("create_time"));
			if (registerTime >= DateUtil.getDateTime(DateUtil.getSysStrCurrentDate("yyyy-MM-dd", -1) + " 00:00:00", "yyyy-MM-dd HH:mm:ss")) {
				rsMap.put("new_user", 0);
			} else {
				rsMap.put("new_user", 1);
			}
			
			// 注册时Ip地址; Isp运营商、国家、省份、地市
			String operIp = String.valueOf(rsMap.get("register_ip"));
			SpapDimIp dimIp = null;
			if(!StringUtil.isNullOrEmpty(operIp)){
				dimIp = dimHolderService.getInformation(operIp);
			}
			if (null == dimIp) {
				rsMap.put("isp_id", UNKNOWN_VALUE);
				rsMap.put("country_id", UNKNOWN_VALUE);
				rsMap.put("region_id", UNKNOWN_VALUE);
				rsMap.put("city_id", UNKNOWN_VALUE);
			} else {
				rsMap.put("isp_id", dimIp.getIspId());
				rsMap.put("country_id", dimIp.getCountryId());
				rsMap.put("region_id", dimIp.getRegionId());
				rsMap.put("city_id", dimIp.getCityId());
			}
			
			// offer清洗转换到队列
			// add to queue to wait for flushing
			if (!this.messageQueueToFlush.offer(rsMap)) {
				// the queue is full, flush first
				flush();
				if (!this.messageQueueToFlush.offer(rsMap)) {
					// fail again, maybe an error
					logger.error("failed to add user ods info to flushing queue.");
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
		if (!StringUtil.isNullOrEmpty(batchSqlInsert)) {
			batchSqlInsert = batchSqlInsert.replace("ods_user_info_dm_yyyymm", "ods_user_info_dm_" + optime_month);
		}
		logger.info("flushing {}...", this.getName());
		List<Map<String,Object>> messagesToFlush = new ArrayList<Map<String,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<String,Object>>> shardingFlushEntriesMap = new HashMap<Integer, List<Map<String,Object>>>();
		for (Map<String, Object> message : messagesToFlush) {
			Integer shardingKey = NumberUtils.intValue(message.get("sex"));
			List<Map<String, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(shardingKey);
			if (shardingFlushEntries == null) {
				shardingFlushEntries = new java.util.ArrayList<Map<String, Object>>(128);
				shardingFlushEntriesMap.put(shardingKey, shardingFlushEntries);
			}
			shardingFlushEntries.add(message);
		}
				
		for(List<Map<String,Object>> shardingFlushEntries : shardingFlushEntriesMap.values()) {		
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = dimDataSource.clusterDataSource().getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<String,Object> userLog : shardingFlushEntries) {
					Object[] params = new Object[] {
							userLog.get("user_id"),
							userLog.get("new_user"),
							userLog.get("spap_id"),
							userLog.get("cube"),
							userLog.get("nickname"),
							userLog.get("face_src"),
							userLog.get("large_face_src"),
							userLog.get("small_face_src"),
							userLog.get("email"),
							userLog.get("mobile"),
							userLog.get("register_ip"),
							userLog.get("sex"),
							userLog.get("qr_code"),
							userLog.get("update_time"),
							userLog.get("create_time"),
							userLog.get("nn_time"),
							userLog.get("profile_birth_time"),
							userLog.get("email_activation_time"),
							userLog.get("guest"),
							userLog.get("register_state"),
							userLog.get("industry_code"),
							userLog.get("industry_name"),
							userLog.get("province"),
							userLog.get("city"),
							userLog.get("county"),
							userLog.get("register_type"),
							userLog.get("age"),
							NumberUtils.intValue(userLog.get("industry_id"), -9),
							NumberUtils.intValue(userLog.get("app_plat_id"), -9),
							NumberUtils.intValue(userLog.get("app_id"), -9),
							NumberUtils.intValue(userLog.get("app_version_id"), -9),
							NumberUtils.intValue(userLog.get("os_id"), -9),
							NumberUtils.intValue(userLog.get("os_version_id"), -9),
							NumberUtils.intValue(userLog.get("manufacturer_id"), -9),
							NumberUtils.intValue(userLog.get("manufacturer_model_id"), -9),
							NumberUtils.intValue(userLog.get("isp_id"), -9),
							NumberUtils.intValue(userLog.get("country_id"), -9),
							NumberUtils.intValue(userLog.get("region_id"), -9),
							NumberUtils.intValue(userLog.get("city_id"), -9),
							NumberUtils.intValue(userLog.get("channel_id"), -9),
							NumberUtils.intValue(userLog.get("network_id"),-9),
							userLog.get("year"),
							userLog.get("month"),
							userLog.get("day"),
							userLog.get("hour"),
							userLog.get("store_id"),
							NumberUtils.intValue(ScriptTimeUtil.optime_yesday())
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
							singlSqlInsert = singlSqlInsert.replace("ods_user_info_dm_yyyymm", "ods_user_info_dm_" + optime_month);
						}
						
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<String, Object> userLog : shardingFlushEntries) {
							try {
								pstmt.setLong(1, NumberUtils.longValue(userLog.get("user_id")));
								pstmt.setInt(2, NumberUtils.intValue(userLog.get("new_user")));
								pstmt.setLong(3, NumberUtils.longValue(userLog.get("spap_id")));
								
								pstmt.setString(4, String.valueOf(userLog.get("cube")));
								pstmt.setString(5, String.valueOf(userLog.get("nickname")));
								pstmt.setString(6, String.valueOf(userLog.get("face_src")));
								pstmt.setString(7, String.valueOf(userLog.get("large_face_src")));
								pstmt.setString(8, String.valueOf(userLog.get("small_face_src")));
								pstmt.setString(9, String.valueOf(userLog.get("email")));
								pstmt.setString(10, String.valueOf(userLog.get("mobile")));
								pstmt.setString(11, String.valueOf(userLog.get("register_ip")));
								pstmt.setInt(12, NumberUtils.intValue(userLog.get("sex")));
								pstmt.setString(13, String.valueOf(userLog.get("qr_code")));
								pstmt.setLong(14, NumberUtils.longValue(userLog.get("update_time")));
								pstmt.setLong(15, NumberUtils.longValue(userLog.get("create_time")));
								pstmt.setLong(16, NumberUtils.longValue(userLog.get("nn_time")));
								pstmt.setLong(17, NumberUtils.longValue(userLog.get("profile_birth_time")));
								pstmt.setLong(18, NumberUtils.longValue(userLog.get("email_activation_time")));
								pstmt.setInt(19, NumberUtils.intValue(userLog.get("guest")));
								pstmt.setInt(20, NumberUtils.intValue(userLog.get("register_state")));
								pstmt.setInt(21, NumberUtils.intValue(userLog.get("industry_code")));
								pstmt.setString(22, String.valueOf(userLog.get("industry_name")));
								pstmt.setString(23, String.valueOf(userLog.get("province")));
								pstmt.setString(24, String.valueOf(userLog.get("city")));
								pstmt.setString(25, String.valueOf(userLog.get("county")));
								pstmt.setInt(26, NumberUtils.intValue(userLog.get("register_type")));
								pstmt.setInt(27, NumberUtils.intValue(userLog.get("age")));
								
								pstmt.setInt(28, NumberUtils.intValue(userLog.get("industry_id"), -9));
								pstmt.setInt(29, NumberUtils.intValue(userLog.get("app_plat_id"), -9));
								pstmt.setInt(30, NumberUtils.intValue(userLog.get("app_id"), -9));
								pstmt.setInt(31, NumberUtils.intValue(userLog.get("app_version_id"), -9));
								pstmt.setInt(32, NumberUtils.intValue(userLog.get("os_id"), -9));
								pstmt.setInt(33, NumberUtils.intValue(userLog.get("os_version_id"), -9));
								pstmt.setInt(34, NumberUtils.intValue(userLog.get("manufacturer_id"), -9));
								pstmt.setInt(35, NumberUtils.intValue(userLog.get("manufacturer_model_id"), -9));
								pstmt.setInt(36,NumberUtils.intValue(userLog.get("isp_id"), -9));
								pstmt.setInt(37,NumberUtils.intValue(userLog.get("country_id"), -9));
								pstmt.setInt(38,NumberUtils.intValue(userLog.get("region_id"), -9));
								pstmt.setInt(39,NumberUtils.intValue(userLog.get("city_id"), -9));
								pstmt.setInt(40,NumberUtils.intValue(userLog.get("channel_id"),-9));
								pstmt.setInt(41, NumberUtils.intValue(userLog.get("network_id"),-9));
								pstmt.setInt(42, NumberUtils.intValue(userLog.get("year")));
								pstmt.setInt(43, NumberUtils.intValue(userLog.get("month")));
								pstmt.setInt(44, NumberUtils.intValue(userLog.get("day")));
								pstmt.setInt(45, NumberUtils.intValue(userLog.get("hour")));
								pstmt.setInt(46, NumberUtils.intValue(userLog.get("store_id")));
								pstmt.setInt(47, NumberUtils.intValue(ScriptTimeUtil.optime_yesday()));

								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + userLog.toString(), ex2);
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

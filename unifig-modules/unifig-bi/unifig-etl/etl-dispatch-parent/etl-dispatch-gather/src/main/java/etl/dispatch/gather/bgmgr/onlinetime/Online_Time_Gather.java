package etl.dispatch.gather.bgmgr.onlinetime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tools.plugin.utils.DateUtil;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.gather.bgmgr.enums.LogType;
import etl.dispatch.gather.bgmgr.enums.PlatType;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.domain.DimIp;
import etl.dispatch.java.ods.service.OdsFullDimHolderService;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.JdbcUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

/**
 * 
 * @Description: 计算时长：拉取登陆登出数据，分类插入登陆登出表
 * @author: ylc
 */
@Service
public class Online_Time_Gather extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(Online_Time_Gather.class);
	private static final String configJsonPath = "classpath*:conf/json/online_time_gather.json";
	private final static int BATCH_INSERT_COUNT = 512;
	public final static int UNKNOWN_VALUE = -9;
	@Autowired
	private OdsFullDimHolderService holderService;

	private DataSourcePool sourcePool;
	private DataSourcePool targetPool;
	
	
	public String getName() {
		return "Online_Time_gather";
	}

	private final int[] PARAM_TYPES = new int[] { 
			Types.VARCHAR, 
			Types.INTEGER,
			Types.INTEGER,
			Types.BIGINT, 
			Types.BIGINT
		};
	
	private final int[] PARAM_TYPES_ALL = new int[] { 
			Types.VARCHAR,
			Types.BIGINT,
			Types.VARCHAR, 
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER
		};
	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		DataSource source = null;
		DataSource target = null;
		if (null != paramMap && !paramMap.isEmpty()) {
			// 来源数据库
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_SOURCEDATA);
			// 目标数据库
			Map<String, Object> dataTargetMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_TARGETDATA);
			if ((null != dataSourceMap && !dataSourceMap.isEmpty()) && (null != dataTargetMap && !dataTargetMap.isEmpty())) {
				sourcePool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				source = sourcePool.getDataSource(dataSourceMap);
				targetPool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				target = targetPool.getDataSource(dataTargetMap);
				if (null == source || null == target) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				} else {
					logger.info(" Server Ip:" + IpUtils.getIPAddress() + "---> [" + this.getClass().getCanonicalName() + "]; dataSource url:" + dataSourceMap.get("url") + " , dataTarget url:" + dataTargetMap.get("url"));
				}
			}
		}

		try {
			// 创建目标表 ；in, out 插入批次 30分钟
			String create_login = super.getJsonConfigValue(configJsonPath, "create_dm_login_yyyymm");
			if (!StringUtil.isNullOrEmpty(create_login)) {
				SqlUtils.sqlExecute(target, create_login, this.getClass().getName());
			}
			String create_logout = super.getJsonConfigValue(configJsonPath, "create_dm_logout_yyyymm");
			if (!StringUtil.isNullOrEmpty(create_logout)) {
				SqlUtils.sqlExecute(target, create_logout, this.getClass().getName());
			}
			// 创建日志明细表
			String create_all_log = super.getJsonConfigValue(configJsonPath, "create_all_log_yyyymm");
			if (!StringUtil.isNullOrEmpty(create_all_log)) {
				create_all_log = create_all_log.replace("all_time_log_yyyymm", "all_time_log_" + DateUtil.getSysStrCurrentDate("yyyyMM"));
				SqlUtils.sqlExecute(target, create_all_log, this.getClass().getName());
			}

			// 拉取数据
			// super.deleteCach(this.getClass().getCanonicalName());
			long last_time = 0l;
			Long cacheTime = NumberUtils.longValue(super.findCach(this.getClass().getCanonicalName()));
			if (cacheTime > 0) {
				last_time = cacheTime;
			}

			String selectSource = super.getJsonConfigValue(configJsonPath, "selectSource");
			List<Map> querySqlList = null;
			if (!StringUtil.isNullOrEmpty(selectSource)) {
				selectSource = selectSource.replace("loginout_log_yyyy_mm", "loginout_log_" + DateUtil.getSysStrCurrentDate("yyyy_MM"));
				selectSource = selectSource.replace("${timestamp}", String.valueOf(last_time));
				querySqlList = SqlUtils.querySqlList(source, selectSource, this.getClass().getName());
				// 更新查询时间点
				super.saveCach(this.getClass().getCanonicalName(), DateUtil.getSysCurrentTimestamp().getTime());
			}
			
			List<Map<String, Object>> messagesToFlush = new ArrayList<>();
			if (querySqlList != null && !querySqlList.isEmpty()) {
				for (Map<String, Object> resMap : querySqlList) {
					// 获取用户数据
					String uinfo = String.valueOf(resMap.get("user"));
					if (!StringUtil.isNullOrEmpty(uinfo)) {
						// 用户信息
						JSONObject jsonObject = JSON.parseObject(uinfo);
						// Cube账号
						resMap.put("cube_id", NumberUtils.intValue(jsonObject.get("name")));
						// 设备信息
						List<Map<Object, Object>> deviceMap = (List<Map<Object, Object>>) jsonObject.get("devices");
						if (null != deviceMap && !deviceMap.isEmpty()) {
							// 平台类型CN
							String platName = String.valueOf(deviceMap.get(0).get("name")).toLowerCase();
							PlatType platType = PlatType.lookup.get(platName);
							String descLowerCase = (null == platType) ? "未知" : platType.getDescLowerCase();
							DimAppPlat dimAppPlat = holderService.getAppPlatByName(descLowerCase, true);
							// 平台类型Id
							resMap.put("plat_id", NumberUtils.intValue(dimAppPlat.getId(), -9));
						}
					}
					// 获取客户端Ip地址
					String operIp = String.valueOf(resMap.get("ip"));
					DimIp dimIp = null;
					if (!StringUtil.isNullOrEmpty(operIp)) {
						dimIp = holderService.getInformation(operIp);
					}
					if (null == dimIp) {
						resMap.put("isp_id", UNKNOWN_VALUE);
						resMap.put("country_id", UNKNOWN_VALUE);
						resMap.put("region_id", UNKNOWN_VALUE);
						resMap.put("city_id", UNKNOWN_VALUE);
					} else {
						resMap.put("isp_id", dimIp.getIspId());
						resMap.put("country_id", dimIp.getCountryId());
						resMap.put("region_id", dimIp.getRegionId());
						resMap.put("city_id", dimIp.getCityId());
					}
					messagesToFlush.add(resMap);
				}
			}
			// 日志按类型分组插入
			this.groupLogInsert(messagesToFlush, target);
			// 日志全量数据插入
			this.fullsLogInsert(messagesToFlush, target);
			// 完成回调
			super.callback(true, null, scriptBean, callback);
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getClass().getName() + ", DataBase IP :" + super.getUrl(source) + ",message: " + ex.getMessage(), scriptBean, callback);
		}
	}
	
	/**
	 * 日志数据分组批量插入
	 * @param messagesToFlush
	 * @param target
	 * @throws IOException
	 */
	public void groupLogInsert(List<Map<String, Object>> messagesToFlush, DataSource target) throws IOException {
		if (null == messagesToFlush || messagesToFlush.isEmpty()) {
			return;
		}
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<String, List<Map<String, Object>>> shardingFlushEntriesMap = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> message : messagesToFlush) {
			String shardingKey = String.valueOf(message.get("type"));
			List<Map<String, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(shardingKey);
			if (shardingFlushEntries == null) {
				shardingFlushEntries = new java.util.ArrayList<Map<String, Object>>(128);
				shardingFlushEntriesMap.put(shardingKey, shardingFlushEntries);
			}
			shardingFlushEntries.add(message);
		}
		String insertLogin  = super.getJsonConfigValue(configJsonPath, "insertLogin");
		String insertLogout = super.getJsonConfigValue(configJsonPath, "insertLogout");
		for(List<Map<String,Object>> shardingFlushEntries : shardingFlushEntriesMap.values()) {		
			String sqlInsert =  null;
			String batchSqlInsert = null;
			// 登入日志批处理SQL
			if (LogType.LOGIN.getDesc().equals(shardingFlushEntries.get(0).get("type"))) {
				sqlInsert = insertLogin;
				String sqlInsertWithoutValues = null;
				if (!StringUtil.isNullOrEmpty(sqlInsert)) {
					sqlInsertWithoutValues = sqlInsert;
					int delimiter = sqlInsert.indexOf("VALUES ");
					if (delimiter == -1) {
						delimiter = sqlInsert.indexOf("values ");
					}
					if (delimiter != -1) {
						sqlInsertWithoutValues = sqlInsert.substring(0, delimiter);
					}
					batchSqlInsert = sqlInsertWithoutValues;
				}
			}
			// 登出日志批处理SQL
			if (LogType.LOGOUT.getDesc().equals(shardingFlushEntries.get(0).get("type"))) {
			    sqlInsert = insertLogout;
				String sqlInsertWithoutValues = null;
				if (!StringUtil.isNullOrEmpty(sqlInsert)) {
					sqlInsertWithoutValues = sqlInsert;
					int delimiter = sqlInsert.indexOf("VALUES ");
					if (delimiter == -1) {
						delimiter = sqlInsert.indexOf("values ");
					}
					if (delimiter != -1) {
						sqlInsertWithoutValues = sqlInsert.substring(0, delimiter);
					}
					batchSqlInsert = sqlInsertWithoutValues;
				}
			}
			
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = target.getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<String,Object> logMap : shardingFlushEntries) {
					Object[] params = new Object[] { 
							logMap.get("type"), 
							logMap.get("cube_id"), 
							logMap.get("plat_id"), 
							logMap.get("timestamp"), 
							super.getHash64(logMap,"login_primary_key") 
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
					logger.error("fatal error while flushing " + this.getClass().getName() + ", message: " + ex.getMessage(), ex);
				} else {
					logger.error("SQL exception while flushing " + this.getClass().getName() + ": " + ex.getMessage(), ex);
					// 非致命错误（如字段值超过数据库定义等常规异常），尝试单条flush，尽量减少失败的影响
					try {
						if (!autoCommit0)
							connection.rollback();
						String singlSqlInsert = sqlInsert;
						
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<String, Object> logMap : shardingFlushEntries) {
							try {
								pstmt.setString(1, String.valueOf(logMap.get("type")));
								pstmt.setInt(2, NumberUtils.intValue(logMap.get("cube_id")));
								pstmt.setInt(3, NumberUtils.intValue(logMap.get("plat_id")));
								pstmt.setLong(4, NumberUtils.longValue(logMap.get("timestamp")));
								pstmt.setLong(5, NumberUtils.longValue(super.getHash64(logMap,"login_primary_key")));
								pstmt.executeUpdate();
							} catch (SQLException ex2) {
								logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + logMap.toString(), ex2);
							}
						}
						if (!autoCommit0)
							connection.commit();
						pstmt.close();
					} catch (SQLException e) {
						logger.error("error while rollback " + this.getClass().getName() + ": " + e.getMessage(), e);
					}
				}
			} finally {
				try {
					if (null != connection) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error("error while connection close " + this.getClass().getName() + ": " + e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * 异步批量入库
	 * @param messagesToFlush
	 * @param target
	 * @throws IOException
	 */
	public void fullsLogInsert(List<Map<String, Object>> messagesToFlush, DataSource target) throws IOException {
		if (null == messagesToFlush || messagesToFlush.isEmpty()) {
			return;
		}
		String sqlInsert = super.getJsonConfigValue(configJsonPath, "insertLogAll");
		String batchSqlInsert = null;
		String sqlInsertWithoutValues = null;
		if (!StringUtil.isNullOrEmpty(sqlInsert)) {
			sqlInsertWithoutValues = sqlInsert;
			int delimiter = sqlInsert.indexOf("VALUES ");
			if (delimiter == -1) {
				delimiter = sqlInsert.indexOf("values ");
			}
			if (delimiter != -1) {
				sqlInsertWithoutValues = sqlInsert.substring(0, delimiter);
			}
			batchSqlInsert = sqlInsertWithoutValues;
		}
		if(!StringUtil.isNullOrEmpty(batchSqlInsert)){
			batchSqlInsert = batchSqlInsert.replace("all_time_log_yyyymm", "all_time_log_" + DateUtil.getSysStrCurrentDate("yyyyMM"));
		}
		final String batchSql = batchSqlInsert;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			public void run() {
				Connection connection = null;
				boolean autoCommit0 = false;
				try {
					connection = target.getConnection();
					autoCommit0 = connection.getAutoCommit();
					Statement pstmt = connection.createStatement();
					int count = 0;
					StringBuilder sqlBuilder = new StringBuilder(batchSql);
					for(Map<String,Object> logMap : messagesToFlush) {
						Object[] params = new Object[] {
								logMap.get("login_primary_key"),
								logMap.get("timestamp"), 
								logMap.get("type"), 
								logMap.get("cube_id"), 
								logMap.get("plat_id"), 
								logMap.get("ip"),
								logMap.get("isp_id"),
								logMap.get("country_id"),
								logMap.get("region_id"),
								logMap.get("city_id")
						};
						
						SqlUtils.appendSqlValues(sqlBuilder, params, PARAM_TYPES_ALL);
						++count;
						if(count >= BATCH_INSERT_COUNT) {
							pstmt.executeUpdate(sqlBuilder.toString());
							if(!autoCommit0) connection.commit();
							count = 0;
							sqlBuilder = new StringBuilder(batchSql);
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
						logger.error("fatal error while flushing " + this.getClass().getName() + ", message: " + ex.getMessage(), ex);
					} else {
						logger.error("SQL exception while flushing " + this.getClass().getName() + ": " + ex.getMessage(), ex);
						// 非致命错误（如字段值超过数据库定义等常规异常），尝试单条flush，尽量减少失败的影响
						try {
							if (!autoCommit0)
								connection.rollback();
							String singlSqlInsert = sqlInsert;
							if(!StringUtil.isNullOrEmpty(singlSqlInsert)){
								singlSqlInsert = singlSqlInsert.replace("all_time_log_yyyymm", "all_time_log_" + DateUtil.getSysStrCurrentDate("yyyyMM"));
							}
							
							// try again in non-batch mode
							PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
							for (Map<String, Object> logMap : messagesToFlush) {
								try {
									pstmt.setString(1, String.valueOf(logMap.get("login_primary_key")));
									pstmt.setLong(2, NumberUtils.longValue(logMap.get("timestamp")));
									pstmt.setString(3, String.valueOf(logMap.get("type")));
									pstmt.setInt(4, NumberUtils.intValue(logMap.get("cube_id")));
									pstmt.setInt(5, NumberUtils.intValue(logMap.get("plat_id")));
									pstmt.setString(6, String.valueOf(logMap.get("ip")));
									pstmt.setInt(7, NumberUtils.intValue(logMap.get("isp_id")));
									pstmt.setInt(8, NumberUtils.intValue(logMap.get("country_id")));
									pstmt.setInt(9, NumberUtils.intValue(logMap.get("region_id")));
									pstmt.setInt(10, NumberUtils.intValue(logMap.get("city_id")));

									pstmt.executeUpdate();
								} catch (SQLException ex2) {
									logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + logMap.toString(), ex2);
								}
							}
							if (!autoCommit0)
								connection.commit();
							pstmt.close();
						} catch (SQLException e) {
							logger.error("error while rollback " + this.getClass().getName() + ": " + e.getMessage(), e);
						}
					}
				} finally {
					try {
						if (null != connection) {
							connection.close();
						}
					} catch (SQLException e) {
						logger.error("error while connection close " + this.getClass().getName() + ": " + e.getMessage(), e);
					}
				}
			
			}
		});
	}

	@Override
	public void stop() {

	}
}

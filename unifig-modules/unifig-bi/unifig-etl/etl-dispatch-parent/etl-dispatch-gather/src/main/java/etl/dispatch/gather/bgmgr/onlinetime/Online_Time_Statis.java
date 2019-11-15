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
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.utils.DateUtil;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.java.datasource.DataSourcePool;
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
 * @Description: 计算时长
 * @author: ylc
 */
@Service
public class Online_Time_Statis extends AbstractScript{
	private static Logger logger = LoggerFactory.getLogger(Online_Time_Statis.class);
	private static final String configJsonPath = "classpath*:conf/json/online_time_statis.json";
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	private final static int BATCH_INSERT_COUNT = 512;
	@Autowired
	private DataSourcePool sourcePool;

	public String getName() {
		return "Online_Time_Statis";
	}
	private final int[] PARAM_TYPES = new int[] {
			Types.INTEGER, 
			Types.INTEGER, 
			Types.BIGINT, 
			Types.BIGINT, 
	};
	
	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		DataSource dataSource = null;
		if (null != paramMap && !paramMap.isEmpty()) {
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_DATASOURCE);
			if (null != dataSourceMap && !dataSourceMap.isEmpty()) {
				sourcePool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				dataSource = sourcePool.getDataSource(dataSourceMap);
				if (null == dataSource) {
					super.callback(false, "数据源获取失败; dataSource config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				}else{
					logger.info(" Server Ip:"+IpUtils.getIPAddress()+"---> [" + this.getClass().getCanonicalName() + "]; dataSource url:"+ dataSourceMap.get("url"));
				}
			}
		}
		try {
			// 第一步：创建单次临时表
			String creat_tmptime = super.getJsonConfigValue(configJsonPath, "creat_tmptime");
			if (!StringUtil.isNullOrEmpty(creat_tmptime)) {
				SqlUtils.sqlExecute(dataSource, creat_tmptime, this.getName());
			}
			// 第二步：清空单次临时统计表
			String delete_tmptime = super.getJsonConfigValue(configJsonPath, "trancate_tmptime");
			if (!StringUtil.isNullOrEmpty(delete_tmptime)) {
				SqlUtils.sqlExecute(dataSource, delete_tmptime, this.getName());
			}
			// 第三步：创建目标表
			String creat_alltime = super.getJsonConfigValue(configJsonPath, "creat_alltime");
			if (!StringUtil.isNullOrEmpty(creat_alltime)) {
				SqlUtils.sqlExecute(dataSource, creat_alltime, this.getName());
			}
			// 第四步 limit分页查询记录
			int offset = 0;
			List<Long> hashKeyList = new ArrayList<Long>();
			for (;;) {
				String limit_select_sql = super.getJsonConfigValue(configJsonPath, "selectTimeLogLimit");
				String sqlBatchInsertTmp = super.getJsonConfigValue(configJsonPath, "sqlBatchInsertTmp");
				if (!StringUtil.isNullOrEmpty(limit_select_sql)) {
					limit_select_sql = limit_select_sql.replace("${offset}", String.valueOf(offset));
					limit_select_sql = limit_select_sql.replace("${rows}", String.valueOf(DEFAULT_LIMIT_MAX_SIZE));
					List<Map> rslist = SqlUtils.querySqlList(dataSource, limit_select_sql, this.getName());
					if (null == rslist || rslist.isEmpty()) {
						logger.debug("select sql result is Empty;" + this.getClass().getCanonicalName() + "; Sql:" + limit_select_sql);
						break;
					} else {
						// 批量插入到单次临时表
						if (!StringUtil.isNullOrEmpty(sqlBatchInsertTmp)) {
							List<Long> rsKeylist= this.offer(rslist, dataSource, sqlBatchInsertTmp);
							if(null!=rsKeylist &&  !rsKeylist.isEmpty()){
								hashKeyList.addAll(rsKeylist);
							}
						}
						offset = offset + DEFAULT_LIMIT_MAX_SIZE; 
					}
				}
			}
			// 第五步：历史数据 插入单次临时表 
			String sqlInsertHistTmp = super.getJsonConfigValue(configJsonPath, "sqlInsertHistTmp");
			if (!StringUtil.isNullOrEmpty(sqlInsertHistTmp)) {
				SqlUtils.sqlExecute(dataSource, sqlInsertHistTmp, this.getName());
			}
			// 第六步：清空trancate历史表
			String trancate_histime = super.getJsonConfigValue(configJsonPath, "trancate_histime");
			if (!StringUtil.isNullOrEmpty(trancate_histime)) {
				SqlUtils.sqlExecute(dataSource, trancate_histime, this.getName());
			}
			// 第七步：单次临时表 汇总插入到最终表(0:离线； 1:在线)；默认离线状态
			String sqlInsertStatis = super.getJsonConfigValue(configJsonPath, "sqlInsertStatis");
			if (!StringUtil.isNullOrEmpty(sqlInsertStatis)) {
				SqlUtils.sqlExecute(dataSource, sqlInsertStatis, this.getName());
			}
			// 第八步：更新在线设备状态(0:离线； 1:在线)；
			List<Map> rslist =  null;
			String selectUpdateStatusLog = super.getJsonConfigValue(configJsonPath, "selectUpdateStatusLog");
			if (!StringUtil.isNullOrEmpty(selectUpdateStatusLog)) {
				rslist = SqlUtils.querySqlList(dataSource, selectUpdateStatusLog, this.getName());
			}
			String updateStatusLog = super.getJsonConfigValue(configJsonPath, "updateStatusLog");
			if(rslist != null){
				this.update(rslist, dataSource,updateStatusLog);
			}
			
			// 第九步：删除匹配过日志
			String deleteLogIn = super.getJsonConfigValue(configJsonPath, "deleteLogIn");
			if (!StringUtil.isNullOrEmpty(deleteLogIn)) {
				SqlUtils.sqlExecute(dataSource, deleteLogIn, this.getName());
			}
			String deleteLogOut = super.getJsonConfigValue(configJsonPath, "deleteLogOut");
			if (!StringUtil.isNullOrEmpty(deleteLogOut) && !hashKeyList.isEmpty()) {
				this.delete(hashKeyList, dataSource, deleteLogOut);
			}
			String delete_tmplog = super.getJsonConfigValue(configJsonPath, "trancate_tmptime");
			if (!StringUtil.isNullOrEmpty(delete_tmplog)) {
				SqlUtils.sqlExecute(dataSource, delete_tmplog, this.getName());
			}

			// 完成回调
			super.callback(true, null, scriptBean, callback);
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(dataSource) + ",message: " + ex.getMessage(), scriptBean, callback);
		}
	}
	private void update(List<Map> rslist, DataSource dataSource,String updateStatusLog) throws SQLException {
		Connection connection = dataSource.getConnection();
		try {
			String singlSqlUpdate = updateStatusLog;
			PreparedStatement pstmt = connection.prepareStatement(singlSqlUpdate);
			for (Map<String, Object> log : rslist) {
				try {
					pstmt.setInt(1, 1);//(0:离线； 1:在线)；默认离线状态，在线的更新为在线状态
					pstmt.setInt(2, NumberUtils.intValue(log.get("cube_id")));
					pstmt.setInt(3, NumberUtils.intValue(log.get("plat_id")));

					pstmt.executeUpdate();
				} catch (SQLException ex2) {
					logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + log.toString(), ex2);
				}
			}
			pstmt.close();
		} catch (SQLException e) {
			logger.error("error while rollback " + this.getName() + ": " + e.getMessage(), e);
		}finally {
			try {
				if (null != connection) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("error while connection close " + this.getName() + ": " + e.getMessage(), e);
			}
		}
	}
	
    private void delete(List<Long> rslist, DataSource dataSource,String deleteLogOut) throws SQLException {
		Connection connection = dataSource.getConnection();
		try {
			String singlSqlDelete = deleteLogOut;
			PreparedStatement pstmt = connection.prepareStatement(singlSqlDelete);
			for (Long hashKey : rslist) {
				try {
					pstmt.setLong(1, hashKey);
					pstmt.executeUpdate();
				} catch (SQLException ex2) {
					logger.error("SQL exception while save ods user info : " + ex2.getMessage() + ", failed message: \n\t" + hashKey.toString(), ex2);
				}
			}
			pstmt.close();
		} catch (SQLException e) {
			logger.error("error while rollback " + this.getName() + ": " + e.getMessage(), e);
		}finally {
			try {
				if (null != connection) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("error while connection close " + this.getName() + ": " + e.getMessage(), e);
			}
		}
	}
	
	
	/**
	 * 分批次处理数据
	 * @param rslist
	 */
	@SuppressWarnings("rawtypes")
	private List<Long> offer(List<Map> rslist, DataSource dataSource, String sqlInsert){
		List<Map> overLogList = new ArrayList<Map>();
		List<Long> keyList  = new ArrayList<Long>();
		for (Map<Object, Object> rsMap : rslist) {
			String status  = String.valueOf(rsMap.get("status"));
			long outTime = NumberUtils.longValue(rsMap.get("outTime"));
			long inTime = NumberUtils.longValue(rsMap.get("inTime"));
			long hashid = NumberUtils.longValue(rsMap.get("hashid"));
			if (!status.equalsIgnoreCase("unover")) {
				//离线设备计算时长
				rsMap.put("onlineTime", (outTime - inTime));
				keyList.add(hashid);
			} else {
				//在线设备不算时长(若缺少下线记录，重复计算会导致时长异常)
				rsMap.put("onlineTime", 0);
			}
			overLogList.add(rsMap);
		}
		this.flush(dataSource, sqlInsert, rslist);
		return keyList;
	}
	
	/**
	 * 批量flush数据入库
	 */
	private void flush(DataSource dataSource, String sqlInsert, List<Map> rslist) {
		if (rslist.isEmpty()) {
			return;
		}
		String batchSqlInsert = null;
		String sqlInsertWithoutValues = null;
		if (!StringUtil.isNullOrEmpty(sqlInsert)) {
			sqlInsertWithoutValues = sqlInsert;
			// 开启多values插入方式，准备手动构建多值插入的SQL
			int delimiter = sqlInsert.indexOf("VALUES ");
			if (delimiter == -1) {
				delimiter = sqlInsert.indexOf("values ");
			}
			if (delimiter != -1) {
				sqlInsertWithoutValues = sqlInsert.substring(0, delimiter);
			}
			batchSqlInsert = sqlInsertWithoutValues;
		}
		logger.info("flushing {}...", this.getName());
		List<Map> messagesToFlush = rslist;
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<String, Object>>> shardingFlushEntriesMap = new HashMap<Integer, List<Map<String, Object>>>();
		for (Map<String, Object> message : messagesToFlush) {
			Integer shardingKey = NumberUtils.intValue(message.get("plat_id"));
			List<Map<String, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(shardingKey);
			if (shardingFlushEntries == null) {
				shardingFlushEntries = new java.util.ArrayList<Map<String, Object>>(128);
				shardingFlushEntriesMap.put(shardingKey, shardingFlushEntries);
			}
			shardingFlushEntries.add(message);
		}
		for (List<Map<String, Object>> shardingFlushEntries : shardingFlushEntriesMap.values()) {
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = dataSource.getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for (Map<String, Object> log : shardingFlushEntries) {
					Object[] params = new Object[] { 
							NumberUtils.intValue(log.get("cube_id")), 
							NumberUtils.intValue(log.get("plat_id")), 
							NumberUtils.longValue(log.get("hashid")), 
							NumberUtils.longValue(log.get("onlineTime")), 
					};
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
						String singlSqlInsert = sqlInsert;

						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<String, Object> log : shardingFlushEntries) {
							try {
								pstmt.setInt(1, NumberUtils.intValue(log.get("cube_id")));
								pstmt.setInt(2, NumberUtils.intValue(log.get("plat_id")));
								pstmt.setLong(3, NumberUtils.longValue(log.get("hashid")));
								pstmt.setLong(4, NumberUtils.longValue(log.get("online_time")));

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
		
	}
}

package etl.dispatch.gather.bgmgr.schedule.online;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.gather.bgmgr.enums.ScheduleActionType;
import etl.dispatch.gather.bgmgr.schedule.offline.ScheduleInfoGatherService;
import etl.dispatch.java.datasource.DataSourcePool;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.JdbcUtil;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

/**
 * 
 * @Description: 日程管理
 * @author: ylc
 */
@Service
public class ScheduleInfoOnLineService extends AbstractScript implements ScheduledService{
	private static Logger logger = LoggerFactory.getLogger(ScheduleInfoOnLineService.class);
	private static final String configJsonPath = "classpath*:conf/json/schedule_info_online_service.json";
	private Long last_timestamp = 0l;
	private ArrayBlockingQueue<Map<Object, Object>> messageQueueToFlush;
	private final static int BATCH_INSERT_COUNT = 512;
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	
	private DataSourcePool sourcePool;
	private DataSourcePool targetPool;
	
	private DataSource target ;
	private DataSource source ;
	private String sqlInsert ="";
	
	private final int[] PARAM_TYPES = new int[] {
			Types.INTEGER,
			Types.INTEGER
	};
	
	@Override
	public String getName() {
		return "ScheduleInfoOnLineService";
	}
	
	public ScheduleInfoOnLineService() {
		this.last_timestamp = NumberUtils.longValue(PropertiesHolder.getProperty("online.interval.time"));
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
		}
		try {
			// 1:创建总表与临时表
			String create_all_sche_log_all_store = super.getJsonConfigValue(configJsonPath, "create_all_sche_log_all_store");
			if (!StringUtil.isNullOrEmpty(create_all_sche_log_all_store)) {
				SqlUtils.sqlExecute(target, create_all_sche_log_all_store, this.getName());
			}
			
			String create_tmp_sche_log_all_store = super.getJsonConfigValue(configJsonPath, "create_tmp_sche_log_all_store");
			if (!StringUtil.isNullOrEmpty(create_tmp_sche_log_all_store)) {
				SqlUtils.sqlExecute(target, create_tmp_sche_log_all_store, this.getName());
			}

			// 2:拉取数据
			List<Map> resList = new ArrayList<Map>();
			this.sqlInsert = super.getJsonConfigValue(configJsonPath, "insert_all_sche_log_all_store");
			if (!StringUtil.isNullOrEmpty(this.sqlInsert)) {
				long last_time = 0l;
				Long cacheTime = NumberUtils.longValue(super.findCach(ScheduleInfoOnLineService.class.getCanonicalName()));
				if (cacheTime > 0) {
					last_time = cacheTime;
				}
				int limit = 0;
				for (;;) {
					String selectSource = super.getJsonConfigValue(configJsonPath, "selectSource");
					if (!StringUtil.isNullOrEmpty(selectSource)) {
						selectSource = selectSource.replace("zuobiao_log.schedule_log_yyyy_mm", "zuobiao_log.schedule_log_" + DateUtil.getSysStrCurrentDate("yyyy_MM"));
						selectSource = selectSource.replace("${timestamp}", String.valueOf(last_time));
						selectSource = selectSource.replace("${offset}", String.valueOf(limit));
						selectSource = selectSource.replace("${rows}", String.valueOf(DEFAULT_LIMIT_MAX_SIZE));
						List<Map> rslist = SqlUtils.querySqlList(source, selectSource, this.getName());
						super.saveCach(ScheduleInfoOnLineService.class.getCanonicalName(), DateUtil.getSysCurrentTimestamp().getTime());
						if (null == rslist || rslist.isEmpty()) {
							logger.debug("select sql result is Empty;" + ScheduleInfoOnLineService.class.getCanonicalName() + "; Sql:" + selectSource);
							break;
						} else {
							limit = limit + DEFAULT_LIMIT_MAX_SIZE;
							resList.addAll(rslist);
						}
					}
				}
			}
			
			// 过滤数据
			List<Map> filterList = resList.stream().filter(a -> {
				String action = String.valueOf(a.get("action")).toLowerCase();
				if (!StringUtil.isNullOrEmpty(action)) {
					if (action.equals(ScheduleActionType.ADD.getDesc())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}).collect(Collectors.toList());
			
			if (filterList.isEmpty()) {
				logger.info("[" + this.getClass().getCanonicalName() + "] ： data is null");
				super.callback(true, null, scriptBean, callback);
				return;
			}
			// 插入all表
			this.flush(filterList);

			// 数据转到tmp表
			String insert_tmp = super.getJsonConfigValue(configJsonPath, "insert_tmp");
			if (!StringUtil.isNullOrEmpty(insert_tmp)) {
				SqlUtils.sqlExecute(target, insert_tmp, this.getName());
			}
			// truncate all表
			String truncate_all_sche = super.getJsonConfigValue(configJsonPath, "truncate_all_sche");
			if (!StringUtil.isNullOrEmpty(truncate_all_sche)) {
				SqlUtils.sqlExecute(target, truncate_all_sche, this.getName());
			}
			// sum到tmp表到all表
			String insert_all_sche_sum = super.getJsonConfigValue(configJsonPath, "insert_all_sche_sum");
			if (!StringUtil.isNullOrEmpty(insert_all_sche_sum)) {
				SqlUtils.sqlExecute(target, insert_all_sche_sum, this.getName());
			}
			// 删除tmp表
			String drop_tmp = super.getJsonConfigValue(configJsonPath, "drop_tmp");
			if (!StringUtil.isNullOrEmpty(drop_tmp)) {
				SqlUtils.sqlExecute(target, drop_tmp, this.getName());
			}

			super.callback(true, null, scriptBean, callback);
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(source) + ",message: " + ex.getMessage(), scriptBean, callback);
		}
	}
	
	private void flush(List<Map> filterList) {
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
		Connection connection = null;
		boolean autoCommit0 = false;
		try {
			connection = target.getConnection();
			autoCommit0 = connection.getAutoCommit();
			Statement pstmt = connection.createStatement();
			int count = 0;
			StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
			for (Map log : filterList) {
				Object[] params = new Object[] {
						NumberUtils.intValue(log.get("master")),
						1
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
					String singlSqlInsert = this.sqlInsert;

					// try again in non-batch mode
					PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
					for (Map<Object, Object> log : filterList) {
						try {
							pstmt.setInt(1, NumberUtils.intValue(log.get("master")));
							pstmt.setInt(2, 1);
							
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

	@Override
	public void stop() {
		
	}
	
	@Override
	public void schedule() {
		
	}
}

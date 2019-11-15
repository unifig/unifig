package etl.dispatch.gather.tables.registinfo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
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
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.gather.tables.logininfo.LoginInfo;
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
 * 
 * @Description: 注册日志
 * @author: ylc
 */
@Service
public class RegistInfo extends AbstractScript implements ScheduledService{
	private static Logger logger = LoggerFactory.getLogger(LoginInfo.class);
	private static final String configJsonPath = "classpath*:conf/json/regist_info.json";
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
	private String sqlInsert ="";
	private String sqlInsertWithoutValues;

	@Override
	public String getName() {
		return "RegistInfo";
	}
	
	public RegistInfo() {
		messageQueueToFlush = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
	}
	
	private final int[] PARAM_TYPES = new int[] {
			Types.BIGINT,
			Types.VARCHAR,
			Types.BIGINT,
			Types.INTEGER,
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
			optime_yesday = ScriptTimeUtil.optime_yesday();
			Date yesDate = DateUtil.formatStrToDate(optime_yesday, "yyyyMMdd");
			
			// 创建目标表
			String create_regist_info_ds_log = super.getJsonConfigValue(configJsonPath, "create_regist_info_ds_log");
			if (!StringUtil.isNullOrEmpty(create_regist_info_ds_log)) {
				SqlUtils.sqlExecute(target, create_regist_info_ds_log, this.getName());
			}
			
			// 删除昨日数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("${statisDate}", optime_yesday);
				SqlUtils.sqlExecute(target, delete_yes_date, this.getName());
			}
			
			// 2:拉取数据
			this.sqlInsert = super.getJsonConfigValue(configJsonPath, "insert_regist_info_ds_log");
			if(!StringUtil.isNullOrEmpty(this.sqlInsert)){
				int limit = 0;
				for (;;) {
					String selectSource = super.getJsonConfigValue(configJsonPath, "select_source_sql");
					if (!StringUtil.isNullOrEmpty(selectSource)) {
						selectSource = selectSource.replace("zuobiao_log.register_log_yyyy_mm", "zuobiao_log.register_log_" + DateUtil.getSysStrCurrentDate("yyyy_MM" , -1));
						selectSource = selectSource.replace("${startTime}", String.valueOf(yesDate.getTime()));
						selectSource = selectSource.replace("${endTime}", String.valueOf(yesDate.getTime() + oneDay));
						selectSource = selectSource.replace("${offset}", String.valueOf(limit));
						selectSource = selectSource.replace("${rows}", String.valueOf(DEFAULT_LIMIT_MAX_SIZE));
						List<Map> rslist = SqlUtils.querySqlList(source, selectSource, this.getName());
						if (null == rslist || rslist.isEmpty()) {
							isDbEmpty.compareAndSet(false, true);
							logger.info("select sql result is Empty;" + this.getClass().getCanonicalName() + "; Sql:" + selectSource);
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
			
			for (;;) {
				//数据库数据Empty;且队列已Empty
				if (isDbEmpty.get() && messageQueueToFlush.isEmpty()) {
					// 完成回调
					super.callback(true, null, scriptBean, callback);
					return;
				}
				Thread.currentThread().sleep(10 * 1000);
			}
			// 完成回调
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(source) + ",message: " + ex.getMessage(), scriptBean, callback);
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
		List< Map<Object, Object>> messagesToFlush = new ArrayList<Map<Object,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<String, List<Map<Object, Object>>> shardingFlushEntriesMap = messagesToFlush.stream().collect(Collectors.groupingBy(a->{
			return String.valueOf(a.get("app_device_type"));
		}));

		for(String key : shardingFlushEntriesMap.keySet()) {	
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
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = target.getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<Object,Object> log : shardingFlushEntries) {
					Long creatTime = NumberUtils.longValue(log.get("timestamp"));
					Date registTime = DateUtil.format2Date(creatTime);
					Object[] params = new Object[] { 
							NumberUtils.longValue(log.get("user_id"),-9), 
							super.StringValue(log.get("master")), 
							NumberUtils.longValue(log.get("timestamp"),-9), 
							NumberUtils.intValue(log.get("channel"),-9), 
							super.StringValue(log.get("app_version")), 
							super.StringValue(log.get("os_version")), 
							super.StringValue(log.get("device_vendor")), 
							super.StringValue(log.get("action")), 
							super.StringValue(log.get("device_model")), 
							super.StringValue(log.get("app_device_type")), 
							super.StringValue(log.get("os_name")), 
							
							NumberUtils.intValue(DateUtil.getYear(registTime)), 
							DateUtil.getIntMonth(registTime), 
							DateUtil.getIntDay(registTime), 
							NumberUtils.intValue(DateUtil.getHour(registTime),-9), 
							NumberUtils.intValue(DateUtil.formatDate(registTime, "yyyyMMdd"),-9) 
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
						for (Map<Object, Object> log : shardingFlushEntries) {
							try {
								Long creatTime = NumberUtils.longValue(log.get("timestamp"));
								Date registTime = DateUtil.format2Date(creatTime);

								pstmt.setLong(1,NumberUtils.longValue(log.get("user_id"),-9)); 
								pstmt.setString(2,super.StringValue(log.get("master"))); 
								pstmt.setLong(3,NumberUtils.longValue(log.get("timestamp"),-9)); 
								pstmt.setInt(4, NumberUtils.intValue(log.get("channel"),-9)); 
								pstmt.setString(5,super.StringValue(log.get("app_version"))); 
								pstmt.setString(6,super.StringValue(log.get("os_version"))); 
								pstmt.setString(7,super.StringValue(log.get("device_vendor")));
								pstmt.setString(8,super.StringValue(log.get("action")));
								pstmt.setString(9,super.StringValue(log.get("device_model")));
								pstmt.setString(10,super.StringValue(log.get("app_device_type"))); 
								pstmt.setString(11,super.StringValue(log.get("os_name")));
								pstmt.setInt(12, NumberUtils.intValue(DateUtil.getYear(registTime),-9));
								pstmt.setInt(13, DateUtil.getIntMonth(registTime));
								pstmt.setInt(14, DateUtil.getIntDay(registTime));
								pstmt.setInt(15, NumberUtils.intValue(DateUtil.getHour(registTime),-9));
								pstmt.setInt(16, NumberUtils.intValue(DateUtil.formatDate(registTime, "yyyyMMdd"),-9));

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
	
	@Override
	public void schedule() {
		this.flush();
	}
}

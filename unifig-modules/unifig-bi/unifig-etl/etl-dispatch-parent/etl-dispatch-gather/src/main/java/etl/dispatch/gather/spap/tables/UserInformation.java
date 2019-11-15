package etl.dispatch.gather.spap.tables;

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
 * 用户信息拉取
 * @author liu
 *
 */

@Service
public class UserInformation extends AbstractScript implements ScheduledService{

	private static Logger logger = LoggerFactory.getLogger(UserInformation.class);
	private static final String configJsonPath = "classpath*:conf/spapjson/user_info.json";
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
	
	public UserInformation() {
		messageQueueToFlush = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
	}
	
	@Override
	public String getName() {
		return "Userinfo";
	}
	
	private final int[] PARAM_TYPES = new int[] { 
			Types.BIGINT,
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
	};
	
	@Override
	public void stop() {
		
	}

	@Override
	public void schedule() {
		this.flush();
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
			optime_yesday = ScriptTimeUtil.optime_yesday();
			
			// 创建目标表
			String create_user_info_ds_log = super.getJsonConfigValue(configJsonPath, "create_user_info_ds_log");
			if (!StringUtil.isNullOrEmpty(create_user_info_ds_log)) {
				create_user_info_ds_log = create_user_info_ds_log.replace("user_info_yyyymmdd", "user_info_" + optime_yesday);
				SqlUtils.sqlExecute(target, create_user_info_ds_log, this.getName());
			}
			
			// 删除昨日数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("bi_interface_spap.user_info_yyyymmdd", "bi_interface_spap.user_info_" + optime_yesday);
				SqlUtils.sqlExecute(target, delete_yes_date, this.getName());
			}
			
			// 2:拉取数据
			this.sqlInsert = super.getJsonConfigValue(configJsonPath, "insert_user_info_log");
			if(!StringUtil.isNullOrEmpty(this.sqlInsert)){
				int limit = 0;
				for (;;) {
					String selectSource = super.getJsonConfigValue(configJsonPath, "select_source_sql");
					if (!StringUtil.isNullOrEmpty(selectSource)) {
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
			// 删除ods历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			if (!StringUtil.isNullOrEmpty(delete_table) && saveDays.isPresent()) {
				delete_table = delete_table.replace("bi_interface_spap.user_info_yyyymmdd", "bi_interface_spap.user_info_" + DateUtil.getSysStrCurrentDate("yyyyMMdd", -saveDays.get()));
				SqlUtils.sqlExecute(target, delete_table, this.getName());
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
			// 完成回调
		} catch (IOException ex) {
			super.callback(false, "config json change JsonParser fail , error:" + ex.getMessage(), scriptBean, callback);
		} catch (SQLException ex) {
			System.out.println();
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(source) + ",message: " + ex.getMessage(), scriptBean, callback);
		} catch (Exception e) {
			System.out.println();
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
			return String.valueOf(a.get("type"));
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
			if (!StringUtil.isNullOrEmpty(batchSqlInsert)) {
				batchSqlInsert = batchSqlInsert.replace("user_info_yyyymmdd", "user_info_" + optime_yesday );
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
					Long creatTime = NumberUtils.longValue(log.get("create_time"));
					Date registTime = DateUtil.format2Date(creatTime);
					Object[] params = new Object[] {
							log.get("user_id"),
							log.get("spap_id"),
							log.get("cube"),
							log.get("nickname"),
							log.get("face_src"),
							log.get("large_face_src"),
							log.get("small_face_src"),
							log.get("email"),
							log.get("mobile"),
							log.get("register_ip"),
							log.get("sex"),
							log.get("qr_code"),
							log.get("update_time"),
							log.get("create_time"),
							log.get("nn_time"),
							log.get("profile_birth_time"),
							log.get("email_activation_time"),
							Boolean.parseBoolean(String.valueOf(log.get("guest"))) ? 1 : 0,
							log.get("register_state"),
							log.get("industry_code"),
							log.get("industry_name"),
							log.get("province"),
							log.get("city"),
							log.get("county"),
							Boolean.parseBoolean(String.valueOf(log.get("register_type"))) ? 1 : 0,
									
							NumberUtils.intValue(DateUtil.getYear(registTime)),
							DateUtil.getIntMonth(registTime),
							DateUtil.getIntDay(registTime),
							NumberUtils.intValue(DateUtil.getHour(registTime)),
							NumberUtils.intValue(DateUtil.formatDate(registTime,"yyyyMMdd")),};
					
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
							singlSqlInsert = singlSqlInsert.replace("user_info_yyyymmdd", "user_info_" + optime_yesday );
						}
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<Object, Object> log : shardingFlushEntries) {
							try {
								Long creatTime = NumberUtils.longValue(log.get("create_time"));
								Date registTime = DateUtil.format2Date(creatTime);
								 
								pstmt.setLong(1, NumberUtils.longValue(log.get("user_id")));
								pstmt.setLong(2, NumberUtils.longValue(log.get("spap_id")));
								pstmt.setString(3, String.valueOf(log.get("cube")));
								pstmt.setString(4, String.valueOf(log.get("nickname")));
								pstmt.setString(5, String.valueOf(log.get("face_src")));
								pstmt.setString(6, String.valueOf(log.get("large_face_src")));
								pstmt.setString(7, String.valueOf(log.get("small_face_src")));
								pstmt.setString(8, String.valueOf(log.get("email")));
								pstmt.setString(9, String.valueOf(log.get("mobile")));
								pstmt.setString(10, String.valueOf(log.get("register_ip")));
								pstmt.setInt(11, NumberUtils.intValue(log.get("sex")));
								pstmt.setString(12, String.valueOf(log.get("qr_code")));
								pstmt.setLong(13, NumberUtils.longValue(log.get("update_time")));
								pstmt.setLong(14, NumberUtils.longValue(log.get("create_time")));
								pstmt.setLong(15, NumberUtils.longValue(log.get("nn_time")));
								pstmt.setLong(16, NumberUtils.longValue(log.get("profile_birth_time")));
								pstmt.setLong(17, NumberUtils.longValue(log.get("email_activation_time")));
								pstmt.setInt(18, Boolean.parseBoolean(String.valueOf(log.get("guest"))) ? 1 : 0);
								pstmt.setInt(19, NumberUtils.intValue(log.get("register_state")));
								pstmt.setInt(20, NumberUtils.intValue(log.get("industry_code")));
								pstmt.setString(21, String.valueOf(log.get("industry_name")));
								pstmt.setString(22, String.valueOf(log.get("province")));
								pstmt.setString(23, String.valueOf(log.get("city")));
								pstmt.setString(24, String.valueOf(log.get("county")));
								pstmt.setInt(25, Boolean.parseBoolean(String.valueOf(log.get("register_type"))) ? 1 : 0);
								pstmt.setInt(26, NumberUtils.intValue(DateUtil.getYear(registTime)));
								pstmt.setInt(27, DateUtil.getIntMonth(registTime));
								pstmt.setInt(28, DateUtil.getIntDay(registTime));
								pstmt.setInt(29, NumberUtils.intValue(DateUtil.getHour(registTime)));
								pstmt.setInt(30, NumberUtils.intValue(DateUtil.formatDate(registTime,"yyyyMMdd")));
								
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

}

package etl.dispatch.gather.tables.groupinfo;

import java.io.IOException;
import java.net.UnknownHostException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClientURI;
import com.tools.plugin.utils.NewMapUtil;
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
 * 
 * @Description: 拉取群信息
 * @author: ylc
 */
@Service
public class Engine_Group_Info extends AbstractScript implements ScheduledService{
	private static Logger logger = LoggerFactory.getLogger(Engine_Group_Info.class);
	private static final String configJsonPath = "classpath*:conf/json/engine_group_info.json";
	public final static int DEFAULT_LIMIT_MAX_SIZE = 128;
	public final static int UNKNOWN_VALUE = -9;
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private final static int BATCH_INSERT_COUNT = 512;
	private ArrayBlockingQueue<Map<String,Object>> messageQueueToFlush;
	private AtomicBoolean isDbEmpty = new AtomicBoolean(false);
	
	private MongoDbFactory dbFactory;
	private DataSourcePool targetPool;
	
	private DataSource target = null;
	
	private final int[] PARAM_TYPES = new int[] {
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.INTEGER,
			Types.BIGINT,
			Types.BIGINT,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
	};
	
	private String sqlInsert ="";
	private String optime_yesday;
	private String sqlInsertWithoutValues;
	private Optional<Integer> saveDays = Optional.empty();
	
	public Engine_Group_Info() {
		messageQueueToFlush = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
	}
	
	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		Map<String, Object> paramMap = scriptBean.getParamMap();
		if (null != paramMap && !paramMap.isEmpty()) {
			// 来源数据库
			Map<String, Object> dataSourceMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_SOURCEDATA);
			// 目标数据库
			Map<String, Object> dataTargetMap = (Map<String, Object>) paramMap.get(CommonConstants.PROP_PARAMS_TARGETDATA);
			if ((null != dataSourceMap && !dataSourceMap.isEmpty()) && (null != dataTargetMap && !dataTargetMap.isEmpty())) {
				try {
					if (StringUtil.isNullOrEmpty(dataSourceMap.get("password")) || StringUtil.isNullOrEmpty(dataSourceMap.get("database")) || StringUtil.isNullOrEmpty(dataSourceMap.get("uri")) || StringUtil.isNullOrEmpty(dataSourceMap.get("username"))) {
						super.callback(false, "数据源获取失败; source config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
					}
					dbFactory = this.mongoDbFactory(dataSourceMap.get("database"), dataSourceMap.get("uri"), dataSourceMap.get("username"), dataSourceMap.get("password"));
				} catch (UnknownHostException e) {
					super.callback(false, "数据源获取失败; source config:" + JSON.toJSONString(dataSourceMap), scriptBean, callback);
				}
				targetPool = SpringContextHolder.getBean("dataSourcePool", DataSourcePool.class);
				target = targetPool.getDataSource(dataTargetMap);
				if (null == target) {
					super.callback(false, "数据源获取失败; target config:" + JSON.toJSONString(dataTargetMap), scriptBean, callback);
				} else {
					logger.info(" Server Ip:" + IpUtils.getIPAddress() + "---> [" + this.getClass().getCanonicalName() + "]; dataSource url:" + dataSourceMap.get("url") + " , dataTarget url:" + dataTargetMap.get("url"));
				}
			}

			saveDays = Optional.ofNullable((Integer) paramMap.get(CommonConstants.PROP_PARAMS_SAVEDAYS));
		}
		try {
			optime_yesday = ScriptTimeUtil.optime_yesday();

			// 创建目标表
			String target_sql = super.getJsonConfigValue(configJsonPath, "create_engine_group_info_ds");
			if (!StringUtil.isNullOrEmpty(target_sql)) {
				target_sql = target_sql.replace("engine_group_info_yyyymmdd", "engine_group_info_" + optime_yesday);
				SqlUtils.sqlExecute(target, target_sql, this.getName());
			} else {
				logger.error("path " + configJsonPath + "; create target table, get sql with key'create_engine_group_info_ds' value is null ");
			}

			// 删除昨日数据
			String delete_yes_date = super.getJsonConfigValue(configJsonPath, "delete_yes_date");
			if (!StringUtil.isNullOrEmpty(delete_yes_date)) {
				delete_yes_date = delete_yes_date.replace("bi_interface.engine_group_info_yyyymmdd", "bi_interface.engine_group_info_" + optime_yesday);
				SqlUtils.sqlExecute(target, delete_yes_date, this.getName());
			}

			// 第2步 分页查询记录
			int page = 1;
			this.sqlInsert = super.getJsonConfigValue(configJsonPath, "insert_engine_group_info_log");
			DBCollection collection = dbFactory.getDb("zb_cloud").getCollection("group_info");
			for (;;) {
				List<JSONObject> rslist = this.getLimit(collection, page, DEFAULT_LIMIT_MAX_SIZE);
				if (null == rslist || rslist.isEmpty()) {
					isDbEmpty.compareAndSet(false, true);
					break;
				} else {
					page = page + 1;
					this.offer(rslist);
				}
			}

			// 删除历史数据
			String delete_table = super.getJsonConfigValue(configJsonPath, "delete_table");
			if (!StringUtil.isNullOrEmpty(delete_table) && saveDays.isPresent()) {
				delete_table = delete_table.replace("bi_interface.engine_group_info_yyyymmdd", "bi_interface.engine_group_info_" + DateUtil.getSysStrCurrentDate("yyyyMMdd", -saveDays.get()));
				SqlUtils.sqlExecute(target, delete_table, this.getName());
			} else {
				super.callback(false, "历史数据保留配置异常，saveDays is null;", scriptBean, callback);
			}

			for (;;) {
				// 数据库数据Empty;且队列已Empty
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
			super.callback(false, "fatal error while do java script " + this.getName() + ", DataBase IP :" + super.getUrl(target) + ",message: " + ex.getMessage(), scriptBean, callback);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public MongoDbFactory mongoDbFactory(Object databaseName, Object uri, Object userName, Object password) throws UnknownHostException {
		String uriStr = "mongodb://" + userName + ":" + password + "@" + uri + "/" + databaseName;
		logger.info(uriStr);
		MongoClientURI mongoClientURI = new MongoClientURI(uriStr);
		MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClientURI);
		return mongoDbFactory;
	}
	
	private void offer(List<JSONObject> rslist) {
		for (JSONObject rsObj : rslist) {
			Map<String, Object> rsMap = new NewMapUtil("id",rsObj.getInteger("_id"))
					.set("confirmation", rsObj.getInteger("confirmation"))
					.set("create_time", rsObj.getLong("create_time"))
					.set("cube", rsObj.getString("cube"))
					.set("face_src", rsObj.getString("face_src"))
					.set("founder_id", rsObj.getInteger("founder_id"))
					.set("group_name", rsObj.getString("group_name"))
					.set("large_face_src", rsObj.getString("large_face_src"))
					.set("managers", rsObj.getString("managers"))
					.set("master_id", rsObj.getInteger("master_id"))
					.set("members", rsObj.getString("members"))
					.set("notice", rsObj.getString("notice"))
					.set("qr_src", rsObj.getString("qr_src"))
					.set("small_face_src", rsObj.getString("small_face_src"))
					.set("update_face_state", rsObj.getBoolean("update_face_state"))
					.set("update_time", rsObj.getLong("update_time")).get();
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
		List< Map<String, Object>> messagesToFlush = new ArrayList<Map<String,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<String, List<Map<String, Object>>> shardingFlushEntriesMap = messagesToFlush.stream().collect(Collectors.groupingBy(a->{
			return String.valueOf(a.get("update_face_state"));
		}));

		for(String key : shardingFlushEntriesMap.keySet()) {	
			List<Map<String, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(key);
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
				batchSqlInsert = batchSqlInsert.replace("engine_group_info_yyyymmdd", "engine_group_info_" + optime_yesday );
			}
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = target.getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<String,Object> log : shardingFlushEntries) {
					Long creatTime = NumberUtils.longValue(log.get("create_time"));
					Date registTime = DateUtil.format2Date(creatTime);
					Object[] params = new Object[] { 
							NumberUtils.intValue(log.get("id")),
							NumberUtils.intValue(log.get("founder_id")),
							NumberUtils.intValue(log.get("confirmation")),
							this.gbEncoding(String.valueOf(log.get("group_name"))),
							String.valueOf(log.get("face_src")),
							String.valueOf(log.get("large_face_src")),
							String.valueOf(log.get("small_face_src")),
							String.valueOf(log.get("qr_src")),
							String.valueOf(log.get("cube")),
							NumberUtils.intValue(log.get("master_id")),
							String.valueOf(log.get("managers")),
							this.gbEncoding(String.valueOf(log.get("members"))),
							this.gbEncoding(String.valueOf(log.get("notice"))),
							// true : 1 ;  false : 2
							Boolean.valueOf(String.valueOf(log.get("update_face_state"))) ? 1 :2,
							NumberUtils.longValue(log.get("create_time")),
							NumberUtils.longValue(log.get("update_time")),
							
							NumberUtils.intValue(DateUtil.getYear(registTime),-9), 
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
						if(!StringUtil.isNullOrEmpty(singlSqlInsert)){
							singlSqlInsert = singlSqlInsert.replace("engine_group_info_yyyymmdd", "engine_group_info_" + optime_yesday );
						}
						// try again in non-batch mode
						PreparedStatement pstmt = connection.prepareStatement(singlSqlInsert);
						for (Map<String, Object> log : shardingFlushEntries) {
							try {
								Long creatTime = NumberUtils.longValue(log.get("create_time"));
								Date registTime = DateUtil.format2Date(creatTime);

								pstmt.setInt(1, NumberUtils.intValue(log.get("id")));
								pstmt.setInt(2, NumberUtils.intValue(log.get("founder_id")));
								pstmt.setInt(3, NumberUtils.intValue(log.get("confirmation")));
								pstmt.setString(4, this.gbEncoding(String.valueOf(log.get("group_name"))));
								pstmt.setString(5, String.valueOf(log.get("face_src")));
								pstmt.setString(6, String.valueOf(log.get("large_face_src")));
								pstmt.setString(7, String.valueOf(log.get("small_face_src")));
								pstmt.setString(8, String.valueOf(log.get("qr_src")));
								pstmt.setString(9, String.valueOf(log.get("cube")));
								pstmt.setInt(10, NumberUtils.intValue(log.get("master_id")));
								pstmt.setString(11, String.valueOf(log.get("managers")));
								pstmt.setString(12, this.gbEncoding(String.valueOf(log.get("members"))));
								pstmt.setString(13, this.gbEncoding(String.valueOf(log.get("notice"))));
								// true : 1 ; false : 2
								pstmt.setInt(14, Boolean.valueOf(String.valueOf(log.get("update_face_state"))) ? 1 : 2);
								pstmt.setLong(15, NumberUtils.longValue(log.get("create_time")));
								pstmt.setLong(16, NumberUtils.longValue(log.get("update_time")));

								pstmt.setInt(17, NumberUtils.intValue(DateUtil.getYear(registTime), -9));
								pstmt.setInt(18, DateUtil.getIntMonth(registTime));
								pstmt.setInt(19, DateUtil.getIntDay(registTime));
								pstmt.setInt(20, NumberUtils.intValue(DateUtil.getHour(registTime), -9));
								pstmt.setInt(21, NumberUtils.intValue(DateUtil.formatDate(registTime, "yyyyMMdd"), -9));

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

	/**
	 * mongodb分页
	 * @author: ylc
	 */
	@SuppressWarnings("all")
	public List<JSONObject> getLimit(DBCollection collection,int page, int pageSize) {
		DBCursor limit = collection.find().skip((page - 1) * pageSize).sort(new BasicDBObject()).limit(pageSize);
		List resList = new ArrayList<>();
		int size = limit.size();
		for (DBObject dbObject : limit) {
			JSONObject entity = JSON.parseObject(String.valueOf(dbObject));
			resList.add(entity);
		}
		return resList == null ? new ArrayList<>() : resList;
	}


	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void schedule() {
		this.flush();
	}
	@Override
	public void stop() {
		
	}
	
	/**
	 * 
	 * @author: 中文转unicode编码
	 */
	public String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
}

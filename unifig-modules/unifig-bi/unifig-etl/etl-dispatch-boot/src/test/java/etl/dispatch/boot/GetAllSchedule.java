package etl.dispatch.boot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.script.util.SqlUtils;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.StringUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetAllSchedule {
	private static final String DEF_TABLE_NAME = "schedule_yyyymm";
	private static final String DATABASE_NAME = "bi_online";
	@Autowired
	private MongoTemplate mongoTemplate;

	private DB db;
	
	public final static int DEFAULT_LIMIT_MAX_SIZE = 2048;
	public final static int UNKNOWN_VALUE = -9;
	private final static int DEFAULT_QUEUE_CAPACITY = 1024;
	private final static int BATCH_INSERT_COUNT = 512;
	private ArrayBlockingQueue<Map<Object,Object>> messageQueueToFlush;
	
	@Autowired
	private DimDataSource dimDataSource;
	
	private final int[] PARAM_TYPES = new int[] {
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.VARCHAR,
			Types.VARCHAR,
			Types.BIGINT,			
			Types.BIGINT,			
			Types.BIGINT,			
			Types.BIGINT,			
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
			Types.INTEGER,
	};
	
	private String sqlInsert ="insert into  bi_online.schedule_yyyymm (  `master_id` ,  `sche_id` ,  `status` ,`app_device_type`,`app_version`,  `create_timestamp` ,  `start_timestamp` ,  `update_timestamp` ,  `end_timestamp`, `year` ,  `month` , `day` ,  `hour` ,`statis_date` ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String sqlCreate = "CREATE TABLE IF NOT EXISTS bi_online.schedule_yyyymm (  `master_id` int(10) NOT NULL COMMENT '用户id',  `sche_id` int(10) DEFAULT NULL COMMENT '日程id',  `status` int(2) DEFAULT NULL COMMENT '状态（0未完成，1已完成）', `app_device_type` varchar(255) DEFAULT NULL COMMENT '设备', `app_version` varchar(255) DEFAULT NULL COMMENT '版本', `create_timestamp` bigint(20) DEFAULT NULL COMMENT '创建时间',  `start_timestamp` bigint(20) DEFAULT NULL COMMENT '开始时间',  `update_timestamp` bigint(20) DEFAULT NULL COMMENT '修改时间',  `end_timestamp` bigint(20) DEFAULT NULL COMMENT '结束时间',`year` int(4) DEFAULT NULL,  `month` int(4) DEFAULT NULL, `day` int(4) DEFAULT NULL,  `hour` int(4) DEFAULT NULL,`statis_date` int(10) DEFAULT NULL,KEY `idx_schedule_yyyymm` (`statis_date`,`master_id`) USING BTREE) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	private String sqlInsertWithoutValues;

	
	@Before
	public void init(){
		messageQueueToFlush = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
	}

	@Test
	public void getSchedule() {
		int page = 1;
		int pageSize = 128;
		db = mongoTemplate.getDb().getMongo().getDB("zb_cloud");
		DBCollection collection = db.getCollection("schedule");
		for (;;) {
			boolean b = limit(collection, page, pageSize);
			if(messageQueueToFlush.size() > 512){
				this.flush();
			}
			page++;
			pageSize = pageSize + 128;
			if(!b){
				break;
			}
		}
		this.flush();
	}
	
	public boolean limit(DBCollection collection,int page, int pageSize) {
		boolean flag = false;
		DBCursor limit = collection.find().skip((page - 1) * 10).sort(new BasicDBObject()).limit(pageSize);
		if(limit.hasNext()){
			flag = true;
		}
		for (DBObject dbObject : limit) {
			Map<Object, Object> entity = new HashMap<Object, Object>();
			Set<String> keySet = dbObject.keySet();
			for (String key : keySet) {
				entity.put(key, String.valueOf(dbObject.get(key)));
				if(key.equals("update_timestamp")){
					long timestamp = NumberUtils.longValue(dbObject.get(key));
					Date time = DateUtil.format2Date(timestamp);
					entity.put("year",NumberUtils.intValue(DateUtil.getYear(time),-9)); 
					entity.put("month",DateUtil.getIntMonth(time));
					entity.put("day",DateUtil.getIntDay(time));
					entity.put("hour",NumberUtils.intValue(DateUtil.getHour(time),-9)); 
					entity.put("statis_date",NumberUtils.intValue(DateUtil.formatDate(time, "yyyyMMdd"),-9));
				}
				entity.put("app_device_type",-9);
				entity.put("app_version",-9);
			}
			messageQueueToFlush.offer(entity);
		}
		return flag;
	}

	private void flush() {
		if (this.messageQueueToFlush.size() == 0) {
			return;
		}
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
		}
		List<Map<Object,Object>> messagesToFlush = new ArrayList<Map<Object,Object>>();
		this.messageQueueToFlush.drainTo(messagesToFlush);
		
		// 将entriesToFlush按shardingKey分组flush，防止某一组数据库down机时，隔离其影响（单组数据库down机只影响局部数据）
		Map<Integer, List<Map<Object, Object>>> shardingFlushEntriesMap = messagesToFlush.stream().collect(Collectors.groupingBy(a->{
			return NumberUtils.intValue(String.valueOf(a.get("statis_date")).substring(0,String.valueOf(a.get("statis_date")).length()-2));
		}));
				
		for(Integer key : shardingFlushEntriesMap.keySet()) {	
			try {
				this.prepare(String.valueOf(key));
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			String batchSqlInsert = sqlInsertWithoutValues;
			if (!StringUtil.isNullOrEmpty(batchSqlInsert)) {
				batchSqlInsert = batchSqlInsert.replace("schedule_yyyymm", "schedule_" + key);
			}
			List<Map<Object, Object>> shardingFlushEntries = shardingFlushEntriesMap.get(key);
			Connection connection = null;
			boolean autoCommit0 = false;
			try {
				connection = dimDataSource.clusterDataSource().getConnection();
				autoCommit0 = connection.getAutoCommit();
				Statement pstmt = connection.createStatement();
				int count = 0;
				StringBuilder sqlBuilder = new StringBuilder(batchSqlInsert);
				for(Map<Object,Object> log : shardingFlushEntries) {
					Object[] params = new Object[] {
							NumberUtils.intValue(log.get("master_id")),
							NumberUtils.intValue(log.get("sche_id")),
							NumberUtils.intValue(log.get("status")),
							String.valueOf(log.get("app_device_type")),
							String.valueOf(log.get("app_version")),
							NumberUtils.longValue(log.get("create_timestamp")),
							NumberUtils.longValue(log.get("start_timestamp")),
							NumberUtils.longValue(log.get("update_timestamp")),
							NumberUtils.longValue(log.get("end_timestamp")),
							NumberUtils.intValue(log.get("year")),
							NumberUtils.intValue(log.get("month")),
							NumberUtils.intValue(log.get("day")),
							NumberUtils.intValue(log.get("hour")),
							NumberUtils.intValue(log.get("statis_date")),
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
				
			} finally {
				try {
					if (null != connection) {
						connection.close();
					}
				} catch (SQLException e) {
				}
			}
		}
	}

	private void prepare(String key) throws ClassNotFoundException, SQLException {
		boolean falg = false;
		String defTableName = DEF_TABLE_NAME;
		String tableName = defTableName.replace(defTableName, "schedule_" + key);
		falg = existTabSet(DATABASE_NAME, tableName, null);
		if(!falg){
			this.createTable(key);
		}
	}
	
	private void createTable(String key) throws ClassNotFoundException, SQLException {
		Connection connection = dimDataSource.clusterDataSource().getConnection();
		String defTableName = DEF_TABLE_NAME;
		String createSql = sqlCreate;
		createSql = createSql.replace(defTableName, "schedule_" + key);
		connection.createStatement().execute(createSql);
		connection.close();
	}
	
	/**
	 * 判断表是否存在
	 */
	public boolean existTabSet(String schema, String tableName, String name) throws SQLException, ClassNotFoundException {
		Connection connection = dimDataSource.clusterDataSource().getConnection();
		ResultSet rs = connection.getMetaData().getTables(schema, schema, tableName, null);
		if (rs.next()) {
			connection.close();
			return true;
		} else {
			connection.close();
			return false;
		}
	}
	


}

package etl.dispatch.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.redis.RedisHolder;
import com.tools.plugin.utils.MurmurHash;
import com.tools.plugin.utils.system.IpUtils;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.script.util.Combine;
import etl.dispatch.script.util.ScriptTimeUtil;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.NumberUtils;
import etl.dispatch.util.OsUtils;
import etl.dispatch.util.StringUtil;
import redis.clients.jedis.BinaryJedisCluster;

public abstract class AbstractScript implements IScriptService {
	private static Logger logger = LoggerFactory.getLogger(AbstractScript.class);
	private static Map<String, Map<String, String>> jsonMap = new HashMap<>();
	private Map<String, Object> contextMap;
	public DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
	@Value("${st.data.start.ds.date}")
	private String dsDate;
	private static BinaryJedisCluster jedisCluster;
	static {
		boolean develop = PropertiesHolder.getBooleanProperty("webapp.service.develop");
		String redisCluster = null;
		String requirePass  = null;
		//非开发环境 且 操作系统非 Windows，使用正式库配置
		if(!develop && OsUtils.isShellModel()){
			redisCluster=PropertiesHolder.getProperty("plugin.redis.pro.address");
			requirePass =PropertiesHolder.getProperty("plugin.redis.pro.password");
		}else{
			redisCluster=PropertiesHolder.getProperty("plugin.redis.dev.address");
			requirePass =PropertiesHolder.getProperty("plugin.redis.dev.password");
		}
		jedisCluster = RedisHolder.getJedisCluster(redisCluster, requirePass);
	}
	
	@Override
	public void init(Map<String, Object> tasksMap) {
		this.contextMap = tasksMap;
	}

	@Override
	public void start(long startTimes, String groupId, ScriptCallBack callback) {
		String tasksId  = String.valueOf(contextMap.get(CommonConstants.PROP_TASKS_TASKID));
		String serviceIp= IpUtils.getIPAddress();
		if (StringUtil.isNullOrEmpty(tasksId)) {
			logger.error("The dispatch script failed to execute, tasksId parameters are empty!");
			return;
		}
		String taskName = String.valueOf(contextMap.get(CommonConstants.PROP_TASKS_TASKNAME));
		String scriptId = String.valueOf(contextMap.get(CommonConstants.PROP_SCRIPT_SCRIPTID));
		String scriptName = String.valueOf(contextMap.get(CommonConstants.PROP_SCRIPT_SCRIPTNAME));
		if (StringUtil.isNullOrEmpty(scriptId)) {
			String message = "java  Script scriptId：" + scriptId + ",scriptName:" + scriptName + " is error; The dispatch script failed to execute, scriptId parameters are empty!";
			logger.error(message);
			this.callback(false, message, new ScriptBean(groupId, tasksId, taskName, scriptId, scriptName, null, null, null, startTimes, serviceIp), callback);
		}
		String scriptPath = String.valueOf(contextMap.get(CommonConstants.PROP_SCRIPT_SCRIPTPATH));
		if (StringUtil.isNullOrEmpty(scriptPath)) {
			String message = "java  Script scriptId：" + scriptId + ",scriptName:" + scriptName + " is error; The dispatch script failed to execute, scriptPath parameters are empty!";
			logger.error(message);
			this.callback(false, message, new ScriptBean(groupId, tasksId, taskName, scriptId, scriptName, scriptPath, null, null, startTimes, serviceIp), callback);
		}
		String presetParam = String.valueOf(contextMap.get(CommonConstants.PROP_SCRIPT_PRESETPARAM));
		if (StringUtil.isNullOrEmpty(presetParam)) {
			String message = "java  Script scriptId：" + scriptId + ",scriptName:" + scriptName + " is error; preset_param  is null";
			logger.error(message);
			this.callback(false, message, new ScriptBean(groupId, tasksId, taskName, scriptId, scriptName, scriptPath, null, null, startTimes, serviceIp), callback);
		}
		Map<String, Object> sourceMap = (Map<String, Object>) JSON.parse(presetParam);
		if (null == sourceMap || sourceMap.isEmpty()) {
			String message = "java  Script scriptId：" + scriptId + ",scriptName:" + scriptName + " is error; preset_param parsing failed";
			logger.error(message);
			this.callback(false, message, new ScriptBean(groupId, tasksId, taskName, scriptId, scriptName, scriptPath, sourceMap, null, startTimes, serviceIp), callback);
		}
		//参数密文解密
		Map<String, Object> paramMap = this.paramMapDecrypt(sourceMap, new HashMap<String, Object>());
		String personal = String.valueOf(contextMap.get(CommonConstants.PROP_SCRIPT_PERSONAL));
		if (StringUtil.isNullOrEmpty(personal)) {
			logger.error("The dispatch script failed to execute, personal parameters are empty!");
			return;
		}
		String takeEval = String.valueOf(contextMap.get(CommonConstants.PROP_TASKS_TAKEEVAL));
		String alarmNotice = String.valueOf(contextMap.get(CommonConstants.PROP_TASKS_ALARMNOTICE));
		//输出日志
		String allRelyTasksId = String.valueOf(contextMap.get(CommonConstants.PROP_TASKS_AllRELYTASKID));
		logger.info("the task dependence condition, all rely tasksId:"+ allRelyTasksId+"; groupId:"+groupId+"; tasksId:"+tasksId+"; taskName:"+ taskName+"; scriptPath:"+ scriptPath +"; execute times:"+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
		if (StringUtil.isNullOrEmpty(takeEval) || StringUtil.isNullOrEmpty(alarmNotice)) {
			this.start(new ScriptBean(groupId, tasksId, taskName, scriptId, scriptName, scriptPath, paramMap, personal, startTimes, serviceIp), callback);
		} else {
			this.start(new ScriptBean(groupId, tasksId, taskName, scriptId, scriptName, scriptPath, paramMap, personal, startTimes, serviceIp, NumberUtils.intValue(takeEval), alarmNotice), callback);
		}
	}
	
	private Map<String, Object> paramMapDecrypt(Map<String, Object> sourceMap, Map<String, Object> paramMap) {
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			String keyStr = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				paramMap.put(keyStr, paramMapDecrypt((Map<String, Object>) value, new HashMap<String, Object>()));
			} else {
				if (keyStr.endsWith(".encrypted")) {
					String newKey = keyStr.substring(0, keyStr.lastIndexOf(".encrypted"));
					String newValue = PropertiesHolder.dencryptProperty(String.valueOf(value));
					paramMap.put(newKey, newValue);
				} else {
					paramMap.put(keyStr, value);
				}
			}
		}
		return paramMap;
	}
	
	protected abstract void start(ScriptBean scriptBean, ScriptCallBack callback);

	/**
	 * 脚本执行结束回调
	 * @param isSuccess
	 * @param errorMsg
	 * @param scriptBean
	 * @param callback
	 */
	public void callback(boolean isSuccess, String errorMsg, ScriptBean scriptBean, ScriptCallBack callback) {
		scriptBean.setEndTimes(System.currentTimeMillis());
		callback.setSign(isSuccess, errorMsg, scriptBean);
	}

	public String getJsonConfigValue(String jsonPath, String configKey, boolean isCache) throws IOException {
		Resource[] resources = null;
		Map<String, String> jsonData = null;
		if (isCache && null != jsonMap.get(jsonPath)) {
			jsonData = jsonMap.get(jsonPath);
		} else {
			resources = this.getRootResource(jsonPath);
			if (null == resources || resources.length <= 0) {
				return null;
			}
			InputStream is = resources[0].getInputStream();
			String jsonStr = this.streamToString(is);
			if (StringUtil.isNullOrEmpty(jsonStr)) {
				return null;
			}
			jsonData = (Map<String, String>) JSON.parse(jsonStr);
		}
		if (null == jsonData || jsonData.isEmpty()) {
			return null;
		}
		jsonMap.put(jsonPath, jsonData);
		return jsonData.get(configKey);

	}
	/**
	 * 获取Json配置属性
	 * @param jsonPath
	 * @param configKey
	 * @return
	 * @throws IOException 
	 */
	public String getJsonConfigValue(String jsonPath, String configKey) throws IOException {
		return this.getJsonConfigValue(jsonPath, configKey, true);
	}

	/**
	 * 字符流转字符串
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	private String streamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			is.close();
		}
		return sb.toString();
	}
	
	/**
	 * 通配符路径获取资源
	 * @param jsonPath
	 * @return
	 * @throws IOException
	 */
	private Resource[] getRootResource(String jsonPath) throws IOException {
		PathMatchingResourcePatternResolver resolover = new PathMatchingResourcePatternResolver();
		Assert.notNull(jsonPath, jsonPath + " path is null");
		Resource[] locations = resolover.getResources(jsonPath);
		return locations;
	}

	/**
	 * 判断表是否存在
	 * @param dataSource
	 * @param tableName
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public boolean existTabSet(DataSource dataSource, String schema, String tableName, String name) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			if (null != connection) {
				ResultSet rs = connection.getMetaData().getTables(schema, schema, tableName, null);
				if (rs.next()) {
					return true;
				} else {
					return false;
				}
			}
		} finally {
			if (null != connection) {
				connection.close();
			}
		}
		return false;
	}

	/**
	 * Cube条件组合
	 * 
	 * @param criteriaArr
	 * @return
	 */
	public List<String> getCriteriaArr(String[] criteriaArr) {
		List<String> listCriteria = new ArrayList<>();
		Combine.plzh(listCriteria, "", criteriaArr, new ArrayList<Integer>(), criteriaArr.length);
		List<String> listWithRollup = new ArrayList<String>();
		if (null != listCriteria && !listCriteria.isEmpty()) {
			for (String criteria : listCriteria) {
				if (criteria.startsWith(",")) {
					listWithRollup.add(criteria.substring(1));
				}
			}
		}
		return listCriteria;
	}
	
	/**
	 * 取出数组中的最大值
	 * @param arr
	 * @return
	 */
	public int getMax(Set<Integer> set) {
		Integer[] arr = set.toArray(new Integer[] {});
		Integer max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		return max;
	}

	/**
	 * 取出数组中的最小值
	 * @param arr
	 * @return
	 */
	public int getMin(Set<Integer> set) {
		Integer[] arr = set.toArray(new Integer[] {});
		Integer min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min) {
				min = arr[i];
			}
		}
		return min;
	}
	
	/**
	 * 
	 * @Title: getMonth 
	 * @Description: 获得跨月的sql
	 * @return
	 * @return: String
	 */
	public String getMonthSql(String start, String end, String sql) {
		StringBuffer returnSql = new StringBuffer();
		int months = DateUtil.getMonthPeriod(DateTime.parse(getDsDate(start), format).toDate(), DateTime.parse(end, format).toDate());
		for (int i = 0; i < months + 1; i++) {
			String optime_befor_month = ScriptTimeUtil.optime_befor_month(i);
			if (i > 0) {
				returnSql.append(" UNION ALL ");
			}
			String appendSql = sql.replace("_yyyymm", "_" + optime_befor_month);
			returnSql.append(" " + appendSql + " ");
		}
		return returnSql.toString();
	}
	
	/**
	 * 得到最早有数据的一天
	 * 
	 * @param date
	 * @return
	 */
	private String getDsDate(String date) {
		int isStartDate = new Period(DateTime.parse(dsDate, format), DateTime.parse(date, format), PeriodType.days()).getDays();
		if (isStartDate < 0) {
			return dsDate;
		}
		return date;
	}
	
	/**
	 * 
	 * @Title: getDelHistorySql 
	 * @Description: 获取删除历史数据sql
	 * @param delete_table
	 * @param delete_date
	 * @param string
	 * @param integer
	 * @return
	 * @return: String
	 */
	public String getDelHistorySql(String delete_table, String delete_date, String tablePrefix, Integer saveDays) {
		String saveStatis = DateUtil.getSysStrCurrentDate("yyyyMMdd", saveDays * -1);
		String saveMonth = saveStatis.substring(0, saveStatis.length() - 2);
		String firstDayOfMonth = DateUtil.getFirstDayByTime(saveStatis, "yyyyMMdd", "yyyyMMdd");
		StringBuffer sql = new StringBuffer();
		// 有数据的一天
		if(dsDate.equals(getDsDate(saveStatis))){
			return null;
		}
		// 没有分DM的表，删除历史数据sql
		if(StringUtil.isNullOrEmpty(delete_table) && StringUtil.isNullOrEmpty(tablePrefix)){
			delete_date = delete_date.replace("${statisDate}", saveStatis);
			sql.append(delete_date);
			return sql.toString();
		}
		// 有分DM的表删除历史数据
		if (saveStatis.equals(firstDayOfMonth)) {
			delete_table = delete_table.replace(tablePrefix + "yyyymm", tablePrefix + ScriptTimeUtil.optime_befor_month(1));
			sql.append(delete_table);
		} else {
			delete_date = delete_date.replace("${statisDate}", saveStatis);
			delete_date = delete_date.replace(tablePrefix + "yyyymm", tablePrefix + saveMonth);
			sql.append(delete_date);
		}
		return sql.toString();
	}
	
	public String getDelHistorySql(String delete_date, Integer saveDays) {
		return getDelHistorySql(null, delete_date, null, saveDays);
	}
	/**
	 * 
	 * @Title: getUrl 
	 * @Description: 获取连接的url，区分链接
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 * @return: String
	 */
	public String getUrl(DataSource dataSource) {
		String ip = "";
		List<String> ret = new ArrayList<>();
		try {
			String url = dataSource.getConnection().getMetaData().getURL();
			String regex = "(^|[^\\d])(((2[0-4]\\d|25[0-5]|1\\d\\d|\\d\\d?)\\.){3}(2[0-4]\\d|25\\d|1\\d\\d|\\d\\d?))";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(url);
			while (m.find()) {
				ret.add(m.group(2));
			}
			if (!ret.isEmpty()) {
				ip = ret.get(0);
			}
			return ip;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	public Long getHash64(Map<String,Object> resMap, String properties){
		if(null!= resMap && !resMap.isEmpty()){
			return this.getHash64(String.valueOf(resMap.get(properties)));
		}
		return 0l;
	}
	/**
	 * 获取hash6s4
	 * @author: ylc
	 */
	public Long getHash64(String value) {
		if (StringUtil.isNullOrEmpty(value)) {
			return 0l;
		}
		StringBuffer buff = new StringBuffer(value);
		for (int i = 0; i < 20; i++) {
			buff.append(value);
		}
		return MurmurHash.hash64(buff.toString());
	}
	
	/**
	 * redis缓存数据<br/>
	 */
	public void saveCach(String redisKey, Object redisValue) {
		if (null != jedisCluster) {
			try {
				jedisCluster.set(redisKey.getBytes("utf-8"), String.valueOf(redisValue).getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to delete redis value , error message is" + e.getMessage() + " ;Please contact the administrator");
			}
		} else {
			logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
		}
	}
	
	/**
	 * redis缓存数据<br/>
	 */
	public Object findCach(String redisKey) {
		byte[] bytesInfo = null;
		if (null != jedisCluster) {
			try {
				bytesInfo = jedisCluster.get(redisKey.getBytes("utf-8"));
				if (null != bytesInfo && bytesInfo.length > 0) {
					return new String(bytesInfo, "utf-8");
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to delete redis value , error message is" + e.getMessage() + " ;Please contact the administrator");
			}
		} else {
			logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
		}
		return null;
	}
	
	/**
	 * redis缓存数据<br/>
	 */
	public void deleteCach(String redisKey) {
		if (null != jedisCluster) {
			try {
				jedisCluster.del(redisKey.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to delete redis value , error message is" + e.getMessage() + " ;Please contact the administrator");
			}
		} else {
			logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
		}
	}
	
	/**
	 * 填充数据
	 * @author: ylc
	 */
	public String StringValue(Object obj) {
		if(StringUtil.isNullOrEmpty(obj)){
			return "-9";
		}
		return String.valueOf(obj);
	}
}

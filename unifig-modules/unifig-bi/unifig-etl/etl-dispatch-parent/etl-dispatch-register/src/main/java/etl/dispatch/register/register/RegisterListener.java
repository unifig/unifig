package etl.dispatch.register.register;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.redis.RedisHolder;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.util.MD5;
import etl.dispatch.util.OsUtils;
import redis.clients.jedis.BinaryJedisCluster;

public class RegisterListener {
	static Logger logger = LoggerFactory.getLogger(RegisterListener.class);
	private static String webappName = PropertiesHolder.getProperty("webapp.service.name");
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
		logger.info("use redis cluster service >>>"+ redisCluster);
		jedisCluster = RedisHolder.getJedisCluster(redisCluster, requirePass);
	}

	@SuppressWarnings("unchecked")
	public static void register(Map<String, Object> messageMap) throws IOException {
		if (null == messageMap || messageMap.isEmpty()) {
			return;
		}
		byte[] registerByte = null;
		try {
			if (null != jedisCluster) {
				registerByte = jedisCluster.get(MD5.encryptToHex(webappName).getBytes("utf-8"));
			} else {
				logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to get the distributed redis connection , error message is" + e.getMessage() + " ;Please contact the administrator");
		}
		if (null != registerByte && registerByte.length > 0) {
			Map<String, Object> serversMap = (Map<String, Object>) JSON.parse(new String(registerByte, "utf-8"));
			serversMap.put(messageMap.get("serverIp") + ":" + messageMap.get("serverPort"), messageMap);
			jedisCluster.set(MD5.encryptToHex(webappName).getBytes("utf-8"), JSON.toJSONString(serversMap).getBytes("utf-8"));
		} else {
			Map<String, Object> serversMap = new HashMap<String, Object>();
			serversMap.put(messageMap.get("serverIp") + ":" + messageMap.get("serverPort"), messageMap);
			jedisCluster.set(MD5.encryptToHex(webappName).getBytes("utf-8"), JSON.toJSONString(serversMap).getBytes("utf-8"));
		}
	}
}

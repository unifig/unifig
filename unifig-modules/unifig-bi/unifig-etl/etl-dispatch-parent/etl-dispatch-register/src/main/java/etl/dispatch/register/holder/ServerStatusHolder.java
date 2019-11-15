package etl.dispatch.register.holder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.tools.plugin.redis.RedisHolder;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.util.MD5;
import etl.dispatch.util.OsUtils;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.encode.HexUtils;
import redis.clients.jedis.BinaryJedisCluster;

/**
 * 服务器集群状态持有类,提供刷新，查询等功能 </br>
 * 
 *
 */
@Component
public class ServerStatusHolder {
	private static final Logger logger = LoggerFactory.getLogger(ServerStatusHolder.class);
	private static String webappName = PropertiesHolder.getProperty("webapp.service.name");
	private static final long deathTime = 1000 * 60 * 3;
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

	/**
	 * 获取服务器注册状态
	 * 
	 * @return
	 */
	public static Map<String, Object> getClusterServers() throws IOException {
		byte[] registerByte = null;
		if (null != jedisCluster) {
			registerByte = jedisCluster.get(MD5.encryptToHex(webappName).getBytes("utf-8"));
		} else {
			logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
		}
		if (null == registerByte || registerByte.length < 1) {
			return null;
		}
		// 转换字符串为Map对象
		Map<String, Object> serversMap = (Map<String, Object>) JSON.parse(new String(registerByte, "utf-8"));
		Map<String, Object> sersNewMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> serMap : serversMap.entrySet()) {
			if (null == serMap.getValue()) {
				continue;
			}
			Map<String, Object> message = (Map<String, Object>) serMap.getValue();
			String activeTime = String.valueOf(message.get("activeTime"));
			if (StringUtil.isNullOrEmpty(activeTime)) {
				continue;
			}
			// 3分钟不注册即认为服务关闭
			if (new Date().getTime() <= (Long.valueOf(activeTime) + deathTime)) {
				sersNewMap.put(serMap.getKey(), serMap.getValue());
			}
		}
		jedisCluster.set(MD5.encryptToHex(webappName).getBytes("utf-8"), JSON.toJSONString(sersNewMap).getBytes("utf-8"));
		return sersNewMap;
	}
}

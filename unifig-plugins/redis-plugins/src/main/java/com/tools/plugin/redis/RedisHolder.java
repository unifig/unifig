package com.tools.plugin.redis;

import java.util.Map;

import com.tools.plugin.redis.util.HttpManager;
import com.tools.plugin.utils.StringUtil;

import redis.clients.jedis.BinaryJedisCluster;

public class RedisHolder {

	public static BinaryJedisCluster getJedisCluster(String redisCluster, String requirePass) {
		if (StringUtil.isNullOrEmpty(redisCluster)) {
			return null;
		}
		if (StringUtil.isNullOrEmpty(requirePass)) {
			return null;
		}
		Map<String, Object> loginRedisMap = HttpManager.getInstance().login(redisCluster, requirePass);

		if (null == loginRedisMap || loginRedisMap.isEmpty()) {
			return null;
		}
		return (BinaryJedisCluster) loginRedisMap.get("jedisCluster");
	}
	
	
}

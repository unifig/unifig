package com.tools.plugin.redis.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.tools.plugin.utils.NewMapUtil;
import com.tools.plugin.utils.StringUtil;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedisPool;

public class HttpManager {
	private static HttpManager httpManager = new HttpManager();
	private static final int TIME_OUT = 5000;
	private static final int MAX_REDIRECTIONS = 8;
	private static final int MAX_TOTAL = 1024;
	private static final int MAX_IDEL = 200;
	private static final int MAX_WAIT = 10000;

	public static HttpManager getInstance() {
		if (httpManager == null) {
			httpManager = new HttpManager();
		}
		return httpManager;
	}

	@SuppressWarnings("null")
	public Map<String, Object> login(String redisCluster, String requirepass) {
		if (StringUtil.isNullOrEmpty(redisCluster)) {
			return null;
		}
		String[] clusterNodes = redisCluster.split(",");
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();

		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		BinaryJedisCluster jedisCluster = null;

		for (String clusterNode : clusterNodes) {
			String[] ipPort = clusterNode.split(":");

			JedisCluster jedisNode = null;
			try {
				jedisNode = new JedisCluster(new HostAndPort(ipPort[0], Integer.parseInt(ipPort[1])), Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, 1, requirepass, new GenericObjectPoolConfig());
			} catch (Exception ex) {
				//解决分片节点异常影响整个集群
			}
			if (null != jedisNode) {
				jedisClusterNodes.add(new HostAndPort(ipPort[0], Integer.parseInt(ipPort[1])));
				shards.add(new JedisShardInfo(ipPort[0], Integer.parseInt(ipPort[1])));
			}
		}
		GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(MAX_TOTAL);
		poolConfig.setMaxIdle(MAX_IDEL);
		poolConfig.setMaxWaitMillis(MAX_WAIT);
		poolConfig.setTestOnBorrow(false);
		poolConfig.setTestWhileIdle(false);
		if (!StringUtil.isNullOrEmpty(requirepass)) {
			jedisCluster = new BinaryJedisCluster(jedisClusterNodes, TIME_OUT, TIME_OUT, MAX_REDIRECTIONS, requirepass, poolConfig);
		} else {
			jedisCluster = new BinaryJedisCluster(jedisClusterNodes, TIME_OUT, MAX_REDIRECTIONS, poolConfig);
		}
		return new NewMapUtil().set("jedisCluster", jedisCluster).set("redisPool", new ShardedJedisPool(poolConfig, shards)).get();
	}
}

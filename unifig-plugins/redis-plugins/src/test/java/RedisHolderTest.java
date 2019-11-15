import java.io.UnsupportedEncodingException;

import com.tools.plugin.redis.RedisHolder;

import redis.clients.jedis.BinaryJedisCluster;

public class RedisHolderTest {

	public static void main(String[] arges) {
		// 入库redis公有缓存
		BinaryJedisCluster jedisCluster = RedisHolder.getJedisCluster("","");
		if (null != jedisCluster) {
			// session到Redis库
			System.out.println(jedisCluster.get("aaaaa0".getBytes()));
		}

	}
}

package com.lvmama.soa.monitor.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisUtils {
	public static final String DEFAULT_HOST = "10.2.1.80";
	public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	public static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private static final String OK_CODE = "OK";
	private static final String OK_MULTI_CODE = "+OK";

	/**
	 * 快速设置JedisPoolConfig, 不执行idle checking。
	 */
	public static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setTimeBetweenEvictionRunsMillis(-1);
		return poolConfig;
	}

	/**
	 * 快速设置JedisPoolConfig, 设置执行idle checking的间隔和可被清除的idle时间.
	 * 默认的checkingIntervalSecs是30秒，可被清除时间是60秒。
	 */
	public static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal,
			int checkingIntervalSecs, int evictableIdleTimeSecs) {
		JedisPoolConfig poolConfig = createPoolConfig(maxIdle, maxTotal);
		poolConfig.setTimeBetweenEvictionRunsMillis(checkingIntervalSecs * 1000);
		poolConfig.setMinEvictableIdleTimeMillis(evictableIdleTimeSecs * 1000);
		return poolConfig;
	}
	
	public static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal,
			int checkingIntervalSecs, int evictableIdleTimeSecs,int maxWaitMillis) {
		JedisPoolConfig poolConfig = createPoolConfig(maxIdle, maxTotal,
				checkingIntervalSecs,evictableIdleTimeSecs);
		poolConfig.setMaxWaitMillis(maxWaitMillis * 1000);
		
		return poolConfig;
	}
	
	/**
	 * 判断 是 OK 或 +OK.
	 */
	public static boolean isStatusOk(String status) {
		return (status != null)
				&& (OK_CODE.equals(status) || OK_MULTI_CODE.equals(status));
	}

	/**
	 * 退出然后关闭Jedis连接。如果Jedis为null则无动作。
	 */
	public static void closeJedis(Jedis jedis) {
		if ((jedis != null) && jedis.isConnected()) {
			try {
				try {
					jedis.quit();
				} catch (Exception e) {
				}
				jedis.disconnect();
			} catch (Exception e) {
			}
		}
	}
}

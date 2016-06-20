package com.lvmama.soa.monitor.util;

import java.util.List;

public class CacheUtil {
	public static final String NULL_OBJECT = ident("soa_monitor.CACHE.NULL_OBJECT");

	private static String ident(String s) {
		return s;
	}

	// private static final JedisTemplate jedisReaderTemplate =
	// JedisTemplate.getReaderInstance();

	// public static void set(String key,Object value,int seconds){
	// jedisReaderTemplate.set(key, value, seconds);
	// }
	//
	// public static <T> T get(String key,Class<T> clazz){
	// return jedisReaderTemplate.get(key, clazz);
	// }
	//
	// public static void del(final String... keys){
	// jedisReaderTemplate.del(keys);
	// }
	//
	// public static <V> List<V> getArray(final String key, final Class<V>
	// clazz) {
	// return jedisReaderTemplate.getArray(key, clazz);
	// }
	//
	// public static <V> void setArray(final String key,List<V> value, int
	// seconds ){
	// jedisReaderTemplate.setArray(key, value, seconds);
	// }

	public static void set(String key, Object value, int seconds) {
		MemcachedUtil.getInstance().set(key, seconds, value);
	}

	public static <T> T get(String key, Class<T> clazz) {
		return (T)MemcachedUtil.getInstance().get(key);
	}

	public static void del(final String... keys) {
		for(String key:keys){
			MemcachedUtil.getInstance().del(key);			
		}
	}

	public static <V> List<V> getArray(final String key, final Class<V> clazz) {
		return (List<V>)MemcachedUtil.getInstance().get(key);
	}

	public static <V> void setArray(final String key, List<V> value, int seconds) {
		MemcachedUtil.getInstance().set(key, seconds, value);
	}

	public static boolean isNullObject(Object obj) {
		return NULL_OBJECT.equals(obj);
	}

}

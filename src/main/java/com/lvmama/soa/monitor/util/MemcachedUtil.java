package com.lvmama.soa.monitor.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedUtil {
	private final static  Log log = LogFactory.getLog(MemcachedUtil.class);
	private MemCachedClient memCachedClient;
	
	private static MemcachedUtil instance=new MemcachedUtil();
	
	private void init() {
		try {
			//数据缓存服务器，“,”表示配置多个memcached服务
			String[] servers = PropertyUtil.getProperty("cache.server").replaceAll(" ", "").split(",");
			SockIOPool pool = SockIOPool.getInstance("dataServer");
			pool.setServers(servers);
			pool.setFailover(true);
			pool.setInitConn(10);
			pool.setMinConn(5);
			pool.setMaxConn(50);
			pool.setMaintSleep(30);
			pool.setNagle(false);
			pool.setSocketTO(30000);
			pool.setBufferSize(1024*1024*5);
			pool.setAliveCheck(true);
//			pool.setHashingAlg(SockIOPool.CONSISTENT_HASH);
			pool.initialize(); /* 建立MemcachedClient实例 */
			memCachedClient = new MemCachedClient("dataServer");
		}
		catch (Exception ex) {
			log.error("MemcachedUtil.init() error", ex);
		}
	}
	
	private MemcachedUtil(){
		init();
	}
	
	public static MemcachedUtil getInstance() {
		return instance;
	}
    
    public boolean tryLock(String lockKey, int lockSec, int timeOutSec){
    	long start=System.currentTimeMillis();
    	while(true){
    		boolean locked=memCachedClient.add(lockKey, "", getDateAfter(lockSec));
    		if(locked){
    			return true;
    		}else{
    			long now=System.currentTimeMillis();
    			long costed = now-start;
				if(costed>=timeOutSec*1000){
					return false;
				}
    		}
    	}
    }
    
    public boolean releaseLock(String lockKey){
    	return memCachedClient.delete(lockKey);
    }
    
    public static Date getDateAfter(int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.now());
 		cal.add(Calendar.SECOND, second);
 		return cal.getTime();
 	}
    
    public boolean set(String key, int seconds, Object obj) {
		boolean result = memCachedClient.set(key, obj, getDateAfter(seconds));
		return result;
	}
    
    public Object get(String key) {
    	return memCachedClient.get(key);
	}
    
    public boolean del(String key){
    	return memCachedClient.delete(key);
    }
}

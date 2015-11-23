package com.lvmama.soa.monitor.util;

public class DistributedLock {
	private static final int DEFAULT_LOCK_SECCONDS = 5;
	private static final int DEFAULT_TRYLOCK_TIMEOUT_SECONDS = 5;
	
	public static boolean tryLock(String lockKey){
    	return MemcachedUtil.getInstance().tryLock(lockKey,DEFAULT_LOCK_SECCONDS,DEFAULT_TRYLOCK_TIMEOUT_SECONDS);
    }
    
    public static boolean tryLock(String lockKey, int lockSec, int timeOutSec){
    	return MemcachedUtil.getInstance().tryLock(lockKey, lockSec, timeOutSec);
    }
    
    public static boolean releaseLock(String lockKey){
    	return MemcachedUtil.getInstance().releaseLock(lockKey);
    }
}

package com.lvmama.soa.monitor.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.entity.DubboAppMinute;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.util.DateUtil;

@Component("appMinuteCache")
public class AppMinuteCache {
	Log log = LogFactory.getLog(AppMinuteCache.class);
	
	@Autowired
	DubboAppMinuteService dubboProviderAppService;
	
	private static final ReadWriteLock providerLock = new ReentrantReadWriteLock();
//	private static final ReadWriteLock consumerLock = new ReentrantReadWriteLock();
	
	private static final Map<String,AtomicReference<DubboAppMinute>> PROVIDER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboAppMinute>>();
//	private static final Map<String,AtomicReference<DubboProviderApp>> CONSUMER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboProviderApp>>();
	
	public static void updateProviderAppCache(DubboMethodMinuteIP minute) {
		String appName = minute.getAppName();
		Date time = minute.getTime();
		String timeStr = DateUtil.format(time);
		String key = "AppMinuteCache.updateProviderAppCache_"+appName + "_" + timeStr;

		AtomicReference<DubboAppMinute> appReference = null;

		try {
			providerLock.readLock().lock();
			appReference = PROVIDER_CACHE.get(key);
		} finally {
			providerLock.readLock().unlock();
		}
		if (appReference == null) {
			appReference = new AtomicReference<DubboAppMinute>();
			DubboAppMinute app = new DubboAppMinute();
			app.setAppName(appName);
			app.setTime(time);
			app.setElapsedAvg(0L);
			app.setFailTimes(0L);
			app.setSuccessTimes(0L);
			app.setElapsedMax(0L);
			appReference.set(app);
			try {
				providerLock.writeLock().lock();
				if (PROVIDER_CACHE.get(key) == null) {
					PROVIDER_CACHE.put(key, appReference);
				} else {
					appReference = PROVIDER_CACHE.get(key);
				}
			} finally {
				providerLock.writeLock().unlock();
			}
		}

		while (true) {
			DubboAppMinute appOld = appReference.get();
			DubboAppMinute appUpdated = new DubboAppMinute();
			appUpdated.setAppName(appOld.getAppName());
			appUpdated.setTime(appOld.getTime());
			if(minute.getSuccessTimes()!=null&&minute.getSuccessTimes()!=0){
				appUpdated.setElapsedAvg((appOld.getElapsedAvg()
						* appOld.getSuccessTimes() + minute.getElapsedTotal())
						/ (appOld.getSuccessTimes() + minute.getSuccessTimes()));				
			}
			if (minute.getSuccessTimes() != null){
				appUpdated.setSuccessTimes(appOld.getSuccessTimes()
						+ minute.getSuccessTimes());				
			}
			if(minute.getFailTimes()!=null){
				appUpdated.setFailTimes(appOld.getFailTimes()
						+ minute.getFailTimes());				
			}
			
			if(minute.getElapsedMax()!=null){
				appUpdated.setElapsedMax(Math.max(appOld.getElapsedMax(),
						minute.getElapsedMax()));				
			}
			if (appReference.compareAndSet(appOld, appUpdated)) {
				break;
			}
		}
	}
	
//	public static void updateConsumerAppCache(DubboConsumerDetail detail){}
	
	public void writeProviderAppCacheToDB(){
		long start=DateUtil.now().getTime();
		log.info("writeProviderAppCacheToDB START");
		Map<String,AtomicReference<DubboAppMinute>> copyMap=null;
		try{
			providerLock.writeLock().lock();
			copyMap=new HashMap<String,AtomicReference<DubboAppMinute>>(PROVIDER_CACHE);			
			PROVIDER_CACHE.clear();
		}finally{
			providerLock.writeLock().unlock();
		}
		
		for(String key:copyMap.keySet()){
			try{
				AtomicReference<DubboAppMinute> appReference=copyMap.get(key);
				dubboProviderAppService.insertOrAppend(appReference.get());				
			}catch(Exception e){
				log.error("Insert or update Provider App error:"+copyMap.get(key).get(), e);
			}
		}
		log.info("writeProviderAppCacheToDB END cost:"+(DateUtil.now().getTime()-start)+"ms");
	}
}

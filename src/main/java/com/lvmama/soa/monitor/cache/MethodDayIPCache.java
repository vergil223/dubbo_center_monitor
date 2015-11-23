package com.lvmama.soa.monitor.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

@Component("methodDayIPCache")
public class MethodDayIPCache {
	private static final Log log = LogFactory.getLog(MethodDayIPCache.class);
	
	@Autowired
	private DubboMethodDayIPService dubboMethodDayIPService;
	
//	@Autowired
//	DubboMethodMinuteIPService dubboMethodMinuteIPService;
	
	private static final ReadWriteLock providerLock = new ReentrantReadWriteLock();
//	private static final ReadWriteLock consumerLock = new ReentrantReadWriteLock();
	
	private static final Map<String,AtomicReference<DubboMethodDayIP>> PROVIDER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboMethodDayIP>>();
//	private static final Map<String,AtomicReference<DubboMethodDayIP>> CONSUMER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboMethodDayIP>>();
	
	public static void updateProviderCache(DubboMethodMinuteIP minute) {
		String appName = minute.getAppName();
		String service = minute.getService();
		String method = minute.getMethod();
		String consumerIP = minute.getConsumerIP();
		String providerIP = minute.getProviderIP();
		Date time = DateUtil.trimToDay(minute.getTime());
		String key = "MethodIPDayCache.updateProviderCache_"+appName + "_" + service + "_" + method + "_" + consumerIP
				+ "_" + providerIP + "_" + DateUtil.format(time);

		AtomicReference<DubboMethodDayIP> dayReference = null;

		try {
			providerLock.readLock().lock();
			dayReference = PROVIDER_CACHE.get(key);
		} finally {
			providerLock.readLock().unlock();
		}
		if (dayReference == null) {
			dayReference = new AtomicReference<DubboMethodDayIP>();
			DubboMethodDayIP day = new DubboMethodDayIP(appName, service, method,
					consumerIP, providerIP, time);
			day.setElapsedAvg(0L);
			day.setFailTimes(0L);
			day.setSuccessTimes(0L);
			day.setElapsedMax(0L);
			dayReference.set(day);
			try {
				providerLock.writeLock().lock();
				if (PROVIDER_CACHE.get(key) == null) {
					PROVIDER_CACHE.put(key, dayReference);
				} else {
					dayReference = PROVIDER_CACHE.get(key);
				}
			} finally {
				providerLock.writeLock().unlock();
			}
		}

		while (true) {
			DubboMethodDayIP dayOld = dayReference.get();
			DubboMethodDayIP dayUpdated = new DubboMethodDayIP(appName, service, method,
					consumerIP, providerIP, time);

			if (minute.getSuccessTimes() != null
					&& minute.getSuccessTimes() != 0) {
				dayUpdated
						.setElapsedAvg((dayOld.getElapsedAvg()
								* dayOld.getSuccessTimes() + minute
									.getElapsedTotal())
								/ (dayOld.getSuccessTimes() + minute
										.getSuccessTimes()));
			}
			
			if (minute.getSuccessTimes() != null){
				dayUpdated.setSuccessTimes(dayOld.getSuccessTimes()
						+ minute.getSuccessTimes());				
			}
			if (minute.getFailTimes() != null) {
				dayUpdated.setFailTimes(dayOld.getFailTimes()
						+ minute.getFailTimes());
			}

			if (minute.getElapsedMax() != null) {
				dayUpdated.setElapsedMax(Math.max(dayOld.getElapsedMax(),
						minute.getElapsedMax()));
			}
			dayUpdated.setElapsedMaxDetail(DubboDetailUtil.mergeDetailToStr(dayOld.getElapsedMaxDetail(), DateUtil.HHmm(minute.getTime()) + " "
					+ minute.getElapsedMax(), true));
			dayUpdated.setElapsedTotalDetail(DubboDetailUtil.mergeDetailToStr(dayOld.getElapsedTotalDetail(), DateUtil.HHmm(minute.getTime()) + " "
					+ minute.getElapsedTotal(), false));
			dayUpdated.setSuccessTimesDetail(DubboDetailUtil.mergeDetailToStr(dayOld.getSuccessTimesDetail(), DateUtil.HHmm(minute.getTime()) + " "
					+ minute.getSuccessTimes(), false));
			dayUpdated.setFailTimesDetail(DubboDetailUtil.mergeDetailToStr(dayOld.getFailTimesDetail(), DateUtil.HHmm(minute.getTime()) + " "
					+ minute.getFailTimes(), false));

			if (dayReference.compareAndSet(dayOld, dayUpdated)) {
				break;
			}
		}
	}
	
	public void writeMethodDayIPCacheToDB(){
		long start=DateUtil.now().getTime();
		log.info("writeMethodDayIPCacheToDB START");
		Map<String,AtomicReference<DubboMethodDayIP>> copyMap=null;
		try{
			providerLock.writeLock().lock();
			copyMap=new HashMap<String,AtomicReference<DubboMethodDayIP>>(PROVIDER_CACHE);			
			PROVIDER_CACHE.clear();
		}finally{
			providerLock.writeLock().unlock();
		}
		
		ExecutorService exec=Executors.newFixedThreadPool(10);
		
		for(String key:copyMap.keySet()){
			try {
				AtomicReference<DubboMethodDayIP> dayReference = copyMap
						.get(key);
				DubboMethodDayIP day = dayReference.get();
				exec.submit(new writeToDBRunnable(dubboMethodDayIPService, day));
			}catch(Exception e){
				log.error("writeMethodDayIPCacheToDB error:"+copyMap.get(key).get(), e);
			}
		}
		
		exec.shutdown();
		try{
			while(!exec.awaitTermination(300, TimeUnit.SECONDS)){
				log.info("writeMethodDayIPCacheToDB still running, costed:"+(DateUtil.now().getTime()-start)+"ms");
			}			
		}catch(Exception e){
			log.error("error when waiting for threads end.",e);
		}
		
		log.info("writeMethodDayIPCacheToDB END, cost:"+(DateUtil.now().getTime()-start)+"ms");
	}
	
	private class writeToDBRunnable implements Runnable{
		private DubboMethodDayIPService dubboMethodDayIPService;
		private DubboMethodDayIP day;
		public writeToDBRunnable(DubboMethodDayIPService dubboMethodDayIPService,DubboMethodDayIP day){
			this.dubboMethodDayIPService=dubboMethodDayIPService;
			this.day=day;
		}
		@Override
		public void run() {
			dubboMethodDayIPService.insertOrAppend(day);
		}
	}
}

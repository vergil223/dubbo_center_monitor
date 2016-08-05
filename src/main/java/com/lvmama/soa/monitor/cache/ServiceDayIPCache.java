package com.lvmama.soa.monitor.cache;

import java.math.BigDecimal;
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

import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.service.DubboServiceDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

@Component("serviceDayIPCache")
public class ServiceDayIPCache {
	private static final Log log = LogFactory.getLog(ServiceDayIPCache.class);
	
	@Autowired
	private DubboServiceDayIPService dubboServiceDayIPService;
	
	private static final ReadWriteLock providerLock = new ReentrantReadWriteLock();
//	private static final ReadWriteLock consumerLock = new ReentrantReadWriteLock();
	
	private static final Map<String,AtomicReference<DubboServiceDayIP>> PROVIDER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboServiceDayIP>>();
//	private static final Map<String,AtomicReference<DubboServiceDayIP>> CONSUMER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboServiceDayIP>>();
	
	public static void updateProviderCache(DubboMethodMinuteIP minute) {
		String appName = minute.getAppName();
		String service = minute.getService();
		String consumerIP = minute.getConsumerIP();
		String providerIP = minute.getProviderIP();
		Date time = DateUtil.trimToDay(minute.getTime());
		String key = "ServiceDayIPCache.updateProviderCache_"+appName + "_" + service  + "_" + consumerIP
				+ "_" + providerIP + "_" + DateUtil.format(time);

		AtomicReference<DubboServiceDayIP> dayReference = null;

		try {
			providerLock.readLock().lock();
			dayReference = PROVIDER_CACHE.get(key);
		} finally {
			providerLock.readLock().unlock();
		}
		if (dayReference == null) {
			dayReference = new AtomicReference<DubboServiceDayIP>();
			DubboServiceDayIP day = new DubboServiceDayIP(appName, service,
					consumerIP, providerIP, time);
			day.setElapsedAvg(BigDecimal.ZERO);
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
			DubboServiceDayIP dayOld = dayReference.get();
			DubboServiceDayIP dayUpdated = new DubboServiceDayIP(appName, service,
					consumerIP, providerIP, time);

			if (minute.getSuccessTimes() != null
					&& minute.getSuccessTimes() != 0) {
				dayUpdated
						.setElapsedAvg((dayOld.getElapsedAvg().multiply(new BigDecimal(dayOld.getSuccessTimes())).add(minute
								.getElapsedTotal())).divide(new BigDecimal(dayOld.getSuccessTimes() + minute
										.getSuccessTimes()),4,BigDecimal.ROUND_HALF_UP));
				
				dayUpdated.setSuccessTimes(dayOld.getSuccessTimes()
						+ minute.getSuccessTimes());
			}else{
				dayUpdated.setElapsedAvg(dayOld.getElapsedAvg());
			}
			
			
			if (minute.getSuccessTimes() != null){
				dayUpdated.setSuccessTimes(dayOld.getSuccessTimes()
						+ minute.getSuccessTimes());				
			}else{
				dayUpdated.setSuccessTimes(dayOld.getSuccessTimes());
			}
			
			
			if (minute.getFailTimes() != null) {
				dayUpdated.setFailTimes(dayOld.getFailTimes()
						+ minute.getFailTimes());
			}else{
				dayUpdated.setFailTimes(dayOld.getFailTimes());
			}

			if (minute.getElapsedMax() != null) {
				dayUpdated.setElapsedMax(Math.max(dayOld.getElapsedMax(),
						minute.getElapsedMax()));
			}else{
				dayUpdated.setElapsedMax(dayOld.getElapsedMax());
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
	
	public void writeServiceDayIPCacheToDB(){
		long start=DateUtil.now().getTime();
		log.info("writeServiceDayIPCacheToDB START");
		Map<String,AtomicReference<DubboServiceDayIP>> copyMap=null;
		try{
			providerLock.writeLock().lock();
			copyMap=new HashMap<String,AtomicReference<DubboServiceDayIP>>(PROVIDER_CACHE);			
			PROVIDER_CACHE.clear();
		}finally{
			providerLock.writeLock().unlock();
		}
		
		ExecutorService exec=Executors.newFixedThreadPool(10);
		for(String key:copyMap.keySet()){
			try{
				AtomicReference<DubboServiceDayIP> dayReference=copyMap.get(key);
				DubboServiceDayIP day = dayReference.get();
				exec.submit(new writeToDBRunnable(dubboServiceDayIPService,day));
			}catch(Exception e){
				log.error("writeServiceDayIPCacheToDB error:"+copyMap.get(key).get(), e);
			}
		}
		
		exec.shutdown();
		try{
			while(!exec.awaitTermination(300, TimeUnit.SECONDS)){
				log.info("writeServiceDayIPCacheToDB still running, costed:"+(DateUtil.now().getTime()-start)+"ms");
			}			
		}catch(Exception e){
			log.error("error when waiting for threads end.",e);
		}
		
		log.info("writeServiceDayIPCacheToDB END, cost:"+(DateUtil.now().getTime()-start)+"ms");
	}
	
	private class writeToDBRunnable implements Runnable{
		private DubboServiceDayIPService dubboServiceDayIPService;
		private DubboServiceDayIP day;
		public writeToDBRunnable(DubboServiceDayIPService dubboServiceDayIPService,DubboServiceDayIP day){
			this.dubboServiceDayIPService=dubboServiceDayIPService;
			this.day=day;
		}
		@Override
		public void run() {
			dubboServiceDayIPService.insertOrAppend(day);
		}
	}
}

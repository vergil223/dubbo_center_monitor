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

import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.service.DubboMethodDayService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

@Component("methodDayCache")
public class MethodDayCache {
	private static final Log log = LogFactory.getLog(MethodDayCache.class);
	
	@Autowired
	private DubboMethodDayService dubboMethodDayService;
	
	private static final ReadWriteLock providerLock = new ReentrantReadWriteLock();
	
	private static final Map<String,AtomicReference<DubboMethodDay>> PROVIDER_CACHE=new ConcurrentHashMap<String,AtomicReference<DubboMethodDay>>();
	
	public static void updateProviderCache(DubboMethodMinuteIP minute) {
		String appName = minute.getAppName();
		String service = minute.getService();
		String method = minute.getMethod();
		Date time = DateUtil.trimToDay(minute.getTime());
		String key = "MethodDayCache.updateProviderCache_"+appName + "_" + service + "_" + method + "_" + DateUtil.format(time);

		AtomicReference<DubboMethodDay> dayReference = null;

		try {
			providerLock.readLock().lock();
			dayReference = PROVIDER_CACHE.get(key);
		} finally {
			providerLock.readLock().unlock();
		}
		if (dayReference == null) {
			dayReference = new AtomicReference<DubboMethodDay>();
			DubboMethodDay day = new DubboMethodDay(appName, service, method, time);
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
			DubboMethodDay dayOld = dayReference.get();
			DubboMethodDay dayUpdated = new DubboMethodDay(appName, service, method, time);

			if (minute.getSuccessTimes() != null
					&& minute.getSuccessTimes() != 0) {
				dayUpdated
						.setElapsedAvg((dayOld.getElapsedAvg().multiply(BigDecimal.valueOf(dayOld.getSuccessTimes())).add(minute
									.getElapsedTotal())).divide(BigDecimal.valueOf(dayOld.getSuccessTimes() + minute
										.getSuccessTimes()),4,BigDecimal.ROUND_HALF_UP));
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
	
	public void writeMethodDayCacheToDB(){
		long start=DateUtil.now().getTime();
		log.info("writeMethodDayCacheToDB START");
		Map<String,AtomicReference<DubboMethodDay>> copyMap=null;
		try{
			providerLock.writeLock().lock();
			copyMap=new HashMap<String,AtomicReference<DubboMethodDay>>(PROVIDER_CACHE);			
			PROVIDER_CACHE.clear();
		}finally{
			providerLock.writeLock().unlock();
		}
		
		ExecutorService exec=Executors.newFixedThreadPool(10);
		
		for(String key:copyMap.keySet()){
			try {
				AtomicReference<DubboMethodDay> dayReference = copyMap
						.get(key);
				DubboMethodDay day = dayReference.get();
				exec.submit(new writeToDBRunnable(dubboMethodDayService, day));
			}catch(Exception e){
				log.error("writeMethodDayCacheToDB error:"+copyMap.get(key).get(), e);
			}
		}
		
		exec.shutdown();
		try{
			while(!exec.awaitTermination(300, TimeUnit.SECONDS)){
				log.info("writeMethodDayCacheToDB still running, costed:"+(DateUtil.now().getTime()-start)+"ms");
			}			
		}catch(Exception e){
			log.error("error when waiting for threads end.",e);
		}
		
		log.info("writeMethodDayCacheToDB END, cost:"+(DateUtil.now().getTime()-start)+"ms");
	}
	
	private class writeToDBRunnable implements Runnable{
		private DubboMethodDayService dubboMethodDayService;
		private DubboMethodDay day;
		public writeToDBRunnable(DubboMethodDayService dubboMethodDayService,DubboMethodDay day){
			this.dubboMethodDayService=dubboMethodDayService;
			this.day=day;
		}
		@Override
		public void run() {
			dubboMethodDayService.insertOrAppend(day);
		}
	}
}

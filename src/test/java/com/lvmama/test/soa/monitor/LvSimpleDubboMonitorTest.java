package com.lvmama.test.soa.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.monitor.MonitorService;
import com.lvmama.soa.monitor.cache.AppMinuteCache;
import com.lvmama.soa.monitor.cache.MethodDayCache;
import com.lvmama.soa.monitor.cache.MethodDayIPCache;
import com.lvmama.soa.monitor.cache.ServiceDayIPCache;
import com.lvmama.soa.monitor.job.migration.DubboMethodDayIPMigrationJob;
import com.lvmama.soa.monitor.job.migration.DubboMethodDayMigrationJob;
import com.lvmama.soa.monitor.job.migration.DubboServiceDayIPMigrationJob;
import com.lvmama.soa.monitor.service.LvDubboMonitor;
import com.lvmama.soa.monitor.util.DateUtil;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class LvSimpleDubboMonitorTest extends BaseTest{
	private Log log=LogFactory.getLog(LvSimpleDubboMonitorTest.class);
	
	@Autowired
	LvDubboMonitor lvSimpleDubboMonitor;
	
	@Autowired
	AppMinuteCache appMinuteCache;
	
	@Autowired
	MethodDayCache methodDayCache;
	
	@Autowired
	MethodDayIPCache methodDayIPCache;
	
	@Autowired
	ServiceDayIPCache serviceDayIPCache;
	
	@Autowired
	DubboMethodDayIPMigrationJob dubboMethodDayIPMigrationJob;
	@Autowired
	DubboMethodDayMigrationJob dubboMethodDayMigrationJob;
	@Autowired
	DubboServiceDayIPMigrationJob dubboServiceDayIPMigrationJob;
	
	private static final String APP_NAME_TEST = "Test_APP_NAME";
	private static final String yyyyMMddHHmmss = "20151104000000";
	private static final String PROVIDER="192.168.0.222";
	private static final String CONSUMER="192.168.0.111";
	private static final int SUCCESS_TIMES = 10;
	private static final int FAIL_TIMES = 2;
	private static final int ELAPSED_TOTAL = 20;
	private static final int MAX_ELAPSED = 5;
	
	@Test
	public void testCollectProvider(){
		URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, yyyyMMddHHmmss);
		statistics=statistics.setHost(PROVIDER);
		statistics=statistics.addParameter(MonitorService.CONSUMER, CONSUMER);
		statistics=statistics.addParameter("appName", APP_NAME_TEST);
		statistics=statistics.setServiceInterface("com.lvmama.test.soa.monitor.LvSimpleDubboMonitorTest");
		statistics=statistics.addParameter(MonitorService.METHOD, "testInsertProvider");
		statistics=statistics.addParameter(MonitorService.SUCCESS, SUCCESS_TIMES);
		statistics=statistics.addParameter(MonitorService.FAILURE, FAIL_TIMES);
		statistics=statistics.addParameter(MonitorService.ELAPSED, ELAPSED_TOTAL);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, MAX_ELAPSED);
		
		lvSimpleDubboMonitor.collect(statistics);
	}
	
	@Test
	public void testElapsedAvg(){
		URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);
		statistics=statistics.setHost(PROVIDER);
		statistics=statistics.addParameter(MonitorService.CONSUMER, CONSUMER);
		statistics=statistics.addParameter("appName", APP_NAME_TEST);
		statistics=statistics.setServiceInterface("com.lvmama.test.soa.monitor.LvSimpleDubboMonitorTest");
		statistics=statistics.addParameter(MonitorService.METHOD, "testInsertProvider");
		statistics=statistics.addParameter(MonitorService.FAILURE, 0L);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110000");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 1L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 115L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 115L); 
		lvSimpleDubboMonitor.collect(statistics);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110100");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 1L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 293L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 293L);
		lvSimpleDubboMonitor.collect(statistics);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110200");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 0L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 0L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 0L);
		lvSimpleDubboMonitor.collect(statistics);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110300");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 0L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 0L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 0L);
		lvSimpleDubboMonitor.collect(statistics);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110400");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 0L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 0L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 0L);
		lvSimpleDubboMonitor.collect(statistics);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110500");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 2L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 247L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 132L);
		lvSimpleDubboMonitor.collect(statistics);
		
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, "20160801110600");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 0L);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 0L);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, 0L);
		lvSimpleDubboMonitor.collect(statistics);
		
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$:"+MethodDayIPCache.getCache());
		
		appMinuteCache.writeProviderAppCacheToDB();
		
//		@Autowired
//		MethodDayCache methodDayCache;
		methodDayCache.writeMethodDayCacheToDB();
//		@Autowired
//		MethodDayIPCache methodDayIPCache;
		methodDayIPCache.writeMethodDayIPCacheToDB();
//		@Autowired
//		ServiceDayIPCache serviceDayIPCache;
		serviceDayIPCache.writeServiceDayIPCacheToDB();
		
		
//		@Autowired
//		DubboMethodDayIPMigrationJob dubboMethodDayIPMigrationJob;
		dubboMethodDayIPMigrationJob.redisToMysqlToday();
//		@Autowired
//		DubboMethodDayMigrationJob dubboMethodDayMigrationJob;
		dubboMethodDayMigrationJob.redisToMysqlToday();
//		@Autowired
//		DubboServiceDayIPMigrationJob dubboServiceDayIPMigrationJob;
		dubboServiceDayIPMigrationJob.redisToMysqlToday();
		
		System.out.println("END");
	}
	
	@Test
	public void testCollectConsumer(){
		URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, yyyyMMddHHmmss);
		statistics=statistics.setHost(CONSUMER);
		statistics=statistics.addParameter(MonitorService.PROVIDER, PROVIDER);
		statistics=statistics.addParameter("appName", APP_NAME_TEST);
		statistics=statistics.setServiceInterface("com.lvmama.test.soa.monitor.LvSimpleDubboMonitorTest");
		statistics=statistics.addParameter(MonitorService.METHOD, "testInsertProvider");
		statistics=statistics.addParameter(MonitorService.SUCCESS, SUCCESS_TIMES);
		statistics=statistics.addParameter(MonitorService.FAILURE, FAIL_TIMES);
		statistics=statistics.addParameter(MonitorService.ELAPSED, ELAPSED_TOTAL);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, MAX_ELAPSED);
		
		lvSimpleDubboMonitor.collect(statistics);
	}
	
	@Test
	public void concurrentCollectProvider(){
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for(int i=1;i<=1000;i++){
			threadPool.submit(new Runnable(){
				@Override
				public void run() {
					URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);
					statistics=statistics.addParameter(Constants.TIMESTAMP_KEY,String.valueOf(((DateUtil.parse(yyyyMMddHHmmss).getTime()+(Double.valueOf(Math.random()*24*60*60*1000)).intValue())/100000)*100000));
					statistics=statistics.setHost(PROVIDER);
					statistics=statistics.addParameter(MonitorService.CONSUMER, CONSUMER);
					statistics=statistics.addParameter("appName", APP_NAME_TEST+"_"+((Double.valueOf(Math.random()*4)).intValue()));
					statistics=statistics.setServiceInterface("com.lvmama.test.soa.monitor.LvSimpleDubboMonitorTest");
					statistics=statistics.addParameter(MonitorService.METHOD, "testInsertProvider");
					statistics=statistics.addParameter(MonitorService.SUCCESS, SUCCESS_TIMES);
					statistics=statistics.addParameter(MonitorService.FAILURE, FAIL_TIMES);
					statistics=statistics.addParameter(MonitorService.ELAPSED, ELAPSED_TOTAL);
					statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, MAX_ELAPSED);
					
					lvSimpleDubboMonitor.collect(statistics);
				}
			});
		}
		
		threadPool.shutdown();
		try{
			log.info("concurrentCollectProvider() END, thread still alive...");
			System.in.read();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void concurrentCollectProviderWithRealTime(){
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for(int i=1;i<=1000000;i++){
			threadPool.submit(new Runnable(){
				@Override
				public void run() {
					URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);
					statistics=statistics.addParameter(Constants.TIMESTAMP_KEY,String.valueOf(((System.currentTimeMillis())/100000)*100000));
					statistics=statistics.setHost(PROVIDER);
					statistics=statistics.addParameter(MonitorService.CONSUMER, CONSUMER);
					statistics=statistics.addParameter("appName", APP_NAME_TEST+"_"+((Double.valueOf(Math.random()*4)).intValue()));
					statistics=statistics.setServiceInterface("com.lvmama.test.soa.monitor.LvSimpleDubboMonitorTest");
					statistics=statistics.addParameter(MonitorService.METHOD, "testInsertProvider");
					statistics=statistics.addParameter(MonitorService.SUCCESS, SUCCESS_TIMES);
					statistics=statistics.addParameter(MonitorService.FAILURE, FAIL_TIMES);
					statistics=statistics.addParameter(MonitorService.ELAPSED, ELAPSED_TOTAL);
					statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, MAX_ELAPSED);
					
					lvSimpleDubboMonitor.collect(statistics);
				}
			});
		}
		
		try{
			log.info("concurrentCollectProvider() END, thread still alive...");
			System.in.read();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void concurrentCollectWithMockData(){
		ExecutorService exec = Executors.newFixedThreadPool(100);
		List<Future> resultFutures=new ArrayList<Future>();
		for (int a = 0; a < DataMockConst.apps.size(); a++) {
			try {
				String appName = DataMockConst.apps.get(a);
				Future f=exec.submit(new CollectStatisticsRunnable(appName));
				resultFutures.add(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(Future f:resultFutures){
			try{
				f.get();
			}catch(Exception e){
				log.error("error when waiting for result", e);
			}
		}
		
		System.out.println("concurrentCollectWithMockData() END but Thread still alive..");
		try{
			System.in.read();			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private class CollectStatisticsRunnable implements Runnable{
		private String appName;
		public CollectStatisticsRunnable(String appName){
			this.appName=appName;
		}
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			long count = 0;
			
			Date time = DateUtil.trimToDay(DateUtil.now());

			URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);

			statistics=statistics.addParameter(MonitorService.SUCCESS, SUCCESS_TIMES);
			statistics=statistics.addParameter(MonitorService.FAILURE, FAIL_TIMES);
			statistics=statistics.addParameter(MonitorService.ELAPSED, ELAPSED_TOTAL);
			statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, MAX_ELAPSED);
			
			for (int s = 0; s < DataMockConst.services.size(); s++) {
				String service = DataMockConst.services.get(s);
				for (int m = 0; m < DataMockConst.methods.size(); m++) {
					String method = DataMockConst.methods.get(m);
					for (int ipp = 0; ipp < DataMockConst.IP_PROVIDER
							.size(); ipp++) {
						String ipProvider = DataMockConst.IP_PROVIDER
								.get(ipp);
						for (int ipc = 0; ipc < DataMockConst.IP_CONSUMER
								.size(); ipc++) {
							String ipConsumer = DataMockConst.IP_CONSUMER
									.get(ipc);

							statistics=statistics.addParameter(Constants.TIMESTAMP_KEY,time.getTime()+(Double.valueOf(Math.random()*24*60*60*1000)).intValue());
							statistics=statistics.setHost(ipProvider);
							statistics=statistics.addParameter(MonitorService.CONSUMER, ipConsumer);
							statistics=statistics.addParameter("appName", appName.trim());
							statistics=statistics.setServiceInterface(service.trim());
							statistics=statistics.addParameter(MonitorService.METHOD, method.trim());
							
							lvSimpleDubboMonitor.collect(statistics);
//							dbShardService.checkAndCreateTable(day
//									.getShardTableName());
//							count += dubboMethodDayIPService
//									.insertOrAppend(day);
						}
					}
				}
			}
			
			System.out.println("cost " + (System.currentTimeMillis() - start)
					+ "ms to insert " + count + " records");
		}
		
	}
	
	@Test
	public void testCollectProvider2(){
		URL statistics=new URL("dubbo", NetUtils.getLocalHost(), 0);
		long time = 20151123000000L;
		statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, String.valueOf(time));
		statistics=statistics.setHost(PROVIDER);
		statistics=statistics.addParameter(MonitorService.CONSUMER, CONSUMER);
		statistics=statistics.addParameter("appName", APP_NAME_TEST);
		statistics=statistics.setServiceInterface("com.lvmama.test.soa.monitor.LvSimpleDubboMonitorTest");
		statistics=statistics.addParameter(MonitorService.METHOD, "testInsertProvider2");
		statistics=statistics.addParameter(MonitorService.SUCCESS, 0);
		statistics=statistics.addParameter(MonitorService.FAILURE, 0);
		statistics=statistics.addParameter(MonitorService.ELAPSED, 0);
		statistics=statistics.addParameter(MonitorService.MAX_ELAPSED, MAX_ELAPSED);
		
		lvSimpleDubboMonitor.collect(statistics);
		for(int i=1;i<=59;i++){
			statistics=statistics.removeParameter(Constants.TIMESTAMP_KEY);
			statistics=statistics.removeParameter(MonitorService.SUCCESS);
			statistics=statistics.removeParameter(MonitorService.ELAPSED);
			
			statistics=statistics.addParameter(Constants.TIMESTAMP_KEY, String.valueOf(time+i*100));
			statistics=statistics.addParameter(MonitorService.SUCCESS, i%2);
			statistics=statistics.addParameter(MonitorService.ELAPSED, i%2);
			lvSimpleDubboMonitor.collect(statistics);
		}
		
		try{
			log.info("testCollectProvider2() END, thread still alive...");
			System.in.read();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])throws Exception{
		Random random=new Random(24*60*60*1000L);
		for(int i=0;i<100;i++){
			System.out.println(String.valueOf(((DateUtil.parse(yyyyMMddHHmmss).getTime()+(Double.valueOf(Math.random()*24*60*60*1000)).intValue())/100000)*100000));			
		}
		
	}
}

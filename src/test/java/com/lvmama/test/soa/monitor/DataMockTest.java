package com.lvmama.test.soa.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class DataMockTest extends BaseTest{
	private static final Log log=LogFactory.getLog(DataMockTest.class);
	
	private static final int MINUTES_OF_DAY = 1440;
	
	@Autowired
	DubboMethodDayIPService dubboMethodDayIPService;
	
	@Test
	public void testInsertMethodDayIP() {
		ExecutorService exec = Executors.newFixedThreadPool(100);
		List<Future> resultFutures=new ArrayList<Future>();
		for (int a = 0; a < DataMockConst.apps.size(); a++) {
			try {
				String appName = DataMockConst.apps.get(a);
				Future f=exec.submit(new InsertMethodDayIPRunnable(appName));
				resultFutures.add(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		exec.shutdown();
		for(Future f:resultFutures){
			try{
				f.get();
			}catch(Exception e){
				log.error("error when waiting for result", e);
			}
		}

	}
	
	private class InsertMethodDayIPRunnable implements Runnable{
		private String appName;
		public InsertMethodDayIPRunnable(String appName){
			this.appName=appName;
		}
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			long count = 0;
			
			Date time = DateUtil.trimToDay(DateUtil.now());

			DubboMethodDayIP day = new DubboMethodDayIP();
			day.setTime(time);
			day.setSuccessTimes(10L);
			day.setFailTimes(1L);
			day.setElapsedAvg(2L);
			day.setElapsedMax(5L);
			day.setSuccessTimesDetail("1031 10");
			day.setFailTimesDetail("1031 1");
			day.setElapsedTotalDetail("1031 20");
			day.setElapsedMaxDetail("1031 5");
			
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

							day.setAppName(appName.trim());
							day.setService(service.trim());
							day.setMethod(method.trim());
							day.setProviderIP(ipProvider);
							day.setConsumerIP(ipConsumer);

							count += dubboMethodDayIPService
									.insertOrAppend(day);
						}
					}
				}
			}
			
			System.out.println("cost " + (System.currentTimeMillis() - start)
					+ "ms to insert " + count + " records");
		}
		
	}
//	@Test
//	public void testInsertProviderDetail(){
//		long now=System.currentTimeMillis();
//		
//		for(int minute=1;minute<=MINUTES_OF_DAY;minute++){
//			Date time = new Date(now-minute*60*1000L);
//			
//			DubboProviderDetail detail=new DubboProviderDetail();
//			detail.setTime(time);
//			detail.setSuccessTimes(1000L);
//			detail.setFailTimes(10L);
//			detail.setElapsedMax(1000L);
//			detail.setElapsedTotal(400000L);
//
//			
//			for(int a=0;a<DataMockConst.apps.size();a++){
//				long start=System.currentTimeMillis();
//				long count=0;
//				try{
//					String appName=DataMockConst.apps.get(a);
//					for(int s=0;s<DataMockConst.services.size();s++){
//						String service=DataMockConst.services.get(s);
//						for(int m=0;m<DataMockConst.methods.size();m++){
//							String method=DataMockConst.methods.get(m);
//							
//							detail.setAppName(appName.trim());
//							detail.setService(service.trim());
//							detail.setMethod(method.trim());
//							count+=DubboProviderDetailService.insertOrUpdate(detail);			
//						}
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				
//				System.out.println("cost "+(System.currentTimeMillis()-start)+"ms to insert "+count+" records");
//			}
//		}
//		
//	}
	
	
	
	
	
	
	
}

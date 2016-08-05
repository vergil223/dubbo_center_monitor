package com.lvmama.test.soa.monitor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.dao.mybatis.DubboMethodDayIPDao;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class DubboMethodDayIPServiceTest extends BaseTest{
	@Autowired
	private DubboMethodDayIPService dubboMethodDayIPService;
	
	@Autowired
	private DubboMethodDayIPDao dubboMethodDayIPDao;
	
	@Test
	public void testInsertOrAppend(){
		int num_of_threads = 10;
		
		CountDownLatch start=new CountDownLatch(1);
		CountDownLatch end=new CountDownLatch(num_of_threads);
		
		ExecutorService exec =Executors.newFixedThreadPool(num_of_threads);
		for(int i=1;i<=num_of_threads;i++){
			exec.submit(new testInsertOrAppendRunnable(start,end));		
		}
		start.countDown();
		
		exec.shutdown();
		try{
			end.await();			
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{			
			System.out.println("testInsertOrAppend() END");			
		}
	}
	
	@Test
	public void testSelectMergedList(){
		List<DubboMethodDayIP> list=dubboMethodDayIPService.selectMergedList("pet_public", "com.lvmama.comm.bee.service.sync.SyncExecuteService", "20151120");
		System.out.println("------------------------------");
		for(DubboMethodDayIP day:list){
			System.out.println(day);
		}
		System.out.println("------------------------------");
	}
	
	@Test
	public void testSelectByMethod(){
		List<DubboMethodDayIP> listRedis=dubboMethodDayIPService.selectByMethod("pet_public", "com.lvmama.comm.bee.service.sync.SyncBaseService","findTriggerPageListWithTime", "20151201");
		
		List<DubboMethodDayIP> listMySql=dubboMethodDayIPService.selectByMethod("pet_public", "com.lvmama.comm.bee.service.sync.SyncBaseService","findSyncTemplateById", "20151118");
		
		System.out.println("---------------Redis---------------");
		for(DubboMethodDayIP day:listRedis){
			System.out.println(day);
		}
		System.out.println("------------------------------");
		
		System.out.println("-------------Mysql-----------------");
		for(DubboMethodDayIP day:listMySql){
			System.out.println(day);
		}
		System.out.println("------------------------------");
	}
	
	private class testInsertOrAppendRunnable implements Runnable{
		CountDownLatch startLatch;
		CountDownLatch endLatch;
		
		testInsertOrAppendRunnable(CountDownLatch start,CountDownLatch end){
			startLatch=start;
			endLatch=end;
		}
		@Override
		public void run() {
			try{
				startLatch.await();
				insertOrAppend();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}finally{				
				endLatch.countDown();				
			}
		}
	}

	private void insertOrAppend() {
		Date now = DateUtil.now();
		
		DubboMethodDayIP day = new DubboMethodDayIP("LZY5", "com.lvmama.test.soa.monitor.DubboMethodDayIPServiceTest", "testInsertOrAppend",
				"111.333.555.777", "222.444.666.888", DateUtil.trimToDay(now));
		day.setSuccessTimes(10L);
		day.setFailTimes(1L);
		day.setElapsedAvg(BigDecimal.valueOf(2L));
		day.setElapsedMax(5L);
		day.setSuccessTimesDetail(DateUtil.HHmm(now) + " "
				+ day.getSuccessTimes()+StringUtil.getLineSeparator());
		day.setFailTimesDetail(DateUtil.HHmm(now) + " "
				+ day.getFailTimes()+StringUtil.getLineSeparator());
		day.setElapsedTotalDetail(DateUtil.HHmm(now) + " "
				+ day.getElapsedAvg().multiply(BigDecimal.valueOf(day.getSuccessTimes()))+StringUtil.getLineSeparator());
		day.setElapsedMaxDetail(DateUtil.HHmm(now) + " "
				+ day.getElapsedMax()+StringUtil.getLineSeparator());
		
		for(int i=1;i<=10;i++){
			dubboMethodDayIPService.insertOrAppend(day);
			dubboMethodDayIPDao.insert(day);
		}
	}
	
	
	
}

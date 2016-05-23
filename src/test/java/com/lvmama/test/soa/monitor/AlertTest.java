package com.lvmama.test.soa.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.cache.MethodDayIPCache;
import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class AlertTest extends BaseTest{
	private static final Log log=LogFactory.getLog(AlertTest.class);
	
//	@Autowired
//	private DubboMethodDayIPService dubboMethodDayIPService;
	@Autowired
	private MethodDayIPCache methodDayIPCache;
	
	@Autowired
	private DubboMethodDayIPService dubboMethodDayIPService;
	
	@Autowired
	private IAlertRecordService iAlertRecordService;
	
	@Test
	public void doTest(){
//		prepareYesterdaysData();
//		testMethodDayIPSuccessTimesWithOtherDaysAlert();
		testGetAlertMsg();
	}
	
//	@Test
	public void testMethodDayIPSuccessTimesWithinOneDayAlert() {
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("testMethodDayIPSuccessTimesAlert");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.lvmama.test.soa.monitor.AlertTest");
		
		long time=DateUtil.trimToMin(DateUtil.daysBefore(DateUtil.now(), 1)).getTime();
		for(int i=1;i<=10;i++){
			minute.setSuccessTimes(1000L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			methodDayIPCache.updateProviderCache(minute);
		}
		minute.setSuccessTimes(30000L);
		time+=60000;
		minute.setTime(new Date(time+60000));
		
		methodDayIPCache.updateProviderCache(minute);
		methodDayIPCache.writeMethodDayIPCacheToDB();
		try{
			System.in.read();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void prepareYesterdaysData() {
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("testMethodDayIPSuccessTimesAlert");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.lvmama.test.soa.monitor.AlertTest");
		
		//前一天每分钟1000次调用
		long time=DateUtil.trimToMin(DateUtil.daysBefore(DateUtil.now(), 1)).getTime();
		for(int i=1;i<=600;i++){
			minute.setSuccessTimes(1000L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			methodDayIPCache.updateProviderCache(minute);
		}
		
		methodDayIPCache.updateProviderCache(minute);
		methodDayIPCache.writeMethodDayIPCacheToDB();
		dubboMethodDayIPService.migrateFromRedisToMysql(DateUtil.yyyyMMdd(new Date(time)));
		
		try{
			Thread.sleep(5000L);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testMethodDayIPSuccessTimesWithOtherDaysAlert() {
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("testMethodDayIPSuccessTimesAlert");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.lvmama.test.soa.monitor.AlertTest");
		
		//今天每分钟4000次调用
		long time=DateUtil.trimToMin(DateUtil.now()).getTime();
		for(int i=1;i<=10;i++){
			minute.setSuccessTimes(2000L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			methodDayIPCache.updateProviderCache(minute);
		}
		
		methodDayIPCache.updateProviderCache(minute);
		methodDayIPCache.writeMethodDayIPCacheToDB();
		
		try{
			System.in.read();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void testGetAlertMsg(){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("insertTime_from", DateUtil.parse("20160523140000"));
		params.put("insertTime_to", DateUtil.parse("20160523143000"));
	
		for(TAltRecord tAltRecord:iAlertRecordService.selectList(params)){
			System.out.println("******************"+tAltRecord.getAlertMsg());
		}
	}
	
}

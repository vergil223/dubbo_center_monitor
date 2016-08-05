package com.lvmama.test.soa.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.cache.MethodDayCache;
import com.lvmama.soa.monitor.cache.MethodDayIPCache;
import com.lvmama.soa.monitor.dao.redis.JedisTemplate;
import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.service.DubboMethodDayService;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.service.alert.IAlertService;
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
	private MethodDayCache methodDayCache;
	
	@Autowired
	private DubboMethodDayIPService dubboMethodDayIPService;
	@Autowired
	private DubboMethodDayService dubboMethodDayService;
	
	@Autowired
	private IAlertService methodDayAlertService;
	
	@Autowired
	private IAlertRecordService iAlertRecordService;
	
	private static final JedisTemplate jedisWriterTemplate = JedisTemplate.getWriterInstance();
	
	@Test
	public void doTest(){
//		prepareYesterdaysDataMethodIP();
//		testMethodDayIPSuccessTimesWithOtherDaysAlert();
		
//		prepareYesterdaysDataMethod();
		testMethodDaySuccessTimesWithOtherDaysAlert();
		
//		testMethodDaySuccessTimesWithinOneDayAlert();
		
//		testGetAlertMsg();
//		
//		countAlertMsg();
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
	
	public void prepareYesterdaysDataMethodIP() {
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
		
		methodDayIPCache.writeMethodDayIPCacheToDB();
		dubboMethodDayIPService.migrateFromRedisToMysql(DateUtil.yyyyMMdd(DateUtil.daysBefore(DateUtil.now(), 1)));
		
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
			minute.setSuccessTimes(4000L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			methodDayIPCache.updateProviderCache(minute);
		}
		
		methodDayIPCache.writeMethodDayIPCacheToDB();
		
		try{
			System.in.read();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void testMethodDaySuccessTimesWithinOneDayAlert() {
		Set<String> keysToDel=jedisWriterTemplate.keys("*DubboMethodDay*");
		for(String key:keysToDel){
			jedisWriterTemplate.del(key);			
		}
		
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("testMethodDayIPSuccessTimesAlert");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.lvmama.test.soa.monitor.AlertTest");
		
		long time=DateUtil.trimToMin(DateUtil.daysBefore(DateUtil.now(), 1)).getTime();
		for(int i=1;i<=10;i++){
			minute.setSuccessTimes(600L);
//			minute.setSuccessTimes(0L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			methodDayCache.updateProviderCache(minute);
		}
		minute.setSuccessTimes(1L);
		time+=60000;
		minute.setTime(new Date(time+60000));
		methodDayCache.updateProviderCache(minute);
		
		minute.setSuccessTimes(1000L);
		time+=60000;
		minute.setTime(new Date(time+60000));
		
		methodDayCache.updateProviderCache(minute);
		methodDayCache.writeMethodDayCacheToDB();
		try{
			System.in.read();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void prepareYesterdaysDataMethod() {
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("testMethodDayIPSuccessTimesAlert");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.lvmama.test.soa.monitor.AlertTest");
		
		//前一天每分钟1000次调用
		long time=DateUtil.trimToMin(DateUtil.daysBefore(DateUtil.now(), 1)).getTime();
		for(int i=1;i<=360;i++){
			if(i==180){
				minute.setConsumerIP("3.3.3.3");
				minute.setProviderIP("4.4.4.4");
			}
			minute.setSuccessTimes(1000L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			MethodDayCache.updateProviderCache(minute);
		}
				
		methodDayCache.writeMethodDayCacheToDB();
		dubboMethodDayService.migrateFromRedisToMysql(DateUtil.yyyyMMdd(DateUtil.daysBefore(DateUtil.now(), 1)));
		
		try{
			Thread.sleep(5000L);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMethodDaySuccessTimesWithOtherDaysAlert() {
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("autoIncrease");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.pub.api.SoaTestService");
		
		//今天每分钟4000次调用
		long time=DateUtil.trimToMin(DateUtil.now()).getTime();
		for(int i=1;i<=2;i++){
			minute.setSuccessTimes(10000L);
			time+=60000;
			minute.setTime(new Date(time+60000));
			methodDayCache.updateProviderCache(minute);
		}
		
		methodDayCache.writeMethodDayCacheToDB();
		
		try{
			System.in.read();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void testGetAlertMsg(){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("insertTime_from", DateUtil.parse("20160530000000"));
		params.put("insertTime_to",   DateUtil.parse("20160530235959"));
	
		for(TAltRecord tAltRecord:iAlertRecordService.selectList(params)){
			System.out.println("******************"+tAltRecord.getAlertMsg());
		}
	}
	
	public void countAlertMsg(){
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("insertTime_from", DateUtil.parse("20160526090000"));
		params.put("insertTime_to",   DateUtil.parse("20160526230000"));
	
		System.out.println("******************"+iAlertRecordService.count(params));
	}
	
	@Test
	public void testSaveOrUpdateAlert(){
		TAltAlert tAltAlert=new TAltAlert();
		tAltAlert.setActionIds("1");
		tAltAlert.setActionParam("actionParam:a");
		tAltAlert.setConditionIds("1");
		tAltAlert.setConditionParam("conditionParam:b");
		tAltAlert.setDescription("DESC");
		tAltAlert.setEnabled("N");
		tAltAlert.setName("test Alert 20160623");
		tAltAlert.setTarget(".*");
		tAltAlert.setTargetExclude("*ExludeService*");
		
		methodDayAlertService.saveOrUpdate(tAltAlert);
		
		System.out.println(tAltAlert.getId_());
	}
	
}

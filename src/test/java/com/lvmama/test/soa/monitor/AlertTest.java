package com.lvmama.test.soa.monitor;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.cache.MethodDayIPCache;
import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class AlertTest extends BaseTest{
	private static final Log log=LogFactory.getLog(AlertTest.class);
	
//	@Autowired
//	private DubboMethodDayIPService dubboMethodDayIPService;
	@Autowired
	private MethodDayIPCache methodDayIPCache;
	
	@Test
	public void testMethodDayIPSuccessTimesAlert() {
		DubboMethodMinuteIP minute=new DubboMethodMinuteIP();
		minute.setAppName("ALERT_TEST");
		minute.setConsumerIP("1.1.1.1");
		minute.setMethod("testMethodDayIPSuccessTimesAlert");
		minute.setProviderIP("2.2.2.2");
		minute.setService("com.lvmama.test.soa.monitor.AlertTest");
		
		long time=DateUtil.trimToMin(DateUtil.now()).getTime();
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
	
	
	
	
	
	
	
}

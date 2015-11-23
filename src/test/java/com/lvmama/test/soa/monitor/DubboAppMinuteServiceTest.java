package com.lvmama.test.soa.monitor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.dao.mybatis.DubboAppMinuteDao;
import com.lvmama.soa.monitor.entity.DubboAppMinute;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class DubboAppMinuteServiceTest extends BaseTest{
	private static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	@Autowired
	DubboAppMinuteService dubboAppMinuteService;
	
	@Test
	public void testInsertOrUpdate()throws Exception{
		DubboAppMinute app=new DubboAppMinute();
		app.setAppName("lvmama_soa_monitor");
		app.setTime(new SimpleDateFormat(yyyyMMddHHmmss).parse("20881011133000"));
		app.setSuccessTimes(10000L);
		app.setFailTimes(1L);
		app.setElapsedAvg(1L);
		app.setElapsedMax(5L);
		
		dubboAppMinuteService.insertOrAppend(app);
	}
	
	@Test
	public void testQueryByAppNameAndTime() throws Exception{
		SimpleDateFormat format=new SimpleDateFormat(yyyyMMddHHmmss); 
		
		Map param=new HashMap();
		param.put("time_from", format.parse("20151028180000"));
		param.put("time_to", format.parse("20151028190000"));
		param.put("appName", "super_front");
		
		List<DubboAppMinute> appList = dubboAppMinuteService.selectList(param);
		for(DubboAppMinute app:appList){
			System.out.println("----------------------");
			System.out.println(app.getAppName());
			System.out.println(app.getTime());
			System.out.println(app.getSuccessTimes());
			System.out.println(app.getFailTimes());
			System.out.println(app.getElapsedAvg());
			System.out.println(app.getElapsedMax());
			System.out.println("----------------------");
		}
	}
	
	@Test
	public void testGetAppNames(){
		List<String> appNames=dubboAppMinuteService.getAppNames();
		for(String appName:appNames){
			System.out.println(appName);			
		}
	}
	
}

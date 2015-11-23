package com.lvmama.test.soa.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.service.DubboServiceDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class DubboServiceDayIPServiceTest extends BaseTest {
	@Autowired
	private DubboServiceDayIPService dubboServiceDayIPService;
	
	@Test
	public void testSelectByAppNameAndDay(){
		String appName="pet_public";
		String yyyyMMdd="20151118";
		
		List<DubboServiceDayIP> list=dubboServiceDayIPService.selectMergedListByAppNameAndDay(appName, yyyyMMdd);
		for(DubboServiceDayIP day:list){
			System.out.println(day);
		}
	}
	
	@Test
	public void testSelectList(){
		//20151118_com.lvmama.soa.monitor.entity.DubboServiceDayIP_pet_public_com.lvmama.comm.bee.service.sync.SyncBaseService_10.2.2.73_10.2.2.73_20151118
		Map param=new HashMap();
		param.put("appName", "pet_public");
		param.put("serviceName", "com.lvmama.comm.bee.service.sync.SyncBaseService");
		param.put("consumerIP", "10.2.2.73");
		param.put("providerIP", "10.2.2.73");
//		param.put("time", DateUtil.parseDateYYYYMMdd("20151118"));
		param.put("time_from", DateUtil.parseDateYYYYMMdd("20151118"));
		param.put("time_to", DateUtil.parseDateYYYYMMdd("20151118"));
		
		List<DubboServiceDayIP> list=dubboServiceDayIPService.selectList(param);
		for(DubboServiceDayIP day:list){
			System.out.println(day);
		}
	}
}

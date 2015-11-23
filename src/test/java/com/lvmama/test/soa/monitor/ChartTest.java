package com.lvmama.test.soa.monitor;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.service.chart.AppMinuteChartService;
import com.lvmama.soa.monitor.service.chart.MethodMinuteChartService;
import com.lvmama.soa.monitor.service.chart.ServiceMinuteChartService;
import com.lvmama.soa.monitor.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class ChartTest extends BaseTest{
	@Autowired
	private AppMinuteChartService appMinuteChartService;
	@Autowired
	private ServiceMinuteChartService serviceMinuteChartService;
	@Autowired
	private MethodMinuteChartService methodMinuteChartService;
	
	@Test
	public void testAppMinuteChart(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("time_from", DateUtil.parse("20990101000000"));
		param.put("time_to", DateUtil.parse("20990101240000"));
		param.put("appName", "Test_APP_NAME");
		
		appMinuteChartService.drawSuccessChart(param);
		appMinuteChartService.drawFailChart(param);
		appMinuteChartService.drawElapsedAvgChart(param);
		appMinuteChartService.drawElapsedMaxChart(param);
	}
	
	@Test
	public void testServiceMinuteChart(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("time_from", DateUtil.parse("20151116000000"));
		param.put("time_to",   DateUtil.parse("20151116240000"));
		param.put("appName", "super_order");
		param.put("serviceName", "com.lvmama.tnt.order.service.TntHotelOrderManagerService");
		
		serviceMinuteChartService.drawSuccessChart(param);
		serviceMinuteChartService.drawFailChart(param);
		serviceMinuteChartService.drawElapsedAvgChart(param);
		serviceMinuteChartService.drawElapsedMaxChart(param);
	}
	
	@Test
	public void testMethodChart(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("appName", "pet_public");
		param.put("service", "com.lvmama.comm.bee.service.sync.SyncBaseService");
		param.put("method", "findTriggerPageListWithTime");
		param.put("time", DateUtil.parseDateYYYYMMdd("20151120"));
		
		methodMinuteChartService.drawSuccessChart(param);
		methodMinuteChartService.drawFailChart(param);
		methodMinuteChartService.drawElapsedAvgChart(param);
		methodMinuteChartService.drawElapsedMaxChart(param);
	}
	
	@Test
	public void testDrawRecentChart(){
		appMinuteChartService.drawRecentChart();
	}
}

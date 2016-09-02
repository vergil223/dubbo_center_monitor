package com.lvmama.soa.monitor.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.service.DubboServiceDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.web.controller.chart.ProviderAppChartController;

@Controller
@RequestMapping("/list/provider/service")
public class ListServiceByAppController {
	private static final Log log=LogFactory.getLog(ListServiceByAppController.class);
	
	@Autowired
	DubboServiceDayIPService dubboServiceDayIPService;
	
	@Autowired
	DubboAppMinuteService dubboAppMinuteService;
	
	@RequestMapping(value = "/service_day_list")
	public String listServiceByApp(HttpServletRequest request,HttpServletResponse response,String appName,String dayStr){
		String nextUrl = "/service_day_list";
		
		request.setAttribute("appNames", dubboAppMinuteService.getAppNames());
		request.setAttribute("serviceDayList", new ArrayList());
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			return nextUrl;			
		}
		if(StringUtil.isEmpty(dayStr)||StringUtil.isNullStr(dayStr)){
			return nextUrl;
		}
		
		List<DubboServiceDayIP> serviceDayList=dubboServiceDayIPService.selectMergedListByAppNameAndDay(appName, DateUtil.yyyyMMdd(DateUtil.parseWebDate(dayStr)));
		serviceDayList=sortBySuccessTimes(serviceDayList);
		
		request.setAttribute("serviceDayList", serviceDayList);
		request.setAttribute("appName", appName);
		request.setAttribute("dayStr", dayStr);
		
		return nextUrl;
	}
	
	private List<DubboServiceDayIP> sortBySuccessTimes(List<DubboServiceDayIP> serviceList){
		Collections.sort(serviceList, new SuccessTimesComparator());
		return serviceList;
	}
	
	private class SuccessTimesComparator implements Comparator<DubboServiceDayIP>{
		@Override
		public int compare(DubboServiceDayIP day1, DubboServiceDayIP day2) {
			if(day1==null){
				return 1;
			}else if(day2==null){
				return -1;
			}
			
			long successTimes1=day1.getSuccessTimes();
			long successTimes2=day2.getSuccessTimes();
			
			if(successTimes2>successTimes1){
				return 1;
			}else if(successTimes2<successTimes1){
				return -1;
			}else{
				return 0;
			}
		}
	}

	
}

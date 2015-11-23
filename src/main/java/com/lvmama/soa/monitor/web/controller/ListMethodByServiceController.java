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

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Controller
@RequestMapping("/list/provider/method")
public class ListMethodByServiceController {
	private static final Log log=LogFactory.getLog(ListMethodByServiceController.class);
	
	@Autowired
	DubboMethodDayIPService dubboMethodDayIPService;
	
	@Autowired
	DubboAppMinuteService dubboAppMinuteService;
	
	@RequestMapping(value = "/method_day_list")
	public String listServiceByApp(HttpServletRequest request,HttpServletResponse response,String appName,String service,String dayStr){
		String nextUrl = "/method_day_list";
		
		request.setAttribute("appNames", dubboAppMinuteService.getAppNames());
		request.setAttribute("methodDayList", new ArrayList());
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			return nextUrl;			
		}
		if(StringUtil.isEmpty(service)||StringUtil.isNullStr(service)){
			return nextUrl;			
		}
		if(StringUtil.isEmpty(dayStr)||StringUtil.isNullStr(dayStr)){
			return nextUrl;
		}
		
		List<DubboMethodDayIP> methodDayList=dubboMethodDayIPService.selectMergedList(appName,service, DateUtil.yyyyMMdd(DateUtil.parseWebDate(dayStr)));
		methodDayList=sortBySuccessTimes(methodDayList);
		
		request.setAttribute("methodDayList", methodDayList);
		request.setAttribute("appName", appName);
		request.setAttribute("service", service);
		request.setAttribute("dayStr", dayStr);
		
		return nextUrl;
	}
	
	private List<DubboMethodDayIP> sortBySuccessTimes(List<DubboMethodDayIP> serviceList){
		Collections.sort(serviceList, new SuccessTimesComparator());
		return serviceList;
	}
	
	private class SuccessTimesComparator implements Comparator<DubboMethodDayIP>{
		@Override
		public int compare(DubboMethodDayIP day1, DubboMethodDayIP day2) {
			if(day1==null){
				return 1;
			}else if(day2==null){
				return -1;
			}
			
			long successTimes1=day1.getSuccessTimes();
			long successTimes2=day2.getSuccessTimes();
			
			if(successTimes2>successTimes1){
				return 1;
			}else{
				return -1;
			}
		}
	}

	
}

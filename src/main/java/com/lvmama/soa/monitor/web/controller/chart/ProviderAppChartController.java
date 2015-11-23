package com.lvmama.soa.monitor.web.controller.chart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lvmama.soa.monitor.constant.ChartConst;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.service.chart.AppMinuteChartService;
import com.lvmama.soa.monitor.util.ChartUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Controller
@RequestMapping("/chart/provider/app")
public class ProviderAppChartController {
	private static final Log log=LogFactory.getLog(ProviderAppChartController.class);
	
	@Autowired
	AppMinuteChartService providerAppChartService;
	
	@Autowired
	DubboAppMinuteService providerAppService;
	
	@RequestMapping(value = "/home")
	public String getChart(HttpServletRequest request,HttpServletResponse response,String appName){
		List<String> appNames=providerAppService.getAppNames();
		
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			appName="";
		}
		request.setAttribute("appName", appName);
		request.setAttribute("appNames", appNames);
		return "/app_chart_home";
	}
	
	@RequestMapping(value = "/success")
	public void getSuccessChart(HttpServletRequest request,
			HttpServletResponse response, String appName) {
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			return;
		}
		String chartFullPath = providerAppChartService.chartFullPath(appName, ChartConst.CHART_TYPE_SUCCESS_TIMES);
		
		ChartUtil.showChartToResponse(response, chartFullPath);
	}
	
	@RequestMapping(value = "/fail")
	public void getFailChart(HttpServletRequest request,
			HttpServletResponse response, String appName) {
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			return;
		}
		String chartFullPath = providerAppChartService.chartFullPath(appName, ChartConst.CHART_TYPE_FAIL_TIMES);
		
		ChartUtil.showChartToResponse(response, chartFullPath);
	}
	
	@RequestMapping(value = "/elapsedAvg")
	public void getElapsedAvgChart(HttpServletRequest request,
			HttpServletResponse response, String appName) {
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			return;
		}
		String chartFullPath = providerAppChartService.chartFullPath(appName, ChartConst.CHART_TYPE_ELAPSED_AVG);
		
		ChartUtil.showChartToResponse(response, chartFullPath);
	}
	
	@RequestMapping(value = "/elapsedMax")
	public void getElapsedMaxChart(HttpServletRequest request,
			HttpServletResponse response, String appName) {
		if(StringUtil.isEmpty(appName)||StringUtil.isNullStr(appName)){
			return;
		}
		String chartFullPath = providerAppChartService.chartFullPath(appName, ChartConst.CHART_TYPE_ELAPSED_MAX);
		
		ChartUtil.showChartToResponse(response, chartFullPath);
	}
	
}

package com.lvmama.soa.monitor.web.controller.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.service.chart.MethodMinuteChartService;
import com.lvmama.soa.monitor.util.ChartUtil;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Controller
@RequestMapping("/chart/provider/method")
public class ProviderMethodChartController {
	Log log = LogFactory.getLog(ProviderMethodChartController.class);

	@Autowired
	MethodMinuteChartService methodMinuteChartService;
	@Autowired
	DubboAppMinuteService dubboAppMinuteService;

	@RequestMapping(value = "/home")
	public String getChart(HttpServletRequest request,
			HttpServletResponse response, String appName, String service,
			String method, String dayStr, String minuteFrom, String minuteTo) {
		List<String> appNames = dubboAppMinuteService.getAppNames();

		if (StringUtil.isEmpty(appName) || StringUtil.isNullStr(appName)) {
			appName = "";
		}
		if (StringUtil.isEmpty(service) || StringUtil.isNullStr(service)) {
			service = "";
		}
		if (StringUtil.isEmpty(method) || StringUtil.isNullStr(method)) {
			method = "";
		}

		request.setAttribute("appName", appName);
		request.setAttribute("appNames", appNames);
		request.setAttribute("service", service);
		request.setAttribute("method", method);
		request.setAttribute("dayStr", dayStr);
		request.setAttribute("minuteFrom", minuteFrom);
		request.setAttribute("minuteTo", minuteTo);

		return "/method_chart_home";
	}

	@RequestMapping(value = "/success")
	public void getSuccessChart(HttpServletRequest request,
			HttpServletResponse response, String appName, String service,
			String method, String dayStr, String minuteFrom, String minuteTo) {
		if (StringUtil.isEmpty(service) || StringUtil.isNullStr(service)) {
			return;
		}

		Map<String, Object> param = buildParameters(appName, service, method,
				dayStr, minuteFrom, minuteTo);

		String chartFullPath = methodMinuteChartService.drawSuccessChart(param);
		ChartUtil.showChartToResponse(response, chartFullPath);
	}

	@RequestMapping(value = "/fail")
	public void getFailChart(HttpServletRequest request,
			HttpServletResponse response, String appName, String service,
			String method, String dayStr, String minuteFrom, String minuteTo) {
		if (StringUtil.isEmpty(service) || StringUtil.isNullStr(service)) {
			return;
		}

		Map<String, Object> param = buildParameters(appName, service, method,
				dayStr, minuteFrom, minuteTo);

		String chartFullPath = methodMinuteChartService.drawFailChart(param);
		ChartUtil.showChartToResponse(response, chartFullPath);
	}

	@RequestMapping(value = "/elapsedAvg")
	public void getElapsedAvgChart(HttpServletRequest request,
			HttpServletResponse response, String appName, String service,
			String method, String dayStr, String minuteFrom, String minuteTo) {
		if (StringUtil.isEmpty(service) || StringUtil.isNullStr(service)) {
			return;
		}

		Map<String, Object> param = buildParameters(appName, service, method,
				dayStr, minuteFrom, minuteTo);

		String chartFullPath = methodMinuteChartService
				.drawElapsedAvgChart(param);
		ChartUtil.showChartToResponse(response, chartFullPath);
	}

	@RequestMapping(value = "/elapsedMax")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public void getElapsedMaxChart(HttpServletRequest request,
			HttpServletResponse response, String appName, String service,
			String method, String dayStr, String minuteFrom, String minuteTo) {
		if (StringUtil.isEmpty(service) || StringUtil.isNullStr(service)) {
			return;
		}

		Map<String, Object> param = buildParameters(appName, service, method,
				dayStr, minuteFrom, minuteTo);

		String chartFullPath = methodMinuteChartService
				.drawElapsedMaxChart(param);
		ChartUtil.showChartToResponse(response, chartFullPath);
	}

	private Map<String, Object> buildParameters(String appName, String service,
			String method, String dayStr, String minuteFrom, String minuteTo) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("appName", appName);
		param.put("service", service);
		param.put("method", method);

		Date day = DateUtil.now();
		if (!StringUtil.isEmpty(dayStr) && !StringUtil.isNullStr(dayStr)) {
			day = DateUtil.parseWebDate(dayStr);
		}
		if (StringUtil.isEmpty(minuteFrom)) {
			minuteFrom = "0000";
		}
		param.put("time_from", DateUtil.changeHHmm(day, minuteFrom));
		if (StringUtil.isEmpty(minuteTo)) {
			minuteTo = "2359";
		}
		param.put("time_to", DateUtil.changeHHmm(day, minuteTo));
		
		DubboMethodDayIP dubboMethodDayIP=new DubboMethodDayIP();
		dubboMethodDayIP.setAppName(appName);
		param.put("time", day);
		dubboMethodDayIP.setTime(day);
		param.put("shardTableName", dubboMethodDayIP.getShardTableName());
		
		return param;
	}

}

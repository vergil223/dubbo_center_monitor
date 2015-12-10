package com.lvmama.soa.monitor.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lvmama.soa.monitor.constant.RestfulConst;
import com.lvmama.soa.monitor.entity.DubboAppMinute;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.web.vo.restful.Response;

@Controller
@RequestMapping("/list/provider/app")
public class ListAppStatisticController {
	private static final int PERIOD_MINUTE_FROM = 10;
	private static final int PERIOD_MINUTE_TO = 5;

	private static Log log = LogFactory
			.getLog(ListAppStatisticController.class);

	@Autowired
	private DubboAppMinuteService dubboAppMinuteService;

	@RequestMapping(value = "/{app}", method = RequestMethod.GET)
	@ResponseBody
	public String latestAppStatistic(@PathVariable String app) {
		Response response = new Response();
		try {
			Date now = DateUtil.now();
			Date timeFrom = DateUtil.minutesBefore(now, PERIOD_MINUTE_FROM);
			Date timeTo = DateUtil.minutesBefore(now, PERIOD_MINUTE_TO);

			DubboAppMinute tmp = getMergedAppMinute(app, timeFrom, timeTo);

			Map<String, String> result = new HashMap<String, String>();
			result.put("appName", app);
			result.put("timeFrom", DateUtil.yyyyMMddHHmmss(timeFrom));
			result.put("timeTo", DateUtil.yyyyMMddHHmmss(timeTo));
			result.put("successTimes", String.valueOf(tmp.getSuccessTimes()));
			result.put("failTimes", String.valueOf(tmp.getFailTimes()));
			result.put("elapsedAvg", String.valueOf(tmp.getElapsedAvg()));
			result.put("elapsedMax", String.valueOf(tmp.getElapsedMax()));

			response.setBody(result);
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		} catch (Exception e) {
			log.error("latestAppStatistic error for app:" + app, e);
			response.setStatus(RestfulConst.STATUS_FAIL);
		}

		return response.toJSON();
	}

	@RequestMapping(value = "/{app}/success", method = RequestMethod.GET)
	@ResponseBody
	public String latestAppSuccess(@PathVariable String app) {
		Response response = new Response();
		try {
			Date now = DateUtil.now();
			Date timeFrom = DateUtil.minutesBefore(now, PERIOD_MINUTE_FROM);
			Date timeTo = DateUtil.minutesBefore(now, PERIOD_MINUTE_TO);

			Map<String, String> result = new HashMap<String, String>();
			DubboAppMinute tmp = getMergedAppMinute(app, timeFrom, timeTo);
			result.put("successTimes", String.valueOf(tmp.getSuccessTimes()));
			response.setBody(result);
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		} catch (Exception e) {
			log.error("latestAppSuccess error for app:" + app, e);
			response.setStatus(RestfulConst.STATUS_FAIL);
		}

		return response.toJSON();
	}

	@RequestMapping(value = "/{app}/fail", method = RequestMethod.GET)
	@ResponseBody
	public String latestAppFail(@PathVariable String app) {
		Response response = new Response();
		try {
			Date now = DateUtil.now();
			Date timeFrom = DateUtil.minutesBefore(now, PERIOD_MINUTE_FROM);
			Date timeTo = DateUtil.minutesBefore(now, PERIOD_MINUTE_TO);

			Map<String, String> result = new HashMap<String, String>();
			DubboAppMinute tmp = getMergedAppMinute(app, timeFrom, timeTo);
			result.put("failTimes", String.valueOf(tmp.getFailTimes()));
			response.setBody(result);
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		} catch (Exception e) {
			log.error("latestAppFail error for app:" + app, e);
			response.setStatus(RestfulConst.STATUS_FAIL);
		}

		return response.toJSON();
	}

	@RequestMapping(value = "/{app}/elapsed_avg", method = RequestMethod.GET)
	@ResponseBody
	public String latestAppElapsedAvg(@PathVariable String app) {
		Response response = new Response();
		try {
			Date now = DateUtil.now();
			Date timeFrom = DateUtil.minutesBefore(now, PERIOD_MINUTE_FROM);
			Date timeTo = DateUtil.minutesBefore(now, PERIOD_MINUTE_TO);

			Map<String, String> result = new HashMap<String, String>();
			DubboAppMinute tmp = getMergedAppMinute(app, timeFrom, timeTo);
			result.put("elapsedAvg", String.valueOf(tmp.getElapsedAvg()));
			response.setBody(result);
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		} catch (Exception e) {
			log.error("latestAppElapsedAvg error for app:" + app, e);
			response.setStatus(RestfulConst.STATUS_FAIL);
		}

		return response.toJSON();
	}

	@RequestMapping(value = "/{app}/elapsed_max", method = RequestMethod.GET)
	@ResponseBody
	public String latestAppElapsedMax(@PathVariable String app) {
		Response response = new Response();
		try {
			Date now = DateUtil.now();
			Date timeFrom = DateUtil.minutesBefore(now, PERIOD_MINUTE_FROM);
			Date timeTo = DateUtil.minutesBefore(now, PERIOD_MINUTE_TO);

			Map<String, String> result = new HashMap<String, String>();
			DubboAppMinute tmp = getMergedAppMinute(app, timeFrom, timeTo);
			result.put("elapsedMax", String.valueOf(tmp.getElapsedMax()));
			response.setBody(result);
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		} catch (Exception e) {
			log.error("latestAppElapsedMax error for app:" + app, e);
			response.setStatus(RestfulConst.STATUS_FAIL);
		}

		return response.toJSON();
	}

	private DubboAppMinute getMergedAppMinute(String app, Date timeFrom,
			Date timeTo) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("appName", app);
		param.put("time_from", timeFrom);
		param.put("time_to", timeTo);

		List<DubboAppMinute> list = dubboAppMinuteService.selectList(param);
		DubboAppMinute tmp = new DubboAppMinute();
		for (DubboAppMinute minute : list) {
			if (tmp.getSuccessTimes() > 0 || minute.getSuccessTimes() > 0) {
				tmp.setElapsedAvg((tmp.getSuccessTimes() * tmp.getElapsedAvg() + minute
						.getSuccessTimes() * minute.getElapsedAvg())
						/ (tmp.getSuccessTimes() + minute.getSuccessTimes()));
			}
			tmp.setSuccessTimes(tmp.getSuccessTimes()
					+ minute.getSuccessTimes());
			tmp.setFailTimes(tmp.getFailTimes() + minute.getFailTimes());
			tmp.setElapsedMax(Math.max(tmp.getElapsedMax(),
					minute.getElapsedMax()));
		}
		return tmp;
	}
}

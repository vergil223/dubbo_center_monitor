package com.lvmama.soa.monitor.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lvmama.soa.monitor.constant.RestfulConst;
import com.lvmama.soa.monitor.dao.redis.JedisTemplate;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.web.vo.restful.Response;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
	private static Log log = LogFactory
			.getLog(MonitorController.class);

	@Autowired
	private DubboAppMinuteService dubboAppMinuteService;

	@RequestMapping(value = "/redisAlive", method = RequestMethod.GET)
	@ResponseBody
	public String latestAppStatistic() {
		Response response = new Response();
		try {
			JedisTemplate jedisReaderTemplate = JedisTemplate.getReaderInstance();
			jedisReaderTemplate.get("abc");
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		} catch (Exception e) {
			log.error("redisAlive error", e);
			response.setStatus(RestfulConst.STATUS_FAIL);
		}

		return response.toJSON();
	}
}

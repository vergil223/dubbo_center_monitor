package com.lvmama.soa.monitor.web.controller.alert;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lvmama.soa.monitor.constant.RestfulConst;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.web.vo.restful.Response;

@Controller
@RequestMapping("/alert")
public class AlertMsgController {
	private static final Log log = LogFactory.getLog(AlertMsgController.class);
	
	@Autowired
	private IAlertRecordService iAlertRecordService; 
	
	@RequestMapping("/getByTime/{yyyyMMddHHmmssFrom}/{yyyyMMddHHmmssTo}")
	@ResponseBody
	public String getAlertListByTime(@PathVariable String yyyyMMddHHmmssFrom, @PathVariable String yyyyMMddHHmmssTo){
		Response response=new Response();
		Map<String,Object> body=new HashMap<String,Object>();
		try{
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("insertTime_from", DateUtil.parse(yyyyMMddHHmmssFrom));
			params.put("insertTime_to", DateUtil.parse(yyyyMMddHHmmssTo));
		
			body.put("alertMsg", iAlertRecordService.selectList(params));
			
			response.setBody(body);
			
			response.setStatus(RestfulConst.STATUS_SUCCESS);
		}catch(Exception e){
			log.error("Error when getAlertListByTime, from:["+yyyyMMddHHmmssFrom+"] , to:["+yyyyMMddHHmmssTo+"]",e);
			response.setStatus(RestfulConst.STATUS_FAIL);
			response.setErrorMsg("Error when get alert messages by time.");
		}
		return response.toJSON();
		
	}
}

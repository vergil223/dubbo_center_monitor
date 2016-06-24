package com.lvmama.soa.monitor.web.controller.alert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lvmama.soa.monitor.constant.Enabled;
import com.lvmama.soa.monitor.constant.RestfulConst;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.service.alert.impl.AlertService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.web.vo.restful.Response;

@Controller
@RequestMapping("/alert")
public class AlertMsgController {
	private static final Log log = LogFactory.getLog(AlertMsgController.class);
	
	@Autowired
	private IAlertRecordService iAlertRecordService; 
	
	@Autowired
	private AlertService methodDayAlertService;
	
	@RequestMapping("/enableList")
	@ResponseBody
	public List<String> enableList(){
		List<String> enableList = Arrays.asList(Enabled.Y,Enabled.N);
		return enableList;
	}
	
	@RequestMapping("/listAlert")
	@ResponseBody
	public List<TAltAlert> listAlert(){
		List<TAltAlert> result = methodDayAlertService.findAllAlert();
		return result;
	}
	
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
	
	@RequestMapping("/queryAlertRecord/{yyyyMMddHHmmssFrom}/{yyyyMMddHHmmssTo}")
	@ResponseBody
	public List<TAltRecord> getAlertListByTime(@PathVariable String yyyyMMddHHmmssFrom, @PathVariable String yyyyMMddHHmmssTo,@RequestBody TAltRecord tAltRecordParam){
		try{
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("appName", tAltRecordParam.getAppName());
			params.put("service", tAltRecordParam.getService());
			params.put("method", tAltRecordParam.getMethod());
			params.put("insertTime_from", DateUtil.parse(yyyyMMddHHmmssFrom));
			params.put("insertTime_to", DateUtil.parse(yyyyMMddHHmmssTo));
		
			List<TAltRecord> result = iAlertRecordService.selectList(params);
			
			//TODO test
			TAltRecord testRecored=new TAltRecord();
			testRecored.setId_(-9L);
			testRecored.setAppName("TA");
			testRecored.setService("TS");
			testRecored.setMethod("TM");
			testRecored.setAlertMsg("Test message");
			result.add(testRecored);
			
			
			return result;
		}catch(Exception e){
			log.error("Error when getAlertListByTime,appName:["+tAltRecordParam.getService()+"], service:["+tAltRecordParam.getService()+"], method:["+tAltRecordParam.getMethod()+"] from:["+yyyyMMddHHmmssFrom+"] , to:["+yyyyMMddHHmmssTo+"]",e);
			return null;
		}
	}
	
	@RequestMapping("/count/{yyyyMMddHHmmssFrom}/{yyyyMMddHHmmssTo}")
	@ResponseBody
	public String countByTime(@PathVariable String yyyyMMddHHmmssFrom, @PathVariable String yyyyMMddHHmmssTo){
		Response response=new Response();
		Map<String,Object> body=new HashMap<String,Object>();
		try{
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("insertTime_from", DateUtil.parse(yyyyMMddHHmmssFrom));
			params.put("insertTime_to", DateUtil.parse(yyyyMMddHHmmssTo));
		
			body.put("alertMsg", iAlertRecordService.count(params));
			
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

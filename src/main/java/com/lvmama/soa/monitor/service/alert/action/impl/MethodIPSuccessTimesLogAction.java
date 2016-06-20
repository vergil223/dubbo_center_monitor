package com.lvmama.soa.monitor.service.alert.action.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;

public class MethodIPSuccessTimesLogAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(MethodIPSuccessTimesLogAction.class);
	
	@Override
	protected void doAction(Map<String, Object> param,
			Map<String, String> actionParam) {
		DubboMethodDayIP dubboMethodDayIP=(DubboMethodDayIP)param.get(AlertParamKey.DUBBO_METHOD_DAY_IP);
		if(dubboMethodDayIP==null){
			return;
		}
		
		String app=dubboMethodDayIP.getAppName();
		String service=dubboMethodDayIP.getService();
		String method=dubboMethodDayIP.getMethod();
		log.error("===============================================================================");
		log.error("[ALERT]["+actionParam.get("logParam")+"]　success times increased highly. APP:"+app+", method:"+service+"."+method);
		log.error("===============================================================================");
	}

}

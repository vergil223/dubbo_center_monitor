package com.lvmama.soa.monitor.service.alert.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.util.StringUtil;

@Service("methodDayIPAlertService")
public class DubboMethodDayIPAlertService extends AlertService{
	protected boolean isTarget(Map<String, Object> param, TAltAlert tAltAlert) {
		DubboMethodDayIP methodDayIp = (DubboMethodDayIP) param
				.get(AlertParamKey.DUBBO_METHOD_DAY_IP);
		String service = methodDayIp.getService();
		String method = methodDayIp.getMethod();

		String target = tAltAlert.getTarget();
		String targetExclude = tAltAlert.getTargetExclude();

		if(!StringUtil.isEmpty(targetExclude)){
			for (String curTargetExclude : targetExclude.split(",")) {
				if (StringUtil.match(service + "." + method, curTargetExclude)) {
					return false;
				}
			}			
		}

		if(!StringUtil.isEmpty(target)){
			for (String curTarget : target.split(",")) {
				if (StringUtil.match(service + "." + method, curTarget)) {
					return true;
				}
			}			
		}

		return false;
	}
}

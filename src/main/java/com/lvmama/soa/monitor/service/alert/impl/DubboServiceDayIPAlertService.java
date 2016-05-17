package com.lvmama.soa.monitor.service.alert.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.util.StringUtil;

@Service("dubboServiceDayIPAlertService")
public class DubboServiceDayIPAlertService extends AlertService{
	protected boolean isTarget(Map<String, Object> param, TAltAlert tAltAlert) {
		DubboServiceDayIP serviceDayIp = (DubboServiceDayIP) param
				.get(AlertParamKey.DUBBO_SERVICE_DAY_IP);
		String service = serviceDayIp.getService();

		String target = tAltAlert.getTarget();
		String targetExclude = tAltAlert.getTargetExclude();

		for (String curTargetExclude : targetExclude.split(",")) {
			if (StringUtil.match(service, curTargetExclude)) {
				return false;
			}
		}

		for (String curTarget : target.split(",")) {
			if (StringUtil.match(service, curTarget)) {
				return true;
			}
		}

		return false;
	}
}

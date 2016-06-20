package com.lvmama.soa.monitor.service.alert.action.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;

public class MethodSuccessTimesLogAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(MethodSuccessTimesLogAction.class);
	
	@Override
	protected void doAction(Map<String, Object> param,
			Map<String, String> actionParam) {
		log.error("===============================================================================");
		log.error("[ALERT]"+(String)param.get(AlertParamKey.ALERT_MSG));
		log.error("===============================================================================");
	}

}

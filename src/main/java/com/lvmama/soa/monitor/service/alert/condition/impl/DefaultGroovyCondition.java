package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service("defaultGroovyCondition")
public class DefaultGroovyCondition extends AbstractCondition {
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		// TODO Auto-generated method stub
		return false;
	}

}

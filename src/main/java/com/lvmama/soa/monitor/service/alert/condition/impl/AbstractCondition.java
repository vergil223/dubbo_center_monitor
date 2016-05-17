package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.service.alert.condition.ICondition;
import com.lvmama.soa.monitor.util.ParamUtil;

public abstract class AbstractCondition implements ICondition{
	private static final Log log = LogFactory.getLog(AbstractCondition.class);
	
	@Override
	public boolean match(Map<String, Object> param, String conditionParam) {
		try{
			boolean isMatch= doMatch(param, ParamUtil.convertParamToMap(conditionParam));
			return isMatch;
		}catch(Exception e){
			log.error("AbstractCondition.match error",e);
			return false;
		}
	}
	
	protected abstract boolean doMatch(Map<String, Object> param, Map<String, String> conditionParam);
}

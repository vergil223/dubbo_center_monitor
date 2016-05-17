package com.lvmama.soa.monitor.service.alert.condition;

import java.util.Map;

public interface ICondition {
	public boolean match(Map<String,Object> param,String conditionParam);
}

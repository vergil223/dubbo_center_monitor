package com.lvmama.soa.monitor.service.alert.action;

import java.util.Map;

public interface IAction {
	public void action(Map<String,Object> param, String actionParam);
}

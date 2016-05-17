package com.lvmama.soa.monitor.service.alert.action.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.service.alert.action.IAction;
import com.lvmama.soa.monitor.util.ParamUtil;

public abstract class AbstractAction implements IAction{
	private static final Log log = LogFactory.getLog(AbstractAction.class);
	
	public void action(Map<String,Object> param, String actionParam){
		try{
			doAction(param, ParamUtil.convertParamToMap(actionParam));
		}catch(Exception e){
			log.error("AbstractAction.action error",e);
		}
	}
	
	protected abstract void doAction(Map<String, Object> param, Map<String, String> actionParam);
}

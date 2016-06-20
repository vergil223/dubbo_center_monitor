package com.lvmama.soa.monitor.service.alert.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.constant.Enabled;
import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.constant.alert.ContentType;
import com.lvmama.soa.monitor.dao.mybatis.alert.TAltActionDao;
import com.lvmama.soa.monitor.dao.mybatis.alert.TAltAlertDao;
import com.lvmama.soa.monitor.dao.mybatis.alert.TAltConditionDao;
import com.lvmama.soa.monitor.entity.alert.TAltAction;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.entity.alert.TAltCondition;
import com.lvmama.soa.monitor.service.alert.IAlertService;
import com.lvmama.soa.monitor.service.alert.action.IAction;
import com.lvmama.soa.monitor.service.alert.condition.ICondition;

@Service
public abstract class AlertService implements IAlertService {
	private static final Log log = LogFactory.getLog(AlertService.class);

	@Autowired
	private TAltAlertDao tAltAlertDao;
	@Autowired
	private TAltConditionDao tAltConditionDao;
	@Autowired
	private TAltActionDao tAltActionDao;
	@Autowired
	private ICondition defaultGroovyCondition;
	@Autowired
	private IAction defaultGroovyAction;
	
	@Override
	public List<TAltAlert> findAllAlert(){
		return tAltAlertDao.findAllEnabledAlert();
	}
	
	@Override
	public void alert(Map<String, Object> param) {
		List<TAltAlert> tAltAlerts = tAltAlertDao.findAllEnabledAlert();
		for (TAltAlert tAltAlert : tAltAlerts) {
			try {
				if (isTarget(param, tAltAlert)) {
					List<TAltCondition> tAltConditions = getConditions(tAltAlert);
					List<TAltAction> tAltActions = getActions(tAltAlert);
					
					boolean meetCondition=true;
					for (int i = 0; i < tAltConditions.size(); i++) {
						TAltCondition curCondition = tAltConditions.get(i);
						if (!Enabled.Y.equals(curCondition.getEnabled())) {
							continue;
						}
						try {
							if (!doCondition(param, curCondition,
									tAltAlert.getConditionParam())) {
								//do not trigger action if one of the condition failed
								meetCondition=false;
								break;
							}
						} catch (Exception e) {
							log.error(
									"error when do condition, condition id:"
											+ curCondition.getId_(), e);
						}
					}
					
					if(!meetCondition){
						continue;
					}
					
					for (int i = 0; i < tAltActions.size(); i++) {
						TAltAction tAltAction = tAltActions.get(i);
						if (!Enabled.Y.equals(tAltAction.getEnabled())) {
							continue;
						}
						try {
							doAction(param, tAltAction,tAltAlert.getActionParam());
						} catch (Exception e) {
							log.error(
									"error when do action, action id:"
											+ tAltAction.getId_(), e);
						}
					}
				}
			} catch (Exception e) {
				log.error(
						"error when do alert, alert id:" + tAltAlert.getId_(),
						e);
			}

		}

	}

	private List<TAltCondition> getConditions(TAltAlert tAltAlert) {
		return tAltConditionDao.findByIds(tAltAlert.getConditionIds());
	}

	private List<TAltAction> getActions(TAltAlert tAltAlert) {
		return tAltActionDao.findByIds(tAltAlert.getActionIds());
	}
	
	abstract protected boolean isTarget(Map<String, Object> param, TAltAlert tAltAlert);

	protected boolean doCondition(Map<String, Object> param,
			TAltCondition tAltCondition, String conditionParam) {
		ICondition condition = createConditionInstance(tAltCondition);
		if (condition == null) {
			return false;
		}

		param.put(AlertParamKey.CONDITION_CONTENT, tAltCondition.getContent());
		return condition.match(param, conditionParam);
	}

	private ICondition createConditionInstance(TAltCondition tAltCondition) {
		if (tAltCondition == null) {
			return null;
		}

		try {
			if (ContentType.JAVA_CLASS.equals(tAltCondition.getContentType())) {
				String className = tAltCondition.getContent();
				Class conditionClass = Class.forName(className);
				return (ICondition) conditionClass.newInstance();
			} else if (ContentType.GROOVY
					.equals(tAltCondition.getContentType())) {
				String groovyScript = tAltCondition.getContent();
				return defaultGroovyCondition;
			} else {
				log.error("Unknow condition content type:["
						+ tAltCondition.getContentType() + "]");
				return null;
			}
		} catch (Exception e) {
			log.error("error when create condition instance.", e);
			return null;
		}

	}

	protected void doAction(Map<String, Object> param, TAltAction tAltAction,
			String actionParam) {
		IAction action = createActionInstance(tAltAction);
		if (action != null) {
			param.put(AlertParamKey.ACTION_CONTENT, tAltAction.getContent());
			action.action(param, actionParam);
		}
	}

	private IAction createActionInstance(TAltAction tAltAction) {
		if (tAltAction == null) {
			return null;
		}

		try {
			if (ContentType.JAVA_CLASS.equals(tAltAction.getContentType())) {
				String className = tAltAction.getContent();
				Class actionClass = Class.forName(className);
				return (IAction) actionClass.newInstance();
			} else if (ContentType.GROOVY.equals(tAltAction.getContentType())) {
				return defaultGroovyAction;
			} else {
				log.error("Unknow action content type:["
						+ tAltAction.getContentType() + "]");
				return null;
			}
		} catch (Exception e) {
			log.error("error when create action instance.", e);
			return null;
		}

	}
}

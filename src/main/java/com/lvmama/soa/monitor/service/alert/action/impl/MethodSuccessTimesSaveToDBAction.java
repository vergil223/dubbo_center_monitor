package com.lvmama.soa.monitor.service.alert.action.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.SpringUtil;

public class MethodSuccessTimesSaveToDBAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(MethodSuccessTimesSaveToDBAction.class);
	
	@Override
	protected void doAction(Map<String, Object> param,
			Map<String, String> actionParam) {
		DubboMethodDay dubboMethodDay=(DubboMethodDay)param.get(AlertParamKey.DUBBO_METHOD_DAY);
		if(dubboMethodDay==null){
			return;
		}
		
		IAlertRecordService iAlertRecordService=(IAlertRecordService)SpringUtil.getContext().getBean("alertRecordService");
		
		TAltRecord tAltRecord=new TAltRecord();
		tAltRecord.setAppName(dubboMethodDay.getAppName());
		tAltRecord.setService(dubboMethodDay.getService());
		tAltRecord.setMethod(dubboMethodDay.getMethod());
		tAltRecord.setInsertTime(DateUtil.now());
		List<String> alertMsgList=(List<String>)param.get(AlertParamKey.ALERT_MSG);
		if(alertMsgList!=null){
			for(String alertMsg:alertMsgList){
				tAltRecord.setAlertMsg(alertMsg);
				iAlertRecordService.insert(tAltRecord);							
			}
		}
	}

}

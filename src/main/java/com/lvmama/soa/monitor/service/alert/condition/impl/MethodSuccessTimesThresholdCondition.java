package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

/**
 * 和前10分钟的平均success times比较，如果数量增加了200%就触发警告
 * @author Administrator
 *
 */
public class MethodSuccessTimesThresholdCondition extends AbstractCondition {
	private static final long DEFAULT_THRESHOLD_SUCCESS_TIMES=1000;
	private static final int DEFAULT_MINUTE_SCOPE=5;
	
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		DubboMethodDay day=(DubboMethodDay)param.get(AlertParamKey.DUBBO_METHOD_DAY);
		if(day==null){
			return false;
		}
		
		long thresholdSuccessTimes=DEFAULT_THRESHOLD_SUCCESS_TIMES;
		String thresholdSuccessTimesStr=conditionParam.get("thresholdSuccessTimes");
		if(!StringUtil.isEmpty(thresholdSuccessTimesStr)){
			thresholdSuccessTimes=Long.valueOf(thresholdSuccessTimesStr);
		}
		
		int minuteScope=DEFAULT_MINUTE_SCOPE;
		String minuteScopeStr=conditionParam.get("minuteScope");
		if(!StringUtil.isEmpty(minuteScopeStr)){
			minuteScope=Integer.valueOf(minuteScopeStr);
		}
		
		List<List<String>> successTimesList=DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail());
		
		for(int i=0;i<minuteScope;i++){
			List<String> minAndTimes=successTimesList.get(successTimesList.size()-1-i);
			String hhmm=minAndTimes.get(0);		
			long successTimes=Long.parseLong(minAndTimes.get(1));
			
			if(successTimes>=thresholdSuccessTimes){
				List alertMsgList=(List)param.get(AlertParamKey.ALERT_MSG);
				if(alertMsgList==null){
					alertMsgList=new ArrayList<String>();
				}
				alertMsgList.add("The success times has exceeded threadshold value: ["+thresholdSuccessTimes+"] . App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], current time:["+DateUtil.changeHHmm(day.getTime(), hhmm)  +"], current successTimes:["+successTimes+"]");
				param.put(AlertParamKey.ALERT_MSG, alertMsgList);
				
				return true;
			}
		}
		
		return false;
	}
	
}

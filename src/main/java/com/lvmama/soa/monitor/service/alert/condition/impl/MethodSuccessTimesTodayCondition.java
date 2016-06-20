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
 * 
 * 另外，为了避免少量调用但比例提升很大的情况(1次到10次)，增加了一个阈值，只有同时超过比例,并且参与比较的两个success times都超过阈值才会触发报警
 * @author Administrator
 *
 */
public class MethodSuccessTimesTodayCondition extends AbstractCondition {
	private static final int DEFAULT_PERCENT_INCREASE=2;
	private static final int DEFAULT_MINUTES_TO_COMPARE=10;
	private static final long DEFAULT_THRESHOLD_SUCCESS_TIMES=100;
	
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		DubboMethodDay day=(DubboMethodDay)param.get(AlertParamKey.DUBBO_METHOD_DAY);
		if(day==null){
			return false;
		}
		
		int percentIncrease=DEFAULT_PERCENT_INCREASE;
		String percentIncreaseStr=conditionParam.get("percentIncrease");
		if(!StringUtil.isEmpty(percentIncreaseStr)){
			percentIncrease=Integer.parseInt(percentIncreaseStr);
		}
		
		int minutesToCompare=DEFAULT_MINUTES_TO_COMPARE;
		String minutesToCompareStr=conditionParam.get("minutesToCompare");
		if(!StringUtil.isEmpty(minutesToCompareStr)){
			minutesToCompare=Integer.parseInt(minutesToCompareStr);
		}
		
		long thresholdSuccessTimes=DEFAULT_THRESHOLD_SUCCESS_TIMES;
		String thresholdSuccessTimesStr=conditionParam.get("thresholdSuccessTimes");
		if(!StringUtil.isEmpty(thresholdSuccessTimesStr)){
			thresholdSuccessTimes=Long.parseLong(thresholdSuccessTimesStr);
		}
		
		List<List<String>> successTimesList=DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail());
		
		for(int i=1;i<=minutesToCompare&&successTimesList.size()-1-i>=0;i++){
			List<String> curHHmmAndSuccessTime=successTimesList.get(successTimesList.size()-i);
			List<String> lastHHmmAndSuccessTime=successTimesList.get(successTimesList.size()-i-1);
			
			String curHHmm =curHHmmAndSuccessTime.get(0);
			long curSuccessTimes=Long.valueOf(curHHmmAndSuccessTime.get(1));
			
			long lastSuccessTimes=Long.valueOf(lastHHmmAndSuccessTime.get(1));
			
			//whether success times increased too high
			if(lastSuccessTimes>0&&curSuccessTimes>=(1+percentIncrease)*lastSuccessTimes){
				if(lastSuccessTimes>=thresholdSuccessTimes){
					List alertMsgList=(List)param.get(AlertParamKey.ALERT_MSG);
					if(alertMsgList==null){
						alertMsgList=new ArrayList<String>();
					}
					alertMsgList.add("The success times is "+new BigDecimal(curSuccessTimes).divide(new BigDecimal(lastSuccessTimes), 1, BigDecimal.ROUND_HALF_UP)+" times bigger than last minute("+lastSuccessTimes+">"+curSuccessTimes+"). App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], current time:["+DateUtil.changeHHmm(day.getTime(), curHHmm)  +"], current successTimes:["+curSuccessTimes+"]");
					param.put(AlertParamKey.ALERT_MSG, alertMsgList);
					return true;					
				}
			}
			
			//whether success times decreased too low
			if(lastSuccessTimes>(1+percentIncrease)*curSuccessTimes){
				if(curSuccessTimes<=thresholdSuccessTimes&&lastSuccessTimes>=thresholdSuccessTimes){
					List alertMsgList=(List)param.get(AlertParamKey.ALERT_MSG);
					if(alertMsgList==null){
						alertMsgList=new ArrayList<String>();
					}
					
					BigDecimal divideResult=null;
					if(curSuccessTimes==0){
						divideResult=new BigDecimal(999);
					}else{
						divideResult = new BigDecimal(lastSuccessTimes).divide(new BigDecimal(curSuccessTimes), 1, BigDecimal.ROUND_HALF_UP);						
					}
					alertMsgList.add("The success times decreased "+divideResult+" times than last minute("+lastSuccessTimes+">"+curSuccessTimes+"). App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], current time:["+DateUtil.changeHHmm(day.getTime(), curHHmm)  +"], current successTimes:["+curSuccessTimes+"]");
					param.put(AlertParamKey.ALERT_MSG, alertMsgList);
					return true;					
				}
			}
		}
		
		return false;
	}
	
}

package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

/**
 * 和前10分钟的平均success times比较，如果数量增加了200%就触发警告
 * @author Administrator
 *
 */
public class MethodSuccessTimesCondition extends AbstractCondition {
	private static final int DEFAULT_PERCENT_INCREASE=2;
	private static final int DEFAULT_MINUTES_TO_COMPARE=10;
	
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		DubboMethodDayIP day=(DubboMethodDayIP)param.get(AlertParamKey.DUBBO_METHOD_DAY_IP);
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
		
		List<List<String>> successTimesList=DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail());
		
		List<String> latestMinAndTimes=successTimesList.get(successTimesList.size()-1);
		String latestHhmm=latestMinAndTimes.get(0);
		int latestMinuteOfDay=DateUtil.getMinuteOfDay(latestHhmm);
		long latestSuccessTimes=Long.parseLong(latestMinAndTimes.get(1));
		
		int count=0;
		long totalSuccessTimesForCompare=0;
		for(int i=successTimesList.size()-2;i>=0;i--){
			List<String> curMinAndTimes=successTimesList.get(i);
			int curMinuteOfDay=DateUtil.getMinuteOfDay(curMinAndTimes.get(0));
			if(curMinuteOfDay+minutesToCompare>=latestMinuteOfDay){
				count++;
				long curSuccessTimes=Long.parseLong(curMinAndTimes.get(1));
				totalSuccessTimesForCompare+=curSuccessTimes;
			}else{
				break;
			}
		}
		if(count>1){
			long averageSuccessTimesForCompare=totalSuccessTimesForCompare/count;
			if(latestSuccessTimes>=(1+percentIncrease)*averageSuccessTimesForCompare){
				return true;
			}
		}
		
		return false;
	}
	
	

}

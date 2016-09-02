package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

/**
 * 和前10分钟的平均执行时间比较，如果数量增加了200%就触发警告
 * 
 * @author Administrator
 *
 */
public class MethodElapsedAvgTodayCondition extends AbstractCondition {
	private static final int DEFAULT_PERCENT_INCREASE=2;
	private static final int DEFAULT_MINUTES_TO_COMPARE=10;
	
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		DubboMethodDay day=(DubboMethodDay)param.get(AlertParamKey.DUBBO_METHOD_DAY);
		if(day==null){
			return false;
		}
		
		BigDecimal percentIncrease=BigDecimal.valueOf(DEFAULT_PERCENT_INCREASE);
		String percentIncreaseStr=conditionParam.get("percentIncrease");
		if(!StringUtil.isEmpty(percentIncreaseStr)){
			percentIncrease=new BigDecimal(percentIncreaseStr);
		}
		
		int minutesToCompare=DEFAULT_MINUTES_TO_COMPARE;
		String minutesToCompareStr=conditionParam.get("minutesToCompare");
		if(!StringUtil.isEmpty(minutesToCompareStr)){
			minutesToCompare=Integer.parseInt(minutesToCompareStr);
		}
		
		List<List<String>> successTimesList=DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail());
		List<List<String>> elapsedTotalList=DubboDetailUtil.detailStrToList(day.getElapsedTotalDetail());
		
		for(int i=1;i<=minutesToCompare&&successTimesList.size()-1-i>=0;i++){
			List<String> curHHmmAndSuccessTime=successTimesList.get(successTimesList.size()-i);
			List<String> curHHmmAndElapsedTotal=elapsedTotalList.get(elapsedTotalList.size()-i);
			
			List<String> lastHHmmAndSuccessTime=successTimesList.get(successTimesList.size()-i-1);
			List<String> lastHHmmAndElapsedTotal=elapsedTotalList.get(elapsedTotalList.size()-i-1);
			
			
			String curHHmm =curHHmmAndSuccessTime.get(0);
			long curSuccessTimes=Long.valueOf(curHHmmAndSuccessTime.get(1));
			long curElapsedTotal=Long.valueOf(curHHmmAndElapsedTotal.get(1));
			long lastSuccessTimes=Long.valueOf(lastHHmmAndSuccessTime.get(1));
			long lastElapsedTotal=Long.valueOf(lastHHmmAndElapsedTotal.get(1));
			
			if(curSuccessTimes==0||curElapsedTotal==0||lastSuccessTimes==0||lastElapsedTotal==0){
				continue;
			}
			
			BigDecimal curElapsedAvg=new BigDecimal(curElapsedTotal).divide(new BigDecimal(curSuccessTimes), 4, BigDecimal.ROUND_HALF_UP);
			BigDecimal lastElapsedAvg=new BigDecimal(lastElapsedTotal).divide(new BigDecimal(lastSuccessTimes), 4, BigDecimal.ROUND_HALF_UP);
			
			//whether elapsed average increased too high
			if(BigDecimal.ONE.add(percentIncrease).multiply(lastElapsedAvg).compareTo(curElapsedAvg)<=0){
				List<String> alertMsgList=(List<String>)param.get(AlertParamKey.ALERT_MSG);
				if(alertMsgList==null){
					alertMsgList=new ArrayList<String>();
				}
				alertMsgList.add("The elapsed average is "+curElapsedAvg.divide(lastElapsedAvg, 2, BigDecimal.ROUND_HALF_UP)+" times bigger than last minute("+lastElapsedAvg+">"+curElapsedAvg+"). App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], current time:["+DateUtil.changeHHmm(day.getTime(), curHHmm)  +"], current elapsed average:["+curElapsedAvg+"]");
				param.put(AlertParamKey.ALERT_MSG, alertMsgList);
				return true;					
			}
		}
		
		return false;
	}
	
	public static void main(String args[]){
		DubboMethodDay day=new DubboMethodDay();
		day.setAppName("pet_public");
		day.setService("com.lvmama.comm.pet.service.seo.RecommendInfoService");
		day.setMethod("getRecommendInfoByParentBlockIdAndPageChannel");
		day.setSuccessTimesDetail("1550 1\n1551 6\n1552 60\n1553 21\n1554 1\n1555 1\n1556 1\n1557 28\n1558 0\n");
		day.setElapsedTotalDetail("1550 62\n1551 1540511\n1552 16190481\n1553 6083402\n1554 1383\n1555 212\n1556 131\n1557 5597867\n1558 0\n");
		day.setTime(DateUtil.now());
		
		Map<String, Object> param =new HashMap<String, Object>();
		param.put(AlertParamKey.DUBBO_METHOD_DAY, day);
		
		Map<String, String> conditionParam=new HashMap<String, String>(); 
		conditionParam.put("percentIncrease", "4");
		conditionParam.put("minutesToCompare", "10");
		
		boolean matched=new MethodElapsedAvgTodayCondition().doMatch(param, conditionParam);
		System.out.println(matched);
		System.out.println(matched);
	}
	
}

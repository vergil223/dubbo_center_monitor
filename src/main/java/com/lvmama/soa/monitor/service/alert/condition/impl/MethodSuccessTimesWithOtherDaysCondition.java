package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.service.DubboMethodDayService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.SpringUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

/**
 * 和前几天同期平均success times比较，如果数量增加了200%就触发警告
 * @author Administrator
 *
 */
public class MethodSuccessTimesWithOtherDaysCondition extends AbstractCondition {
	private static final BigDecimal DEFAULT_PERCENT_INCREASE=BigDecimal.valueOf(2);
	private static final int DEFAULT_DAYS_TO_COMPARE=1;
	private static final int DEFAULT_MIN_SCOPE=-5;
	private static final long DEFAULT_THRESHOLD_SUCCESS_TIMES=100;
	
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		DubboMethodDay day=(DubboMethodDay)param.get(AlertParamKey.DUBBO_METHOD_DAY);
		if(day==null){
			return false;
		}
		
		BigDecimal percentIncrease=DEFAULT_PERCENT_INCREASE;
		String percentIncreaseStr=conditionParam.get("percentIncrease");
		if(!StringUtil.isEmpty(percentIncreaseStr)){
			percentIncrease=new BigDecimal(percentIncreaseStr);
		}
		
		//建议设成1，避免对缓存和DB造成太大负担
		int daysToCompare=DEFAULT_DAYS_TO_COMPARE;
		String daysToCompareStr=conditionParam.get("daysToCompare");
		if(!StringUtil.isEmpty(daysToCompareStr)){
			daysToCompare=Integer.parseInt(daysToCompareStr);
		}
		
		int minScope=DEFAULT_MIN_SCOPE;
		String minScopeStr=conditionParam.get("minScope");
		if(!StringUtil.isEmpty(minScopeStr)){
			minScope=Integer.parseInt(minScopeStr);
		}
		
		long thresholdSuccessTimes=DEFAULT_THRESHOLD_SUCCESS_TIMES;
		String thresholdSuccessTimesStr=conditionParam.get("thresholdSuccessTimes");
		if(!StringUtil.isEmpty(thresholdSuccessTimesStr)){
			thresholdSuccessTimes=Long.parseLong(thresholdSuccessTimesStr);
		}
		
		
		List<List<String>> successTimesList=DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail());
		List<String> latestMinAndTimes=successTimesList.get(successTimesList.size()-1);
		String hhmmTo=latestMinAndTimes.get(0);
		String hhmmFrom=DateUtil.hhmmDiffMinInSameDay(hhmmTo, minScope);
		
		int curCount=0;
		long curTotalSuccessTimes=0;
		for(List<String> hhmmAndSuccessTimes:DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail(),hhmmFrom,hhmmTo)){
			curTotalSuccessTimes+=Long.parseLong(hhmmAndSuccessTimes.get(1));
			curCount++;
		}
		
		Map<String, Object> params = prepareParams(day);
		Date baseDate=day.getTime();
		int count=0;
		long totalSuccessTimes=0;
		DubboMethodDayService dubboMethodDayService=(DubboMethodDayService)SpringUtil.getContext().getBean("dubboMethodDayService");
		for(int i=1;i<=daysToCompare;i++){			
			params.put("time", DateUtil.trimToDay(DateUtil.daysBefore(baseDate,i)));
			List<DubboMethodDay> daysForCompare=dubboMethodDayService.selectList(params);
			for(DubboMethodDay dayForCompare:daysForCompare){
				List<List<String>> successTimesListForCompare=DubboDetailUtil.detailStrToList(dayForCompare.getSuccessTimesDetail(),hhmmFrom,hhmmTo);
				for(List<String> hhmmAndSuccessTimes:successTimesListForCompare){
					totalSuccessTimes+=Long.parseLong(hhmmAndSuccessTimes.get(1));
					count++;
				}
			}
		}
		if(count>0&&curCount>0){
			long avgSuccessTimesCur=curTotalSuccessTimes/curCount;
			long avgSuccessTimesForCompare=totalSuccessTimes/count;
			
			if(percentIncrease.add(BigDecimal.ONE).multiply(BigDecimal.valueOf(avgSuccessTimesForCompare)).compareTo(BigDecimal.valueOf(avgSuccessTimesCur))<0){
				if(avgSuccessTimesForCompare>thresholdSuccessTimes){
					List alertMsgList=(List)param.get(AlertParamKey.ALERT_MSG);
					if(alertMsgList==null){
						alertMsgList=new ArrayList<String>();
					}
					alertMsgList.add("The success times is "+percentIncrease+" times bigger than the average data of the same minute in pervious "+daysToCompare+" days. App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], current time:["+DateUtil.changeHHmm(baseDate, hhmmFrom)  +"], current successTimes:["+avgSuccessTimesCur+"]");
					param.put(AlertParamKey.ALERT_MSG, alertMsgList);
					
					return true;									
				}
			}
		}
		
		return false;
	}

	private Map<String, Object> prepareParams(DubboMethodDay day) {
		Map<String,Object> params=new HashMap<String,Object>();

		params.put("appName", day.getAppName());
		params.put("service", day.getService());
		params.put("method", day.getMethod());
		return params;
	}
	
}

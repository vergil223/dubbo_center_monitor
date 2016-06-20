package com.lvmama.soa.monitor.service.alert.condition.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.SpringUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

/**
 * 和前几天同期平均success times比较，如果数量增加了200%就触发警告
 * 
 * 会导致大量mysql读取，或者消耗比较大的缓存空间，谨慎开启。尽量对部分服务使用
 * 
 * @author Administrator
 *
 */
public class MethodIPSuccessTimesWithOtherDaysCondition extends AbstractCondition {
	private static final BigDecimal DEFAULT_PERCENT_INCREASE=BigDecimal.valueOf(2);
	private static final int DEFAULT_DAYS_TO_COMPARE=1;
	private static final int DEFAULT_MIN_SCOPE=-5;
	
	@Override
	protected boolean doMatch(Map<String, Object> param,
			Map<String, String> conditionParam) {
		DubboMethodDayIP day=(DubboMethodDayIP)param.get(AlertParamKey.DUBBO_METHOD_DAY_IP);
		if(day==null){
			return false;
		}
		
		BigDecimal percentIncrease=DEFAULT_PERCENT_INCREASE;
		String percentIncreaseStr=conditionParam.get("percentIncrease");
		if(!StringUtil.isEmpty(percentIncreaseStr)){
			percentIncrease=new BigDecimal(percentIncreaseStr);
		}
		
		//daysToCompare这个参数需要读取前几天的method day ip详细数据，不要设置过大，推荐用1天。
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
		DubboMethodDayIPService dubboMethodDayIPService=(DubboMethodDayIPService)SpringUtil.getContext().getBean("dubboMethodDayIPService");
		for(int i=1;i<=daysToCompare;i++){			
			params.put("time", DateUtil.trimToDay(DateUtil.daysBefore(baseDate,i)));
			List<DubboMethodDayIP> daysForCompare=dubboMethodDayIPService.selectList(params);
			for(DubboMethodDayIP dayForCompare:daysForCompare){
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
			
			if(percentIncrease.add(BigDecimal.ONE).multiply(BigDecimal.valueOf(avgSuccessTimesForCompare)).compareTo(BigDecimal.valueOf(avgSuccessTimesCur))<=0){
				param.put(AlertParamKey.ALERT_MSG, "The success times is "+percentIncrease+" times bigger than the average data of the same minute in pervious "+daysToCompare+" days. App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], providerIP:["+day.getProviderIP()+"], consumerIP:["+day.getConsumerIP()+"] current time:["+DateUtil.changeHHmm(baseDate, hhmmFrom)  +"], current successTimes:["+avgSuccessTimesCur+"]");
				return true;				
			}
		}
		
		return false;
	}

	private Map<String, Object> prepareParams(DubboMethodDayIP day) {
		Map<String,Object> params=new HashMap<String,Object>();

		params.put("appName", day.getAppName());
		params.put("service", day.getService());
		params.put("method", day.getMethod());
		params.put("consumerIP", day.getConsumerIP());
		params.put("providerIP", day.getProviderIP());
		return params;
	}
	
}

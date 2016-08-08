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
 * 和前几天平均执行时间比较，如果数量增加了200%就触发警告
 * @author Administrator
 *
 */
public class MethodElapsedAvgWithOtherDaysCondition extends AbstractCondition {
	private static final BigDecimal DEFAULT_PERCENT_INCREASE=BigDecimal.valueOf(2);
	private static final int DEFAULT_DAYS_TO_COMPARE=1;
	private static final int DEFAULT_MIN_SCOPE=-5;
	
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
		
		List<List<String>> elapsedTotalList=DubboDetailUtil.detailStrToList(day.getElapsedTotalDetail());
		List<String> latestelapsedTotalAndTimes=elapsedTotalList.get(elapsedTotalList.size()-1);
		String hhmmTo=latestelapsedTotalAndTimes.get(0);
		String hhmmFrom=DateUtil.hhmmDiffMinInSameDay(hhmmTo, minScope);
		
		long curTotalSuccessTimes=0;
		long curElapsedTotal=0;
		
		for(List<String> hhmmAndSuccessTimes:DubboDetailUtil.detailStrToList(day.getSuccessTimesDetail(),hhmmFrom,hhmmTo)){
			curTotalSuccessTimes+=Long.parseLong(hhmmAndSuccessTimes.get(1));
		}
		
		for(List<String> hhmmAndElapsedTotal:DubboDetailUtil.detailStrToList(day.getElapsedTotalDetail(),hhmmFrom,hhmmTo)){
			curElapsedTotal+=Long.parseLong(hhmmAndElapsedTotal.get(1));
		}
		
		if(curTotalSuccessTimes==0||curElapsedTotal==0){
			//如果当前没有调用就直接返回
			return false;
		}
		
		Map<String, Object> params = prepareParams(day);
		Date baseDate=day.getTime();
		long totalSuccessTimesForCompare=0;
		long elapsedTotalForCompare=0;
		DubboMethodDayService dubboMethodDayService=(DubboMethodDayService)SpringUtil.getContext().getBean("dubboMethodDayService");
		for(int i=1;i<=daysToCompare;i++){
			params.put("time", DateUtil.trimToDay(DateUtil.daysBefore(baseDate,i)));
			List<DubboMethodDay> daysForCompare=dubboMethodDayService.selectList(params);
			for(DubboMethodDay dayForCompare:daysForCompare){
				List<List<String>> successTimesListForCompare=DubboDetailUtil.detailStrToList(dayForCompare.getSuccessTimesDetail(),hhmmFrom,hhmmTo);
				List<List<String>> elapsedTotalListForCompare=DubboDetailUtil.detailStrToList(dayForCompare.getElapsedTotalDetail(),hhmmFrom,hhmmTo);
				for(List<String> hhmmAndSuccessTimes:successTimesListForCompare){
					totalSuccessTimesForCompare+=Long.parseLong(hhmmAndSuccessTimes.get(1));
				}
				for(List<String> hhmmAndElapsedTotal:elapsedTotalListForCompare){
					elapsedTotalForCompare+=Long.parseLong(hhmmAndElapsedTotal.get(1));
				}
			}
		}
		
		if(totalSuccessTimesForCompare==0||elapsedTotalForCompare==0){
			//如果需要比较的时间段没有调用就直接返回
			return false;
		}
		
		BigDecimal elapsedAvgCur=new BigDecimal(curElapsedTotal).divide(new BigDecimal(curTotalSuccessTimes), 4, BigDecimal.ROUND_HALF_UP);
		BigDecimal elapsedAvgForCompare=new BigDecimal(elapsedTotalForCompare).divide(new BigDecimal(totalSuccessTimesForCompare), 4, BigDecimal.ROUND_HALF_UP);
			
		if(percentIncrease.add(BigDecimal.ONE).multiply(elapsedAvgForCompare).compareTo(elapsedAvgCur)<0){
			if(elapsedAvgForCompare.compareTo(BigDecimal.ZERO)>0){
				List<String> alertMsgList=(List<String>)param.get(AlertParamKey.ALERT_MSG);
				if(alertMsgList==null){
					alertMsgList=new ArrayList<String>();
				}
				
				BigDecimal divideResult=null;
				if(elapsedAvgForCompare.compareTo(BigDecimal.ZERO)==0){
					divideResult=new BigDecimal(999);
				}else{
					divideResult = elapsedAvgCur.divide(elapsedAvgForCompare, 2, BigDecimal.ROUND_HALF_UP);						
				}
				
				alertMsgList.add("The elapsed average is "+divideResult+" times bigger than the average data of the same minute in pervious "+daysToCompare+" days ("+elapsedAvgForCompare+">"+elapsedAvgCur+"). App:["+day.getAppName()+"], service:["+day.getService()+"], method:["+day.getMethod()+"], current time:["+DateUtil.changeHHmm(baseDate, hhmmFrom)  +"], current elapsed average:["+elapsedAvgCur+"]");
				param.put(AlertParamKey.ALERT_MSG, alertMsgList);
				
				return true;									
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

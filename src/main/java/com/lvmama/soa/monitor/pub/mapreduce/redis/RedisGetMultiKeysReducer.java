package com.lvmama.soa.monitor.pub.mapreduce.redis;

import java.util.HashMap;
import java.util.Map;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.pub.mapreduce.Context;
import com.lvmama.soa.monitor.pub.mapreduce.Reducer;

public class RedisGetMultiKeysReducer implements Reducer<String,DubboServiceDayIP,Map<String,DubboServiceDayIP>>{
//	private static final ThreadLocal<Map<String,DubboServiceDayIP>> reduceResult=new ThreadLocal<Map<String,DubboServiceDayIP>>();
//	static{
//		reduceResult.set(new HashMap<String,DubboServiceDayIP>());
//	} 
	
	private final Map<String,DubboServiceDayIP> reduceResult=new HashMap<String,DubboServiceDayIP>();
	
	@Override
	public void reduce(String service, DubboServiceDayIP day, Context<String,DubboServiceDayIP,Map<String,DubboServiceDayIP>> context) {
		DubboServiceDayIP mergedDay=reduceResult.get(service);
		if(mergedDay==null){
			reduceResult.put(service, day);
		}else{
			mergedDay=DubboServiceDayIP.merge(day, mergedDay,false);
			reduceResult.put(service, mergedDay);
		}
	}
	
	public Map<String,DubboServiceDayIP> getReduceResult(){
		return reduceResult;
	}

}

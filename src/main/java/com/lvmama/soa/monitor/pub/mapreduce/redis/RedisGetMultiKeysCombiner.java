package com.lvmama.soa.monitor.pub.mapreduce.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.pub.mapreduce.Combiner;

public class RedisGetMultiKeysCombiner implements Combiner<Map<String,DubboServiceDayIP>,List<DubboServiceDayIP>>{

	@Override
	public List<DubboServiceDayIP> combine(
			List<Map<String, DubboServiceDayIP>> reduceResult) {
		Map<String, DubboServiceDayIP> resultMap=new HashMap<String, DubboServiceDayIP>();
		for(Map<String, DubboServiceDayIP> map:reduceResult){
			for(Entry<String,DubboServiceDayIP> entry:map.entrySet()){
				String service=entry.getKey();
				DubboServiceDayIP day=entry.getValue();
				if(resultMap.get(service)==null){
					resultMap.put(service, day);
				}else{
					DubboServiceDayIP oldDay=resultMap.get(service);
					resultMap.put(service, DubboServiceDayIP.merge(day, oldDay));
				}
			}
		}
		
		List<DubboServiceDayIP> resultList=new ArrayList<DubboServiceDayIP>();
		resultList.addAll(resultMap.values());
		return resultList;
	}

}

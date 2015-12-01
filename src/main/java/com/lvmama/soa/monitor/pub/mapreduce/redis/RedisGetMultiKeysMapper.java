package com.lvmama.soa.monitor.pub.mapreduce.redis;

import java.util.HashMap;
import java.util.Map;

import com.lvmama.soa.monitor.dao.redis.JedisTemplate;
import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.pub.mapreduce.Context;
import com.lvmama.soa.monitor.pub.mapreduce.Mapper;

public class RedisGetMultiKeysMapper implements Mapper<String,String,String,DubboServiceDayIP,Map<String,DubboServiceDayIP>>{
	private static final JedisTemplate jedisReaderTemplate = JedisTemplate.getReaderInstance();
	
//	private static final ThreadLocal<Map<String,DubboServiceDayIP>> mapResult=new ThreadLocal<Map<String,DubboServiceDayIP>>();
//	static{
//		mapResult.set(new HashMap<String,DubboServiceDayIP>());
//	}
	
	private final Map<String,DubboServiceDayIP> mapResult=new HashMap<String,DubboServiceDayIP>();
	@Override
	public void map(String key, String value, Context<String,DubboServiceDayIP,Map<String,DubboServiceDayIP>> context) {
			DubboServiceDayIP day = jedisReaderTemplate.get(key, DubboServiceDayIP.class);
			
			synchronized(mapResult){
				DubboServiceDayIP mergedDay=mapResult.get(day.getService());
				if(mergedDay==null){
					mergedDay=day;
				}else{
					mergedDay=DubboServiceDayIP.merge(day, mergedDay,false);
				}
				
				mapResult.put(mergedDay.getService(), mergedDay);							
			}
		}
	@Override
	public Map<String,DubboServiceDayIP> getMapResult() {
		return mapResult; 
	}
}



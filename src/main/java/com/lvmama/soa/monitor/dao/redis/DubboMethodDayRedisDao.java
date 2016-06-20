package com.lvmama.soa.monitor.dao.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;

@Repository("dubboMethodDayRedisDao")
public class DubboMethodDayRedisDao {
	private static final String TTL_KEY = "com.lvmama.soa.monitor.entity.DubboMethodDay.ttl_seconds";
	private static final JedisTemplate jedisReaderTemplate = JedisTemplate.getReaderInstance();
    private static final JedisTemplate jedisWriterTemplate = JedisTemplate.getWriterInstance();
    
	public int insert(DubboMethodDay day) {
		if (day == null) {
			return 0;
		}
		if(jedisWriterTemplate.get(getKey(day))!=null){
			return 0;
		}
		jedisWriterTemplate.set(getKey(day), day, ttl());
		return 1;
	}
	
	public int update(DubboMethodDay day) {
		if (day == null) {
			return 0;
		}
		jedisWriterTemplate.set(getKey(day), day, ttl());
		return 1;
	}
	
	public DubboMethodDay findOne(DubboMethodDay day) {
		if (day == null) {
			return null;
		}
		Object obj= jedisReaderTemplate.get(getKey(day), DubboMethodDay.class);
		if(obj!=null){
			return (DubboMethodDay)obj;
		}else{
			return null;
		}
		
	}
	
	public String getKey(DubboMethodDay day){
		return DateUtil.yyyyMMdd(day.getTime())+"_"+day.uniqueKey();
	}
	
	private Integer ttl() {
		return Integer.valueOf(PropertyUtil.getProperty(TTL_KEY, "0"));
	}
	
	public Set<String> getKeysByDate(String yyyyMMDD){
		return jedisReaderTemplate.keys(yyyyMMDD+"_com.lvmama.soa.monitor.entity.DubboMethodDay_*");
	}
	
	public DubboMethodDay getByKey(String key){
		return jedisReaderTemplate.get(key, DubboMethodDay.class);
	}
	
	public void deleteByKeys(Set<String> keys){
		String[] keyArray=new String[keys.size()];
		int i=0;
		for(String key:keys){
			keyArray[i++]=key;
		}
		jedisWriterTemplate.del(keyArray);
	}
	
	public void deleteByKey(String key){
		jedisWriterTemplate.del(key);
	}
	
	public List<DubboMethodDay> getMergedListByAppNameAndDay(String appName,String serviceName,String yyyyMMdd){
		String keyPattern=yyyyMMdd+"_"+"com.lvmama.soa.monitor.entity.DubboMethodDay_"+appName+"_"+serviceName+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		Map<String,DubboMethodDay> map=new HashMap<String,DubboMethodDay>();
		for(String key:keys){
			DubboMethodDay day = jedisReaderTemplate.get(key, DubboMethodDay.class);
			
			DubboMethodDay mergedDay=map.get(day.getMethod());
			if(mergedDay==null){
				mergedDay=day;
			}else{
				mergedDay=DubboMethodDay.merge(day, mergedDay,false);
			}
			
			map.put(mergedDay.getMethod(), mergedDay);			
		}
		
		List<DubboMethodDay> resultList=new ArrayList<DubboMethodDay>();
		for(Entry<String,DubboMethodDay> entry:map.entrySet()){
			resultList.add(entry.getValue());
		}
		
		return resultList;
	}
	
	public List<DubboMethodDay> selectList(Map<String,Object> params){
		Assert.notEmpty(params.get("appName"), "appName");
		Assert.notEmpty(params.get("service"), "service");
		Assert.notEmpty(params.get("method"), "method");
		Assert.notEmpty(params.get("time"), "time");
		
		String keyPattern=DateUtil.yyyyMMdd((Date)params.get("time"))+"_"+"com.lvmama.soa.monitor.entity.DubboMethodDay_"+params.get("appName").toString()+"_"+params.get("service").toString()+"_"+params.get("method").toString()+"_*";
		
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		List<DubboMethodDay> resultList=new ArrayList<DubboMethodDay>();
		for(String key:keys){
			DubboMethodDay day = jedisReaderTemplate.get(key, DubboMethodDay.class);
			
			resultList.add(day);
		}
		
		return resultList;
	}
	
	public List<DubboMethodDay> selectByMethod(String appName,String serviceName,String method,String yyyyMMdd){
		Assert.notEmpty(appName, "appName");
		Assert.notEmpty(serviceName, "service");
		Assert.notEmpty(method, "method");
		Assert.notEmpty(yyyyMMdd, "time");
		
		String keyPattern=yyyyMMdd+"_"+"com.lvmama.soa.monitor.entity.DubboMethodDay_"+appName+"_"+serviceName+"_"+method+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		List<DubboMethodDay> resultList=new ArrayList<DubboMethodDay>();
		for(String key:keys){
			DubboMethodDay day = jedisReaderTemplate.get(key, DubboMethodDay.class);
			
			resultList.add(day);
		}
		
		return resultList;
	}
	
}

package com.lvmama.soa.monitor.dao.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Repository("dubboMethodDayIPRedisDao")
public class DubboMethodDayIPRedisDao {
	private static final String TTL_KEY = "com.lvmama.soa.monitor.entity.DubboMethodDayIP.ttl_seconds";
	private static final JedisTemplate jedisReaderTemplate = JedisTemplate.getReaderInstance();
    private static final JedisTemplate jedisWriterTemplate = JedisTemplate.getWriterInstance();
    
	public int insert(DubboMethodDayIP day) {
		if (day == null) {
			return 0;
		}
		if(jedisWriterTemplate.get(getKey(day))!=null){
			return 0;
		}
		jedisWriterTemplate.set(getKey(day), day, ttl());
		return 1;
	}
	
	public int update(DubboMethodDayIP day) {
		if (day == null) {
			return 0;
		}
		jedisWriterTemplate.set(getKey(day), day, ttl());
		return 1;
	}
	
	public DubboMethodDayIP findOne(DubboMethodDayIP day) {
		if (day == null) {
			return null;
		}
		Object obj= jedisReaderTemplate.get(getKey(day), DubboMethodDayIP.class);
		if(obj!=null){
			return (DubboMethodDayIP)obj;
		}else{
			return null;
		}
		
	}
	
	public String getKey(DubboMethodDayIP day){
		return DateUtil.yyyyMMdd(day.getTime())+"_"+day.uniqueKey();
	}
	
	private Integer ttl() {
		return Integer.valueOf(PropertyUtil.getProperty(TTL_KEY, "0"));
	}
	
	public Set<String> getKeysByDate(String yyyyMMDD){
		return jedisReaderTemplate.keys(yyyyMMDD+"_com.lvmama.soa.monitor.entity.DubboMethodDayIP_*");
	}
	
	public DubboMethodDayIP getByKey(String key){
		return jedisReaderTemplate.get(key, DubboMethodDayIP.class);
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
	
	public List<DubboMethodDayIP> getMergedListByAppNameAndDay(String appName,String serviceName,String yyyyMMdd){
		String keyPattern=yyyyMMdd+"_"+"com.lvmama.soa.monitor.entity.DubboMethodDayIP_"+appName+"_"+serviceName+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		Map<String,DubboMethodDayIP> map=new HashMap<String,DubboMethodDayIP>();
		for(String key:keys){
			DubboMethodDayIP day = jedisReaderTemplate.get(key, DubboMethodDayIP.class);
			
			DubboMethodDayIP mergedDay=map.get(day.getMethod());
			if(mergedDay==null){
				mergedDay=day;
			}else{
				mergedDay=DubboMethodDayIP.merge(day, mergedDay,false);
			}
			
			map.put(mergedDay.getMethod(), mergedDay);			
		}
		
		List<DubboMethodDayIP> resultList=new ArrayList<DubboMethodDayIP>();
		for(Entry<String,DubboMethodDayIP> entry:map.entrySet()){
			resultList.add(entry.getValue());
		}
		
		return resultList;
	}
	
	public List<DubboMethodDayIP> selectList(Map<String,Object> params){
		Assert.notEmpty(params.get("appName"), "appName");
		Assert.notEmpty(params.get("service"), "service");
		Assert.notEmpty(params.get("method"), "method");
		Assert.notEmpty(params.get("time"), "time");
		
		String providerIP=params.get("providerIP")==null?"*":params.get("providerIP").toString();
		String consumerIP=params.get("consumerIP")==null?"*":params.get("consumerIP").toString();
		
		String keyPattern=DateUtil.yyyyMMdd((Date)params.get("time"))+"_"+"com.lvmama.soa.monitor.entity.DubboMethodDayIP_"+params.get("appName").toString()+"_"+params.get("service").toString()+"_"+params.get("method").toString()+"_"+consumerIP+"_"+providerIP+"_*";
		
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		List<DubboMethodDayIP> resultList=new ArrayList<DubboMethodDayIP>();
		for(String key:keys){
			DubboMethodDayIP day = jedisReaderTemplate.get(key, DubboMethodDayIP.class);
			
			resultList.add(day);
		}
		
		return resultList;
	}
	
	public List<DubboMethodDayIP> selectByMethod(String appName,String serviceName,String method,String yyyyMMdd){
		Assert.notEmpty(appName, "appName");
		Assert.notEmpty(serviceName, "service");
		Assert.notEmpty(method, "method");
		Assert.notEmpty(yyyyMMdd, "time");
		
		String keyPattern=yyyyMMdd+"_"+"com.lvmama.soa.monitor.entity.DubboMethodDayIP_"+appName+"_"+serviceName+"_"+method+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		List<DubboMethodDayIP> resultList=new ArrayList<DubboMethodDayIP>();
		for(String key:keys){
			DubboMethodDayIP day = jedisReaderTemplate.get(key, DubboMethodDayIP.class);
			
			resultList.add(day);
		}
		
		return resultList;
	}
	
}

package com.lvmama.soa.monitor.dao.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Repository("dubboServiceDayIPRedisDao")
public class DubboServiceDayIPRedisDao {
	private static final String TTL_KEY = "com.lvmama.soa.monitor.entity.DubboServiceDayIP.ttl_seconds";
	private static final JedisTemplate jedisReaderTemplate = JedisTemplate.getReaderInstance();
    private static final JedisTemplate jedisWriterTemplate = JedisTemplate.getWriterInstance();
    
	public int insert(DubboServiceDayIP day) {
		if (day == null) {
			return 0;
		}
		if(jedisWriterTemplate.get(getKey(day))!=null){
			return 0;
		}
		jedisWriterTemplate.set(getKey(day), day, ttl());
		return 1;
	}
	
	public int update(DubboServiceDayIP day) {
		if (day == null) {
			return 0;
		}
		jedisWriterTemplate.set(getKey(day), day, ttl());
		return 1;
	}
	
	public DubboServiceDayIP findOne(DubboServiceDayIP day) {
		if (day == null) {
			return null;
		}
		Object obj= jedisReaderTemplate.get(getKey(day), DubboServiceDayIP.class);
		if(obj!=null){
			return (DubboServiceDayIP)obj;
		}else{
			return null;
		}
		
	}
	
	public List<DubboServiceDayIP> selectList(Map<String, Object> param) {
		Assert.notEmpty(param.get("service"), "service");
		Assert.notNull(param.get("time_from"), "time_from");
		
		String appName=null;
		if(appName==null||StringUtil.isNullStr(appName)||StringUtil.isEmpty(param.get("appName").toString())){
			appName="*";
		}else{
			appName=param.get("appName").toString();			
		}
		String service=param.get("service").toString();
		Date timeFrom=(Date)param.get("time_from");
		
		String keyPattern=DateUtil.yyyyMMdd(timeFrom)+"_"+"com.lvmama.soa.monitor.entity.DubboServiceDayIP_"+appName+"_"+service+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		List<DubboServiceDayIP> resultList=new ArrayList<DubboServiceDayIP>();
		for(String key:keys){
			DubboServiceDayIP day = jedisReaderTemplate.get(key, DubboServiceDayIP.class);
			resultList.add(day);			
		}
		return resultList;
	}
	
	public String getKey(DubboServiceDayIP day){
		return DateUtil.yyyyMMdd(day.getTime())+"_"+day.uniqueKey();
	}
	
	private Integer ttl() {
		return Integer.valueOf(PropertyUtil.getProperty(TTL_KEY, "0"));
	}
	
	public List<DubboServiceDayIP> getMergedListByAppNameAndDay(String appName,
			String yyyyMMDD){
		String keyPattern=yyyyMMDD+"_"+"com.lvmama.soa.monitor.entity.DubboServiceDayIP_"+appName+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		
		Map<String,DubboServiceDayIP> map=new HashMap<String,DubboServiceDayIP>();
		for(String key:keys){
			DubboServiceDayIP day = jedisReaderTemplate.get(key, DubboServiceDayIP.class);
			
			DubboServiceDayIP mergedDay=map.get(day.getService());
			if(mergedDay==null){
				mergedDay=day;
			}else{
				mergedDay=DubboServiceDayIP.merge(day, mergedDay);
			}
			
			map.put(mergedDay.getService(), mergedDay);			
		}
		
		List<DubboServiceDayIP> resultList=new ArrayList<DubboServiceDayIP>();
		for(Entry<String,DubboServiceDayIP> entry:map.entrySet()){
			resultList.add(entry.getValue());
		}
		
		return resultList;
	}
	
	public Set<String> getKeysByDate(String yyyyMMDD){
		return jedisReaderTemplate.keys(yyyyMMDD+"_com.lvmama.soa.monitor.entity.DubboServiceDayIP_*");
	}
	
	public DubboServiceDayIP getByKey(String key){
		return jedisReaderTemplate.get(key, DubboServiceDayIP.class);
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

}

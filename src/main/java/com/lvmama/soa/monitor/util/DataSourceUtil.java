package com.lvmama.soa.monitor.util;


public class DataSourceUtil {
	private static final String REDIS_PROP_SURFIX=".ttl_seconds";
	public static boolean canReadFromRedis(Class clazz,String yyyyMMDD){
//		String ttl=PropertyUtil.getProperty(clazz.getName()+REDIS_PROP_SURFIX,"0");
//		
//		long deleteTime=DateUtil.parseDateYYYYMMdd(yyyyMMDD).getTime()+ Integer.valueOf(ttl)*1000;
//		
//		if(deleteTime>DateUtil.now().getTime()){
//			return true;
//		}
//		return false;
		
		//because the total data can be stored in Redis 2.X version is limited by the memory, we should set a shorter TTL such as 1 day.  
		if(StringUtil.isEmpty(yyyyMMDD)){
			return false;
		}
		if(DateUtil.yyyyMMdd(DateUtil.now()).compareTo(yyyyMMDD)>0){
			return false;
		}
		return true;
	}
}

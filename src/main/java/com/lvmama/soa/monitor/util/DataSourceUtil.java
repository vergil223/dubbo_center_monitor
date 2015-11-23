package com.lvmama.soa.monitor.util;


public class DataSourceUtil {
	private static final String REDIS_PROP_SURFIX=".ttl_seconds";
	public static boolean canReadFromRedis(Class clazz,String yyyyMMDD){
		String ttl=PropertyUtil.getProperty(clazz.getName()+REDIS_PROP_SURFIX,"0");
		
		long deleteTime=DateUtil.parseDateYYYYMMdd(yyyyMMDD).getTime()+ Integer.valueOf(ttl)*1000;
		
		if(deleteTime>DateUtil.now().getTime()){
			return true;
		}
		return false;
	}
}

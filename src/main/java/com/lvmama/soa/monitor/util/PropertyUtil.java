package com.lvmama.soa.monitor.util;

import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyUtil {
	private static final Log log=LogFactory.getLog(PropertyUtil.class);
	
	private static final Properties prop=new Properties();
	static{
		try{
			prop.load(PropertyUtil.class.getResourceAsStream("/const.properties"));
			prop.load(PropertyUtil.class.getResourceAsStream("/redis.properties"));
			prop.load(PropertyUtil.class.getResourceAsStream("/memcached.properties"));
			
			log.info("[Property values START]----------------------------------");
			for(Entry<Object,Object> entry:prop.entrySet()){
				log.info(entry.getKey()+"="+entry.getValue());
			}
			log.info("[Property values END]----------------------------------");
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static String getProperty(String key){
		return prop.getProperty(key);
	}
	
	public static String getProperty(String key,String defaultValue){
		String value = prop.getProperty(key);
		if(value==null){
			return defaultValue;
		}else{
			return value;
		}
	}
}

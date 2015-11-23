package com.lvmama.soa.monitor.util;

public class Assert {
	public static void notEmpty(Object o,String fieldName){
		if(o==null||"".equals(o)){
			throw new IllegalArgumentException(fieldName+" should not be empty.");
		}
	}
	
	public static void notNull(Object o,String fieldName){
		if(o==null){
			throw new IllegalArgumentException(fieldName+" should not be null.");
		}
	}
}

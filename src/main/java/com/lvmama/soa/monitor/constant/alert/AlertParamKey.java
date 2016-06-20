package com.lvmama.soa.monitor.constant.alert;

public class AlertParamKey {
	public static final String DUBBO_SERVICE_DAY_IP=ident("DUBBO_SERVICE_DAY_IP");
	public static final String DUBBO_METHOD_DAY_IP=ident("DUBBO_METHOD_DAY_IP");
	public static final String DUBBO_METHOD_DAY=ident("DUBBO_METHOD_DAY");
	public static final String CONDITION_CONTENT=ident("CONDITION_CONTENT");
	public static final String ACTION_CONTENT=ident("ACTION_CONTENT");
	
	public static final String ALERT_MSG=ident("ALERT_MSG");
	
	private static String ident(String s){
		return s;
	}
}

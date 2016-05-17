package com.lvmama.soa.monitor.constant.alert;

public class AlertParamKey {
	public static final String DUBBO_SERVICE_DAY_IP=ident("dubboServiceDayIP");
	public static final String DUBBO_METHOD_DAY_IP=ident("dubboMethodDayIP");
	public static final String CONDITION_CONTENT=ident("conditionContent");
	public static final String ACTION_CONTENT=ident("actionContent");
	
	private static String ident(String s){
		return s;
	}
}

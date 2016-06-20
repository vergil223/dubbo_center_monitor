package com.lvmama.soa.monitor.constant.alert;

public class AlertCacheConst {
	public static final String KEY_ALL_ENABLED_ALERT=ident("ALERT_allEnabledAlert");
	public static final int KEY_ALL_ENABLED_ALERT_TTL_SEC=ident(300);
	
	public static final String KEY_CONDITION_BY_IDS=ident("ALERT_ConditionByIDs");
	public static final int KEY_CONDITION_BY_IDS_TTL_SEC=ident(300);
	
	public static final String KEY_ACTION_BY_IDS=ident("ALERT_ActionByIDs");
	public static final int KEY_ACTION_BY_IDS_TTL_SEC=ident(300);
	
	private static String ident(String s){
		return s;
	}
	
	private static int ident(int i){
		return i;
	}
}

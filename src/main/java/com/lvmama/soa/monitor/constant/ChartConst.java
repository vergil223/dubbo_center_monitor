package com.lvmama.soa.monitor.constant;

public class ChartConst {
	public static final String CHART_TYPE_SUCCESS_TIMES=ident("SUCCESS_TIMES");
	public static final String CHART_TYPE_FAIL_TIMES=ident("FAIL_TIMES");
	public static final String CHART_TYPE_ELAPSED_AVG=ident("ELAPSED_AVG");
	public static final String CHART_TYPE_ELAPSED_MAX=ident("ELAPSED_MAX");
	
	private static String ident(String s){
		return s;
	}
}

package com.lvmama.soa.monitor.util;

public class StringUtil {
	public static boolean isEmpty(String s){
		return (s==null||s.length()==0);
	}
	
	public static boolean isNullStr(String s){
		return "null".equals(s);
	}
	
	public static String getLineSeparator(){
		return System.getProperty("line.separator");
	}
	
	public static void main(String args[]){
		System.out.println("a"+getLineSeparator()+"b");
	}
}

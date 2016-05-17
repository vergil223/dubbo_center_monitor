package com.lvmama.soa.monitor.util;

import java.util.regex.Pattern;

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
	
	public static boolean match(String str, String pattern){
		return Pattern.compile(pattern).matcher(str).matches();
	}
	
	public static void main(String args[]){
		String targetStr="a.b";
		
//			String pattern = "com.lvmama.test.*ServiceImpl.((?!get|query|select)\\w)+";
		String pattern = ".*";
		
		System.out.println(StringUtil.match(targetStr, pattern));
	}
}

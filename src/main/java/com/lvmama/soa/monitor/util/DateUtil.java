package com.lvmama.soa.monitor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static final String yyyyMMddHHmmss="yyyyMMddHHmmss";
	
	public static final String WEB_DATE_FORMAT="yyyy-MM-dd";
	
	public static Date parse(String yyyyMMddHHmmssStr){
			try {
				return new SimpleDateFormat(yyyyMMddHHmmss).parse(yyyyMMddHHmmssStr);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}					
	}
	
	public static Date parseWebDate(String dateStr) {
		try {
			return new SimpleDateFormat(WEB_DATE_FORMAT).parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Date parseDateYYYYMMdd(String yyyyMMdd) {
		try {
			return new SimpleDateFormat("yyyyMMdd").parse(yyyyMMdd);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String format(Date date) {
		return new SimpleDateFormat(yyyyMMddHHmmss).format(date);
	}
	
	public static String getTodayYMD(){
		return new SimpleDateFormat("yyyyMMdd").format(now());
	}
	
	public static String getYesterdayYMD(){
		return new SimpleDateFormat("yyyyMMdd").format(daysBefore(1));
	}
	
	public static Date now(){
		return new Date();
	}
	
	public static Date daysBefore(int days){
		Calendar now=Calendar.getInstance();
		now.setTime(now());
		now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR)-days);
		return now.getTime();
	}
	
	public static Date minutesBefore(Date date,int minutes){
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)-minutes);
		return c.getTime();
	}
	
	public static Date trimToMin(Date date){
		if(date==null){
			return null;
		}
		
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		
		return c.getTime();
	}
	
	public static Date trimToDay(Date date){
		if(date==null){
			return null;
		}
		
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		
		return c.getTime();
	}
	
	public static String HHmm(Date date){
		return new SimpleDateFormat("HHmm").format(date);
	}
	
	public static String yyyyMMdd(Date date){
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}
	
	public static String yyyyMMddHHmmss(Date date){
		return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}
	
	public static Date changeHHmm(Date date,String HHmm){
		if(HHmm==null||HHmm.length()!=4){
			throw new IllegalArgumentException("argument is invalid:["+HHmm+"]");
		}
		
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, Integer.valueOf(HHmm.substring(2)));
		c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(HHmm.substring(0,2)));
		
		return c.getTime();
	}
	
	public static void main(String args[])throws Exception{
		Date d=new Date();
		System.out.println(d);
//		System.out.println("trimToMin:"+DateUtil.trimToMin(d));
//		System.out.println("trimToDay:"+DateUtil.trimToDay(d));
//		System.out.println(HHmm(d));
//		System.out.println(yyyyMMdd(d));
//		System.out.println(new Date(0L));
//		System.out.println(changeHHmm(d,"1524"));
//		System.out.println(daysBefore(365));
//		System.out.println(getYesterdayYMD());
//		System.out.println(minutesBefore(d,12));
		System.out.println(yyyyMMddHHmmss(d));
	}
}

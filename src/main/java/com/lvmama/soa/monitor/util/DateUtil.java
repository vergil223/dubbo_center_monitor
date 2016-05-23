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
	
	public static Date daysBefore(Date date,int days){
		Calendar baseDate=Calendar.getInstance();
		baseDate.setTime(date);
		baseDate.set(Calendar.DAY_OF_YEAR, baseDate.get(Calendar.DAY_OF_YEAR)-days);
		return baseDate.getTime();
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
	
	/**
	 * 获取当前hhmm是当天的第几分钟，范围是0-1440
	 * @param hhmm
	 * @return
	 */
	public static int getMinuteOfDay(String hhmm){
		String hour=hhmm.substring(0, 2);
		if(hour.startsWith("0")){
			hour=hour.substring(1);
		}
		String min=hhmm.substring(2);
		if(min.startsWith("0")){
			min=min.substring(1);
		}
		
		return 60*Integer.parseInt(hour)+Integer.parseInt(min);
	}
	
	/**
	 * 假设输入为0130, -5,则输出0125
	 * 如果计算完的结果超出了当天，则把超出部分改成当天最后一分钟：
	 * (0002,-5) -> 0000
	 * (2358,5) -> 2359
	 * @param diffMin
	 * @return
	 */
	public static String hhmmDiffMinInSameDay(String hhmm, int diffMin){
		Assert.notEmpty(hhmm, "hhmm");
		
		Date baseDate=DateUtil.changeHHmm(now(), hhmm);
		long baseMillSec=baseDate.getTime();
		
		long diffTempMillSec=baseMillSec+(diffMin*60*1000);
		Date diffDate=new Date(diffTempMillSec);
		
		String diffYYYYMMdd = DateUtil.yyyyMMdd(diffDate);
		String baseYYYYMMdd = DateUtil.yyyyMMdd(baseDate);
		
		if(diffYYYYMMdd.equals(baseYYYYMMdd)){
			return DateUtil.HHmm(diffDate);			
		}else if(diffYYYYMMdd.compareTo(baseYYYYMMdd)>0){
			return "2359";
		}else{
			return "0000";
		}
	}
	
	public static void main(String args[])throws Exception{
		System.out.println(hhmmDiffMinInSameDay("2358",5));
	}
}

package com.lvmama.soa.monitor.util.biz;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;

public class DubboDetailUtil {
	private static final Log log=LogFactory.getLog(DubboDetailUtil.class);
	
	public static String mergeDetailToStr(String oldDetail, String newDetail, boolean isMax) {
		List<List<String>> oldDetailList = detailStrToList(oldDetail);
		List<List<String>> newDetailList = detailStrToList(newDetail);
		
		for (List<String> oldRow : oldDetailList) {
			String oldTime = oldRow.get(0);
			String oldValue = oldRow.get(1);
		
			for (Iterator<List<String>> newIter = newDetailList.iterator(); newIter
					.hasNext();) {
				List<String> newRow = newIter.next();
				String newTime = newRow.get(0);
				String newValue = newRow.get(1);
		
				if (oldTime.endsWith(newTime)) {
					oldRow.remove(1);
					if (isMax) {
						oldRow.add(
								1,
								String.valueOf(Math.max(
										Integer.valueOf(oldValue),
										Integer.valueOf(newValue))));
					} else {
						oldRow.add(
								1,
								String.valueOf(Integer.valueOf(oldValue)
										+ Integer.valueOf(newValue)));
					}
		
					newIter.remove();
				}
			}
		}
		
		oldDetailList.addAll(newDetailList);
		return detailListToStr(oldDetailList);
	}

	public static List<List<String>> detailStrToList(String detailStr){
		return detailStrToList(detailStr, null, null);
	}
	
	public static List<List<String>> detailStrToList(String detailStr, String hhmmStart,String hhmmEnd){
		BufferedReader br = new BufferedReader(new StringReader(detailStr));
		List<List<String>> resultList = new ArrayList<List<String>>();
		try{
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				if ("".equals(line.trim())) {
					continue;
				}
				
				String[] row = line.trim().split(" ");
				String time = row[0].trim();
				String value = row[1].trim();
				if(!StringUtil.isEmpty(time)&&!StringUtil.isEmpty(value)){
					if(!StringUtil.isEmpty(hhmmStart)&&hhmmStart.compareTo(time)>0){
						continue;
					}
					if(!StringUtil.isEmpty(hhmmEnd)&&hhmmEnd.compareTo(time)<0){
						continue;
					}
					List<String> l = new ArrayList<String>();
					l.add(time);
					l.add(value);
					resultList.add(l);				
				}
			}
			
		}catch(Exception e){
			log.error("detailStrToList error, detailStr:"+detailStr,e); 
		}
		
		return resultList;
	}
	
	private static final String detailListToStr(List<List<String>> detailList) {
		StringBuilder resultStr=new StringBuilder();
		for(List<String> row:detailList){
			try{
				if(row==null){
					continue;
				}
				
				String time=row.get(0).trim();
				String value=row.get(1).trim();
				
				if(!StringUtil.isEmpty(time)&&!StringUtil.isEmpty(value)){
					resultStr.append(time).append(" ").append(value).append(StringUtil.getLineSeparator());
				}
			}catch(Exception e){
				log.error("detailListToStr error",e);
			}
		}
		return resultStr.toString();
	}
	
	public static String mergeDetailStr(List<String> detailContentList,boolean isMax){
		//key--time, value--success, fail, elapsed times
		Map<String,String> mergedDetail=new HashMap<String,String>();
		
		for(String detail:detailContentList){
			List<List<String>> singleDetail=detailStrToList(detail);
			for(List<String> singleTimeValue:singleDetail){
				String time=singleTimeValue.get(0);
				String value=singleTimeValue.get(1);
				
				String oldValue=mergedDetail.get(time);
				if(oldValue==null){
					mergedDetail.put(time, value);
					continue;
				}
				
				String newValue;
				if (isMax) {
					newValue=String.valueOf(Math.max(
									Integer.valueOf(oldValue),
									Integer.valueOf(value)));
				} else {
					newValue=
							String.valueOf(Integer.valueOf(oldValue)
									+ Integer.valueOf(value));
				}
				mergedDetail.put(time, newValue);
			}
		}
		
		List<List<String>> mergedDetailContentList=new ArrayList<List<String>>();
		for(Entry<String,String> entry:mergedDetail.entrySet()){
			String time=entry.getKey();
			String value=entry.getValue();
			List<String> l=new ArrayList<String>();
			l.add(time);
			l.add(value);
			mergedDetailContentList.add(l);
		}
		
		Collections.sort(mergedDetailContentList, new Comparator<List<String>>(){

			@Override
			public int compare(List<String> o1, List<String> o2) {
				String time1=o1.get(0);
				String time2=o2.get(0);
				return time1.compareTo(time2);
			}
			
		});
		
		return detailListToStr(mergedDetailContentList);
	} 
	
	public static void main(String args[])throws Exception{
		int NUM_OF_DETAIL=243;
		
		StringBuilder mockDetailStr=new StringBuilder();
		long millSecForStartOfToday=DateUtil.trimToDay(DateUtil.now()).getTime();
		for(int i=1;i<=60*24;i++){
			millSecForStartOfToday=millSecForStartOfToday+60*1000L;
			mockDetailStr.append(DateUtil.HHmm(new Date(millSecForStartOfToday))+" 1");	
			mockDetailStr.append(StringUtil.getLineSeparator());
		}
		
		List<String> detailList=new ArrayList<String>();
		for(int i=1;i<=NUM_OF_DETAIL;i++){
			detailList.add(mockDetailStr.toString());
		}
		
		//old logic START
		long start=DateUtil.now().getTime();
		String mergedDetailStr="";
		for(String detail:detailList){
			mergedDetailStr=mergeDetailToStr(mergedDetailStr,detail,false);			
		}
		long cost=DateUtil.now().getTime()-start;
		System.out.println("OLD logic cost "+cost+"ms to merge "+NUM_OF_DETAIL+" detail content");
		//old logic END
		
		//new logic START
		long start2=DateUtil.now().getTime();
		String mergedDetailStr2=mergeDetailStr(detailList,false);
		long cost2=DateUtil.now().getTime()-start2;
		System.out.println("NEW logic cost "+cost2+"ms to merge "+NUM_OF_DETAIL+" detail content");
		System.out.println(mergedDetailStr2);
		//new logic END
		
	}
}

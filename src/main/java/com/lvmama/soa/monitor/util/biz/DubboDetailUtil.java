package com.lvmama.soa.monitor.util.biz;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvmama.soa.monitor.util.StringUtil;

public class DubboDetailUtil {
	private static final Log log=LogFactory.getLog(DubboDetailUtil.class);
	
	public static String mergeDetailToStr(String oldDetail, String newDetail, boolean isMax) {
		List<List<String>> oldSuccessList = detailStrToList(oldDetail);
		List<List<String>> newSuccessList = detailStrToList(newDetail);
		
		for (List<String> oldRow : oldSuccessList) {
			String oldTime = oldRow.get(0);
			String oldValue = oldRow.get(1);
		
			for (Iterator<List<String>> newIter = newSuccessList.iterator(); newIter
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
		
		oldSuccessList.addAll(newSuccessList);
		return detailListToStr(oldSuccessList);
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
}

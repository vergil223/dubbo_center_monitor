package com.lvmama.soa.monitor.util;

import java.util.HashMap;
import java.util.Map;

public class ParamUtil {
	/**
	 * paramStr的格式：paramA=aaa,paramB=bbb...
	 * @param paramStr
	 * @return
	 */
	public static Map<String,String> convertParamToMap(String paramStr){
		Map<String,String> resultMapParam=new HashMap<String,String>();
		
		if(!StringUtil.isEmpty(paramStr)){
			for(String paramPairStr:paramStr.split(",")){
				String[] paramPair =paramPairStr.split("=");
				String key=paramPair[0].trim();
				String value=paramPair[1].trim();
				
				resultMapParam.put(key, value);
			}			
		}
		
		return resultMapParam;
	}
}

package com.lvmama.soa.monitor.service;

import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;

public interface DubboServiceDayIPService {
	public int insertOrAppend(DubboServiceDayIP dubboServiceDayIP);
	
	public List<DubboServiceDayIP> selectList(Map<String, Object> param);
	
	public List<DubboServiceDayIP> selectMergedListByAppNameAndDay(String appName,String yyyyMMdd);
	
	public void migrateFromRedisToMysql(String yyyyMMDD);
}

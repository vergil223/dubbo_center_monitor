package com.lvmama.soa.monitor.service;

import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;

public interface DubboMethodDayIPService {
	public int insertOrAppend(DubboMethodDayIP dubboMethodDayIP);
	
	public List<DubboMethodDayIP> selectMergedList(String appName,String serviceName,String yyyyMMdd);
	
	public void migrateFromRedisToMysql(String yyyyMMDD);
	
	public List<DubboMethodDayIP> selectList(Map<String,Object> params);
}

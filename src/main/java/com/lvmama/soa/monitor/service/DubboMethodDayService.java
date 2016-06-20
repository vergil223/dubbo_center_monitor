package com.lvmama.soa.monitor.service;

import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.entity.DubboMethodDay;

public interface DubboMethodDayService {
	public int insertOrAppend(DubboMethodDay DubboMethodDay);
	
	public List<DubboMethodDay> selectMergedList(String appName,String serviceName,String yyyyMMdd);
	
	public void migrateFromRedisToMysql(String yyyyMMDD);
	
	public List<DubboMethodDay> selectList(Map<String,Object> params);
	
	public List<DubboMethodDay> selectByMethod(String appName,String serviceName,String method,String yyyyMMdd);
}

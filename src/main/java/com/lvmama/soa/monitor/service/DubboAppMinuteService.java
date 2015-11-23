package com.lvmama.soa.monitor.service;

import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.entity.DubboAppMinute;

public interface DubboAppMinuteService {
	public int insertOrAppend(DubboAppMinute app);

	public List<DubboAppMinute> selectList(Map<String,Object> map);
	
	public List<String> getAppNames();
}

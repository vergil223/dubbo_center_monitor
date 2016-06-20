package com.lvmama.soa.monitor.service.alert;

import java.util.List;
import java.util.Map;

import com.lvmama.soa.monitor.entity.alert.TAltRecord;

public interface IAlertRecordService {
	public void insert(TAltRecord record);
	
	public List<TAltRecord> selectList(Map params);
	
	public long count(Map params);
}

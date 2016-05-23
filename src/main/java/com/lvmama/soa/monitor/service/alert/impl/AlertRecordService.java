package com.lvmama.soa.monitor.service.alert.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.dao.mybatis.alert.TAltRecordDao;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.util.DateUtil;

@Service
public class AlertRecordService implements IAlertRecordService {
	@Autowired
	private TAltRecordDao tAltRecordDao;
	
	public void insert(TAltRecord record){
		record.setInsertTime(DateUtil.now());
		tAltRecordDao.insert(record);
	}
	
	public List<TAltRecord> selectList(Map params){
		return tAltRecordDao.selectList(params);
	}
}

package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.util.DateUtil;

@Repository
public class TAltRecordDao extends BaseDao{
	public void insert(TAltRecord record){
		record.setInsertTime(DateUtil.now());
		this.insert("T_ALT_RECORD.insert", record);
	}
	
	public List<TAltRecord> selectList(Map params){
		return this.getList("T_ALT_RECORD.selectList", params);
	}
}

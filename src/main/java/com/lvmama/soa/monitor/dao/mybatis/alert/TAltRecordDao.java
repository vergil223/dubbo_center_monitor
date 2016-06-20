package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.Date;
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
	
	public int deleteBeforeTime(Date insertTime){
		return this.delete("T_ALT_RECORD.deleteBeforeTime", insertTime);
	}
	
	public long count(Map params){
		return this.get("T_ALT_RECORD.count", params);
	}
}

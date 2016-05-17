package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;

@Repository
public class TAltAlertDao extends BaseDao{
	public List<TAltAlert> findAllEnabledAlert(){
		return this.getList("T_ALT_ALERT.findAllEnabledAlert", new HashMap());
	}
}

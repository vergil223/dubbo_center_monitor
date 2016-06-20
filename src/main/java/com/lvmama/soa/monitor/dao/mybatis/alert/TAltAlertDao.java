package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.constant.alert.AlertCacheConst;
import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.util.CacheUtil;

@Repository
public class TAltAlertDao extends BaseDao{
	public List<TAltAlert> findAllEnabledAlert(){
		List<TAltAlert> cachedList = CacheUtil.getArray(AlertCacheConst.KEY_ALL_ENABLED_ALERT, TAltAlert.class);
		if(cachedList==null){
			List<TAltAlert> resultList = this.getList("T_ALT_ALERT.findAllEnabledAlert", new HashMap<String,Object>());
			CacheUtil.setArray(AlertCacheConst.KEY_ALL_ENABLED_ALERT, resultList, AlertCacheConst.KEY_ALL_ENABLED_ALERT_TTL_SEC);
			return resultList;
		}else{
			return cachedList;
		}
	}
	
	public List<TAltAlert> findAllAlert(){
			List<TAltAlert> resultList = this.getList("T_ALT_ALERT.findAllAlert", new HashMap<String,Object>());
			return resultList;
	}
}

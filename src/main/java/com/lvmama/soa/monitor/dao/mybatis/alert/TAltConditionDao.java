package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.constant.alert.AlertCacheConst;
import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltCondition;
import com.lvmama.soa.monitor.util.CacheUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Repository
public class TAltConditionDao extends BaseDao{
	public List<TAltCondition> findByIds(String ids){
		if(StringUtil.isEmpty(ids)){
			return new ArrayList<TAltCondition>();
		}
		
		String key=AlertCacheConst.KEY_CONDITION_BY_IDS+"_"+ids;
		
		List<TAltCondition> cachedList=CacheUtil.getArray(key, TAltCondition.class);
		
		if(cachedList==null){
			HashMap<String,Object> params=new HashMap<String,Object>();
			params.put("ids", ids);
			List<TAltCondition> resultList = this.getList("T_ALT_CONDITION.findByIds", params);
			
			CacheUtil.setArray(key, resultList, AlertCacheConst.KEY_CONDITION_BY_IDS_TTL_SEC);
			return resultList;			
		}else{
			return cachedList;
		}
		
	}
}

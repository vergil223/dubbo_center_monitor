package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.constant.alert.AlertCacheConst;
import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltAction;
import com.lvmama.soa.monitor.util.CacheUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Repository
public class TAltActionDao extends BaseDao{
	public List<TAltAction> findByIds(String ids){
		if(StringUtil.isEmpty(ids)){
			return new ArrayList<TAltAction>();
		}
		
		String key=AlertCacheConst.KEY_ACTION_BY_IDS+"_"+ids;
		
		List<TAltAction> cachedList=CacheUtil.getArray(key, TAltAction.class);
		
		if(cachedList==null){
			HashMap<String,Object> params=new HashMap<String,Object>();
			params.put("ids", ids);
			List<TAltAction> resultList = this.getList("T_ALT_ACTION.findByIds", params);
			
			CacheUtil.setArray(key, resultList, AlertCacheConst.KEY_ACTION_BY_IDS_TTL_SEC);
			return resultList;
		}else{
			return cachedList;
		}
	}
}

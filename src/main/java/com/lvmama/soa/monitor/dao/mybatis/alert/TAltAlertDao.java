package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.constant.Enabled;
import com.lvmama.soa.monitor.constant.alert.AlertCacheConst;
import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.util.Assert;
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
	
	public TAltAlert saveOrUpdate(TAltAlert tAltAlert){
		Assert.notNull(tAltAlert,"tAltAlert");
		
		if(tAltAlert.getId_()!=null){
			this.update("T_ALT_ALERT.update", tAltAlert);
		}else{
			this.insert("T_ALT_ALERT.save", tAltAlert);
		}
		return tAltAlert;
	}
	
	public int batchDelete(String ids){
		Map<String,String> param=new HashMap<String,String>();
		param.put("ids", ids);
		return this.delete("T_ALT_ALERT.batchDelete", param);
	}
	
	public int batchEnable(String ids){
		Map<String,String> param=new HashMap<String,String>();
		param.put("ids", ids);
		param.put("enabled", Enabled.Y);
		return this.delete("T_ALT_ALERT.batchUpdateEnabled", param);
	}
	
	public int batchDisable(String ids){
		Map<String,String> param=new HashMap<String,String>();
		param.put("ids", ids);
		param.put("enabled", Enabled.N);
		return this.delete("T_ALT_ALERT.batchUpdateEnabled", param);
	}
	
	public TAltAlert findById(Long id_){
		return this.get("T_ALT_ALERT.selectByPrimaryKey", id_);
	}
}

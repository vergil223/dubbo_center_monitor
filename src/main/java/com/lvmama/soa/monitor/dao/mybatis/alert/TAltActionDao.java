package com.lvmama.soa.monitor.dao.mybatis.alert;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.dao.mybatis.BaseDao;
import com.lvmama.soa.monitor.entity.alert.TAltAction;

@Repository
public class TAltActionDao extends BaseDao{
	public List<TAltAction> findByIds(String ids){
		HashMap<String,Object> params=new HashMap<String,Object>();
		params.put("ids", ids);
		return this.getList("T_ALT_ACTION.findByIds", params);
	}
}

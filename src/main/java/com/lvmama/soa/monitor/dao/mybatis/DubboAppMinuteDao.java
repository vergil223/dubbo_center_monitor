package com.lvmama.soa.monitor.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboAppMinute;

@Repository("dubboAppMinuteDao")
public class DubboAppMinuteDao extends BaseDao {
	public int insert(DubboAppMinute detail) {
		if (detail == null) {
			return 0;
		}
		return this.insert("DUBBO_APP_MINUTE.insert", detail);
	}

	public int append(DubboAppMinute detail) {
		if (detail == null) {
			return 0;
		}

		return this.update("DUBBO_APP_MINUTE.append", detail);
	}
	
	public List<DubboAppMinute> selectList(Map<String,Object> map){
		if(map==null||map.isEmpty()){
			return null;
		}
		
		return this.getList("DUBBO_APP_MINUTE.selectList", map);
	}
	
	@SuppressWarnings("rawtypes")
	public List<String> getAppNames(){
		return this.getList("DUBBO_APP_MINUTE.getAppNames",new HashMap());
	}
}

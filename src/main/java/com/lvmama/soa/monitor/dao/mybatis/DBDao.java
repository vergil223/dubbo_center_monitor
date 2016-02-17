package com.lvmama.soa.monitor.dao.mybatis;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.util.SpringUtil;

@Repository("dbDao")
public class DBDao extends BaseDao{
	private static final Log log = LogFactory.getLog(DBDao.class);
	
	private static final Set<String> existsTableNames=new HashSet<String>();
	
	public boolean checkAndCreateTable(String tableName,String createTableDDL) {
		if (existsTableNames.contains(tableName)) {
			return true;
		}

		try {
			if (!tableExists(tableName)) {
				createTable(createTableDDL);
			}
			existsTableNames.add(tableName);
			return true;
		} catch (Exception e) {
			log.error("checkAndCreateTable error, will try again", e);
			if (tableExists(tableName)) {
				existsTableNames.add(tableName);
				return true;
			} else {
				log.error("table not exists and failed to create it, table name:"+tableName, e);
				return false;
			}
		}

	}
	
	private void createTable(String sql){
		Map<String,String> m=new HashMap<String,String>();
		m.put("sql", sql);
		
		DBDao dbDao=(DBDao)SpringUtil.getContext().getBean("dbDao");
		dbDao.createTable(m);
	}
	
	public boolean tableExists(String tableName){
		Object obj=this.get("DB.tableExists", tableName);
		if(obj!=null&&obj instanceof Integer){
			Integer count=(Integer)obj;
			return count>0;
		}
		return false;
	}
	
	public void createTable(Map m){
		this.update("DB.createTable", m);
	}
	
	public List<String> getTableNames(String tableNameLike){
		return this.getList("DB.getTableNames", tableNameLike);
	}
	
	public void cleanBigColumn(String tableName,Date dateBeforeToClean){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("tableName", tableName);
		param.put("time", dateBeforeToClean);
		this.update("DB.cleanBigColumnByTime", param);
	}
	
	public void optimizeTable(String tableName){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("tableName", tableName);
		this.update("DB.optimizeTable", param);
	}
}

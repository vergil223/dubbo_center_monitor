package com.lvmama.soa.monitor.dao.mybatis.sharding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.shardbatis.strategy.ShardStrategy;
import com.lvmama.soa.monitor.dao.mybatis.DBDao;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.util.SpringUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Deprecated
//some sql which contains sub select is not supported by shardbatis, so this class is not used now.
public class DubboMethodDayIPShardStrategyImpl implements ShardStrategy{
	private static final Set<String> existsTableNames=new HashSet<String>();
	
	private static final List<String> BASE_TABLE_NAMES_FOR_SHARD=Arrays.asList(new String[]{"DUBBO_METHOD_DAY_IP"});
	
	@Override
	public String getTargetTableName(String baseTableName ,Object params, String mapperId) {
		if(params==null){
			return baseTableName;
		}
		if(baseTableName==null||!BASE_TABLE_NAMES_FOR_SHARD.contains(baseTableName.toUpperCase())){
			return baseTableName;
		}
		
		String targetTableName=baseTableName;
		if(params instanceof DubboMethodDayIP){
			DubboMethodDayIP day=(DubboMethodDayIP)params;
			String appName=day.getAppName();
			
			if(!StringUtil.isEmpty(appName)){
				targetTableName=baseTableName+"_"+appName.toString().toUpperCase();				
			}
		}else if(params instanceof Map){
			Map map=(Map)params;
			Object appName=map.get("appName");
			
			if(appName!=null&&!StringUtil.isEmpty(appName.toString())){
				targetTableName= baseTableName+"_"+appName.toString().toUpperCase();				
			}
		}
		
		if(!existsTableNames.contains(targetTableName)){
			if(!isTableExists(targetTableName)){
				createTable(targetTableName);
			}
			existsTableNames.add(targetTableName);
		}
		
		return targetTableName;
	}
	
	private void createTable(String tableName){
		StringBuilder sql=new StringBuilder();
		sql.append(" CREATE TABLE `"+tableName+"` (                                                                       ");
		sql.append("   `ID_` bigint(20) NOT NULL AUTO_INCREMENT,                                                          ");
		sql.append("   `APP_NAME` varchar(20) NOT NULL,                                                                   ");
		sql.append("   `SERVICE` varchar(200) NOT NULL,                                                                   ");
		sql.append("   `METHOD` varchar(50) NOT NULL,                                                                     ");
		sql.append("   `CONSUMER_IP` varchar(15) NOT NULL,                                                                ");
		sql.append("   `PROVIDER_IP` varchar(15) NOT NULL,                                                                ");
		sql.append("   `TIME` datetime NOT NULL,                                                                          ");
		sql.append("   `SUCCESS_TIMES` bigint(20) NOT NULL,                                                               ");
		sql.append("   `FAIL_TIMES` bigint(20) NOT NULL,                                                                  ");
		sql.append("   `ELAPSED_AVG` int(11) NOT NULL,                                                                    ");
		sql.append("   `ELAPSED_MAX` int(11) NOT NULL,                                                                    ");
		sql.append("   `SUCCESS_TIMES_DETAIL` text,                                                                       ");
		sql.append("   `FAIL_TIMES_DETAIL` text,                                                                          ");
		sql.append("   `ELAPSED_TOTAL_DETAIL` text,                                                                       ");
		sql.append("   `ELAPSED_MAX_DETAIL` text,                                                                         ");
		sql.append("   PRIMARY KEY (`ID_`),                                                                               ");
		sql.append("   KEY `IDX_"+tableName+"_1` (`APP_NAME`,`SERVICE`,`METHOD`,`PROVIDER_IP`,`CONSUMER_IP`,`TIME`)       ");
		sql.append(" ) ENGINE=MyISAM DEFAULT CHARSET=utf8;                                                                ");
		
		Map<String,String> m=new HashMap<String,String>();
		m.put("sql", sql.toString());
		
		DBDao dbDao=(DBDao)SpringUtil.getContext().getBean("dbDao");
		dbDao.createTable(m);
	}
	
	private boolean isTableExists(String tableName){
		DBDao dbDao=(DBDao)SpringUtil.getContext().getBean("dbDao");
		return dbDao.tableExists(tableName);
	}

}

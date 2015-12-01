package com.lvmama.soa.monitor.dao.mybatis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;

@Repository("dubboMethodDayIPDao")
public class DubboMethodDayIPDao extends BaseDao{
	@Autowired
	private DBDao dbDao;
	
	public int insert(DubboMethodDayIP day) {
		if (day == null) {
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.insert("DUBBO_METHOD_DAY_IP.insert", day);
	}
	
	public int update(DubboMethodDayIP day) {
		if (day == null) {
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.update("DUBBO_METHOD_DAY_IP.update", day);
	}
	
	public DubboMethodDayIP findOne(DubboMethodDayIP day) {
		if (day == null) {
			return null;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return (DubboMethodDayIP)this.get("DUBBO_METHOD_DAY_IP.findOne", day);
	}
	
	public List<DubboMethodDayIP> getMergedList(String appName,String serviceName,String yyyyMMdd){
		Assert.notEmpty(appName, "appName");
		Assert.notEmpty(serviceName, "serviceName");
		Assert.notEmpty(yyyyMMdd, "yyyyMMdd");
		
		DubboMethodDayIP dayParam=new DubboMethodDayIP();
		dayParam.setAppName(appName);
		dayParam.setService(serviceName);
		dayParam.setTime(DateUtil.parseDateYYYYMMdd(yyyyMMdd));
		
		return this.getList("DUBBO_METHOD_DAY_IP.getMergedList",dayParam);
	}
	
	public List<DubboMethodDayIP> selectList(Map<String,Object> params){
		Assert.notEmpty(params.get("appName"), "appName");
		Assert.notEmpty(params.get("service"), "service");
		Assert.notEmpty(params.get("method"), "method");
		Assert.notEmpty(params.get("time"), "time");
		
		return this.getList("DUBBO_METHOD_DAY_IP.selectList", params);
	}
	
	private String createTableDDL(String tableName) {
		StringBuilder sql=new StringBuilder();
		sql.append(" CREATE TABLE `"+tableName+"` (                                                                       ");
		sql.append("   `ID_` bigint(20) NOT NULL AUTO_INCREMENT,                                                          ");
		sql.append("   `APP_NAME` varchar(25) NOT NULL,                                                                   ");
		sql.append("   `SERVICE` varchar(200) NOT NULL,                                                                   ");
		sql.append("   `METHOD` varchar(75) NOT NULL,                                                                     ");
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
		sql.append("   UNIQUE KEY `IDX_"+tableName+"_1` (`APP_NAME`,`SERVICE`,`METHOD`,`PROVIDER_IP`,`CONSUMER_IP`,`TIME`) USING BTREE      ");
		sql.append(" ) ENGINE=MyISAM DEFAULT CHARSET=utf8;                                                                ");
		return sql.toString();
	}
	
	public List<DubboMethodDayIP> selectByMethod(String appName,String serviceName,String method,String yyyyMMdd){
		Assert.notEmpty(appName, "appName");
		Assert.notEmpty(serviceName, "service");
		Assert.notEmpty(method, "method");
		Assert.notEmpty(yyyyMMdd, "time");
		
		DubboMethodDayIP dayParam=new DubboMethodDayIP();
		dayParam.setAppName(appName);
		dayParam.setService(serviceName);
		dayParam.setMethod(method);
		dayParam.setTime(DateUtil.parseDateYYYYMMdd(yyyyMMdd));
		return this.getList("DUBBO_METHOD_DAY_IP.selectByMethod", dayParam);
	}
}

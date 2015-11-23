package com.lvmama.soa.monitor.dao.mybatis;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;

@Repository("dubboServiceDayIPDao")
public class DubboServiceDayIPDao extends BaseDao{
	@Autowired
	private DBDao dbDao;
	
	public int insert(DubboServiceDayIP day) {
		if (day == null) {
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.insert("DUBBO_SERVICE_DAY_IP.insert", day);
	}
	
	public int update(DubboServiceDayIP day) {
		if (day == null) {
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.update("DUBBO_SERVICE_DAY_IP.update", day);
	}
	
	public DubboServiceDayIP findOne(DubboServiceDayIP day) {
		if (day == null) {
			return null;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return (DubboServiceDayIP)this.get("DUBBO_SERVICE_DAY_IP.findOne", day);
	}
	
	public List<DubboServiceDayIP> getMergedListByAppNameAndDay(String appName,String yyyyMMDD){
		Assert.notNull(appName, "appName");
		Assert.notNull(yyyyMMDD, "yyyyMMDD");
		
		DubboServiceDayIP paramDay=new DubboServiceDayIP();
		paramDay.setAppName(appName);
		paramDay.setTime(DateUtil.parseDateYYYYMMdd(yyyyMMDD));
		
		return this.getList("DUBBO_SERVICE_DAY_IP.getMergedListByAppNameAndDay", paramDay);
	}
	
	public List<DubboServiceDayIP> selectList(Map<String,Object> param){
		return this.getList("DUBBO_SERVICE_DAY_IP.selectList", param);
	}
	
	private String createTableDDL(String tableName) {
		StringBuilder sql=new StringBuilder();
		sql.append(" CREATE TABLE `"+tableName+"` (                                                                       ");
		sql.append("   `ID_` bigint(20) NOT NULL AUTO_INCREMENT,                                                          ");
		sql.append("   `APP_NAME` varchar(25) NOT NULL,                                                                   ");
		sql.append("   `SERVICE` varchar(200) NOT NULL,                                                                   ");
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
		sql.append("   UNIQUE KEY `IDX_"+tableName+"_1` (`APP_NAME`,`SERVICE`,`PROVIDER_IP`,`CONSUMER_IP`,`TIME`) USING BTREE      ");
		sql.append(" ) ENGINE=MyISAM DEFAULT CHARSET=utf8;                                                                ");
		return sql.toString();
	}
}

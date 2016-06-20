package com.lvmama.soa.monitor.dao.mybatis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.CacheUtil;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Repository("dubboMethodDayDao")
public class DubboMethodDayDao extends BaseDao{
	@Autowired
	private DBDao dbDao;
	
	public int insert(DubboMethodDay day) {
		if (day == null) {
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.insert("DUBBO_METHOD_DAY.insert", day);
	}
	
	public int update(DubboMethodDay day) {
		if (day == null) {
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.update("DUBBO_METHOD_DAY.update", day);
	}
	
	public DubboMethodDay findOne(DubboMethodDay day) {
		if (day == null) {
			return null;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return (DubboMethodDay)this.get("DUBBO_METHOD_DAY.findOne", day);
	}
	
	public List<DubboMethodDay> getMergedList(String appName,String serviceName,String yyyyMMdd){
		Assert.notEmpty(appName, "appName");
		Assert.notEmpty(serviceName, "serviceName");
		Assert.notEmpty(yyyyMMdd, "yyyyMMdd");
		
		DubboMethodDay dayParam=new DubboMethodDay();
		dayParam.setAppName(appName);
		dayParam.setService(serviceName);
		dayParam.setTime(DateUtil.parseDateYYYYMMdd(yyyyMMdd));
		
		return this.getList("DUBBO_METHOD_DAY.getMergedList",dayParam);
	}
	
	public List<DubboMethodDay> selectList(Map<String,Object> params){
		Assert.notEmpty(params.get("appName"), "appName");
		Assert.notEmpty(params.get("service"), "service");
		Assert.notEmpty(params.get("method"), "method");
		Assert.notEmpty(params.get("time"), "time");
		
		String cacheKey=params.get("appName")+"_"+params.get("service")+"_"+params.get("method")+"_"+DateUtil.yyyyMMdd((Date)params.get("time"));
		List<DubboMethodDay> cachedObj=CacheUtil.getArray(cacheKey,DubboMethodDay.class);
		if(cachedObj==null){
			if(params.get("shardTableName")==null){
				DubboMethodDay DubboMethodDay=new DubboMethodDay();
				DubboMethodDay.setAppName(params.get("appName").toString());
				params.put("shardTableName", DubboMethodDay.getShardTableName());			
			}
			
			List<DubboMethodDay> result= this.getList("DUBBO_METHOD_DAY.selectList", params);
			
			CacheUtil.setArray(cacheKey, result, 86400);
			
			return result;
		}else{
			return cachedObj;
		}
	}
	
	private String createTableDDL(String tableName) {
		StringBuilder sql=new StringBuilder();
		sql.append(" CREATE TABLE `"+tableName+"` (                                                                       ");
		sql.append("   `ID_` bigint(20) NOT NULL AUTO_INCREMENT,                                                          ");
		sql.append("   `APP_NAME` varchar(25) NOT NULL,                                                                   ");
		sql.append("   `SERVICE` varchar(200) NOT NULL,                                                                   ");
		sql.append("   `METHOD` varchar(75) NOT NULL,                                                                     ");
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
		sql.append("   UNIQUE KEY `IDX_"+tableName+"_1` (`APP_NAME`,`SERVICE`,`METHOD`,`TIME`) USING BTREE, ");
		sql.append("   KEY `IDX_"+tableName+"_2` (`TIME`,`APP_NAME`,`SERVICE`,`METHOD`) USING BTREE                       ");
		sql.append(" ) ENGINE=MyISAM DEFAULT CHARSET=utf8;                                                                ");
		return sql.toString();
	}
	
	public List<DubboMethodDay> selectByMethod(String appName,String serviceName,String method,String yyyyMMdd){
		Assert.notEmpty(appName, "appName");
		Assert.notEmpty(serviceName, "service");
		Assert.notEmpty(method, "method");
		Assert.notEmpty(yyyyMMdd, "time");
		
		DubboMethodDay dayParam=new DubboMethodDay();
		dayParam.setAppName(appName);
		dayParam.setService(serviceName);
		dayParam.setMethod(method);
		dayParam.setTime(DateUtil.parseDateYYYYMMdd(yyyyMMdd));
		return this.getList("DUBBO_METHOD_DAY.selectByMethod", dayParam);
	}
	
	public int delete(DubboMethodDay day){
		if(day==null||StringUtil.isEmpty(day.getAppName())||day.getTime()==null){
			return 0;
		}
		dbDao.checkAndCreateTable(day.getShardTableName(),createTableDDL(day.getShardTableName()));
		return this.delete("DUBBO_METHOD_DAY.delete", day);
	}
}

package com.lvmama.soa.monitor.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.dao.mybatis.DubboServiceDayIPDao;
import com.lvmama.soa.monitor.dao.redis.DubboServiceDayIPRedisDao;
import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.service.DubboServiceDayIPService;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DataSourceUtil;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.DistributedLock;

@Service("dubboServiceDayIPService")
public class DubboServiceDayIPServiceImpl implements DubboServiceDayIPService {
	private static final Log log = LogFactory.getLog(DubboServiceDayIPServiceImpl.class);

	@Autowired
	DubboServiceDayIPDao dubboServiceDayIPDao;
	@Autowired
	DubboServiceDayIPRedisDao dubboServiceDayIPRedisDao;

	@Override
	public int insertOrAppend(DubboServiceDayIP day) {
		if (day == null) {
			return 0;
		}

		int count = 0;
		String lockKey = day.uniqueKey();
		try {
			while (!DistributedLock.tryLock(lockKey)) {
				log.warn("waiting for lock:"+lockKey);
				try {
					Thread.sleep(1000L);
				} catch (Exception e) {
					log.error("sleep error when try to get lock", e);
				}
			}

			count = append(day);
			if (count == 0) {
				count = dubboServiceDayIPRedisDao.insert(day);
			}
		} catch (Exception e) {
			log.error("insertOrAppend error", e);
		} finally {
			DistributedLock.releaseLock(lockKey);
		}

		return count;
	}

	private int append(DubboServiceDayIP day) {
		DubboServiceDayIP oldDay = dubboServiceDayIPRedisDao.findOne(day);
		if(oldDay==null){
			return 0;
		}

		oldDay=DubboServiceDayIP.merge(day, oldDay);
		
		return dubboServiceDayIPRedisDao.update(oldDay);
	}

	@Override
	public List<DubboServiceDayIP> selectList(Map<String, Object> param) {
		Assert.notNull(param.get("time_from"), "time_from");
		Assert.notEmpty(param.get("appName"), "appName");
		
		DubboServiceDayIP day=new DubboServiceDayIP();
		day.setAppName(param.get("appName").toString());
		param.put("shardTableName", day.getShardTableName());
		
		if(DataSourceUtil.canReadFromRedis(DubboServiceDayIP.class, DateUtil.yyyyMMdd((Date)param.get("time_from")))){
			return dubboServiceDayIPRedisDao.selectList(param);			
		}else{
			return dubboServiceDayIPDao.selectList(param);
		}
	}

	@Override
	public List<DubboServiceDayIP> selectMergedListByAppNameAndDay(String appName,
			String yyyyMMdd) {
		if(DataSourceUtil.canReadFromRedis(DubboServiceDayIP.class, yyyyMMdd)){
			return dubboServiceDayIPRedisDao.getMergedListByAppNameAndDay(appName,yyyyMMdd);
		}else{
			return dubboServiceDayIPDao.getMergedListByAppNameAndDay(appName,yyyyMMdd);
		}
	}
	
	@Override
	public void migrateFromRedisToMysql(String yyyyMMDD){
		Set<String> keys=dubboServiceDayIPRedisDao.getKeysByDate(yyyyMMDD);
		
		Set<String> deleteApps=new HashSet<String>();
		for(String key:keys){
			try{
				DubboServiceDayIP day =dubboServiceDayIPRedisDao.getByKey(key);
				if(!deleteApps.contains(day.getAppName())){
					//remove existing data of the same day before insert the latest data
					int deletedNum=dubboServiceDayIPDao.delete(day);
					log.info("removed "+deletedNum+" records in "+day.getShardTableName());
					deleteApps.add(day.getAppName());
				}
				dubboServiceDayIPDao.insert(day);		
//				dubboServiceDayIPRedisDao.deleteByKey(key);
			}catch(Exception e){
				log.error("migrateFromRedisToMysql error. key="+key, e);
			}
		}
	}
	
}

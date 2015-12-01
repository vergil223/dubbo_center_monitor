package com.lvmama.soa.monitor.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.dao.mybatis.DubboMethodDayIPDao;
import com.lvmama.soa.monitor.dao.redis.DubboMethodDayIPRedisDao;
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DataSourceUtil;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.DistributedLock;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

@Service("dubboMethodDayIPService")
public class DubboMethodDayIPServiceImpl implements DubboMethodDayIPService {
	private static final Log log = LogFactory.getLog(DubboMethodDayIPServiceImpl.class);

	@Autowired
	DubboMethodDayIPDao dubboMethodDayIPDao;
	@Autowired
	DubboMethodDayIPRedisDao dubboMethodDayIPRedisDao;

	@Override
	public int insertOrAppend(DubboMethodDayIP day) {
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
				count = dubboMethodDayIPRedisDao.insert(day);
			}
		} catch (Exception e) {
			log.error("insertOrAppend error", e);
		} finally {
			DistributedLock.releaseLock(lockKey);
		}

		return count;
	}

	private int append(DubboMethodDayIP day) {
		DubboMethodDayIP oldDay = dubboMethodDayIPRedisDao.findOne(day);
		if(oldDay==null){
			return 0;
		}

		if(oldDay.getSuccessTimes()>0||day.getSuccessTimes()>0){
			oldDay.setElapsedAvg((oldDay.getSuccessTimes() * oldDay.getElapsedAvg() + day
					.getSuccessTimes() * day.getElapsedAvg())
					/ (oldDay.getSuccessTimes() + day.getSuccessTimes()));
		}
		oldDay.setSuccessTimes(oldDay.getSuccessTimes() + day.getSuccessTimes());
		oldDay.setFailTimes(oldDay.getFailTimes() + day.getFailTimes());
		oldDay.setElapsedMax(Math.max(oldDay.getElapsedMax(),
				day.getElapsedMax()));

		oldDay.setSuccessTimesDetail(DubboDetailUtil.mergeDetailToStr(
				oldDay.getSuccessTimesDetail(), day.getSuccessTimesDetail(),
				false));
		oldDay.setFailTimesDetail(DubboDetailUtil.mergeDetailToStr(oldDay.getFailTimesDetail(),
				day.getFailTimesDetail(), false));
		oldDay.setElapsedTotalDetail(DubboDetailUtil.mergeDetailToStr(
				oldDay.getElapsedTotalDetail(), day.getElapsedTotalDetail(),
				false));
		oldDay.setElapsedMaxDetail(DubboDetailUtil.mergeDetailToStr(oldDay.getElapsedMaxDetail(),
				day.getElapsedMaxDetail(), true));
		
		return dubboMethodDayIPRedisDao.update(oldDay);
	}
	
	@Override
	public List<DubboMethodDayIP> selectMergedList(
			String appName, String serviceName, String yyyyMMdd) {
		if(DataSourceUtil.canReadFromRedis(DubboMethodDayIP.class, yyyyMMdd)){
			return dubboMethodDayIPRedisDao.getMergedListByAppNameAndDay(appName,serviceName,yyyyMMdd);
		}else{
			return dubboMethodDayIPDao.getMergedList(appName,serviceName,yyyyMMdd);
		}
	}
	
	@Override
	public void migrateFromRedisToMysql(String yyyyMMDD){
		Set<String> keys=dubboMethodDayIPRedisDao.getKeysByDate(yyyyMMDD);
		for(String key:keys){
			try{
				DubboMethodDayIP day =dubboMethodDayIPRedisDao.getByKey(key);
				dubboMethodDayIPDao.insert(day);		
//				dubboMethodDayIPRedisDao.deleteByKey(key);
			}catch(Exception e){
				log.error("migrateFromRedisToMysql error. key="+key, e);
			}
		}
	}

	@Override
	public List<DubboMethodDayIP> selectList(Map<String, Object> params) {
		if(DataSourceUtil.canReadFromRedis(DubboMethodDayIP.class, DateUtil.yyyyMMdd((Date)params.get("time")))){
			return dubboMethodDayIPRedisDao.selectList(params);
		}else{
			return dubboMethodDayIPDao.selectList(params);
		}
	}

	@Override
	public List<DubboMethodDayIP> selectByMethod(String appName,
			String serviceName, String method, String yyyyMMdd) {
		if(DataSourceUtil.canReadFromRedis(DubboMethodDayIP.class, yyyyMMdd)){
			return dubboMethodDayIPRedisDao.selectByMethod(appName,serviceName,method,yyyyMMdd);
		}else{
			return dubboMethodDayIPDao.selectByMethod(appName,serviceName,method,yyyyMMdd);
		}
	}

	
}

package com.lvmama.soa.monitor.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.dao.mybatis.DubboMethodDayDao;
import com.lvmama.soa.monitor.dao.redis.DubboMethodDayRedisDao;
import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.service.DubboMethodDayService;
import com.lvmama.soa.monitor.service.alert.IAlertService;
import com.lvmama.soa.monitor.util.DataSourceUtil;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.DistributedLock;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

@Service("dubboMethodDayService")
public class DubboMethodDayServiceImpl implements DubboMethodDayService {
	private static final Log log = LogFactory.getLog(DubboMethodDayServiceImpl.class);
	
	@Autowired
	private IAlertService methodDayAlertService;
	@Autowired
	DubboMethodDayDao dubboMethodDayDao;
	@Autowired
	DubboMethodDayRedisDao DubboMethodDayRedisDao;

	@Override
	public int insertOrAppend(DubboMethodDay day) {
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
				alert(day);
				count = DubboMethodDayRedisDao.insert(day);
			}
		} catch (Exception e) {
			log.error("insertOrAppend error", e);
		} finally {
			DistributedLock.releaseLock(lockKey);
		}

		return count;
	}

	private int append(DubboMethodDay day) {
		DubboMethodDay oldDay = DubboMethodDayRedisDao.findOne(day);
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
		
		alert(oldDay);
		
		return DubboMethodDayRedisDao.update(oldDay);
	}
	
	private void alert(DubboMethodDay DubboMethodDay) {
		new Thread(new MethodDayAlertRunnable(methodDayAlertService,DubboMethodDay)).start();
	}
	
	private class MethodDayAlertRunnable implements Runnable{
		private IAlertService methodDayAlertService;
		private DubboMethodDay dubboMethodDay;
		public MethodDayAlertRunnable(IAlertService methodDayIPAlertService,DubboMethodDay DubboMethodDay){
			this.methodDayAlertService=methodDayIPAlertService;
			this.dubboMethodDay=DubboMethodDay;
		}
		@Override
		public void run() {
			Map<String,Object> alertParam=new HashMap<String,Object>();
			alertParam.put(AlertParamKey.DUBBO_METHOD_DAY, dubboMethodDay);
			methodDayAlertService.alert(alertParam);
		}
	}
	
	@Override
	public List<DubboMethodDay> selectMergedList(
			String appName, String serviceName, String yyyyMMdd) {
		if(DataSourceUtil.canReadFromRedis(DubboMethodDay.class, yyyyMMdd)){
			return DubboMethodDayRedisDao.getMergedListByAppNameAndDay(appName,serviceName,yyyyMMdd);
		}else{
			return dubboMethodDayDao.getMergedList(appName,serviceName,yyyyMMdd);
		}
	}
	
	@Override
	public void migrateFromRedisToMysql(String yyyyMMDD){
		Set<String> keys=DubboMethodDayRedisDao.getKeysByDate(yyyyMMDD);
		
		Set<String> deleteApps=new HashSet<String>();
		for(String key:keys){
			try{
				DubboMethodDay day =DubboMethodDayRedisDao.getByKey(key);
				if(!deleteApps.contains(day.getAppName())){
					//remove existing data of the same day before insert the latest data
					int deletedNum=dubboMethodDayDao.delete(day);
					log.info("removed "+deletedNum+" records in "+day.getShardTableName());
					deleteApps.add(day.getAppName());
				}
				dubboMethodDayDao.insert(day);		
			}catch(Exception e){
				log.error("migrateFromRedisToMysql error. key="+key, e);
			}
		}
	}

	@Override
	public List<DubboMethodDay> selectList(Map<String, Object> params) {
		if(DataSourceUtil.canReadFromRedis(DubboMethodDay.class, DateUtil.yyyyMMdd((Date)params.get("time")))){
			return DubboMethodDayRedisDao.selectList(params);
		}else{
			return dubboMethodDayDao.selectList(params);
		}
	}

	@Override
	public List<DubboMethodDay> selectByMethod(String appName,
			String serviceName, String method, String yyyyMMdd) {
		if(DataSourceUtil.canReadFromRedis(DubboMethodDay.class, yyyyMMdd)){
			return DubboMethodDayRedisDao.selectByMethod(appName,serviceName,method,yyyyMMdd);
		}else{
			return dubboMethodDayDao.selectByMethod(appName,serviceName,method,yyyyMMdd);
		}
	}

	
}

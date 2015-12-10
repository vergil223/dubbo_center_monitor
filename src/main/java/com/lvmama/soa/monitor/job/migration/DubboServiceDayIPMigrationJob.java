package com.lvmama.soa.monitor.job.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvmama.soa.monitor.service.DubboServiceDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;

@Component("dubboServiceDayIPMigrationJob")
public class DubboServiceDayIPMigrationJob {
	private static final Log log=LogFactory.getLog(DubboServiceDayIPMigrationJob.class);
	
	@Autowired
	DubboServiceDayIPService dubboServiceDayIPService;
	
	/**
	 * To run after the target day. 
	 * For example, if the job run on 1:00am Jan 2nd, it will migrate the data of Jan 1st. 
	 */
	public void redisToMysql(){
		log.info("DubboServiceDayIPMigrationJob.redisToMysql() START");
		long start=DateUtil.now().getTime();
		
		String yyyyMMDD=DateUtil.getYesterdayYMD();
		dubboServiceDayIPService.migrateFromRedisToMysql(yyyyMMDD);
		
		long cost = DateUtil.now().getTime()-start;
		log.info("DubboServiceDayIPMigrationJob.redisToMysql() END. costs:"+cost+"ms");
	}
	
	/**
	 * To migrate the data of the day this job run
	 * 
	 */
	public void redisToMysqlToday(){
		log.info("DubboServiceDayIPMigrationJob.redisToMysql() START");
		long start=DateUtil.now().getTime();
		
		String yyyyMMDD=DateUtil.getTodayYMD();
		dubboServiceDayIPService.migrateFromRedisToMysql(yyyyMMDD);
		
		long cost = DateUtil.now().getTime()-start;
		log.info("DubboServiceDayIPMigrationJob.redisToMysql() END. costs:"+cost+"ms");
	}
}

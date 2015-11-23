package com.lvmama.soa.monitor.job.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.util.DateUtil;

@Component("dubboMethodDayIPMigrationJob")
public class DubboMethodDayIPMigrationJob {
	private static final Log log=LogFactory.getLog(DubboMethodDayIPMigrationJob.class);
	
	@Autowired
	DubboMethodDayIPService dubboMethodDayIPService;
	
	/**
	 * To run after the target day. 
	 * For example, if the job run on 1:00am Jan 2nd, it will migrate the data of Jan 1st. 
	 */
	public void redisToMysql(){
		log.info("DubboMethodDayIPMigrationJob.redisToMysql() START");
		long start=DateUtil.now().getTime();
		
		String yyyyMMDD=DateUtil.getYesterdayYMD();
		dubboMethodDayIPService.migrateFromRedisToMysql(yyyyMMDD);
		
		long cost = DateUtil.now().getTime()-start;
		log.info("DubboMethodDayIPMigrationJob.redisToMysql() END. costs:"+cost+"ms");
	}
}

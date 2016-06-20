package com.lvmama.soa.monitor.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvmama.soa.monitor.dao.mybatis.alert.TAltRecordDao;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Component("alertRecordCleanJob")
public class AlertRecordCleanJob {
	private static final Log log=LogFactory.getLog(AlertRecordCleanJob.class);
	
	private static final int DAYS_BEFORE_TO_CLEAN_ALERT_RECORD;
	static{
		DAYS_BEFORE_TO_CLEAN_ALERT_RECORD=StringUtil.isEmpty(PropertyUtil.getProperty("DAYS_BEFORE_TO_CLEAN_ALERT_RECORD"))?7:Integer.valueOf(PropertyUtil.getProperty("DAYS_BEFORE_TO_CLEAN_ALERT_RECORD"));
	}
	
	@Autowired
	private TAltRecordDao tAltRecordDao;
	
	/**
	 * clean T_ALT_RECORD
	 */
	public void clean(){
		long start=DateUtil.now().getTime();
		log.info("AlertRecordCleanJob START");
		
		Date insertTime=DateUtil.changeHHmm(DateUtil.daysBefore(DAYS_BEFORE_TO_CLEAN_ALERT_RECORD), "2359");
		tAltRecordDao.deleteBeforeTime(insertTime);
		
		long cost=DateUtil.now().getTime()-start;
		log.info("AlertRecordCleanJob END. cost:"+cost+"ms");
	}
	
}

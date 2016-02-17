package com.lvmama.soa.monitor.job;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lvmama.soa.monitor.dao.mybatis.DBDao;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;
import com.lvmama.soa.monitor.util.StringUtil;

@Component("bigColumnCleanJob")
public class BigColumnCleanJob {
	private static final Log log=LogFactory.getLog(BigColumnCleanJob.class);
	
	private static final int DAYS_BEFORE_TO_CLEAN;
	static{
		DAYS_BEFORE_TO_CLEAN=StringUtil.isEmpty(PropertyUtil.getProperty("DAYS_BEFORE_TO_CLEAN"))?60:Integer.valueOf(PropertyUtil.getProperty("DAYS_BEFORE_TO_CLEAN"));
	}
	
	@Autowired
	private DBDao dbDao;
	
	/**
	 * clean DUBBO_METHOD_DAY_IP_XXX and DUBBO_SERVICE_DAY_IP_XXX, to set below big column as null for old data:
	 * SUCCESS_TIMES_DETAIL
	 * FAIL_TIMES_DETAIL
	 * ELAPSED_TOTAL_DETAIL
	 * ELAPSED_MAX_DETAIL
	 */
	public void clean(){
		long start=DateUtil.now().getTime();
		log.info("BigColumnCleanJob START");
		
		doClean("DUBBO_METHOD_DAY_IP_%");
		doClean("DUBBO_SERVICE_DAY_IP_%");
		
		long cost=DateUtil.now().getTime()-start;
		log.info("BigColumnCleanJob END. cost:"+cost+"ms");
	}
	
	private void doClean(String tableNameLike){
		long start=DateUtil.now().getTime();
		log.info("BigColumnCleanJob.doClean START for table:["+tableNameLike+"], DAYS_BEFORE_TO_CLEAN="+DAYS_BEFORE_TO_CLEAN);
		
		List<String> tableNames=Collections.EMPTY_LIST;
		Date dateBeforeToClean=null;
		try{
			tableNames=dbDao.getTableNames(tableNameLike);
			dateBeforeToClean=DateUtil.daysBefore(DAYS_BEFORE_TO_CLEAN);
		}catch(Exception e){
			log.error("error in BigColumnCleanJob.doClean:["+tableNameLike+"], DAYS_BEFORE_TO_CLEAN="+DAYS_BEFORE_TO_CLEAN);
			return;
		}
		
		for(String tableName:tableNames){
				System.out.println(tableName);
				doCleanColumn(tableName, dateBeforeToClean);
				doOptimizeTable(tableName);
		}
		
		long cost=DateUtil.now().getTime()-start;
		log.info("BigColumnCleanJob.doClean END for table:["+tableNameLike+"]. cost:"+cost+"ms");
	}

	private void doOptimizeTable(String tableName) {
		try {
			long start = DateUtil.now().getTime();
			log.info("BigColumnCleanJob.doOptimizeTable START for table:["
					+ tableName + "]");

			dbDao.optimizeTable(tableName);

			long cost = DateUtil.now().getTime() - start;
			log.info("BigColumnCleanJob.doOptimizeTable END for table:["
					+ tableName + "]. cost:" + cost + "ms");
		} catch (Exception e) {
			log.error("error when doOptimizeTable() for table:[" + tableName
					+ "]", e);
		}
	}

	private void doCleanColumn(String tableName, Date dateBeforeToClean) {
		try {
			long start = DateUtil.now().getTime();
			log.info("BigColumnCleanJob.doCleanColumn START for table:["
					+ tableName + "], date before:[" + dateBeforeToClean + "]");

			dbDao.cleanBigColumn(tableName, dateBeforeToClean);

			long cost = DateUtil.now().getTime() - start;
			log.info("BigColumnCleanJob.doCleanColumn END for table:["
					+ tableName + "], date before:[" + dateBeforeToClean
					+ "]. cost:" + cost + "ms");
		} catch (Exception e) {
			log.error("error when doCleanColumn for table:[" + tableName
					+ "], date before:[" + dateBeforeToClean + "]", e);
		}
	}
	
}

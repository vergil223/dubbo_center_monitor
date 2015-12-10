package com.lvmama.test.soa.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.job.migration.DubboServiceDayIPMigrationJob;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class DubboServiceDayIPMigrationJobTest extends BaseTest{
	@Autowired
	private DubboServiceDayIPMigrationJob dubboServiceDayIPMigrationJob;
	
	@Test
	public void testMigrateYesterday(){
		dubboServiceDayIPMigrationJob.redisToMysql();
	}
	
	@Test
	public void testMigrateToday(){
		dubboServiceDayIPMigrationJob.redisToMysqlToday();
	}
}

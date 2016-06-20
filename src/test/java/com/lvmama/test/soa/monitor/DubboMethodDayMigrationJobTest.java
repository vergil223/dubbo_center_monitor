package com.lvmama.test.soa.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.job.migration.DubboMethodDayMigrationJob;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class DubboMethodDayMigrationJobTest extends BaseTest{
	@Autowired
	private DubboMethodDayMigrationJob dubboMethodDayMigrationJob;
	
	@Test
	public void testMigrateYesterday(){
		dubboMethodDayMigrationJob.redisToMysql();
	}
	
	@Test
	public void testMigrateToday(){
		dubboMethodDayMigrationJob.redisToMysqlToday();
	}
}

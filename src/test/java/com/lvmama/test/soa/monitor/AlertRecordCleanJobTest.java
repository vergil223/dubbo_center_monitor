package com.lvmama.test.soa.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.dao.mybatis.alert.TAltRecordDao;
import com.lvmama.soa.monitor.job.AlertRecordCleanJob;
import com.lvmama.soa.monitor.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class AlertRecordCleanJobTest extends BaseTest{
	@Autowired
	AlertRecordCleanJob alertRecordCleanJob;
	
	@Autowired
	TAltRecordDao tAltRecordDao;
	
	@Test
	public void testDao()throws Exception{
		tAltRecordDao.deleteBeforeTime(DateUtil.changeHHmm(DateUtil.daysBefore(2), "1415"));
	}
	
	@Test
	public void testJob()throws Exception{
		alertRecordCleanJob.clean();
	}
	
}

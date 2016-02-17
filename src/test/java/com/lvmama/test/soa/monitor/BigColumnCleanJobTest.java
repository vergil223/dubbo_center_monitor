package com.lvmama.test.soa.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.job.BigColumnCleanJob;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class BigColumnCleanJobTest extends BaseTest{
	@Autowired
	BigColumnCleanJob bigColumnCleanJob;
	
	@Test
	public void testJob()throws Exception{
		bigColumnCleanJob.clean();
	}
	
}

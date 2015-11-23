package com.lvmama.test.soa.monitor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.lvmama.soa.monitor.util.SpringUtil;


public class BaseTest implements ApplicationContextAware{

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringUtil.setContext(applicationContext);
	}
	
}

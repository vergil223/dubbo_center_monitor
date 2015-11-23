package com.lvmama.soa.monitor.util;

import org.springframework.context.ApplicationContext;

public class SpringUtil {
	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext context) {
		SpringUtil.context = context;
	}
}

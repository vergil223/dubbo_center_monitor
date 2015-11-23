package com.lvmama.soa.monitor.web.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.lvmama.soa.monitor.util.SpringUtil;

public class LvContextLoaderListener extends ContextLoaderListener {

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 * @param event
	 *            ServletContextEvent
	 */
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);

		WebApplicationContext context = (WebApplicationContext) event
				.getServletContext()
				.getAttribute(
						WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		SpringUtil.setContext(context);
	}
}

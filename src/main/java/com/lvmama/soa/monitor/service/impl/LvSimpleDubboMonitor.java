package com.lvmama.soa.monitor.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;
import com.lvmama.soa.monitor.cache.AppMinuteCache;
import com.lvmama.soa.monitor.cache.MethodDayIPCache;
import com.lvmama.soa.monitor.cache.ServiceDayIPCache;
import com.lvmama.soa.monitor.entity.DubboMethodMinuteIP;
import com.lvmama.soa.monitor.service.LvDubboMonitor;
import com.lvmama.soa.monitor.util.DateUtil;

@Service("lvSimpleDubboMonitor")
public class LvSimpleDubboMonitor implements LvDubboMonitor {
	Log log = LogFactory.getLog(LvSimpleDubboMonitor.class);
	
	@Override
	public void collect(URL statistics) {
		try {
			String timestamp = statistics.getParameter(Constants.TIMESTAMP_KEY);
			Date now;
			if (timestamp == null || timestamp.length() == 0) {
				now = new Date();
			} else if (timestamp.length() == "yyyyMMddHHmmss".length()) {
				now = new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp);
			} else {
				now = new Date(Long.parseLong(timestamp));
			}
			now=DateUtil.trimToMin(now);

			if (fromConsumer(statistics)) {
				//so far, don't collect consumer side statistics
				
				/*DubboMethodMinuteIP minute = new DubboMethodMinuteIP();

				minute.setTime(now);
				minute.setAppName(statistics.getParameter("appName"));
				minute.setService(statistics.getServiceInterface());
				minute.setMethod(statistics.getParameter(MonitorService.METHOD));
				String consumer = statistics.getHost();
                String provider = statistics.getParameter(MonitorService.PROVIDER);
				minute.setSuccessTimes(Long.valueOf(statistics.getParameter(
						MonitorService.SUCCESS, 0)));
				minute.setFailTimes(Long.valueOf(statistics.getParameter(
						MonitorService.FAILURE, 0)));
				minute.setElapsedTotal(Long.valueOf(statistics.getParameter(
						MonitorService.ELAPSED, 0)));
				minute.setElapsedMax(Long.valueOf(statistics.getParameter(
						MonitorService.MAX_ELAPSED, 0)));*/
			} else {
				DubboMethodMinuteIP minute = new DubboMethodMinuteIP();

				minute.setTime(now);
				minute.setAppName(statistics.getParameter("appName"));
				minute.setService(statistics.getServiceInterface());
				minute.setMethod(statistics.getParameter(MonitorService.METHOD));
				String consumerIP = statistics.getParameter(MonitorService.CONSUMER);
                int i = consumerIP.indexOf(':');
                if (i > 0) {
                	consumerIP = consumerIP.substring(0, i);
                }
                String providerIP = statistics.getHost();
				minute.setConsumerIP(consumerIP);
				minute.setProviderIP(providerIP);
				minute.setSuccessTimes(Long.valueOf(statistics.getParameter(
						MonitorService.SUCCESS, 0L)));
				minute.setFailTimes(Long.valueOf(statistics.getParameter(
						MonitorService.FAILURE, 0L)));
				minute.setElapsedTotal(Long.valueOf(statistics.getParameter(
						MonitorService.ELAPSED, 0L)));
				minute.setElapsedMax(Long.valueOf(statistics.getParameter(
						MonitorService.MAX_ELAPSED, 0L)));

				MethodDayIPCache.updateProviderCache(minute);
				ServiceDayIPCache.updateProviderCache(minute);
				AppMinuteCache.updateProviderAppCache(minute);
			}
		} catch (Throwable t) {
			log.error("LvSimpleDubboMonitor.collect() error", t);
		}
	}

	private boolean fromConsumer(URL statistics) {
		return statistics.hasParameter(MonitorService.PROVIDER);
	}

}

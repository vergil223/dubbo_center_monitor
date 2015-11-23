package com.lvmama.soa.monitor.service;

import com.alibaba.dubbo.common.URL;

public interface LvDubboMonitor {
	public void collect(URL statistics);
}

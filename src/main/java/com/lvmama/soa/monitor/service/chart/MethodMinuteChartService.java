package com.lvmama.soa.monitor.service.chart;

import java.util.Map;

public interface MethodMinuteChartService {
	public String drawSuccessChart(Map<String,Object> param);
	
	public String drawFailChart(Map<String, Object> param);

	public String drawElapsedAvgChart(Map<String, Object> param);

	public String drawElapsedMaxChart(Map<String, Object> param);
}

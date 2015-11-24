package com.lvmama.soa.monitor.pub.mapreduce;

public interface Reducer<RK,RV,RR> {
	public void reduce(RK key,RV value, Context<RK,RV,RR> context);
	
	public RR getReduceResult();
}

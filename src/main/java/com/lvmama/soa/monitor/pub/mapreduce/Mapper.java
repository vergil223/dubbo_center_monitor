package com.lvmama.soa.monitor.pub.mapreduce;

import java.util.Map;

public interface Mapper<IK,IV,RK,RV,RR> {
	public void map(IK key,IV value,Context<RK,RV,RR> context);
	
	public Map<RK,RV> getMapResult();
}

package com.lvmama.soa.monitor.pub.mapreduce;

import java.util.List;

public interface Combiner<RR,FR> {
	public FR combine(List<RR> reduceResult);
}

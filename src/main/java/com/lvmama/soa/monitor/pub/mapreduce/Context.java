package com.lvmama.soa.monitor.pub.mapreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Context<RK,RV,R> {
	private List<Map<RK,RV>> mapResults=new ArrayList<Map<RK,RV>>();
	private List<R> reduceResult=new ArrayList<R>();
	
	public List<Map<RK, RV>> getMapResults() {
		return mapResults;
	}
	public void setMapResults(List<Map<RK, RV>> mapResult) {
		this.mapResults = mapResult;
	}
	public List<R> getReduceResult() {
		return reduceResult;
	}
	public void setReduceResult(List<R> reduceResult) {
		this.reduceResult = reduceResult;
	}
}

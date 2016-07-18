package com.lvmama.soa.monitor.web.vo;

import java.util.ArrayList;
import java.util.List;

public class  PageResult <T>{
	private long totalCount=0;
	private List resultList=new ArrayList<T>();
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}

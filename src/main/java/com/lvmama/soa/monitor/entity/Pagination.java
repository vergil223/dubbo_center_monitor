package com.lvmama.soa.monitor.entity;

public class Pagination {
	Integer pageSize=10;
	Integer currentPage=1;
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	
	public Integer getLimitFrom(){
		if(pageSize==null||currentPage==null){
			return 0;
		}
		
		return pageSize*(currentPage-1);
	}
}

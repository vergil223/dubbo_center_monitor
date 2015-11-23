package com.lvmama.soa.monitor.entity;

import java.util.Date;

public class DubboAppMinute {
	private Long id_;
	private String appName; 
	private Date time;
	private Long successTimes=0L;
	private Long failTimes=0L;
	private Long elapsedAvg=0L;
	private Long elapsedMax=0L;
	
	public String toString() {
		return this.getClass()+"@" + System.identityHashCode(this) + " id_=["
				+ id_ + "]appName=[" + appName + "]time=[" + time
				+ "]successTimes=[" + successTimes + "]failTimes[" + failTimes
				+ "]elapsedAvg=[" + elapsedAvg + "]elapsedMax=[" + elapsedMax
				+ "]";
	}
	
	public Long getId_() {
		return id_;
	}
	public void setId_(Long id_) {
		this.id_ = id_;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Long getSuccessTimes() {
		return successTimes;
	}
	public void setSuccessTimes(Long successTimes) {
		this.successTimes = successTimes;
	}
	public Long getFailTimes() {
		return failTimes;
	}
	public void setFailTimes(Long failTimes) {
		this.failTimes = failTimes;
	}
	public Long getElapsedAvg() {
		return elapsedAvg;
	}
	public void setElapsedAvg(Long elapsedAvg) {
		this.elapsedAvg = elapsedAvg;
	}
	public Long getElapsedMax() {
		return elapsedMax;
	}
	public void setElapsedMax(Long elapsedMax) {
		this.elapsedMax = elapsedMax;
	}
	
}

package com.lvmama.soa.monitor.entity;

import java.math.BigDecimal;
import java.util.Date;

public class DubboMethodMinuteIP {
	private Long id_;
	private String appName; 
	private String service;
	private String method;
	private String consumerIP;
	private String providerIP;
	private Date time;
	private Long successTimes=0L;
	private Long failTimes=0L;
	private BigDecimal elapsedTotal=BigDecimal.ZERO;
	private Long elapsedMax=0L;
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
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
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
	
	public BigDecimal getElapsedTotal() {
		return elapsedTotal;
	}
	public void setElapsedTotal(BigDecimal elapsedTotal) {
		this.elapsedTotal = elapsedTotal;
	}
	public Long getElapsedMax() {
		return elapsedMax;
	}
	public void setElapsedMax(Long elapsedMax) {
		this.elapsedMax = elapsedMax;
	}
	public String getConsumerIP() {
		return consumerIP;
	}
	public void setConsumerIP(String consumerIP) {
		this.consumerIP = consumerIP;
	}
	public String getProviderIP() {
		return providerIP;
	}
	public void setProviderIP(String providerIP) {
		this.providerIP = providerIP;
	}
	
	
	
}

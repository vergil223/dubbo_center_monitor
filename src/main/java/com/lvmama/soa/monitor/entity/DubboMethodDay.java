package com.lvmama.soa.monitor.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

public class DubboMethodDay implements Shardable,java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id_;
	private String appName; 
	private String service;
	private String method;
	private Date time;
	private Long successTimes=0L;
	private Long failTimes=0L;
	private BigDecimal elapsedAvg=BigDecimal.ZERO;
	private Long elapsedMax=0L;
	private String successTimesDetail="";
	private String failTimesDetail="";
	private String elapsedTotalDetail="";
	private String elapsedMaxDetail="";
	
	public DubboMethodDay(){}
	
	public DubboMethodDay(String appName,
			String service, String method,  Date time) {
		this.appName=appName;
		this.time=time;
		this.service=service;
		this.method=method;
	}
	
	public String toString() {
		return this.getClass()+"@" + System.identityHashCode(this) + " id_=["
				+ id_ + "]appName=[" + appName + "]service=[" + service
				+ "]method=[" + method
				+ "]time=[" + time
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
	
	public BigDecimal getElapsedAvg() {
		return elapsedAvg;
	}

	public void setElapsedAvg(BigDecimal elapsedAvg) {
		this.elapsedAvg = elapsedAvg;
	}

	public Long getElapsedMax() {
		return elapsedMax;
	}
	public void setElapsedMax(Long elapsedMax) {
		this.elapsedMax = elapsedMax;
	}
	public String getSuccessTimesDetail() {
		return successTimesDetail;
	}
	public void setSuccessTimesDetail(String successTimesDetail) {
		this.successTimesDetail = successTimesDetail;
	}
	public String getFailTimesDetail() {
		return failTimesDetail;
	}
	public void setFailTimesDetail(String failTimesDetail) {
		this.failTimesDetail = failTimesDetail;
	}
	public String getElapsedTotalDetail() {
		return elapsedTotalDetail;
	}
	public void setElapsedTotalDetail(String elapsedTotalDetail) {
		this.elapsedTotalDetail = elapsedTotalDetail;
	}
	public String getElapsedMaxDetail() {
		return elapsedMaxDetail;
	}
	public void setElapsedMaxDetail(String elapsedMaxDetail) {
		this.elapsedMaxDetail = elapsedMaxDetail;
	}
	
	public String uniqueKey(){
		return "com.lvmama.soa.monitor.entity.DubboMethodDay_"+this.appName+"_"+this.service+"_"+this.method+"_"+DateUtil.yyyyMMdd(this.time);
	}
	
	public String getShardTableName(){
		return ("DUBBO_METHOD_DAY"+"_"+this.appName.replaceAll("-", "_")).toUpperCase();
	}
	
	public static DubboMethodDay merge(DubboMethodDay newDay, DubboMethodDay oldDay) {
		return merge(newDay, oldDay,true);
	}
	
	public static DubboMethodDay merge(DubboMethodDay newDay, DubboMethodDay oldDay,boolean needMergeDetail) {
		if(oldDay.getSuccessTimes()>0||newDay.getSuccessTimes()>0){
			oldDay.setElapsedAvg((new BigDecimal(oldDay.getSuccessTimes()).multiply(oldDay.getElapsedAvg()).add(new BigDecimal(newDay
					.getSuccessTimes()).multiply(newDay.getElapsedAvg()))).divide(new BigDecimal(oldDay.getSuccessTimes()+newDay.getSuccessTimes()),4,BigDecimal.ROUND_HALF_UP));
		}
		oldDay.setSuccessTimes(oldDay.getSuccessTimes() + newDay.getSuccessTimes());
		oldDay.setFailTimes(oldDay.getFailTimes() + newDay.getFailTimes());
		oldDay.setElapsedMax(Math.max(oldDay.getElapsedMax(),
				newDay.getElapsedMax()));
		
		if(needMergeDetail){
			oldDay.setSuccessTimesDetail(DubboDetailUtil.mergeDetailToStr(
					oldDay.getSuccessTimesDetail(), newDay.getSuccessTimesDetail(),
					false));
			oldDay.setFailTimesDetail(DubboDetailUtil.mergeDetailToStr(oldDay.getFailTimesDetail(),
					newDay.getFailTimesDetail(), false));
			oldDay.setElapsedTotalDetail(DubboDetailUtil.mergeDetailToStr(
					oldDay.getElapsedTotalDetail(), newDay.getElapsedTotalDetail(),
					false));
			oldDay.setElapsedMaxDetail(DubboDetailUtil.mergeDetailToStr(oldDay.getElapsedMaxDetail(),
					newDay.getElapsedMaxDetail(), true));
		}
		
		return oldDay;
	}
}

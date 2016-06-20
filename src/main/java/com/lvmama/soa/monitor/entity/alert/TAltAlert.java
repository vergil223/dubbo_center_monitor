package com.lvmama.soa.monitor.entity.alert;

public class TAltAlert implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id_;
	private String target;
	private String targetExclude;
	private String conditionIds;
	private String conditionParam;
	private String actionIds;
	private String actionParam;
	private String name;
	private String description;
	private String enabled;
	public Long getId_() {
		return id_;
	}
	public void setId_(Long id_) {
		this.id_ = id_;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getTargetExclude() {
		return targetExclude;
	}
	public void setTargetExclude(String targetExclude) {
		this.targetExclude = targetExclude;
	}
	public String getConditionIds() {
		return conditionIds;
	}
	public void setConditionIds(String conditionIds) {
		this.conditionIds = conditionIds;
	}
	public String getConditionParam() {
		return conditionParam;
	}
	public void setConditionParam(String conditionParam) {
		this.conditionParam = conditionParam;
	}
	public String getActionIds() {
		return actionIds;
	}
	public void setActionIds(String actionIds) {
		this.actionIds = actionIds;
	}
	public String getActionParam() {
		return actionParam;
	}
	public void setActionParam(String actionParam) {
		this.actionParam = actionParam;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
}

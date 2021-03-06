package com.lvmama.soa.monitor.web.vo.restful;

import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Response {
	private String status;
	private String errorMsg;
	private Map body;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map getBody() {
		return body;
	}

	public void setBody(Map body) {
		this.body = body;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String toJSON(){
		return JSON.toJSONString(this);
	}
}

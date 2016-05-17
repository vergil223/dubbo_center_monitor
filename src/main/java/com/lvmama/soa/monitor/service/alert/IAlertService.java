package com.lvmama.soa.monitor.service.alert;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface IAlertService {
	public void alert(Map<String,Object> param);
}

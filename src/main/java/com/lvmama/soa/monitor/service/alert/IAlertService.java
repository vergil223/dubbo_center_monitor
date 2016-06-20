package com.lvmama.soa.monitor.service.alert;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.entity.alert.TAltAlert;

@Service
public interface IAlertService {
	public void alert(Map<String,Object> param);
	
	public List<TAltAlert> findAllAlert();
}

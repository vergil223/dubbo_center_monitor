package com.lvmama.soa.monitor.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.dao.mybatis.DubboAppMinuteDao;
import com.lvmama.soa.monitor.entity.DubboAppMinute;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;

@Service("dubboAppMinuteService")
public class DubboAppMinuteServiceImpl implements
		DubboAppMinuteService {
	@Autowired
	DubboAppMinuteDao dubboAppMinuteDaoDao;
	
	@Override
	public int insertOrAppend(DubboAppMinute app) {
		if (app == null) {
			return 0;
		}

		int count = dubboAppMinuteDaoDao.append(app);
		if (count == 0) {
			count = dubboAppMinuteDaoDao.insert(app);
		}
		if(count==0){
			//if multiple processes do insert together, only one process will success. In this situation, need to update again.
			count=dubboAppMinuteDaoDao.append(app);
		}

		return count;
	}

	@Override
	public List<DubboAppMinute> selectList(Map map) {
		return dubboAppMinuteDaoDao.selectList(map);
	}
	
	@Override
	public List<String> getAppNames(){
		return dubboAppMinuteDaoDao.getAppNames();
	}

	
}

package com.lvmama.soa.monitor.web.controller.alert;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.lvmama.soa.monitor.constant.Enabled;
import com.lvmama.soa.monitor.entity.alert.TAltAlert;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.service.alert.impl.AlertService;

@Controller
@RequestMapping("/alertConf")
public class AlertConfController {
	private static final Log log = LogFactory.getLog(AlertConfController.class);
	
	@Autowired
	private IAlertRecordService iAlertRecordService; 
	
	@Autowired
	private AlertService methodDayAlertService;
	
	@RequestMapping("/enableList")
	@ResponseBody
	public List<String> enableList(){
		List<String> enableList = Arrays.asList(Enabled.Y,Enabled.N);
		return enableList;
	}
	
	@RequestMapping("/listAlert")
	@ResponseBody
	public List<TAltAlert> listAlert(){
		List<TAltAlert> result = methodDayAlertService.findAllAlert();
		return result;
	}
	
	@RequestMapping("/loadById/{id_}")
	@ResponseBody
	public TAltAlert loadById(@PathVariable Long id_){
		TAltAlert result=methodDayAlertService.loadById(id_);
		return result;
	}
	
//	var operateType={
//            'delete':"删除",
//            'enable':'启用',
//            'disable':'禁用'
//    }
	
	@RequestMapping("/delete/{id_}")
	@ResponseBody
	public String delete(@PathVariable Long id_){
		if(id_!=null){
			methodDayAlertService.batchDelete(id_.toString());
			return JSON.toJSONString("删除成功");
		}
		return JSON.toJSONString("删除失败");
	}
	
	@RequestMapping("/enable/{id_}")
	@ResponseBody
	public String enable(@PathVariable Long id_){
		if(id_!=null){
			methodDayAlertService.batchEnable(id_.toString());
			return JSON.toJSONString("启用成功");
		}
		return JSON.toJSONString("启用失败");
	}
	
	@RequestMapping("/disable/{id_}")
	@ResponseBody
	public String disable(@PathVariable Long id_){
		if(id_!=null){
			methodDayAlertService.batchDisable(id_.toString());
			return JSON.toJSONString("禁用成功");
		}
		return JSON.toJSONString("禁用成功");
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public TAltAlert save(@RequestBody TAltAlert tAltAlert){
		TAltAlert result = methodDayAlertService.saveOrUpdate(tAltAlert);
		return result;
	}
	
	@RequestMapping("/batchDelete")
	@ResponseBody
	public String batchDelete(String ids){
		if(ids==null){
			return JSON.toJSONString("请选择需要删除的记录");
		}
		
		methodDayAlertService.batchDelete(ids);
		
		return JSON.toJSONString("删除成功");
	}
	
	@RequestMapping("/batchDisable")
	@ResponseBody
	public String batchDisable(String ids){
		if(ids==null){
			return JSON.toJSONString("请选择需要禁用的记录");
		}
		
		methodDayAlertService.batchDisable(ids);
		
		return JSON.toJSONString("禁用成功");
	}
	
	@RequestMapping("/batchEnable")
	@ResponseBody
	public String batchEnable(String ids){
		if(ids==null){
			return JSON.toJSONString("请选择需要启用的记录");
		}
		
		methodDayAlertService.batchEnable(ids);
		
		return JSON.toJSONString("启用成功");
	}
	
}

package com.lvmama.soa.monitor.service.alert.action.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import com.alibaba.dubbo.remoting.zookeeper.zkclient.ZkclientZookeeperTransporter;
import com.lvmama.soa.monitor.constant.alert.AlertParamKey;
import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.entity.alert.TAltRecord;
import com.lvmama.soa.monitor.service.alert.IAlertRecordService;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;
import com.lvmama.soa.monitor.util.SpringUtil;

/**
 * 为这个方法在zookeeper里增加强制Mock规则。
 * 需要配置的参数：
 * mock: mock内容，比如return null, return 0
 * recoverMockSeconds:恢复时间（秒）
 * @author Administrator
 *
 */
public class MethodMockAction extends AbstractAction{
	private static final Log log = LogFactory.getLog(MethodMockAction.class);
	
	private final String root="/dubbo/";
	
	private final ZookeeperClient zookeeperClient;
	
	private static final String ZKHOST_DEFAULT = "127.0.0.1";
	private static final int ZKPORT_DEFAULT = 20880;
	
	public MethodMockAction(){
		String zkHost=ZKHOST_DEFAULT;
		int zkPort=ZKPORT_DEFAULT;
		try{
			String zkAddress=PropertyUtil.getProperty("dubbo.registry.address","127.0.0.1:20880");
			
			String[] zkHostAndPort=zkAddress.split(":");
			if(zkHostAndPort.length==2){
				zkHost=zkHostAndPort[0];
				zkPort=Integer.valueOf(zkHostAndPort[1]);
			}
		}catch(Exception e){
			zkHost=ZKHOST_DEFAULT;
			zkPort=ZKPORT_DEFAULT;
			log.error("error when load zookeeper address from properties, will use default value:"+zkHost+":"+zkPort);
		}
		
		URL zookeeperUrl=new URL("zookeeper",zkHost,zkPort);
		ZkclientZookeeperTransporter zookeeperTransporter=new ZkclientZookeeperTransporter(); 
		zookeeperClient=zookeeperTransporter.connect(zookeeperUrl);
	}
	@Override
	protected void doAction(Map<String, Object> param,
			Map<String, String> actionParam) {
		DubboMethodDay dubboMethodDay=(DubboMethodDay)param.get(AlertParamKey.DUBBO_METHOD_DAY);
		if(dubboMethodDay==null){
			return;
		}
		if(StringUtils.isBlank(dubboMethodDay.getService())||StringUtils.isBlank(dubboMethodDay.getMethod())){
			return;
		}
		
		StringBuilder sb = new StringBuilder();
        sb.append(Constants.OVERRIDE_PROTOCOL);
        sb.append("://");
        sb.append(Constants.ANYHOST_VALUE);
        sb.append("/");
        sb.append(dubboMethodDay.getService());
        sb.append("?");
        Map<String, String> overrideParam = new HashMap<String, String>();
        overrideParam.put(Constants.CATEGORY_KEY, Constants.CONFIGURATORS_CATEGORY);
        overrideParam.put(Constants.ENABLED_KEY, "true");
        overrideParam.put(Constants.DYNAMIC_KEY, "false");
        overrideParam.put(dubboMethodDay.getMethod()+"."+Constants.MOCK_KEY, URL.encode("force:"+actionParam.get("mock")));
        sb.append(StringUtils.toQueryString(overrideParam));
        String mockUrl = root+dubboMethodDay.getService()+"/configurators/"+URL.encode(sb.toString());
        
		startMock(mockUrl);
		log.info("Force mock for method:"+dubboMethodDay.getService()+"."+dubboMethodDay.getMethod()+"["+actionParam.get("mock")+"]");
		
		IAlertRecordService iAlertRecordService=(IAlertRecordService)SpringUtil.getContext().getBean("alertRecordService");
		TAltRecord tAltRecord=new TAltRecord();
		tAltRecord.setAppName(dubboMethodDay.getAppName());
		tAltRecord.setService(dubboMethodDay.getService());
		tAltRecord.setMethod(dubboMethodDay.getMethod());
		tAltRecord.setInsertTime(DateUtil.now());
		tAltRecord.setAlertMsg("Force mock for method:"+dubboMethodDay.getService()+"."+dubboMethodDay.getMethod()+"["+actionParam.get("mock")+"]");
		iAlertRecordService.insert(tAltRecord);							
		
		//过2分钟后再取消mock
        ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new endMockRunnable(mockUrl), Integer.valueOf(actionParam.get("recoverMockSeconds")), TimeUnit.SECONDS);
        scheduledExecutorService.shutdown();
	}
	
	private class endMockRunnable implements Runnable{
		private String urlStr;
		
		public endMockRunnable(String urlStr) {
			super();
			this.urlStr = urlStr;
		}

		@Override
		public void run() {
			try{
				endMock(urlStr);
				log.info("End mock :"+urlStr);
			}catch(Exception e){
				log.error("error when run endMockRunnable, url:"+urlStr,e);
			}
		}
		
	}
	
	private void startMock(String urlStr){
		//如果开启mock之后本应用掉线，就自动关闭mock，所以这里用临时节点
		zookeeperClient.create(urlStr, true);
	}
	
	private void endMock(String urlStr){
		zookeeperClient.delete(urlStr);
	}
}



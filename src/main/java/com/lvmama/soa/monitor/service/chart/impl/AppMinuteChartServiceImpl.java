package com.lvmama.soa.monitor.service.chart.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lvmama.soa.monitor.constant.ChartConst;
import com.lvmama.soa.monitor.dao.mybatis.DubboAppMinuteDao;
import com.lvmama.soa.monitor.entity.DubboAppMinute;
import com.lvmama.soa.monitor.service.DubboAppMinuteService;
import com.lvmama.soa.monitor.service.chart.AppMinuteChartService;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;

@Service("appMinuteChartService")
public class AppMinuteChartServiceImpl implements AppMinuteChartService{
	Log log = LogFactory.getLog(AppMinuteChartServiceImpl.class);
	
	private static final int DATA_PERIOD_MS = 24*60*60*1000;
	
	@Autowired
	DubboAppMinuteService dubboProviderAppService;
	@Autowired
	DubboAppMinuteDao dubboProviderAppDao;
	
	private String draw(String appName, String chartType,TimeSeries timeseries){
		TimeSeriesCollection xydataset = new TimeSeriesCollection();
		xydataset.addSeries(timeseries);
		
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("["+appName+"] "+chartType, "time", chartType, xydataset, true, true, false);
		jfreechart.setBackgroundPaint(Color.WHITE);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setDomainGridlinePaint(Color.GRAY);
        xyplot.setRangeGridlinePaint(Color.GRAY);
        xyplot.setDomainGridlinesVisible(true);
        xyplot.setRangeGridlinesVisible(true);
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        
        BufferedImage image = jfreechart.createBufferedImage(1200, 600);
        
        String chartFullPath = chartFullPath(appName,chartType);
		File methodChartFile = new File(chartFullPath);
        File methodChartDir = methodChartFile.getParentFile();
        if (methodChartDir != null && ! methodChartDir.exists()) {
            methodChartDir.mkdirs();
        }
        try{
        	FileOutputStream output = new FileOutputStream(methodChartFile);
        	try {
        		ImageIO.write(image, "png", output);
        		output.flush();
        	} finally {
        		output.close();
        	}        	
        }catch(Exception e){
        	throw new RuntimeException(e);
        }
        
        return chartFullPath;
	}
	
	private void validParam(Map<String,Object> param){
		Assert.notEmpty(param.get("appName"), "appName");
		Assert.notNull(param.get("time_from"), "time_from");
		Assert.notNull(param.get("time_to"), "time_to");
	}
	
	private List<DubboAppMinute> getData(Map<String, Object> param) {
		List<DubboAppMinute> list=dubboProviderAppService.selectList(param);
		return list;
	}
	
	@Override
	public String chartFullPath(String appName, String chartType) {
		return PropertyUtil.getProperty("chartPath") + DateUtil.getTodayYMD()
				+ File.separator + appName + File.separator
				+ "PROVIDER" + File.separator + appName + "_" + chartType
				+ ".png";
	}
	
	@Override
	public String drawSuccessChart(Map<String,Object> param){
		validParam(param);
		TimeSeries timeseries = new TimeSeries("Success Times");
		for(DubboAppMinute app:getData(param)){
			timeseries.add(new Minute(app.getTime()), app.getSuccessTimes());
		}
		
		return this.draw(param.get("appName").toString(), ChartConst.CHART_TYPE_SUCCESS_TIMES, timeseries);
	}
	
	@Override
	public String drawFailChart(Map<String, Object> param) {
		validParam(param);
		TimeSeries timeseries = new TimeSeries("Fail Times");
		for(DubboAppMinute app:getData(param)){
			timeseries.add(new Minute(app.getTime()), app.getFailTimes());
		}
		
		return this.draw(param.get("appName").toString(), ChartConst.CHART_TYPE_FAIL_TIMES, timeseries);
	
	}

	@Override
	public String drawElapsedAvgChart(Map<String, Object> param) {
		validParam(param);
		TimeSeries timeseries = new TimeSeries("Elapsed Avg");
		for(DubboAppMinute app:getData(param)){
			timeseries.add(new Minute(app.getTime()), app.getElapsedAvg());
		}
		
		return this.draw(param.get("appName").toString(), ChartConst.CHART_TYPE_ELAPSED_AVG, timeseries);
	}

	@Override
	public String drawElapsedMaxChart(Map<String, Object> param) {
		validParam(param);
		TimeSeries timeseries = new TimeSeries("Elapsed Max");
		for(DubboAppMinute app:getData(param)){
			timeseries.add(new Minute(app.getTime()), app.getElapsedMax());
		}
		
		return this.draw(param.get("appName").toString(), ChartConst.CHART_TYPE_ELAPSED_MAX, timeseries);
	}

	@Override
	public void drawRecentChart() {
		log.info("ProviderAppChartServiceImpl.drawRecentChart() START.");
		Date now=DateUtil.now();
		Date from=new Date(now.getTime()-DATA_PERIOD_MS);

		Map<String,Object> param=new HashMap<String,Object>();
		param.put("time_from", from);
		param.put("time_to", now);			
		
		//TODO get from DB
		List<String> appNames=dubboProviderAppDao.getAppNames();
		for(String appName:appNames){
			param.put("appName", appName);
			this.drawSuccessChart(param);
			this.drawFailChart(param);
			this.drawElapsedAvgChart(param);
			this.drawElapsedMaxChart(param);
		}
		
		log.info("ProviderAppChartServiceImpl.drawRecentChart() END, cost:"+(DateUtil.now().getTime()-now.getTime())+"ms");
		
	}
}

package com.lvmama.soa.monitor.service.chart.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
import com.lvmama.soa.monitor.entity.DubboMethodDayIP;
import com.lvmama.soa.monitor.service.DubboMethodDayIPService;
import com.lvmama.soa.monitor.service.chart.MethodIPMinuteChartService;
import com.lvmama.soa.monitor.util.Assert;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.soa.monitor.util.PropertyUtil;
import com.lvmama.soa.monitor.util.StringUtil;
import com.lvmama.soa.monitor.util.biz.DubboDetailUtil;

@Service("methodIPMinuteChartService")
public class MethodIPMinuteChartServiceImpl implements MethodIPMinuteChartService{
	Log log = LogFactory.getLog(MethodIPMinuteChartServiceImpl.class);
	
	@Autowired
	DubboMethodDayIPService dubboMethodDayIPService;
	
	private String draw(String appName, String serviceName,String methodName,String providerIP,String consumerIP,String chartType,TimeSeries timeseries){
		TimeSeriesCollection xydataset = new TimeSeriesCollection();
		xydataset.addSeries(timeseries);
		
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("["+appName+"-"+serviceName+"-"+methodName+"() "+consumerIP+"->"+providerIP+"] "+chartType, "time", chartType, xydataset, true, true, false);
		jfreechart.setBackgroundPaint(Color.WHITE);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setDomainGridlinePaint(Color.GRAY);
        xyplot.setRangeGridlinePaint(Color.GRAY);
        xyplot.setDomainGridlinesVisible(true);
        xyplot.setRangeGridlinesVisible(true);
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        
        BufferedImage image = jfreechart.createBufferedImage(1200, 600);
        
        String chartFullPath = chartFullPath(appName,serviceName,methodName,chartType);
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
		Assert.notEmpty(param.get("service"), "service");
		Assert.notEmpty(param.get("method"), "method");
		Assert.notNull(param.get("time"), "time");
		
		if(param.get("time_from")==null){
			param.put("time_from", DateUtil.changeHHmm((Date)param.get("time"), "0000"));
		}
		if(param.get("time_to")==null){
			param.put("time_to", DateUtil.changeHHmm((Date)param.get("time"), "2359"));
		}
	}
	
	private List<DubboMethodDayIP> getData(Map<String, Object> param) {
		List<DubboMethodDayIP> list=dubboMethodDayIPService.selectList(param);
		return list;
	}
	
	public String chartFullPath(String appName, String serviceName,
			String methodName, String chartType) {
		return PropertyUtil.getProperty("chartPath") + DateUtil.getTodayYMD()
				+ File.separator + appName + File.separator + "PROVIDER"
				+ File.separator + serviceName + File.separator + methodName
				+ File.separator + methodName + "_" + chartType + ".png";
	}
	
	@Override
	public String drawSuccessChart(Map<String,Object> param){
		validParam(param);
		String hhmmStart=DateUtil.HHmm((Date)param.get("time_from"));
		String hhmmEnd=DateUtil.HHmm((Date)param.get("time_to"));
		
		Date date=null;
		String totalDetail="";
		for(DubboMethodDayIP day:getData(param)){
			if(date==null){
				date=day.getTime();				
			}
			String detail=day.getSuccessTimesDetail();
			totalDetail=DubboDetailUtil.mergeDetailToStr(totalDetail, detail, false);
		}
		
		TimeSeries timeseries = new TimeSeries("Success Times");
		List<List<String>> totalDetailList=DubboDetailUtil.detailStrToList(totalDetail,hhmmStart,hhmmEnd);
		for(List<String> detail:totalDetailList){
			String time=detail.get(0);
			String value=detail.get(1);
			
			timeseries.add(new Minute(DateUtil.changeHHmm(date, time)), Long.valueOf(value));
		}
		
		return this.draw(param.get("appName").toString(), param.get("service").toString(),param.get("method").toString(),param.get("providerIP").toString(),param.get("consumerIP").toString(),ChartConst.CHART_TYPE_SUCCESS_TIMES, timeseries);
	}
	
	@Override
	public String drawFailChart(Map<String, Object> param) {
		validParam(param);
		String hhmmStart=DateUtil.HHmm((Date)param.get("time_from"));
		String hhmmEnd=DateUtil.HHmm((Date)param.get("time_to"));
		
		Date date=null;
		String totalDetail="";
		for(DubboMethodDayIP day:getData(param)){
			if(date==null){
				date=day.getTime();				
			}
			String detail=day.getFailTimesDetail();
			totalDetail=DubboDetailUtil.mergeDetailToStr(totalDetail, detail, false);
		}
		
		TimeSeries timeseries = new TimeSeries("Fail Times");
		List<List<String>> totalDetailList=DubboDetailUtil.detailStrToList(totalDetail,hhmmStart,hhmmEnd);
		for(List<String> detail:totalDetailList){
			String time=detail.get(0);
			String value=detail.get(1);
			
			timeseries.add(new Minute(DateUtil.changeHHmm(date, time)), Long.valueOf(value));
		}
		
		return this.draw(param.get("appName").toString(), param.get("service").toString(),param.get("method").toString(),param.get("providerIP").toString(),param.get("consumerIP").toString(),ChartConst.CHART_TYPE_FAIL_TIMES, timeseries);
	}

	@Override
	public String drawElapsedAvgChart(Map<String, Object> param) {
		validParam(param);
		String hhmmStart=DateUtil.HHmm((Date)param.get("time_from"));
		String hhmmEnd=DateUtil.HHmm((Date)param.get("time_to"));
		
		Date date=null;
		String elapsedTotalDetail="";
		String successTotalDetail="";
		List<DubboMethodDayIP> data = getData(param);
		for(DubboMethodDayIP day:data){
			if(date==null){
				date=day.getTime();				
			}
			elapsedTotalDetail=DubboDetailUtil.mergeDetailToStr(elapsedTotalDetail, day.getElapsedTotalDetail(), false);
			successTotalDetail=DubboDetailUtil.mergeDetailToStr(successTotalDetail, day.getSuccessTimesDetail(), false);
		}
		
		TimeSeries timeseries = new TimeSeries("Elapsed Avg");
		List<List<String>> elapsedTotalDetailList=DubboDetailUtil.detailStrToList(elapsedTotalDetail,hhmmStart,hhmmEnd);
		List<List<String>> successTotalDetailList=DubboDetailUtil.detailStrToList(successTotalDetail,hhmmStart,hhmmEnd);
		for(List<String> successDetail:successTotalDetailList){
			String successTime=successDetail.get(0);
			String successValue=successDetail.get(1);
			if(StringUtil.isEmpty(successTime)||"0".equals(successTime.trim())){
				continue;
			}
			
			for(Iterator<List<String>> iter=elapsedTotalDetailList.iterator();iter.hasNext();){
				List<String> elapsedDetail=iter.next();
				String elapsedTime=elapsedDetail.get(0);
				String elapsedValue=elapsedDetail.get(1);
				if(successTime.equals(elapsedTime)){
					if(successValue!=null&&Long.valueOf(successValue)>0L){
						BigDecimal elapsedAvgValue = new BigDecimal(elapsedValue).divide(new BigDecimal(successValue), 2, BigDecimal.ROUND_HALF_UP);						
						timeseries.add(new Minute(DateUtil.changeHHmm(date, successTime)), elapsedAvgValue);
						break;
					}
				}
			}
		}
		
		return this.draw(param.get("appName").toString(), param.get("service").toString(),param.get("method").toString(),param.get("providerIP").toString(),param.get("consumerIP").toString(),ChartConst.CHART_TYPE_ELAPSED_AVG, timeseries);
	}

	@Override
	public String drawElapsedMaxChart(Map<String, Object> param) {
		validParam(param);
		String hhmmStart=DateUtil.HHmm((Date)param.get("time_from"));
		String hhmmEnd=DateUtil.HHmm((Date)param.get("time_to"));
		
		Date date=null;
		String totalDetail="";
		for(DubboMethodDayIP day:getData(param)){
			if(date==null){
				date=day.getTime();				
			}
			String detail=day.getElapsedMaxDetail();
			totalDetail=DubboDetailUtil.mergeDetailToStr(totalDetail, detail, true);
		}
		
		TimeSeries timeseries = new TimeSeries("Elapsed Max");
		List<List<String>> totalDetailList=DubboDetailUtil.detailStrToList(totalDetail,hhmmStart,hhmmEnd);
		for(List<String> detail:totalDetailList){
			String time=detail.get(0);
			String value=detail.get(1);
			
			timeseries.add(new Minute(DateUtil.changeHHmm(date, time)), Long.valueOf(value));
		}
		
		return this.draw(param.get("appName").toString(), param.get("service").toString(),param.get("method").toString(),param.get("providerIP").toString(),param.get("consumerIP").toString(),ChartConst.CHART_TYPE_ELAPSED_MAX, timeseries);
	}
}

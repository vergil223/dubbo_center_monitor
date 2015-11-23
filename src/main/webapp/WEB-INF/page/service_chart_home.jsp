<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Service Chart</title>
</head>
<%@ include  file="head.jsp"%>
<%@ include  file="menu.jsp"%>
<%
String appName=(String)request.getAttribute("appName"); 
java.util.List<String> appNames=(java.util.List)request.getAttribute("appNames");

String service=request.getAttribute("service")==null?"":(String)request.getAttribute("service");
String dayStr=request.getAttribute("dayStr")==null?"":(String)request.getAttribute("dayStr");
String minuteFrom=request.getAttribute("minuteFrom")==null?"":(String)request.getAttribute("minuteFrom");
String minuteTo=request.getAttribute("minuteTo")==null?"":(String)request.getAttribute("minuteTo");
%>

<body>
<form name="form1" action="<%=request.getContextPath()%>/chart/provider/service/home.do">

application name:
<select name="appName">
<%for(String a:appNames){
	String selected="";
	if(a.equals(appName)){
		selected="selected=\"selected\"";
	}
%>
  <option value ="<%=a%>" <%=selected%>><%=a%></option>
<%}%>
</select>
Service:<input type="text" name="service" value="<%=service%>"/>
Day: <input type="text" id="dayStr" name="dayStr" value="<%=dayStr %>">
HHmm from:<input type="text" name="minuteFrom" value="<%=minuteFrom%>"/>
HHmm to:<input type="text" name="minuteTo" value="<%=minuteTo%>"/>
<input type="submit" value="提交"/>

<br/>
<br/>

<iframe src="<%=request.getContextPath()%>/chart/provider/service/success.do?appName=<%=appName %>&service=<%=service %>&dayStr=<%=dayStr %>&minuteFrom=<%=minuteFrom %>&minuteTo=<%=minuteTo %>" name="app_success"	    height="600" width="80%"></iframe>
<iframe src="<%=request.getContextPath()%>/chart/provider/service/fail.do?appName=<%=appName %>&service=<%=service %>&dayStr=<%=dayStr %>&minuteFrom=<%=minuteFrom %>&minuteTo=<%=minuteTo %>" name="app_fail"	        height="600" width="80%"></iframe>
<iframe src="<%=request.getContextPath()%>/chart/provider/service/elapsedAvg.do?appName=<%=appName %>&service=<%=service %>&dayStr=<%=dayStr %>&minuteFrom=<%=minuteFrom %>&minuteTo=<%=minuteTo %>" name="app_elapsed_avg"	height="600" width="80%"></iframe>
<iframe src="<%=request.getContextPath()%>/chart/provider/service/elapsedMax.do?appName=<%=appName %>&service=<%=service %>&dayStr=<%=dayStr %>&minuteFrom=<%=minuteFrom %>&minuteTo=<%=minuteTo %>" name="app_elapsed_max"	height="600" width="80%"></iframe>
</form>

<%@ include  file="buttom.jsp"%>

<script language="javascript">
$(function() {
    $( "#dayStr" ).datepicker({
    	dateFormat:"yy-mm-dd"
    });
  });
</script>

<script language="javascript">
//window.setInterval("window.open(document.all.app_success.src,'app_success','')",10000);
//window.setInterval("window.open(document.all.app_fail.src,'app_fail','')",10000);
//window.setInterval("window.open(document.all.app_elapsed_avg.src,'app_elapsed_avg','')",10000);
//window.setInterval("window.open(document.all.app_elapsed_max.src,'app_elapsed_max','')",10000);
</script>

</body>
</html>


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.lvmama.soa.monitor.entity.DubboServiceDayIP"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Service Statistics</title>
</head>
<%@ include  file="head.jsp"%>
<%@ include  file="menu.jsp"%>
<%
String appName=(String)request.getAttribute("appName"); 
java.util.List<String> appNames=(java.util.List)request.getAttribute("appNames");

java.util.List<DubboServiceDayIP> serviceDayList=(java.util.List)request.getAttribute("serviceDayList");
String dayStr=request.getAttribute("dayStr")==null?"":(String)request.getAttribute("dayStr");
%>

<body>
<form name="form1" action="<%=request.getContextPath()%>/list/provider/service/service_day_list.do">

application name:
<select name="appName">
<%
for(String a:appNames){
	String selected="";
	if(a.equals(appName)){
		selected="selected=\"selected\"";
	}
%>
  <option value ="<%=a%>" <%=selected%>><%=a%></option>
<%}%>
</select>
Day: <input type="text" id="dayStr" name="dayStr" value="<%=dayStr %>">
<input type="submit" value="提交"/>

<br/>
<br/>

<table border="1">
	<tr>
		<th>Application</th>
		<th>Service</th>
		<th>Success Times</th>
		<th>Fail times</th>
		<th>Elapsed Average(ms)</th>
		<th>Elapsed Max(ms)</th>
		<th>Operation</th>
	</tr>
<%for(DubboServiceDayIP serviceDay:serviceDayList){%>
	<tr>
		<td><%=appName %></td>
		<td><%=serviceDay.getService() %></td>
		<td><%=serviceDay.getSuccessTimes() %></td>
		<td><%=serviceDay.getFailTimes() %></td>
		<td><%=serviceDay.getElapsedAvg() %></td>
		<td><%=serviceDay.getElapsedMax() %></td>
		<td>
			<a href="<%=request.getContextPath()%>/chart/provider/service/home.do?appName=<%=appName %>&service=<%=serviceDay.getService() %>&dayStr=<%=dayStr %>">Chart</a>
			<a href="<%=request.getContextPath()%>/list/provider/method/method_day_list.do?appName=<%=appName %>&service=<%=serviceDay.getService() %>&dayStr=<%=dayStr %>">Methods</a>
		</td>
	</tr>
<%}%>
</table>

</form>

<%@ include  file="buttom.jsp"%>

<script language="javascript">
$(function() {
    $( "#dayStr" ).datepicker({
    	dateFormat:"yy-mm-dd"
    });
  });
</script>

</body>
</html>


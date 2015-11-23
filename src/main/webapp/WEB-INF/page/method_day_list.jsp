<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.lvmama.soa.monitor.entity.DubboMethodDayIP"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Method Statistics</title>
</head>
<%@ include  file="head.jsp"%>
<%@ include  file="menu.jsp"%>
<%
String appName=(String)request.getAttribute("appName"); 
String service=(String)request.getAttribute("service"); 
java.util.List<String> appNames=(java.util.List)request.getAttribute("appNames");

java.util.List<DubboMethodDayIP> methodDayList=(java.util.List)request.getAttribute("methodDayList");
String dayStr=request.getAttribute("dayStr")==null?"":(String)request.getAttribute("dayStr");
%>

<body>
<form name="form1" action="<%=request.getContextPath()%>/list/provider/method/method_day_list.do">

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
Service: <input type="text" name="service" value="<%=service %>">
Day: <input type="text" id="dayStr" name="dayStr" value="<%=dayStr %>">
<input type="submit" value="提交"/>

<br/>
<br/>

<table border="1">
	<tr>
		<th>Application</th>
		<th>Service</th>
		<th>Method</th>
		<th>Success Times</th>
		<th>Fail times</th>
		<th>Elapsed Average(ms)</th>
		<th>Elapsed Max(ms)</th>
		<th>Operation</th>
	</tr>
<%for(DubboMethodDayIP methodDay:methodDayList){%>
	<tr>
		<td><%=appName %></td>
		<td><%=service %></td>
		<td><%=methodDay.getMethod() %></td>
		<td><%=methodDay.getSuccessTimes() %></td>
		<td><%=methodDay.getFailTimes() %></td>
		<td><%=methodDay.getElapsedAvg() %></td>
		<td><%=methodDay.getElapsedMax() %></td>
		<td>
			<a href="<%=request.getContextPath()%>/chart/provider/method/home.do?appName=<%=appName %>&service=<%=service %>&method=<%=methodDay.getMethod() %>&dayStr=<%=dayStr %>">Chart</a>
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


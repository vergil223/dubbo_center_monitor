<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Application Chart</title>
</head>
<%@ include  file="head.jsp"%>
<%@ include  file="menu.jsp"%>
<%
String appName=(String)request.getAttribute("appName"); 
java.util.List<String> appNames=(java.util.List)request.getAttribute("appNames");
%>

<body>
<form name="form1" action="<%=request.getContextPath()%>/chart/provider/app/home.do">

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
<input type="submit" value="提交"/>

<br/>
<br/>

<iframe src="<%=request.getContextPath()%>/chart/provider/app/success.do?appName=<%=appName %>" name="app_success"	    height="600" width="80%"></iframe>
<iframe src="<%=request.getContextPath()%>/chart/provider/app/fail.do?appName=<%=appName %>" name="app_fail"	        height="600" width="80%"></iframe>
<iframe src="<%=request.getContextPath()%>/chart/provider/app/elapsedAvg.do?appName=<%=appName %>" name="app_elapsed_avg"	height="600" width="80%"></iframe>
<iframe src="<%=request.getContextPath()%>/chart/provider/app/elapsedMax.do?appName=<%=appName %>" name="app_elapsed_max"	height="600" width="80%"></iframe>
</form>

<script language="javascript">
window.setInterval("window.open(document.all.app_success.src,'app_success','')",5000);
window.setInterval("window.open(document.all.app_fail.src,'app_fail','')",5000);
window.setInterval("window.open(document.all.app_elapsed_avg.src,'app_elapsed_avg','')",5000);
window.setInterval("window.open(document.all.app_elapsed_max.src,'app_elapsed_max','')",5000);
	//window.setInterval("alert('interval!')",5000);
	function refreshChart(){
		alert("refreshChart() START");
		
		window.open(document.all.app_success.src,'app_success','');
		window.open(document.all.app_fail.src,'app_fail','');
		window.open(document.all.app_elapsed_avg.src,'app_elapsed_avg','');
		window.open(document.all.app_elapsed_max.src,'app_elapsed_max','');
		
		alert("refreshChart() END");
	}
</script>

</body>
</html>
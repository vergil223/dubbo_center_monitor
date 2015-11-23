<%
String path = request.getContextPath();
String basePath = request.getScheme() + "://"
		+ request.getServerName() + ":" + request.getServerPort()
		+ path;
%>

<script src="<%=basePath%>/js/jquery/jquery.js" type="text/javascript"></script>
<script src="<%=basePath%>/js/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/jquery-ui/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/jquery-ui/jquery-ui.structure.min.css"/>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/js/jquery-ui/jquery-ui.theme.min.css"/>

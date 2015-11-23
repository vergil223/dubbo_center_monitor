<style>
  .ui-menu { width: 150px; }
</style>

<table border="1">
<ul id="menu">
  <li>List
    <ul>
      <li><a href="<%=request.getContextPath()%>/list/provider/service/service_day_list.do">Service</a></li>
      <li><a href="<%=request.getContextPath()%>/list/provider/method/method_day_list.do">Method</a></li>
    </ul>
  </li>
  <li>Chart
    <ul>
      <li><a href="<%=request.getContextPath()%>/chart/provider/app/home.do">Application</a></li>
      <li><a href="<%=request.getContextPath()%>/chart/provider/service/home.do">Service</a></li>
    </ul>
  </li>
</ul>
</table>

<script>
  $(function() {
    $( "#menu" ).menu();
  });
</script>
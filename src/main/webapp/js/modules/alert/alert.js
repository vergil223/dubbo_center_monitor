var alert = angular.module('alert',['ngAnimate','ngRoute','queryFilter','breadCrumb','dialog','dateRangePicker','angularUtils.directives.dirPagination']);

alert.config(function($routeProvider){
	$routeProvider.when("/admin/router/:serviceKey/list",{
        templateUrl:"templates/router/provider-routes.html",
        controller:"providerRoutes"
    }).when("/admin/router/:serviceKey/condition/add",{
        templateUrl:"templates/router/condition-edit.html",
        controller:"conditionEdit"
    }).when("/admin/router/:serviceKey/script/add",{
        templateUrl:"templates/router/script-edit.html",
        controller:"scriptEdit"
    }).when("/admin/router/edit/condition/:serviceKey/:id",{
        templateUrl:"templates/router/condition-edit.html",
        controller:"conditionEdit"
    }).when("/admin/router/edit/script/:serviceKey/:id",{
        templateUrl:"templates/router/script-edit.html",
        controller:"scriptEdit"
    }).when("/admin/route/list",{
        templateUrl:"templates/router/route-abstracts.html",
        controller:"listRoutes"
    }).when("/alert/list",{
        templateUrl:"templates/alert/alert_list.html",
        controller:"listAlert"
    }).when("/alert/add",{
        templateUrl:"templates/alert/alert_edit.html",
        controller:"alertEdit"
    }).when("/alert/edit/:id_",{
        templateUrl:"templates/alert/alert_edit.html",
        controller:"alertEdit"
    }).when("/alert/getAlertMsg",{
        templateUrl:"templates/alert/alertMsg_list.html",
        controller:"listAlertMsg"
    }).otherwise("/alert/list");
});

alert.controller('listAlertMsg', function ($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog) {
    $menu.switchMenu('alert/getAlertMsg');
    $breadcrumb.pushCrumb("预警记录","预警记录","listAlertMsg");
    $scope.details=[];
    $scope.isEmpty=false;
    
    $scope.totalCount=0;
    $scope.pageSize=6;
    $scope.pagination = {
            last: 1,
            current: 1
    };
    
    $scope.timeRange={};
    $scope.timeRange.startTime=new Date().getTime();
    $scope.timeRange.endTime=new Date().getTime();
    
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    };
    
    var numberFormat=function(number){
        if(number<10){
            return "0"+number;
        }
        return number+"";
    }
    
    $scope.sortKey = "INSERT_TIME";   
	$scope.reverse = false; 
    
    $scope.sort = function(keyname){
		$scope.sortKey = keyname;   //set the sortKey to the param passed
		$scope.reverse = !$scope.reverse; //if true make it false and vice versa
		doRefreshData();
	}
    
    var doRefreshData=function(){

    	var params={};
    	if($scope.query.appName){
    		params.appName=$scope.query.appName;
		}
		if($scope.query.service){
			params.service=$scope.query.service;
		}
		if($scope.query.method){
			params.method=$scope.query.method;
		}
		if($scope.pageSize){
			params.pageSize=$scope.pageSize;			
		}
		if($scope.pagination.current){
			params.currentPage=$scope.pagination.current;
		}
		
		var initStartDate = new Date($scope.timeRange.startTime);
        var initEndDate = new Date($scope.timeRange.endTime);
        var timeFrom = initStartDate.getFullYear()+numberFormat(initStartDate.getMonth()+1)+numberFormat(initStartDate.getDate())+numberFormat(initStartDate.getHours())+numberFormat(initStartDate.getMinutes())+"00";
        var timeTo = initEndDate.getFullYear()+numberFormat(initEndDate.getMonth()+1)+numberFormat(initEndDate.getDate())+numberFormat(initEndDate.getHours())+numberFormat(initEndDate.getMinutes())+"00";
        
//      $scope.sortKey = keyname;   //set the sortKey to the param passed
//		$scope.reverse = !$scope.reverse;
        var sortBy=$scope.sortKey;
        var sortType="asc";
        if($scope.reverse){
        	sortType="desc";
        }
        
		$httpWrapper.post({
			url:"alert/queryAlertRecord/"+timeFrom+"/"+timeTo+"/"+sortBy+"/"+sortType,
			data:params,
			success:function(data){
				$scope.details=data.resultList;
				if(!data.resultList||data.resultList.length<=0){
					$scope.isEmpty=true;
				}
				$scope.originData=data.resultList;
				$scope.totalCount=data.totalCount;
			}
		}); 
    }
    
    $scope.onPageChange=function(newPageNumber){
    	doRefreshData();
    }
    
    $scope.refreshData=function(){
    	doRefreshData();
    };
    
});

alert.controller('listAlert', function ($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog) {
    $menu.switchMenu('alert/alert');
    $breadcrumb.pushCrumb("预警规则概要列表","预警规则概要列表","listAlert");
    $scope.details=[];
    $scope.isEmpty=false;
    
    var refreshData=function(){
    		$httpWrapper.post({
    			url:"alertConf/listAlert",
    			success:function(data){
    				$scope.details=data;
    				if(!data||data.length<=0){
    					$scope.isEmpty=true;
    				}
    				$scope.originData=data;
    			}
    		});    		
    };
    refreshData();
    
    $httpWrapper.post({
        url:"alertConf/enableList",
        success:function(data){
            $scope.enableList=data;
        }
    });
    
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    };
    
    var operateType={
            'delete':"删除",
            'enable':'启用',
            'disable':'禁用'
    }
    
    $scope.operate=function(type,item){
        $dialog.confirm({
            content:"确认要"+operateType[type]+"吗?",
            size:"small",
            callback: function () {
                $httpWrapper.post({
                    url:"alertConf/"+type+"/"+item.id_,
                    success: function (data) {
                            $dialog.info({content:data, size:"small"});
                            refreshData();
                    }
                });
            }
        });

    }
    
    $scope.batchOperation=function(type){
        var selected=[];
        for(var i=0;i<$scope.details.length;i++){
            if($scope.details[i].checked){
                selected.push($scope.details[i].id_);
            }
        }
        if(selected.length<=0){
            $dialog.info({
                content:"请选择操作项！",
                size:"small"
            });
            return ;
        }
        var operationName = "";
        if('Delete'==type){
            operationName="删除";
        }else if('Disable'==type){
            operationName='禁用';
        }else if('Enable'==type){
            operationName='启用';
        }
        $dialog.confirm({
            content:"确认批量"+operationName+"路由规则吗?",
            size:"small",
            callback:function(){
                $httpWrapper.post({
                    url:"alertConf/batch"+type,
                    data:"ids="+selected.join(","),
                    config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
                    success: function (data) {
                            $dialog.info({
                                content:data,
                                size:"small",
                                callback:function(){
                                    refreshData();
                                }
                            });
                    }
                });
            }
        });
    }
});

alert.controller('alertEdit',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog){
    $menu.switchMenu('alert/alert');
    $breadcrumb.pushCrumb("新增预警规则","新增预警规则","alertEdit");
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.id_=$routeParams.id_;
    $scope.item={};
    $scope.item.id=$scope.id_;
    $httpWrapper.post({
        url:"alertConf/enableList",
        success:function(data){
            $scope.enableList=data;
        }
    });
    
    if($scope.id_){
        $httpWrapper.post({
            url:"alertConf/loadById/"+$scope.id_,
            success: function (data) {
                $scope.item=data;
            }
        });
    }
    
    var parseRule=function(rules){
        rules = rules.trim();
        rules = rules.split("&");
        var ruleArray = [];
        for(var i=0;i<rules.length;i++){
            var rule = rules[i];
            var ruleObj={};
            var index=0;
            if((index=rule.indexOf("!="))>0){
                ruleObj.condition=rule.substring(0,index).trim();
                ruleObj.rule="!=";
                ruleObj.value=rule.substring(index+2);
            }else{
                index=rule.indexOf("=");
                ruleObj.condition=rule.substring(0,index).trim();
                ruleObj.rule="=";
                ruleObj.value=rule.substring(index+1);
            }
            ruleArray.push(ruleObj);
        }
        return ruleArray;
    }
    $scope.whenConditions=[{
        val:'consumer.host',
        text:"消费者IP地址"
    },{
        val:'consumer.application',
        text:"消费者应用名"
    },{
        val:'consumer.version',
        text:"消费者版本"
    },{
        val:'consumer.cluster',
        text:'消费者集群'
    }];
    $scope.rules=[{
        val:'=',
        text:"匹配"
    },{
        val:'!=',
        text:"不匹配"
    }];
    var ipPattern = "^(([1-9]{1}[0-9]{0,2}[\\.]{1}){3}(([1-9]{1}[0-9]{0,2})|[\\*]{1}){1}){1}([,]{1}(([1-9]{1}[0-9]{0,2}[\\.]{1}){3}(([1-9]{1}[0-9]{0,2})|[\\*]{1}){1}){1}){0,}$";
    var textPattern = "^[a-zA-Z0-9-_]{1,}$";
    var numPattern = "^[1-9]{1}[0-9]{0,}$";
    var whenConditions={
        "consumer.host":{
            title:'输的地址可以是ip段(192.168.0.*)也可以可是具体ip地址，多个通过逗号分隔开',
            pattern:ipPattern
        },
        "consumer.application":{
            title:"请输入消费端app名称",
            pattern:textPattern
        },
        "consumer.version":{
            title:"请输入消费端版本",
            pattern:textPattern
        },
        "consumer.cluster":{
            title:"请输入消费端的集群名",
            pattern:textPattern
        }
    };
    $scope.thenConditions=[{
        val:'provider.host',
        text:"提供者IP地址"
    },{
        val:'provider.cluster',
        text:"提供者集群"
    },{
        val:'provider.protocol',
        text:"提供者协议"
    },{
        val:'provider.version',
        text:"提供者版本号"
    },{
        val:'provider.port',
        text:"提供者端口"
    }];
    var thenConditions={
        "provider.host":{
            title:'输的地址可以是ip段(192.168.0.*)也可以可是具体ip地址，多个通过逗号分隔开',
            pattern:ipPattern
        },
        "provider.application":{
            title:"请输入提供者的app名称",
            pattern:textPattern
        },
        "provider.protocol":{
            title:"请输入提供者的协议",
            pattern:textPattern
        },
        "provider.port":{
            title:"提供者端口必须是整型",
            pattern:numPattern
        },
        "provider.version":{
            title:"请输入提供者的版本",
            pattern:textPattern
        }
    };
    $scope.save=function(){
        $httpWrapper.post({
            url:"alertConf/save",
            data:$scope.item,
            success: function (data) {
                	$scope.id_=data.id_;
                	$scope.item.id_=data.id_;
                    $dialog.info({
                        content:"预警规则保存成功,ID="+$scope.item.id_,
                        size:"small"
                    });
            }
        });
    }
    
    $scope.selectMethod=function(){
        if(!$scope.item.method){
            if($scope.selectedMethod&&$scope.selectedMethod!=""){
                $scope.item.method=$scope.selectedMethod;
            }
        }else{
            if($scope.selectedMethod&&$scope.selectedMethod!=""){
                $scope.item.method=$scope.item.method+","+$scope.selectedMethod;
            }
        }


    }
    $scope.switchTab=function(tabName){
        $scope.currentTab=tabName;
    }

    $scope.switchTab('when');
    $scope.addWhen= function () {
        $scope.whenList.push({condition:'consumer.host',rule:'=',value:''});
    };
    $scope.addThen= function () {
        $scope.thenList.push({condition:'provider.host',rule:'=',value:''});
    };

    $scope.removeWhen=function(index){
        var newArray=[];
        newArray=newArray.concat($scope.whenList.slice(0,index));
        newArray=newArray.concat($scope.whenList.slice(index+1));
        $scope.whenList=newArray;
    }
    $scope.removeThen=function(index){
        var newArray=[];
        newArray=newArray.concat($scope.thenList.slice(0,index));
        newArray=newArray.concat($scope.thenList.slice(index+1));
        $scope.thenList=newArray;
    }
});

alert.controller('listRoutes', function ($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog) {
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb("路由规则概要列表","路由规则概要列表","listRoutes");
    $scope.details=[];
    $scope.isEmpty=false;
    $httpWrapper.post({
        url:"route/list.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
    });
    $httpWrapper.post({
        url:"app/list.htm",
        success:function(data){
            $scope.applications=data;
        }
    });
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});

alert.controller('scriptEdit', function ($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog) {
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb('对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则",'对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则","routeScriptEdit");
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.serviceKey=encodeURI($routeParams.serviceKey);
    $scope.id=$routeParams.id;
    $scope.item={};
    $scope.item.id=$scope.id;
    var editor = CodeMirror.fromTextArea(document.getElementById("script-editor"), {
        lineNumbers: true,
        styleActiveLine: true,
        matchBrackets: true
    });
    if($scope.id){
        $httpWrapper.post({
            url:"route/get_"+$scope.id+".htm",
            success: function (data) {
                $scope.item=data;
                editor.setValue(data.rule);
            }
        });
    }

     CodeMirror.fromTextArea(document.getElementById("script-demo"), {
        lineNumbers: true,
        styleActiveLine: true,
        matchBrackets: true, readOnly:true
    });
    $scope.save= function () {
        var script = editor.getDoc().getValue();
        if(script&&script!=""){
            console.log(script);
            try{
                esprima.parse(script, { tolerant: true, loc: true });
            }catch (e){
                var errorContent = e.message;
                var position = {};
                editor.setCursor(e.lineNumber-1);
                $dialog.alert({
                    content:"脚本存在语法错误，"+errorContent,
                    size:"small"
                });
            }
            $scope.item.service=$scope.service;
            $scope.item.type="script";
            $scope.item.scriptType="javascript";
            $scope.item.rule=script;
            $httpWrapper.post({
                url:$scope.id?"route/update.htm":"route/create.htm",
                data:$scope.item,
                success: function (data) {
                    if(data.result==ajaxResultStatu.SUCCESS){
                        $dialog.info({
                            content:"成功编辑路由规则！",
                            size:"small"
                        });
                    }
                }
            });
        }else{
            $dialog.alert({
               content:"请输入路由脚本！",
                size:"small"
            });
        }

    }
});

alert.controller('conditionEdit',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog){
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb('对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则",'对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则","routeConditionEdit");
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.serviceKey=encodeURI($routeParams.serviceKey);
    $scope.id=$routeParams.id;
    $scope.item={};
    $scope.item.id=$scope.id;
    $scope.whenList=[];
    $scope.thenList=[];
    $httpWrapper.post({
        url:"override/provider/"+encodeURIComponent($routeParams.serviceKey)+"/methods.htm",
        success:function(data){
            $scope.methods=data;
        }
    });
    if($scope.id){
        $httpWrapper.post({
            url:"route/get_"+$scope.id+".htm",
            success: function (data) {
                $scope.item=data;
                if(data.matchRule){
                    $scope.whenList = parseRule(data.matchRule);
                    for(var i=0;i<$scope.whenList.length;i++){
                        var when = $scope.whenList[i];
                        if(when.condition=="consumer.methods"){
                            $scope.item.method=when.value;
                            $scope.removeWhen(i);
                            break;
                        }
                    }
                }
                if(data.filterRule){
                    $scope.thenList = parseRule(data.filterRule);
                }
            }
        });
    }
    var parseRule=function(rules){
        rules = rules.trim();
        rules = rules.split("&");
        var ruleArray = [];
        for(var i=0;i<rules.length;i++){
            var rule = rules[i];
            var ruleObj={};
            var index=0;
            if((index=rule.indexOf("!="))>0){
                ruleObj.condition=rule.substring(0,index).trim();
                ruleObj.rule="!=";
                ruleObj.value=rule.substring(index+2);
            }else{
                index=rule.indexOf("=");
                ruleObj.condition=rule.substring(0,index).trim();
                ruleObj.rule="=";
                ruleObj.value=rule.substring(index+1);
            }
            ruleArray.push(ruleObj);
        }
        return ruleArray;
    }
    $scope.whenConditions=[{
        val:'consumer.host',
        text:"消费者IP地址"
    },{
        val:'consumer.application',
        text:"消费者应用名"
    },{
        val:'consumer.version',
        text:"消费者版本"
    },{
        val:'consumer.cluster',
        text:'消费者集群'
    }];
    $scope.rules=[{
        val:'=',
        text:"匹配"
    },{
        val:'!=',
        text:"不匹配"
    }];
    var ipPattern = "^(([1-9]{1}[0-9]{0,2}[\\.]{1}){3}(([1-9]{1}[0-9]{0,2})|[\\*]{1}){1}){1}([,]{1}(([1-9]{1}[0-9]{0,2}[\\.]{1}){3}(([1-9]{1}[0-9]{0,2})|[\\*]{1}){1}){1}){0,}$";
    var textPattern = "^[a-zA-Z0-9-_]{1,}$";
    var numPattern = "^[1-9]{1}[0-9]{0,}$";
    var whenConditions={
        "consumer.host":{
            title:'输的地址可以是ip段(192.168.0.*)也可以可是具体ip地址，多个通过逗号分隔开',
            pattern:ipPattern
        },
        "consumer.application":{
            title:"请输入消费端app名称",
            pattern:textPattern
        },
        "consumer.version":{
            title:"请输入消费端版本",
            pattern:textPattern
        },
        "consumer.cluster":{
            title:"请输入消费端的集群名",
            pattern:textPattern
        }
    };
    $scope.thenConditions=[{
        val:'provider.host',
        text:"提供者IP地址"
    },{
        val:'provider.cluster',
        text:"提供者集群"
    },{
        val:'provider.protocol',
        text:"提供者协议"
    },{
        val:'provider.version',
        text:"提供者版本号"
    },{
        val:'provider.port',
        text:"提供者端口"
    }];
    var thenConditions={
        "provider.host":{
            title:'输的地址可以是ip段(192.168.0.*)也可以可是具体ip地址，多个通过逗号分隔开',
            pattern:ipPattern
        },
        "provider.application":{
            title:"请输入提供者的app名称",
            pattern:textPattern
        },
        "provider.protocol":{
            title:"请输入提供者的协议",
            pattern:textPattern
        },
        "provider.port":{
            title:"提供者端口必须是整型",
            pattern:numPattern
        },
        "provider.version":{
            title:"请输入提供者的版本",
            pattern:textPattern
        }
    };
    $scope.save=function(){
        var matchRule ="";
        if($scope.whenList.length>0){
            for(var i=0;i<$scope.whenList.length;i++){
                var when = $scope.whenList[i];
                var condition = whenConditions[when.condition];
                var reg = new RegExp(condition.pattern);
                if(!reg.test(when.value)){
                    $dialog.alert({content:condition.title, size:"small"});
                    return ;
                }
                matchRule+=when.condition+when.rule+when.value+"&"
            }
        }
        if($scope.item.method){
            matchRule+="consumer.methods="+$scope.item.method;
            delete $scope.item.method;
        }else if(matchRule.length>0){
            matchRule=matchRule.substring(0,matchRule.length-1);
        }
        
        
        var filterRule="";
        if($scope.thenList.length>0){
            for(var i=0;i<$scope.thenList.length;i++){
                var then = $scope.thenList[i];
                var condition = thenConditions[then.condition];
                var reg = new RegExp(condition.pattern);
                if(!reg.test(then.value)){
                    $dialog.alert({content:condition.title, size:"small"});
                    return ;
                }
                filterRule+=then.condition+then.rule+then.value+"&";
            }
        }
        if(filterRule.length>0){
            filterRule=filterRule.substring(0,filterRule.length-1);
        }
        $scope.item.matchRule=matchRule;
        $scope.item.filterRule=filterRule;
        $scope.item.service=$scope.service;
        $scope.item.type="condition";
        $httpWrapper.post({
            url:$scope.id?"route/update.htm":"route/create.htm",
            data:$scope.item,
            success: function (data) {
                if(data.result==ajaxResultStatu.SUCCESS){
                    $dialog.info({
                        content:"成功编辑路由规则！",
                        size:"small"
                    });
                }
            }
        });
    }
    $scope.selectMethod=function(){
        if(!$scope.item.method){
            if($scope.selectedMethod&&$scope.selectedMethod!=""){
                $scope.item.method=$scope.selectedMethod;
            }
        }else{
            if($scope.selectedMethod&&$scope.selectedMethod!=""){
                $scope.item.method=$scope.item.method+","+$scope.selectedMethod;
            }
        }


    }
    $scope.switchTab=function(tabName){
        $scope.currentTab=tabName;
    }

    $scope.switchTab('when');
    $scope.addWhen= function () {
        $scope.whenList.push({condition:'consumer.host',rule:'=',value:''});
    };
    $scope.addThen= function () {
        $scope.thenList.push({condition:'provider.host',rule:'=',value:''});
    };

    $scope.removeWhen=function(index){
        var newArray=[];
        newArray=newArray.concat($scope.whenList.slice(0,index));
        newArray=newArray.concat($scope.whenList.slice(index+1));
        $scope.whenList=newArray;
    }
    $scope.removeThen=function(index){
        var newArray=[];
        newArray=newArray.concat($scope.thenList.slice(0,index));
        newArray=newArray.concat($scope.thenList.slice(index+1));
        $scope.thenList=newArray;
    }
});

alert.controller('providerRoutes', function ($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog) {
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb('服务'+decodeURIComponent($routeParams.serviceKey)+"路由配置列表",'服务'+decodeURIComponent($routeParams.serviceKey)+"路由配置列表","providerRoutes");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.serviceKey=encodeURI($routeParams.serviceKey);
    $scope.enabledOptios=[{
        val:true,
        text:"已启用"
    },{
        val:false,
        text:"已禁用"
    }];
    $scope.routerTypes=[{
        val:'condition',
        text:"条件规则"
    },{
        val:'script',
        text:"脚本规则"
    }];
    var refreshData=function(){
        $httpWrapper.post({
            url:"route/provider/"+encodeURIComponent($routeParams.serviceKey)+"/list.htm",
            success:function(data){
                $scope.details=data;
                if(!data||data.length<=0){
                    $scope.isEmpty=true;
                }
                $scope.originData=data;
            }
        });
    }
    refreshData();
    $scope.select=function(){
        if($scope.selectAll){
            for(var i=0;i<$scope.details.length;i++){
                $scope.details[i].checked=true;
            }
        }else{
            for(var i=0;i<$scope.details.length;i++){
                $scope.details[i].checked=false;
            }
        }
    }
    $scope.batchOperation=function(type){
        var selected=[];
        for(var i=0;i<$scope.details.length;i++){
            if($scope.details[i].checked){
                selected.push($scope.details[i].id);
            }
        }
        if(selected.length<=0){
            $dialog.info({
                content:"请选择操作项！",
                size:"small"
            });
            return ;
        }
        var operationName = "";
        if('delete'==type){
            operationName="删除";
        }else if('disable'==type){
            operationName='禁用';
        }else if('enable'==type){
            operationName='启用';
        }
        $dialog.confirm({
            content:"确认批量"+operationName+"路由规则吗?",
            size:"small",
            callback:function(){
                $httpWrapper.post({
                    url:"route/batch-"+type+".htm",
                    data:"ids="+selected.join(","),
                    config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
                    success: function (data) {
                        if(data.result==ajaxResultStatu.SUCCESS){
                            $dialog.info({
                                content:"批量"+operationName+"配置成功！",
                                size:"small",
                                callback:function(){
                                    refreshData();
                                }
                            });
                        }else{
                            $dialog.alert({
                                content:"批量"+operationName+"配置失败！",
                                size:"small",
                                callback:function(){
                                    refreshData();
                                }
                            });
                        }
                    }
                });
            }
        });

    }
    var operateType={
        'delete':"删除",
        'enable':'启用',
        'disable':'禁用'
    }
    $scope.operate=function(type,item){
        $dialog.confirm({
            content:"确认执行"+operateType[type]+"操作?",
            size:"small",
            callback: function () {
                $httpWrapper.post({
                    url:"route/"+type+"_"+item.id+".htm",
                    success: function (data) {
                        if(data.result==ajaxResultStatu.SUCCESS){
                            refreshData();
                            $dialog.info({content:operateType[type]+"成功！", size:"small"});
                        }
                    }
                });
            }
        });

    }
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});
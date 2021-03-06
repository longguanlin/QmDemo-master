<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>教师端|学生信息管理系统</title>
    <link rel="shortcut icon" href="favicon.ico"/>
	<link rel="bookmark" href="favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="easyui/css/default.css" />
    <link rel="stylesheet" type="text/css" href="easyui/themes/bootstrap/easyui.css" />
    <link rel="stylesheet" type="text/css" href="easyui/themes/icon.css" />
    <script type="text/javascript" src="easyui/jquery.min.js"></script>
    <script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src='easyui/js/outlook2.js'> </script>
    <script type="text/javascript">
	 var _menus = {"menus":[
						{"menuid":"1","icon":"","menuname":"教学管理",
							"menus":[
									{"menuid":"11","menuname":"成绩登记","icon":"icon-pencil-add","url":"TeacherAction-toExamTeacherView"},
								]
						},
						{"menuid":"2","icon":"","menuname":"教师信息",
							"menus":[
									{"menuid":"21","menuname":"教师通讯录","icon":"icon-note","url":"TeacherAction-toTeacherNoteListView"},
								]
						},
						{"menuid":"3","icon":"","menuname":"系统管理",
							"menus":[
							        {"menuid":"31","menuname":"个人信息","icon":"icon-password","url":"TeacherAction-toPersonal"},
								]
						}
				]};


    </script>

</head>
<body class="easyui-layout" style="overflow-y: hidden"  scroll="no">
	
    <div region="north" split="true" border="false" style="overflow: hidden; height: 30px;
        background: url(images/layout-browser-hd-bg.gif) rgb(138,144,153) repeat-x center 50%;
        line-height: 20px;color: #fff; font-family: Verdana, 微软雅黑,黑体">
        <span style="float:right; padding-right:20px;" class="head"><span style="color:red; font-weight:bold;">${user.name}&nbsp;</span>您好&nbsp;&nbsp;&nbsp;<a href="SystemAction-loginOut" id="loginOut">安全退出</a></span>
        <span style="padding-left:10px; font-size: 16px; ">成绩管理系统</span>
    </div>
    <div region="south" split="true" style="height: 30px; background: rgb(138,144,153); ">
        <div class="footer">Copyright &copy; 2019</div>
    </div>
    <div region="west" hide="true" split="true" title="导航菜单" style="width:180px;" id="west">
	<div id="nav" class="easyui-accordion" fit="true" border="false">
		<!--  导航内容 -->
	</div>
	
    </div>
    <div id="mainPanle" region="center" style="background: #eee; overflow-y:hidden">
        <div id="tabs" class="easyui-tabs"  fit="true" border="false" >
			<jsp:include page="/WEB-INF/view/teacher/welcome.jsp" />
		</div>
    </div>

	<iframe width=0 height=0 src="refresh.jsp"></iframe>
	
</body>
</html>
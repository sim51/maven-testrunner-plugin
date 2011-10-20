<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><s:text name="user.sauthentifier"/></title>
	</head>
	<body>
		<!-- DIV PAGE -->
		<div id="page">
			
			<h1><s:text name="user.sauthentifier"/></h1>
			
			<s:form action="login" method="POST">
				<s:textfield key="user.login" name="username" /> 
				<s:textfield key="user.password" name="password" /> 
				<s:submit key="btn.connecter"/>
			</s:form>
			
		</div>
		<!-- FIN DIV PAGE -->
		
	</body>
</html>


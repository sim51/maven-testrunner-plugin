<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//Dtd XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/Dtd/xhtml1-strict.dtd"> 
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><s:text name="contact.titre.ajout" /></title>
	</head>
	<body>
		<!-- DIV PAGE -->
		<div id="page">
			<h1><s:text name="contact.titre.ajout" /></h1>
			
			<!-- DIV TOOL -->
			<div id="tools">
			
				<div id="listContact">
					<a href="listContact.action"><s:text name="contact.retour_list"/></A>
				</div>
			
			</div>
			<!-- FIN DIV TOOL -->
			
			<!-- DIV CONTACT -->
			<div id="contact">
				
				<s:form action="saveCreateContact" method="POST">
					
					<s:textfield key="contact.prenom" name="prenom" />
					
					<s:textfield key="contact.nom" name="nom" />
					
					<s:textfield key="contact.telephone" name="telephone" />
					
					<s:reset key="btn.reset"/>
					<s:submit key="btn.ajouter" />
					
				</s:form>
				
			</div>
			<!-- FIN DIV CONTACT -->
		
		</div>
		<!-- FIN DIV PAGE -->
		
	</body>
</html>


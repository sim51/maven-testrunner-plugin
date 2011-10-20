<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//Dtd XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/Dtd/xhtml1-strict.dtd"> 
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><s:text name="contact.titre.list"/></title>
	</head>
	<body>
		<!-- DIV PAGE -->
		<div id="page">
			<h1><s:text name="contact.titre.list"/></h1>
			
			<!-- DIV TOOL -->
			<div id="tools">
			
				<div id="addContact">
					<s:url id="url" action="createContact" />
					<s:a href="%{url}"><s:text name="common.ajouter"/></s:a>
				</div>
			
				<div id="searchForm">
					<s:form action="searchContact" method="GET">
						<s:textfield name="searchString"/>
						<s:submit key="btn.rechercher" />
					</s:form>
				</div>
				
			</div>
			<!-- FIN DIV TOOL -->
			
			<!-- DIV CONTACT -->
			<div id="contact">
				
				<table>
					<tbody>
						<tr>
							<th><s:text name="contact.prenom" /></th>
							<th><s:text name="contact.nom" /></th>
							<th><s:text name="contact.telephone" /></th>
							<th><s:text name="common.action" /></th>
						</tr>
			
						<s:iterator value="contacts">
							<tr>
								<td><s:property value="prenom" /></td>
								<td><s:property value="nom" /></td>
								<td><s:property value="telephone" /></td>
								<td>
									<s:url id="url" action="updateContact">
										<s:param name="id"><s:property value="id" /></s:param>
									</s:url>
									<s:a href="%{url}"><s:text name="common.editer" /></s:a>
									<s:url id="url" action="deleteContact">
										<s:param name="id"><s:property value="id" /></s:param>
									</s:url>
									<s:a href="%{url}"><s:text name="common.delete" /></s:a>
								</td>
							</tr>
						</s:iterator>
						
					</tbody>
				</table>
				
			</div>
			<!-- FIN DIV CONTACT -->
		
		</div>
		<!-- FIN DIV PAGE -->
		
	</body>
</html>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<c:set var="titol"><spring:message code="notificacio.form.titol.crear"/></c:set>

<html>
<head>
	<title>${titol}</title>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/newMassiuSave"/></c:set>
    <form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="NotificacioMassiuCommand" enctype="multipart/form-data">
		<not:inputFile name="ficheroCsv" textKey="notificacioMassiu.form.camp.arxiuCsv" labelSize="3" inputSize="6"/>
		<not:inputFile name="ficheroZip" textKey="notificacioMassiu.form.camp.arxiuZip" labelSize="3" inputSize="6"/>
	
	</form:form>
</body>
</html>
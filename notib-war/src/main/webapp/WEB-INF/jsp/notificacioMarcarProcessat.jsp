<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="notificacio.pendent.camp.marcar.processat.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
	<not:modalHead/>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="marcarProcessatCommand">
		<not:inputTextarea required="true" name="motiu" textKey="notificacio.pendent.camp.marcar.processat.motiu"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="notificacio.pendent.camp.marcar.processat.boto"/></button>
			<a href="<c:url value="/bustiaUser"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

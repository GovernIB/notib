<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
	<title><spring:message code="enviament.estat.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
</head>
<body>
	<h4><spring:message code="enviament.info.seccio.estat"/></h4>
	<table class="table table-bordered table-striped" style="width:100%">
	<tbody>
		<tr>
			<td width="30%"><strong><spring:message code="enviament.estat.estat.data"/></strong></td>
			<td><fmt:formatDate value="${notificacioEstat.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
		</tr>
		<tr>
			<td><strong><spring:message code="enviament.estat.estat.codi"/></strong></td>
			<td>${notificacioEstat.estatCodi}</td>
		</tr>
		<tr>
			<td><strong><spring:message code="enviament.estat.estat.descripcio"/></strong></td>
			<td>${notificacioEstat.estatDescripcio}</td>
		</tr>
		<c:if test="${not empty notificacioEstat.numSeguiment}">
			<tr>
				<td><strong><spring:message code="enviament.estat.estat.seguiment"/></strong></td>
				<td>${notificacioEstat.numSeguiment}</td>
			</tr>
		</c:if>
	</tbody>
	</table>
	<c:if test="${notificacioEstat.certificacioDisponible}">
		<h4><spring:message code="enviament.info.seccio.certificacio"/></h4>
	</c:if>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>

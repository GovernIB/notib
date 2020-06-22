detallErrorCallback<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="excepcio.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<not:modalHead/>
</head>
<body>
	<c:if test="${not empty event}">
		<dl class="dl-horizontal">
			<dt><spring:message code="event.detall.camp.data"/></dt>
			<dd><fmt:formatDate value="${event.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<dt><spring:message code="event.detall.camp.tipus"/></dt>
			<dd>${event.tipus}</dd>
			<dt><spring:message code="event.detall.camp.descripcio"/></dt>
			<dd>${event.descripcio}</dd>
		</dl>
		<div class="panel-body" >
			<pre style="height:300px">${event.errorDescripcio}</pre>
		</div>
	</c:if>
<%-- 
	<div id="modal-botons">
		<a href="<c:url value="/excepcio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
--%>	
</body>
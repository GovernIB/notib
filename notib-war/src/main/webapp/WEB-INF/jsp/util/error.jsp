<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title>NOTIB - Error ${errorObject.statusCode}</title>
	<rip:modalHead/>
</head>
<body>
	<c:if test="${not empty errorObject}">
		<c:choose>
			<c:when test="${errorObject.throwableClassName == 'java.lang.SecurityException'}">
				<div class="alert alert-danger" style="margin-top: 20px;" role="alert">
					<strong><spring:message code="error.acces.notib"/></strong> ${errorObject.exceptionMessage}</div>
			</c:when>
			<c:otherwise>
				<dl class="dl-horizontal" style="margin-top: 20px;">
					<dt>Recurs</dt>
					<dd>${errorObject.requestUri}</dd>
					<dt>Missatge</dt>
					<dd>${errorObject.exceptionMessage}</dd>
				</dl>
				<div class="panel panel-default" id="traca-panel">
					<div class="panel-heading">
						<h4 class="panel-title">
							<a class="accordion-toggle" data-toggle="collapse" data-parent="#traca-panel" href="#traca-stack">Traça de l'error</a>
						</h4>
					</div>
					<div id="traca-stack" class="panel-collapse collapse in">
						<div class="panel-body" >
							<pre>${errorObject.fullStackTrace}</pre>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</c:if>

</body>
</html>

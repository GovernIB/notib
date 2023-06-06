<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title>NOTIB - Error ${errorObject.statusCode}</title>
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
<c:if test="${not empty errorObject}">
	<c:choose>
		<c:when test="${errorObject.throwableClassName == 'java.lang.SecurityException'}">
			<div class="alert alert-danger" style="margin-top: 20px;" role="alert">
				<strong><spring:message code="error.acces.notib"/></strong> ${errorObject.exceptionMessage}
			</div>
			<div class="col-md-2" style="padding-top: 20px;">
				<a href="<c:url value="/index"><c:param name="canviRol" value="tothom"/></c:url>" class="btn btn-primary"><spring:message code="comu.boto.inici"/></a>
				<a href="<c:url value="/usuari/logout"/>" class="btn btn-default" style="margin-left: 10px;"><spring:message code="decorator.menu.accions.desconectar"/></a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="row">
				<div class="col-md-10">
					<dl class="dl-horizontal" style="margin-top: 20px;">
						<dt>Recurs</dt>
						<dd>${errorObject.requestUri}</dd>
						<dt>Missatge</dt>
						<dd>${errorObject.exceptionMessage}</dd>
					</dl>
				</div>
				<div class="col-md-2" style="padding-top: 20px;">
					<a href="<c:url value="/index"><c:param name="canviRol" value="tothom"/></c:url>" class="btn btn-primary"><spring:message code="comu.boto.inici"/></a>
					<a id="mu_logout" href="<c:url value="/usuari/logout"/>" class="btn btn-default" style="margin-left: 10px;"><spring:message code="decorator.menu.accions.desconectar"/></a>
				</div>
			</div>
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

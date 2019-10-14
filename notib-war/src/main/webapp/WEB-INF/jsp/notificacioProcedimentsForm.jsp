<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="notificacio.form.titol.procediments"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
</head>
<body>
	<table class="table table-hover">
		<thead>
			<tr>
				<th scope="col"><spring:message code="notificacio.procediment.codi"/><spr</th>
				<th scope="col"><spring:message code="notificacio.procediment.nom"/></th>
				<th scope="col"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${procediments}" var="procediment">
					<tr>
						<td scope="row" name="codi">${procediment.codi}</td>
						<td scope="row" name="nom">${procediment.nom}</td>
						<td scope="row" name="id">
						<button onclick="window.top.location='/notib/notificacio/new/${procediment.id}';return false;" class="btn btn-default"><spring:message code="notificacio.form.titol.procediments.iniciar"/></button>
						</td>
					</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
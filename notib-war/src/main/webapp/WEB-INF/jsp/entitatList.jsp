<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualRepresentant",
			es.caib.notib.war.helper.RolHelper.isUsuariActualRepresentant(request));
%>
<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
	
	<link href="<c:url value="/css/entitat.css"/>" rel="stylesheet" type="text/css">
	
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
		
	
	<c:if test="${isRolActualAdministrador}">
	<div class="text-right btn-novaentitat" data-toggle="botons-titol">
		<a class="btn btn-default" href="<c:url value="/entitats/new"/>" data-toggle="modal" data-datatable-id="entitats"><span class="fa fa-plus"></span>&nbsp;<spring:message code="entitat.list.boto.nova.entitat"/></a>
	</div>
	</c:if>
		
	<script id="botonsTemplate" type="text/x-jsrender"></script>
	<table
		id="entitats"
		data-toggle="datatable"
		data-url="<c:url value="/entitats/datatable"/>"
		data-search-enabled="true"
		data-default-order="2"
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		class="table table-striped table-bordered"
		data-info-type="search"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="codi"><spring:message code="entitat.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="entitat.list.columna.nom"/></th>
				<th data-col-name="cif"><spring:message code="entitat.list.columna.cif"/></th>
				<th data-col-name="activa" data-template="#cellActivaTemplate">
					<spring:message code="entitat.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="permisosCount" data-orderable="false" data-template="#cellPermisosTemplate" width="10%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="<c:url value="/entitats/{{:id}}/permis"/>" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="entitat.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<c:if test="${isRolActualAdministrador}">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/entitats/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !activa}}
								<li><a href="<c:url value="/entitats/{{:id}}/enable"/>" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="<c:url value="/entitats/{{:id}}/disable"/>" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="<c:url value="/entitats/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
						</c:if>
						<c:if test="${isRolActualRepresentant}">
						<div class="dropdown">
							<a class="btn btn-primary" href="<c:url value="/entitats/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a>
						</c:if>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
</body>
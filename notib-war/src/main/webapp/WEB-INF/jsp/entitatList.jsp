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
			"isRolActualAdministradorEntitat",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
	pageContext.setAttribute(
			"isRolActualUsuari",
			es.caib.notib.war.helper.RolHelper.isUsuariActualUsuari(request));
%>
<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<link href="<c:url value="/css/entitat.css"/>" rel="stylesheet" type="text/css">
</head>
<body>
	<c:if test="${isRolActualAdministrador}">
		<div class="text-right btn-novaentitat" data-toggle="botons-titol">
			<a class="btn btn-default" href="<c:url value="/entitat/new"/>" data-toggle="modal" data-datatable-id="entitats"><span class="fa fa-plus"></span>&nbsp;<spring:message code="entitat.list.boto.nova.entitat"/></a>
		</div>
	</c:if>
	<script id="botonsTemplate" type="text/x-jsrender">
<%--
		<c:if test="${isRolActualAdministrador}">
			<p style="text-align:right"><a id="grup-entitat-nou" class="btn btn-default" href="entitat/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="entitat.list.boto.nova.entitat"/></a></p>
		</c:if>
--%>
	</script>

	<table
		id="entitats"
		data-toggle="datatable"
		data-url="<c:url value="/entitat/datatable"/>"
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
				<th data-col-name="dir3Codi"><spring:message code="entitat.list.columna.dir3Codi"/></th>
				<th data-col-name="activa" data-template="#cellActivaTemplate">
					<spring:message code="entitat.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="numAplicacions" data-orderable="false" data-template="#cellAplicacionsTemplate" width="10%">
					<script id="cellAplicacionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/entitat/{{:id}}/aplicacio"/>" class="btn btn-default"><span class="fa fa-puzzle-piece"></span>&nbsp;<spring:message code="entitat.list.boto.aplicacions"/>&nbsp;<span class="badge">{{:numAplicacions}}</span></a>
					</script>
				</th>
				<th data-col-name="permisosCount" data-orderable="false" data-template="#cellPermisosTemplate" width="10%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="<c:url value="/entitat/{{:id}}/permis"/>" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="entitat.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<c:if test="${isRolActualAdministrador}">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/entitat/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									{{if !activa}}
									<li><a href="<c:url value="/entitat/{{:id}}/enable"/>" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
									{{else}}
									<li><a href="<c:url value="/entitat/{{:id}}/disable"/>" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
									{{/if}}
									<li><a href="<c:url value="/entitat/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</ul>
							</div>
						</c:if>
						<c:if test="${isRolActualRepresentant}">
							<div class="dropdown">
								<a class="btn btn-primary" href="<c:url value="/entitat/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a>
							</div>
						</c:if>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
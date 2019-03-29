<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
%>
<html>
<head>
	<title><spring:message code="entitat.permis.list.titol"/></title>
	<meta name="subtitle" content="${entitat.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="<c:url value="/entitat/${entitat.id}/permis/new"/>" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="entitat.permis.list.boto.nou.permis"/></a>
	</div>
	<table
		id="permisos"
		data-toggle="datatable"
		data-url="<c:url value="/entitat/${entitat.id}/permis/datatable"/>"
		data-search-enabled="false"
		data-paging-enabled="false"
		data-default-order="3"
		data-default-dir="asc"
		class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="usuari" data-visible="false"></th>
				<th data-col-name="administrador" data-visible="false"></th>
				<th data-col-name="administradorEntitat" data-visible="false"></th>
				<th data-col-name="aplicacio" data-visible="false"></th>
				<th data-col-name="tipus" data-renderer="enum(TipusEnumDto)">
					<spring:message code="entitat.permis.list.columna.tipus"/>
				</th>
				<th data-col-name="principal"><spring:message code="entitat.permis.form.camp.principal"/></th>
				<th data-template="#cellPermisosTemplate">
					<spring:message code="entitat.permis.list.columna.permisos"/>
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						{{if usuari}}<span class="label label-default"><spring:message code="entitat.permis.list.permis.usuari"/></span>{{/if}}
						{{if administrador}}<span class="label label-default"><spring:message code="entitat.permis.list.permis.administrador"/></span>{{/if}}
						{{if administradorEntitat}}<span class="label label-default"><spring:message code="entitat.permis.list.permis.administradorentitat"/></span>{{/if}}
						{{if aplicacio}}<span class="label label-default"><spring:message code="entitat.permis.list.permis.aplicacio"/></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/entitat/${entitatId}/permis/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="<c:url value="/entitat/${entitatId}/permis/{{:id}}/delete"/>" data-confirm="<spring:message code="entitat.permis.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<c:if test="${isRolActualAdministrador}">
		<div class="text-right">
			<a class="btn btn-default" href="<c:url value="/entitat"/>" data-datatable-id="permisos"><span class="fa fa-reply"></span>&nbsp;<spring:message code="entitat.permis.list.boto.tornar"/></a>
		</div>
	</c:if>
</body>
</html>

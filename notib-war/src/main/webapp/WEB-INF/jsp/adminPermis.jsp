<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="permis.list.titol"/></title>
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
		<a class="btn btn-default" href="<c:url value="/entitats/${entitat.id}/permis/new"/>" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="permis.list.boto.nou.permis"/></a>
	</div>
	
	<table
		id="permisos"
		data-toggle="datatable"
		<%-- data-url="<c:url value="permis/datatable"/>" --%>
		data-url="<c:url value="/entitats/${entitat.id}/permis/datatable"/>"
		data-search-enabled="false"
		data-paging-enabled="false"
		data-default-order="1"
		data-default-dir="asc"
		class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="tipus" data-renderer="enum(TipusEnumDto)">
					<spring:message code="permis.list.columna.tipus"/>
				</th>
				<th data-col-name="nom"><spring:message code="permis.form.camp.nom"/></th>
				
				<th data-col-name="representant" data-template="#cellPermisTemplate">
					<spring:message code="permis.list.columna.Permis"/>
					<script id="cellPermisTemplate" type="text/x-jsrender">
						{{if representant}}<spring:message code="permis.list.valor.representant"/>{{/if}}
						{{if aplicacio}}<spring:message code="permis.list.valor.aplicacio"/>{{/if}}
					</script>
				</th>
				
				<th data-col-name="aplicacio" data-visible="false"></th>
				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/entitats/${entitatId}/permis/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="<c:url value="/entitats/${entitatId}/permis/{{:id}}/delete"/>" data-confirm="<spring:message code="entitat.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<div class="text-right" >
		<a class="btn btn-default" href="<c:url value="/entitats/"/>" data-datatable-id="permisos"><span class="fa fa-reply"></span>&nbsp;<spring:message code="usuari.list.boto.tornar.enrere"/></a>
	</div>
	
</body>
</html>

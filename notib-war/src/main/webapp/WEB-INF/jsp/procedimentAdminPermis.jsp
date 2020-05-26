<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="procediment.permis.titol"/></title>
	<meta name="subtitle" content="${procediment.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table 
		id="taulaDades" 
		data-toggle="datatable" 
		data-url="<c:url value="/procediment/${procediment.id}/permis/datatable"/>" 
		data-search-enabled="false" 
		data-paging-enabled="false" 
		data-default-order="1" 
		data-default-dir="asc" 
		data-botons-template="#tableButtonsTemplate" 
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="tipus" data-renderer="enum(TipusEnumDto)"><spring:message code="procediment.permis.columna.tipus"/></th>
				<th data-col-name="principal"><spring:message code="procediment.permis.columna.principal"/></th>
				<th data-col-name="read" data-template="#cellPermisReadTemplate">
					<spring:message code="procediment.permis.columna.consulta"/>
					<script id="cellPermisReadTemplate" type="text/x-jsrender">
						{{if read}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="processar" data-template="#cellPermisProcessarTemplate">
					<spring:message code="procediment.permis.columna.processar"/>
					<script id="cellPermisProcessarTemplate" type="text/x-jsrender">
						{{if processar}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="notificacio" data-template="#cellPermisNotificacioTemplate">
					<spring:message code="procediment.permis.columna.notificacio"/>
					<script id="cellPermisNotificacioTemplate" type="text/x-jsrender">
						{{if notificacio}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="administration" data-template="#cellPermisGestioTemplate">
					<spring:message code="procediment.permis.columna.gestio"/>
					<script id="cellPermisGestioTemplate" type="text/x-jsrender">
						{{if administration}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="../../procediment/${procediment.id}/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="../../procediment/${procediment.id}/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="../../procediment/${procediment.id}/permis/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.permis.boto.nou.permis"/></a></p>
	</script>
	<a href="<c:url value="/procediment?mantenirPaginacio=true"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
	<div class="clearfix"></div>
</body>
</html>

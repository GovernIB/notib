<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="avis.list.titol"/></title>

	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/datatable.accions-massives.js"/>"></script>
	<link href="<c:url value="/css/datatable-accions-massives.css"/>" rel="stylesheet"/>

</head>
<body>
	<script type="text/javascript">
		$(document).ready(function() {
			let eventMessages = {
				'confirm-accio-massiva': "<spring:message code="enviament.list.user.confirm.accio.massiva"/>",
			};
			initEvents($('#avisos'), 'avis', eventMessages);
		});
	</script>
	<table id="avisos"
		   data-toggle="datatable"
		   data-url="<c:url value="/avis/datatable"/>"
		   data-search-enabled="false"
		   data-default-order="2"
		   data-default-dir="asc"
		   data-selection-enabled="true"
		   data-botons-template="#botonsTemplate"
		   class="table table-striped table-bordered"
		   style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="assumpte"><spring:message code="avis.list.columna.assumpte"/></th>
				<th data-col-name="dataInici" data-converter="date"><spring:message code="avis.list.columna.dataInici"/></th>
				<th data-col-name="dataFinal" data-converter="date"><spring:message code="avis.list.columna.dataFinal"/></th>
				<th data-col-name="actiu" data-template="#cellActivaTemplate">
					<spring:message code="avis.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="avisNivell" data-orderable="false" width="10%" data-template="#cellAvisNivellTemplate">
					<spring:message code="avis.list.columna.avisNivell"/>
					<script id="cellAvisNivellTemplate" type="text/x-jsrender">
						{{if avisNivell == 'INFO'}}
							<spring:message code="avis.nivell.enum.INFO"/>
						{{else avisNivell == 'WARNING'}}
							<spring:message code="avis.nivell.enum.WARNING"/>
						{{else avisNivell == 'ERROR'}}
							<spring:message code="avis.nivell.enum.ERROR"/>
						{{/if}}
					</script>
				</th>			
				
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="avis/{{:id}}" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !actiu}}
									<li><a href="avis/{{:id}}/enable"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
									<li><a href="avis/{{:id}}/disable"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="avis/{{:id}}/delete" data-confirm="<spring:message code="avis.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="enviament.list.user.seleccio.tots" />" class="btn btn-default" ><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="enviament.list.user.seleccio.cap" />" class="btn btn-default" ><span class="fa fa-square-o"></span></button>
				<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					<span class="badge seleccioCount">${fn:length(seleccio)}</span> <spring:message code="enviament.list.user.accions.massives"/> <span class="caret"></span>
				</button>
				<ul class="dropdown-menu dropdown-left">
					<li><a href="avis/enable/massiu"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
					<li><a href="avis/disable/massiu"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
					<li><a href="avis/delete/massiu"title='<spring:message code="comu.boto.esborrar"/>'><span class="fa fa-trash"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
				</ul>
				<a class="btn btn-default" href="avis/new" data-toggle="modal" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="avis.list.boto.nova.avis"/></a>
			</div>
		</div>
	</script>
</body>
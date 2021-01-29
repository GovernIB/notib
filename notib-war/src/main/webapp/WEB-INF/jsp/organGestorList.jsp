<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="organgestor.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	<script type="text/javascript">
	$(document).ready(function() {
		$('#btnNetejar').click(function() {
			$(':input', $('#filtre')).each (function() {
				var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea') {
					this.value = '';
				} else if (type == 'checkbox' || type == 'radio') {
					this.checked = false;
				} else if (tag == 'select') {
					this.selectedIndex = 0;
				}
			});
			$('#form-filtre').submit();
		});
	});
	</script>
</head>
<body>
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="organGestorFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<not:inputText name="codi" inline="true" placeholderKey="organgestor.list.columna.codi"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="nom" inline="true" placeholderKey="organgestor.list.columna.nom"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="oficina" inline="true" placeholderKey="organgestor.list.columna.oficina"/>
			</div>
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<table
		id="organGestor"
		data-toggle="datatable"
		data-url="<c:url value="/organgestor/datatable"/>"
		data-search-enabled="false"
		data-default-order="1"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-botons-template="#botonsTemplate"
		data-save-state="true"
		data-mantenir-paginacio="true"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="codi"><spring:message code="organgestor.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="organgestor.list.columna.nom"/></th>
				<c:if test="${setLlibre}">
					<th data-col-name="llibreCodiNom"><spring:message code="procediment.list.columna.llibre"/></th>
				</c:if>
				<c:if test="${setOficina}"> 
					<th data-col-name="oficinaCodiNom"><spring:message code="organgestor.list.columna.oficina.sir"/>
				</c:if>
				<c:if test="${!setOficina}"> 
					<th data-col-name="oficinaNom"><spring:message code="organgestor.list.columna.oficina"/></th>
				</c:if>
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="100px">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="organgestor/{{:id}}/permis" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="organgestor.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="100px">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<c:if test="${setOficina}">
									<li><a href="organgestor/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								</c:if>
								<li><a href="organgestor/{{:codi}}/update" data-toggle="ajax"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="organgestor.list.boto.actualitzar"/></a></li>
								<li><a href="organgestor/{{:codi}}/delete" data-toggle="ajax" data-confirm="<spring:message code="organgestor.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			<a id="organgestor-boto-update" class="btn btn-warning" href="organgestor/update"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="organgestor.list.boto.actualitzar.tots"/></a>
			<a id="organgestor-boto-nou" class="btn btn-default" href="organgestor/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="organgestor.list.boto.nou"/></a>
		</p>
	</script>
	
</body>
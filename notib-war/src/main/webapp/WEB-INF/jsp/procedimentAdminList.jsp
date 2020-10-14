<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<% 
pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
%>
<html>
<head>
	<title><spring:message code="procediment.list.titol"/></title>
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
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="procedimentFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<not:inputText name="codi" inline="true" placeholderKey="procediment.list.columna.codi"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="nom" inline="true" placeholderKey="procediment.list.columna.nom"/>
			</div>
			<div class="col-md-5">
				<not:inputSelect name="organGestor" optionItems="${organsGestors}" optionValueAttribute="codi" optionTextAttribute="valor" placeholderKey="notificacio.list.filtre.camp.organGestor" inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
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
		id="procediment"
		data-toggle="datatable"
		data-url="<c:url value="/procediment/datatable"/>"
		data-search-enabled="false"
		data-default-order="3"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		data-botons-template="#botonsTemplate"
		style="width:100%"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="codi"><spring:message code="procediment.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="procediment.list.columna.nom"/></th>
				<th data-col-name="entitatNom"><spring:message code="procediment.list.columna.entitat"/></th>
				<th data-col-name="organGestorDesc"><spring:message code="procediment.list.columna.organGestor"/></th>
				<th data-col-name="comu" data-template="#cellComuTemplate">
					<spring:message code="procediment.list.columna.comu"/>
					<script id="cellComuTemplate" type="text/x-jsrender">
						{{if comu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="pagadorpostal"><spring:message code="procediment.list.columna.pagadorpostal"/></th>
				<th data-col-name="pagadorcie"><spring:message code="procediment.list.columna.pagadorcie"/></th>
				<th data-col-name="agrupar" data-visible="false" id="agrupable"></th>
			
				<th data-col-name="grupsCount" data-template="#cellGrupsTemplate" data-orderable="false" width="10%">
					<script id="cellGrupsTemplate" type="text/x-jsrender">
					{{if agrupar != true}}
						<button class="btn btn-default" disabled><span class="fa fa-users"></span>&nbsp;<spring:message code="procediment.list.boto.grups"/>&nbsp;</a>
					{{else}}
						<a href="${unitatCodiUrlPrefix}procediment/{{:id}}/grup" class="btn btn-default"><span class="fa fa-users"></span>&nbsp;<spring:message code="procediment.list.boto.grups"/>&nbsp;<span class="badge">{{:grupsCount}}</span></button>
					{{/if}}
					</script>
				</th>	
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="10%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="${unitatCodiUrlPrefix}procediment/{{:id}}/permis" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="procediment.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${unitatCodiUrlPrefix}procediment/{{:id}}" data-toggle="modal" data-maximized="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="${unitatCodiUrlPrefix}procediment/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			<c:if test="${isRolActualAdministradorEntitat}">
				<a id="procediment-boto-cache" class="btn btn-warning" href="${unitatCodiUrlPrefix}procediment/cache/refrescar"><span class="fa fa-trash"></span>&nbsp;<spring:message code="procediment.list.boto.cache"/></a>
				<c:if test="${!isCodiDir3Entitat}">
					<a id="procediment-boto-update" class="btn btn-default" href="${unitatCodiUrlPrefix}procediment/update/auto" data-toggle="modal" data-maximized="false"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="procediment.list.boto.procediment.auto"/></a>
				</c:if>
			</c:if>
			<a id="procediment-boto-nou" class="btn btn-default" href="${unitatCodiUrlPrefix}procediment/new" data-toggle="modal" data-maximized="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.list.boto.nou.procediment"/></a>
		</p>
	</script>
	
</body>
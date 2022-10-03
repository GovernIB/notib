<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(request));
%>
<html>
<head>
	<title><spring:message code="aplicacio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	<link href="<c:url value="/css/entitatusuari.css"/>" rel="stylesheet" type="text/css">
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="<c:url value="/entitat/${entitat.id}/aplicacio/new"/>" data-toggle="modal" data-datatable-id="taulaAplicacions"><span class="fa fa-plus"></span>&nbsp;<spring:message code="aplicacio.list.boto.nova.aplicacio"/></a>
	</div>
	<form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="aplicacioFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<not:inputText name="codiUsuari" inline="true"  placeholderKey="aplicacio.list.filtre.camp.codi.usuari"/>
			</div>
			<div class="col-md-4">
				<not:inputText name="callbackUrl" inline="true"  placeholderKey="aplicacio.list.filtre.camp.callback.url"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect id="activa" name="activa" optionItems="${aplicacioFiltreCommand.aplicacioEstats}" optionValueAttribute="value"
								 optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"/>
			</div>
		</div>
		<div class="col-md-2 pull-right form-buttons"  style="text-align: right;">
			<button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
			<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
		</div>
	</form:form>
	<script id="botonsTemplate" type="text/x-jsrender"></script>
	<table
		id="taulaAplicacions"
		data-toggle="datatable"
		data-url="<c:url value="/entitat/${entitat.id}/aplicacio/datatable"/>"
		data-search-enabled="false"
		data-selection-enabled="false"
		data-default-order="0" 
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		class="table table-bordered table-striped"
		data-info-type="search"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="usuariCodi" width="20%"><spring:message code="aplicacio.list.columna.codi"/></th>
				<th data-col-name="callbackUrl" width="50%"><spring:message code="aplicacio.list.columna.callback.url"/></th>
				<th data-col-name="activa" data-template="#cellActivaTemplate">
					<spring:message code="aplicacio.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="aplicacio/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !activa}}
								<li><a href="aplicacio/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="aplicacio/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="aplicacio/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="aplicacio.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<c:if test="${isRolActualAdministrador}">
		<div class="text-right">
			<a class="btn btn-default" href="<c:url value="/entitat"/>" data-datatable-id="taulaAplicacions"><span class="fa fa-reply"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		</div>
	</c:if>

<script>
	$(document).ready(() =>   {
		$('#btn-netejar-filtre').click(() => {
			$(':input', $('#form-filtre')).each((x, y) => {
				let type = y.type, tag = y.tagName.toLowerCase();
				if (type === 'text') {
					y.value = '';
				}
				if (tag === 'select') {
					y.selectedIndex = 0;
				}
			});
			$('#form-filtre').submit();
		});
	});
</script>
</body>
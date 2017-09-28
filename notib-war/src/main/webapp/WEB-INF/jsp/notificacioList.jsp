<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
%>
<c:set var="ampladaConcepte">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">35%</c:when>
		<c:otherwise>55%</c:otherwise>
	</c:choose>
</c:set>
<html>
<head>
	<title><spring:message code="notificacio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script type="text/javascript">
var enviamentEstats = [];
<c:forEach var="estat" items="${notificacioDestinatariEstats}">
enviamentEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>
	$(document).ready(function() {
		$('#notificacio').on('rowinfo.dataTable', function(e, td, rowData) {
			var getUrl = "<c:url value="/notificacio/"/>" + rowData.id + "/enviament";
	        $.get(getUrl).done(function(data) {
	        	$(td).append(
	        			'<table class="table teble-striped table-bordered"><thead>' +
	        			'<tr>' +
        				'<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' + 
	        			'<th><spring:message code="notificacio.list.enviament.list.destinatari"/></th>' + 
	        			'<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
	        			'<th></th>' + 
	        			'</tr>' +
						'</thead><tbody></tbody></table>');
	        	contingutTbody = '';
				for (i = 0; i < data.length; i++) {
					contingutTbody += '<tr>';
					contingutTbody += '<td>' + data[i].titular + '</td>';
					contingutTbody += '<td>' + data[i].destinatari + '</td>';
					contingutTbody += '<td>';
					contingutTbody += (data[i].estat) ? enviamentEstats[data[i].estat] : '';
					if (data[i].notificaError) {
						var errorTitle = '';
						if (data[i].notificaErrorError) {
							errorTitle = data[i].notificaErrorError;
						}
						var escaped = data[i].notificaErrorError.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
						contingutTbody += ' <span class="fa fa-warning text-danger" title="' + escaped + '"></span>';
					}
					contingutTbody += '</td>';
					contingutTbody += '<td width="5%">';
					contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '"/>" data-toggle="modal" class="btn btn-default btn-sm"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>';
					<%--contingutTbody += '<div class="dropdown">';
					contingutTbody += '<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>';
					contingutTbody += '<ul class="dropdown-menu">';
					contingutTbody += '<li><a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/info"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>';
					contingutTbody += '<li><a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/event"/>" data-toggle="modal"><span class="fa fa-calendar-o"></span>&nbsp;&nbsp;<spring:message code="notificacio.list.enviament.list.accio.events"/></a></li>';
					contingutTbody += '<li><a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].referencia + '/consultarEstat"/>" data-toggle="modal"><span class="fa fa-share-square-o"></span>&nbsp;&nbsp;<spring:message code="notificacio.list.enviament.list.accio.consultar.estat"/></a></li>';
					contingutTbody += '</ul>';
					contingutTbody += '</div>';--%>
					contingutTbody += '</td>';
					contingutTbody += '</tr>';
				}
				$('table tbody', td).append(contingutTbody);
				$('table tbody td').webutilModalEval();
			});
		});
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
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="notificacioFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<not:inputText name="concepte" inline="true"  placeholderKey="notificacio.list.filtre.camp.concepte"/>
			</div>
			<div class="col-md-3">
				<not:inputDate name="dataInici" placeholderKey="notificacio.list.filtre.camp.datainici" inline="true" required="false" />
			</div>
			<div class="col-md-3">
				<not:inputDate name="dataFi" placeholderKey="notificacio.list.filtre.camp.datafi" inline="true" required="false" />
			</div>
			<div class="col-md-3">
				<not:inputText name="destinatari" inline="true" placeholderKey="notificacio.list.filtre.camp.destinatari"/>
			</div>
			<c:if test="${isRolActualAdministrador}">
				<div class="col-md-3">
					<not:inputSelect name="entitatId" optionItems="${entitat}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.entitat" inline="true"/>
				</div>
			</c:if>
			<div class="pull-right form-buttons">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	<table
		id="notificacio"
		data-toggle="datatable"
		data-url="<c:url value="/notificacio/datatable"/>"
		data-search-enabled="false"
		data-default-order="3"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-row-info="true"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="errorNotifica" data-visible="false"></th>
				<th data-col-name="errorNotificaError" data-visible="false"></th>
				<th data-col-name=createdDate data-converter="datetime" width="15%"><spring:message code="notificacio.list.columna.enviament.data"/></th>
				<th data-col-name="concepte" width="${ampladaConcepte}"><spring:message code="notificacio.list.columna.concepte"/></th>
				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="entitat.nom" width="20%"><spring:message code="notificacio.list.columna.entitat"/></th>
				</c:if>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="20%">
					<spring:message code="notificacio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<span class="fa fa-clock-o"></span>&nbsp;<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT"/>
						{{else estat == 'ENVIADA'}}
							<span class="fa fa-send-o"></span>&nbsp;<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.ENVIADA"/>
						{{else estat == 'FINALITZADA'}}
							<span class="fa fa-check"></span>&nbsp;<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.FINALITZADA"/>
						{{else}}
							{{:estat}}
						{{/if}}
						{{if errorNotifica}}<span class="fa fa-warning text-danger" title="{{:errorNotificaError}}"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="5%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/notificacio/{{:id}}"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
						<%--<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/notificacio/{{:id}}"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								<li><a href="<c:url value="/notificacio/{{:id}}/event"/>" data-toggle="modal"><span class="fa fa-calendar-o"></span>&nbsp;<spring:message code="notificacio.list.accio.events"/></a></li>
								{{if estat == 'PENDENT'}}
									<li><a href="<c:url value="/notificacio/{{:id}}/enviar"/>"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="notificacio.list.accio.enviar"/></a></li>
								{{/if}}
								<li><a href="<c:url value="/notificacio/{{:id}}/document"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="notificacio.list.accio.descarregar.document"/></a></li>
							</ul>
						</div>--%>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

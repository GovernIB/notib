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

<html>
<head>
	<title><spring:message code="consulta.list.titol"/></title>
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
	<link href="<c:url value="/css/notificacio.css"/>" rel="stylesheet" type="text/css">
<script type="text/javascript">
	$(document).ready(function() {
		$('#notificacio').on('rowinfo.dataTable', function(e, td, rowData) {
			var getUrl = "<c:url value="/notificacions/"/>" + rowData.id + "/destinatari";
			console.log('>>> getUrl: ' + getUrl);
	        $.get(getUrl).done(function(data) {
	        	$(td).append(	
	        			'<table class="table teble-striped table-bordered"><thead>' +
	        			'<tr>' +
	        				'<th><spring:message code="notificacio.list.destinatari.list.destinatari"/></th>' + 
	        				'<th><spring:message code="notificacio.list.destinatari.list.estat"/></th>' +
	        				'<th></th>' + 
	        			'</tr>' +
						'</thead><tbody></tbody></table>'
	        	);
	        	$table = '';
				for (i = 0; i < data.length; i++) { 
					$table = $table + '<tr>';
					$table = $table + '<td>' + data[i].destinatari + '</td>';
					$table = $table + '<td>';
					if(data[i].estatUnificat == 'AUSENT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.AUSENT"/>';
					} else if(data[i].estatUnificat == 'DESCONEGUT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.DESCONEGUT"/>';
					} else if(data[i].estatUnificat == 'ADRESA_INCORRECTA') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.ADRESA_INCORRECTA"/>';
					} else if(data[i].estatUnificat == 'EDITANT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.EDITANT"/>';
					} else if(data[i].estatUnificat == 'ENVIADA_CENTRE_IMPRESSIO') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.ENVIADA_CENTRE_IMPRESSIO"/>';
					} else if(data[i].estatUnificat == 'ENVIADA_DEH') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.ENVIADA_DEH"/>';
					} else if(data[i].estatUnificat == 'LLEGIDA') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.LLEGIDA"/>';
					} else if(data[i].estatUnificat == 'ERROR_ENVIAMENT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.ERROR_ENVIAMENT"/>';
					} else if(data[i].estatUnificat == 'EXTRAVIADA') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.EXTRAVIADA"/>';
					} else if(data[i].estatUnificat == 'MORT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.MORT"/>';
					} else if(data[i].estatUnificat == 'NOTIFICADA') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.NOTIFICADA"/>';
					} else if(data[i].estatUnificat == 'PENDENT_ENVIAMENT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.PENDENT_ENVIAMENT"/>';
					} else if(data[i].estatUnificat == 'PENDENT_COMPAREIXENSA') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.PENDENT_COMPAREIXENSA"/>';
					} else if(data[i].estatUnificat == 'REBUTJADA') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.REBUTJADA"/>';
					} else if(data[i].estatUnificat == 'DATA_ENVIAMENT_PROGRAMAT') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.DATA_ENVIAMENT_PROGRAMAT"/>';
					} else if(data[i].estatUnificat == 'SENSE_INFORMACIO') {
						$table = $table + '<spring:message code="es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum.SENSE_INFORMACIO"/>';
					} else {
						$table = $table + data[i].estatUnificat;
					}
					$table = $table + '</td>';
					$table = $table + '<td width="10%">';
					$table = $table + '<div class="dropdown">';
					$table = $table + '<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>';
					$table = $table + '<ul class="dropdown-menu">';
					$table = $table + '<li><a href="<c:url value="/notificacions/' + rowData.id + '/destinatari/' + data[i].id + '/info"/>" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>';
					$table = $table + '<li><a href="<c:url value="/notificacions/' + rowData.id + '/destinatari/' + data[i].id + '/llistaevents"/>" data-toggle="modal"><span class="fa fa-calendar-check-o"></span>&nbsp;&nbsp;<spring:message code="notificacio.list.destinatari.list.boto.events"/></a></li>';
					$table = $table + '</ul>';
					$table = $table + '</div>';
					$table = $table + '</td>';
					$table = $table + '</tr>';
				}
				$('table tbody', td).append($table);
				$('table tbody td').webutilModalEval();
			});
		});
		$('#btnNetejar').click(function() {
				$(':input', $('#filtre')).each (function() {
					var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea')
					this.value = '';
				else if (type == 'checkbox' || type == 'radio')
						this.checked = false;
				else if (tag == 'select')
						this.selectedIndex = 0;
			});
				$('#form-filtre').submit();
		});
	});
	function callPDF(notId) {
		$.get("<c:url value="/consulta/showpdf/"/>" + notId);
	}
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
		data-url="<c:url value="/notificacions/datatable"/>"
		data-search-enabled="false"
		data-default-order="2"
		data-default-dir="asc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-row-info="true"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="enviamentDataProgramada" data-converter="datetime" width="15%"><spring:message code="notificacio.list.columna.enviament.data.programada"/></th>
				<c:set var="myWidth">
					<c:choose>
						<c:when test="${isRolActualAdministrador}">35%</c:when>
						<c:otherwise>55%</c:otherwise>
					</c:choose>
				</c:set>
				<th data-col-name="concepte" width="${myWidth}"><spring:message code="notificacio.list.columna.concepte"/></th>
				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="entitat.nom" width="20%"><spring:message code="notificacio.list.columna.entitat"/></th>
				</c:if>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="20%">
					<spring:message code="notificacio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT"/>
						{{else estat == 'ENVIADA_NOTIFICA'}}
							<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.ENVIADA_NOTIFICA"/>
						{{else estat == 'PROCESSADA'}}
							<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT"/>
						{{else}}
							{{:estat}}
						{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/notificacions/{{:id}}"/>" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								<li><a href="<c:url value="/notificacions/{{:id}}/llistaevents"/>" data-toggle="modal"><span class="fa fa-calendar-check-o"></span>&nbsp;<spring:message code="notificacio.list.boto.events"/></a></li>								
								<li><a href="<c:url value="/notificacions/descarregar/{{:id}}"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="notificacio.list.boto.descarregar.document"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

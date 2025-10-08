<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="integracio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
<script>

	function formate_date(timestamp, converter) {
		if (converter.indexOf('date') === 0 || converter.indexOf('time') != -1) {
			var date = new Date(timestamp);
			var horaAmbFormat = "";
			var dataAmbFormat;
			if (converter.indexOf('time') != -1) {
				var hores = ("00" + date.getHours()).slice(-2);
				var minuts = ("00" + date.getMinutes()).slice(-2);
				var segons = ("00" + date.getSeconds()).slice(-2);
				horaAmbFormat = hores + ":" + minuts + ":" + segons;
			}
			if (converter.indexOf('date') === 0) {
				var dia = ("00" + date.getDate()).slice(-2);
				var mes = ("00" + (date.getMonth() + 1)).slice(-2);
				var any = date.getFullYear();
				dataAmbFormat = dia + "/" + mes + "/" + any;
				if (converter == 'datetime') {
					dataAmbFormat += " " + horaAmbFormat;
				}
			} else {
				dataAmbFormat = horaAmbFormat;
			}
		}
		return dataAmbFormat;
	}

	$(document).ready(function (){

		$('#missatges-integracions').on('click', '.integracio-details', function() {

			let id = $(this).attr("data-index");
			let baseUrl = "<c:url value="/integracio/${codiActual}/detall/"/>";
			$.ajax({
				type: 'GET',
				url: baseUrl + id,
				success: data => {
					$('#integracio-data').html(formate_date(data.data, 'datetime'));
					$('#integracio-descripcio').html(data.descripcio);
					$('#integracio-tipus').html(data.tipus);
					$('#integracio-estat').html(data.estat);
					if (!data.parametres || data.parametres.length === 0) {
						$('.integracio-parameters').hide();
					} else {

						$('.integracio-parameters').show();
						let htmlList = "<ul>";
						let clipBoard = ""
						data.parametres.forEach(function (param) {
							htmlList += '<li><strong>' + param.codi + ':</strong> ' + param.valor + '</li>';
							clipBoard += "\b" + param.codi + '\b: ' + param.valor + "\n";
						});
						htmlList += '</ul>';
						$('#integracio-parameters-list').html(htmlList);
						$("#copyParametres").click(e => {
							e.stopPropagation();
							navigator.clipboard.writeText(clipBoard);
						});
					}
					let showError = data.estat === 'ERROR' || data.estat === 'WARN';
					if (showError) {
						$('.integracio-error').show();
						$('#integracio-errorDescripcio').html(data.errorDescripcio);
						$('#integracio-excepcioMessage').html(data.excepcioMessage);
						if (data.excepcioStacktrace) {
							$('#integracio-excepcioStacktrace').html(data.excepcioStacktrace);
						} else {
							$('#integracio-excepcioStacktrace').hide();
						}
						let clipBoard = data.errorDescripcio + "\n" + data.excepcioStacktrace;
						$("#copyError").click(e => {
							e.stopPropagation();
							navigator.clipboard.writeText(clipBoard);
						});
					} else {
						$('.integracio-error').hide();
					}
					$('#modal-details').modal();
				},
				error: () => {
					console.error("Error obtinguent el detall de la integració " + id);
				}
			});
		});

		$('#btnNetejar').click(() => {
			$(':input', $('#filtre')).each((x, y) => {
				let type = y.type, tag = y.tagName.toLowerCase();
				if (type === 'text') {
					y.value = '';
				}
				if (tag === 'select') {
					y.selectedIndex = 0;
				}
			});
			$('#filtre').submit();
		});

		$("#netejarIntegracions").click(() => {
			if (!confirm("Aquesta acció esborrarà totes les integracions. Vols continuar?")) {
				return false;
			}
		});
	});
</script>

<form:form id="filtre" action="" method="post" cssClass="well" modelAttribute="integracioFiltreCommand">
	<div class="row">
		<div class="col-md-2">
			<not:inputText name="entitatCodi" inline="true" placeholderKey="integracio.filtre.codi.entitat"/>
		</div>
		<div class="col-md-2">
			<not:inputSelect name="estat" optionItems="${integracioEstats}" optionValueAttribute="value"
							 optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"/>
		</div>
		<div class="col-md-2">
			<not:inputSelect name="tipus" optionItems="${integracioTipus}" optionValueAttribute="value"
							 optionTextKeyAttribute="text" emptyOption="true" placeholderKey="integracio.list.columna.tipus" inline="true"/>
		</div>
		<div class="col-md-2">
			<not:inputText name="descripcio" inline="true" placeholderKey="integracio.list.columna.descripcio"/>
		</div>
		<div class="col-md-2">
			<not:inputDate name="dataInici" inline="true" placeholderKey="notificacio.list.filtre.camp.datainici" required="false"/>
		</div>
		<div class="col-md-2">
			<not:inputDate name="dataFi" inline="true" placeholderKey="notificacio.list.filtre.camp.datafi" required="false"/>
		</div>
<%--		<c:if test="${'CALLBACK' == codiActual}">--%>
			<div class="col-md-2">
				<not:inputText name="aplicacio" inline="true" placeholderKey="integracio.filtre.codi.aplicacio"/>
			</div>
<%--		</c:if>--%>

		<div class="col-md-2 pull-right">
			<div class="pull-right">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</div>
</form:form>

	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">
			<li<c:if test="${integracio.codi == codiActual}"> class="active pestanya"</c:if>>
				<a href="<c:url value="/integracio/${integracio.codi}"/>"><spring:message code="${integracio.nom}"/>
					<c:if test="${integracio.numErrors > 0}">
						<span class="badge small" style="background-color: #d9534f;">${integracio.numErrors}</span>
					</c:if>	
				</a>
			</li>
		</c:forEach>
	</ul>
	<br/>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
        	<div class="btn-group">
				<a class="btn btn-success" href="<c:url value="/integracio/diagnostic"/>" data-toggle="modal" data-height="450px"  style="margin-right:10px"><span class="fa fa-list"></span>&nbsp;<spring:message code="integracio.diagnostic"/></a>
				<button id="netejarIntegracions" class="btn btn-default" href="<c:url value="/integracio/netejar"/>"><span class="fa fa-trash"></span>&nbsp;<spring:message code="integracio.netejar"/></a>
			</div>
		</div>
	</script>
	<table id="missatges-integracions" data-toggle="datatable" data-url="<c:url value="/integracio/datatable"/>"
<%--		   data-filter="#filtre"--%>
<%--			<c:if test="${codiActual == 'CALLBACK'}">--%>
<%--			   data-search-enabled="true"--%>
<%--			   data-info-type="search"--%>
<%--			</c:if>--%>
		   class="table table-striped table-bordered" style="width:100%"
		   data-botons-template="#botonsTemplate">
		<thead>
			<tr>
<%--				<th data-col-name="excepcioMessage" data-visible="false"></th>--%>
<%--				<th data-col-name="excepcioStacktrace" data-visible="false"></th>--%>
				<th data-col-name="data" data-orderable="false" data-converter="datetime"><spring:message code="integracio.list.columna.data"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="integracio.list.columna.descripcio"/></th>
				<th data-col-name="aplicacio" data-orderable="false"><spring:message code="integracio.list.columna.aplicacio"/></th>
				<c:if test="${'REGISTRE' == codiActual or 'NOTIFICA' == codiActual or 'CALLBACK' == codiActual or 'FIRMASERV' == codiActual or 'CARPETA' == codiActual
							or 'EMAIL' == codiActualor or 'CIE' == codiActual}">
					<th data-col-name="notificacioId" data-orderable="false"><spring:message code="integracio.list.columna.notificacio.id"/></th>
				</c:if>
				<th data-col-name="tipus" data-orderable="false"><spring:message code="integracio.list.columna.tipus"/></th>
				<th data-col-name="codiEntitat" data-orderable="false"><spring:message code="integracio.list.columna.entitat"/></th>
				<th data-col-name="tempsResposta" data-template="#cellTempsTemplate" data-orderable="false">
					<spring:message code="integracio.list.columna.temps.resposta"/>
					<script id="cellTempsTemplate" type="text/x-jsrender">{{:tempsResposta}} ms</script>
				</th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" data-orderable="false">
					<spring:message code="integracio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'OK'}}
							<span class="label label-success"><span class="fa fa-check"></span>&nbsp;<spring:message code="es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto.OK"/></span>
						{{/if}}
						{{if estat == 'ERROR'}}
							<span class="label label-danger" title="{{:excepcioMessage}}"><span class="fa fa-warning"></span>&nbsp;<spring:message code="es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto.ERROR"/></span>
						{{/if}}
						{{if estat == 'WARN'}}
							<span class="label label-warning" title="{{:excepcioMessage}}"><span class="fa fa-warning"></span>&nbsp;<spring:message code="es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto.WARN"/></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<button class="integracio-details" data-index="{{:id}}" class="btn btn-default""><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></button>
					</script>
				</th>
			</tr>
		</thead>
	</table>

	<div id="modal-details" class="modal fade" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
					<h4 class="modal-title"><spring:message code="integracio.detall.titol"/></h4>
				</div>
				<div class="modal-body" style="padding: 25px; height: 184px;">
					<dl class="dl-horizontal">
						<dt><spring:message code="integracio.detall.camp.data"/></dt>
						<dd id="integracio-data"></dd>
						<dt><spring:message code="integracio.detall.camp.descripcio"/></dt>
						<dd id="integracio-descripcio"></dd>
						<dt><spring:message code="integracio.detall.camp.tipus"/></dt>
						<dd id="integracio-tipus"></dd>
						<dt><spring:message code="integracio.detall.camp.estat"/></dt>
						<dd id="integracio-estat"></dd>
						<dt class="integracio-parameters">
							<spring:message code="integracio.detall.camp.params"/>
						</dt>
						<dt>
							<button id="copyParametres" class="btn btn-default" title="<spring:message code="comu.clipboard.copy"/>"><span class="fa fa-clipboard"></span></button>
						</dt>
						<dd id="integracio-parameters-list" class="integracio-parameters" style="max-height: 300px; overflow: auto; margin-bottom: 15px;">
						</dd>
						<dt class="integracio-error"><spring:message code="integracio.detall.camp.error.desc"/>
						</dt>
						<dt>
							<button id="copyError" class="btn btn-default" title="<spring:message code="comu.clipboard.copy"/>"><span class="fa fa-clipboard"></span></button>
						</dt>
						<dd id="integracio-errorDescripcio" class="integracio-error"></dd>
						<dt class="integracio-error"><spring:message code="integracio.detall.camp.excepcio.missatge"/></dt>
						<dd id="integracio-excepcioMessage" class="integracio-error"></dd>
					</dl>
					<pre id="integracio-excepcioStacktrace" class="integracio-error" style="height:300px"></pre>
					<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px; display: none;">
						<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
					</div>
				</div>
				<div class="modal-footer">
					<a class="btn btn-default" data-modal-cancel="true" data-dismiss="modal" ><spring:message code="comu.boto.tancar"/></a>
				</div>
			</div>
		</div>
	</div>
</body>
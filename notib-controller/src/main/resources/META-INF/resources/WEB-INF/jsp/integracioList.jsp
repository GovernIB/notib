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
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
<script>
	var data = ${data};

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
		$('#missatges-integracions').on('click', '.integracio-details', function () {
			let index = $(this).data('index');
			let details = data[index];
			$('#integracio-data').html(formate_date(details.data, 'datetime'));
			$('#integracio-descripcio').html(details.descripcio);
			$('#integracio-tipus').html(details.tipus);
			$('#integracio-estat').html(details.estat);
			if (!details.parametres || details.parametres.length === 0) {
				$('.integracio-parameters').hide();
			} else {
				$('.integracio-parameters').show();
				var htmlList = "<ul>";
				details.parametres.forEach(function (param) {
					htmlList += '<li><strong>' + param.codi + ':</strong> ' + param.valor + '</li>'
				});
				htmlList += '</ul>';
				$('#integracio-parameters-list').html(htmlList);
			}
			let isError = details.estat === 'ERROR';
			if (isError){
				$('.integracio-error').show();
				$('#integracio-errorDescripcio').html(details.errorDescripcio);
				$('#integracio-excepcioMessage').html(details.excepcioMessage);
				if (details.excepcioStacktrace) {
					$('#integracio-excepcioStacktrace').html(details.excepcioStacktrace);
				} else {
					$('#integracio-excepcioStacktrace').hide();
				}

			}else {
				$('.integracio-error').hide();
			}

			$('#modal-details').modal();
		});

		<%--let codi = "${codiActual}";--%>
		<%--if (codi === "CALLBACK") {--%>
		<%--	console.log("show");--%>
		<%--	console.log($("#missatges-integracions_filter"));--%>
		<%--	$("#missatges-integracions_filter").show();--%>
		<%--} else {--%>
		<%--	console.log("hide");--%>
		<%--	$("#missatges-integracions_filter").hide();--%>
		<%--}--%>


		<%--$(".pestanya").click(() => {--%>
		<%--	let codi = "${codiActual}";--%>
		<%--	if (codi === "CALLBACK") {--%>
		<%--		$("#missatges-integracions_filter").show();--%>
		<%--	} else {--%>
		<%--		$("#missatges-integracions_filter").hide();--%>
		<%--	}--%>
		<%--});--%>
	});
</script>

<form:form id="filtre" action="" method="post" cssClass="well" modelAttribute="integracioFiltreCommand">
	<div class="row">
		<div class="col-md-2">
			<not:inputText name="entitatCodi" inline="true" placeholderKey="integracio.filtre.codi.entitat"/>
		</div>
		<c:if test="${'CALLBACK' == codiActual}">
			<div class="col-md-2">
				<not:inputText name="aplicacio" inline="true" placeholderKey="integracio.filtre.codi.aplicacio"/>
			</div>
		</c:if>
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
		<p style="text-align:right"><a class="btn btn-default" href="<c:url value="/integracio/netejar"/>"><span class="fa fa-trash"></span>&nbsp;<spring:message code="integracio.netejar"/></a></p>
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
				<th data-col-name="excepcioMessage" data-visible="false"></th>
				<th data-col-name="excepcioStacktrace" data-visible="false"></th>
				<th data-col-name="data" data-orderable="false" data-converter="datetime"><spring:message code="integracio.list.columna.data"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="integracio.list.columna.descripcio"/></th>
				<c:if test="${codiActual == 'CALLBACK'}">
					<th data-col-name="aplicacio" data-orderable="false"><spring:message code="integracio.list.columna.aplicacio"/></th>
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
							<span class="label label-success"><span class="fa fa-check"></span>&nbsp;{{:estat}}</span>
						{{else}}
							<span class="label label-danger" title="{{:excepcioMessage}}"><span class="fa fa-warning"></span>&nbsp;{{:estat}}</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="index" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<button class="integracio-details" data-index="{{:index}}" class="btn btn-default""><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></button>
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
						<dt class="integracio-parameters"><spring:message code="integracio.detall.camp.params"/></dt>
						<dd id="integracio-parameters-list" class="integracio-parameters">
						</dd>
						<dt class="integracio-error"><spring:message code="integracio.detall.camp.error.desc"/></dt>
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
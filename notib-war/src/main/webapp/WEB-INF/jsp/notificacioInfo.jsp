<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<title><spring:message code="notificacio.info.titol" /></title>
<script
	src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
<script
	src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
<link
	href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>"
	rel="stylesheet"></link>
<script
	src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<not:modalHead />
<script type="text/javascript">
	var eventTipus = [];
	<c:forEach var="tipus" items="${eventTipus}">
	eventTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
	</c:forEach>
	$(document).ready(
			function() {
				$('#events').on(
						'rowinfo.dataTable',
						function(e, td, rowData) {
							$(td).empty();
							$(td).append(
									'<textarea style="width:100%" rows="10">'
											+ rowData['errorDescripcio']
											+ '</textarea>');
						});
				$('#events').on('draw.dt', function(e, settings) {
					var api = new $.fn.dataTable.Api(settings);
					api.rows().every(function(rowIdx, tableLoop, rowLoop) {
						var data = this.data();
						if (!data.error) {
							$('td:last-child', this.node()).empty();
						}
					});
				});
			});
</script>
</head>
<body>
	<c:if test="${notificacio.notificaError}">
		<div class="alert alert-danger well-sm">
			<span class="fa fa-warning text-danger"></span>
			<spring:message code="notificacio.info.error.titol" />
			<button class="btn btn-default btn-xs pull-right"
				data-toggle="collapse" data-target="#collapseError"
				aria-expanded="false" aria-controls="collapseError">
				<span class="fa fa-bars"></span>
			</button>
			<div id="collapseError" class="collapse">
				<br />
				<table class="table table-bordered"
					style="background-color: white; width: 100%">
					<tbody>
						<tr>
							<td width="10%"><strong><spring:message
										code="notificacio.info.error.data" /></strong></td>
							<td><fmt:formatDate value="${notificacio.notificaErrorData}"
									pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.error.error" /></strong></td>
							<td><textarea rows="10" style="width: 100%">${fn:escapeXml(notificacio.notificaErrorDescripcio)}</textarea></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#dades"
			aria-controls="dades" role="tab" data-toggle="tab"> <spring:message
					code="notificacio.info.tab.dades" />
		</a></li>
		<li role="presentation"><a href="#events" aria-controls="events"
			role="tab" data-toggle="tab"> <spring:message
					code="notificacio.info.tab.events" />
		</a></li>
		<c:if test="${permisGestio == null || permisGestio}">
			<li role="presentation"><a href="#accions"
				aria-controls="accions" role="tab" data-toggle="tab"> <spring:message
						code="notificacio.info.tab.accions" />
			</a></li>
		</c:if>
	</ul>
	<br />
	<div class="tab-content">
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'dades'}"> active</c:if>"
			id="dades">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<strong><spring:message
								code="notificacio.info.seccio.dades" /></strong>
					</h3>
				</div>
				<table class="table table-bordered" style="width: 100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message
										code="notificacio.info.dada.entitat" /></strong></td>
							<td>${notificacio.procediment.entitat.dir3Codi}
								(${notificacio.procediment.entitat.dir3Codi})</td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.concepte" /></strong></td>
							<td>${notificacio.concepte}</td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.tipus" /></strong></td>
							<td><spring:message
									code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.${notificacio.enviamentTipus}" /></td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.procediment.sia" /></strong></td>
							<td>(${notificacio.procediment.codisia})</td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.estat" /></strong></td>
							<td><spring:message
									code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.${notificacio.estat}" /></td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.creacio.data" /></strong></td>
							<td><fmt:formatDate value="${notificacio.createdDate}"
									pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.creacio.usuari" /></strong></td>
							<td>${notificacio.createdBy.nom}
								(${notificacio.createdBy.codi})</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message
									code="notificacio.info.seccio.document" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<tr>
								<td width="30%"><strong><spring:message
											code="notificacio.info.document.arxiu.nom" /></strong></td>
								<td>${notificacio.document.arxiuNom} <a
									href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar"/>"
									class="btn btn-default btn-sm pull-right"
									title="<spring:message code="notificacio.info.document.descarregar"/>"><span
										class="fa fa-download"></span></a>
								</td>
							</tr>
							<tr>
								<td><strong><spring:message
											code="notificacio.info.document.normalitzat" /></strong></td>
								<td>${notificacio.document.normalitzat}</td>
							</tr>
							<tr>
								<td><strong><spring:message
											code="notificacio.info.document.generar.csv" /></strong></td>
								<td>${notificacio.document.generarCsv}</td>
							</tr>
						</tbody>
					</table>
			</div>
			<c:if test="${not empty notificacio.procediment}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.postal" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if
								test="${not empty notificacio.procediment.pagadorpostal.dir3codi}">
								<tr>
									<td width="30%"><strong><spring:message
												code="notificacio.info.pagador.correus.codi.dir3" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.dir3codi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.contracte" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.contracteNum}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.client" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.facturacioClientCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.vigencia" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.contracteDataVig}</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.cie" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if
								test="${not empty notificacio.procediment.pagadorcie.dir3codi}">
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.cie.codi.dir3" /></strong></td>
									<td>${notificacio.procediment.pagadorcie.dir3codi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.cie.vigencia" /></strong></td>
									<td>${notificacio.procediment.pagadorcie.contracteDataVig}</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
			</c:if>
			<%-- <c:if test="${not empty notificacio.seuExpedientSerieDocumental}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.seucaib"/></strong>
						</h3>
	 				</div>
					<table class="table table-bordered" style="width:100%">
					<tbody>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.expedient.serie"/></strong></td>
							<td>${notificacio.seuExpedientSerieDocumental}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.expedient.unitat"/></strong></td>
							<td>${notificacio.seuExpedientUnitatOrganitzativa}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.expedient.id"/></strong></td>
							<td>${notificacio.seuExpedientIdentificadorEni}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.expedient.titol"/></strong></td>
							<td>${notificacio.seuExpedientTitol}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.registre.oficina"/></strong></td>
							<td>${notificacio.seuRegistreOficina}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.registre.llibre"/></strong></td>
							<td>${notificacio.seuRegistreLlibre}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.idioma"/></strong></td>
							<td>${notificacio.seuIdioma}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.avis.titol"/></strong></td>
							<td>${notificacio.seuAvisTitol}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.avis.text"/></strong></td>
							<td>${notificacio.seuAvisText}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.avis.text.mobil"/></strong></td>
							<td>${notificacio.seuAvisTextMobil}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.ofici.titol"/></strong></td>
							<td>${notificacio.seuOficiTitol}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.seucaib.ofici.text"/></strong></td>
							<td>${notificacio.seuOficiText}</td>
						</tr>
					</tbody>
					</table>
				</div>
			</c:if> --%>
		</div>
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'events'}"> active</c:if>"
			id="events">
			<table id="events" data-toggle="datatable"
				data-url="<c:url value="/notificacio/${notificacio.id}/event"/>"
				data-search-enabled="false" data-paging="false" data-info="false"
				data-row-info="true" class="table table-striped table-bordered"
				style="width: 100%">
				<thead>
					<tr>
						<th data-col-name="id" data-visible="false">#</th>
						<th data-col-name="enviamentAssociat" data-visible="false"></th>
						<th data-col-name="errorDescripcio" data-visible="false"></th>
						<th data-col-name="createdBy.nom" data-orderable="false"><spring:message
								code="notificacio.event.list.columna.usuari" /></th>
						<th data-col-name="data" data-converter="datetime"
							data-orderable="false"><spring:message
								code="notificacio.event.list.columna.data" /></th>
						<th data-col-name="tipus" data-template="#cellTipus"
							data-orderable="false"><spring:message
								code="notificacio.event.list.columna.tipus" /> <script
								id="cellTipus" type="text/x-jsrender">
							{{:~eval('eventTipus["' + tipus + '"]')}}
							{{if enviamentAssociat}}<span class="label label-default pull-right" title="<spring:message code="notificacio.event.list.info.associat"/>">E</span>{{/if}}
						</script></th>
						<th data-col-name="error" data-template="#cellResultat"
							data-orderable="false"><spring:message
								code="notificacio.event.list.columna.estat" /> <script
								id="cellResultat" type="text/x-jsrender">
							{{if error}}
								<span class="fa fa-warning text-danger" title="<spring:message code="enviament.event.list.processat.error"/>"></span>
							{{else}}
								<span class="fa fa-check text-success" title="<spring:message code="enviament.event.list.processat.ok"/>"></span>
							{{/if}}
						</script></th>
					</tr>
				</thead>
			</table>
		</div>
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'accions'}"> active</c:if>"
			id="accions">
			<c:set var="algunaAccioDisponible" value="${false}" />
			<ul class="list-group">
				<c:if test="${notificacio.estat == 'PENDENT'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message
										code="notificacio.info.accio.enviar" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									href="<not:modalUrl value="/notificacio/${notificacio.id}/enviar"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-send"></span>
									<spring:message code="notificacio.info.accio.enviar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
			</ul>
			<c:if test="${not algunaAccioDisponible}">
				<div class="alert alert-info well-sm" role="alert">
					<spring:message code="notificacio.info.accio.no.accions" />
				</div>
			</c:if>
		</div>
	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default"
			data-modal-cancel="true"><spring:message code="comu.boto.tancar" /></a>
	</div>
</body>
</html>

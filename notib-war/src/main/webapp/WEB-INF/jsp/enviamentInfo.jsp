<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
	<title><spring:message code="enviament.info.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
<script type="text/javascript">
var eventTipus = [];
<c:forEach var="tipus" items="${eventTipus}">
eventTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
</script>
</head>
<body>
	<c:if test="${enviament.notificaError}">
		<div class="alert alert-danger well-sm">
			<span class="fa fa-warning text-danger"></span>
			<spring:message code="enviament.info.error.titol"/>
			<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapseError" aria-expanded="false" aria-controls="collapseError">
				<span class="fa fa-bars"></span>
			</button>
			<div id="collapseError" class="collapse">
				<br/>
				<textarea rows="10" style="width:100%">${enviament.notificaErrorError}</textarea>
			</div>
		</div>
	</c:if>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.dades"/>
			</a>
		</li>
		<li role="presentation">
			<a href="#events" aria-controls="events" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.events"/>
			</a>
		</li>
		<li role="presentation">
			<a href="#accions" aria-controls="accions" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.accions"/>
			</a>
		</li>
	</ul>
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'dades'}"> active</c:if>" id="dades">
			<br/>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<strong><spring:message code="enviament.info.seccio.dades"/></strong>
					</h3>
 				</div>
				<table class="table table-bordered" style="width:100%">
				<tbody>
					<tr>
						<td width="30%"><strong><spring:message code="enviament.info.dada.referencia"/></strong></td>
						<td>${enviament.notificaReferencia}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.nif"/></strong></td>
						<td>${enviament.dehNif}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.procediment"/></strong></td>
						<td>${enviament.dehProcedimentCodi}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.obligada"/></strong></td>
						<td>${enviament.dehObligat}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.servei.tipus"/></strong></td>
						<td>${enviament.serveiTipus}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.retard.postal"/></strong></td>
						<td>${enviament.retardPostal}</td>
					</tr>
					<c:if test="${not empty enviament.caducitat}">
						<tr>
							<td><strong><spring:message code="enviament.info.dada.caducitat"/></strong></td>
							<td><fmt:formatDate value="${enviament.caducitat}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
					</c:if>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.estat"/></strong></td>
						<td>
							<c:if test="${not empty enviament.estat}">
								<spring:message code="es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.${enviament.estat}"/>
								<button class="btn btn-default btn-sm pull-right" role="button" data-toggle="collapse" data-target="#collapseEstat" aria-expanded="false" aria-controls="collapseEstat">
									<span class="fa fa-info-circle"></span>
									<spring:message code="enviament.info.dada.estat.mesinfo"/>
								</button>
								<div id="collapseEstat" class="collapse" style="margin-top: 1.5em">
									<table class="table table-bordered" style="width:100%">
									<tbody>
										<tr>
											<td><strong><spring:message code="enviament.info.dada.estat.notifica"/></strong></td>
											<td>
												<spring:message code="es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.${enviament.notificaEstat}"/>
											</td>
										</tr>
										<tr>
											<td><strong><spring:message code="enviament.info.dada.estat.seucaib"/></strong></td>
											<td>
												<spring:message code="es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.${enviament.seuEstat}"/>
											</td>
										</tr>
									</tbody>
									</table>
								</div>
							</c:if>
						</td>
					</tr>
				</tbody>
				</table>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<strong><spring:message code="enviament.info.seccio.titular"/></strong>
					</h3>
 				</div>
				<table class="table table-bordered" style="width:100%">
				<tbody>
					<tr>
						<td><strong><spring:message code="enviament.info.titular.nif"/></strong></td>
						<td>${enviament.titularNif}</td>
					</tr>
					<c:if test="${not empty enviament.titularNom}">
						<tr>
							<td width="30%"><strong><spring:message code="enviament.info.titular.nom"/></strong></td>
							<td>${enviament.titularNom}</td>
						</tr>
					</c:if>
					<c:if test="${not empty enviament.titularLlinatges}">
						<tr>
							<td><strong><spring:message code="enviament.info.titular.llinatges"/></strong></td>
							<td>${enviament.titularLlinatges}</td>
						</tr>
					</c:if>
					<c:if test="${not empty enviament.titularTelefon}">
						<tr>
							<td><strong><spring:message code="enviament.info.titular.telefon"/></strong></td>
							<td>${enviament.titularTelefon}</td>
						</tr>
					</c:if>
					<c:if test="${not empty enviament.titularEmail}">
						<tr>
							<td><strong><spring:message code="enviament.info.titular.email"/></strong></td>
							<td>${enviament.titularEmail}</td>
						</tr>
					</c:if>
				</tbody>
				</table>
			</div>
			<c:if test="${not empty enviament.destinatariNif}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="enviament.info.seccio.destinatari"/></strong>
						</h3>
	 				</div>
					<table class="table table-bordered" style="width:100%">
					<tbody>
						<tr>
							<td><strong><spring:message code="enviament.info.destinatari.nif"/></strong></td>
							<td>${enviament.destinatariNif}</td>
						</tr>
						<c:if test="${not empty enviament.destinatariNom}">
							<tr>
								<td width="30%"><strong><spring:message code="enviament.info.destinatari.nom"/></strong></td>
								<td>${enviament.destinatariNom}</td>
							</tr>
						</c:if>
						<c:if test="${not empty enviament.destinatariLlinatges}">
							<tr>
								<td><strong><spring:message code="enviament.info.destinatari.llinatges"/></strong></td>
								<td>${enviament.destinatariLlinatges}</td>
							</tr>
						</c:if>
						<c:if test="${not empty enviament.destinatariTelefon}">
							<tr>
								<td><strong><spring:message code="enviament.info.destinatari.telefon"/></strong></td>
								<td>${enviament.destinatariTelefon}</td>
							</tr>
						</c:if>
						<c:if test="${not empty enviament.destinatariEmail}">
							<tr>
								<td><strong><spring:message code="enviament.info.destinatari.email"/></strong></td>
								<td>${enviament.destinatariEmail}</td>
							</tr>
						</c:if>
					</tbody>
					</table>
				</div>
			</c:if>
			<c:if test="${not empty enviament.domiciliTipus}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="enviament.info.seccio.domicili"/></strong>
						</h3>
	 				</div>
					<table class="table table-bordered" style="width:100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="enviament.info.domicili.tipus"/></strong></td>
							<td>${enviament.domiciliTipus}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.tipus.concret"/></strong></td>
							<td>${enviament.domiciliConcretTipus}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.via"/></strong></td>
							<td>${enviament.domiciliViaTipus} ${enviament.domiciliViaNom}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.numeracio"/></strong></td>
							<td>
								${enviament.domiciliNumeracioTipus}
								${enviament.domiciliNumeracioNumero}
								${enviament.domiciliNumeracioPuntKm}
								${enviament.domiciliApartatCorreus}
							</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.bloc"/></strong></td>
							<td>${enviament.domiciliBloc}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.portal"/></strong></td>
							<td>${enviament.domiciliPortal}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.escala"/></strong></td>
							<td>${enviament.domiciliEscala}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.planta"/></strong></td>
							<td>${enviament.domiciliPlanta}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.porta"/></strong></td>
							<td>${enviament.domiciliPorta}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.complement"/></strong></td>
							<td>${enviament.domiciliComplement}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.poblacio"/></strong></td>
							<td>${enviament.domiciliPoblacio}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.codi.postal"/></strong></td>
							<td>${enviament.domiciliCodiPostal}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.municipi"/></strong></td>
							<td>${enviament.domiciliMunicipiNom} (${enviament.domiciliMunicipiCodiIne})</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.provincia"/></strong></td>
							<td>${enviament.domiciliProvinciaNom} (${enviament.domiciliProvinciaCodi})</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.pais"/></strong></td>
							<td>${enviament.domiciliPaisNom} (${enviament.domiciliPaisCodiIso})</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.linea1"/></strong></td>
							<td>${enviament.domiciliLinea1}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.linea2"/></strong></td>
							<td>${enviament.domiciliLinea2}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.cie"/></strong></td>
							<td>${enviament.domiciliCie}</td>
						</tr>
					</tbody>
					</table>
				</div>
			</c:if>
		</div>
		<div role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'events'}"> active</c:if>" id="events">
			<table
				id="events"
				data-toggle="datatable"
				data-url="<c:url value="/notificacio/${notificacioId}/enviament/${enviamentId}/event"/>"
				data-search-enabled="false"
				data-paging="false"
				data-info="false"
				class="table table-striped table-bordered"
				style="width:100%">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false">#</th>
					<th data-col-name="destinatariAssociat" data-visible="false"></th>
					<th data-col-name="errorDescripcio" data-visible="false"></th>
					<th data-col-name="data" data-converter="datetime" data-orderable="false"><spring:message code="enviament.event.list.columna.data"/></th>
					<th data-col-name="tipus" data-template="#cellTipus" data-orderable="false">
						<spring:message code="enviament.event.list.columna.tipus"/>
						<script id="cellTipus" type="text/x-jsrender">
						{{:~eval('eventTipus["' + tipus + '"]')}}
					</script>
					</th>
					<th data-col-name="descripcio" data-orderable="false"><spring:message code="enviament.event.list.columna.descripcio"/></th>
					<th data-col-name="error" data-template="#cellResultat" data-orderable="false">
						<spring:message code="notificacio.event.list.columna.resultat"/>
						<script id="cellResultat" type="text/x-jsrender">
						{{if error}}
							<span class="fa fa-warning text-danger" title="{{>errorDescripcio}}"></span>
						{{else}}
							<span class="fa fa-check text-success"></span>
						{{/if}}
					</script>
					</th>
				</tr>
			</thead>
			</table>
		</div>
		<div role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'accions'}"> active</c:if>" id="accions">
			<br/>
			<c:set var="algunaAccioDisponible" value="${false}"/>
			<c:if test="${enviament.notificaEstat != 'NOTIB_PENDENT'}">
				<c:set var="algunaAccioDisponible" value="${true}"/>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="enviament.info.accio.seccio.notifica"/></strong>
							<a href="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/refrescarEstat"/>" class="btn btn-default btn-sm" style="float:right; position: relative; top: -6px; right: -6px;"><span class="fa fa-refresh"></span> <spring:message code="enviament.info.accio.refrescar.estat"/></a>
						</h3>
	  				</div>
	  				<table class="table table-bordered" style="width:100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="enviament.estat.estat.data"/></strong></td>
							<td><fmt:formatDate value="${notificacioEstat.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.estat.estat.codi"/></strong></td>
							<td>${notificacioEstat.estatCodi}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.estat.estat.descripcio"/></strong></td>
							<td>${notificacioEstat.estatDescripcio}</td>
						</tr>
						<c:if test="${not empty notificacioEstat.numSeguiment}">
							<tr>
								<td><strong><spring:message code="enviament.estat.estat.seguiment"/></strong></td>
								<td>${notificacioEstat.numSeguiment}</td>
							</tr>
						</c:if>
						<tr>
							<td colspan="2" class="text-right">
								<button class="btn btn-default"><span class="fa fa-download"></span> <spring:message code="enviament.info.accio.obtenir.cert"/></button>
								<a href="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/comunicacioSeu"/>" class="btn btn-default">
									<span class="fa fa-check-square-o"></span>
									<spring:message code="enviament.info.accio.comunicacio.seu"/>
								</a>
							</td>
						</tr>
					</tbody>
					</table>
					<div class="panel-body text-right">
						<button class="btn btn-default"><span class="fa fa-send"></span> Enviar certificacio</button>
					</div>
				</div>
			</c:if>
			<c:if test="${pluginSeuDisponible && enviament.seuEstat != 'NOTIB_PENDENT'}">
				<c:set var="algunaAccioDisponible" value="${true}"/>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="enviament.info.accio.seccio.seu.caib"/></strong>
							<button class="btn btn-default btn-sm" style="float:right; position: relative; top: -6px; right: -6px;"><span class="fa fa-refresh"></span> <spring:message code="enviament.info.accio.refrescar.estat"/></button>
						</h3>
	  				</div>
	  				<table class="table table-bordered" style="width:100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="enviament.estat.estat.data"/></strong></td>
							<td><fmt:formatDate value="${notificacioEstat.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.estat.estat.codi"/></strong></td>
							<td>${notificacioEstat.estatCodi}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.estat.estat.descripcio"/></strong></td>
							<td>${notificacioEstat.estatDescripcio}</td>
						</tr>
						<c:if test="${not empty notificacioEstat.numSeguiment}">
							<tr>
								<td><strong><spring:message code="enviament.estat.estat.seguiment"/></strong></td>
								<td>${notificacioEstat.numSeguiment}</td>
							</tr>
						</c:if>
						<tr>
							<td colspan="2" class="text-right">
								<button class="btn btn-default"><span class="fa fa-download"></span> <spring:message code="enviament.info.accio.obtenir.cert"/></button>
							</td>
						</tr>
					</tbody>
					</table>
				</div>
			</c:if>
			<c:if test="${not algunaAccioDisponible}">
				<div class="alert alert-info well-sm" role="alert">
					<spring:message code="enviament.info.accio.no.accions"/>
				</div>
			</c:if>
		</div>
	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>

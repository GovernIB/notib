	<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
	pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
	pageContext.setAttribute(
			"isRolActualAdministradorOrgan",
			es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(request));
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(request));
%>

<html>
<head>
<title>
	<c:choose>
		<c:when test="${notificacio.enviamentTipus == 'COMUNICACIO'}">
			<spring:message code="comunicacio.info.titol" />
		</c:when>
		<c:otherwise>
			<spring:message code="notificacio.info.titol" />
		</c:otherwise>
	</c:choose>
</title>
<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/webjars/bootstrap/3.3.6/js/dropdown.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
<not:modalHead />
<script type="text/javascript">
$(function() {
    $(document).on("click", "a.fileDownloadSimpleRichExperience", function() {
        $.fileDownload($(this).attr('href'), {
            preparingMessageHtml: "Estam preparant la descàrrega, per favor esperi...",
            failMessageHtml: "<strong style='color:red'>Ho sentim.<br/>S'ha produït un error intentant descarregar el document.</strong>"
        });
        return false; //this is critical to stop the click event which will trigger a normal file download!
    });
});

var eventTipus = [];
<c:forEach var="tipus" items="${eventTipus}">
eventTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
var notificacioApp = "${notificacio.tipusUsuari == 'APLICACIO'}";
$(document).ready(function() {
	let $tableEvents = $('#table-events');
	$tableEvents.on('rowinfo.dataTable', function(e, td, rowData) {

			$(td).empty();
			$(td).append('<textarea style="width:100%" rows="10">' + rowData['errorDescripcio'] + '</textarea>');
	});
	$tableEvents.on('draw.dt', function(e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		api.rows().every(function(rowIdx, tableLoop, rowLoop) {
			var data = this.data();
			if (!data.error || data.errorDescripcio == null) {
			$('td:last-child', this.node()).empty();
			}
		});
	});

	$(document.body).on('hidden.bs.modal', function () {
		$('.tab-content').load(location.href + " .tab-content");
	});

	$('#registrar-btn').click(function() {
		$('#registrar-avis-user').css('display', 'inline-block');
	    $('#registrar-btn').attr('disabled', true);
        window.location.href = '<not:modalUrl value="/notificacio/${notificacio.id}/registrar"/>';
	    return false;    
    });	
	
	$('#enviar-btn').click(function() {
		$('#enviar-avis-user').css('display', 'inline-block');
	    $('#enviar-btn').attr('disabled', true);
        window.location.href = '<not:modalUrl value="/notificacio/${notificacio.id}/enviar"/>';
	    return false;    
    });	
	
});
</script>
<style type="text/css">
.modal-backdrop {
    visibility: hidden !important;
}
.modal.in {
    background-color: rgba(0,0,0,0.5);
}
.btn-certificacio {
	margin-top: 5%;
}
</style>
</head>
<body>
	<c:if test="${notificacio.notificaError}">
		<div class="alert alert-danger well-sm">
			<span class="fa fa-warning text-danger"></span>+
			<c:choose>
				<c:when test="${fn:contains(notificacio.noticaErrorEventTipus, 'NOTIFICA')
					&& notificacio.noticaErrorEventTipus != 'NOTIFICA_REGISTRE'}">
					<spring:message code="notificacio.info.error.titol" />
				</c:when>
				<c:when test="${fn:contains(notificacio.noticaErrorEventTipus, 'REGISTRE')}">
					<spring:message code="enviament.info.error.registre" />
				</c:when>
				<c:when test="${fn:contains(notificacio.noticaErrorEventTipus, 'CALLBACK')}">
					<spring:message code="enviament.info.error.callback" />
				</c:when>
				<c:otherwise>
					<spring:message code="notificacio.info.error.generic"></spring:message>
				</c:otherwise>
			</c:choose>
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
	<div id="registrar-avis-user" class="alert alert-info well-sm" role="alert" style="display: none; width: 100%">
		<spring:message code="notificacio.info.registrar.avis.user" />
	</div>
	<div id="enviar-avis-user" class="alert alert-info well-sm" role="alert" style="display: none; width: 100%">
		<spring:message code="notificacio.info.enviar.avis.user" />
	</div>
	<c:set var="activeTab" value="dades" />
	<c:if test="${not empty pestanyaActiva}"><c:set var="activeTab" value="${pestanyaActiva}" /></c:if>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" <c:if test='${activeTab == "dades"}'>class="active"</c:if>>
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"> 
				<spring:message code="notificacio.info.tab.dades" />
			</a>
		</li>
		<li role="presentation" <c:if test='${activeTab == "events"}'>class="active"</c:if>>
			<a href="#events" aria-controls="events" role="tab" data-toggle="tab"> 
				<spring:message code="notificacio.info.tab.events" />
			</a>
		</li>
		<c:if test="${permisGestio == null || permisGestio || isRolActualAdministradorEntitat || isRolActualAdministradorOrgan}">
			<li role="presentation" <c:if test='${activeTab == "accions"}'>class="active"</c:if>>
				<a href="#accions" aria-controls="accions" role="tab" data-toggle="tab"> 
					<spring:message code="notificacio.info.tab.accions" />
				</a>
			</li>
		</c:if>
		<c:if test="${isRolActualAdministradorEntitat || isRolActualAdministradorOrgan || isRolActualAdministrador}">
			<li role="presentation" <c:if test='${activeTab == "historic"}'>class="active"</c:if>>
				<a href="#historic" aria-controls="historic" role="tab" data-toggle="tab">
					<spring:message code="notificacio.info.tab.historic" />
				</a>
			</li>
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
						<strong>
						<c:choose>
							<c:when test="${notificacio.enviamentTipus == 'COMUNICACIO'}">
								<spring:message code="comunicacio.info.seccio.dades" />
							</c:when>
							<c:otherwise>
								<spring:message code="notificacio.info.seccio.dades" />
							</c:otherwise>
						</c:choose>
						</strong>
					</h3>
				</div>
				<table class="table table-bordered" style="width: 100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="notificacio.info.dada.entitat" /></strong></td>
							<td>${notificacio.organGestorCodi} - ${notificacio.organGestorNom}</td>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.proc.ser" /></strong></td>
							<td>${notificacio.procediment.codi} - ${notificacio.procediment.nom}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.num.expedient" /></strong></td>
							<td>${notificacio.numExpedient}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.concepte" /></strong></td>
							<td>${notificacio.concepte}</td>
						</tr>
						<c:if test="${notificacio.descripcio}">
							<tr>
								<td><strong><spring:message code="notificacio.info.dada.descripcio" /></strong></td>
								<td>${notificacio.descripcio}</td>
							</tr>
						</c:if>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.idioma" /></strong></td>
							<td>${notificacio.idioma}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.creacio.data" /></strong></td>
							<td><fmt:formatDate value="${notificacio.createdDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.creacio.usuari" /></strong></td>
							<td>${notificacio.createdBy.nom}
								(${notificacio.createdBy.codi})</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.enviament.data" /></strong></td>
							<td><fmt:formatDate value="${notificacio.enviadaDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<c:if test="${notificacio.enviamentDataProgramada != null}">
							<tr>
								<td><strong><spring:message code="notificacio.info.dada.enviament.programada.data"/></strong></td>
								<td><fmt:formatDate value="${notificacio.enviamentDataProgramada}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							</tr>
						</c:if>
						<c:if test="${notificacio.estatDate != null}">
							<tr>
								<td><strong><spring:message code="notificacio.info.dada.finalitzada.dada"/></strong></td>
								<td><fmt:formatDate value="${notificacio.estatDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							</tr>
						</c:if>
						<c:if test="${notificacio.estatProcessatDate != null}">
							<tr>
								<td><strong><spring:message code="notificacio.info.dada.processada.dada"/></strong></td>
								<td><fmt:formatDate value="${notificacio.estatProcessatDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							</tr>
						</c:if>
						<c:if test="${notificacio.caducitat != null}">
							<tr>
								<td><strong><spring:message code="notificacio.info.dada.caducitat"/></strong></td>
								<td><fmt:formatDate value="${notificacio.caducitat}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							</tr>
						</c:if>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.retard" /></strong></td>
							<td>${notificacio.retard}</td>
						</tr>
						<c:if test="${notificacio.estat != null && notificacio.estat != ''}">
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.estat" /></strong></td>
							<td>
								<c:choose>
									<c:when test="${notificacio.enviant}">
										<span class="fa fa-clock-o"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'PENDENT'}">
										<span class="fa fa-clock-o"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'ENVIADA'}">
										<span class="fa fa-send-o"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'FINALITZADA'}">
										<span class="fa fa-check"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'REGISTRADA'}">
										<span class="fa fa-file-o"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'PROCESSADA'}">
										<span class="fa fa-check-circle"></span>
									</c:when>
								</c:choose>
								<c:choose>
									<c:when test="${notificacio.enviant}">
										<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIANT"/>
									</c:when>
									<c:otherwise>
										<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.${notificacio.estat}" />
									</c:otherwise>
								</c:choose>
								<c:if test="${notificacio.notificaError}">
									<span class="fa fa-warning text-danger" title="${notificacio.notificaErrorDescripcio}"></span>
								</c:if>
<%--								<c:if test="${notificacio.tipusUsuari == 'APLICACIO' and notificacio.errorLastEvent}">--%>
<%--									<span class="fa fa-exclamation-circle text-primary" title="<spring:message code="notificacio.list.client.error"/>"></span>--%>
<%--								</c:if>--%>
<%--								<c:if test="${notificacio.estat == 'PROCESSADA' and not empty notificacio.estatDate}">--%>
<%--									<br>--%>
<%--									<span class="horaProcessat"><fmt:formatDate value="${notificacio.estatDate}" pattern="dd/MM/yyyy HH:mm:ss" /></span>--%>
<%--									<br>--%>
<%--								</c:if>--%>
								<c:if test="${notificacio.estat == 'FINALITZADA' or notificacio.estat == 'PROCESSADA'}">
									(<c:forEach items="${notificacio.enviaments}" var="enviament" varStatus="status">
										<c:if test="${not empty enviament.notificaEstat}">
											<spring:message code="es.caib.notib.client.domini.EnviamentEstat.${enviament.notificaEstat}"/>
											${!status.last ? ', ' : ''}
										</c:if>
									</c:forEach>)
								</c:if>
							</td>
						</tr>
						</c:if>

						<c:if test="${!notificacio.hasEnviamentsPendents || notificacio.estat == 'FINALITZADA_AMB_ERRORS'}">
							<tr>
								<td colspan="2">
									<a href="<c:url value="/notificacio/${notificacio.id}/justificant"/>" data-toggle="modal" data-height="250px" data-refresh="true" class="btn btn-default btn-sm pull-right">
									<spring:message code="comu.boto.justificant"/>&nbsp;<span class="fa fa-download"></span>
									</a>
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
			<c:if test="${not empty notificacio.grup}">
				<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<strong><spring:message
										code="notificacio.info.seccio.grup" /></strong>
							</h3>
						</div>
						<table class="table table-bordered" style="width: 100%">
							<tbody>
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.grup.codi" /></strong>
									</td>
									<td>
									${notificacio.grup.codi}
									</td>
								</tr>
								<tr>
									<td>
									<strong><spring:message code="notificacio.info.grup.nom" /></strong>
									</td>
									<td>
										${notificacio.grup.nom}
									</td>
								</tr>
							</tbody>
						</table>
				</div>
			</c:if>
			<c:if test="${not empty notificacio.document}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong>
								<c:choose>
									<c:when test="${notificacio.enviamentTipus == 'COMUNICACIO'}">
										<spring:message code="comunicacio.info.seccio.document" />
									</c:when>
									<c:otherwise>
										<spring:message code="notificacio.info.seccio.document" />
									</c:otherwise>
								</c:choose>
							</strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<tr>
								<td width="30%">
									<strong><spring:message	code="notificacio.info.document.arxiu.nom" /></strong>
								</td>
								<td>${notificacio.document.arxiuNom}
									<a id="descarregarDocument" href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar/${notificacio.document.id}"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience" title="<spring:message code="notificacio.info.document.descarregar"/>">
										<spring:message code="notificacio.info.document.descarregar"/>
										<span class="fa fa-download"></span>
									</a>
								</td>
							</tr>
							<c:if test="${not empty notificacio.document2}">
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.document.arxiu.nom" /></strong>
									</td>
									<td>${notificacio.document2.arxiuNom}
										<a id="descarregarDocument" href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar/${notificacio.document2.id}"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience" title="<spring:message code="notificacio.info.document.descarregar"/>">
											<spring:message code="notificacio.info.document.descarregar"/>
											<span class="fa fa-download"></span>
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty notificacio.document3}">
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.document.arxiu.nom" /></strong>
									</td>
									<td>${notificacio.document3.arxiuNom}
										<a id="descarregarDocument" href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar/${notificacio.document3.id}"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience" title="<spring:message code="notificacio.info.document.descarregar"/>">
											<spring:message code="notificacio.info.document.descarregar"/>
											<span class="fa fa-download"></span>
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty notificacio.document4}">
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.document.arxiu.nom" /></strong>
									</td>
									<td>${notificacio.document4.arxiuNom}
										<a id="descarregarDocument" href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar/${notificacio.document4.id}"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience" title="<spring:message code="notificacio.info.document.descarregar"/>">
											<spring:message code="notificacio.info.document.descarregar"/>
											<span class="fa fa-download"></span>
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty notificacio.document5}">
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.document.arxiu.nom" /></strong>
									</td>
									<td>${notificacio.document5.arxiuNom}
										<a id="descarregarDocument" href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar/${notificacio.document5.id}"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience" title="<spring:message code="notificacio.info.document.descarregar"/>">
											<spring:message code="notificacio.info.document.descarregar"/>
											<span class="fa fa-download"></span>
										</a>
									</td>
								</tr>
							</c:if>
							<tr>
								<td><strong><spring:message code="notificacio.info.document.normalitzat" /></strong></td>
								<td>
								<c:choose>
									<c:when test="${notificacio.document.normalitzat}">
									   Si
									</c:when>
									<c:otherwise>
										No
									</c:otherwise>
								</c:choose>
								</td>
							</tr>
							<tr>
								<td><strong><spring:message code="notificacio.info.document.generar.csv" /></strong></td>
								<td>
								<c:choose>
									<c:when test="${notificacio.document.generarCsv}">
									   Si
									</c:when>
									<c:otherwise>
										No
									</c:otherwise>
								</c:choose>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</c:if>
			<c:if test="${not empty notificacio.operadorPostal}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.postal" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if
								test="${not empty notificacio.operadorPostal.organismePagadorCodi}">
								<tr>
									<td width="30%"><strong><spring:message
												code="notificacio.info.pagador.correus.codi.dir3" /></strong></td>
									<td>${notificacio.operadorPostal.organismePagadorCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.contracte" /></strong></td>
									<td>${notificacio.operadorPostal.contracteNum}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.client" /></strong></td>
									<td>${notificacio.operadorPostal.facturacioClientCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.vigencia" /></strong></td>
									<td>
									<fmt:formatDate pattern="dd/MM/yyyy" value="${notificacio.operadorPostal.contracteDataVig}" />
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				</c:if>
				<c:if test="${not empty notificacio.cie}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.cie" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if test="${not empty notificacio.cie.organismePagadorCodi}">
								<tr>
									<td width="30%"><strong><spring:message
												code="notificacio.info.pagador.cie.codi.dir3" /></strong></td>
									<td>${notificacio.cie.organismePagadorCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.cie.vigencia" /></strong></td>
									<td>
									<fmt:formatDate pattern="dd/MM/yyyy" value="${notificacio.cie.contracteDataVig}" />
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				</c:if>

				<c:if test="${not empty notificacio.enviaments}">
				
					<c:forEach items="${notificacio.enviaments}" var="enviament" varStatus="status">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<strong><spring:message code="notificacio.info.seccio.enviaments" /> ${status.index + 1}</strong>
								</h3>
							</div>
							<table class="table teble-striped table-bordered">
									<tbody>
										<tr>
											<th><spring:message code="notificacio.list.enviament.list.titular"/></th>
											<td>
												<c:choose>
													<c:when test="${not empty enviament.titular.nom}">${enviament.titular.nom}</c:when>
													<c:otherwise>${enviament.titular.raoSocial}</c:otherwise>
												</c:choose>
												${enviament.titular.llinatge1}
												${enviament.titular.llinatge2}
												<c:if test="${not empty enviament.titular.nif}">(${enviament.titular.nif})</c:if>
												<c:if test="${enviament.perEmail}"> - <span class="fa fa-envelope-o"></span> ${enviament.titular.email}</c:if>
											</td>
										</tr>
						    			<tr>
							    			<th><spring:message code="notificacio.list.enviament.list.destinataris"/></th>
							    			<td>
							    			<c:choose>
							    			<c:when test="${not empty enviament.destinataris}">
							    			<c:set var="destinataris" value=""/>
							    				<c:forEach items="${enviament.destinataris}" var="destinatari">
							    				<c:set var="destinataris" value="${destinataris} ${destinatari.nom} ${destinatari.llinatge1} ${destinatari.llinatge2} (${destinatari.nif}),"/>
							    				</c:forEach>
							    				<c:set var="destinatarisLength" value="${fn:length(destinataris)}"/>
												<c:set var="destinatarisLengthLess" value="${fn:length(destinataris) - 1}"/>
												${fn:substring(destinataris, 0, destinatarisLengthLess)}
							    			</c:when>
							    			<c:otherwise>
							    				<spring:message code="notificacio.list.enviament.list.sensedestinataris"/>
							    			</c:otherwise>
							    			</c:choose>
							    			</td>
							    		</tr>
										<tr>
											<th><spring:message code="notificacio.list.enviament.list.estat"/></th>
											<td>
												<c:if test="${not empty enviament.notificaEstat}">
													<spring:message code="es.caib.notib.client.domini.EnviamentEstat.${enviament.notificaEstat}"/>
													<c:if test="${enviament.perEmail and enviament.notificaEstat == 'FINALITZADA'}">
														(<c:choose><c:when test="${notificacio.enviamentTipus == 'NOTIFICACIO'}"><spring:message code="notificacio.list.enviament.list.finalitzat.avis.email"/></c:when><c:otherwise><spring:message code="notificacio.list.enviament.list.finalitzat.email"/></c:otherwise></c:choose>)
													</c:if>
												</c:if>
												<c:if test="${enviament.notificacioError}">
													<c:set var="errorTitle">
														<c:choose>
														<c:when test="${not empty enviament.notificacioErrorDescripcio}">
															${enviament.notificacioErrorDescripcio}
														</c:when>
														<c:otherwise>
															Descripció de l'error no registrada
														</c:otherwise>
														</c:choose>
													</c:set>
													<span class="fa fa-warning text-danger" title="${errorTitle}"></span>
												</c:if>
											</td>
										</tr>
						    			<tr>
							    			<th><spring:message code="enviament.info.seccio.notifica.registre"/></th>
							    			<c:choose>
							    				<c:when test="${not empty enviament.registreNumeroFormatat}">
													<td>
													<table class="table table-striped" style="width:100%">
														<tbody>
															<tr>
																<td><strong><spring:message code="enviament.info.seu.registre.num"/></strong></td>
																<td>${enviament.registreNumeroFormatat}</td>
															</tr>
															<tr>
																<td><strong><spring:message code="enviament.info.seu.registre.data"/></strong></td>
																<td><fmt:formatDate value="${enviament.registreData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
															</tr>
															<c:if test="${not empty enviament.registreEstat}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.estat"/></strong></td>
																	<td>${enviament.registreEstat}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.sirRecepcioData}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.data.sir.recepcio"/></strong></td>
																	<td><fmt:formatDate value="${enviament.sirRecepcioData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.sirRegDestiData}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.data.sir.desti"/></strong></td>
																	<td><fmt:formatDate value="${enviament.sirRegDestiData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
																</tr>
															</c:if>
															<c:if test="${(isRolActualAdministradorEntitat || isRolActualAdministradorOrgan) && (not empty notificacio.registreOficinaNom || not empty notificacio.registreLlibreNom)}">
																<c:if test="${not empty notificacio.registreOficinaNom}">
																	<tr>
																		<td width="30%">
																			<strong><spring:message code="notificacio.info.seccio.llocregistre.camp.oficina" /></strong>
																		</td>
																		<td>${notificacio.registreOficinaNom}</td>
																	</tr>
																</c:if>
																<c:if test="${not empty notificacio.registreLlibreNom}">
																	<tr>
																		<td width="30%">
																			<strong><spring:message code="notificacio.info.seccio.llocregistre.camp.llibre" /></strong>
																		</td>
																		<td>${notificacio.registreLlibreNom}</td>
																	</tr>
																</c:if>
															</c:if>
															<%-- Assentament registral o Registre normal (versió anterior) --%>

															<c:if test="${notificacio.enviamentTipus == 'COMUNICACIO' && enviament.titular.interessatTipus == 'ADMINISTRACIO'}">
															<c:if test="${(not empty enviament.registreEstat && (enviament.registreEstat == 'DISTRIBUIT' || enviament.registreEstat == 'VALID' || enviament.registreEstat == 'OFICI_EXTERN'  || enviament.registreEstat == 'OFICI_SIR')) || (empty enviament.registreEstat && not empty enviament.registreNumeroFormatat)}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.justificant"/></strong></td>
																	<td>
																	<a href="<not:modalUrl value="/notificacio/${notificacio.id}/enviament/${enviament.id}/justificantDescarregar"/>" onerror="location.reload();" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience">
																		<spring:message code="enviament.info.accio.descarregar.justificant"/>
																		<span class="fa fa-download"></span>
																	</a>
																	</td>
																<tr>
															</c:if>
															</c:if>

														</tbody>
													</table>
													</td>
							    				</c:when>
							    				<c:otherwise>
							    					<td><spring:message code="notificacio.list.enviament.list.noregistrat"/></td>
							    				</c:otherwise>
							    			</c:choose>
							    		</tr>
						    			<tr>
							    			<th><spring:message code="enviament.info.seccio.notifica.certificacio"/></th>
						    				<c:choose>
						    				<c:when test="${not empty enviament.notificaCertificacioData}">
							    				<td>
							    					<table class="table table-striped" style="width:100%">
														<tbody>
															<tr>
																<td><strong><spring:message code="enviament.info.notifica.certificacio.data"/></strong></td>
																<td><fmt:formatDate value="${enviament.notificaCertificacioData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
															</tr>
															<c:if test="${not empty enviament.notificaCertificacioMime}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.mime"/></strong></td>
																	<td>${enviament.notificaCertificacioMime}</td>
																</tr>
															</c:if>
															<tr>
																<td><strong><spring:message code="enviament.info.notifica.certificacio.origen"/></strong></td>
																<td><spring:message code="enviament.datat.origen.enum.${enviament.notificaCertificacioOrigen}"/> (${enviament.notificaCertificacioOrigen})</td>
															</tr>
															<c:if test="${not empty enviament.notificaCertificacioMetadades}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.metadades"/></strong></td>
																	<td>${enviament.notificaCertificacioMetadades}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioCsv}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.csv"/></strong></td>
																	<td>${enviament.notificaCertificacioCsv}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioTipus}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.tipus"/></strong></td>
																	<td>${enviament.notificaCertificacioTipus}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioArxiuTipus}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.arxiu.tipus"/></strong></td>
																	<td>${enviament.notificaCertificacioArxiuTipus}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioNumSeguiment}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.num.seguiment"/></strong></td>
																	<td>${enviament.notificaCertificacioNumSeguiment}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioArxiuId}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.document"/></strong></td>
																	<td>
																		certificacio_${enviament.notificaIdentificador}.pdf
																		<a href="<not:modalUrl value="/notificacio/${notificacio.id}/enviament/${enviament.id}/certificacioDescarregar"/>" class="btn btn-default btn-sm pull-right btn-certificacio fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.notifica.certificacio.num.descarregar"/>">
																			<spring:message code="enviament.info.notifica.certificacio.num.descarregar"/>
																			<span class="fa fa-download"></span>
																		</a>
																	</td>
																</tr>
															</c:if>
														</tbody>
													</table>
								    			</td>
								    		</c:when>
												<c:when test="${notificacio.comunicacioSir and enviament.registreEstatFinal}">
													<td>
														<a href="<c:url value="/notificacio/${enviament.id}/justificant/sir"/>" data-toggle="modal" data-height="250px" data-refresh="true" class="btn btn-default btn-sm pull-right">
															<spring:message code="enviament.info.accio.descarregar.justificant.sir"/>&nbsp;<span class="fa fa-download"></span>
														</a>
													</td>
												</c:when>
								    		<c:otherwise>
								    			<td>
								    				<spring:message code="notificacio.list.enviament.list.sensecertificacio"/>
								    			</td>
								    		</c:otherwise>
							    			</c:choose>
						    			</tr>
									</tbody>
							</table>
						</div>
					</c:forEach>
				</c:if>
		</div>
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'events'}"> active</c:if>"
			id="events">
			<table id="table-events" data-toggle="datatable"
				data-url="<c:url value="/notificacio/${notificacio.id}/event"/>"
				data-search-enabled="false" data-paging="false" data-info="false"
				data-row-info="true" class="table table-striped table-bordered"
				style="width: 100%">
				<thead>
					<tr>
						<th data-col-name="id" data-visible="false">#</th>
						<th data-col-name="enviamentAssociat" data-visible="false"></th>
						<th data-col-name="errorDescripcio" data-visible="false"></th>
<%--						<th data-col-name="callbackEstat" data-visible="false"></th>--%>
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
<%--							{{if callbackEstat == 'PENDENT' && ~eval('notificacioApp') == 'true'}}<span style="padding-right:4px; color:#666;" class="fa fa-clock-o pull-right" title="<spring:message code="notificacio.event.list.info.pendent"/>"></span>{{/if}}--%>
						</script></th>
						<c:if test="${notificacio.tipusUsuari == 'APLICACIO'}">
							<th data-col-name="callbackEstat" data-visible="false"></th>
							<th data-col-name="callbackIntents" data-visible="false"><spring:message
									code="notificacio.event.list.columna.callbackIntents" /></th>
							<th data-col-name="callbackError" data-template="#cellCallback" data-orderable="false">
								<spring:message code="notificacio.event.list.columna.callback" />
								<script id="cellCallback" type="text/x-jsrender">
								{{if callbackEstat == 'PENDENT'}}
									<span style="padding-right:4px; color:#666;" class="fa fa-clock-o pull-left" title="<spring:message code="notificacio.event.list.info.callback.pendent"/>"></span>
									{{if callbackIntents > 0}}
										<span style="padding-right:4px;" class="fa fa-warning text-warning pull-left" title="{{:callbackError}}"></span>
									{{/if}}
								{{/if}}
								{{if callbackEstat == 'NOTIFICAT'}}
									<span style="padding-right:4px;" class="fa fa-check text-success pull-left" title="<spring:message code="notificacio.event.list.info.callback.notificat"/>"></span>
								{{/if}}
								{{if callbackEstat == 'PROCESSAT'}}
								<span style="padding-right:4px;" class="fa fa-info text-info pull-left" title="<spring:message code="notificacio.event.list.info.callback.processat"/>"></span>
								{{/if}}
								{{if callbackEstat == 'ERROR'}}
									<span style="padding-right:4px;" class="fa fa-warning text-danger pull-left" title="{{:callbackError}}"></span>
								{{/if}}
								</script>
							</th>
						</c:if>
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
		<div id="accions" role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'accions'}"> active</c:if>">
			<c:set var="algunaAccioDisponible" value="${false}" />
			<ul class="list-group">

				<c:if test="${notificacio.eventsCallbackPendent}">
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.enviar.callback.boto" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a id="enviar-callback-btn" href="<not:modalUrl value="/notificacio/${notificacio.id}/enviar/callback"/>"
										class="btn btn-default btn-sm"> <span class="fa fa-send"></span>
									<spring:message code="notificacio.info.accio.enviar.callback.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>

				<c:if test="${notificacio.estat == 'PENDENT'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.registrar" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									id="registrar-btn" href="<not:modalUrl value="/notificacio/${notificacio.id}/registrar"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-send"></span>
									<spring:message code="notificacio.info.accio.registrar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<c:if test="${notificacio.estat == 'REGISTRADA'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.enviar" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									id="enviar-btn" href="<not:modalUrl value="/notificacio/${notificacio.id}/enviar"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-send"></span>
									<spring:message code="notificacio.info.accio.enviar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<!-- Acció reprendre consulta d'estat  - Estat == ENVIADA && notificaErrorTipus == ERROR_REINTENTS_CONSULTA -->
				<c:if test="${notificacio.estat == 'ENVIADA' && notificacio.notificaErrorTipus == 'ERROR_REINTENTS_CONSULTA'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reactivar.consulta" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a href="<not:modalUrl value="/notificacio/${notificacio.id}/reactivarconsulta"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-play"></span>
									<spring:message code="notificacio.info.accio.reactivar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<!-- Acció reprendre consulta d'estat SIR  - Estat == ENVIADA && notificaErrorTipus == ERROR_REINTENTS_SIR -->
				<c:if test="${notificacio.estat == 'ENVIADA' && notificacio.notificaErrorTipus == 'ERROR_REINTENTS_SIR'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reactivar.consulta.sir" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a href="<not:modalUrl value="/notificacio/${notificacio.id}/reactivarsir"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-play"></span>
									<spring:message code="notificacio.info.accio.reactivar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<c:if test="${notificacio.tipusUsuari == 'APLICACIO' && notificacio.errorLastCallback}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reintent" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									href="<not:modalUrl value="/notificacio/${notificacio.id}/refrescarEstatClient"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-undo"></span>
									<spring:message code="notificacio.info.accio.reintent.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<c:if test="${(notificacio.estat == 'ENVIADA_AMB_ERRORS' || notificacio.estat == 'FINALITZADA_AMB_ERRORS') && !notificacio.justificantCreat}">
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reactiva.errors" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
										href="<not:modalUrl value="/notificacio/${notificacio.id}/reactivarErrors"/>"
										class="btn btn-default btn-sm"> <span class="fa fa-undo"></span>
									<spring:message code="notificacio.info.accio.reactiva.errors.boto" />
								</a>
							</div>
						</div>
					</li>
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reintent.errors" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
										href="<not:modalUrl value="/notificacio/${notificacio.id}/reenviarErrors"/>"
										class="btn btn-default btn-sm"> <span class="fa fa-undo"></span>
									<spring:message code="notificacio.info.accio.reintent.errors.boto" />
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
		<div role="tabpanel"
			 class="tab-pane<c:if test="${pipellaActiva == 'events'}"> active</c:if>"
			 id="historic">
			<table id="table-historic"
				   data-toggle="datatable"
				   data-url="<c:url value="/notificacio/${notificacio.id}/historic"/>"
				   data-search-enabled="false"
				   data-paging="false"
				   data-row-info="false"
				   class="table table-striped table-bordered"
				   style="width: 100%">
				<thead>
				<tr>

					<th data-col-name="tipusOperacio" data-orderable="false"><spring:message code="notificacio.historic.list.columna.tipusOperacio" /></th>
					<th data-col-name="joinPoint" data-orderable="false"><spring:message code="notificacio.historic.list.columna.joinPoint" /></th>
					<th data-col-name="createdBy" data-orderable="false"><spring:message code="notificacio.historic.list.columna.createdBy" /></th>
					<th data-col-name="createdDate" data-orderable="false" data-converter="datetime"><spring:message code="notificacio.historic.list.columna.createdDate" /></th>
					<th data-col-name="comunicacioTipus" data-orderable="false"><spring:message code="notificacio.historic.list.columna.comunicacioTipus" /></th>
					<th data-col-name="tipusUsuari" data-orderable="false"><spring:message code="notificacio.historic.list.columna.tipusUsuari" /></th>
					<th data-col-name="usuari" data-orderable="false"><spring:message code="notificacio.historic.list.columna.usuari" /></th>
					<th data-col-name="emisor" data-orderable="false"><spring:message code="notificacio.historic.list.columna.emisor" /></th>
					<th data-col-name="tipus" data-orderable="false"><spring:message code="notificacio.historic.list.columna.tipus" /></th>
					<th data-col-name="organ" data-orderable="false"><spring:message code="notificacio.historic.list.columna.organ" /></th>
					<th data-col-name="procediment" data-orderable="false"><spring:message code="notificacio.historic.list.columna.procediment" /></th>
					<th data-col-name="grup" data-orderable="false"><spring:message code="notificacio.historic.list.columna.grup" /></th>
					<th data-col-name="concepte" data-orderable="false"><spring:message code="notificacio.historic.list.columna.concepte" /></th>
					<th data-col-name="descripcio" data-orderable="false"><spring:message code="notificacio.historic.list.columna.descripcio" /></th>
					<th data-col-name="numExpedient" data-orderable="false"><spring:message code="notificacio.historic.list.columna.numExpedient" /></th>
					<th data-col-name="enviamentDataProgramada" data-orderable="false" data-converter="datetime"><spring:message code="notificacio.historic.list.columna.enviamentDataProgramada" /></th>
					<th data-col-name="retard" data-orderable="false"><spring:message code="notificacio.historic.list.columna.retard" /></th>
					<th data-col-name="caducitat" data-orderable="false" data-converter="datetime"><spring:message code="notificacio.historic.list.columna.caducitat" /></th>
					<th data-col-name="documentId" data-orderable="false"><spring:message code="notificacio.historic.list.columna.documentId" /></th>
					<th data-col-name="estat" data-orderable="false"><spring:message code="notificacio.historic.list.columna.estat" /></th>
					<th data-col-name="estatDate" data-orderable="false" data-converter="datetime"><spring:message code="notificacio.historic.list.columna.estatDate" /></th>
					<th data-col-name="motiu" data-orderable="false"><spring:message code="notificacio.historic.list.columna.motiu" /></th>
					<th data-col-name="pagadorPostalId" data-orderable="false"><spring:message code="notificacio.historic.list.columna.pagadorPostalId" /></th>
					<th data-col-name="pagadorCieId" data-orderable="false"><spring:message code="notificacio.historic.list.columna.pagadorCieId" /></th>
					<th data-col-name="registreEnviamentIntent" data-orderable="false"><spring:message code="notificacio.historic.list.columna.registreEnviamentIntent" /></th>
					<th data-col-name="registreNumero" data-orderable="false"><spring:message code="notificacio.historic.list.columna.registreNumero" /></th>
					<th data-col-name="registreNumeroFormatat" data-orderable="false"><spring:message code="notificacio.historic.list.columna.registreNumeroFormatat" /></th>
					<th data-col-name="registreData" data-orderable="false" data-converter="datetime"><spring:message code="notificacio.historic.list.columna.registreData" /></th>
					<th data-col-name="notificaEnviamentData" data-orderable="false" data-converter="datetime"><spring:message code="notificacio.historic.list.columna.notificaEnviamentData" /></th>
					<th data-col-name="notificaEnviamentIntent" data-orderable="false"><spring:message code="notificacio.historic.list.columna.notificaEnviamentIntent" /></th>
					<th data-col-name="notificaErrorTipus" data-orderable="false"><spring:message code="notificacio.historic.list.columna.notificaErrorTipus" /></th>
					<th data-col-name="errorLastCallback" data-orderable="false"><spring:message code="notificacio.historic.list.columna.errorLastCallback" /></th>
					<th data-col-name="errorEventId" data-orderable="false"><spring:message code="notificacio.historic.list.columna.errorEventId" /></th>

				</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacio"/>" class="btn btn-default"
			data-modal-cancel="true"><spring:message code="comu.boto.tancar" /></a>
	</div>
</body>
</html>

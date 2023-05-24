<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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
	<title><spring:message code="enviament.info.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
	<not:modalHead/>
<script type="text/javascript">

$(function() {
    $(document).on("click", "a.fileDownloadSimpleRichExperience", function() {
        $.fileDownload($(this).attr('href'), {
            preparingMessageHtml: "Estam preparant la descàrrega, per favor esperi...",
            failMessageHtml: "<strong style='color:red'>Ho sentim.<br/>S'ha produït un error intentant descarregar el document.</strong>"//,
        });
        return false;
    });
});

var eventTipus = [];
<c:forEach var="tipus" items="${eventTipus}">
eventTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
$(document).ready(function() {
	$('#events').on('rowinfo.dataTable', function(e, td, rowData) {
		let errorText = rowData['errorDescripcio'];
		if (rowData['fiReintents']) {
			errorText += "\n\nEsgotats els reintents.";
		}
		$(td).empty();
    	$(td).append('<textarea style="width:100%" rows="10">' + errorText + '</textarea>');
	});
	$('#events').on('draw.dt', function(e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		api.rows().every(function (rowIdx, tableLoop, rowLoop) {
			var data = this.data();
			if (!data.errorDescripcio) {
				$('td:last-child', this.node()).empty();
			}
		});
	});

	$("#remesa-link").click(e => {
		e.preventDefault();
		$.ajax({
			url: '<c:url value="/notificacio/filtrades"/>' + '/${enviament.notificacio.referencia}',
			// success: () => top.location = "/notib/",
			success: () => window.open('<c:url value="/"/>', "_blank"),
			error: err => console.error(err)
		});
	});

	$("#refrescarEstat").click(e => {
		$("#refrescarEstat").prop("disabled", true);
		e.preventDefault();
		$.ajax({
			url: '<c:url value="/notificacio/${notificacioId}/enviament/${enviamentId}/refrescarEstatNotifica"/>',
			success: data => {
				$("#refrescarEstat").prop("disabled", false);
				let classe = data.ok ? "alert-success" : "alert-danger";
				let div = '<div class="alert ' + classe +'"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true">' +
						'<span class="fa fa-times"></span></button>' + data.msg + '</div>';
				$("#contingut-missatges").append(div);
				window.location.href = '<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}?pipellaActiva=estatNotifica"/>';
			},
			error: err => console.error(err)
		});
	});

	$("#refrescarEstatSir").click(e => {
		$("#refrescarEstatSir").prop("disabled", true);
		e.preventDefault();
		$.ajax({
			url: '<c:url value="/notificacio/${notificacioId}/enviament/${enviamentId}/refrescarEstatSir"/>',
			success: data => {
				$("#refrescarEstatSir").prop("disabled", false);
				let classe = data.ok ? "alert-success" : "alert-danger";
				let div = '<div class="alert ' + classe +'"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true">' +
						'<span class="fa fa-times"></span></button>' + data.msg + '</div>';
				$("#contingut-missatges").append(div);
				window.location.href = '<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}?pipellaActiva=estatNotifica"/>';
			},
			error: err => console.error(err)
		});
	});
});
</script>
</head>
<body>
<div id="contingut-missatges"></div>
<%-- TODO EVENTS: mostrar missatges d'events --%>
	<c:if test="${enviament.notificacio.notificaError}">
		<div class="alert alert-danger well-sm">
			<span class="fa fa-warning text-danger"></span>
			<c:choose>
				<c:when test="${enviament.notificacio.notificaErrorTipus == 'ERROR_REGISTRE'}">
					<spring:message code="enviament.info.error.registre"/>
				</c:when>
				<c:otherwise>
					<spring:message code="enviament.info.error.titol"/>
				</c:otherwise>
			</c:choose>
			<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapseError" aria-expanded="false" aria-controls="collapseError">
				<span class="fa fa-bars"></span>
			</button>
			<div id="collapseError" class="collapse">
				<br/>
				<textarea rows="10" style="width:100%">${fn:escapeXml(enviament.notificacio.notificaErrorDescripcio)}</textarea>
			</div>
		</div>
	</c:if>

	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation"<c:if test="${pipellaActiva == 'dades'}"> class="active"</c:if>>
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.dades"/>
			</a>
		</li>
		<li role="presentation"<c:if test="${pipellaActiva == 'estatNotifica'}"> class="active"</c:if>>
			<a href="#estatNotifica" aria-controls="estatNotifica" role="tab" data-toggle="tab">
				<c:choose>
					<c:when test="${enviament.perEmail}">
						<spring:message code="enviament.info.tab.estat.email"/>
					</c:when>
					<c:when test="${notificacio.enviamentTipus == 'COMUNICACIO' && enviament.titular.interessatTipus == 'ADMINISTRACIO'}">
						<spring:message code="enviament.info.tab.estat.sir"/>
					</c:when>
					<c:otherwise>
						<spring:message code="enviament.info.tab.estat.notifica"/>
					</c:otherwise>
				</c:choose>				
			</a>
		</li>
		<li role="presentation"<c:if test="${pipellaActiva == 'estatSeu'}"> class="active"</c:if>>
			<a href="#estatRegistre" aria-controls="estatRegistre" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.estat.registre"/>
			</a>
		</li>
		<li role="presentation"<c:if test="${pipellaActiva == 'events'}"> class="active"</c:if>>
			<a href="#events" aria-controls="events" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.events"/>
			</a>
		</li>
		<c:if test="${isRolActualAdministradorEntitat || isRolActualAdministradorOrgan || isRolActualAdministrador}">
		<li role="presentation"<c:if test="${pipellaActiva == 'historic'}"> class="active"</c:if>>
			<a href="#historic" aria-controls="historic" role="tab" data-toggle="tab">
				<spring:message code="notificacio.info.tab.historic"/>
			</a>
		</li>
		</c:if>
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
					<c:choose>
						<c:when test="${not empty enviament.notificacio.id}">
							<tr>
								<td width="1%"><strong><spring:message code="enviament.info.dada.identificadors.identificador"/></strong></td>
								<td>
									${enviament.notificacio.referencia}
									<a id="remesa-link" class="btn btn-default btn-sm pull-right" title="<spring:message code="enviament.info.notifica.certificacio.num.descarregar"/>">
											<span class="fa fa-external-link"></span>
									</a>
								</td>
							</tr>
							<tr>
								<td width="1%"><strong><spring:message code="enviament.info.dada.identificadors.referencia"/></strong></td>
								<td>${enviament.notificaReferencia}</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr><td colspan="2" width="1%"><strong>NOTIB</strong></td></tr>
							<tr><td colspan="2">${enviament.notificaReferencia}</td></tr>
						</c:otherwise>
					</c:choose>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.nif"/></strong></td>
						<td colspan="4">${enviament.entregaDeh.nif}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.procediment"/></strong></td>
						<td colspan="4">${enviament.entregaDeh.procedimentCodi}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.obligada"/></strong></td>
						<td colspan="4">
							<c:choose>
								<c:when test="${enviament.entregaDeh.obligat}"><spring:message code="comu.si"/></c:when>
								<c:otherwise><spring:message code="comu.no"/></c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.servei.tipus"/></strong></td>
						<c:choose><c:when test="${not empty enviament.serveiTipus}"><c:set var="envTip" value="${enviament.serveiTipus}"/></c:when><c:otherwise><c:set var="envTip" value="NORMAL"/></c:otherwise></c:choose>
						<td colspan="4"><spring:message code="es.caib.notib.logic.intf.dto.NotificaServeiTipusEnumDto.${envTip}"/></td>
					</tr>
					<tr>
						<td width="30%"><strong><spring:message code="enviament.info.dada.estat"/></strong></td>
						<td colspan="4">
							<spring:message code="es.caib.notib.client.domini.EnviamentEstat.${enviament.notificaEstat}"/>
							<c:if test="${enviament.notificaError and enviament.notificaEstat != 'FINALITZADA' and enviament.notificaEstat == 'PROCESSADA'}">
								<span class="fa fa-warning text-danger" title="<c:out value='${enviament.notificaErrorDescripcio}' escapeXml='true'/>"></span>
							</c:if>
							<c:if test="${enviament.fiReintents}">
								<span class="fa fa-warning text-warning" title="<c:out value='${enviament.fiReintentsDesc}' escapeXml='true'/>"></span>
							</c:if>
							<c:if test="${enviament.callbackFiReintents}">
								<span class="fa fa-warning text-info" title="<c:out value='${enviament.callbackFiReintentsDesc}' escapeXml='true'/>"></span>
							</c:if>
							<c:if test="${enviament.callbackFiReintents}">
								<span class="fa fa-warning text-info" title="<c:out value='${enviament.callbackFiReintentsDesc}' escapeXml='true'/>"></span>
							</c:if>
							<c:if test="${not empty enviament.notificacioMovilErrorDesc}">
								<span style="color:#8a6d3b;  cursor:pointer;" class="fa fa-mobile fa-lg" title="<c:out value='${enviament.notificacioMovilErrorDesc}' escapeXml='true'/>"></span>
							</c:if>
						</td>
					</tr>
				</tbody>
				</table>
			</div>
			<div class="row">
				<c:set var="titularColSize" value="12"/>
				<c:if test="${not empty enviament.titular.nif}"><c:set var="titularColSize" value="12"/></c:if>
				<div class="col-sm-${titularColSize}">
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
								<td>${enviament.titular.nif}</td>
							</tr>
							<c:if test="${not empty enviament.titular.nom}">
								<tr>
									<td width="30%"><strong><spring:message code="enviament.info.titular.nom"/></strong></td>
									<td>${enviament.titular.nom}</td>
								</tr>
							</c:if>
							<c:if test="${not empty enviament.titular.llinatge1}">
								<tr>
									<td><strong><spring:message code="enviament.info.titular.llinatges"/></strong></td>
									<td>${enviament.titular.llinatge1} 
									<c:if test="${not empty titular.llinatge2}">
										${titular.llinatge2}
									</c:if>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty enviament.titular.telefon}">
								<tr>
									<td><strong><spring:message code="enviament.info.titular.telefon"/></strong></td>
									<td>${enviament.titular.telefon}</td>
								</tr>
							</c:if>
							<c:if test="${not empty enviament.titular.email}">
								<tr>
									<td><strong><spring:message code="enviament.info.titular.email"/></strong></td>
									<td>${enviament.titular.email}</td>
								</tr>
							</c:if>
						</tbody>
						</table>
					</div>
				</div>
				<c:forEach var="destinatari" items="${enviament.destinataris}" varStatus="loop">
					<c:if test="${not empty destinatari.nif}">
						<div class="col-sm-12">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h3 class="panel-title">
										<strong><spring:message code="enviament.info.seccio.destinatari"/> nº ${loop.index+1}</strong>
									</h3>
				 				</div>
								<table class="table table-bordered" style="width:100%">
								<tbody>
									<tr>
										<td><strong><spring:message code="enviament.info.destinatari.nif"/></strong></td>
										<td>${destinatari.nif}</td>
									</tr>
									<c:if test="${not empty destinatari.nom}">
										<tr>
											<td width="30%"><strong><spring:message code="enviament.info.destinatari.nom"/></strong></td>
											<td>${destinatari.nom}</td>
										</tr>
									</c:if>
									<c:if test="${not empty destinatari.llinatge1}">
										<tr>
											<td><strong><spring:message code="enviament.info.destinatari.llinatges"/></strong></td>
											<td>${destinatari.llinatge1} 
											<c:if test="${not empty destinatari.llinatge2}">
												${destinatari.llinatge2}
											</c:if>
											</td>
										</tr>
									</c:if>
									<c:if test="${not empty destinatari.telefon}">
										<tr>
											<td><strong><spring:message code="enviament.info.destinatari.telefon"/></strong></td>
											<td>${destinatari.telefon}</td>
										</tr>
									</c:if>
									<c:if test="${not empty destinatari.email}">
										<tr>
											<td><strong><spring:message code="enviament.info.destinatari.email"/></strong></td>
											<td>${destinatari.email}</td>
										</tr>
									</c:if>
								</tbody>
								</table>
							</div>
						</div>
					</c:if>
				</c:forEach>
			</div>
			<c:if test="${not empty enviament.entregaPostal.domiciliConcretTipus}">
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
							<td>${enviament.entregaPostal.domiciliConcretTipus}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.tipus.concret"/></strong></td>
							<td>${enviament.entregaPostal.domiciliConcretTipus}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.via"/></strong></td>
							<td>${enviament.entregaPostal.viaTipus} ${enviament.entregaPostal.viaNom}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.numeracio"/></strong></td>
							<td>
<%--								${enviament.entregaPostal.numeracioTipus}--%>
								${enviament.entregaPostal.numeroCasa}
								${enviament.entregaPostal.puntKm}
								${enviament.entregaPostal.apartatCorreus}
							</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.bloc"/></strong></td>
							<td>${enviament.entregaPostal.bloc}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.portal"/></strong></td>
							<td>${enviament.entregaPostal.portal}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.escala"/></strong></td>
							<td>${enviament.entregaPostal.escala}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.planta"/></strong></td>
							<td>${enviament.entregaPostal.planta}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.porta"/></strong></td>
							<td>${enviament.entregaPostal.porta}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.complement"/></strong></td>
							<td>${enviament.entregaPostal.complement}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.poblacio"/></strong></td>
							<td>${enviament.entregaPostal.poblacio}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.codi.postal"/></strong></td>
							<td>${enviament.entregaPostal.codiPostal}</td>
						</tr>
<%--						<tr>--%>
<%--							<td><strong><spring:message code="enviament.info.domicili.municipi"/></strong></td>--%>
<%--							<td>${enviament.entregaPostal.municipiNom} (${enviament.entregaPostal.municipiCodi})</td>--%>
<%--						</tr>--%>
<%--						<tr>--%>
<%--							<td><strong><spring:message code="enviament.info.domicili.provincia"/></strong></td>--%>
<%--							<td>${enviament.entregaPostal.provinciaNom} (${enviament.entregaPostal.provincia})</td>--%>
<%--						</tr>--%>
<%--						<tr>--%>
<%--							<td><strong><spring:message code="enviament.info.domicili.pais"/></strong></td>--%>
<%--							<td>${enviament.entregaPostal.paisNom} (${enviament.entregaPostal.paisCodi})</td>--%>
<%--						</tr>--%>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.linea1"/></strong></td>
							<td>${enviament.entregaPostal.linea1}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.linea2"/></strong></td>
							<td>${enviament.entregaPostal.linea2}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.domicili.cie"/></strong></td>
							<td>${enviament.entregaPostal.cie}</td>
						</tr>
					</tbody>
					</table>
				</div>
			</c:if>
		</div>
		<div role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'estatNotifica'}"> active</c:if>" id="estatNotifica">
			<c:if test="${enviament.notificacio.estat == 'PENDENT'}">
				<div class="alert alert-warning well-sm" role="alert" style="margin-top: 1em">
					<c:choose><c:when test="${enviament.perEmail}"><spring:message code="enviament.info.email.no.enviat"/></c:when><c:otherwise><spring:message code="enviament.info.notifica.no.enviada"/></c:otherwise></c:choose>
				</div>
			</c:if>
			<c:if test="${enviament.notificacio.estat != 'PENDENT'}">
				<c:choose>
					<c:when test="${(notificacio.enviamentTipus == 'COMUNICACIO' && enviament.titular.interessatTipus == 'ADMINISTRACIO')}">
						<p class="text-right" style="margin-top: 1em">
							<button id="refrescarEstatSir" class="btn btn-default">
								<span class="fa fa-refresh"></span>
								<spring:message code="enviament.info.accio.refrescar.estat"/>
							</button>
						</p>
					</c:when>
					<c:when test="${enviament.perEmail}">
						<br/>
					</c:when>
					<c:otherwise>
						<p class="text-right" style="margin-top: 1em">
<%--							<a id="refrescarEstat" href="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/refrescarEstatNotifica"/>" class="btn btn-default">--%>
							<button id="refrescarEstat" class="btn btn-default">
								<span class="fa fa-refresh"></span>
								<spring:message code="enviament.info.accio.refrescar.estat"/>
							</button>
						</p>
					</c:otherwise>
				</c:choose>
				<div class="row">
					<c:set var="datatColSize" value="12"/>
					<c:if test="${not empty enviament.notificaCertificacioData}"><c:set var="datatColSize" value="5"/></c:if>
					<div class="col-sm-${datatColSize}">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<strong><spring:message code="enviament.info.seccio.notifica.datat"/></strong>
								</h3>
			 				</div>
							<table class="table table-bordered" style="width:100%">
							<tbody>
								<tr>
									<td width="30%"><strong><spring:message code="enviament.info.notifica.estat"/></strong></td>
									<td><spring:message code="es.caib.notib.client.domini.EnviamentEstat.${enviament.notificaEstat}"/></td>
								</tr>
								<c:if test="${not empty enviament.sirRecepcioData}">
									<tr>
										<td><strong><spring:message code="enviament.info.sir.recepcio.data"/></strong></td>
										<td><fmt:formatDate value="${enviament.sirRecepcioData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.sirRegDestiData}">
									<tr>
										<td><strong><spring:message code="enviament.info.sir.registre.desti.data"/></strong></td>
										<td><fmt:formatDate value="${enviament.sirRegDestiData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaEstatData}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.estat.data"/></strong></td>
										<td><fmt:formatDate value="${enviament.notificaEstatData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaIdentificador}">
									<tr>
										<td><strong><spring:message code="notificacio.list.filtre.camp.identificador"/></strong></td>
										<td>${enviament.notificaIdentificador}</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaDatatErrorDescripcio}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.estat.descripcio"/></strong></td>
										<td>${enviament.notificaDatatErrorDescripcio}</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaDatatOrigen}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.datat.origen"/></strong></td>
										<td><spring:message code="enviament.datat.origen.enum.${enviament.notificaDatatOrigen}"/> (${enviament.notificaDatatOrigen})</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaDatatReceptorNif}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.datat.receptor.nif"/></strong></td>
										<td>${enviament.notificaDatatReceptorNif}</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaDatatReceptorNom}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.datat.receptor.nom"/></strong></td>
										<td>${enviament.notificaDatatReceptorNom}</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaDatatNumSeguiment}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.datat.num.seguiment"/></strong></td>
										<td>${enviament.notificaDatatNumSeguiment}</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.notificaDatatErrorDescripcio}">
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.datat.error.desc"/></strong></td>
										<td>${enviament.notificaDatatErrorDescripcio}</td>
									</tr>
								</c:if>
							</tbody>
							</table>
						</div>
					</div>
					<c:if test="${not empty enviament.notificaCertificacioData}">
						<div class="col-sm-7">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h3 class="panel-title">
										<strong><spring:message code="enviament.info.seccio.notifica.certificacio"/></strong>
									</h3>
				 				</div>
								<table class="table table-bordered" style="width:100%">
								<tbody>
									<tr>
										<td width="30%"><strong><spring:message code="enviament.info.notifica.certificacio.data"/></strong></td>
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
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.certificacio.document"/></strong></td>
										<td>
										<div></div>
											${enviament.notificaCertificacioArxiuNom}
											<a href="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/certificacioDescarregar"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.notifica.certificacio.num.descarregar"/>"><span class="fa fa-download"></span></a>
										</td>
									</tr>
								</tbody>
								</table>
							</div>
						</div>
					</c:if>
				</div>
			</c:if>
		</div>
		<div role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'estatRegistre'}"> active</c:if>" id="estatRegistre">
			<c:if test="${empty enviament.registreNumeroFormatat}">
				<div class="alert alert-warning well-sm" role="alert" style="margin-top: 1em">
					<spring:message code="enviament.info.estat.registre.no.enviada"/>
				</div>
				<c:if test="${enviament.notificacio.estat != 'PENDENT'}"><%-- TODO: Els dos botons que hi ha a continuació es poden eliminar --%>
					<c:if test="${notificacio.enviamentTipus == 'COMUNICACIO'}">
						<p class="well well-sm text-right" style="margin-top: 1em">
							<a href="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/comunicacioSeu"/>" class="btn btn-default">
								<span class="fa fa-check-square-o"></span>
								<spring:message code="enviament.info.accio.seu.informar.obertura"/>
							</a>
						</p>
					</c:if>
					<c:if test="${notificacio.enviamentTipus == 'NOTIFICACIO'}">
						<form action="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/certificacioSeu"/>" class="well well-sm text-right" style="margin-top: 1em" method="post" enctype="multipart/form-data">
							<input type="file" name="certificat" class="pull-left"/>
							<button type="submit" class="btn btn-default">
								<span class="fa fa-send-o"></span>
								<spring:message code="enviament.info.accio.seu.enviar.certificacio"/>
							</button>
						</form>
					</c:if>
				</c:if>
			</c:if>
			<c:if test="${not empty enviament.registreNumeroFormatat}">
				<br>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="enviament.info.seccio.registre"/></strong>
						</h3>
	 				</div>
					<table class="table table-bordered" style="width:100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="enviament.info.seu.registre.num"/></strong></td>
							<td>${enviament.registreNumeroFormatat}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="enviament.info.seu.registre.data"/></strong></td>
							<td><fmt:formatDate value="${enviament.registreData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
						<c:if test="${not empty enviament.registreEstat}">
							<tr>
								<td width="30%"><strong><spring:message code="enviament.info.seu.registre.estat"/></strong></td>
								<td>${enviament.registreEstat}</td>
							</tr>
						</c:if>
					</tbody>
					</table>
				</div>
				<c:if test="${enviament.notificacio.estat == 'REGISTRADA'}">
					<c:if test="${enviament.registreEstat == 'DISTRIBUIT' || enviament.registreEstat == 'OFICI_EXTERN'  || enviament.registreEstat == 'OFICI_SIR' }">
						<a href="<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/justificantDescarregar"/>" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience">
							<span class="fa fa-download"></span>
							<spring:message code="enviament.info.accio.descarregar.justificant"/>
						</a>
<%-- 						<button onclick='descarrega("<not:modalUrl value="/notificacio/${notificacioId}/enviament/${enviamentId}/justificantDescarregar"/>")' class="btn btn-default btn-sm pull-right"> --%>
<!-- 							<span class="fa fa-download"></span> -->
<%-- 							<spring:message code="enviament.info.accio.descarregar.justificant"/> --%>
<!-- 						</button> -->
					</c:if>
				</c:if>
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
				data-row-info="true"
				class="table table-striped table-bordered"
				style="width:100%">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false">#</th>
					<th data-col-name="errorDescripcio" data-visible="false"></th>
					<th data-col-name="fiReintents" data-visible="false"></th>
					<th data-col-name="data" data-converter="datetime" data-orderable="false"><spring:message code="enviament.event.list.columna.data"/></th>
					<th data-col-name="tipus" data-template="#cellTipus" data-orderable="false">
						<spring:message code="enviament.event.list.columna.tipus"/>
						<script id="cellTipus" type="text/x-jsrender">
							{{:~eval('eventTipus["' + tipus + '"]')}}
						</script>
					</th>
					<th data-col-name="error" data-template="#cellResultat" data-orderable="false">
						<spring:message code="notificacio.event.list.columna.estat"/>
						<script id="cellResultat" type="text/x-jsrender">
							{{if error}}
								<span class="fa fa-warning text-danger" title="<spring:message code="enviament.event.list.processat.error"/>"></span>
							{{else}}
								<span class="fa fa-check text-success" title="<spring:message code="enviament.event.list.processat.ok"/>"></span>
							{{/if}}
							{{if fiReintents}}
									<span class="fa fa-warning text-info" title="<spring:message code="enviament.event.list.fi.reintents"/>"></span>
							{{/if}}
						</script>
					</th>
					<th data-col-name="intents" data-orderable="false"><spring:message code="notificacio.event.list.columna.intents"/></th>
				</tr>
			</thead>
			</table>
			<c:if test="${enviament.callbackPendent}">
				<br>
				<div class="alert alert-info well-sm"><span class="fa fa-clock-o"></span> <span><spring:message code="callback.pendent.enviament"/> ${enviament.callbackData}</span></div>
			</c:if>
		</div>
		<div role="tabpanel" class="tab-pane<c:if test="${pipellaActiva == 'historic'}"> active</c:if>" id="historic">
			<table id="historic"
				   data-toggle="datatable"
				   data-url="<c:url value="/notificacio/${notificacioId}/enviament/${enviamentId}/historic"/>"
				   data-search-enabled="false"
				   data-paging="false"
				   data-info="false"
				   data-row-info="false"
				   class="table table-striped table-bordered"
				   style="width: 100%">
				<thead>
				<tr>

					<th data-col-name="tipusOperacio" data-orderable="false"><spring:message code="enviament.historic.list.columna.tipusOperacio"/></th>
					<th data-col-name="joinPoint" data-orderable="false"><spring:message code="enviament.historic.list.columna.joinPoint"/></th>
					<th data-col-name="createdBy" data-orderable="false"><spring:message code="enviament.historic.list.columna.createdBy"/></th>
					<th data-col-name="createdDate" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.createdDate"/></th>
					<th data-col-name="titularId" data-orderable="false"><spring:message code="enviament.historic.list.columna.titularId"/></th>
					<th data-col-name="destinataris" data-orderable="false"><spring:message code="enviament.historic.list.columna.destinataris"/></th>
					<th data-col-name="domiciliTipus" data-orderable="false"><spring:message code="enviament.historic.list.columna.domiciliTipus"/></th>
					<th data-col-name="domicili" data-orderable="false"><spring:message code="enviament.historic.list.columna.domicili"/></th>
					<th data-col-name="serveiTipus" data-orderable="false"><spring:message code="enviament.historic.list.columna.serveiTipus"/></th>
					<th data-col-name="cie" data-orderable="false"><spring:message code="enviament.historic.list.columna.cie"/></th>
					<th data-col-name="formatSobre" data-orderable="false"><spring:message code="enviament.historic.list.columna.formatSobre"/></th>
					<th data-col-name="formatFulla" data-orderable="false"><spring:message code="enviament.historic.list.columna.formatFulla"/></th>
					<th data-col-name="dehObligat" data-orderable="false"><spring:message code="enviament.historic.list.columna.dehObligat"/></th>
					<th data-col-name="dehNif" data-orderable="false"><spring:message code="enviament.historic.list.columna.dehNif"/></th>
					<th data-col-name="notificaReferencia" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaReferencia"/></th>
					<th data-col-name="notificaIdentificador" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaIdentificador"/></th>
					<th data-col-name="notificaDataCreacio" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.notificaDataCreacio"/></th>
					<th data-col-name="notificaDataDisposicio" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.notificaDataDisposicio"/></th>
					<th data-col-name="notificaDataCaducitat" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.notificaDataCaducitat"/></th>
					<th data-col-name="notificaEmisorDir3" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaEmisorDir3"/></th>
					<th data-col-name="notificaArrelDir3" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaArrelDir3"/></th>
					<th data-col-name="notificaEstat" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaEstat"/></th>
					<th data-col-name="notificaEstatData" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.notificaEstatData"/></th>
					<th data-col-name="notificaEstatFinal" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaEstatFinal"/></th>
					<th data-col-name="notificaDatatOrigen" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaDatatOrigen"/></th>
					<th data-col-name="notificaDatatReceptorNif" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaDatatReceptorNif"/></th>
					<th data-col-name="notificaDatatNumSeguiment" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaDatatNumSeguiment"/></th>
					<th data-col-name="notificaCertificacioData" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaCertificacioData"/></th>
					<th data-col-name="notificaCertificacioArxiuId" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaCertificacioArxiuId"/></th>
					<th data-col-name="notificaCertificacioOrigen" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaCertificacioOrigen"/></th>
					<th data-col-name="notificaCertificacioTipus" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaCertificacioTipus"/></th>
					<th data-col-name="notificaCertificacioArxiuTipus" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaCertificacioArxiuTipus"/></th>
					<th data-col-name="notificaCertificacioNumSeguiment" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaCertificacioNumSeguiment"/></th>
					<th data-col-name="registreNumeroFormatat" data-orderable="false"><spring:message code="enviament.historic.list.columna.registreNumeroFormatat"/></th>
					<th data-col-name="registreData" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.registreData"/></th>
					<th data-col-name="registreEstat" data-orderable="false"><spring:message code="enviament.historic.list.columna.registreEstat"/></th>
					<th data-col-name="registreEstatFinal" data-orderable="false"><spring:message code="enviament.historic.list.columna.registreEstatFinal"/></th>
					<th data-col-name="sirConsultaData" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.sirConsultaData"/></th>
					<th data-col-name="sirRecepcioData" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.sirRecepcioData"/></th>
					<th data-col-name="sirRegDestiData" data-orderable="false" data-converter="datetime"><spring:message code="enviament.historic.list.columna.sirRegDestiData"/></th>
					<th data-col-name="notificacioErrorEvent" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificacioErrorEvent"/></th>
					<th data-col-name="notificaError" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaError"/></th>
					<th data-col-name="notificaDatatErrorDescripcio" data-orderable="false"><spring:message code="enviament.historic.list.columna.notificaDatatErrorDescripcio"/></th>

				</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>

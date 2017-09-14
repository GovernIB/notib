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
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
</head>
<body>
	<h4><spring:message code="enviament.info.seccio.dades"/></h4>
	<table class="table table-bordered table-striped" style="width:100%">
	<tbody>
		<tr>
			<td width="30%"><strong><spring:message code="enviament.info.dada.referencia"/></strong></td>
			<td>${enviament.referencia}</td>
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
	<h4><spring:message code="enviament.info.seccio.titular"/></h4>
	<table class="table table-bordered table-striped" style="width:100%">
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
	<c:if test="${not empty enviament.destinatariNif}">
		<h4><spring:message code="enviament.info.seccio.destinatari"/></h4>
		<table class="table table-bordered table-striped" style="width:100%">
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
	</c:if>
	<c:if test="${not empty enviament.domiciliTipus}">
		<h4><spring:message code="enviament.info.seccio.domicili"/></h4>
		<table class="table table-bordered table-striped" style="width:100%">
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
	</c:if>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>

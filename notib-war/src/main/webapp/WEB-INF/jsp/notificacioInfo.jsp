<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
	<title><spring:message code="notificacio.info.titol"/></title>
	<not:modalHead/>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<h4><spring:message code="notificacio.info.seccio.dades"/></h4>
	<table class="table table-bordered table-striped" style="width:100%">
	<tbody>
		<tr>
			<td width="30%"><strong><spring:message code="notificacio.info.dada.data.creacio"/></strong></td>
			<td><fmt:formatDate value="${notificacio.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.dada.entitat"/></strong></td>
			<td>${notificacio.entitat.nom} (${notificacio.entitat.dir3Codi})</td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.dada.concepte"/></strong></td>
			<td>${notificacio.concepte}</td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.dada.tipus"/></strong></td>
			<td><spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.${notificacio.enviamentTipus}"/></td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.dada.procediment.sia"/></strong></td>
			<td>${notificacio.procedimentDescripcioSia} (${notificacio.procedimentCodiSia})</td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.dada.estat"/></strong></td>
			<td><spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.${notificacio.estat}"/></td>
		</tr>
	</tbody>
	</table>
	<h4><spring:message code="notificacio.info.seccio.document"/></h4>
	<table class="table table-bordered table-striped" style="width:100%">
	<tbody>
		<tr>
			<td width="30%"><strong><spring:message code="notificacio.info.document.arxiu.nom"/></strong></td>
			<td>
				${notificacio.documentArxiuNom}
				<a href="<c:url value="/modal/notificacio/${notificacio.id}/document"/>" class="btn btn-default btn-sm pull-right" title="<spring:message code="notificacio.info.document.descarregar"/>"><span class="fa fa-download"></span></a>
			</td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.document.normalitzat"/></strong></td>
			<td>${notificacio.documentNormalitzat}</td>
		</tr>
		<tr>
			<td><strong><spring:message code="notificacio.info.document.generar.csv"/></strong></td>
			<td>${notificacio.documentGenerarCsv}</td>
		</tr>
	</tbody>
	</table>
	<c:if test="${not empty notificacio.pagadorCorreusCodiDir3 or not empty notificacio.pagadorCieCodiDir3}">
		<h4><spring:message code="notificacio.info.seccio.pagador"/></h4>
		<table class="table table-bordered table-striped" style="width:100%">
		<tbody>
			<c:if test="${not empty notificacio.pagadorCorreusCodiDir3}">
				<tr>
					<td width="30%"><strong><spring:message code="notificacio.info.pagador.correus.codi.dir3"/></strong></td>
					<td>${notificacio.pagadorCorreusCodiDir3}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="notificacio.info.pagador.correus.contracte"/></strong></td>
					<td>${notificacio.pagadorCorreusContracteNum}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="notificacio.info.pagador.correus.client"/></strong></td>
					<td>${notificacio.pagadorCorreusCodiClientFacturacio}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="notificacio.info.pagador.correus.vigencia"/></strong></td>
					<td>${notificacio.pagadorCorreusDataVigencia}</td>
				</tr>
			</c:if>
			<c:if test="${not empty notificacio.pagadorCieCodiDir3}">
				<tr>
					<td><strong><spring:message code="notificacio.info.pagador.cie.codi.dir3"/></strong></td>
					<td>${notificacio.pagadorCieCodiDir3}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="notificacio.info.pagador.cie.vigencia"/></strong></td>
					<td>${notificacio.pagadorCieDataVigencia}</td>
				</tr>
			</c:if>
		</tbody>
		</table>
	</c:if>
	<h4><spring:message code="notificacio.info.seccio.seucaib"/></h4>
	<c:if test="${not empty notificacio.seuExpedientSerieDocumental}">
		<table class="table table-bordered table-striped" style="width:100%">
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
	</c:if>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>

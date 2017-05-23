<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>

<head>
	<title><spring:message code="notificacio.form.titol" arguments="${servei.descripcio}"/></title>
	<not:modalHead/>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	
	<link href="<c:url value="/css/notificacio.css"/>" rel="stylesheet" type="text/css">
	
	<script type="text/javascript">

		$(document).ready(function(){
		    
    		$('#callPDF').click(function(e) {  
    			 $.get("<c:url value="/consulta/showpdf/${notificacioId}"/>");
    		});
			
		});
		
	</script>
	
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/consulta/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal well" commandName="notificacioDto" role="form">
		
		<%-- <h3 class="notificacio-header"><spring:message code="notificacio.form.dadesnotificacio"/></h3> --%>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.enviament"/></div>
		
		<c:set var="envtipus"><spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.${notificacioDto.enviamentTipus}"/></c:set>
		<div class="col-xs-6 labelin2">
		<div class="form-group">
			<label class="control-label col-xs-4" for=""><spring:message code="notificacio.form.camp.tipusenviament"/></label>
			<div class="col-xs-8">
				<form:input path="" cssClass="form-control" id="enviamentTipus" disabled="true" value="${envtipus}" />
			</div>
		</div>
		</div>
		
		<div class="col-xs-6 labelin2"><not:inputText name="enviamentDataProgramada" textKey="notificacio.form.camp.enviament.data.programada" disabled="true" labelSize="4"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.concepte"/></div>
		<div class="col-xs-12"><not:inputText name="concepte" textKey="notificacio.form.camp.concepte" disabled="true" labelSize="2"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.pagador.correus"/></div>
		<div class="col-xs-6"><not:inputText name="pagadorCorreusCodiDir3" textKey="notificacio.form.camp.pc.codidir3" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6 labelin2"><not:inputText name="pagadorCorreusContracteNum" textKey="notificacio.form.camp.pc.contractenum" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6 labelin2"><not:inputText name="pagadorCorreusCodiClientFacturacio" textKey="notificacio.form.camp.pc.ccfacturacio" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6"><not:inputText name="pagadorCorreusDataVigencia" textKey="notificacio.form.camp.pc.datavigencia" disabled="true" labelSize="4"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.pagador.cie"/></div>
		<div class="col-xs-6"><not:inputText name="pagadorCieCodiDir3" textKey="notificacio.form.camp.pcie.codidir3" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6"><not:inputText name="pagadorCieDataVigencia" textKey="notificacio.form.camp.pcie.datavigencia" disabled="true" labelSize="4"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.procediment.sia"/></div>
		<div class="col-xs-6"><not:inputText name="procedimentCodiSia" textKey="notificacio.form.camp.procediment.codi.sia" disabled="true" labelSize="4"/></div>
		<div class="col-xs-12"><not:inputText name="procedimentDescripcioSia" textKey="notificacio.form.camp.procediment.descripcio.sia" disabled="true" labelSize="2"/></div>
		
		<%-- <div class="col-xs-6"><not:inputText name="documentSha1" textKey="notificacio.form.camp.document.sha1" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6 labelin2"><not:inputText name="documentNormalitzat" textKey="notificacio.form.camp.document.normalitzat" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6"><not:inputText name="documentGenerarCsv" textKey="notificacio.form.camp.document.generar.csv" disabled="true" labelSize="4"/></div> --%>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.expedient.seu"/></div>
		<div class="col-xs-6 labelin2"><not:inputText name="seuExpedientSerieDocumental" textKey="notificacio.form.camp.se.serie.documental" disabled="true" labelSize="4"/></div>
		<div class="col-xs-6 labelin2"><not:inputText name="seuExpedientUnitatOrganitzativa" textKey="notificacio.form.camp.se.unitat.organitzativa" disabled="true" labelSize="4"/></div>
		<div class="col-xs-12"><not:inputText name="seuExpedientIdentificadorEni" textKey="notificacio.form.camp.se.identificador.eni" disabled="true" labelSize="2"/></div>
		<div class="col-xs-12"><not:inputText name="seuExpedientTitol" textKey="notificacio.form.camp.se.titol" disabled="true" labelSize="2"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.registre.seu"/></div>
		<div class="col-xs-12"><not:inputText name="seuRegistreOficina" textKey="notificacio.form.camp.sr.oficina" disabled="true" labelSize="2"/></div>
		<div class="col-xs-12"><not:inputText name="seuRegistreLlibre" textKey="notificacio.form.camp.sr.llibre" disabled="true" labelSize="2"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.idioma"/></div>
		<div class="col-xs-12"><not:inputText name="seuIdioma" textKey="notificacio.form.camp.seu.idioma" disabled="true" labelSize="2"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.avis.seu"/></div>
		<div class="col-xs-12"><not:inputText name="seuAvisTitol" textKey="notificacio.form.camp.sa.titol" disabled="true" labelSize="2"/></div>
		<div class="col-xs-12"><not:inputText name="seuAvisText" textKey="notificacio.form.camp.sa.text" disabled="true" labelSize="2"/></div>
		<div class="col-xs-12"><not:inputText name="seuAvisTextMobil" textKey="notificacio.form.camp.sa.text.mobil" disabled="true" labelSize="2"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.oficina.seu"/></div>
		<div class="col-xs-12"><not:inputText name="seuOficiTitol" textKey="notificacio.form.camp.so.titol" disabled="true" labelSize="2"/></div>
		<div class="col-xs-12"><not:inputText name="seuOficiText" textKey="notificacio.form.camp.so.text" disabled="true" labelSize="2"/></div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.camp.estat.notificacio"/></div>
		
		<c:set var="notestat"><spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.${notificacioDto.estat}"/></c:set>
		<div class="col-xs-6">
		<div class="form-group">
			<label class="control-label col-xs-4" for=""><spring:message code="notificacio.form.camp.estat"/></label>
			<div class="col-xs-8">
				<form:input path="" cssClass="form-control" id="enviamentTipus" disabled="true" value="${notestat}" />
			</div>
		</div>
		</div>
		
	</form:form>
	
	<script id="botonsTemplate" type="text/x-jsrender"></script>
	
	<div id="modal-botons" class="col-xs-12 well">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"> <span class="fa fa-reply"></span>&nbsp;<spring:message code="comu.boto.tornar"/> </a>
	</div>
		
</body>

</html>

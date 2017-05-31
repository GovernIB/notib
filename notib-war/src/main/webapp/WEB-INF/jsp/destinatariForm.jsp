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
	
	<link href="<c:url value="/css/notificacio.css"/>" rel="stylesheet" type="text/css">
	
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
	
	<script type="text/javascript"></script>
	
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/consulta/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal well" commandName="notificacioDestinatariDto" role="form">
		
		<div class="col-xs-12"> <not:inputText name="referencia" textKey="notificacio.form.destinatari.referencia" disabled="true" labelSize="2"/> </div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.destinatari.titular"/></div>
		<div class="col-xs-12"> <not:inputText name="titularNom" textKey="notificacio.form.destinatari.titular.nom" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="titularLlinatges" textKey="notificacio.form.destinatari.titular.llinatges" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-6"> <not:inputText name="titularNif" textKey="notificacio.form.destinatari.titular.nif" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="titularTelefon" textKey="notificacio.form.destinatari.titular.telefon" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-12"> <not:inputText name="titularEmail" textKey="notificacio.form.destinatari.titular.email" disabled="true" labelSize="2"/> </div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.destinatari.destinatari"/></div>
		<div class="col-xs-12"> <not:inputText name="destinatariNom" textKey="notificacio.form.destinatari.destinatai.nom" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="destinatariLlinatges" textKey="notificacio.form.destinatari.destinatari.llinatges" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-6"> <not:inputText name="destinatariNif" textKey="notificacio.form.destinatari.destinatari.nif" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="destinatariTelefon" textKey="notificacio.form.destinatari.destinatari.telefon" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-12"> <not:inputText name="destinatariEmail" textKey="notificacio.form.destinatari.destinatari.email" disabled="true" labelSize="2"/> </div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.destinatari.domicili"/></div>
		
		<c:set var="domtipus"><spring:message code="es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto.${notificacioDestinatariDto.domiciliTipus}"/></c:set>
		<div class="col-xs-6">
		<div class="form-group">
			<label class="control-label col-xs-4" for=""><spring:message code="notificacio.form.destinatari.domicili.tipus"/></label>
			<div class="col-xs-8">
				<form:input path="" cssClass="form-control" id="domiciliTipus" disabled="true" value="${domtipus}" />
			</div>
		</div>
		</div>
		
		<c:set var="concrtipus"><spring:message code="es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto.${notificacioDestinatariDto.domiciliConcretTipus}"/></c:set>
		<div class="col-xs-6">
		<div class="form-group">
			<label class="control-label col-xs-4" for=""><spring:message code="notificacio.form.destinatari.domicili.concret.tipus"/></label>
			<div class="col-xs-8">
				<form:input path="" cssClass="form-control" id="domiciliConcretTipus" disabled="true" value="${concrtipus}" />
			</div>
		</div>
		</div>
		
		<div class="col-xs-6"> <not:inputText name="domiciliViaTipus" textKey="notificacio.form.destinatari.domicili.via.tipus" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliViaNom" textKey="notificacio.form.destinatari.domicili.via.nom" disabled="true" labelSize="2"/> </div>
				
		<c:set var="domnumtip"><spring:message code="es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto.${notificacioDestinatariDto.domiciliNumeracioTipus}"/></c:set>
		<div class="col-xs-6">
		<div class="form-group">
			<label class="control-label col-xs-4" for=""><spring:message code="notificacio.form.destinatari.domicili.numeracio.tipus"/></label>
			<div class="col-xs-8">
				<form:input path="" cssClass="form-control" id="domiciliNumeracioTipus" disabled="true" value="${domnumtip}" />
			</div>
		</div>
		</div>
		
		<div class="col-xs-6"> <not:inputText name="domiciliNumeracioNumero" textKey="notificacio.form.destinatari.domicili.numeracio.numero" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="domiciliNumeracioPuntKm" textKey="notificacio.form.destinatari.domicili.numeracio.puntkm" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="domiciliApartatCorreus" textKey="notificacio.form.destinatari.domicili.apartat.correus" disabled="true" labelSize="4"/> </div>
		
		<div class="col-xs-12"> <not:inputText name="domiciliBloc" textKey="notificacio.form.destinatari.domicili.bloc" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliPortal" textKey="notificacio.form.destinatari.domicili.portal" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliEscala" textKey="notificacio.form.destinatari.domisili.escala" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliPlanta" textKey="notificacio.form.destinatari.domicili.planta" disabled="true" labelSize="2"/> </div> 
		<div class="col-xs-12"> <not:inputText name="domiciliPorta" textKey="notificacio.form.destinatari.domicili.porta" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliComplement" textKey="notificacio.form.destinatari.domicili.complement" disabled="true" labelSize="2"/> </div>
		
		<div class="col-xs-6"> <not:inputText name="domiciliPoblacio" textKey="notificacio.form.destinatari.domicili.poblacio" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="domiciliMunicipiCodiIne" textKey="notificacio.form.destinatari.domicili.municipi.codi.ine" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliMunicipiNom" textKey="notificacio.form.destinatari.domicili.municipi.nom" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-6"> <not:inputText name="domiciliCodiPostal" textKey="notificacio.form.destinatari.domicili.codi.postal" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="domiciliProvinciaCodi" textKey="notificacio.form.destinatari.domicili.provincia.codi" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliProvinciaNom" textKey="notificacio.form.destinatari.domicili.provincia.nom" disabled="true" labelSize="2"/> </div>
		
		<div class="col-xs-6"> <not:inputText name="domiciliPaisCodiIso" textKey="notificacio.form.destinatari.domicili.pais.codi.iso" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="domiciliCie" textKey="notificacio.form.destinatari.domicili.pais.nom" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliPaisNom" textKey="notificacio.form.destinatari.domicili.linea1" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliLinea1" textKey="notificacio.form.destinatari.domicili.linea2" disabled="true" labelSize="2"/> </div>
		<div class="col-xs-12"> <not:inputText name="domiciliLinea2" textKey="notificacio.form.destinatari.domicili.cie" disabled="true" labelSize="2"/> </div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.destinatari.deh"/></div>
		<div class="col-xs-6"> <not:inputText name="dehObligat" textKey="notificacio.form.destinatari.deh.obligat" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="dehNif" textKey="notificacio.form.destinatari.deh.nif" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="dehProcedimentCodi" textKey="notificacio.form.destinatari.deh.procediment.codi" disabled="true" labelSize="4"/> </div>
		
		<div class="col-xs-12 sub-menu"><spring:message code="notificacio.form.destinatari.altres"/></div>
		
		<c:set var="notificaservtip"><spring:message code="es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto.${notificacioDestinatariDto.serveiTipus}"/></c:set>
		<div class="col-xs-6">
		<div class="form-group">
			<label class="control-label col-xs-4" for=""><spring:message code="notificacio.form.destinatari.servei.tipus"/></label>
			<div class="col-xs-8">
				<form:input path="" cssClass="form-control" id="serveiTipus" disabled="true" value="${notificaservtip}" />
			</div>
		</div>
		</div>
		
		<div class="col-xs-6"> <not:inputText name="retardPostal" textKey="notificacio.form.destinatari.retard.postal" disabled="true" labelSize="4"/> </div>
		<div class="col-xs-6"> <not:inputText name="caducitat" textKey="notificacio.form.destinatari.caducitat" disabled="true" labelSize="4"/> </div>
		
	</form:form>
	
	<script id="botonsTemplate" type="text/x-jsrender"></script>
	
	
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"> <span class="fa fa-reply"></span>&nbsp;<spring:message code="comu.boto.tornar"/> </a>
	</div>
		
</body>

</html>

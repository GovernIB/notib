<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<c:choose>
	<c:when test="${empty notificacioCommand.id}"><c:set var="titol"><spring:message code="notificacio.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="notificacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<c:set var="dadesGenerals"><spring:message code="notificacio.form.titol.dadesgenerals"/></c:set>
<c:set var="document"><spring:message code="notificacio.form.titol.document"/></c:set>
<c:set var="parametresRegistre"><spring:message code="notificacio.form.titol.parametresregistre"/></c:set>
<c:set var="enviaments"><spring:message code="notificacio.form.titol.enviaments"/></c:set>
<c:set var="titular"><spring:message code="notificacio.form.titol.enviaments.titular"/></c:set>
<c:set var="destinataris"><spring:message code="notificacio.form.titol.enviaments.destinataris"/></c:set>
<c:set var="entregaPostal"><spring:message code="notificacio.form.titol.entregapostal"/></c:set>
<c:set var="entregaPostalDades"><spring:message code="notificacio.form.titol.entregapostal.dades"/></c:set>
<c:set var="entregaDireccio"><spring:message code="notificacio.form.titol.entregadireccio"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
	
<script type="text/javascript">
$(document).ready(function() {

	//$('.dadesgeneralsForm').hide();
	$('.documentForm').hide();
	$('.parametresregistreForm').hide();
	$('.enviamentsForm').hide();
	$('.entregapostalForm').hide();
	$('.entregapostal').hide();
	$('.entregadireccioForm').hide();
	
	$('#tipusDocument').on('change', function() {
		if ($(this).val() == 'ARXIU') {
			$('#input-origen-arxiu').removeClass('hidden');
			$('#input-origen-csvuuid').addClass('hidden');
		} else {
			$('#input-origen-csvuuid').removeClass('hidden');
			$('#input-origen-arxiu').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});
	
	$('#entregaPostalActiva').change(function(){
		$('.entregapostal').slideToggle();
	});
	
	$('#addDestinatariButton').on('click', function() {
		addDestinatari();
	});
	
	$('#dadesgenerals').on('click', function() {
		$('.dadesgeneralsForm').slideToggle();
	});
	$('#document').on('click', function() {
		$('.documentForm').slideToggle();
	});
	$('#parametresregistre').on('click', function() {
		$('.parametresregistreForm').slideToggle();
	});
	$('#enviaments').on('click', function() {
		$('.enviamentsForm').slideToggle();
	});
	$('#entregapostal').on('click', function() {
		$('.entregapostalForm').slideToggle();
	});
	$('#entregadireccio').on('click', function() {
		$('.entregadireccioForm').slideToggle();
	});


	var agrupable = $("#procedimentId").children(":selected").attr("class");
	var procedimentId = $("#procedimentId").children(":selected").attr("value");
	comprovarGrups(agrupable, procedimentId);
	
	$('#procedimentId').on('change', function() {
		var agrupable = $(this).children(":selected").attr("class");
		var procedimentId = $(this).children(":selected").attr("value");
		comprovarGrups(agrupable, procedimentId)
		webutilModalAdjustHeight();
	});
});	

function addDestinatari() {
	var destinatariForm = $(".personaForm:first").clone();
	
	$(destinatariForm).appendTo(".newDestinatari").find("input[type='text']").val("");
	
	webutilModalAdjustHeight();
}

function comprovarGrups(agrupable, procedimentId) {
	var notificacioGrupsUrl = "<c:url value="/notificacio/"/>" + procedimentId + "/grups";
	console.log(notificacioGrupsUrl);
	if (agrupable == 'true') {
		var procediments = [{}];
			$.get(notificacioGrupsUrl).done(function(grups) {
				
				console.log(procediments);
				for (i = 0; i < grups.length; i++) {
					var select = document.getElementById('grup');
					var option = document.createElement('option');
					option.name = 'grup';
					option.text = grups[i].nom;
					option.value = grups[i].id;
					select.add(option);
					
					procediments.push({
						nom: grups[i].nom,
						id: grups[i].id
					});
				}
			})
			$('#grup').find('option').remove();
			$('.agrupable').removeClass('hidden');
		} else {
			$('.agrupable').addClass('hidden');
		}
	}
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/notificacio/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="notificacioCommandV2" enctype="multipart/form-data">
		<not:notTitol name="${dadesGenerals}" id="dadesgenerals"></not:notTitol>
		<div class="row dadesgeneralsForm">
			<div class="col-md-12">
				<not:inputText name="emisorDir3Codi" textKey="notificacio.form.camp.codiemisor" value="${entitat.dir3Codi}" labelSize="2" readonly="true" required="true"/>
			</div>
			<div class="col-md-6">
				<not:inputSelect name="comunicacioTipus" textKey="notificacio.form.camp.comunicaciotipus" required="true"/>
			</div>
			<div class="col-md-6">
				<not:inputSelect name="enviamentTipus" textKey="notificacio.form.camp.enviamenttipus" required="true"/>
			</div>
			<div class="col-md-12">
				<not:inputText name="concepte" textKey="notificacio.form.camp.concepte" labelSize="2" required="true"/>
			</div>
			<div class="col-md-12">
				<not:inputTextarea name="descripcio" textKey="notificacio.form.camp.descripcio" labelSize="2"/>
			</div>
			<div class="col-md-6">
				<not:inputDate name="enviamentDataProgramada" textKey="notificacio.form.camp.enviamentdata" />
			</div>
			<div class="col-md-6">
				<not:inputText name="retard" textKey="notificacio.form.camp.retard" labelSize="2"/>
			</div>
			<div class="col-md-12">
				<not:inputSelect name="procedimentId" textKey="notificacio.form.camp.procediment" optionItems="${procediments}" optionAgrupableAttribute="agrupar" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2"/>
			</div>
			<div class="col-md-12 agrupable hidden">
				
				<div class="form-group">
					<label class="control-label col-xs-2" for="grup"><spring:message code="notificacio.form.camp.grup"/></label>
					<div class="controls col-xs-10">
						<select class="form-control" path="grup" style="width:100%" id="grup"></select>
					</div>
				</div>
				<!--
				<not:inputSelect name="grupId" textKey="notificacio.form.camp.grup" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2"/>
				-->
			</div>
		</div>
		<not:notTitol name="${document}" id="document"></not:notTitol>
		<div class="row documentForm">
			<div class="col-md-6">
				<not:inputSelect name="tipusDocument" textKey="notificacio.form.camp.codiemisor" labelSize="4"/>
			</div>
			<div id="input-origen-csvuuid"  class="col-md-6">
				<not:inputText name="documentArxiuUuidCsv" textKey="notificacio.form.camp.codiemisor" labelSize="3"/>
			</div>
			<div id="input-origen-arxiu" class="col-md-6 hidden" >
				<not:inputFile  name="arxiu" textKey="notificacio.form.camp.arxiu" labelSize="3"/>
			</div>
			<div class="col-md-12">
				<not:inputCheckbox name="document.normalitzat" textKey="notificacio.form.camp.normalitzat" labelSize="2"/>
			</div>
			<div class="col-md-12">
				<not:inputTextAddGrup name="document.metadades" textKey="notificacio.form.camp.metadades" labelSize="2"/>
			</div>
		</div>
		<not:notTitol name="${parametresRegistre}" id="parametresregistre" />
		<div class="row parametresregistreForm">
			
		</div>
		<not:notTitol name="${enviaments}" id="enviaments"></not:notTitol>
		<div class="row enviamentsForm">
			<div class="col-md-6">
				<not:inputSelect name="enviament.serveiTipus" textKey="notificacio.form.camp.destinatari.serveitipus" labelSize="4" required="true"/>
			</div>
			<div>
				<not:personaDefinir tipus="titular" titol="${titular}" />
			</div>
			<div class="destinatari">
				<not:personaDefinir tipus="destinatari" titol="${destinataris}" />
			</div>
			<div class="newDestinatari"></div>
		</div>
		
		<not:notTitol name="${entregaPostal}" id="entregapostal"></not:notTitol>
		<div class="row entregapostalForm">
			<div class="col-md-12">
				<not:inputCheckbox name="entregaPostalActiva" textKey="notificacio.form.camp.entregapostal.activa" labelSize="2" />
			</div>
			<div class="entregapostal">
				<not:entregaDefinir titol="${entregaPostalDades}" />
			</div>
		</div>
		<not:notTitol name="${entregaDireccio}" id="entregadireccio"></not:notTitol>
		<div class="row entregadireccioForm">
			<div class="col-md-12">
				<not:inputCheckbox name="entregaDeh.obligat" textKey="notificacio.form.camp.entregapostal.deh" labelSize="2" />
			</div>
		</div>
		<div id="modal-botons">
			<button id="addNotificacioButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/notificacio/new"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>	
	</form:form>
	
</body>
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
<c:set var="metodeEntrega"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega"/></c:set>
<c:set var="entregaPostal"><spring:message code="notificacio.form.titol.entregapostal"/></c:set>
<c:set var="entregaPostalDades"><spring:message code="notificacio.form.titol.entregapostal.dades"/></c:set>
<c:set var="entregaDireccio"><spring:message code="notificacio.form.titol.entregadireccio"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<style type="text/css">

.separacioEnviaments {
	border-top: 8px solid #eee;
}

.form-horizontal .control-label{
    text-align: left;
}
</style>
<script type="text/javascript">

$(document).ready(function() {
	
	$('.nextForm').click(function(){
		$('.nav-tabs > .active').next('li').find('a').trigger('click');
	});
	
	var count = 0;
	$('.entregapostal').hide();
	
	$('#tipusDocument').on('change', function() {
		if ($(this).val() == 'ARXIU') {
			$('#metadades').removeClass('hidden');
			$('#input-origen-arxiu').removeClass('hidden');
			$('#input-origen-csvuuid').addClass('hidden');
		} else {
			$('#metadades').addClass('hidden');
			$('#input-origen-csvuuid').removeClass('hidden');
			$('#input-origen-arxiu').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});
	
	$('#entregaPostalActiva').change(function(){
		$('.entregapostal').slideToggle();
	});

	var agrupable = $("#procedimentId").children(":selected").attr("class");
	var procedimentId = $("#procedimentId").children(":selected").attr("value");
	
	$('#procedimentId').on('change', function() {
		var agrupable = $(this).children(":selected").attr("class");
		var procedimentId = $(this).children(":selected").attr("value");
		comprovarGrups(agrupable, procedimentId)
		webutilModalAdjustHeight();
	});
	
	//Add metadata
	$('#add').on('click', function () {
		//Input to add
		var metadataInput = 
			"<div class='form-group'>" +
				"<label class='control-label col-xs-2'></label>" +
				"<div class='col-xs-10'>" +
					"<div class='input-group'>" +
					"<input name='metadades' id='metadades' type='text' class='form-control add grupVal_" + count + "' readonly/>" +
					"<span class='input-group-addon' id='remove'><span class='fa fa-remove'></span></span>" +
					"</div>" +
				"</div>" +
			"</div>";

		var val = $(".input-add").children().val();
		if (val != '') {
			$("#list").prepend(metadataInput);
			$(".grupVal_" + count).attr("value", val);
			$("#list").find("#remove").addClass("grupVal_" + count);
			count++;
		}
		
		webutilModalAdjustHeight();
	});
	

});	

function addDestinatari(enviament_id) {
	var number;
	var num;
	var enviament_id_num = enviament_id.substring(enviament_id.indexOf( '[' ) + 1, enviament_id.indexOf( ']' ));
	enviament_id_num = parseInt(enviament_id_num);
	if ($("div[class*=' personaForm_" + enviament_id_num + "']").hasClass("hidden")) {
		$("div[class*=' personaForm_" + enviament_id_num + "']").removeClass("hidden").show();
	} else {
		var destinatariForm = $("div[class*=' personaForm_" + enviament_id_num + "']").last().clone();
		destinatariForm.find('input').each(function() {
			number = this.name.substring(this.name.lastIndexOf( '[' ) + 1, this.name.lastIndexOf( ']' ));
			//Obtenir numero personaForm
			num = parseInt(number);
			++num;
		    this.name= this.name.replace("is[" + number, "is[" + num);
		    this.id= this.id.replace("is[" + number, "is[" + num);
		    
		    destinatariForm.removeClass('personaForm_' + enviament_id_num + '_' + number).addClass('personaForm_' + enviament_id_num + '_' + num);

		    //id botó delete destinatari
		    if($(this).hasClass('delete')) {
			    this.name= this.name.replace("][" + number, "][" + num);
			    this.id= this.id.replace("][" + number, "][" + num);
		    }
		});
		
		$(destinatariForm).appendTo('.newDestinatari_'+ enviament_id_num).slideDown("slow").find("input[type='text']").val("");
		
		webutilModalAdjustHeight();
	}
}

function addEnv() { 
	var number;
	var num;
	var enviamentForm = $(".enviamentsForm").last().clone();
	var enviamentFormNou;
	
	enviamentForm.find('input').each(function() {
		number = this.name.substring(this.name.indexOf( '[' ) + 1, this.name.indexOf( ']' ));
		num = parseInt(number);
		++num;
		this.name= this.name.replace(number,num);
		this.id= this.id.replace(number,num);
		
		if($(this).attr("id") == "envioTooltip") {
			this.value= this.value.replace(number,num);
			$(this).tooltip();
		}
		
		if($(this).hasClass('formEnviament')) {
			this.name= this.name.replace("[" + number, "[" + num);
		    this.id= this.id.replace("[" + number, "[" + num);
		}
	});
		
	$(enviamentForm).appendTo(".newEnviament").slideDown("slow").find("input[type='text']").val("");

	var newDestinatariForm = $('.newDestinatari_' + number + ':last');
	newDestinatariForm.removeClass('newDestinatari_'+number).addClass('newDestinatari_'+num);

	$('.newDestinatari_' + num).children('div').each(function (i) {

		var destinatariForm = $('.personaForm_' + number + '_' + 0 + ':last');
		destinatariForm.removeClass('personaForm_'+number + '_' + 0).addClass('personaForm_' + num + '_' + 0);

	    var enviamentForm = $('.enviamentForm_' + number + ':last');
	    enviamentForm.removeClass('enviamentForm_' + number).addClass('enviamentForm_' + num);
	    
		if (i === 0){
			$(this).addClass('hidden');
		} else {
			$(this).remove();
		}
	});
	
	webutilModalAdjustHeight();
}

function destinatarisDelete(className) {
	var element = document.getElementById(className);
	var parent = $(element).closest(".destinatariForm");
	var classParent = $(parent).attr('class');
	
	var destinatari_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));
	var enviament_id_num = className.substring(className.indexOf('[') + 1, className.indexOf(']'));
	
	//Si es el primer destinatari (0)
	if (destinatari_id_num == 0) {
		$(parent).addClass('hidden');
	} else {
		$(parent).remove();
	}
}

function enviamentDelete(className) {
	var element = document.getElementById(className);
	var parent = $(element).closest(".enviamentsForm");
	var classParent = $(parent).attr('class');
	
	var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));
	
	//Si es el primer destinatari (0)
	if (enviament_id_num == 0) {
		$(parent).addClass('hidden');
	} else {
		$(parent).remove();
	}
}

function validateForm() {

}

</script>
</head>
<body>
	<c:forEach items="${errors}" var="error" varStatus="status">
		<c:set var="errorConcepte" value="${error}"></c:set>
	</c:forEach>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#dadesgeneralsForm" aria-controls="dadesgeneralsForm" role="tab" data-toggle="tab"><spring:message code="notificacio.form.titol.dadesgenerals"/><c:if test="${not empty errorConcepte}"> <span class="fa fa-warning text-danger"></span></c:if></a> </li>
		<li role="presentation"><a href="#documentForm" aria-controls="documentForm" role="tab" data-toggle="tab"><spring:message code="notificacio.form.titol.document"/></a></li>
		<li role="presentation"><a href="#parametresregistreForm" aria-controls="parametresregistreForm" role="tab" data-toggle="tab"><spring:message code="notificacio.form.titol.parametresregistre"/></a></li>
		<li role="presentation"><a href="#enviamentsForm" aria-controls="enviamentsForm" role="tab" data-toggle="tab"><spring:message code="notificacio.form.titol.enviaments"/></a></li>
	</ul>
	<br/>
	<c:set var="formAction"><not:modalUrl value="/notificacio/newOrModify"/></c:set>
	<form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="notificacioCommandV2" enctype="multipart/form-data">
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dadesgeneralsForm">
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
						<not:inputText name="concepte" textKey="notificacio.form.camp.concepte" labelSize="2" required="true" />
					</div>
					<div class="col-md-12">
						<not:inputTextarea name="descripcio" textKey="notificacio.form.camp.descripcio" labelSize="2"/>
					</div>
					<div class="col-md-12">
					<form:hidden path="procedimentId" value="${procediment.id}"/>
						<not:inputText name="procedimentNom" textKey="notificacio.form.camp.procediment" value="${procediment.nom}" labelSize="2" readonly="true"/>
					</div>
					<c:if test="${not empty grups}">
						<div class="col-md-12">
							<not:inputSelect name="grupId" textKey="notificacio.form.camp.grup" optionItems="${grups}" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2"/>
						</div>
					</c:if>
				</div>
				<div class="text-right col-md-12">
					<div class="btn-group">
						<button type="button" onclick="validateForm()" class="btn btn-info nextForm"><spring:message code="comu.boto.seguent"/> <span class="fa fa-forward"></span></button>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="documentForm">
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
					<div class="col-md-12 hidden" id="metadades">
						<not:inputTextAdd name="document.metadades" idIcon="add" textKey="notificacio.form.camp.metadades" labelSize="2"/>
						<div id="list"></div>
					</div>
				</div>
				<div class="text-right col-md-12">
					<div class="btn-group">
						<button type="button" class="btn btn-info nextForm"><spring:message code="comu.boto.seguent"/> <span class="fa fa-forward"></span></button>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="parametresregistreForm">
				<div class="row parametresregistreForm">
					<div class="col-md-6">
						<not:inputText name="oficina" value="${procediment.oficina}" textKey="notificacio.form.camp.oficina" labelSize="4" readonly="true"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="llibre" value="${procediment.llibre}" textKey="notificacio.form.camp.llibre" labelSize="4" readonly="true"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="extracte" textKey="notificacio.form.camp.extracte" labelSize="4"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="docFisica" textKey="notificacio.form.camp.doc" labelSize="4"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="idioma" textKey="notificacio.form.camp.idioma" labelSize="4"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="tipusAssumpte" value="${procediment.tipusAssumpte}" textKey="notificacio.form.camp.tipus" labelSize="4" readonly="true"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="numExpedient" textKey="notificacio.form.camp.expedient" labelSize="4"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="refExterna" textKey="notificacio.form.camp.externa" labelSize="4"/>
					</div>
					<div class="col-md-6">
						<not:inputText name="codiAssumpte" textKey="notificacio.form.camp.codi" labelSize="4"/>
					</div>
					<div class="col-md-12">
						<not:inputTextarea name="observacions" textKey="notificacio.form.camp.observacions" labelSize="2"/>
					</div>
				</div>
				<div class="text-right col-md-12">
					<div class="btn-group">
						<button type="button" class="btn btn-info nextForm"><spring:message code="comu.boto.seguent"/> <span class="fa fa-forward"></span></button>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="enviamentsForm">
			<c:set var="j" value="${0}"/>
			<c:forEach items="enviaments" var="enviament" varStatus="status">
			<div class="newEnviament">
				<div class="row enviamentsForm formEnviament enviamentForm_${j}">	
					<div class="col-md-6">
						<not:inputSelect name="enviaments[${j}].serveiTipus" textKey="notificacio.form.camp.destinatari.serveitipus" labelSize="4" required="true" />
					</div>
					<div class="titular">
						<div class="col-md-12">
							<div>
								<label class="text-primary">${titular}</label>
							</div>
						</div>
						<div class="col-md-12 separacio"><hr></div>
						<div class="personaForm">
							<div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.nif" textKey="notificacio.form.camp.titular.nif" required="true" />
								</div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.nom" textKey="notificacio.form.camp.titular.nom" required="true" />
								</div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.llinatge1" textKey="notificacio.form.camp.titular.llinatge1" required="true" />
								</div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.llinatge2" textKey="notificacio.form.camp.titular.llinatge2" />
								</div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.email" textKey="notificacio.form.camp.titular.email" />
								</div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.telefon" textKey="notificacio.form.camp.titular.telefon" />
								</div>
								<div class="col-md-6">
									<not:inputText name="enviaments[${j}].titular.dir3codi" textKey="notificacio.form.camp.titular.dir3codi" />
								</div>
							</div>
						</div>
					</div>
					<div class="destinatari">
						<div class="col-md-8">
							<div>
								<label class="text-primary">${destinataris}</label>
							</div>
						</div>
						<div class="col-md-12">
							<hr>
						</div>
						<c:set var="i" value="${0}"/>
						<c:forEach items="destinataris" var="destinatari" varStatus="status">
							<div class="newDestinatari_${j}">
								<div class="col-md-12 destinatariForm hidden personaForm_${j}_${i} ">
									<div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].nif" textKey="notificacio.form.camp.titular.nif" required="true" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].nom" textKey="notificacio.form.camp.titular.nom" required="true" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].llinatge1" textKey="notificacio.form.camp.titular.llinatge1" required="true" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].llinatge2" textKey="notificacio.form.camp.titular.llinatge2" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].email" textKey="notificacio.form.camp.titular.email" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].telefon" textKey="notificacio.form.camp.titular.telefon" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].destinataris[${i}].dir3codi" textKey="notificacio.form.camp.titular.dir3codi" />
										</div>
										<div class="col-Vd-6 text-right">
											<input type="button" class="btn btn-default btn-group delete" name="destinatarisDelete[${j}][${i}]"  onclick="destinatarisDelete(this.id)" id="destinatarisDelete[${j}][${i}]" value="<spring:message code="notificacio.form.boto.eliminar.destinatari"/>"/>
										</div>
										<div class="col-md-12">
											<hr>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
						</div>	
						<div class="col-md-12">
							<div class="text-right">
								<input type="button" class="btn btn-default" id="addDestinatri" onclick="addDestinatari()" value="<spring:message code="notificacio.form.boto.nou.destinatari"/>"/>
							</div>
						</div>
						<div class="col-md-12 separacio"></div>
						<div class="metodeEntrega">
							<div class="col-md-8">
								<div>
									<label class="text-primary">${metodeEntrega}</label>
								</div>
							</div>
							<div class="col-md-12">
								<not:inputCheckbox name="enviaments[${j}].entregaPostalActiva" textKey="notificacio.form.camp.entregapostal.activa" labelSize="2" />
							</div>
							<div class="entregapostal">
								<div class="col-md-12">
									<div class="col-md-12">
										<not:inputSelect name="enviaments[${j}].entregaPostal.tipus" textKey="notificacio.form.camp.entregapostal.tipus" required="true" labelSize="2"/>
									</div>
									<div class="col-md-6">
										<not:inputSelect name="enviaments[${j}].entregaPostal.tipusVia" textKey="notificacio.form.camp.entregapostal.tipusvia" required="true" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.viaNom" textKey="notificacio.form.camp.entregapostal.vianom" required="true" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.numeroCasa" textKey="notificacio.form.camp.entregapostal.numerocasa" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.portal" textKey="notificacio.form.camp.entregapostal.portal" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.escala" textKey="notificacio.form.camp.entregapostal.escala" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.planta" textKey="notificacio.form.camp.entregapostal.planta" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.porta" textKey="notificacio.form.camp.entregapostal.porta" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.bloc" textKey="notificacio.form.camp.entregapostal.bloc" />
									</div>
									<div class="col-md-12">
										<not:inputText name="enviaments[${j}].entregaPostal.complement" textKey="notificacio.form.camp.entregapostal.complement" labelSize="2"/>
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.codiPostal" textKey="notificacio.form.camp.entregapostal.codipostal" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.poblacio" textKey="notificacio.form.camp.entregapostal.poblacio" />
									</div>
									<div class="col-md-12">
										<not:inputText name="enviaments[${j}].entregaPostal.paisCodi" textKey="notificacio.form.camp.entregapostal.paiscodi" labelSize="2" inputSize="4"/>
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" />
									</div>
									<div class="col-md-6">
										<not:inputText name="enviaments[${j}].entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" />
									</div>
								</div>
							</div>
							<div class="col-md-12">
								<not:inputCheckbox name="enviaments[${j}].entregaDeh.obligat" textKey="notificacio.form.camp.entregapostal.deh" labelSize="2" />
							</div>
							<div class="col-md-12 text-right">
								<div class="btn-group">
									<input type="button" class="btn btn-default formEnviament" name="enviamentDelete[${j}]"  onclick="enviamentDelete(this.id)" id="enviamentDelete[${j}]" value="<spring:message code="notificacio.form.boto.eliminar.enviament"/>"/>
								</div>
								<div class="col-md-12">
									<hr class="separacioEnviaments">
								</div>
							</div>
						</div>
						</div>
						<div class="col-md-12 separacio"></div>
							<div class="metodeEntrega">
								<div class="col-md-8">
									<div>
										<label class="text-primary">${metodeEntrega}</label>
									</div>
								</div>
							</div>
							<div class="col-md-12 text-right">
								<div class="btn-group">
									<input type="button" class="btn btn-default" id="addEnviament" onclick="addEnv()" value="<spring:message code="notificacio.form.boto.nou.enviament"/>" />
								</div>
							</div>
							<div class="col-md-12">
								<hr>
							</div>
							<div class="text-right col-md-12">
								<div class="btn-group">
									<button type="submit" class="btn btn-success saveForm"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
								<div class="col-md-12">
									<not:inputCheckbox name="entregaPostalActiva" textKey="notificacio.form.camp.entregapostal.activa" labelSize="2" />
								</div>
								<div class="entregapostal">
									<not:entregaDefinir titol="${entregaPostalDades}" />
								</div>
								<div class="col-md-12">
									<not:inputCheckbox name="entregaDeh.obligat" textKey="notificacio.form.camp.entregapostal.deh" labelSize="2" />
								</div>
							</div>
						</div>
					</div>
			</c:forEach>	
			</div>
		</div>
		<div id="modal-botons text-right">
			<button id="addNotificacioButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
		</div>	
	</form:form>
	
</body>
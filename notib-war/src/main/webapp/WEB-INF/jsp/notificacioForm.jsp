<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>



<c:choose>
    <c:when test="${empty notificacioCommand.id}"><c:set var="titol"><spring:message code="notificacio.form.titol.crear"/> <br> <small>  ${procediment.nom}</small></c:set></c:when>
    <c:otherwise><c:set var="titol"><spring:message code="notificacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<c:set var="dadesGenerals"><spring:message code="notificacio.form.titol.dadesgenerals"/></c:set>
<c:set var="document"><spring:message code="notificacio.form.titol.document"/></c:set>
<c:set var="parametresRegistre"><spring:message code="notificacio.form.titol.parametresregistre"/></c:set>
<c:set var="enviaments"><spring:message code="notificacio.form.titol.enviaments"/></c:set>
<c:set var="titular"><spring:message code="notificacio.form.titol.enviaments.titular"/></c:set>
<c:set var="destinatarisTitol"><spring:message code="notificacio.form.titol.enviaments.destinataris"/></c:set>
<c:set var="metodeEntrega"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega"/></c:set>
<c:set var="entregaPostal"><spring:message code="notificacio.form.titol.entregapostal"/></c:set>
<c:set var="entregaPostalDades"><spring:message code="notificacio.form.titol.entregapostal.dades"/></c:set>
<c:set var="entregaDireccio"><spring:message code="notificacio.form.titol.entregadireccio"/></c:set>
<c:set var="entitatDir3Codi">${entitat.dir3Codi}</c:set>


<c:url value="/notificacio/nivellsAdministracions" 	var="urlNivellAdministracions"/>
<c:url value="/notificacio/comunitatsAutonomes" 	var="urlComunitatsAutonomes"/>
<c:url value="/notificacio/provincies" 				var="urlProvincies"/>
<c:url value="/notificacio/localitats" 				var="urlLocalitats"/>


<html>
<head>
    <title>${titol}</title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
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
.help-block {
	font-size: x-small;
}
#entregaPostal .help-block {
/* 	font-size: 8px; */
}
.inputcss {
	width: calc(100% - 175px);
	float: left;
	height: 44px;
}
.labelcss {
	width: 175px;
	float: left;
}
.select2-container--bootstrap {
	width: 100% !important;
}
.delete {
	top: 27px;
}
.dest .form-group {
	margin-bottom: 0px;
}
.separacioEnviaments {
    border-top: 8px solid #eee;
}

.form-horizontal .control-label{
    text-align: left;
}
.title {
	margin-top: 2%;
	font-size: larger;
}
.title > label {
	color: #ff9523;
}
.title > hr {
	margin-top: 0%;
}
.title-envios {
	color: #ffffff;
	margin-top: 1%;
	font-size: larger;
}
.title-envios > hr {
	margin-top: 0%;
	height: 1px;
	background-color: #696666;
}
.newEnviament {
	padding: 1%;
	border-radius: 5px;
}
.container-envios {
	background-color: #EFE8EA;
}
.title-container {
	text-align: center;
	background-color: #696666;
	width: 12%;
}
.datepicker table tr td.today, .datepicker table tr td.today:hover { 
	color: #000000;
	background: #a4a4a4 !important; 
	background-color: #a4a4a4 !important;
}

.entregaPostalInfo {
	display: none;
}
.loading {
	background: rgba( 255, 255, 255, 0.8 );
  	display: none;
  	height: 100%;
  	position: fixed;
  	width: 100%;
  	z-index: 9999;
  	left: 0;
  	top: 0;
}
.loading-gif {
	left: 50%;
  	margin-left: -32px;
  	margin-top: -32px;
  	position: absolute;
  	top: 50%;
  	width: 4%;
}
.loading-gif img {
	width: 45%;
}
.loading-text {
	left: 47%;
  	margin-left: -32px;
  	margin-top: -32px;
  	position: absolute;
  	top: 55%;
}
.avis-metodo-envio {
	margin-top: 2%;
	text-align: center;
	color: #373737;
	padding: 1% 0 0 0;
}
.avis-metodo-envio span:before {
	color: #ff9f59;
	font-size: 15px;
	margin-right: 6px;
}
[id^="select2-procedimentId-result-"] {
	padding: 6px 20px;
}
.select2-results__group {
	font-size: 14px !important;
}
.unselectable {
    background-color: #ddd;
    cursor: not-allowed;
}

</style>
<script type="text/javascript">



var interessatsTipus = new Array();
var interessatTipusOptions = "";
<c:forEach items="${interessatTipus}" var="it" varStatus="status">
   	interessatTipusOptions = interessatTipusOptions + "<option value=${it.value}" + (${status.index == 0} ? " selected='selected'" : "") + "><spring:message code='${it.text}'/></option>";
</c:forEach>

$(document).ready(function() {
	
	
	$(document).on('change','select.paisos', function() {
		var provincia = $(this).closest("#entregaPostal").find("select[class*='provincies']");
		var poblacioSelect = $(this).closest("#entregaPostal").find("div[class*='poblacioSelect']");
		var poblacioText = $(this).closest("#entregaPostal").find("div[class*='poblacioText']");
		if ($(this).val() != 'ES') {
			$(poblacioSelect).addClass('hidden');
			$(poblacioText).removeClass('hidden');
			$(provincia).prop('disabled', 'disabled');
			$(provincia).parent().parent().addClass('hidden');
		} else {
			$(poblacioSelect).removeClass('hidden');
			$(poblacioText).addClass('hidden');
			$(provincia).removeAttr('disabled');
			$(provincia).parent().parent().removeClass('hidden');
		}
	});
	
	var tipusDocumentDefault = $('#tipusDocumentDefault').val();
	var tipusDocumentSelected = $('#tipusDocumentSelected').val();
	$('.customSelect').webutilInputSelect2(null);

	if (tipusDocumentSelected != '') {
		$(".customSelect").val(tipusDocumentSelected).trigger("change");
	} else if (tipusDocumentDefault != '') {
		$(".customSelect").val(tipusDocumentDefault).trigger("change");
		
		if (tipusDocumentDefault == 'CSV') {
			$('#documentArxiuCsv').val("${nomDocument}");
		} else if (tipusDocumentDefault == 'UUID') {
			$('#documentArxiuUuid').val("${nomDocument}");
		} else if (tipusDocumentDefault == 'URL') {
			$('#documentArxiuUrl').val("${nomDocument}");
		}
	}

	$(document).on('change','select.enviamentTipus', function() {
		var dadesNormalitzat = $(this).closest("#entregaPostal").find("div[class*='normalitzat']");
		var dadesSenseNormalitzar = $(this).closest("#entregaPostal").find("div[class*='senseNormalitzar']");
		
		if ($(this).val() == 'SENSE_NORMALITZAR') {
			$(dadesNormalitzat).addClass('hidden');
			$(dadesSenseNormalitzar).removeClass('hidden');
		} else {
			$(dadesNormalitzat).removeClass('hidden');
			$(dadesSenseNormalitzar).addClass('hidden');
		}
	});
	
	var numPlus = 1;
	
    $(".container-envios").find('.enviamentsForm').each(function() {
    	if($(this).find('.eliminar_enviament').attr('id') != 'enviamentDelete_0') {
    		$(this).find('.eliminar_enviament').removeClass('hidden');
    	}
    });

//     $( "#form" ).submit(function( event ) {
//       $("#organGestor").prop("disabled", false);
//       return true;
//     });
    $('#tipusDocument').on('change', function() {
        if ($(this).val() == 'CSV') {
            $('#metadades').removeClass('hidden');
            $('#input-origen-csv').removeClass('hidden');
            $('#input-origen-uuid').addClass('hidden');
            $('#documentArxiuUuid').val('');
            $('#input-origen-url').addClass('hidden');
            $('#documentArxiuUrl').val('');
            $('#input-origen-arxiu').addClass('hidden');
            $('#arxiu').val('');
        } else if ($(this).val() == 'UUID') {
        	$('#input-origen-csv').addClass('hidden');
        	$('#documentArxiuCsv').val('');
            $('#input-origen-uuid').removeClass('hidden');
            $('#input-origen-url').addClass('hidden');
            $('#documentArxiuUrl').val('');
            $('#input-origen-arxiu').addClass('hidden');
            $('#arxiu').val('');
    	} else if ($(this).val() == 'URL') {
    		$('#input-origen-csv').addClass('hidden');
    		$('#documentArxiuCsv').val('');
            $('#input-origen-uuid').addClass('hidden');
            $('#documentArxiuUuid').val('');
            $('#input-origen-url').removeClass('hidden');
            $('#input-origen-arxiu').addClass('hidden');
            $('#arxiu').val('');
    	} else if ($(this).val() == 'ARXIU'){
    		$('#input-origen-csv').addClass('hidden');
    		$('#documentArxiuCsv').val('');
            $('#input-origen-uuid').addClass('hidden');
            $('#documentArxiuUuid').val('');
            $('#input-origen-url').addClass('hidden');
            $('#documentArxiuUrl').val('');
            $('#input-origen-arxiu').removeClass('hidden');
        }
        webutilModalAdjustHeight();
    });

    var agrupable = $("#procedimentId").children(":selected").attr("class");
    var procedimentId = $("#procedimentId").children(":selected").attr("value");

    $('#organGestor').on('change', function() {
    	//### seleccionat per defecte si només hi ha un (empty + òrgan)
    	if ($('#organGestor').children('option').length == 2) {
    		$('#organGestor option:eq(1)').attr('selected', 'selected');
    		$('#organGestor').trigger('change.select2');
    	}else if($('#organGestor').children('option').length == 3){
    		$('#organGestor > option').each(function() {
    			if(this.value == $('#entitatDir3Codi').val())
    				$('#organGestor option:eq(1)').attr('selected', 'selected');
        			$('#organGestor').trigger('change.select2');
    		});
    	}
    	
    	var organ = $(this).val();
    	if (organ == undefined || organ == "") {
			organ = "-";
        }
    	if ($('#organGestor').children('option').length > 1) {
    		$.ajax({
    			type: 'GET',
    			url: "<c:url value="/notificacio/organ/"/>" + organ + "/procediments",
    			success: function(data) {
    				var select2Options = {
    						theme: 'bootstrap',
    						width: 'auto'};
    				// Procediments
    				var procediments = data;
    				var selProcediments = $("#procedimentId");
    				selProcediments.empty();
    				if (procediments && procediments.length > 0) {
    					selProcediments.append("<option value=\"\"><spring:message code='notificacio.form.camp.procediment.select'/></option>");
    					var procedimentsComuns = [];
    					var procedimentsOrgan = [];
    					$.each(data, function(i, val) {
    						if(val.comu) {
    							procedimentsComuns.push(val);
    						} else {
    							procedimentsOrgan.push(val);
    						}
    					});
    					if (procedimentsComuns.length > 0) {
    						selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.comuns'/>'>");
    							$.each(procedimentsComuns, function(index, val) {
    								selProcediments.append("<option value=\"" + val.id + "\">" + val.codi +' - '+ val.nom + "</option>");
    							});
    						selProcediments.append("</optgroup>");
    					}
    					var isOnlyOneProcedimentOrgan = (procedimentsOrgan.length < 2);
    					if (procedimentsOrgan.length > 0) {
    						selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.organs'/>'>");
    							$.each(procedimentsOrgan, function(index, val) {
    								if (isOnlyOneProcedimentOrgan) {
    									selProcediments.append("<option value='" + val.id + "' selected>" + val.codi +' - '+ val.nom + "</option>");
    									$("#organGestor").val(val.organGestor).trigger("change.select2");
    								} else {
    									selProcediments.append("<option value='" + val.id + "'>" + val.codi +' - '+ val.nom + "</option>");
    								}
    							});
    						selProcediments.append("</optgroup>");
    						selProcediments.trigger('change.select2');
    					}
    					if (selProcediments.children('option').length == 2) {
    			    		$('#procedimentId option:eq(1)').attr('selected', 'selected');
    			    		selProcediments.trigger('change');
    			    	}
    				} else {
    					selProcediments.append("<option value=\"\"><spring:message code='notificacio.form.camp.procediment.buit'/></option>");
    				}
    				selProcediments.select2(select2Options);
    			},
    			error: function() {
    				console.log("error obtenint els procediments de l'òrgan gestor...");
    			}
    		});
    		
    	}
    	
    });
    $('#procedimentId').on('change', function() {
		var procediment = $(this).val();
		if (procediment == '') {
			$("#organGestor").prop("disabled", false);
		} else {
			$.ajax({
				type: 'GET',
				url: "<c:url value="/notificacio/procediment/"/>" + procediment + "/dades",
				success: function(data) {
					var select2Options = {
							theme: 'bootstrap',
							width: 'auto'};
					// Òrgan gestor
					if (!data.comu) {
						$("#organGestor").val(data.organCodi).trigger("change.select2");
					} else if (data.organsDisponibles.length) {
						if (data.organsDisponibles.length == 1) {
							$("#organGestor").val(data.organsDisponibles[0]).trigger("change.select2");
						}
					}
					// Caducitat
					$("#caducitat").val(data.caducitat);
					// Retard
					$("#retard").val(data.retard);
					// Grups
					var grups = data.grups;
					var selGrups = $("#grupId");
					selGrups.empty();
					selGrups.append("<option value=\"\"></option>");
					if (data.agrupable && grups && grups.length > 0) {
						$.each(grups, function(i, val) {
							selGrups.append("<option value=\"" + val.id + "\">" + val.nom + "</option>");
						});
						selGrups.select2(select2Options);
						$("#grups").removeClass("hidden");
					} else {
						$("#grups").addClass("hidden");
					}
					// TODO: Afegir formats de fulla i sobre
					// Format fulla
// 					var selFormatFulla = $("#grupId");
// 					selFormatFulla.empty();
// 					selFormatFulla.append("<option value=\"\"></option>");
// 					var formatsFulla = data.formatsFulla;
// 					if (grups && grups.ength > 0) {
// 						$.each(grups, function(i, val) {
// 							selGrups.append("<option value=\"" + val.id + "\">" + val.nom + "</option>");
// 						});
// 					}
// 					selGrups.select2(select2Options);
<%--
					// Format sobre
					<div class="col-md-3 formatFulla">
						<c:choose>
							<c:when test="${not empty formatsFulla}">
								<not:inputSelect name="enviaments[${j}].entregaPostal.formatFulla" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatfulla" optionItems="${formatsFulla}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
							</c:when>
							<c:otherwise>
								<not:inputText name="enviaments[${j}].entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" labelClass="labelcss" inputClass="inputcss"/>
							</c:otherwise>
						</c:choose>
						</div>
						<div class="col-md-3 formatSobre">
						<c:choose>
							<c:when test="${not empty formatsSobre}">
								<not:inputSelect name="enviaments[${j}].entregaPostal.formatSobre" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatsobre" optionItems="${formatsSobre}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
							</c:when>
							<c:otherwise>
								<not:inputText name="enviaments[${j}].entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" labelClass="labelcss" inputClass="inputcss"/>
							</c:otherwise>
						</c:choose>	
					</div>					
--%>
				},
				error: function() {
					console.log("error obtenint la informació del procediment...");
				}
			});
		}	
    });
    
    $('#organGestor').trigger('change');
    //Add metadata
    var count = 0;
    $('#add').on('click', function () {
        //Input to add
        var metadataInput =
            "<div class='form-group'>" +
                "<label class='control-label col-xs-2'></label>" +
                "<div class='col-xs-10'>" +
                    "<div class='input-group'>" +
                    "<input name='document.metadadesKeys' id='document.metadadesKeys' type='text' class='form-control width50 add grupKey_" + count + "' readonly/>" +
                    "<input name='document.metadadesValues' id='document.metadadesValues' type='text' class='form-control width50 add grupVal_" + count + "' readonly/>" +
                    "<span class='input-group-addon' id='remove'><span class='fa fa-remove'></span></span>" +
                    "</div>" +
                "</div>" +
            "</div>";
    		
        var keyVal = $(".input-add").children().val();
        var val = $(".input-add").children().eq(1).val();
        if (keyVal != '') {
            $("#list").prepend(metadataInput);
            $(".grupKey_" + count).attr("value", keyVal);
            $(".grupVal_" + count).attr("value", val);
            $("#list").find("#remove").addClass("grupVal_" + count);
            count++;
        }
        webutilModalAdjustHeight();
    });
  	//Eliminar grups
	$(document).on('click', "#remove", function () {
		var grupId = $(this).parent().children().attr('id'); 
		var grupsClass = $(this).attr('class'); 
		var lastClass = grupsClass.split(' ').pop();
		var parentRemove = $("." + lastClass).parent();
		var parentInput = parentRemove.parent();
		var parentDiv = parentInput.parent();
		
		parentDiv.slideUp("normal", function() {
			$(this).remove(); 
			webutilModalAdjustHeight();
		});
			
	});
	$(document).on('change', '.interessat', function() {
		var closest = $(this).closest('.destinatariForm, .personaForm');
		var llinatge1 = closest.find('.llinatge1');
		var llinatge2 = closest.find('.llinatge2');
		var enviamentTipus = $('input[name=enviamentTipus]:checked').val();
		var nif = closest.find('.nif');
		var nifLabel = nif.find('label');
		var dir3codi = closest.find('.dir3Codi');
		var nifLabelText = "<spring:message code='notificacio.form.camp.titular.nif'/>";
		var incapacitat = closest.find('.incapacitat');
		var raoSocial = closest.find('.rao');
		var index = closest.find(".rowId input").val();
		var raoSocialDesc = raoSocial.find('input').val();
		var dir3Desc = closest.find('.codiDir3 input').val();
		if ($(this).val() == 'ADMINISTRACIO') {
			$(llinatge1).addClass('hidden');
			$(llinatge2).addClass('hidden');
			$(dir3codi).removeClass('hidden');
			$(incapacitat).addClass('hidden');
			$(raoSocial).addClass('hidden');
			if(enviamentTipus == 'COMUNICACIO'){
				$(nifLabel).text(nifLabelText);
				$(nif).addClass('hidden');
			}else{
				$(nifLabel).text(nifLabelText + " *");
				$(nif).removeClass('hidden');
				
			}
		} else if ($(this).val() == 'FISICA') {
			$(llinatge1).removeClass('hidden');
			$(llinatge2).removeClass('hidden');
			$(nif).removeClass('hidden');
			$(nifLabel).text(nifLabelText + " *");
			$(dir3codi).addClass('hidden');
			$(incapacitat).removeClass('hidden');
			$(raoSocial).removeClass('hidden');	
		} else {
			$(llinatge1).addClass('hidden');
			$(llinatge2).addClass('hidden');
			$(nif).removeClass('hidden');
			$(dir3codi).addClass('hidden');
			$(nifLabel).text(nifLabelText + " *");
			$(incapacitat).removeClass('hidden');
			$(raoSocial).removeClass('hidden');
		}
		
		if((raoSocialDesc != null && raoSocialDesc != "") && (dir3Desc != null && dir3Desc != "")){
			document.getElementById("searchOrganTit" + index).getElementsByTagName('input')[index].value = dir3Desc+'-'+raoSocialDesc;
			$(dir3codi).find('.help-block').addClass('hidden')
			$(dir3codi).find('.form-group').removeClass('has-error')
		}
		comprovarTitularComuniacio();
		
		
	});
	
	$(document).on('change', 'input[type=radio][name=enviamentTipus]', function (event) {
	    comprovarTitularComuniacio();
		$('.interessat').trigger('change');
		
	});
	
	
	function comprovarTitularComuniacio() {
		var closest = $(this).closest('.destinatariForm, .personaForm');
		var nif = closest.find('.nif');
		var nifLabel = nif.find('label');
		var enviamentTipus = $('input[name=enviamentTipus]:checked').val();
		var nifLabelText = "<spring:message code='notificacio.form.camp.titular.nif'/>";
		if(enviamentTipus == 'COMUNICACIO' && (tipusInteressatTitular == 'JURIDICA' || tipusInteressatTitular == 'FISICA')){
			$('#rowRetard').addClass('hidden');
			$('#rowDataProgramada').addClass('hidden');
			$('#rowCaducitat').addClass('hidden');
		}else{
			$('#rowRetard').removeClass('hidden');
			$('#rowDataProgramada').removeClass('hidden');
			$('#rowCaducitat').removeClass('hidden');
		}
		
		
	}
	
	$(document).on('input', ".titularNif", function () {
		$(this).closest('.enviamentsForm').find('.nifemisor').val($(this).val());
   	});
	
	$('.interessat').trigger('change');
	$('#tipusDocument').trigger('change');
	$('.enviamentTipus').trigger('change');
	
	//Contado descripció
	var fieldDescripcio = $('#descripcio');
	if (fieldDescripcio.val().length != 0) {
		var size = $(fieldDescripcio).val().length;
		$('.textAreaCurrentLength').text(size);
	} else {
		$('.textAreaCurrentLength').text(0);
	};;
	
	$(fieldDescripcio).bind("change paste keyup", function() {
		var size = $(this).val().length;
		$('.textAreaCurrentLength').text(size);
	});
	
	//loading
	$('#form').on("submit", function(){
		$('.loading').fadeIn();
	});
	
	

// 	var organigrama = loadOrganigrama();
// // 	var organigrama =null;
	
	$("#rOrgans").on("dblclick", "tr", function() {
		if(!$(this).hasClass('unselectable')){
			seleccionar($(this)); 
		}
	});
		$("#rOrgans").on("click", ".select", function() {
			if(!$(this).hasClass('unselectable')){
				seleccionar($(this).closest("tr")); 
			}
		});
	
});


	function loadOrganigrama(){
		$.ajax({
			type: 'GET',
			url: "<c:url value="/entitat/organigrama/"/>" + document.getElementById('emisorDir3Codi').value,
			success: function(data) {
				if (Object.keys(data).length > 0) {
// 	 				$.each(data, function(i, item) {
// 	 					list_html += '<tr class="' + (i%2 == 0 ? 'even' : 'odd') + '" data-codi="' + data[i].codigo +'" data-denominacio="' + data[i].denominacion +'"><td>' + data[i].denominacion + '<span class="fa fa-sign-out select pull-right" style="font-size:14px; cursor:pointer; color:#222;"></span></td></tr>';
// 	 				});
// 					$("#organigrama").val(data);
					var t = buscarCodiEnOrganigrama(data);
					$("#organigrama").val(buscarCodiEnOrganigrama(data));
				}else{
					alert("No s'han trobat resultats per aquesta entitat.");
				}
				
				
				
			
			},
			error: function() {
				console.log("error obtenint l'organigrama...");
			}
		});
	}

function addDestinatari(enviament_id) {
	var isMultiple = ${isMultiplesDestinataris};
    var num_enviament = parseInt(enviament_id.substring(enviament_id.indexOf( '[' ) + 1, enviament_id.indexOf( ']' )));
    var num_destinatari = $('div.destenv_' + num_enviament).size();
    
	var destinatari =' \
    <div class="col-md-12 destinatariForm destenv_#num_enviament# personaForm_#num_enviament#_#num_destinatari#"> \
		<div class="col-md-3"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].interessatTipus"><spring:message code="notificacio.form.camp.interessatTipus"/></label> \
				<div class="controls col-xs-12"> \
					<select id="enviaments[#num_enviament#].destinataris[#num_destinatari#].interessatTipus" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].interessatTipus" class="form-control interessat" style="width:100%"> \
						' + interessatTipusOptions + ' \
					</select> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 nif"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].nif"><spring:message code="notificacio.form.camp.titular.nif"/></label> \
				<div class="col-xs-12"> \
					<input id="enviaments[#num_enviament#].destinataris[#num_destinatari#].nif" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].nif" class="form-control " type="text" value=""> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 rao"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].nom"><spring:message code="notificacio.form.camp.titular.nom"/> *</label> \
				<div class="col-xs-12"> \
					<input maxlength="${nomSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].nom" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].nom" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
						<span class="glyphicon glyphicon-info-sign"></span> \
						<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].nom">0</span> \
							<spring:message code="notificacio.form.camp.logitud"/> \
						<span> ${nomSize}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 llinatge1 hidden"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1"><spring:message code="notificacio.form.camp.titular.llinatge1"/> *</label> \
				<div class="col-xs-12"> \
					<input maxlength="${llinatge1Size}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${llinatge1Size}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 llinatge2 hidden"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2"><spring:message code="notificacio.form.camp.titular.llinatge2"/></label> \
				<div class="col-xs-12"> \
					<input maxlength="${llinatge2Size}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${llinatge2Size}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon"><spring:message code="notificacio.form.camp.titular.telefon"/></label> \
				<div class="col-xs-12"> \
					<input maxlength="${telefonSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${telefonSize}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-4"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].email"><spring:message code="notificacio.form.camp.titular.email"/></label> \
				<div class="col-xs-12"> \
					<input maxlength="${emailSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].email" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].email" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].email">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${emailSize}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 dir3Codi"> \
		<div class="form-group"> \
			<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi"><spring:message code="notificacio.form.camp.titular.dir3codi"/></label> \
			<div class="col-xs-12"> \
				<div class="input-group" id="$searchOrgan#num_enviament#" onclick="obrirModalOrganismes(#num_enviament#)"> \
					<input id="searchOrgan#num_enviament#" class="form-control " type="text" value=""> \
					<span class="input-group-addon habilitat">  \
						<a><span class="fa fa-search"></span></a> \
					</span> \
				</div> \
			</div> \
		</div> \
	</div> \
		<div class="col-md-3 hidden"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi"><spring:message code="notificacio.form.camp.titular.dir3codi"/></label> \
				<div class="col-xs-12"> \
					<input id="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi" class="form-control " type="text" value=""> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-2 offset-col-md-2"> \
			<div class="float-right"> \
				<input type="button" class="btn btn-danger btn-group delete" name="destinatarisDelete[#num_enviament#][#num_destinatari#]" onclick="destinatarisDelete(this.id)" id="destinatarisDelete[#num_enviament#][#num_destinatari#]" value="<spring:message code="notificacio.form.boto.eliminar.destinatari"/>"> \
			</div> \
		</div> \
		<div class="col-md-12"> \
			<hr style="border-top: 1px dotted #BBB"> \
		</div> \
	</div>';

	destinatari = replaceAll(destinatari, "#num_enviament#", num_enviament);
	destinatari = replaceAll(destinatari, "#num_destinatari#", num_destinatari);

	$('div.newDestinatari_' + num_enviament).append(destinatari);
	$('#enviaments\\[' + num_enviament + '\\]\\.destinataris\\[' + num_destinatari + '\\]\\.interessatTipus').select2({theme: 'bootstrap', width: 'auto', minimumResultsForSearch: Infinity});

	if (!isMultiple) {
    	$("div[class*=' personaForm_" + num_enviament + "']").closest('div.destinatari').find('.addDestinatari').addClass('hidden');
    }
	$('.interessat').trigger('change');
	
	addContadorAddicionalDestinatari('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].nom');
	addContadorAddicionalDestinatari('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].llinatge1');
	addContadorAddicionalDestinatari('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].llinatge2');
	addContadorAddicionalDestinatari('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].telefon');
	addContadorAddicionalDestinatari('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].email');
}

function replaceAll(string, search, replace) {
	return string.split(search).join(replace);
}
	
function addEnvio() {
    var number;
    var num;
    var numPlus
    var enviamentForm = $(".enviamentsForm").last().clone();

    var classList = enviamentForm.attr('class').split(/\s+/);
    $.each(classList, function(index, classname) {
        if (classname.startsWith('enviamentForm_')) {
            number = classname.substring(14);
            num = parseInt(number);
        }
    });

  	//Titol enviament
    if (num != null) {
    	numPlus = num + 1;
    	enviamentForm.removeClass('enviamentForm_' + num).addClass('enviamentForm_' + numPlus);
    	var badge = enviamentForm.find('.envio\\['+numPlus+'\\]');
    	badge.removeClass('envio[' + numPlus + ']').addClass('envio[' + (numPlus + 1) + ']');
    	badge[0].innerText = "Enviament " + (numPlus + 1);
    	var destinataris = enviamentForm.find('.newDestinatari_' + num);
    	destinataris.removeClass('newDestinatari_' + num).addClass('newDestinatari_' + numPlus);
    	destinataris.empty();
	    enviamentForm.find(':input').each(function() {
	        this.name= this.name.replace(number,numPlus);
	        this.id= this.id.replace(number,numPlus);
	        this.value = this.value.replace(number,numPlus);
			$(this).attr('data-select2-id', numPlus);
			
	        if($(this).attr("id") == "envioTooltip") {
	            this.value= this.value.replace(number,numPlus);
	            $(this).tooltip();
	        }
	
	        if($(this).hasClass('formEnviament')) {
	            this.name= this.name.replace("[" + number, "[" + numPlus);
	            this.id= this.id.replace("[" + number, "[" + numPlus);
	        }
	        
	        if ($(this).attr('type') == 'hidden') {
	        	var hiddenId = parseInt($(this).val());
	        	if (typeof hiddenId == 'number') {
	        		$(this).val(''); //remove hidden id (new)
	        	}
	        }
	    });
	    enviamentForm.find('#entregaPostal').removeClass('entregaPostal_' + num).addClass('entregaPostal_' + numPlus);
	    //select
	    $(enviamentForm).find("span.select2").remove();
	    $(enviamentForm).find('p').remove();
	    $(enviamentForm).find('div').removeClass('has-error');
	    $(enviamentForm).find("select").select2({theme: 'bootstrap', width: 'auto'});
	    $(enviamentForm).find("select").attr('data-select2-eval', 'true');
		$(enviamentForm).appendTo(".newEnviament").slideDown("slow").find("input[type='text']").not(".procedimentcodi").val("");
		
		//Remove last button addEnviament
	    if($(enviamentForm).find('.eliminar_enviament').attr('id') != 'entregaPostal[0]') {
			$(enviamentForm).find('.eliminar_enviament').removeClass('hidden');
	    }
		//Show button addDestinatari
		$(enviamentForm).find('.addDestinatari').removeClass('hidden');
	    //Inicialitzar chechbox incapacitat
		$(enviamentForm).find('input:checkbox').removeAttr('checked');
		//Inicialitzar deh
		$(enviamentForm).find('.entregaDeh_'+numPlus).hide();
	    //Inicialitzar entregapostal
		$(enviamentForm).find('.entregaPostal_'+numPlus).hide();
		$(enviamentForm).find('.entregaPostal_info_'+number).css('display','none');
		
		actualitzarEntrega(numPlus);
	    webutilModalAdjustHeight();
	    
	    addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.nom', '${nomSize}');
	    addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.llinatge1', '${llinatge1Size}');
	    addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.llinatge2', '${llinatge2Size}');
	    addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.email', '${emailSize}');
	    addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.telefon', '${telefonSize}');
    }

}

function addContadorAddicionalDestinatari(fieldId) {
	//Contador
	var fieldSize = 'inputCurrentLength_' + fieldId;
	var fieldSizeClass = $(document.getElementsByClassName(fieldSize)[0]);
	if (fieldSizeClass.val() != undefined && fieldSizeClass.val().length != 0) {
		var size = $(fieldId).val().length;
		$(fieldSizeClass).text(size);
	} else {
		$(fieldSizeClass).text(0);
	};
	
	$(document.getElementById(fieldId)).bind("change paste keyup", function() {
		var size = $(this).val().length;
		$(fieldSizeClass).text(size);
	});
}

function addContadorAddicionalEnviament(fieldId, inputMaxLength) {
	var p = '<p class="info-length text-success"> \
				<span class="glyphicon glyphicon-info-sign"></span> \
				<span class="inputCurrentLength_' + fieldId + '">0</span> \
					<spring:message code="notificacio.form.camp.logitud"/> \
				<span> ' + inputMaxLength + '</span> \
			</p>';
	var inputField = $(document.getElementById(fieldId));
	$(p).insertAfter(inputField);
	//Contador
	var fieldSize = 'inputCurrentLength_' + fieldId;
	var fieldSizeClass = $(document.getElementsByClassName(fieldSize)[0]);
	if (fieldSizeClass.val() != undefined && fieldSizeClass.val().length != 0) {
		var size = $(fieldId).val().length;
		$(fieldSizeClass).text(size);
	} else {
		$(fieldSizeClass).text(0);
	};
	
	$(document.getElementById(fieldId)).bind("change paste keyup", function() {
		var size = $(this).val().length;
		$(fieldSizeClass).text(size);
	});
}

function destinatarisDelete(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".destinatariForm");
    var classParent = $(parent).attr('class');
    var destinatari_id_num = parseInt(className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']')));
    var enviament_id_num = parseInt(className.substring(className.indexOf('[') + 1, className.indexOf(']')));
    var destinatariRoot = $(element).closest(".dest");
    var numDest = destinatariRoot.find(".destinatariForm").size();
    
	$(parent).closest(".destinatari").find('.addDestinatari').removeClass('hidden');
	$(parent).remove();

	// Reanomenar destinataris posteriors
	if (numDest > (destinatari_id_num + 1)) {
		for(var i = (destinatari_id_num + 1); i < numDest; i++) {
			reanumeraDestinatari($(destinatariRoot).find('.destinatariForm:nth-child(' + i + ')'), i);
		}
	}
}

function enviamentDelete(id) {
    var element = document.getElementById(id);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');
    var enviament_id_num = parseInt(id.substring(16));
	var enviamentRoot = $(element).closest(".newEnviament");
	var numEnv = enviamentRoot.find(".enviamentsForm").size();
	 
    $(parent).remove();

    // Reenumerar enviaments posteriors
    if (numEnv > (enviament_id_num + 1)) {
		for(var i = (enviament_id_num + 1); i < numEnv; i++) {
			reanumeraEnviament($(enviamentRoot).find('.enviamentsForm:nth-child(' + i + ')'), i);
		}
	}
}

function reanumeraDestinatari(destinatari, index) {
	var nouIndex = index - 1;

	var classList = destinatari.attr('class').split(/\s+/);
    $.each(classList, function(index, classname) {
        if (classname.startsWith('personaForm_')) {
        	destinatari.removeClass(classname).addClass(classname.substring(0, classname.lastIndexOf('_') + 1) + nouIndex);
        }
    });
    
	destinatari.find(':input').each(function() {
        this.name= this.name.replace('destinataris[' + index,'destinataris[' +nouIndex);
        this.id= this.id.replace('destinataris[' + index,'destinataris[' + nouIndex);
		
        if($(this).hasClass('formEnviament')) {
            this.name= this.name.replace("[" + index, "[" + nouIndex);
            this.id= this.id.replace("[" + index, "[" + nouIndex);
        }
        if($(this).hasClass('delete')) {
            this.name= this.name.substring(0, this.name.lastIndexOf('[') + 1) + nouIndex + ']';
            this.id= this.id.substring(0, this.id.lastIndexOf('[') + 1) + nouIndex + ']';
        }
    });
}

function reanumeraEnviament(enviament, index) {
   	var nouIndex = index - 1;
   	enviament.removeClass('enviamentForm_' + index).addClass('enviamentForm_' + nouIndex);
   	var badge = enviament.find('.envio\\[' + (index + 1) + '\\]');
   	badge.removeClass('envio[' + (index + 1) + ']').addClass('envio[' + index + ']');
   	badge[0].innerText = "Enviament " + index;
   	var destinataris = enviament.find('.newDestinatari_' + index);
   	destinataris.removeClass('newDestinatari_' + index).addClass('newDestinatari_' + nouIndex);
   	destinataris.find('.destinatariForm').each(function(index) {
		$(this).removeClass('destenv_' + index).addClass('destenv_' + nouIndex);
		var classList = $(this).attr('class').split(/\s+/);
	    $.each(classList, function(index, classname) {
	        if (classname.startsWith('personaForm_')) {
	            $(this).removeClass(classname).addClass('personaForm_' + nouIndex + classname.substring(classname.lastIndexOf('_'), classname.length));
	        }
	    });
   	});
    enviament.find('#entregaPostal').removeClass('entregaPostal_' + index).addClass('entregaPostal_' + nouIndex);
   	enviament.find(':input').each(function() {
        this.name= this.name.replace('enviaments[' + index,'enviaments[' +nouIndex);
        this.id= this.id.replace('enviaments[' + index,'enviaments[' + nouIndex);
		if ($(this).attr('data--id') == index) {
			$(this).attr('data-select2-id', nouIndex);
		}
		
        if($(this).hasClass('formEnviament')) {
            this.name= this.name.replace("[" + index, "[" + nouIndex);
            this.id= this.id.replace("[" + index, "[" + nouIndex);
        }
        if($(this).hasClass('delete')) {
            this.name= this.name.replace("destinatarisDelete[" + index, "destinatarisDelete[" + nouIndex);
            this.id= this.id.replace("destinatarisDelete[" + index, "destinatarisDelete[" + nouIndex);
        }
        if($(this).hasClass('eliminar_enviament')) {
            this.name= this.name.replace("enviamentDelete[" + index, "enviamentDelete[" + nouIndex);
            this.id= this.id.replace("enviamentDelete_" + index, "enviamentDelete_" + nouIndex);
        }
    });
	
//     if($(enviament).find('.eliminar_enviament').attr('id') != 'entregaPostal[0]') {
// 		$(enviament).find('.eliminar_enviament').removeClass('hidden');
//     }
// 	actualitzarEntrega(nouIndex);
}

function mostrarEntregaPostal(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');
    var concepteLength = $('#concepte').val().length;
    
    var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));
    
    if ($(element).is(':checked') && concepteLength > 50) {
    	var longitidInfo = $('.entregaPostal_info_' + enviament_id_num);
    	$(longitidInfo).slideDown(1000); 
    };
    
    if($('.entregaPostal_'+enviament_id_num).css('display') != 'none') {
        $('.entregaPostal_'+enviament_id_num).hide();
    } else {
        $('.entregaPostal_'+enviament_id_num).show();
    }
}

function mostrarDestinatari(enviament_id) {
    var number;
    var num;
    var enviament_id_num = enviament_id.substring(enviament_id.indexOf( '[' ) + 1, enviament_id.indexOf( ']' ));
    enviament_id_num = parseInt(enviament_id_num);
//     var isMultiple = ($("div[class*=' personaForm_" + enviament_id_num + "']").find('#isMultiple').val() == 'true');
    var isMultiple = ${isMultiplesDestinataris};
    
    if ($("div[class*=' personaForm_" + enviament_id_num + "']").hasClass("hidden")) {
        $("div[class*=' personaForm_" + enviament_id_num + "']").removeClass("hidden").show();
        
        if (!isMultiple) {
        	$("div[class*=' personaForm_" + enviament_id_num + "']").closest('div.destinatari').find('.addDestinatari').addClass('hidden');
        }
    }
}


function obrirModalOrganismes(index){
	$("#organismesModal").modal();
// 	$("#indexTitular").val(index);
	$("#titular").val(index);
// 	var selOrganismes = $('#selOrganismes');
	webutilModalAdjustHeight();
// 	selOrganismes.append("<option value=\"\"></option>");
	
	loadNivellsAdministracions();
 	loadComunitatsAutonomes();
	
	$(".loading-screen").hide();
	loadOrganigrama();
	netejar();
	
};

// function searchCodiChange(text){
// 	var searchNom = $('#searchNom');
// 	if(text.trim().length ==0){
// 		searchNom.removeAttr('disabled');
// 	}else{
// 		searchNom.prop("disabled", true);
// 	}
	
// };

// function searchNomChange(text){
// 	var searchCodi = $('#searchCodi');
// 	if(text.trim().length ==0){
// 		searchCodi.removeAttr('disabled');
// 	}else{
// 		searchCodi.prop("disabled", true);
// 	}
	
// };

function loadNivellsAdministracions() {
	$.ajax({
		type: 'GET',
		url: '${urlNivellAdministracions}',
		dataType: 'json',
		async: false,
		data: {	}
	}).done(function(data){
		var list_html = '<option value=""></option>';
		if (data.length > 0) {
			$.each(data, function(i, item) {
				list_html += '<option value=' + data[i].codi + '>' + data[i].valor + '</option>';
			});
		}
		$("#o_nivellAdmin").html(list_html);
// 		$("#o_nivellAdmin").select2({
// 			enable : true,
// 			allowClear : true,
// 			dropdownParent: $("#dialeg_organs")
// 		});
		
		$("#o_nivellAdmin").select2({
			theme: 'bootstrap',
			width: 'auto'
		});
		
		
		
	}).fail(function(jqXHR, textStatus) {
// 		refreshAlertes();
	});
}

function loadComunitatsAutonomes() {
	$.ajax({
		type: 'GET',
		url: '${urlComunitatsAutonomes}',
		dataType: 'json',
		async: false,
		data: {	}
	}).done(function(data){
		var list_html = '<option value=""></option>';
		if (data.length > 0) {
			$.each(data, function(i, item) {
				list_html += '<option value=' + data[i].codi + '>' + data[i].valor + '</option>';
			});
		}
		$("#o_comunitat").html(list_html);
		$("#o_comunitat").select2({
			theme: 'bootstrap',
			width: 'auto'
		});
	}).fail(function(jqXHR, textStatus) {
// 		refreshAlertes();
	});
	
}

function loadProvincies(codiCA) {
	if (codiCA != null && codiCA != '') {
		mbloquejar();
		$.ajax({
			type: 'GET',
			url: '${urlProvincies}/' + codiCA ,
			dataType: 'json',
			async: false		
		}).done(function(data){
			var list_html = '<option value=""></option>';
			if (data.length > 0) {
				$.each(data, function(i, item) {
					list_html += '<option value=' + data[i].id + '>' + data[i].descripcio + '</option>';
				});
			}
			$("#o_provincia").html(list_html);
			$("#o_provincia").select2({
				theme: 'bootstrap',
				width: 'auto'
			});
		}).fail(function(jqXHR, textStatus) {
// 			refreshAlertes();
		});
		mdesbloquejar();
	} else {
		var list_html = '<option value=""></option>';
		$("#o_provincia").html(list_html);
		$("#o_provincia").select2({
			theme: 'bootstrap',
			width: 'auto'
		});
	}
	
	
}

function comunitatAutonomaChange(value){
	if(value.trim().length !=0){
		loadProvincies(value);
	}
};

function provinciesChange(value){
	if(value.trim().length !=0){
		loadLocalitats(value);
	}
};

function loadLocalitats(codiProvincia) {
	if (codiProvincia != null && codiProvincia != '') {
		mbloquejar();
		$.ajax({
			type: 'GET',
			url: '${urlLocalitats}/' + codiProvincia,
			dataType: 'json',
			async: false
		}).done(function(data){
			var list_html = '<option value=""></option>';
			if (data.length > 0) {
				$.each(data, function(i, item) {
					list_html += '<option value=' + data[i].id + '>' + data[i].descripcio + '</option>';
				});
			}
			$("#o_localitat").html(list_html);
			$("#o_localitat").select2({
				theme: 'bootstrap',
				width: 'auto'
			});
		}).fail(function(jqXHR, textStatus) {
// 			refreshAlertes();
		});
		mdesbloquejar();
	} else {
		var list_html = '<option value=""></option>';
		$("#o_localitat").html(list_html);
		$("#o_localitat").select2({
			theme: 'bootstrap',
			width: 'auto'
		});
	}
}

function mbloquejar() {
// 	var height = $("#dialeg_organs").css('height');
	var width = $("#dialeg_organs").css('width');
	var top = $("#dialeg_organs").css('top');
// 	$(".mloading-screen").css('height', height);
	$(".mloading-screen").css('width', width);
	$(".mloading-screen").css('top', top);
	$(".mloading-screen").show();
}

function mdesbloquejar() {
	$(".mloading-screen").hide();
}

function netejar() {
	$("#o_provincia").val("");
	$("#o_provincia").html("");
	$("#o_localitat").val("");
	$("#o_localitat").html("");
	$("#o_codi").val("");
	$("#o_denominacio").val("");
	$("#rOrgans").html('');	
}

function seleccionar(fila){
	var from = $('#titular').val().split('-')[0];
	var index = $('#titular').val().split('-')[1] != undefined?$('#titular').val().split('-')[1]:from;
	var dir3Codi;
	var raoSocial;
	var dir3CodiDesc;
// 	var organSelect = document.getElementById('selOrganismes');
	if(fila.size()>0){
// 		var organSeleccionatValue = organSelect.options[organSelect.selectedIndex].value;
// 		var organSeleccionatText = organSelect.options[organSelect.selectedIndex].text;
		
		let codi = fila.data('codi');
		let denominacio = fila.data('denominacio');
		let ocodi = codi + '-' + denominacio;
		
		if(from == 'Tit'){
			dir3Codi = document.getElementById("enviaments[" + index + "].titular.dir3Codi");
			raoSocial = document.getElementById("enviaments[" + index + "].titular.nom");
			dir3CodiDesc =  document.getElementById("searchOrganTit" + index).getElementsByTagName('input')[0];
		}else{
			dir3Codi = document.getElementById("enviaments[" + index + "].destinataris[" + index + "].dir3Codi");
			raoSocial = document.getElementById("enviaments[" + index + "].destinataris[" + index + "].nom");
			dir3CodiDesc =  document.getElementById("searchOrgan" + index);
		}
		
		dir3Codi.value = codi;
		raoSocial.value = denominacio;	
		dir3CodiDesc.value = ocodi;	
		$('#cerrarModal').click();
	}
};

// function netejarFiltre(){
// 	var searchCodi = $('#searchCodi');
// 	var searchNom = $('#searchNom');
// 	var selOrganismes = $('#selOrganismes');
	
// 	searchCodi.removeAttr('disabled');
// 	searchCodi.val('');
// 	searchNom.removeAttr('disabled');
// 	searchNom.val('');
	
// 	selOrganismes.empty();
// 	selOrganismes.append("<option value=\"\"></option>");

// };


function buscarCodiEnOrganigrama(fills){
	var array = new Array();
	if(fills != null && fills != undefined){
		$.each(fills, function(key, obj) { 
			array.push(key);
			if(obj.fills != undefined && obj.fills != null){
				$.each(obj.fills, function(key, obj) { 
					buscarCodiEnOrganigrama(obj.fills);		
				});	
			}	
		});
		
	}
	return array; 
}

function loadOrgansGestors(){
	var codi = $("#o_codi").val();
	var denominacio = $("#o_denominacio").val();
	var nivellAdmin = $("#o_nivellAdmin").val();
	var codiComunitat = $('#o_comunitat').val();
	var codiProvincia = $('#o_provincia').val()!=null?$('#o_provincia').val():'';
	var codiLocalitat = $("#o_localitat").val()!=null?$('#o_localitat').val():'';

	
	if ((codi == null || codi == "") &&
			(denominacio == null || denominacio == "") &&
			(codiComunitat == null || codiComunitat == "")) {
		alert("És obligatori indicar com a mínimm el codi, la denominació o un lloc per a fer la cerca");
		return false;
	} else {
		mbloquejar()
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/cercaUnitats"/>" + 
				'?codi='+codi.trim()+
				'&denominacio='+denominacio+
				'&nivellAdministracio='+nivellAdmin+
				'&comunitatAutonoma='+codiComunitat+
				'&provincia='+codiProvincia+
				'&municipi='+codiLocalitat,
			success: function(data) {
				var list_html = '';
				if (data.length > 0) {
					$.each(data, function(i, item) {
						var enviamentTipus = $('input[name=enviamentTipus]:checked').val();
						var sir = $('#organigrama').val().indexOf(data[i].codi);
						var clase = null;
						var claseBoto = 'select btn btn-success';
						var socSir = (sir!=-1?'<spring:message code="comu.no"/>':'<spring:message code="comu.si"/>');
						
// 						if(enviamentTipus == 'NOTIFICACIO' && sir!=-1 ){
// 							clase = 'unselectable';
// 							claseBoto = 'unselectable select btn btn-success';
// 						}else if(enviamentTipus == 'COMUNICACIO' && sir==-1 ){
						if(enviamentTipus == 'COMUNICACIO' && sir!=-1 ){
							clase = 'unselectable';
							claseBoto = 'unselectable select btn btn-success';
						}else{
							clase = (i%2 == 0 ? 'even' : 'odd');
						}
						
						list_html += '<tr class="'+clase+'" data-codi="' + data[i].codi +'" data-denominacio="' + data[i].nom +'"><td width="85%">' + data[i].nom + 
						'</td><td>'+(socSir)+'</td><td><button type="button" class="'+claseBoto+'"> <spring:message code="comu.boto.seleccionar"/></button</td></tr>';
						
						
// 						list_html += '<tr class="'+clase+'" data-codi="' + data[i].codi +'" data-denominacio="' + data[i].nom +'"><td width="85%">' + data[i].nom + 
// 						'</td><td>'+socSir+'</td><td><button type="button" class="'+clase+'" select btn btn-success"> <spring:message code="comu.boto.seleccionar"/></button</td></tr>';
						
						
// 						if($('#organigrama').val().indexOf(data[i].codi) != -1 ){
// 							list_html += '<tr class="'+clase+'" data-codi="' + data[i].codi +'" data-denominacio="' + data[i].nom +'"><td width="85%">' + data[i].nom + 
// 							'</td><td>'+sir!=-1?'Si':'No'+'</td><td><button type="button" class="select btn btn-success"> <spring:message code="comu.boto.seleccionar"/></button</td></tr>';
// 						}else{
// 							list_html += '<tr class="' + clase) + '" data-codi="' + data[i].codi +'" data-denominacio="' + data[i].nom +'"><td width="85%">' + data[i].nom + 
// 							'</td><td>No</td><td><button  type="button" class="select btn btn-success"> <spring:message code="comu.boto.seleccionar"/></button></td></tr>';
// 						}
						
					});
				}else{
					alert("No s'han trobat oficines segons els filtres seleccionats.");
				}
				$("#rOrgans").html(list_html);
				
// 				$('.disabled').prop('disabled', true);
				mdesbloquejar();
			},
			error: function() {
				console.log("error obtenint les administracions...");
				mdesbloquejar();
			}
		});
	}
	
	
};



function mostrarEntregaDeh(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');

    var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));
    if($('.entregaDeh_' + enviament_id_num).css('display') != 'none') {
        $('.entregaDeh_'+enviament_id_num).hide();
    } else {
        $('.entregaDeh_'+enviament_id_num).show();
    }
}

function actualitzarEntrega(j) {
	var selPaisos = document.getElementById("enviaments[" + j + "].entregaPostal.paisCodi");
	var selProvincies = document.getElementById("enviaments[" + j + "].entregaPostal.provincia");
	var selLocalitats = document.getElementById("enviaments[" + j + "].entregaPostal.municipiCodi");
	var selPoblacio =  document.getElementById("enviaments[" + j + "].entregaPostal.poblacio");
	
	$.ajax({
		type: 'GET',
		url: "<c:url value="/notificacio/paisos/"/>",
		success: function(data) {
			$(selPaisos).empty();
			$(selPaisos).append("<option value=\"\"></option>");
			if (data && data.length > 0) {
				$.each(data, function(i, val) {
					if (val.alfa2Pais == 'ES') {
						$(selPaisos).append("<option value=\"" + val.alfa2Pais + "\" selected>" + val.descripcioPais + "</option>");
					} else {
						$(selPaisos).append("<option value=\"" + val.alfa2Pais + "\">" + val.descripcioPais + "</option>");
					}
					
				});
				var paisCodi = document.getElementsByClassName('enviaments[' + j + '].entregaPostal.paisCodi');
				
				if (paisCodi !== undefined && paisCodi[0] !== undefined) {
					$(selPaisos).val(paisCodi[0].value).change();
				}
			}
			var select2Options = {
					theme: 'bootstrap',
					width: 'auto'};
			$(selPaisos).select2(select2Options);
		},
		error: function() {
			console.log("error obtenint les provincies...");
		}
	});
	//Provincies
	$.ajax({
		type: 'GET',
		url: "<c:url value="/notificacio/provincies/"/>",
		success: function(data) {
			$(selProvincies).empty();
			$(selProvincies).append("<option value=\"\"></option>");
			if (data && data.length > 0) {
				$.each(data, function(i, val) {
					$(selProvincies).append("<option value=\"" + val.id + "\">" + val.descripcio + "</option>");
				});
				
				var provinciaCodi = document.getElementsByClassName('enviaments[' + j + '].entregaPostal.provincia');
				
				if (provinciaCodi !== undefined && provinciaCodi[0] !== undefined) {
					$(selProvincies).val(provinciaCodi[0].value).change();
				}
			}
			var select2Options = {
					theme: 'bootstrap',
					width: 'auto'};
			$(selProvincies).select2(select2Options);
		},
		error: function() {
			console.log("error obtenint les provincies...");
		}
	});

	//Localitats
	$(selProvincies).on('change', function() {
		var provincia = $(this);
		if ($(this).val() == '') {
			$(selLocalitats).find('option').remove();
		} else {
			$.ajax({
				type: 'GET',
				url: "<c:url value="/notificacio/localitats/"/>" + $(provincia).val(),
				success: function(data) {
					$(selLocalitats).empty();
					$(selLocalitats).append("<option value=\"\"></option>");
					if (data && data.length > 0) {
						$.each(data, function(i, val) {
							$(selLocalitats).append("<option value=\"" + val.id + "\">" + val.descripcio + "</option>");
						});
						
						var municipiCodi = document.getElementsByClassName('enviaments[' + j + '].entregaPostal.municipiCodi');
						
						if (municipiCodi !== undefined && municipiCodi[0] !== undefined) {
							$(selLocalitats).val(municipiCodi[0].value).change();
						}
					}
					var select2Options = {
							theme: 'bootstrap',
							width: 'auto'};
					$(selLocalitats).select2(select2Options);
				},
				error: function() {
					console.log("error obtenint les provincies...");
				}
			});
		}
	});	
	
}

function comptarCaracters(idCamp) {
	var fieldConcepte = $('#' + idCamp);
	if (fieldConcepte.val().length != 0) {
		var size = $(fieldConcepte).val().length;
		$('.inputCurrentLength').text(size);
	} else {
		$('.inputCurrentLength').text(0);
	};
	
	//$(fieldConcepte).bind("change paste keyup", function() {
	//	var size = $(this).val().length;
	//	$('.inputCurrentLength').text(size);
	//});
}
	
</script>
</head>
<body>
	<div class="loading">
		<div class="loading-gif">
		<img src="<c:url value="/img/ajax-loader.gif"/>"/>
		</div>
		<div class="loading-text">
		<p><spring:message code="notificacio.form.loading"/></p>
		</div>
	</div>
    <c:set var="formAction"><not:modalUrl value="/notificacio/newOrModify"/></c:set>
    <form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="notificacioCommandV2" enctype="multipart/form-data">
    	<input type="hidden" name="id" value="${notificacioCommandV2.id}">
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-address-book"></span>
				<label><spring:message code="notificacio.form.titol.dadesgenerals" /></label>
				<hr/>
			</div>
<%-- 			<form:hidden path="procedimentId" value="${procediment.id}" /> --%>
			<form:hidden path="emisorDir3Codi" id="emisorDir3Codi" value="${entitat.dir3Codi}" />
			
			<!-- CONCEPTE -->
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="concepte" textKey="notificacio.form.camp.concepte" labelSize="2" required="true" showsize="true" inputMaxLength="${concepteSize}"/>
				</div>
			</div>
			
			<!-- DESCRIPCIÓ -->
			<div class="row">
				<div class="col-md-12">
					<not:inputTextarea name="descripcio" textKey="notificacio.form.camp.descripcio" labelSize="2" inputMaxLength="${descripcioSize}"/>
				</div>
			</div>
			
			<!-- ORGAN -->
			<div class="row">
				<div class="col-md-12">
					<not:inputSelect 
						name="organGestor" 
						textKey="notificacio.form.camp.organGestor" 
						required="true" 
						optionItems="${organsGestors}" 
						optionValueAttribute="codi" 
						optionTextAttribute="organGestorDesc"
						labelSize="2" 
						emptyOption="true"
						optionMinimumResultsForSearch="2"
						emptyOptionTextKey="notificacio.form.camp.organ.select"/>
				</div>
			</div>
			<!-- PROCEDIMENT -->
			<div class="row">
				<div class="col-md-12">
					<not:inputSelect 
						name="procedimentId" 
						textKey="notificacio.form.camp.procediment" 
						required="false" 
						optionItems="${procediments}" 
						optionValueAttribute="id" 
						optionTextAttribute="id" 
						labelSize="2"
						emptyOption="true"
						optionMinimumResultsForSearch="2"
						emptyOptionTextKey="notificacio.form.camp.procediment.select"/>
				</div>
			</div>
			
			<!-- GRUP -->
			<div id="grups" class="row <c:if test='${empty grups}'>hidden</c:if>">
				<div class="col-md-12">
					<not:inputSelect name="grupId" textKey="notificacio.form.camp.grup" optionItems="${grups}" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2" />
				</div>
			</div>
			
			
			<!-- TIPUS D'ENVIAMENT -->
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label class="control-label col-xs-4" for="enviamentTipus"><spring:message code="notificacio.form.camp.enviamenttipus" /></label>
						<div class="controls col-xs-8">
							<div class="col-xs-6">
								<form:radiobutton path="enviamentTipus" value="NOTIFICACIO" checked="checked"/>
								<spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.NOTIFICACIO" />
							</div>
							<div class="col-xs-6">
								<form:radiobutton path="enviamentTipus" value="COMUNICACIO" />
								<spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.COMUNICACIO" />
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<!-- DATA ENVIAMENT PROGRAMADA -->
			<div class="row" id="rowDataProgramada">
				<div class="col-md-12">
					<not:inputDate name="enviamentDataProgramada" textKey="notificacio.form.camp.dataProgramada" info="true" messageInfo="notificacio.form.camp.dataProgramada.info" labelSize="2" inputSize="6" />
				</div>
			</div>
			
			<!-- RETARD -->
			<div class="row" id="rowRetard">
				<div class="col-md-12">
					<not:inputText name="retard" textKey="notificacio.form.camp.retard" info="true" messageInfo="notificacio.form.camp.retard.info" value="10" labelSize="2" inputSize="6"/>
				</div>
			</div>
			
			<!-- CADUCITAT -->
			<div class="row" id="rowCaducitat">
				<div class="col-md-12">
					<not:inputDate name="caducitat" textKey="notificacio.form.camp.caducitat" info="true" messageInfo="notificacio.form.camp.caducitat.info" orientacio="bottom" labelSize="2" inputSize="6" required="true" />
				</div>
			</div>
			
			<!-- NÚMERO D'EXPEDIENT -->
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="numExpedient" textKey="notificacio.form.camp.expedient" labelSize="2" inputSize="6" />
				</div>
			</div>
		</div>
		
		<!-- DOCUMENT -->
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-file"></span>
				<label><spring:message code="notificacio.form.titol.document" /></label>
				<hr/>
			</div>
			
			<!-- TIPUS DE DOCUMENT -->
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
						<form:hidden path="tipusDocumentSelected" value="${tipusDocument}"/>
						<div class="controls col-xs-8">
							<form:hidden path="tipusDocumentDefault"/>
							<select id="tipusDocument" name="tipusDocument" class="customSelect">
							<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
								<option value="${enumValue}" <c:if test="${not empty tipusDocument && tipusDocument == enumValue}">selected</c:if>><spring:message code="tipus.document.enum.${enumValue}"/></option>
							</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<input type="hidden" name="document.id" value="${notificacioCommandV2.document.id}">
				<input type="hidden" name="document.arxiuGestdocId" value="${notificacioCommandV2.document.arxiuGestdocId}">
				<input type="hidden" name="document.arxiuNom" value="${notificacioCommandV2.document.arxiuNom}">
				<!-- CSV -->
				<div id="input-origen-csv" class="col-md-6">
					<not:inputText name="documentArxiuCsv" textKey="notificacio.form.camp.csvuuid" labelSize="3" info="true" messageInfo="notificacio.for.camp.document.avis" />
				</div>
				
				<!-- UUID -->
				<div id="input-origen-uuid" class="col-md-6 hidden">
					<not:inputText name="documentArxiuUuid" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
				</div>
				
				<!-- URL -->
				<div id="input-origen-url" class="col-md-6 hidden">
					<not:inputText name="documentArxiuUrl" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
				</div>
				
				<!-- FITXER -->
				<div id="input-origen-arxiu" class="col-md-6 hidden">
					<c:choose>
						<c:when test="${notificacioCommandV2.tipusDocumentDefault == 'ARXIU'}">
							<not:inputFile name="arxiu" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" fileName="${nomDocument}"/>
						</c:when>
						<c:otherwise>
							<not:inputFile name="arxiu" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis"/>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			
			<!--  DOCUMENT NOTMALITZAT -->
			<div class="row">
				<div class="col-md-12">
					<not:inputCheckbox name="document.normalitzat" textKey="notificacio.form.camp.normalitzat" info="true" messageInfo="notificacio.form.camp.normalitzat.info" labelSize="2" />
				</div>
			</div>
		</div>
		
		<!-- ENVIAMENT -->
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-vcard"></span>
				<label><spring:message code="notificacio.form.titol.enviaments" /></label>
				<hr/>
			</div>
			<c:set var="envios" value="${notificacioCommandV2.enviaments}"/>
			<div class="container-envios">
				<div class="newEnviament">
				<c:forEach items="${envios}" var="enviament" varStatus="status">
					<c:set var="j" value="${status.index}" />
					<c:set var="k" value="${status.index + 1}" />
						<div class="row enviamentsForm formEnviament enviamentForm_${j}">
							<div class="col-md-12">
								<label class="envio[${k}] badge badge-light">Enviament ${k}</label>
							</div>
							<div>
							<input type="hidden" name="enviaments[${j}].id" value="${enviament.id}"/>
						
							<!-- TIPUS DE SERVEI -->
							<div class="col-md-6">
								<div class="form-group">
									<label class="control-label col-xs-4" for="enviaments[${j}].serveiTipus"><spring:message code="notificacio.form.camp.serveitipus" /></label>
									<div class="controls col-xs-8">
										<div class="col-xs-6">
											<form:radiobutton path="enviaments[${j}].serveiTipus" value="NORMAL" checked="checked"/>
											<spring:message code="es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto.NORMAL" />
										</div>
										<div class="col-xs-6">
											<form:radiobutton path="enviaments[${j}].serveiTipus" value="URGENT" />
											<spring:message code="es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto.URGENT" />
										</div>
									</div>
								</div>
							</div>
							
							<!-- TITULAR -->
							<div class="titular">
								<div class="col-md-12 title-envios">
									<div class="title-container">
										<label>${titular}</label>
									</div>
									<hr/>
								</div>
<%-- 								<input class="col-md-6 rowId hidden" type="input" id="rowId" value="${j}"/> --%>
								
								<div class="personaForm">
									<div class='rowId'><input class='hidden' value="${j}"/></div>
									<div>
										<input type="hidden" name="enviaments[${j}].titular.id" value="${enviament.titular.id}"/>
										<!--  TIPUS INTERESSAT -->
										<div class="col-md-6 interessatTipus">
											<not:inputSelect name="enviaments[${j}].titular.interessatTipus" generalClass="interessat" textKey="notificacio.form.camp.interessatTipus" labelSize="4" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
										</div>
										
										<!-- NIF -->
										<div class="col-md-6 nif">
											<not:inputText name="enviaments[${j}].titular.nif" generalClass="titularNif" textKey="notificacio.form.camp.titular.nif"/>
										</div>
										
										<!-- NOM / RAÓ SOCIAL -->
										<div class="col-md-6 rao">
											<not:inputText name="enviaments[${j}].titular.nom" textKey="notificacio.form.camp.titular.nom" required="true" inputMaxLength="${nomSize}" showsize="true"/>
										</div>
										
										<!-- PRIMER LLINATGE -->										
										<div class="col-md-6 llinatge1">
											<not:inputText name="enviaments[${j}].titular.llinatge1" textKey="notificacio.form.camp.titular.llinatge1" required="true" inputMaxLength="${llinatge1Size}" showsize="true"/>
										</div>
										
										<!-- SEGON LLINATGE -->
										<div class="col-md-6 llinatge2">
											<not:inputText name="enviaments[${j}].titular.llinatge2" textKey="notificacio.form.camp.titular.llinatge2" inputMaxLength="${llinatge2Size}" showsize="true"/>
										</div>
										
										<!-- EMAIL -->
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].titular.email" textKey="notificacio.form.camp.titular.email" inputMaxLength="${emailSize}" showsize="true"/>
										</div>
										
										<!-- TELÈFON -->
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].titular.telefon" textKey="notificacio.form.camp.titular.telefon" inputMaxLength="${telefonSize}" showsize="true"/>
										</div>
										
										<!-- CODI DIR3 -->
										<div class="col-md-6 dir3Codi hidden">
											<not:inputTextSearch  funcio="obrirModalOrganismes('Tit-${j}')" searchButton="searchOrganTit${j}" textKey="notificacio.form.camp.titular.dir3codi" required="true" readonly="true" value=""/> 
<%-- 											value="${fn:join(enviaments[j].titular.dir3Codi, enviaments[j].titular.nom)} "/> --%>
										</div>
										
										<div class="col-md-6 codiDir3 hidden">
<%-- 											<not:inputTextSearch  funcio="obrirModalOrganismes(${j})" name="enviaments[${j}].titular.dir3Codi" searchButton="searchOrgan" textKey="notificacio.form.camp.titular.dir3codi" required="true"/> --%>
											<not:inputText name="enviaments[${j}].titular.dir3Codi" textKey="notificacio.form.camp.titular.dir3codi" required="true"/>
										</div>
										
										<!-- INCAPACITAT -->
										<c:if test="${isTitularAmbIncapacitat}">
											<div class="col-md-12 incapacitat">
												<not:inputCheckbox name="enviaments[${j}].titular.incapacitat" textKey="notificacio.form.camp.titular.incapacitat" funcio="mostrarDestinatari(this.id)"/>
											</div>
										</c:if>
									</div>
								</div>
							</div>
							
							<!-- DESTINATARIS -->
							<div class="destinatari">
								<div class="col-md-12 title-envios">
									<div class="title-container">
										<label> ${destinatarisTitol} </label>
									</div>
									<hr/>
								</div>
								<div class="newDestinatari_${j} dest">
									<c:if test="${!empty enviament.destinataris}">
										<c:set value="${enviament.destinataris}" var="destinataris"></c:set>
										<c:forEach items="${destinataris}" var="destinatari" varStatus="status">
											<c:set var="i" value="${status.index}" />
											<div class="col-md-12 destinatariForm destenv_${j} personaForm_${j}_${i}">
<%-- 												<input id="isMultiple" class="hidden" value="${isMultiplesDestinataris}"> --%>
													<input type="hidden" name="enviaments[${j}].destinataris[${i}].id" value="${destinatari.id}"/>
													<!-- TIPUS INTERESSAT -->
													<div class="col-md-3">
														<not:inputSelect name="enviaments[${j}].destinataris[${i}].interessatTipus" generalClass="interessat" textKey="notificacio.form.camp.interessatTipus" labelSize="12" inputSize="12" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
													</div>
													<!-- NIF -->
													<div class="col-md-3 nif">
														<not:inputText name="enviaments[${j}].destinataris[${i}].nif" textKey="notificacio.form.camp.titular.nif" labelSize="12" inputSize="12" />
													</div>
													<!-- NOM / RAÓ SOCIAL -->
													<div class="col-md-3">
														<not:inputText name="enviaments[${j}].destinataris[${i}].nom" textKey="notificacio.form.camp.titular.nom" labelSize="12" inputSize="12" required="true" inputMaxLength="${concepteSize}" showsize="true"/>
													</div>
													<!-- PRIMER LLINATGE -->
													<div class="col-md-3 llinatge1">
														<not:inputText name="enviaments[${j}].destinataris[${i}].llinatge1" textKey="notificacio.form.camp.titular.llinatge1" labelSize="12" inputSize="12" required="true"  inputMaxLength="${concepteSize}" showsize="true"/>
													</div>
													<!-- SEGON LLINATGE -->
													<div class="col-md-3 llinatge2">
														<not:inputText name="enviaments[${j}].destinataris[${i}].llinatge2" textKey="notificacio.form.camp.titular.llinatge2" labelSize="12" inputSize="12" inputMaxLength="${concepteSize}" showsize="true"/>
													</div>
													<!-- TELÈFON -->
													<div class="col-md-3">
														<not:inputText name="enviaments[${j}].destinataris[${i}].telefon" textKey="notificacio.form.camp.titular.telefon" labelSize="12" inputSize="12" inputMaxLength="${concepteSize}" showsize="true"/>
													</div>
													<!-- EMAIL -->
													<div class="col-md-4">
														<not:inputText name="enviaments[${j}].destinataris[${i}].email" textKey="notificacio.form.camp.titular.email" labelSize="12" inputSize="12" inputMaxLength="${concepteSize}" showsize="true"/>
													</div>
													<!-- CODI DIR3 -->
													<div class="col-md-3 dir3Codi hidden">
														<not:inputText name="enviaments[${j}].destinataris[${i}].dir3Codi" textKey="notificacio.form.camp.titular.dir3codi" labelSize="12" inputSize="12"/>
													</div>
													<!-- ELIMINAR DESTINATARI -->
													<div class="col-md-2 offset-col-md-2">
														<div class="float-right">
															<input type="button" class="btn btn-danger btn-group delete" name="destinatarisDelete[${j}][${i}]" onclick="destinatarisDelete(this.id)" id="destinatarisDelete[${j}][${i}]" value="<spring:message code="notificacio.form.boto.eliminar.destinatari"/>"/>
														</div>
													</div>
													<div class="col-md-12">
														<hr style="border-top: 1px dotted #BBB">
													</div>
											</div>
										</c:forEach>
									</c:if>
								</div>
									
								<c:set var="addHidden" value="${isMultiplesDestinataris || empty enviament.destinataris}"/>
								<!-- AFEGIR NOU DESTINATARI -->
								<div class="col-md-12">
									<div class="text-left">	
										<input type="button" class="btn btn-default addDestinatari<c:if test="addHidden"> hidden</c:if>" name="enviaments[${j}]" id="enviaments[${j}]" onclick="addDestinatari(this.id)" value="<spring:message code="notificacio.form.boto.nou.destinatari"/>" />
									</div>
								</div>
							
							</div>
							<div class="col-md-12 separacio"></div>
							
							<div class="metodeEntrega">
								<c:choose>
									<c:when test="${ambEntregaDeh || ambEntregaCie}">
										<div class="col-md-12 title-envios">
											<div class="title-container entrega">
												<label> ${metodeEntrega} </label>
											</div>
											<hr/>
										</div>
									</c:when>
									<c:otherwise>
										<div class="avis-metodo-envio col-md-12">
											<p class="comentari"><span class="fa fa-info-circle"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega.info.cap"/></span></p>
										</div>
									</c:otherwise>
								</c:choose>
								<div class="col-md-12">
									<div class="entregaPostal_info_${j} entregaPostalInfo alert alert-info" role="alert">
										<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
									  	<strong><spring:message code="notificacio.form.camp.logitud.info"/></strong>
									</div>
									<c:if test="${ambEntregaDeh || ambEntregaCie}">
										<div>
											<p class="comentari"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega.info"/></p>
										</div>
									</c:if>
									<c:if test="${ambEntregaCie}">
										<not:inputCheckbox name="enviaments[${j}].entregaPostal.activa" textKey="notificacio.form.camp.entregapostal.activa" labelSize="4" funcio="mostrarEntregaPostal(this.id)" />
									</c:if>
								</div>
								<c:set var="entregaPostalActiva" value="${enviament.entregaPostal.activa}"></c:set>
								<!-- ENTREGA POSTAL -->
								<div id="entregaPostal" class="entregaPostal_${j}" <c:if test="${!entregaPostalActiva}">style="display:none"</c:if>>
									<div class="col-md-12">
										<div class="col-md-12">
											<not:inputSelect name="enviaments[${j}].entregaPostal.tipus" generalClass="enviamentTipus" textKey="notificacio.form.camp.entregapostal.tipus" required="true" optionItems="${entregaPostalTipus}" optionValueAttribute="value" optionTextKeyAttribute="text"  labelClass="labelcss" inputClass="inputcss"/>
										</div>	
										<div class="normalitzat">
											<div class="col-md-4">
												<not:inputSelect name="enviaments[${j}].entregaPostal.viaTipus" generalClass="tipusVia" textKey="notificacio.form.camp.entregapostal.tipusvia" labelClass="labelcss" inputClass="inputcss" required="true" />
											</div>
											<div class="col-md-8">
												<not:inputText name="enviaments[${j}].entregaPostal.viaNom" textKey="notificacio.form.camp.entregapostal.vianom" labelClass="labelcss" inputClass="inputcss" required="true" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.apartatCorreus" textKey="notificacio.form.camp.entregapostal.apartatcorreus" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.numeroCasa" textKey="notificacio.form.camp.entregapostal.numerocasa" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.puntKm" textKey="notificacio.form.camp.entregapostal.puntkm" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.portal" textKey="notificacio.form.camp.entregapostal.portal" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.escala" textKey="notificacio.form.camp.entregapostal.escala" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.planta" textKey="notificacio.form.camp.entregapostal.planta" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.porta" textKey="notificacio.form.camp.entregapostal.porta" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.bloc" textKey="notificacio.form.camp.entregapostal.bloc" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.codiPostal" textKey="notificacio.form.camp.entregapostal.codipostal" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<div class="col-md-6">
												<not:inputSelect name="enviaments[${j}].entregaPostal.paisCodi" generalClass="paisos" emptyOption="true" textKey="notificacio.form.camp.entregapostal.paiscodi" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<div class="col-md-6">
												<not:inputSelect name="enviaments[${j}].entregaPostal.provincia" generalClass="provincies" emptyOption="true" textKey="notificacio.form.camp.entregapostal.provincia" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<div class="col-md-6 poblacioSelect">
												<not:inputSelect name="enviaments[${j}].entregaPostal.municipiCodi" generalClass="localitats" emptyOption="true" textKey="notificacio.form.camp.entregapostal.municipi" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<script>
												actualitzarEntrega('${j}');
											</script>
											<div class="col-md-6">
												<not:inputText name="enviaments[${j}].entregaPostal.poblacio" textKey="notificacio.form.camp.entregapostal.poblacio" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<div class="col-md-12">
												<not:inputText name="enviaments[${j}].entregaPostal.complement" textKey="notificacio.form.camp.entregapostal.complement" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-3 formatFulla">
											<c:choose>
												<c:when test="${not empty formatsFulla}">
													<not:inputSelect name="enviaments[${j}].entregaPostal.formatFulla" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatfulla" optionItems="${formatsFulla}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
												</c:when>
												<c:otherwise>
													<not:inputText name="enviaments[${j}].entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" labelClass="labelcss" inputClass="inputcss"/>
												</c:otherwise>
											</c:choose>
											</div>
											<div class="col-md-3 formatSobre">
											<c:choose>
												<c:when test="${not empty formatsSobre}">
													<not:inputSelect name="enviaments[${j}].entregaPostal.formatSobre" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatsobre" optionItems="${formatsSobre}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
												</c:when>
												<c:otherwise>
													<not:inputText name="enviaments[${j}].entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" labelClass="labelcss" inputClass="inputcss"/>
												</c:otherwise>
											</c:choose>	
											</div>
										</div>	
										<div class="senseNormalitzar hidden">
											<div class="col-md-6">
												<not:inputTextarea name="enviaments[${j}].entregaPostal.linea1" textKey="notificacio.form.camp.entregapostal.linea1" required="true"/>
											</div>
											<div class="col-md-6">
												<not:inputTextarea name="enviaments[${j}].entregaPostal.linea2" textKey="notificacio.form.camp.entregapostal.linea2" required="true"/>
											</div>
											<div class="col-md-6">
												<not:inputText name="enviaments[${j}].entregaPostal.codiPostalNorm" textKey="notificacio.form.camp.entregapostal.codipostal" required="true"/>
											</div>
											<p class="comentari col-xs-12 col-xs-offset-"><spring:message code="notificacio.form.camp.entregapostal.linea.info"/></p>
										</div>
									</div>
								</div>
								<input class="enviaments[${j}].entregaPostal.paisCodi hidden" value="${enviament.entregaPostal.paisCodi}"/>
								<input class="enviaments[${j}].entregaPostal.provincia hidden" value="${enviament.entregaPostal.provincia}"/>
								<input class="enviaments[${j}].entregaPostal.municipiCodi hidden" value="${enviament.entregaPostal.municipiCodi}"/>
								<c:if test="${ambEntregaDeh}">
									<c:set var="entregaDehActiva" value="${enviament.entregaDeh.activa}"></c:set>
									<div class="col-md-12">
										<not:inputCheckbox name="enviaments[${j}].entregaDeh.activa" textKey="notificacio.form.camp.entregadeh.activa" labelSize="4" funcio="mostrarEntregaDeh(this.id)" />
									</div>
									<!-- ENTREGA DEH -->
									<div id="entregaDeh" class="entregaDeh_${j}" <c:if test="${!entregaDehActiva}">style="display:none"</c:if>>
										<div class="col-md-12">
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaDeh.emisorNif" generalClass="nifemisor" textKey="notificacio.form.camp.entregadeh.emisorNif" labelClass="labelcss" inputClass="inputcss" readonly="true"/>
											</div>
											<div class="col-md-12">
												<not:inputCheckbox name="enviaments[${j}].entregaDeh.obligat" textKey="notificacio.form.camp.entregadeh.obligat" labelSize="2" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaDeh.procedimentCodi" generalClass="procedimentcodi" value="${procediment.codi}" textKey="notificacio.form.camp.entregadeh.codiprocediment" labelClass="labelcss" inputClass="inputcss" readonly="true"/>
											</div>
										</div>
									</div>
								</c:if>
								<div class="col-md-12 text-right">
									<div class="btn-group">
										<input type="button" class="btn btn-default formEnviament eliminar_enviament hidden" name="enviamentDelete[${j}]" onclick="enviamentDelete(this.id)" id="enviamentDelete_${j}" value="<spring:message code="notificacio.form.boto.eliminar.enviament"/>" />
									</div>
								</div>
							</div>
						</div>
						</div>
				</c:forEach>
				</div>
			</div>
			<div class="text-left">
				<div class="btn-group">
					<input type="button" class="btn btn-default" id="addEnviament" onclick="addEnvio()" value="<spring:message code="notificacio.form.boto.nou.enviament"/>" />
				</div>
			</div>
			<div class="col-md-12">
				<hr>
			</div>
			<div class="text-right col-md-12">
				<div class="btn-group">
					<a href="<c:url value="/notificacio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
				</div>
				<div class="btn-group">
					<button type="submit" class="btn btn-success saveForm">
						<span class="fa fa-paper-plane"></span>
						<spring:message code="comu.boto.enviar.notificacio" />
					</button>
				</div>
			</div>
		</div>
	</form:form>



	<div class="modal fade" id="organismesModal" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="notificacio.form.dir3.cercar.organismes"/></h4>
				</div>
				<div class="modal-body body">
					
				<div id='dialeg_organs' style='padding: 0px;'>
					<input type="hidden" id="titular" value="">
					<input type="hidden" id="organigrama" value="">
					 

					 
					<div class="row margebaix" style="margin-top:20px;">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.codi" /></label>
								<div class="forminput">
									<input type="text" id="o_codi" class="form-control">
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.denominacio" /></label>
								<div class="forminput">
									<input type="text" id="o_denominacio" class="form-control">
								</div>
							</div>
						</div>
					</div>
					<div class="row margebaix">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.nivell.administracio" /></label>
								<div class="forminput">
									<select id="o_nivellAdmin" class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.comunitat.autonoma" /></label>
								<div class="forminput">
									<select id="o_comunitat" onchange="comunitatAutonomaChange(this.value)" class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
					</div>
					<div class="row margebaix">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.provincia" /></label>
								<div class="forminput">
									<select id="o_provincia" onchange="provinciesChange(this.value)"class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.localitat" /></label>
								<div class="forminput">
									<select id="o_localitat" class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
					</div>
					<div class="mloading-screen ocult">
						<span class="fa fa-spin fa-circle-o-notch  fa-4x" style="color: burlywood;margin-top: 10px;"></span>
					</div>
					<div id="results" class="row" style="width: calc(100% - 30px); background-color: white; height: 240px; border: 1px solid #CCC; margin: 15px; overflow-y: scroll"" >
						<table id="tOficines" class="table table-bordered dataTable dinamicTable">
							<thead>
								<tr class="capsalera" style="font-weight: bold;" >
									<td width="85%"><spring:message code="notificacio.form.dir3.cercar.titol" /></td>
									<td><spring:message code="notificacio.form.dir3.cercar.sir" /></td>
									<td></td>
								</tr>
							</thead>
							<tbody id="rOrgans">
							</tbody>
						</table>
					</div>
					
				</div>
				</div>
				
				<div class="modal-footer">
					<button id="btnNetejar" onclick="netejar()" type="submit" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="loadOrgansGestors" onclick="loadOrgansGestors()" name="accio" value="filtrar" type="button" class="btn btn-info"> <spring:message code="comu.boto.filtrar"/></button>
					<button id="cerrarModal" type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>


	

</body> 
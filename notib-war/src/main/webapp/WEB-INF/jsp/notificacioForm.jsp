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
.inputcss {
	width: calc(100% - 175px);
	float: left;
}
.labelcss {
	width: 175px;
	float: left;
}
.select2-container--bootstrap {
	width: 100% !important;
}
.comentari {
	font-size: 12px;
	color: #999;
	margin-bottom: 0px;
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
</style>
<script type="text/javascript">

$(document).ready(function() {
	
	//Paisos
	$.ajax({
		type: 'GET',
		url: "<c:url value="/notificacio/paisos/"/>",
		success: function(data) {
			var selPaisos = $('.paisos');
			selPaisos.empty();
			selPaisos.append("<option value=\"\"></option>");
			if (data && data.length > 0) {
				$.each(data, function(i, val) {
					selPaisos.append("<option value=\"" + val.alfa2Pais + "\">" + val.descripcioPais + "</option>");
				});
			}
			var select2Options = {
					theme: 'bootstrap',
					width: 'auto'};
			selPaisos.select2(select2Options);
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
			var selProvincies = $('.provincies');
			selProvincies.empty();
			selProvincies.append("<option value=\"\"></option>");
			if (data && data.length > 0) {
				$.each(data, function(i, val) {
					selProvincies.append("<option value=\"" + val.id + "\">" + val.descripcio + "</option>");
				});
			}
			var select2Options = {
					theme: 'bootstrap',
					width: 'auto'};
			selProvincies.select2(select2Options);
		},
		error: function() {
			console.log("error obtenint les provincies...");
		}
	});
	
	$(document).on('change','select.provincies', function() {
		var provincia = $(this);
		//Localitats
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/localitats/"/>" + $(provincia).val(),
			success: function(data) {
				debugger
				var selLocalitats = $(provincia).closest("#entregaPostal").find("select[class*='localitats']");
				selLocalitats.empty();
				selLocalitats.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
					$.each(data, function(i, val) {
						selLocalitats.append("<option value=\"" + val.id + "\">" + val.descripcio + "</option>");
					});
				}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selLocalitats.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint les provincies...");
			}
		});
	});
	var tipusDocumentDefault = $('#tipusDocumentDefault').val();
	$('.customSelect').webutilInputSelect2(null);
	if (tipusDocumentDefault != '') {
		$(".customSelect").val(tipusDocumentDefault).trigger("change");
	}
	
    $('.nextForm').click(function(){
        $('.nav-tabs > .active').next('li').find('a').trigger('click');
    });
	
    $('#concepte').on('input',function(e){
    	 $('#extracte').val($(this).val());
    });
	var numPlus = 1;
	//$('.envio\\[0\\]')[0].innerText = "Envio#" + numPlus;
    //var destinatariForm = $('.destinatariForm');
    //destinatariForm.find('input').each(function() {
    //    if($(this).val().length > 0) {
    //        $(destinatariForm).removeClass('hidden');
    //    }
    //});
    $(".container-envios").find('.enviamentsForm').each(function() {
    	if($(this).find('.eliminar_enviament').attr('id') != 'enviamentDelete[0]') {
    		$(this).find('.eliminar_enviament').removeClass('hidden');
    	}
    });
    
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

    var agrupable = $("#procedimentId").children(":selected").attr("class");
    var procedimentId = $("#procedimentId").children(":selected").attr("value");

    $('#procedimentId').on('change', function() {
        var agrupable = $(this).children(":selected").attr("class");
        var procedimentId = $(this).children(":selected").attr("value");
        comprovarGrups(agrupable, procedimentId)
        webutilModalAdjustHeight();
    });

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
		var dir3codi = closest.find('.dir3codi');
		
		if ($(this).val() == 'ADMINISTRACIO') {
			$(llinatge1).addClass('hidden');
			$(llinatge2).addClass('hidden');
			$(dir3codi).removeClass('hidden');
		} else if ($(this).val() == 'FISICA') {
			$(llinatge1).removeClass('hidden');
			$(llinatge2).removeClass('hidden');
			$(dir3codi).addClass('hidden');
		} else {
			$(llinatge1).addClass('hidden');
			$(llinatge2).addClass('hidden');
			$(dir3codi).addClass('hidden');
		}
	});
	$('.interessat').trigger('change');
	$('#tipusDocument').trigger('change');
});

function addDestinatari(enviament_id) {
    var number;
    var num;
    var enviament_id_num = enviament_id.substring(enviament_id.indexOf( '[' ) + 1, enviament_id.indexOf( ']' ));
    enviament_id_num = parseInt(enviament_id_num);
    
    if ($("div[class*=' personaForm_" + enviament_id_num + "']").hasClass("hidden")) {
        $("div[class*=' personaForm_" + enviament_id_num + "']").removeClass("hidden").show();
        $('#isVisible').attr('value', 'true');
    } else {
        var destinatariForm = $("div[class*=' personaForm_" + enviament_id_num + "']").last().clone();
        var enviamentsForm = $('.enviamentsForm');
        
        destinatariForm.find(':input').each(function() {
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
        //$('.newDestinatari_' + enviament_id_num).find('.destinatariForm').each(function() {
		//	var $this = $(this);
			
        //    $this.find("span.select2").remove();
            
            //$(destinatariForm).find("select").select2();
            //Cercar tots els selects i canviar valor
        //    $this.find("select").webutilInputSelect2();
        //    $this.find("select").attr('data-select2-eval', 'true');
        //});
       
		$(destinatariForm).find("span.select2").remove();
        $(destinatariForm).find("select").webutilInputSelect2();
        //$(destinatariForm).find("select").select2();
        $(destinatariForm).find("select").attr('data-select2-eval', 'true');
       
        $(destinatariForm).find('p').remove();
        $(destinatariForm).find('div').removeClass('has-error');
        $(destinatariForm).appendTo('.newDestinatari_'+ enviament_id_num).slideDown("slow").find("input[type='text']").val("");
       
        webutilModalAdjustHeight();
    }
}

function addEnvio() {
    var number;
    var num;
    var enviamentForm = $(".enviamentsForm").last().clone();
    var enviamentFormNou;

    enviamentForm.find(':input').each(function() {
        number = this.name.substring(this.name.indexOf( '[' ) + 1, this.name.indexOf( ']' ));
        num = parseInt(number);
        ++num;
        this.name= this.name.replace(number,num);
        this.id= this.id.replace(number,num);
        this.value = this.value.replace(number,num);
		$(this).attr('data-select2-id', num);
		
        if($(this).attr("id") == "envioTooltip") {
            this.value= this.value.replace(number,num);
            $(this).tooltip();
        }

        if($(this).hasClass('formEnviament')) {
            this.name= this.name.replace("[" + number, "[" + num);
            this.id= this.id.replace("[" + number, "[" + num);
        }
    });
    //select
    $(enviamentForm).find("span.select2").remove();
    
    $(enviamentForm).find("select").webutilInputSelect2();
    $(enviamentForm).find("select").attr('data-select2-eval', 'true');
    
    $(enviamentForm).find('p').remove();
    $(enviamentForm).find('div').removeClass('has-error');
	$(enviamentForm).appendTo(".newEnviament").slideDown("slow").find("input[type='text']").val("");

    if($(enviamentForm).find('.eliminar_enviament').attr('id') != 'enviamentDelete[0]') {
	$(enviamentForm).find('.eliminar_enviament').removeClass('hidden');
    }
    var newDestinatariForm = $('.newDestinatari_' + number + ':last');
	newDestinatariForm.removeClass('newDestinatari_'+number).addClass('newDestinatari_'+num);

    $('.newDestinatari_' + num).children('div').each(function (i) {

        var destinatariForm = $('.personaForm_' + number + '_' + 0 + ':last');
        destinatariForm.removeClass('personaForm_'+number + '_' + 0).addClass('personaForm_' + num + '_' + 0);

        var enviamentForm = $('.enviamentForm_' + number + ':last');
        enviamentForm.removeClass('enviamentForm_' + number).addClass('enviamentForm_' + num);

        var entregaPostal = $('.entregaPostal_'+number + ':last');
		entregaPostal.removeClass('entregaPostal_'+number).addClass('entregaPostal_'+num);
		//Titol enviament
        if (num != null) {
        	var numPlus = num + 1;
       		$('.envio\\['+num+'\\]:last').removeClass('envio[' + num + ']').addClass('envio[' + numPlus + ']');
       		$('.envio\\['+numPlus+'\\]')[0].innerText = "Enviament " + numPlus;
        }
        
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
        $('#isVisible').attr('value', 'false');
        $(parent).find("input[type='text']").val("");
    } else {
        $(parent).remove();
    }
}

function enviamentDelete(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');
    var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));

    $(parent).remove();

}

function mostrarEntregaPostal(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');

    var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));
    if($('.entregaPostal_'+enviament_id_num).css('display') != 'none') {
        $('#entregaPostalAmagat').attr('value', 'false');
        $('.entregaPostal_'+enviament_id_num).hide();
    } else {
        $('#entregaPostalAmagat').attr('value', 'true');
        $('.entregaPostal_'+enviament_id_num).show();
    }
}

</script>
</head>
<body>
    <c:set var="formAction"><not:modalUrl value="/notificacio/newOrModify"/></c:set>
    <form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="notificacioCommandV2" enctype="multipart/form-data">
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-address-book"></span>
				<label><spring:message code="notificacio.form.titol.dadesgenerals" /></label>
				<hr/>
			</div>
			<form:hidden path="procedimentId" value="${procediment.id}" />
			<form:hidden path="emisorDir3Codi" value="${entitat.dir3Codi}" />
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="concepte" textKey="notificacio.form.camp.concepte" labelSize="2" required="true" />
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<not:inputTextarea name="descripcio" textKey="notificacio.form.camp.descripcio" labelSize="2" />
				</div>
			</div>
			<c:if test="${not empty grups}">
				<div class="row">
					<div class="col-md-12">
						<not:inputSelect name="grupId" textKey="notificacio.form.camp.grup" optionItems="${grups}" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2" />
					</div>
				</div>
			</c:if>
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
			<div class="row">
				<div class="col-md-12">
					<not:inputDate name="enviamentDataProgramada" textKey="notificacio.form.camp.dataProgramada" info="true" messageInfo="notificacio.form.camp.dataProgramada.info" labelSize="2" inputSize="6" />
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="retard" textKey="notificacio.form.camp.retard" info="true" messageInfo="notificacio.form.camp.retard.info" value="${procediment.retard}" labelSize="2" inputSize="6"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<not:inputDate name="caducitat" textKey="notificacio.form.camp.caducitat" info="true" messageInfo="notificacio.form.camp.caducitat.info" orientacio="bottom" labelSize="2" inputSize="6" required="true" />
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="numExpedient" textKey="notificacio.form.camp.expedient" labelSize="2" inputSize="6" />
				</div>
			</div>
		</div>
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-file"></span>
				<label><spring:message code="notificacio.form.titol.document" /></label>
				<hr/>
			</div>
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
						<div class="controls col-xs-8">
							<form:hidden path="tipusDocumentDefault"/>
							<select id="tipusDocument" name="tipusDocument" class="customSelect">
							<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
								<option value="${enumValue}" selected><spring:message code="tipus.document.enum.${enumValue}"/></option>
							</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div id="input-origen-csvuuid" class="col-md-6">
					<not:inputText name="documentArxiuUuidCsvUrl" textKey="notificacio.form.camp.csvuuid" labelSize="3" />
				</div>
				<div id="input-origen-arxiu" class="col-md-6 hidden">
					<not:inputFile name="arxiu" textKey="notificacio.form.camp.arxiu" labelSize="3" />
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<not:inputCheckbox name="document.normalitzat" textKey="notificacio.form.camp.normalitzat" info="true" messageInfo="notificacio.form.camp.normalitzat.info" labelSize="2" />
				</div>
			</div>
		</div>
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-vcard"></span>
				<label><spring:message code="notificacio.form.titol.enviaments" /></label>
				<hr/>
			</div>
			<c:choose>
				<c:when test="${not empty enviosGuardats}">
					<c:set value="${enviosGuardats}" var="envios"></c:set>
				</c:when>
				<c:otherwise>
					<c:set value="enviaments" var="envios"></c:set>
				</c:otherwise>
			</c:choose>
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
							<div class="titular">
								<div class="col-md-12 title-envios">
									<div class="title-container">
										<label>${titular}</label>
									</div>
									<hr/>
								</div>
								<div class="personaForm">
									<div>
										<div class="col-md-6">
											<not:inputSelect name="enviaments[${j}].titular.interessatTipus" generalClass="interessat" textKey="notificacio.form.camp.interessatTipus" labelSize="4" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].titular.nif" textKey="notificacio.form.camp.titular.nif" required="true" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].titular.nom" textKey="notificacio.form.camp.titular.nom" required="true" />
										</div>
										<div class="col-md-6 llinatge1">
											<not:inputText name="enviaments[${j}].titular.llinatge1" textKey="notificacio.form.camp.titular.llinatge1" required="true" />
										</div>
										<div class="col-md-6 llinatge2">
											<not:inputText name="enviaments[${j}].titular.llinatge2" textKey="notificacio.form.camp.titular.llinatge2" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].titular.email" textKey="notificacio.form.camp.titular.email" />
										</div>
										<div class="col-md-6">
											<not:inputText name="enviaments[${j}].titular.telefon" textKey="notificacio.form.camp.titular.telefon" />
										</div>
										<div class="col-md-6 dir3codi hidden">
											<not:inputText name="enviaments[${j}].titular.dir3codi" textKey="notificacio.form.camp.titular.dir3codi" />
										</div>
									</div>
								</div>
							</div>
							<div class="destinatari">
								<div class="col-md-12 title-envios">
									<div class="title-container">
										<label> ${destinatarisTitol} </label>
									</div>
									<hr/>
								</div>
								<c:choose>
									<c:when test="${not empty enviosGuardats}">
										<c:set value="${enviament.destinataris}" var="destinataris"></c:set>
									</c:when>
									<c:otherwise>
										<c:set value="destinataris" var="destinataris"></c:set>
									</c:otherwise>
								</c:choose>
								<div class="newDestinatari_${j} dest">
									<c:forEach items="${destinataris}" var="destinatari"
										varStatus="status">
										<c:set var="i" value="${status.index}" />
										<c:choose>
											<c:when test="${isVisible == true}">
												<c:set value="" var="visible"></c:set>
											</c:when>
											<c:otherwise>
												<c:set value="hidden" var="visible"></c:set>
											</c:otherwise>
										</c:choose>
										<div class="col-md-12 destinatariForm ${visible} personaForm_${j}_${i}">
											<input id="isVisible" name="enviaments[${j}].destinataris[${i}].visible" class="hidden" value="false">
												<div class="col-md-3">
													<not:inputSelect name="enviaments[${j}].destinataris[${i}].interessatTipus" generalClass="interessat" textKey="notificacio.form.camp.interessatTipus" labelSize="12" inputSize="12" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
												</div>
												<div class="col-md-3">
													<not:inputText name="enviaments[${j}].destinataris[${i}].nif" textKey="notificacio.form.camp.titular.nif" labelSize="12" inputSize="12" required="true" />
												</div>
												<div class="col-md-3">
													<not:inputText name="enviaments[${j}].destinataris[${i}].nom" textKey="notificacio.form.camp.titular.nom" labelSize="12" inputSize="12" required="true" />
												</div>
												<div class="col-md-3 llinatge1">
													<not:inputText name="enviaments[${j}].destinataris[${i}].llinatge1" textKey="notificacio.form.camp.titular.llinatge1" labelSize="12" inputSize="12" required="true" />
												</div>
												<div class="col-md-3 llinatge2">
													<not:inputText name="enviaments[${j}].destinataris[${i}].llinatge2" textKey="notificacio.form.camp.titular.llinatge2" labelSize="12" inputSize="12"/>
												</div>
												<div class="col-md-3">
													<not:inputText name="enviaments[${j}].destinataris[${i}].telefon" textKey="notificacio.form.camp.titular.telefon" labelSize="12" inputSize="12"/>
												</div>
												<div class="col-md-4">
													<not:inputText name="enviaments[${j}].destinataris[${i}].email" textKey="notificacio.form.camp.titular.email" labelSize="12" inputSize="12"/>
												</div>
												<div class="col-md-3 dir3codi hidden">
													<not:inputText name="enviaments[${j}].destinataris[${i}].dir3codi" textKey="notificacio.form.camp.titular.dir3codi" labelSize="12" inputSize="12"/>
												</div>
												<div class="col-md-2 offset-col-md-2">
													<div class="float-right">
														<input type="button" class="btn btn-danger btn-group delete" name="destinatarisDelete[${j}][${i}]" onclick="destinatarisDelete(this.id)" id="destinatarisDelete[${j}][${i}]" value="<spring:message code="notificacio.form.boto.eliminar.destinatari"/>" />
													</div>
												</div>
												<div class="col-md-12">
													<hr style="border-top: 1px dotted #BBB">
												</div>
										</div>
									</c:forEach>
								</div>
								<div class="col-md-12">
									<div class="text-left">	
										<input type="button" class="btn btn-default" name="enviaments[${j}]" id="enviaments[${j}]" onclick="addDestinatari(this.id)" value="<spring:message code="notificacio.form.boto.nou.destinatari"/>" />
									</div>
								</div>
							</div>
							<div class="col-md-12 separacio"></div>
							<div class="metodeEntrega">
								<div class="col-md-12 title-envios">
									<div class="title-container entrega">
										<label> ${metodeEntrega} </label>
									</div>
									<hr/>
								</div>
								<div class="col-md-12">
									<p class="comentari col-xs-offset-"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega.info"/></p>
									<not:inputCheckbox name="enviaments[${j}].entregaPostalActiva" textKey="notificacio.form.camp.entregapostal.activa" labelSize="2" funcio="mostrarEntregaPostal(this.id)" />
								</div>
								<input id="entregaPostalAmagat" name="enviaments[${j}].entregaPostal.visible" class="hidden" value="false">
	
								<c:if test="${not empty enviosGuardats}">
									<c:set var="entregaPostalActiva" value="${enviament.entregaPostalActiva}"></c:set>
								</c:if>
								<div id="entregaPostal" class="entregaPostal_${j}"
									<c:if test="${!entregaPostalActiva}">style="display:none"</c:if>>
	
									<div class="col-md-12">
										<div class="col-md-12">
											<not:inputSelect name="enviaments[${j}].entregaPostal.tipus" textKey="notificacio.form.camp.entregapostal.tipus" required="true" labelClass="labelcss" inputClass="inputcss"/>
										</div>
										<div class="col-md-4">
											<not:inputSelect name="enviaments[${j}].entregaPostal.tipusVia" textKey="notificacio.form.camp.entregapostal.tipusvia" labelClass="labelcss" inputClass="inputcss" required="true" />
										</div>
										<div class="col-md-8">
											<not:inputText name="enviaments[${j}].entregaPostal.viaNom" textKey="notificacio.form.camp.entregapostal.vianom" labelClass="labelcss" inputClass="inputcss" required="true" />
										</div>
										<div class="col-md-4">
											<not:inputText name="enviaments[${j}].entregaPostal.numeroCasa" textKey="notificacio.form.camp.entregapostal.numerocasa" labelClass="labelcss" inputClass="inputcss" />
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
										<div class="col-md-8">
											<not:inputText name="enviaments[${j}].entregaPostal.complement" textKey="notificacio.form.camp.entregapostal.complement" labelClass="labelcss" inputClass="inputcss" />
										</div>
										<div class="col-md-4">
											<not:inputText name="enviaments[${j}].entregaPostal.codiPostal" textKey="notificacio.form.camp.entregapostal.codipostal" labelClass="labelcss" inputClass="inputcss"/>
										</div>
										<div class="col-md-6">
											<not:inputSelect name="enviaments[${j}].entregaPostal.provincia" generalClass="provincies" emptyOption="true" textKey="notificacio.form.camp.entregapostal.provincia" labelClass="labelcss" inputClass="inputcss"/>
										</div>
										<div class="col-md-6">
											<not:inputSelect name="enviaments[${j}].entregaPostal.poblacio" generalClass="localitats" emptyOption="true" textKey="notificacio.form.camp.entregapostal.poblacio" labelClass="labelcss" inputClass="inputcss"/>
										</div>
										<div class="col-md-6">
											<not:inputSelect name="enviaments[${j}].entregaPostal.paisCodi" generalClass="paisos" emptyOption="true" textKey="notificacio.form.camp.entregapostal.paiscodi" labelClass="labelcss" inputClass="inputcss"/>
										</div>
										<c:choose>
											<c:when test="${not empty formatsFulla}">
											<div class="col-md-3">
												<not:inputSelect name="enviaments[${j}].entregaPostal.formatFulla" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatfulla" optionItems="${formatsFulla}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											</c:when>
											<c:otherwise>
											<div class="col-md-3">
												<not:inputText name="enviaments[${j}].entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${not empty formatsSobre}">
											<div class="col-md-3">
												<not:inputSelect name="enviaments[${j}].entregaPostal.formatSobre" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatsobre" optionItems="${formatsSobre}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											</c:when>
											<c:otherwise>
											<div class="col-md-3">
												<not:inputText name="enviaments[${j}].entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
								<div class="col-md-12">
									<not:inputCheckbox name="enviaments[${j}].entregaDeh.obligat" textKey="notificacio.form.camp.entregapostal.deh" labelSize="2" />
								</div>
								<div class="col-md-12 text-right">
									<input name="enviamentDelete[${j}]" id="enviamentDelete[${j}]" class="id_enviament hidden">
									<div class="btn-group">
										<input type="button" class="btn btn-default formEnviament eliminar_enviament hidden" name="enviamentDelete[${j}]" onclick="enviamentDelete(this.id)" id="enviamentDelete[${j}]" value="<spring:message code="notificacio.form.boto.eliminar.enviament"/>" />
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

</body> 
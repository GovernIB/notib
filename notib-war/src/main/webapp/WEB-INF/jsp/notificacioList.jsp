<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualUsuari",
			es.caib.notib.war.helper.RolHelper.isUsuariActualUsuari(request));
	pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
	pageContext.setAttribute(
			"isRolActualAdministradorOrgan",
			es.caib.notib.war.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(request));
%>
<c:set var="ampladaConcepte">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">200px</c:when>
		<c:otherwise>300px</c:otherwise>
	</c:choose>
</c:set>
<c:set var="ampladaEnviament">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">160px</c:when>
		<c:otherwise>130px</c:otherwise>
	</c:choose>
</c:set>
<c:set var="refresh_state_succes"><spring:message code="notificacio.list.enviament.list.refresca.estat.exitos"/></c:set>
<c:set var="refresh_state_error"><spring:message code="notificacio.list.enviament.list.refresca.estat.error"/></c:set>
<html>
<head>
	<title><spring:message code="notificacio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
<style type="text/css">
.horaProcessat {
	font-size: small;
}
.datepicker table tr td.today, .datepicker table tr td.today:hover {
	color: #000000;
	background: #a4a4a4 !important;
	background-color: #a4a4a4 !important;
}
.panel.panel-default.info-enviament {
	margin-top: 6%;
}
.motiu_finalitzada {
	background-color: #e0ead5;
}
.info_finalitzada_icon:hover {
	cursor: pointer;
}
.info_finalitzada_div {
	text-align: right;
}
.info_finalitzada_icon {
	top: -16px;
}
.info_finalitzada_link:hover {
	text-decoration: none !important;
}
.motiu_finalitzada > a {
	display: none;
}
.panel-heading.processarButton {
	background-color: red;
}
.not-icon-o {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 22px;
	height: 22px;
	background-color: #999999;
	color: white;
	font-weight: bold;
	font-size: 13px;
	margin: 0 auto;
}
.com-icon-o {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 27px;
	height: 27px;
	background-color: #dddddd;
	color: black;
	font-weight: bold;
	font-size: 13px;
	margin: 0 auto; 
}
#notificacio > tbody td:first-child {
	vertical-align: middle;
}

#nomesAmbErrorsBtn {
	margin-right: 10%;
}
</style>
<script type="text/javascript"> 

var myHelpers = {
		recuperarEstatEnviament: returnEnviamentsStatusDiv, 
		hlpIsUsuari: isRolActualUsuari, 
		hlpIsAdministradorEntitat: isRolActualAdministradorEntitat, 
		hlpIsAdministradorOrgan: isRolActualAdministradorOrgan};

$.views.helpers(myHelpers);

function isRolActualUsuari() {
	return ${isRolActualUsuari};
}

function isRolActualAdministradorEntitat() {
	return ${isRolActualAdministradorEntitat};
}

function isRolActualAdministradorOrgan() {
	return ${isRolActualAdministradorOrgan};
}


function returnEnviamentsStatusDiv(notificacioId) {
	var content = "";
	var getUrl = "<c:url value="/notificacio/"/>" + notificacioId + "/enviament";

	$.getJSON({
	    url: getUrl,
	    success: (user) => {
	    	for (i = 0; i < user.length; i++) {
				content += (user[i].notificaEstat) ? notificacioEnviamentEstats[user[i].notificaEstat] + ',' : '';
			}
	    	if (content !== undefined && content != '') {
	    		content = "("+content.replace(/,\s*$/, "")+")";
	    	}
	    	$('.estat_' + notificacioId).append(content);
	    },
		error: console.log("No s'han pogut recuperar els enviaments de la notificació: " + notificacioId)
	})
}

$(function() {
    $(document).on("click", "a.fileDownloadSimpleRichExperience", function() {
        $.fileDownload($(this).attr('href'), {
            preparingMessageHtml: "Estam preparant la descàrrega, per favor esperi...",
            failMessageHtml: "<strong style='color:red'>Ho sentim.<br/>S'ha produït un error intentant descarregar el document.</strong>"
        });
        return false;
    });
});

function formatDate(data) {
	//Añadir ceros a los numeros de un dígito
	Number.prototype.padLeft = function(base,chr){
		var  len = (String(base || 10).length - String(this).length)+1;
		return len > 0? new Array(len).join(chr || '0')+this : this;
	}
	if (data !== null) {
		//dd/MM/yyyy HH:mm:SS
		var procesDate = new Date(data),
		procesDateFormat = [procesDate.getDate().padLeft(),
			(procesDate.getMonth()+1).padLeft(),
			procesDate.getFullYear()].join('/') +' ' +
           [procesDate.getHours().padLeft(),
        	   procesDate.getMinutes().padLeft(),
        	   procesDate.getSeconds().padLeft()].join(':');
		return procesDateFormat;
	} else {
		return null;
	}
}

var notificacioEstats = [];
<c:forEach var="estat" items="${notificacioEstats}">
notificacioEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>
var notificacioEnviamentEstats = [];
<c:forEach var="estat" items="${notificacioEnviamentEstats}">
notificacioEnviamentEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>
var comunicacioTipus = [];
<c:forEach var="tipus" items="${notificacioComunicacioTipus}">
comunicacioTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
var enviamentTipus = [];
<c:forEach var="tipus" items="${notificacioEnviamentTipus}">
enviamentTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
$(document).ready(function() {
	$('#notificacio').on('rowinfo.dataTable', function(e, td, rowData) {
		var getUrl = "<c:url value="/notificacio/"/>" + rowData.id + "/enviament";
	    $.get(getUrl).done(function(data) {
	    	$(td).empty();
	    	$(td).append(
	    			'<table class="table teble-striped table-bordered">' +
	    			'<caption><spring:message code="notificacio.list.enviament.list.titol"/></caption>' +
	    			'<thead>' +
	    			'<tr>' +
					'<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' + 
	    			'<th><spring:message code="notificacio.list.enviament.list.destinataris"/></th>' +
	    			'<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
	    			'<th></th>' +
	    			'</tr>' +
					'</thead><tbody></tbody></table>');
	    	contingutTbody = '';
			for (i = 0; i < data.length; i++) {
				var nomTitular = '', llinatge1 = '', llinatge2 = '', destinataris = '', nif = '';
				
				if (data[i].titular.nom != null) {
					nomTitular = data[i].titular.nom;
				} else if (data[i].titular.raoSocial != null){
					nomTitular = data[i].titular.raoSocial;
				}
				if (data[i].titular.llinatge1 != null) {
					llinatge1 = data[i].titular.llinatge1;
				}
				if (data[i].titular.llinatge2 != null) {
					llinatge2 = data[i].titular.llinatge2;
				}
				
				$.each(data[i].destinataris, function (index, destinatari) {
					var nomDest = '', llinatge1Dest = '', llinatge2Dest = '';
					if (destinatari.nom != null) {
						nomDest = destinatari.nom;
					} else if (destinatari.raoSocial != null){
						nomDest = destinatari.raoSocial;
					}
					if (destinatari.llinatge1 != null) {
						llinatge1Dest = destinatari.llinatge1;
					}
					if (destinatari.llinatge2 != null) {
						llinatge2Dest = destinatari.llinatge2;
					}
					if (destinatari.nif != null) {
						nif = destinatari.nif;
					} else {
						nif = destinatari.dir3Codi;
					}
					destinataris += nomDest + ' ' + llinatge1Dest + ' ' + llinatge2Dest + ' (' + nif + '), ';
				});
				if (data[i].titular.nif != null) {
					nif = data[i].titular.nif;
				} else {
					nif = data[i].titular.dir3Codi;
				}
				contingutTbody += '<tr data-toggle="modal" data-href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '"/>" style="cursor: pointer;">';
				contingutTbody += '<td>' + nomTitular + ' ' + llinatge1 + ' ' + llinatge2 + '('+ nif +') </td>';
				if (destinataris != ''){
					//Remove last white space
					destinataris = destinataris.substr(0, destinataris.length-1);
					//Remove last comma
					destinataris = destinataris.substr(0, destinataris.length-1);
				} else {
					destinataris = '<spring:message code="notificacio.list.enviament.list.sensedestinataris"/>';
				}
				contingutTbody += '<td>' + destinataris + '</td>';
				contingutTbody += '<td>';
				contingutTbody += (data[i].notificaEstat) ? notificacioEnviamentEstats[data[i].notificaEstat] : '';
				if (data[i].notificacioError) {
					var errorTitle = '';
					if (data[i].notificacioErrorDescripcio) {
						errorTitle = data[i].notificacioErrorDescripcio.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
					} else {
						errorTitle = "Descripció de l'error no registrada";
					}
					contingutTbody += ' <span class="fa fa-warning text-danger" title="' + errorTitle + '"></span>';
				}
				contingutTbody += '</td>';
				contingutTbody += '<td width="114px">';
				if (data[i].notificaCertificacioData != null) {
					contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/certificacioDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.certificacio"/>"><span class="fa fa-download"></span></a>';
				} else if (data[i].notificacioEstat == 'REGISTRADA' &&
						(data[i].registreEstat && (data[i].registreEstat == 'DISTRIBUIT' || data[i].registreEstat == 'OFICI_EXTERN'  || data[i].registreEstat == 'OFICI_SIR')) || (data[i].registreData && data[i].registreNumeroFormatat != '')){
					contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/justificantDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.justificant"/>"><span class="fa fa-download"></span></a>';
				}
				contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '"/>" data-toggle="modal" class="btn btn-default btn-sm"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>';
				contingutTbody += '</td>';
				contingutTbody += '</tr>';
			}
			$('table tbody', td).append(contingutTbody);
			$('table tbody td').webutilModalEval();
		});
	});
	$('#btnNetejar').click(function() {
		$(':input', $('#filtre')).each (function() {
			var type = this.type, tag = this.tagName.toLowerCase();
			if (type == 'text' || type == 'password' || tag == 'textarea') {
				this.value = '';
			} else if (type == 'checkbox' || type == 'radio') {
				this.checked = false;
			} else if (tag == 'select') {
				this.selectedIndex = 0;
			}
			
		});
		$('#nomesAmbErrorsBtn').removeClass('active');
		$('#nomesAmbErrors').val(false);
		omplirProcediments();
		$('#form-filtre').submit();
	});
	$('#nomesAmbErrorsBtn').click(function() {
		nomesAmbErrors = !$(this).hasClass('active');
		$('#nomesAmbErrors').val(nomesAmbErrors);
	})
	$('#organGestor').on('change', function () {
		//Procediments
		omplirProcediments();
	});
	function omplirProcediments() {
		var organGestor = $("#organGestor");
		var selProcediments = $("#procedimentId");
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/procedimentsOrgan/"/>" + $(organGestor).val(),
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
								selProcediments.append("<option value=\"" + val.codi + "\">" + val.valor + "</option>");
							});
						selProcediments.append("</optgroup>");
					}
					if (procedimentsOrgan.length > 0) {
						selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.organs'/>'>");
							$.each(procedimentsOrgan, function(index, val) {
								selProcediments.append("<option value=\"" + val.codi + "\">" + val.valor + "</option>");
							});
						selProcediments.append("</optgroup>");
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

	omplirProcediments();
	loadOrgans();
});

var organsGestors = [];
organsGestors.push({id:"", text:"", estat:"VIGENT"});
<c:forEach items="${organsGestorsPermisLectura}" var="organGestor">
	organsGestors.push({id:"${organGestor.codi}", text:"${organGestor.valor}", estat:"${organGestor.estat}"});
</c:forEach>

function formatState(organ) {
	let msgObsolet = "<spring:message code='notificacio.list.columna.organGestor.obsolet'/>";
	if (organ.estat == 'VIGENT' || organ.estat == null || organ.estat == '') {
		return organ.text;
	}
	return $("<span title='" + msgObsolet + "'>" + organ.text + " <span class='fa fa-warning text-danger'></span></span>");
}

function loadOrgans(){
	var listaOrganos = $('#organGestor');
	listaOrganos.empty();

	var select2Options = {
			theme: 'bootstrap',
			width: 'auto',
			tags: organsGestors,
			templateResult: formatState
	};
	
	listaOrganos.select2(select2Options);
}
</script>
</head>
<body>
<div id="msg-box"></div>
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="notificacioFiltreCommand">
		<div class="row">
			<c:if test="${isRolActualAdministrador && mostrarColumnaEntitat}">
				<div class="col-md-3">
					<not:inputSelect name="entitatId" optionItems="${entitat}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.entitat" inline="true"/>
				</div>
			</c:if>
			<div class="col-md-2">
				<not:inputSelect name="enviamentTipus" optionItems="${notificacioEnviamentTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.enviament.tipus" inline="true"/>
			</div>
			<%--div class="col-md-2">
				<not:inputSelect name="comunicacioTipus" optionItems="${notificacioComunicacioTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.comunicacio.tipus" inline="true"/>
			</div--%>
			<div class="col-md-4">
				<not:inputText name="concepte" inline="true"  placeholderKey="notificacio.list.filtre.camp.concepte"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect name="estat" optionItems="${notificacioEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"/>
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataInici" placeholderKey="notificacio.list.filtre.camp.datainici" inline="true" required="false" />
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataFi" placeholderKey="notificacio.list.filtre.camp.datafi" inline="true" required="false" />
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<not:inputText name="titular" inline="true" placeholderKey="notificacio.list.filtre.camp.titular"/>
			</div>
			<div class="col-md-4">
				<not:inputSelect name="organGestor" placeholderKey="notificacio.list.filtre.camp.organGestor" inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
			</div>
			<div class="col-md-6">
				<not:inputSelect name="procedimentId" optionValueAttribute="id" optionTextAttribute="descripcio" placeholderKey="notificacio.list.filtre.camp.procediment" inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<not:inputSelect name="tipusUsuari" optionItems="${tipusUsuari}" optionValueAttribute="value" optionTextKeyAttribute="text"  emptyOption="true"  placeholderKey="notificacio.list.filtre.camp.tipususuari" inline="true" />
			</div>
			<div class="col-md-4">
				<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
				<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
				<not:inputSuggest 
					name="creadaPer" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					textKey="notificacio.list.filtre.camp.numexpedient"
					placeholderKey="notificacio.list.filtre.camp.creadaper"
					suggestValue="codi"
					suggestText="nom"
					suggestTextAddicional="nif"
					inline="true"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="numExpedient" inline="true" placeholderKey="notificacio.list.filtre.camp.numexpedient"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="identificador" inline="true" placeholderKey="notificacio.list.filtre.camp.identificador"/>
			</div>
			<div class="col-md-2 pull-right form-buttons"  style="text-align: right;">
				<button id="nomesAmbErrorsBtn" title="<spring:message code="notificacio.list.filtre.camp.nomesAmbErrors"/>" class="btn btn-default <c:if test="${nomesAmbErrors}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
				<not:inputHidden name="nomesAmbErrors"/>
				<button id="btnNetejar" type="submit" name="netejar" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	<script id="rowhrefTemplate" type="text/x-jsrender">modal/notificacio/{{:id}}/info</script>
	<table
		id="notificacio"
		data-toggle="datatable"
		data-url="<c:url value="/notificacio/datatable"/>"
		data-search-enabled="false"
		data-default-order="8"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-row-info="true"
		data-filter="#filtre"
		data-save-state="true"
		data-mantenir-paginacio="true"
		data-rowhref-template="#rowhrefTemplate"
		data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="tipusUsuari" data-visible="false">#</th>
				<th data-col-name="errorLastCallback" data-visible="false">#</th>
				<th data-col-name="hasEnviamentsPendentsRegistre" data-visible="false">#</th>
				<th data-col-name="notificaError" data-visible="false"></th>
				<th data-col-name="notificaErrorDescripcio" data-visible="false"></th>
				<th data-col-name="enviant" data-visible="false"></th>
				<th data-col-name="enviamentTipus" data-template="#cellEnviamentTipusTemplate" class="enviamentTipusCol" width="5px">

					<script id="cellEnviamentTipusTemplate" type="text/x-jsrender">
						{{if enviamentTipus == 'NOTIFICACIO'}}
							<div class="not-icon-o" title="<spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.NOTIFICACIO"/>">N</div>
						{{else}}
							<div class="com-icon-o" title="<spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.COMUNICACIO"/>">C</div>
						{{/if}}
					</script>
				</th>
				<%-- <th data-col-name="notificaEnviamentData" data-converter="datetime" width="${ampladaEnviament}"><spring:message code="notificacio.list.columna.enviament.data"/></th>--%>
				<th data-col-name="createdDate" data-converter="datetime" width="${ampladaEnviament}"><spring:message code="notificacio.list.columna.enviament.data"/></th>
				<c:if test="${isRolActualAdministrador && mostrarColumnaEntitat}">
					<th data-col-name="entitatNom" width="170px"><spring:message code="notificacio.list.columna.entitat"/></th>
				</c:if>
				<%--th data-col-name="comunicacioTipus" data-template="#cellComunicacioTipusTemplate">
					<spring:message code="notificacio.list.columna.tipus.comunicacio"/>
					<script id="cellComunicacioTipusTemplate" type="text/x-jsrender">
						{{:~eval('comunicacioTipus["' + comunicacioTipus + '"]')}}
					</script>
				</th--%>
				<th data-col-name="organEstat" data-visible="false"></th>
				<th data-col-name="organGestorDesc" data-template="#cellOrganGestorTemplate" width="200px"><spring:message code="notificacio.form.camp.organGestor"/>
					<script id="cellOrganGestorTemplate" type="text/x-jsrender">
						{{:organGestorDesc}}
						{{if organEstat != 'VIGENT'}}
							<span class="fa fa-warning text-danger" title="<spring:message code='notificacio.list.columna.organGestor.obsolet'/>"></span>{{/if}}
 					</script>
				</th>
				<th data-col-name="procedimentDesc"  width="200px"><spring:message code="notificacio.list.columna.procediment"/></th>
				<c:if test="${mostrarColumnaNumExpedient}">
					<th data-col-name="numExpedient" width="170px"><spring:message code="notificacio.list.columna.num.expedient"/></th>
				</c:if>
				<th data-col-name="concepte" width="${ampladaConcepte}" ><spring:message code="notificacio.list.columna.concepte"/></th>
				<th data-col-name="estatDate" data-converter="datetime" data-visible="false"></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate"  width="120px">
					<spring:message code="notificacio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if enviant}}
							<span class="fa fa-clock-o"></span>
							<spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.ENVIANT"/>
						{{else estat == 'PENDENT'}}
							<span class="fa fa-clock-o"></span>
							{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{else estat == 'ENVIADA'}}
							<span class="fa fa-send-o"></span>
							{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{else estat == 'FINALITZADA'}}
							<span class="fa fa-check"></span>
							{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{else estat == 'REGISTRADA'}}
							<span class="fa fa-file-o"></span>
							{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{else estat == 'PROCESSADA'}}
							<span class="fa fa-check-circle"></span>
							{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{/if}}

						{{if notificaError}}<span class="fa fa-warning text-danger" title="{{>notificaErrorDescripcio}}"></span>{{/if}}
						{{if tipusUsuari == 'APLICACIO' && errorLastEvent}}
							<span class="fa fa-exclamation-circle text-primary" title="<spring:message code="notificacio.list.client.error"/>"></span>
						{{/if}}
						{{if estat == 'PROCESSADA' && estatDate != ''}}
							<br>
							<p class="horaProcessat">{{:~eval('formatDate(' + estatDate+ ')')}}</p>
						{{/if}}
						{{if estat == 'FINALITZADA'}}
							{{:~recuperarEstatEnviament(id)}}
							<p class="estat_{{:id}}"  style="display:inline"></p>
						{{/if}}

					</script>
				</th>
<%-- 				<th data-col-name="notificaEstat"  width="200px"><spring:message code="notificacio.form.camp.organGestor"/></th> --%>
				<th data-col-name="createdByComplet" data-converter="String" width="150px"><spring:message code="notificacio.list.columna.enviament.creada"/></th>
				<th data-col-name="permisProcessar" data-visible="false">
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="60px">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/notificacio/{{:id}}/info"/>" data-toggle="modal" data-height="700px" data-processar="true"><span class="fa fa-info-circle"></span>&nbsp; <spring:message code="comu.boto.detalls"/></a></li>
							{{if permisProcessar }}
								<li><a href="<c:url value="/notificacio/{{:id}}/processar"/>" data-toggle="modal"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a></li>
							{{/if}}
							{{if !enviant}}
								{^{if (~hlpIsUsuari() || ~hlpIsAdministradorEntitat() || ~hlpIsAdministradorOrgan()) && hasEnviamentsPendentsRegistre }}
									<li><a href="<c:url value="/notificacio/{{:id}}/edit"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.editar"/></a></li>
									<li><a href="<c:url value="/notificacio/{{:id}}/delete"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}
							{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

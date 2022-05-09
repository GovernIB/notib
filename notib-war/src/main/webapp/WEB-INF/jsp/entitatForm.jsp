<%@page import="es.caib.notib.core.api.dto.TipusDocumentEnumDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty entitatCommand.id}"><c:set var="titol"><spring:message code="entitat.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="entitat.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<%
	pageContext.setAttribute("tipusDocumentEnumDto", TipusDocumentEnumDto.values()); 
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
%>
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
	<not:modalHead/>
<style type="text/css">
.title {
	text-align: center;
}
.title > label {
	color: #ff9523;
}
.title > hr {
	margin-top: 0%;
}
.select2-container--bootstrap {
	width: 100% !important;
}

.icon {
	float: left;
}
.addBoto {
	float:left;
	width: calc(100% - 40px);
}
.botoAdded {
	float: left;
	margin-left: 5px;
	padding: 9px 10px;
	line-height: 10px;
}
</style>
<script type="text/javascript">

var oficinaActual = '${entitatCommand.oficina}';
var codiDir3Actual =  '${entitatCommand.dir3Codi}';
var codiDir3RegActual = '${entitatCommand.dir3CodiReg}';
var llibreChecked = "${entitatCommand.llibreEntitat}" === "false" ? false : true;

$(document).ready(function() {


	var entitatId = document.getElementById('id').value;

	if (entitatId != '' && !"${tipusDocSelected}") {
		var getUrl = "<c:url value="/entitat/"/>" + entitatId + "/tipusDocument";
		 $.get(getUrl).done(function(data) {
			 var dataMod =[]
		 	$("#tipusDocName").webutilInputSelect2(data);
		 });
	} else {
		$("#tipusDocName").webutilInputSelect2(null);
	}

	<c:choose>
		<c:when test="${not empty tipusDocSelected}">
			let tipusDoc = [];
			<c:forEach items="${tipusDocSelected}" var="tipus">
			tipusDoc.push({codi:"${tipus.codi}", valor:"${tipus.valor}", desc:"${tipus.desc}"});
			</c:forEach>
			$("#tipusDocName").webutilInputSelect2(tipusDoc);
		</c:when>
		<c:otherwise>
			$("#tipusDocName").webutilInputSelect2();
		</c:otherwise>
	</c:choose>
	var data = new Array();

	$('.customSelect').on("select2:select select2:unselect select2-loaded", function (e) {
		var data = [];
		$.each($("#tipusDocName :selected"), function(index, option) {
			data[index] = {
				    'valor': option.value,
				    'desc': option.text,
				};
		});
	    addDefault(data);


	});

	$('#colorFons, #colorLletra').colorpicker();

	$('#dir3Codi').on("change", function() {
		let dir3codi = $(this).val();
		let dir3codiReg = $("#dir3CodiReg").val();

		if (dir3codi !== undefined && dir3codi !== '') {
			if (dir3codiReg == undefined || dir3codiReg == '') {
				updateOficines(dir3codi);
				if($("#llibreEntitat").is(':checked'))
					updateLlibre(dir3codi);
			}
		} else {
			console.log('<spring:message code="procediment.form.avis.oficines"/>');
		}
	});
	$('#dir3CodiReg').on("change", function() {
		let codi = '';
		let dir3codiReg = $(this).val();
		let dir3codi = $("#dir3Codi").val();

		if (dir3codiReg !== undefined && dir3codiReg !== '') {
			codi = dir3codiReg
		} else if (dir3codi !== undefined && dir3codi !== ''){
			codi = dir3codi;
		} else {
			console.log('<spring:message code="procediment.form.avis.oficines"/>');
		}
		if (codi != '') {
			updateOficines(codi);
			if($("#llibreEntitat").is(':checked'))
				updateLlibre(codi);
		}
	});
	$('#oficina').on("change", function(){
		oficinaActual = $(this).val();
	});
	$('#refreshLlibre').on("click", function() {
		let dir3codiReg = $("#dir3CodiReg").val();
		let dir3codi = $("#dir3Codi").val();

		if (dir3codiReg !== undefined && dir3codiReg !== '') {
			updateLlibre(dir3codiReg);
		} else if (dir3codi !== undefined && dir3codi !== ''){
			updateLlibre(dir3codi);
		} else {
			$('#llibreCodiNom').val("");
		}
	});

	$('#llibreEntitat').change(function() {
		if (this.checked) {
			$('#llibre-entitat').show();
			$('#refreshLlibre').trigger("click");
		} else {
			$('#llibre-entitat').hide();
		}
	});
	if (!$('#llibreEntitat').checked) {
		$('#llibre-entitat').hide();
	}

	if (llibreChecked) {
		$('#llibre-entitat').show();
	}

	$('#oficinaEntitat').change(function() {
		if (this.checked) {
			$('#oficina').closest('.form-group').show();
		} else {
			$('#oficina').closest('.form-group').hide();
		}
	});
 	$('#oficinaEntitat').trigger("change");
	loadOficines();

	$('#entregaCieActiva').change(function() {
		if (this.checked) {
			$('#entrega-cie-form').show();
		} else {
			$('#entrega-cie-form').hide();
		}
	});

	if (!$('#entregaCieActiva')[0].checked) {
		$('#entrega-cie-form').hide();
	}
});

function loadOficines() {
	let dir3codiReg = $("#dir3CodiReg").val();
	let dir3codi = $("#dir3Codi").val();

	if (dir3codiReg !== undefined && dir3codiReg !== '') {
		updateOficines(dir3codiReg);
	} else if (dir3codi !== undefined && dir3codi !== ''){
		updateOficines(dir3codi);
	} else {
		console.log('<spring:message code="procediment.form.avis.oficines"/>');
	}
}
function updateOficines(dir3codi) {
	$.ajax({
		type: 'GET',
		url: "<c:url value="/entitat/oficines/"/>" + dir3codi,
		success: function(data) {
			var selOficines = $('#oficina');
			selOficines.empty();
			selOficines.append("<option value=\"\"></option>");
			if (data && data.length > 0) {
					var items = [];
					$.each(data, function(i, val) {
						items.push({
							"id": val.codi,
							"text": val.codi + " - " + val.nom
						});
						selOficines.append("<option value=\"" + val.codi + "\"" + (oficinaActual == val.codi ? "selected" :  "") + ">" + val.codi + " - " + val.nom + "</option>");
					});
				}
			$(".loading-screen").hide();
		},
		error: function() {
			console.log("error obtenint les oficines...");
		}
	});
}
function updateLlibre(dir3codi) {
	$.ajax({
		type: 'GET',
		url: "<c:url value="/entitat/llibre/"/>" + dir3codi,
		success: function(data) {
			if (data && data.codi) {
				$('#llibreCodiNom').val(data.codi + " - " + data.nomLlarg);
				$('#llibre-error').hide();
			} else {
				$('#llibreCodiNom').val("");
				$('#llibre-error').show();
			}
// 			$(".loading-screen").hide();
		},
		error: function() {
			console.log("error obtenint el llibre de l'entitat!");
		}
	});
}
</script>
</head>
<body>
	<c:forEach items="${errors}" var="error" varStatus="status">
	    <c:if test="${error.field == 'tipusDocName'}">
	        <c:set var="errorTipusDoc" value="${error}"></c:set>
	    </c:if>
    </c:forEach>
	<ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active"><a href="#dadesForm" aria-controls="dadesForm" role="tab" data-toggle="tab"><spring:message code="entitat.form.titol.dades"/></a></li>
        <li role="presentation"><a href="#configuracioForm" aria-controls="configuracioForm" role="tab" data-toggle="tab"><c:if test="${not empty errorTipusDoc}"> <span class="fa fa-warning text-danger"></span></c:if> <spring:message code="entitat.form.titol.configuracio"/></a></li>
    </ul>
	<c:set var="formAction"><not:modalUrl value="/entitat"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="entitatCommand" role="form"  enctype="multipart/form-data">
		<br>
		<div class="tab-content">
		<form:hidden path="id"/>
		<div role="tabpanel" class="tab-pane active" id="dadesForm">
<%--			<not:inputText name="codi" textKey="entitat.form.camp.codi" disabled="${modificant}"/>--%>
			<not:inputText name="codi" textKey="entitat.form.camp.codi" required="true"/>
			<not:inputText name="nom" textKey="entitat.form.camp.nom" required="true"/>
			<not:inputSelect name="tipus" textKey="entitat.form.camp.tipus" optionEnum="EntitatTipusEnumDto" required="true"/>
			<not:inputText name="dir3Codi" textKey="entitat.form.camp.codidir3" required="true"/>
			<not:inputText name="dir3CodiReg" textKey="entitat.form.camp.codidir3reg" info="true" messageInfo="entitat.form.camp.codidir3reg.info"/>
			<not:inputText name="apiKey" textKey="entitat.form.camp.apiKey" required="true"/>
			<not:inputCheckbox name="ambEntregaDeh" textKey="entitat.form.camp.entregadeh"/>
			<not:inputCheckbox name="entregaCieActiva" textKey="entitat.form.camp.entregacie"/>
			<div id="entrega-cie-form">
				<not:inputSelect name="operadorPostalId" optionItems="${operadorPostalList}" optionValueAttribute="id"
								 optionTextAttribute="text" required="true" emptyOption="true"
								 textKey="entitat.form.camp.operadorpostal" placeholderKey="entitat.form.camp.operadorpostal" optionMinimumResultsForSearch="0"/>
				<not:inputSelect name="cieId" optionItems="${cieList}" optionValueAttribute="id"
								 optionTextAttribute="text" required="true" emptyOption="true"
								 textKey="entitat.form.camp.cie" placeholderKey="entitat.form.camp.operadorpostal" optionMinimumResultsForSearch="0"/>
			</div>
			<not:inputCheckbox name="llibreEntitat" textKey="entitat.form.camp.llibreEntitat"/>
			<div id="llibre-entitat">
				<div class="form-group">
					<label class="control-label col-xs-4 " for="llibreCodiNom"><spring:message code="entitat.form.camp.llibre" />*</label>
					<div class="col-xs-8">
						<input id="llibreCodiNom" name="llibreCodiNom" class="form-control addBoto" readonly="readonly" type="text" value="${entitatCommand.llibreCodiNom}">
						<button id="refreshLlibre" type="button" class="btn btn-default botoAdded"><span class="fa fa-refresh"></span></button>
						<p id="llibre-error" class="comentari col-xs-12 col-xs-offset-"><spring:message code="entitat.form.camp.llibre.error"/></p>
					</div>
				</div>
			</div>
			<not:inputCheckbox name="oficinaEntitat" textKey="entitat.form.camp.oficinaEntitat"/>
			<not:inputSelect name="oficina" textKey="entitat.form.camp.oficina" required="true" optionMinimumResultsForSearch="0"/>
			<not:inputTextarea name="descripcio" textKey="entitat.form.camp.descripcio"/>
		</div>
		<div role="tabpanel" class="tab-pane " id="configuracioForm">
			<div class="container-fluid col-md-12">
				<div class="title">
					<label><spring:message code="entitat.form.camp.conf.aspecte" /></label>
					<hr/>
				</div>
				<not:inputFile name="logoCap" textKey="entitat.form.camp.conf.logocap" fileEntitat="true" logoMenu="true" inputSize="6"/>
				<not:inputFile name="logoPeu" textKey="entitat.form.camp.conf.logopeu" fileEntitat="true" logoMenu="false" inputSize="6"/>
				<not:inputText name="colorFons" textKey="entitat.form.camp.conf.fons" picker="true"/>
				<not:inputText name="colorLletra" textKey="entitat.form.camp.conf.lletra" picker="true"/>
				<div class="title">
					<label><spring:message code="entitat.form.camp.conf.tipusdoc" /></label>
					<hr/>
				</div>
				<c:set var="campErrors"><form:errors path="tipusDocName"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
					<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/> *</label>
					<div class="controls col-xs-8">
						<select name="tipusDocName" id="tipusDocName" class="customSelect" multiple>
						<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
<%-- 							<c:set var="documentVar"> --%>
<%-- 						      <spring:message code="tipus.document.enum.${enumValue}" /> --%>
<%-- 						  	</c:set>   --%>
							<option value="${enumValue}" selected><spring:message code="tipus.document.enum.${enumValue}" /></option>
<%-- 							<option value="${enumValue}" selected>${enumValue}</option> --%>
						</c:forEach>
						</select>
						<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="tipusDocName"/></p></c:if>
					</div>
				</div>
				<form:hidden path="tipusDocDefaultSelected" value="${tipusDocumentDefault}"/>
				<not:inputSelect name="tipusDocDefault" textKey="entitat.form.camp.conf.tipusdoc.default"/>
			</div>
		</div>
		</div>
		<div id="modal-botons" class="col-xs-12 text-right">
			<button type="submit" id="submitBtn" class="btn btn-success" ><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<c:if test="${isRolActualAdministrador}">
			<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</c:if>
		</div>
	</form:form>
</body>
</html>

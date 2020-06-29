<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty procedimentCommand.codi}"><c:set var="titol"><spring:message code="procediment.form.titol.crear"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="procediment.form.titol.modificar"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
<style type="text/css">
.select2-container {
	width: auto;
}
.body {
	height: 250px;
}
.modal-backdrop {
    visibility: hidden !important;
}
.modal.in {
    background-color: rgba(0,0,0,0.5);
}
.habilitat {
	background-color: white !important;
	cursor: pointer;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	//Organismes
	$("#searchOrgan").click(function(){
		$("#organismesModal").modal();
		var entitatId = $('#entitatId').val();
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/organismes/"/>" + entitatId,
			success: function(data) {
				var selOrganismes = $('#selOrganismes');
				selOrganismes.empty();
				selOrganismes.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.codi + " - " + val.nom
							});
							selOrganismes.append("<option value=\"" + val.codi + "\">" + val.codi + " - " + val.nom + "</option>");
						});
				}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selOrganismes.select2(select2Options);
				$(".loading-screen").hide();
			},
			error: function() {
				console.log("error obtenint els organismes...");
			}
		});
	});
	$('#addOrganismeButton').on('click', function(){
		var organSelect = document.getElementById('selOrganismes');
		var organSeleccionatValue = organSelect.options[organSelect.selectedIndex].value;
		var organSeleccionatText = organSelect.options[organSelect.selectedIndex].text;
		$('#organGestor').val(organSeleccionatValue);
		$('#organGestorNom').val(organSeleccionatText);
	});
	//Oficines
	$("#searchOficina").click(function(){
		$("#oficinesModal").modal();
		var entitatId = $('#entitatId').val();
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/oficines/"/>" + entitatId,
			success: function(data) {
				var selOficines = $('#selOficines');
				selOficines.empty();
				selOficines.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							selOficines.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
						});
					}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selOficines.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint les oficines...");
			}
		});
	});
	$('#addOficinaButton').on('click', function(){
		var oficinaSelect = document.getElementById('selOficines');
		var oficinaSeleccionatValue = oficinaSelect.options[oficinaSelect.selectedIndex].value;
		var oficinaSeleccionatText = oficinaSelect.options[oficinaSelect.selectedIndex].text;
		$('#oficina').val(oficinaSeleccionatValue);
		$('#oficinaNom').val(oficinaSeleccionatText);
	});
	//Llibres
	$("#searchLlibre").click(function(){
		$("#llibresModal").modal();
		var entitatId = $('#entitatId').val();
		var oficina = $('#oficina').val();
		if (oficina == '') {
			oficina = '0';
		}
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/llibres/"/>" + entitatId + "/" + oficina,
			success: function(data) {
				var selLlibres = $('#selLlibres');
				selLlibres.empty();
				selLlibres.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							selLlibres.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
						});
					}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selLlibres.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint els llibres...");
			}
		});
	});
	$('#addLlibreButton').on('click', function(){
		var llibreSelect = document.getElementById('selLlibres');
		var llibreSeleccionatValue = llibreSelect.options[llibreSelect.selectedIndex].value;
		var llibreSeleccionatText = llibreSelect.options[llibreSelect.selectedIndex].text;
		$('#llibre').val(llibreSeleccionatValue);
		$('#llibreNom').val(llibreSeleccionatText);
	});
	//TipusAssumpte
	$("#searchTipusAssumpte").click(function(){
		$("#TipusAssumptesModal").modal();
		var entitatId = $('#entitatId').val();
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/tipusAssumpte/"/>" + entitatId,
			success: function(data) {
				var selTipusAssumpte = $('#selTipusAssumpte');
				selTipusAssumpte.empty();
				selTipusAssumpte.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							selTipusAssumpte.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
						});
					}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selTipusAssumpte.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint els tipus d'assumpte...");
			}
		});
	});
	$('#addTipusAssumpteButton').on('click', function(){
		var tipusAssumpteSelect = document.getElementById('selTipusAssumpte');
		var tipusAssumpteSeleccionatValue = tipusAssumpteSelect.options[tipusAssumpteSelect.selectedIndex].value;
		var tipusAssumpteSeleccionatText = tipusAssumpteSelect.options[tipusAssumpteSelect.selectedIndex].text;
		$('#tipusAssumpte').val(tipusAssumpteSeleccionatValue);
		$('#tipusAssumpteNom').val(tipusAssumpteSeleccionatText);
	});
	//CodiAssumpte
	$("#searchCodiAssumpte").click(function(){
		$("#codisAssumpteModal").modal();
		var entitatId = $('#entitatId').val();
		var tipusAssumpte = $('#tipusAssumpte').val();
		if (tipusAssumpte == '') {
			tipusAssumpte = 'NONE';
		}
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/codiAssumpte/"/>" + entitatId + "/" + tipusAssumpte,
			success: function(data) {
				var selCodisAssumpte = $('#selCodiAssumpte');
				selCodisAssumpte.empty();
				selCodisAssumpte.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							selCodisAssumpte.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
						});
					}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selCodisAssumpte.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint els codis d'assumpte...");
			}
		});
	});
	$('#addCodiAssumpteButton').on('click', function(){
		var codiAssumpteSelect = document.getElementById('selCodiAssumpte');
		var codiAssumpteSeleccionatValue = codiAssumpteSelect.options[codiAssumpteSelect.selectedIndex].value;
		var codiAssumpteSeleccionatText = codiAssumpteSelect.options[codiAssumpteSelect.selectedIndex].text;
		$('#codiAssumpte').val(codiAssumpteSeleccionatValue);
		$('#codiAssumpteNom').val(codiAssumpteSeleccionatText);
	});
});
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/procediment/newOrModify"/></c:set>
	<c:forEach items="${errors}" var="error" varStatus="status">
		<c:if test="${error.field == 'llibre' || error.field == 'oficina'}">
			<c:set var="errorsRegistre" value="${error.field}"></c:set>
		</c:if>
		<c:if test="${error.field == 'codi' || error.field == 'nom'}">
			<c:set var="errorsProc" value="${error.field}"></c:set>
		</c:if>
	</c:forEach>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand" role="form">
		<form:hidden path="id"/>
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dadesgeneralsForm" aria-controls="dadesgeneralsForm" role="tab" data-toggle="tab"><spring:message code="procediment.form.titol.dadesgenerals"/><c:if test='${not empty errorsProc}'> <span class="fa fa-warning text-danger"></span></c:if></a></li>
			<li role="presentation"><a href="#registreForm" aria-controls="registreForm" role="tab" data-toggle="tab"><spring:message code="procediment.form.titol.dadesregistre"/><c:if test='${not empty errorsRegistre}'> <span class="fa fa-warning text-danger"></span></c:if></a></li>
		</ul>
		<br>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dadesgeneralsForm">
				<not:inputText name="codi" textKey="procediment.form.camp.codi" required="true" labelSize="2"/>
				<not:inputText name="nom" textKey="procediment.form.camp.nom" required="true" labelSize="2"/>
				<not:inputText name="retard" textKey="procediment.form.camp.retard" labelSize="2"/>
				<not:inputText name="caducitat" textKey="procediment.form.camp.caducitat" labelSize="2"/>
				<form:hidden path="entitatId" value="${entitat.id}"/>
				<form:hidden path="organGestor"/>
				<not:inputTextSearch name="organGestorNom" textKey="procediment.form.camp.organ" searchButton="searchOrgan" required="true" readonly="true" labelSize="2"/>
<%-- 				<not:inputText name="organGestor" textKey="procediment.form.camp.organ" required="true" labelSize="2"/> --%>
				<not:inputSelect name="pagadorPostalId" emptyOption="true" textKey="procediment.form.camp.postal" optionItems="${pagadorsPostal}" optionValueAttribute="id" optionTextAttribute="dir3codi" labelSize="2"/>
				<not:inputSelect name="pagadorCieId" emptyOption="true" textKey="procediment.form.camp.cie" optionItems="${pagadorsCie}" optionValueAttribute="id" optionTextAttribute="dir3codi" labelSize="2"/>
				<not:inputCheckbox name="agrupar" textKey="procediment.form.camp.agrupar" labelSize="2"/>
			</div>
			<div role="tabpanel" class="tab-pane <c:if test='${not empty errorRegistre}'>active</c:if>" id="registreForm">
				<div class="alert alert-warning" role="alert">
				 	<spring:message code="procediment.form.warning"></spring:message>
				</div>
				<not:inputText name="oficina" textKey="procediment.form.camp.oficina" labelSize="2"/>
				<not:inputText name="llibre" textKey="procediment.form.camp.llibre" labelSize="2"/>
				<form:hidden path="tipusAssumpte"/>
				<not:inputTextSearch name="tipusAssumpteNom" textKey="procediment.form.camp.tipusassumpte" searchButton="searchTipusAssumpte" labelSize="2" readonly="true"/>
				<form:hidden path="codiAssumpte"/>
				<not:inputTextSearch name="codiAssumpteNom" textKey="procediment.form.camp.codiassumpte" searchButton="searchCodiAssumpte" labelSize="2" readonly="true"/>
			</div>
		</div>
		<div id="modal-botons">
			<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/procediments"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>	
	</form:form>
	<!-- Organismes Modal -->
	<div class="modal fade" id="organismesModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="procediment.form.titol.organismes"/></h4>
				</div>
				<div class="modal-body body">
					<select id="selOrganismes"></select> 
					<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
						<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
							<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addOrganismeButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.afegir"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>
	<!-- Oficines Modal -->
	<div class="modal fade" id="oficinesModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="procediment.form.titol.oficines"/></h4>
				</div>
				<div class="modal-body body">
					<select id="selOficines"></select> 
				</div>
				<div class="modal-footer">
					<button id="addOficinaButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.afegir"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>
	<!-- Llibres Modal -->
	<div class="modal fade" id="llibresModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="procediment.form.titol.llibres"/></h4>
				</div>
				<div class="modal-body body">
					<select id="selLlibres"></select> 
				</div>
				<div class="modal-footer">
					<button id="addLlibreButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.afegir"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>
	<!-- Tipus Assumpte Modal -->
	<div class="modal fade" id="TipusAssumptesModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="procediment.form.titol.tipusAssumpte"/></h4>
				</div>
				<div class="modal-body body">
					<select id="selTipusAssumpte"></select> 
				</div>
				<div class="modal-footer">
					<button id="addTipusAssumpteButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.afegir"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>
	<!-- Codis Assumpte Modal -->
	<div class="modal fade" id="codisAssumpteModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="procediment.form.titol.codiAssumpte"/></h4>
				</div>
				<div class="modal-body body">
					<select id="selCodiAssumpte"></select> 
				</div>
				<div class="modal-footer">
					<button id="addCodiAssumpteButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.afegir"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>

</body>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<% 
pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
%>
<c:choose>
	<c:when test="${empty procSerCommand.codi}"><c:set var="titol"><spring:message code="servei.form.titol.crear"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="servei.form.titol.modificar"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
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
var entitatDir3 = "${entitat.dir3Codi}";

$(document).ready(function() {
	var select2 = $('select');
	var select2Options = {
			theme: 'bootstrap',
			width: 'auto'};
	select2.select2(select2Options);
	//Organismes
	function loadOrganismes(){
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
					$(".loading-screen").hide();
				},
				error: function() {
					console.log("error obtenint els organismes...");
				}
			});
	};
	loadOrganismes();
	$("#searchOrgan").click(function(){
		var comu = document.getElementById('comu');
		if(comu == null || (comu!= null && !comu.checked)){
			$("#organismesModal").modal();
			var entitatId = $('#entitatId').val();
			loadOrganismes();
			
		}
		
		
	});
	$('#addOrganismeButton').on('click', function(){
		var organSelect = document.getElementById('selOrganismes');
		var organSeleccionatValue = organSelect.options[organSelect.selectedIndex].value;
		var organSeleccionatText = organSelect.options[organSelect.selectedIndex].text;
		$('#organGestor').val(organSeleccionatValue);
		$('#organGestorNom').val(organSeleccionatText);
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
				$(".loading-screen").hide();
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
				$(".loading-screen").hide();
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

	// CANVIS EN EL FORMULARI SEGONS SI EL SERVEI ES CREAT ES COMÚ O NO
	$('#comu').change(function() {
		if (this.checked) {
			$('#organGestorNom').removeClass('habilitat');
			$('#organGestor').val(entitatDir3);
			var organText = '';
			$("#selOrganismes option").each(function(){
				if ($(this).val() == entitatDir3){
					organText = $(this).text();
				}
			});
			$('#organGestorNom').val(organText);
			$('#entrega-cie').hide();
		} else {
			$('#organGestorNom').addClass('habilitat');
			$('#organGestor').val(null);
			$('#organGestorNom').val(null);
			$('#entrega-cie').show();
		}
	});

	if ($('#comu')[0].checked) {
		$('#entrega-cie').hide();
	}
});
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/servei/newOrModify"/></c:set>
	<c:forEach items="${errors}" var="error" varStatus="status">
		<c:if test="${error.field == 'llibre' || error.field == 'oficina'}">
			<c:set var="errorsRegistre" value="${error.field}"></c:set>
		</c:if>
		<c:if test="${error.field == 'codi' || error.field == 'nom'}">
			<c:set var="errorsProc" value="${error.field}"></c:set>
		</c:if>
	</c:forEach>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procSerCommand" role="form">
		<form:hidden path="id"/>
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dadesgeneralsForm" aria-controls="dadesgeneralsForm" role="tab" data-toggle="tab"><spring:message code="procediment.form.titol.dadesgenerals"/><c:if test='${not empty errorsProc}'> <span class="fa fa-warning text-danger"></span></c:if></a></li>
			<li role="presentation"><a href="#registreForm" aria-controls="registreForm" role="tab" data-toggle="tab"><spring:message code="procediment.form.titol.dadesregistre"/><c:if test='${not empty errorsRegistre}'> <span class="fa fa-warning text-danger"></span></c:if></a></li>
		</ul>
		<br>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dadesgeneralsForm">
				<not:inputText name="codi" textKey="procediment.form.camp.codi" required="true" labelSize="2"/>
				<not:inputText name="nom" textKey="procediment.form.camp.nom" required="true" labelSize="2" inputMinLength="2" inputMaxLength="256"/>
				<not:inputText name="retard" textKey="procediment.form.camp.retard" labelSize="2"/>
				<not:inputText name="caducitat" textKey="procediment.form.camp.caducitat" labelSize="2"/>
				<c:choose>
					<c:when test="${isRolActualAdministradorEntitat}">
						<not:inputCheckbox name="comu" textKey="procediment.form.camp.comu" labelSize="2"/>
					</c:when>
					<c:otherwise>
						<form:hidden path="comu"/>
					</c:otherwise>
				</c:choose>
				<not:inputTextSearch  name="organGestorNom" textKey="procediment.form.camp.organ"
									  searchButton="searchOrgan" required="true" readonly="true" labelSize="2"/>
				<form:hidden path="entitatId" value="${entitat.id}"/>
				<form:hidden path="organGestor"/>
				
				<div id="entrega-cie">
					<not:inputCheckbox name="entregaCieActiva" textKey="procediment.form.camp.entregacie" labelSize="2"/>
					<div id="entrega-cie-form">
						<not:inputSelect name="operadorPostalId" optionItems="${operadorPostalList}" optionValueAttribute="id" labelSize="2"
										 optionTextAttribute="text" required="true" emptyOption="true"
										 textKey="entitat.form.camp.operadorpostal" placeholderKey="entitat.form.camp.operadorpostal" optionMinimumResultsForSearch="0"/>
						<not:inputSelect name="cieId" optionItems="${cieList}" optionValueAttribute="id" labelSize="2"
										 optionTextAttribute="text" required="true" emptyOption="true"
										 textKey="entitat.form.camp.cie" placeholderKey="entitat.form.camp.cie" optionMinimumResultsForSearch="0"/>
					</div>
				</div>
				<not:inputCheckbox name="agrupar" textKey="procediment.form.camp.agrupar" labelSize="2"/>
				<not:inputCheckbox name="requireDirectPermission" textKey="procediment.form.camp.requireDirectPermission" labelSize="2"/>

			</div>
			<div role="tabpanel" class="tab-pane <c:if test='${not empty errorRegistre}'>active</c:if>" id="registreForm">
				<div class="alert alert-warning" role="alert">
				 	<spring:message code="procediment.form.warning"></spring:message>
				</div>
				<form:hidden path="tipusAssumpte"/>
				<not:inputTextSearch name="tipusAssumpteNom" textKey="procediment.form.camp.tipusassumpte" searchButton="searchTipusAssumpte" labelSize="2" readonly="true"/>
				<form:hidden path="codiAssumpte"/>
				<not:inputTextSearch name="codiAssumpteNom" textKey="procediment.form.camp.codiassumpte" searchButton="searchCodiAssumpte" labelSize="2" readonly="true"/>
			</div>
		</div>
		<div id="modal-botons">
			<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/serveis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
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
					<select id="selOrganismes" data-placeholder="<spring:message code="procediment.form.camp.organ"/>"></select> 
					<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
						<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
							<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addOrganismeButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.seleccionar"/></button>
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
					<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
						<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
							<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addOficinaButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.seleccionar"/></button>
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
					<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
						<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
							<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addLlibreButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.seleccionar"/></button>
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
					<select id="selTipusAssumpte"  data-placeholder="<spring:message code="procediment.form.camp.tipusassumpte"/>"></select>
					<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
						<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
							<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addTipusAssumpteButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.seleccionar"/></button>
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
					<select id="selCodiAssumpte"  data-placeholder="<spring:message code="procediment.form.camp.codiassumpte"/>"></select>
					<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
						<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
							<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button id="addCodiAssumpteButton" type="button" class="btn btn-info" data-dismiss="modal"><span class="fa fa-plus"></span> <spring:message code="comu.boto.seleccionar"/></button>
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
				</div>
			</div>
		</div>
	</div>

</body>
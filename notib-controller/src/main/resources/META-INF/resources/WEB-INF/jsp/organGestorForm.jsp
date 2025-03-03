<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<c:if test="${!isModificacio}">
		<title><spring:message code="organgestor.form.titol.crear"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></title>
	</c:if>
	<c:if test="${isModificacio}">
		<title><spring:message code="organgestor.form.titol.modificar"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></title>
	</c:if>
	<title><spring:message code="organgestor.form.titol.crear"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
body {
	height: 300px;
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
.comentari {
	font-size: 12px;
	color: #999;
	margin-bottom: 0px;
}
</style>
<script type="text/javascript">


	let operadorsPostal = [];
	operadorsPostal.push({id:"", text:"", estat:"V"});
	<c:forEach items="${operadorPostalList}" var="operadorsPostal">
	operadorsPostal.push({id:"${operadorsPostal.id}", text:"${operadorsPostal.text}", icona:"${operadorsPostal.icona}"});
	</c:forEach>

	let operadorsCie = [];
	operadorsCie.push({id:"", text:"", estat:"V"});
	<c:forEach items="${cieList}" var="operadorsCie">
	operadorsCie.push({id:"${operadorsCie.id}", text:"${operadorsCie.text}", icona:"${operadorsCie.icona}"});
	</c:forEach>


	$(document).ready(function() {

	loadPagadorPostal($("#operadorPostalId"), operadorsPostal, "<spring:message code='operador.postal.obsolet'/>");
	loadPagadorPostal($("#cieId"), operadorsCie, "<spring:message code='operador.postal.obsolet'/>");
	//Organismes
	var entitatId = $('#entitatId').val();
	var select2 = $('select');
	var select2Options = {
			theme: 'bootstrap',
			width: 'auto'};
	select2.select2(select2Options);
	
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
					if (val.codi == "${organGestorCommand.codi}") {
						selOrganismes.append("<option value=\"" + val.codi + "\" selected>" + val.codi + " - " + val.nom + "</option>");

						$('#selOrganismes').trigger('change');
					} else {
						selOrganismes.append("<option value=\"" + val.codi + "\">" + val.codi + " - " + val.nom + "</option>");
					}
				});
			}
			$(".loading-screen").hide();
		},
		error: function() {
			console.log("error obtenint els organismes...");
		}
	});
	var organSeleccionatValue;
	$('#selOrganismes').on('change', function(){
		var organSelect = document.getElementById('selOrganismes');
		organSeleccionatValue = organSelect.options[organSelect.selectedIndex].value;
		var organSeleccionatText = organSelect.options[organSelect.selectedIndex].text;
		$('#codi').val(organSeleccionatValue);
		$('#nom').val(organSeleccionatText.substring(organSeleccionatValue.length + 3));
		
		<c:if test="${setLlibre}">
		$(".loading-screen").show();
		if (organSeleccionatValue !== undefined && organSeleccionatValue !== '') {
			$.ajax({
				type: 'GET',
				url: "<c:url value="/organgestor/llibre/"/>" + organSeleccionatValue,
				success: function(data) {
					var selLlibres = $('#selLlibres');
					selLlibres.empty();
					selLlibres.append("<option value=\"\"></option>");
					if (data) {
						var items = [];
						items.push({
							"id": data.codi,
							"text": data.codi + " - " + data.nomLlarg
						});
						if (data.codi == "${organGestorCommand.llibre}") {
							selLlibres.append("<option value=\"" + data.codi + "\" selected>" + data.codi + " - " + data.nomLlarg + "</option>");
						} else {
							selLlibres.append("<option value=\"" + data.codi + "\">" + data.codi + " - " + data.nomLlarg + "</option>");
						}
					}
					$(".loading-screen").hide();
				},
				error: function() {
					console.log("error obtenint els llibres...");
				}
			});
		} else {
			alert('<spring:message code="procediment.form.avis.llibres"/>');
		}
		
		$('#selLlibres').on('change', function(){
			var llibreSelect = document.getElementById('selLlibres');
			var llibreSeleccionatValue = llibreSelect.options[llibreSelect.selectedIndex].value;
			var llibreSeleccionatText = llibreSelect.options[llibreSelect.selectedIndex].text;
			$('#llibre').val(llibreSeleccionatValue);
			$('#llibreNom').val(llibreSeleccionatText);
		});
		</c:if>
		getOficines(organSeleccionatValue);
	});
	getOficines(organSeleccionatValue);

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
			
	function getOficines(organSeleccionatValue) {
		<c:if test="${setOficina}">
			if ((organSeleccionatValue !== undefined && organSeleccionatValue !== '') || ("${organGestorCommand.codi}" !== "")) {
				$(".loading-screen").show();
				var organ = organSeleccionatValue || "${organGestorCommand.codi}";
				$.ajax({
					type: 'GET',
					url: "<c:url value="/organgestor/oficines/"/>" + organ,
					success: function(data) {
						var selOficines = $('#selOficines');
						selOficines.empty();
						selOficines.append("<option value=\"\"></option>");
						if (data) {
							$('.oficinainfo').addClass('hidden');
							data.forEach(function(oficina) {
								var items = [];
								items.push({
									"id": oficina.codi,
									"text": oficina.codi + " - " + oficina.nom
								});
								if (oficina.codi == "${organGestorCommand.oficina}") {
									selOficines.append("<option value=\"" + oficina.codi + "\" selected>" + oficina.nom + "</option>");
								} else {
									selOficines.append("<option value=\"" + oficina.codi + "\">" + oficina.nom + "</option>");
								}
							})
						} else {
							$('.oficinainfo').removeClass('hidden');
						}
						$(".loading-screen").hide();
					},
					error: function() {
						console.log("error obtenint les oficines...");
					}
				});
			} else {
				if ("${organGestorCommand.codi}" !== "") {
					alert('<spring:message code="procediment.form.avis.oficines"/>');
				}
			}
				
			$('#selOficines').on('change', function(){
				var oficinaSelect = document.getElementById('selOficines');
				var oficinaSeleccionatValue = oficinaSelect.options[oficinaSelect.selectedIndex].value;
				var oficinaSeleccionatText = oficinaSelect.options[oficinaSelect.selectedIndex].text;
				$('#oficina').val(oficinaSeleccionatValue);
				$('#oficinaNom').val(oficinaSeleccionatText);
			});
		</c:if>
	}
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/organgestor/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="organGestorCommand" role="form">
		<form:hidden path="entitatId" value="${entitat.id}"/>
		<div role="tabpanel" class="tab-pane active" id="dadesgeneralsForm">
			<form:hidden path="id"/>
			<form:hidden path="codi"/>
			<form:hidden path="nom"/>
			<c:if test="${!isModificacio}">
				<div class="form-group">
					<label class="control-label col-xs-2" for="selLlibres"><spring:message code="organgestor.form.camp.organisme"/>:</label>
					<div class="controls col-xs-10">
						<select id="selOrganismes" data-placeholder="<spring:message code="organgestor.form.camp.organisme"/>"></select>
					</div>
				</div>
				<c:if test="${setLlibre}">
					<br/>
					<form:hidden path="llibre"/>
					<form:hidden path="llibreNom"/>
					<div class="form-group">
						<label class="control-label col-xs-2" for="selLlibres"><spring:message code="organgestor.form.camp.llibre"/>:</label>
						<div class="controls col-xs-10">
							<select id="selLlibres" data-placeholder="<spring:message code="organgestor.form.camp.llibre"/>"></select>
							<p class="comentari"><spring:message code="organgestor.form.camp.llibre.info"/></p>
						</div>
					</div>
				</c:if>
			</c:if>
			<c:if test="${isModificacio}">
				<ul class="list-group">
					<li class="list-group-item"><b><spring:message code="organgestor.form.camp.codiDir3"/>:</b> ${organGestorCommand.codi}</li>
					<li class="list-group-item"><b><spring:message code="organgestor.form.camp.organisme"/>:</b> ${organGestorCommand.nom}</li>
					<li class="list-group-item"><b><spring:message code="organgestor.form.camp.llibre"/>:</b> ${organGestorCommand.llibreNom}</li>
					<c:if test="${!setOficina}">
						<li class="list-group-item"><b><spring:message code="organgestor.form.camp.codiDir3"/>:</b> ${organGestorCommand.oficinaNom}</li>
					</c:if>
				</ul>
			</c:if>
			<c:if test="${setOficina}">
				<br/>
				<form:hidden path="oficina"/>
				<form:hidden path="oficinaNom"/>
				<div class="form-group">
					<label class="control-label col-xs-2" for="selOficines"><spring:message code="organgestor.form.camp.oficina"/>:</label>
					<div class="controls col-xs-10">
						<select id="selOficines" data-placeholder="<spring:message code="organgestor.form.camp.oficina"/>"></select>
						<p class="comentari oficinainfo hidden" style="color: #856404;"><spring:message code="organgestor.form.camp.oficina.info"/></p>
					</div>
				</div>
			</c:if>
			<not:inputCheckbox name="permetreSir" generalClass="row" textKey="organgestor.form.camp.permetre.sir" labelSize="2"/>
			<c:if test="${isRolActualAdministradorEntitat}">
				<not:inputCheckbox name="entregaCieDesactivada" generalClass="row" textKey="organgestor.form.camp.entregacie.desactivada" labelSize="2"/>
				<c:choose>
					<c:when test="${not empty operadorPostalList && not empty cieList}">
						<not:inputCheckbox name="entregaCieActiva" textKey="organgestor.form.camp.entregacie" labelSize="2" info="${entregaCieHeredada}" messageInfo="organgestor.form.camp.entregacie.heredada"/>
						<div id="entrega-cie-form">
							<not:inputSelect name="operadorPostalId" optionItems="${operadorPostalList}" optionValueAttribute="id" labelSize="2"
											 optionTextAttribute="text" required="true" emptyOption="true"
											 textKey="entitat.form.camp.operadorpostal" placeholderKey="entitat.form.camp.operadorpostal" optionMinimumResultsForSearch="0"/>
							<not:inputSelect name="cieId" optionItems="${cieList}" optionValueAttribute="id" labelSize="2"
											 optionTextAttribute="text" required="true" emptyOption="true"
											 textKey="entitat.form.camp.cie" placeholderKey="entitat.form.camp.cie" optionMinimumResultsForSearch="0"/>
						</div>
					</c:when>
					<c:otherwise>
						<not:inputCheckbox disabled="true" info="true" messageInfo="organgestor.form.camp.entregacie.no.configurada" name="entregaCieActiva" labelSize="2" generalClass="row" textKey="organgestor.form.camp.entregacie"/>
					</c:otherwise>
				</c:choose>
			</c:if>
			<div class="loading-screen" style="text-align: center; width:100%; hight: 80px;">
				<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
					<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
				</div>
			</div>
		</div>
		<div id="modal-botons">
			<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/organgestor"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>	
	</form:form>
</body>


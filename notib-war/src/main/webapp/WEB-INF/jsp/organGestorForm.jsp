<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="organgestor.form.titol.crear"/> ${entitat.nom} <c:out value=" (${entitat.dir3Codi})"></c:out></title>
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
</style>
<script type="text/javascript">
$(document).ready(function() {
	//Organismes
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
	$('#selOrganismes').on('change', function(){
		var organSelect = document.getElementById('selOrganismes');
		var organSeleccionatValue = organSelect.options[organSelect.selectedIndex].value;
		var organSeleccionatText = organSelect.options[organSelect.selectedIndex].text;
		$('#codi').val(organSeleccionatValue);
		$('#nom').val(organSeleccionatText.substring(organSeleccionatValue.length + 3));
	});
});
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/organgestor/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="organGestorCommand" role="form">
		<form:hidden path="entitatId" value="${entitat.id}"/>
		<div role="tabpanel" class="tab-pane active" id="dadesgeneralsForm">
			<form:hidden path="codi"/>
			<form:hidden path="nom"/>
			<select id="selOrganismes"></select> 
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
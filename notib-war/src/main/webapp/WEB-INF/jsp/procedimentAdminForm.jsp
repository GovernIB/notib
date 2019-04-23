<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty procedimentCommand.codi}"><c:set var="titol"><spring:message code="procediment.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="procediment.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
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
	$('#entitatId').change(function(value){
		var entitatId = $(this).val();
		
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/tipusAssumpte/"/>" + entitatId,
			success: function(data) {
				var selTipusAssumpte = $('#tipusAssumpte');
				var selCodiAssumpte = $('#codiAssumpte');
				selTipusAssumpte.empty();
				selTipusAssumpte.append("<option value=\"\"></option>");
				selCodiAssumpte.empty();
				selCodiAssumpte.append("<option value=\"\"></option>");
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
				var select2Options = {theme: 'bootstrap'};
				selTipusAssumpte.select2("destroy");
				selTipusAssumpte.select2(select2Options);
				selCodiAssumpte.select2("destroy");
				selCodiAssumpte.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint els codis d'assumpte..");
			}
		});
	});
	
	$('#tipusAssumpte').change(function(value){
		var codiTipusAssumpte = $(this).val();
		var entitatId = $('#entitatId').val();
		
		$.ajax({
			type: 'GET',
			url: "<c:url value="/procediment/codiAssumpte/"/>" + entitatId + "/" + codiTipusAssumpte,
			success: function(data) {
				var selCodiAssumpte = $('#codiAssumpte');
				selCodiAssumpte.empty();
				selCodiAssumpte.append("<option value=\"\"></option>");
				if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							selCodiAssumpte.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
						});
					}
				var select2Options = {theme: 'bootstrap'};
				selCodiAssumpte.select2("destroy");
				selCodiAssumpte.select2(select2Options);
			},
			error: function() {
				console.log("error obtenint els codis d'assumpte..");
			}
		});
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
				<not:inputText name="retard" textKey="notificacio.form.camp.retard" labelSize="2"/>
				<c:choose>
				  <c:when test="${entitats != null}">
						<not:inputSelect name="entitatId" textKey="procediment.form.camp.entitat" optionItems="${entitats}" optionValueAttribute="id" optionTextAttribute="nom" required="true" labelSize="2"/>
				  </c:when>
				  <c:otherwise>
				    	<form:hidden path="entitatId" value="${entitat.id}"/>
						<not:inputText name="entitatNom" textKey="procediment.form.camp.entitat" value="${entitat.nom}" required="true" readonly="true" labelSize="2"/>
				  </c:otherwise>
				</c:choose>
				<not:inputSelect name="pagadorPostalId" emptyOption="true" textKey="procediment.form.camp.postal" optionItems="${pagadorsPostal}" optionValueAttribute="id" optionTextAttribute="dir3codi" labelSize="2"/>
				<not:inputSelect name="pagadorCieId" emptyOption="true" textKey="procediment.form.camp.cie" optionItems="${pagadorsCie}" optionValueAttribute="id" optionTextAttribute="dir3codi" labelSize="2"/>
				<not:inputCheckbox name="agrupar" textKey="procediment.form.camp.agrupar" labelSize="2"/>
			</div>
			<div role="tabpanel" class="tab-pane <c:if test='${not empty errorRegistre}'>active</c:if>"" id="registreForm">
				<not:inputText name="llibre" textKey="procediment.form.camp.llibre" required="true" labelSize="2"/>
				<not:inputText name="oficina" textKey="procediment.form.camp.oficina" required="true" labelSize="2"/>
				<not:inputSelect name="tipusAssumpte" id="tipusAssumpte" textKey="procediment.form.camp.tipusassumpte" optionItems="${tipusAssumpte}"  optionValueAttribute="codi" optionTextAttribute="nom" labelSize="2"/>
				<not:inputSelect name="codiAssumpte" id="codiAssumpte" textKey="procediment.form.camp.codiassumpte" optionItems="${codiAssumpte}" emptyOption="true" emptyOptionTextKey="procediment.form.cap" optionValueAttribute="codi" optionTextAttribute="nom" labelSize="2"/>
			</div>
		</div>
		<div id="modal-botons">
			<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/procediments"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>	
	</form:form>
	
</body>
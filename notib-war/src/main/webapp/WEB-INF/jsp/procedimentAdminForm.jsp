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
	$('#tipusAssumpte').change(function(){
		webutilModalAdjustHeight();
	});
});
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/procediment/newOrModify"/></c:set>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#dadesgeneralsForm" aria-controls="dadesgeneralsForm" role="tab" data-toggle="tab"><spring:message code="procediment.form.titol.dadesgenerals"/></a></li>
		<li role="presentation"><a href="#registreForm" aria-controls="registreForm" role="tab" data-toggle="tab"><spring:message code="procediment.form.titol.dadesregistre"/></a></li>
	</ul>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand" role="form">
		<form:hidden path="id"/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dadesgeneralsForm">
				<not:inputText name="codi" textKey="procediment.form.camp.codi" required="true"/>
				<not:inputText name="nom" textKey="procediment.form.camp.nom" required="true"/>
				<not:inputText name="codisia" textKey="procediment.form.camp.codisia" required="true"/>
				<not:inputText name="retard" textKey="notificacio.form.camp.retard"/>
				<c:choose>
				  <c:when test="${entitats != null}">
						<not:inputSelect name="entitatId" textKey="procediment.form.camp.entitat" optionItems="${entitats}" optionValueAttribute="id" optionTextAttribute="nom" required="true"/>
				  </c:when>
				  <c:otherwise>
				    	<form:hidden path="entitatId" value="${entitatId}"/>
						<not:inputText name="entitatNom" textKey="procediment.form.camp.entitat" value="${entitat.nom}" required="true" readonly="true"/>
				  </c:otherwise>
				</c:choose>
				<not:inputSelect name="pagadorPostalId" textKey="procediment.form.camp.postal" optionItems="${pagadorsPostal}" optionValueAttribute="id" optionTextAttribute="dir3codi" required="true"/>
				<not:inputSelect name="pagadorCieId" textKey="procediment.form.camp.cie" optionItems="${pagadorsCie}" optionValueAttribute="id" optionTextAttribute="dir3codi"/>
				<not:inputCheckbox name="agrupar" textKey="procediment.form.camp.agrupar"/>
				
			</div>
			<div role="tabpanel" class="tab-pane" id="registreForm">
				<not:inputText name="llibre" textKey="procediment.form.camp.llibre"/>
				<not:inputText name="oficina" textKey="procediment.form.camp.oficina"/>
				<not:inputSelect name="tipusAssumpte" textKey="procediment.form.camp.tipusassumpte" optionItems="${tipusAssumpteEnum}"  optionValueAttribute="value" optionTextKeyAttribute="text"/>
			</div>
		</div>
		<div id="modal-botons">
				<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
				<a href="<c:url value="/procediments"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>	
	</form:form>
	
</body>
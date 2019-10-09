<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="usuari.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<not:modalHead/>
<script type="text/javascript">
$(document).ready(function() {
	$("#rols").prop("disabled", true);
});
</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/usuari/configuracio"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariCommand" role="form">
		<form:hidden path="codi"/>
		<not:inputText name="nom" textKey="usuari.form.camp.nom" disabled="true"/>
		<not:inputText name="nif" textKey="usuari.form.camp.nif" disabled="true"/>
		<not:inputText name="email" textKey="usuari.form.camp.email" disabled="true"/>
		<not:inputSelect name="rols" textKey="usuari.form.camp.rols" optionItems="${usuariCommand.rols}" disabled="true"/>
		<not:inputCheckbox name="rebreEmailsNotificacio" textKey="usuari.form.camp.rebre.emails.notificacio"/>
		<not:inputSelect name="idioma" optionItems="${idiomaEnumOptions}" textKey="usuari.form.camp.idioma" optionValueAttribute="value" optionTextKeyAttribute="text" disabled="false"/>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/usuari/configuracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

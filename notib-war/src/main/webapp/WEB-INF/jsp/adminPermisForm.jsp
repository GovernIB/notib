<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="permis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>

	<title>${titol}</title>
	
	<link href="<c:url value="/css/permisos.css"/>" rel="stylesheet" type="text/css">
	
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<not:modalHead/>
	
	<script>
	
		$(document).ready(function() {
			$("#modal-botons button[type='submit']").on('click', function() {
				$("form#permisCommand *:disabled").attr('readonly', 'readonly');
				$("form#permisCommand *:disabled").removeAttr('disabled');
			});
		});
		
	</script>

</head>

<body>

	<%-- <c:set var="formAction"><not:modalUrl value="ppermis/create"/></c:set> --%>
	<form:form action="create" method="post" cssClass="form-horizontal" commandName="permisCommand">
	
		<form:hidden path="id"/>
		
		<div class="col-xs-10">
			<not:inputSelect name="Tipus" textKey="permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionEnum="TipusEnumDto"/>
		</div>
		<div class="col-xs-10">
			<not:inputText name="Nom" textKey="permis.form.camp.nom" required="true" disabled="${not empty permisCommand.id}"/>
		</div>
		<div class="col-xs-10">
			<not:inputSelect name="permis" textKey="permis.form.camp.permis" optionEnum="PermisosEnumDto" />
		</div>
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>

</html>

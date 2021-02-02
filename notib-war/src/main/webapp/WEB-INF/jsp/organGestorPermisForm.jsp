<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"isRolActualAdministradorOrgan",
		es.caib.notib.war.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(request));
%>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="organgestor.permis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="procediment.permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<not:modalHead/>
<script>
	$(document).ready(function() {
		$("#modal-botons button[type='submit']").on('click', function() {
			$("form#permisCommand *:disabled").attr('readonly', 'readonly');
			$("form#permisCommand *:disabled").removeAttr('disabled');
		});

		$("#selectAll").on('change', function() {
			if ($(this).prop("checked"))
				$("div.permisosInput :checkbox").prop('checked', true);
			else
				$("div.permisosInput :checkbox").prop('checked', false);
		});

		$("div.permisosInput :checkbox").on('change', function() {
			var totsSeleccionats = true;
			$("div.permisosInput :checkbox").each(function() {
				  if(!$(this).prop('checked'))
					  totsSeleccionats = false;
			});
			$("#selectAll").prop('checked', totsSeleccionats);
		});
	});
</script>
<style>
	.permisosInput {margin-left: 45px}
</style>


</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/organgestor/${organGestor.id}/permis"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<form:hidden path="id"/>
		<not:inputSelect name="tipus" textKey="procediment.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionEnum="TipusEnumDto"/>
		<c:url value="/userajax/usuari" var="urlConsultaInicial"/>
		<c:url value="/userajax/usuaris" var="urlConsultaLlistat"/>
		<not:inputText name="principal" required="true" textKey="entitat.permis.form.camp.principal" disabled="${not empty permisCommand.id}" placeholderKey="entitat.permis.form.camp.principal"/>
		<div class="row">
			<div class="col-xs-6">
				<not:inputCheckbox name="selectAll" labelSize="8" textKey="procediment.permis.form.camp.all"/>
				<div class="permisosInput">
					<not:inputCheckbox name="read" labelSize="8" textKey="procediment.permis.form.camp.consulta"/>
					<not:inputCheckbox name="processar" labelSize="8" textKey="procediment.permis.form.camp.processar"/>
					<not:inputCheckbox name="notificacio" labelSize="8" textKey="procediment.permis.form.camp.notificacio"/>
					<not:inputCheckbox name="administration" labelSize="8" textKey="procediment.permis.form.camp.gestio"/>
				</div>
				<not:inputCheckbox name="administrador" labelSize="8" textKey="procediment.permis.form.camp.administrador" disabled="${isRolActualAdministradorOrgan}"/>
			</div>
			<div class="col-xs-6">
				<not:inputCheckbox name="comuns" textKey="organgestor.permis.form.camp.comuns"/>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/organgestor/${organGestor.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

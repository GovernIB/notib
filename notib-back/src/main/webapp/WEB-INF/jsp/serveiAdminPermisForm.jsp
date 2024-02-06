<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(ssc.getRolActual()));
%>

<c:choose>
	<c:when test="${empty procSerPermisCommand.id}"><c:set var="titol"><spring:message code="procediment.permis.form.titol.crear"/></c:set></c:when>
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

	//Reset de errores de validación
	function resetErrors() {
		$("#id_error").remove();
		$("#principal").parent().closest('.form-group').removeClass('has-error');
	}

	function errorRolTothomPerAdminOrgan() {
		if ($("#tipus").val().toUpperCase() == "ROL" && $("#principal").val().trim().toLowerCase() == 'tothom' && isRolActualAdministradorOrgan) {
			$("#principal").parent().closest('.form-group').addClass('has-error');
			$("#principal").parent().append('<div id="id_error"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<spring:message code="procediment.permis.form.camp.principal.error"/></p></div>');
			return true;
		}
		return false;
	}

	function formatRolUsuari() {
		if ($("#tipus").val().toUpperCase() == "ROL") {
			if ($("#principal").val().trim().toLowerCase() == "tothom")
				$("#principal").val($("#principal").val().trim().toLowerCase());
			else
				$("#principal").val($("#principal").val().trim().toUpperCase());
		} else { // "USUARI"
			$("#principal").val($("#principal").val().trim().toLowerCase());
		}
	}

	$(document).ready(function() {
		$("#modal-botons button[type='submit']").on('click', function() {
			$("form#procSerPermisCommand *:disabled").attr('readonly', 'readonly');
			$("form#procSerPermisCommand *:disabled").removeAttr('disabled');
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

		$("#principal").on('change', function() {
			resetErrors();

			if (errorRolTothomPerAdminOrgan())
				return;

			formatRolUsuari();
		});

		$("#tipus").on('change', function() {
			resetErrors();
			errorRolTothomPerAdminOrgan();
			formatRolUsuari();
		});
	});
</script>
<style>
	.permisosInput {margin-left: 45px}
	.check-label {display: flex; align-items: center;}
	.check-label>span {font-size: 24px; padding-right: 15px; width: 50px; color: #888;}
	.checkbox-primary {text-align: right; padding-right: 30px;}
	.control-label, .controls { cursor: pointer;}
</style>


</head>
<body>
	<script>
		var isRolActualAdministradorOrgan = ${isRolActualAdministradorOrgan};
	</script>
	<c:set var="formAction"><not:modalUrl value="/servei/${servei.id}/permis"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="procSerPermisCommand">
		<form:hidden path="id"/>
		<c:if test="${not servei.comu}">
			<form:hidden path="organ"/>
		</c:if>
		<not:inputSelect name="tipus" textKey="procediment.permis.form.camp.tipus" disabled="${not empty procSerPermisCommand.id}" optionEnum="TipusEnumDto"/>
		<not:inputText name="principal" required="true" textKey="entitat.permis.form.camp.principal" disabled="${not empty procSerPermisCommand.id}" placeholderKey="entitat.permis.form.camp.principal"
			inputMaxLength="${principalSize}" showsize="true"/>
		<c:if test="${servei.comu}">
			<not:inputSelect
				name="organ"
				textKey="entitat.permis.form.camp.organ"
				disabled="${not empty procSerPermisCommand.id}"
				optionItems="${organs}"
				optionTextAttribute="nomComplet"
				optionValueAttribute="codi"
				optionMinimumResultsForSearch="5"
				emptyOption="true"/>
		</c:if>

		<div class="row" style="margin-right: 0px; margin-left: 0px;">
			<div class="form-group" title="<spring:message code="procediment.permis.all.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="selectAll"><span class="fa fa-toggle-on"></span> <spring:message code="procediment.permis.form.camp.all"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="selectAll" cssClass="span12" id="selectAll" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
		</div>
		<div class="permisosInput">
			<div class="form-group" title="<spring:message code="procediment.permis.consulta.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="read"><span class="fa fa-search"></span> <spring:message code="procediment.permis.form.camp.consulta"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="read" cssClass="span12" id="read" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="procediment.permis.processar.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="processar"><span class="fa fa-check-square-o"></span> <spring:message code="procediment.permis.form.camp.processar"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="processar" cssClass="span12" id="processar" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="procediment.permis.gestio.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="administration"><span class="fa fa-cog"></span> <spring:message code="procediment.permis.form.camp.gestio"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="administration" cssClass="span12" id="administration" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="procediment.permis.notificacio.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="notificacio"><span class="fa fa-gavel"></span> <spring:message code="procediment.permis.form.camp.notificacio"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="notificacio" cssClass="span12" id="notificacio" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="procediment.permis.comunicacio.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="comunicacio"><span class="fa fa-envelope-o"></span> <spring:message code="procediment.permis.form.camp.comunicacio"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="comunicacio" cssClass="span12" id="comunicacio" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="procediment.permis.comunicacio.sir.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="comunicacioSir"><span class="fa fa-envelope"></span> <spring:message code="procediment.permis.form.camp.comunicacio.sir"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="comunicacioSir" cssClass="span12" id="comunicacioSir" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/servei/${servei.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

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
	.permisosInput {margin-left: 45px;}
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
	<c:set var="formAction"><not:modalUrl value="/organgestor/${organGestor.id}/permis"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="permisCommand">
		<form:hidden path="id"/>
		<not:inputSelect name="tipus" textKey="procediment.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionEnum="TipusEnumDto"/>
		<c:url value="/userajax/usuari" var="urlConsultaInicial"/>
		<c:url value="/userajax/usuaris" var="urlConsultaLlistat"/>
		<not:inputText name="principal" required="true" textKey="entitat.permis.form.camp.principal" disabled="${not empty permisCommand.id}" placeholderKey="entitat.permis.form.camp.principal"
			inputMaxLength="${principalSize}" showsize="true"/>
		<div class="row" style="margin-right: 0px; margin-left: 0px;">
			<div class="form-group" title="<spring:message code="organ.permis.administrador.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="administrador"><span class="fa fa-user-plus"></span> <spring:message code="procediment.permis.form.camp.administrador"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="administrador" cssClass="span12" id="administrador" disabled="${isRolActualAdministradorOrgan}" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
				<%--		<not:inputCheckbox name="administrador" labelSize="4" textKey="procediment.permis.form.camp.administrador" disabled="${isRolActualAdministradorOrgan}"/>--%>
		</div>
		<div class="row" style="margin-right: 0px; margin-left: 0px;">
			<div class="form-group" title="<spring:message code="organ.permis.all.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="selectAll"><span class="fa fa-toggle-on"></span> <spring:message code="procediment.permis.form.camp.all"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="selectAll" cssClass="span12" id="selectAll" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--			<not:inputCheckbox name="selectAll" labelSize="4" textKey="procediment.permis.form.camp.all"/>--%>
		</div>
		<div class="permisosInput">
			<div class="form-group" title="<spring:message code="organ.permis.consulta.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="read"><span class="fa fa-search"></span> <spring:message code="procediment.permis.form.camp.consulta"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="read" cssClass="span12" id="read" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--		<not:inputCheckbox name="read" labelSize="4" textKey="procediment.permis.form.camp.consulta"/>--%>
			<div class="form-group" title="<spring:message code="organ.permis.processar.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="processar"><span class="fa fa-check-square-o"></span> <spring:message code="procediment.permis.form.camp.processar"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="processar" cssClass="span12" id="processar" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--			not:inputCheckbox name="processar" labelSize="4" textKey="procediment.permis.form.camp.processar"/>--%>
			<div class="form-group" title="<spring:message code="organ.permis.gestio.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="administration"><span class="fa fa-cog"></span> <spring:message code="procediment.permis.form.camp.gestio"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="administration" cssClass="span12" id="administration" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--			not:inputCheckbox name="administration" labelSize="4" textKey="procediment.permis.form.camp.gestio"/>--%>
			<div class="form-group" title="<spring:message code="organ.permis.comuns.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="comuns"><span class="fa fa-globe"></span> <spring:message code="organgestor.permis.form.camp.comuns"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="comuns" cssClass="span12" id="comuns" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--			not:inputCheckbox name="comuns" labelSize="4" textKey="organgestor.permis.form.camp.comuns"/>--%>
			<div class="form-group" title="<spring:message code="organ.permis.notificacio.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="notificacio"><span class="fa fa-gavel"></span> <spring:message code="procediment.permis.form.camp.notificacio"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="notificacio" cssClass="span12" id="notificacio" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--			not:inputCheckbox name="notificacio" labelSize="4" textKey="procediment.permis.form.camp.notificacio"/>--%>
			<div class="form-group" title="<spring:message code="organ.permis.comunicacio.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="comunicacio"><span class="fa fa-envelope-o"></span> <spring:message code="procediment.permis.form.camp.comunicacio"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="comunicacio" cssClass="span12" id="comunicacio" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="organ.permis.comunicacio.sir.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="comunicacioSir"><span class="fa fa-envelope"></span> <spring:message code="procediment.permis.form.camp.comunicacio.sir"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="comunicacioSir" cssClass="span12" id="comunicacioSir" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
			<div class="form-group" title="<spring:message code="organ.permis.comunicacio.sense.proc.info"/>">
				<label class="control-label col-xs-6 col-xs-offset-4 check-label" for="comunicacioSir"><span class="fa fa-paper-plane-o"></span> <spring:message code="procediment.permis.form.camp.comunicacio.sense.procediment"/></label>
				<div class="controls col-xs-2">
					<div class="checkbox checkbox-primary">
						<label class="form-switch"><form:checkbox path="comunicacioSenseProcediment" cssClass="span12" id="comunicacioSenseProcediment" autocomplete="off"/><i></i></label>
					</div>
				</div>
			</div>
<%--		<not:inputCheckbox name="comunicacioSir" labelSize="4" textKey="procediment.permis.form.camp.comunicacio.sir"/>--%>
		</div>
<%--		</div>--%>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/organgestor/${organGestor.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
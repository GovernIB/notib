<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="entitat.permis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="entitat.permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/permisos.css"/>" rel="stylesheet" type="text/css">
	<not:modalHead/>
	<script>
	
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
			
			<c:if test="${empty permisCommand.id}">
				disableGuardarIfNoneChecked();
				$("input[type='checkbox']").change(function( index, element ) { 
					disableGuardarIfNoneChecked();
				});
			</c:if>		

			$("#principal").on('change', function() {
				formatRolUsuari();
			});
			
			$("#tipus").on('change', function() {
				formatRolUsuari();
			});

			$("#guardar").click(() => {
				let principal = $("#principal").val();
				<c:if test="${not empty permisCommand.id}">
					$("#permis-form").submit();
				</c:if>

				$.ajax({type: "GET", url: principal + "/existeix", error: err => console.error(err),
					success: existeix => {
						if (!existeix || confirm("<spring:message code="entitat.permis.form.confirmar.upsert"/>")) {
							$("#permis-form").submit();
						}
					}
				});
			})
		});
		
		function disableGuardarIfNoneChecked(){
			var anyChecked=false;
			$("input[type='checkbox']").each(function( index, element ) { 
				if ($( element ).is(':checked')){
				anyChecked=true;			
				}
			}); 
			if(anyChecked==true){
				$(".submitDialog",parent.document).prop('disabled',false)
				$(".submitDialog").prop('disabled',false)

			} else {
				$(".submitDialog",parent.document).prop('disabled',true)
				$(".submitDialog").prop('disabled',true)

			}
		}
	</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/entitat/${entitat.id}/permis"/></c:set>
	<form:form id="permis-form" action="${formAction}" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<form:hidden path="id"/>
		<not:inputSelect name="tipus" textKey="entitat.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionEnum="TipusEnumDto" />
		<not:inputText name="principal" textKey="entitat.permis.form.camp.principal" readonly="${not empty permisCommand.id}" inputMaxLength="${principalSize}" showsize="true"/>
		<not:inputCheckbox name="usuari" textKey="entitat.permis.form.camp.usuari"/>
		<not:inputCheckbox name="administradorEntitat" textKey="entitat.permis.form.camp.administradorentitat"/>
		<not:inputCheckbox name="aplicacio" textKey="entitat.permis.form.camp.aplicacio"/>
		<div id="modal-botons" class="col-xs-12 text-right">
			<div id="guardar" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></div>
			<a href="<c:url value="/entitats/${entitatId}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

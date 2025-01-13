<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty aplicacioCommand.id}"><c:set var="titol"><spring:message code="aplicacio.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="aplicacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>${titol}</title>
	<not:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="text/javascript">
			$(document).ready(function() {
				$("#btnProvar").click(e => {
					e.preventDefault();
					$.ajax({
						method: "GET",
						url: "<c:url value="/entitat/${entitat.id}/aplicacio/${aplicacioCommand.id}/provar/ajax"/>",
						async: true,
						success: function(data){
							let classe = data && !data.includes("Error") ? "alert-success" : "alert-danger";
							let div = '<div class="alert ' + classe +'">' + data + '</div>';
							$("#contingut-missatges").append(div);
						},
						error: error => console.log(error)
					});
				});
			});
	</script>
</head>
<body>
	<c:set var="createAplication"><not:modalUrl value="/entitat/${entitat.id}/aplicacio/newOrModify"/></c:set>
	<form:form action="${createAplication}"  method="post" cssClass="form-horizontal" modelAttribute="aplicacioCommand" role="form">
		<form:hidden path="id"/>
		<input type="hidden" name="entitatId" value="${entitat.id}"/>
<%--		<c:choose>--%>
<%--			<c:when test="${!empty aplicacioCommand.id}">--%>
				<not:inputText name="usuariCodi" textKey="aplicacio.form.camp.codi" required="true" readonly="${!empty aplicacioCommand.id}"/>
<%--			</c:when>--%>
<%--			<c:otherwise>--%>
<%--				<not:inputSuggest name="usuariCodi" inline="false" placeholderKey="aplicacio.form.camp.codi" textKey="aplicacio.form.camp.codi"--%>
<%--								  urlConsultaInicial="../../../../userajax/usuariDades" urlConsultaLlistat="../../../../userajax/usuarisDades"--%>
<%--								  suggestValue="codi" suggestText="codi" minimumInputLength="2" />--%>
<%--			</c:otherwise>--%>
<%--		</c:choose>--%>

		<not:inputText name="callbackUrl" textKey="aplicacio.form.camp.callback.url" required="true"/>
		<not:inputCheckbox name="headerCsrf" generalClass="row" textKey="aplicacio.form.camp.callback.header.csrf"/>
		<div id="modal-botons">
			<c:if test ="${!empty aplicacioCommand.id}">
				<button id="btnProvar" class="btn btn-primary" href="<c:url value="/entitat/${entitat.id}/aplicacio/${aplicacioCommand.id}/provar"/>"><span class="fa fa-cog"></span>&nbsp;&nbsp;<spring:message code="comu.boto.provar"/></button>
			</c:if>
			<button id="btnSubmit" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat/${entitat.id}/aplicacio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
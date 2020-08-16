<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty grupCommand.id}"><c:set var="titol"><spring:message code="procediment.grup.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="procediment.grup.form.titol.modificar"/></c:set></c:otherwise>
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
<style>
.permisosInput {
	margin-left: 45px
}
</style>

</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/procediment/${procediment.id}/grup"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentGrupCommand">
		<form:hidden path="id"/>
<%-- 		<not:inputSelect name="tipus" textKey="procediment.grup.form.camp.tipus" optionItems="${TipusGrupEnum}"  optionValueAttribute="value" optionTextKeyAttribute="text"/> --%>
		<not:inputSelect name="grupId" textKey="procediment.grup.columna.tipus.grup" optionItems="${grups}" optionValueAttribute="id" optionTextAttribute="nomIRol" required="true"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/procediment/${procediment.id}/grup"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

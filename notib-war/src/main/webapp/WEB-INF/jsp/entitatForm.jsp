<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty entitatCommand.id}"><c:set var="titol"><spring:message code="entitat.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="entitat.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<not:modalHead/>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/entitat"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="entitatCommand" role="form">
		<form:hidden path="id"/>
		<not:inputText name="codi" textKey="entitat.form.camp.codi" required="true"/>
		<not:inputText name="nom" textKey="entitat.form.camp.nom" required="true"/>
		<not:inputSelect name="tipus" textKey="entitat.form.camp.tipus" optionEnum="EntitatTipusEnumDto" required="true"/>
		<not:inputText name="dir3Codi" textKey="entitat.form.camp.codidir3" required="true"/>
		<not:inputTextarea name="descripcio" textKey="entitat.form.camp.descripcio"/>
		<div id="modal-botons" class="col-xs-12 text-right">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

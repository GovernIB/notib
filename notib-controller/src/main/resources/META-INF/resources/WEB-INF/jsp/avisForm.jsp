<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty avisCommand.id}"><c:set var="titol"><spring:message code="avis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="avis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
	<not:modalHead/>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/avis"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="avisCommand" role="form">
		<form:hidden path="id"/>
		<not:inputText name="assumpte" textKey="avis.form.camp.assumpte" required="true"/>
		<not:inputTextarea name="missatge" textKey="avis.form.camp.missatge" required="true"/>
		<not:inputDate name="dataInici" textKey="avis.form.camp.dataInici" required="true"/>
		<not:inputDate name="dataFinal" textKey="avis.form.camp.dataFinal" required="true"/>
		<not:inputSelect name="avisNivell" textKey="avis.form.camp.avisNivell" optionEnum="AvisNivellEnumDto" required="true"/>
		
		<div id="modal-botons" class="well">>
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/avis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
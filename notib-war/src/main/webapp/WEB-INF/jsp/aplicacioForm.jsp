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
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script type="text/javascript"></script>
</head>

<body>

	<c:set var="createAplication"><not:modalUrl value="/aplicacions/save"/></c:set>
	<form:form action="${createAplication}" method="post" cssClass="form-horizontal" commandName="aplicacioCommand" role="form">
		<form:hidden path="id"/>
		<div class="col-xs-6"><not:inputText name="usuariCodi" textKey="aplicacio.form.camp.codi" required="true" labelSize="5"/></div>
		<div class="col-xs-10"><not:inputText name="callbackUrl" textKey="aplicacio.form.camp.urlcallback" required="true" labelSize="3"/></div>
		<div id="modal-botons">
			<button id="btnSubmit" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/aplicacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	
</body>

</html>

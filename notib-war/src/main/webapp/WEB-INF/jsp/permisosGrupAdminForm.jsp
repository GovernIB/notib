<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty procedimentCommand.codi}"><c:set var="titol"><spring:message code="procediment.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="procediment.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/procediment/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand" role="form">
		<form:hidden path="id"/>
		<div class="row">
			<div class="col-md-3">
				<not:inputText name="codi" textKey="procediment.form.camp.codi" required="true"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="nom" textKey="procediment.form.camp.nom"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="codisia" textKey="procediment.form.camp.codisia"/>
			</div>
			<c:choose>
			  <c:when test="${entitats != null}">
			    <div class="col-md-2">
					<not:inputSelect name="entitatId" textKey="procediment.form.camp.entitat" optionItems="${entitats}" optionValueAttribute="id" optionTextAttribute="nom" required="true"/>
				</div>
			  </c:when>
			  <c:otherwise>
			    <div class="col-md-2">
					<not:inputText name="entitatId" textKey="procediment.form.camp.entitat" required="true" value="${entitat.nom}" disabled="true"></not:inputText>
				</div>
			  </c:otherwise>
			</c:choose>
			<div class="col-md-2">
				<not:inputSelect name="pagadorPostalId" textKey="procediment.form.camp.postal" optionItems="${pagadorsPostal}" optionValueAttribute="id" optionTextAttribute="dir3codi" required="true"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect name="pagadorCieId" textKey="procediment.form.camp.cie" optionItems="${pagadorsCie}" optionValueAttribute="id" optionTextAttribute="dir3codi"/>
			</div>
			<div class="col-md-2">
				<not:inputCheckbox name="agrupar" textKey="procediment.form.camp.agrupar"/>
			</div>
			<div id="modal-botons">
				<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
				<a href="<c:url value="/procediments"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</div>	
		</div>
	</form:form>
	
</body>
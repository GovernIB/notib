<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
    <title><spring:message code="anular.form.titol"/></title>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <not:modalHead/>
</head>
<body>
<c:set var="formAction"><not:modalUrl value="/notificacio/anular"/></c:set>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="anularCommand" role="form">
    <form:hidden path="notificacioId"/>
    <form:hidden path="enviamentId"/>
    <form:hidden path="notificacionsId"/>
    <form:hidden path="enviamentsId"/>
    <form:hidden path="massiu"/>
    <form:hidden path="seleccioTipus"/>
    <div class="row">
        <div class="col-md-2">
            <not:inputTextarea name="motiu" textKey="ampliar.plazo.form.motiu" required="true" inputMaxLength="${motiuSize}"/>
        </div>
        <div id="modal-botons">
            <button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
            <a href="<c:url value="/grup"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
        </div>
    </div>
</form:form>
</body>

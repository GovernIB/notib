<%@page import="es.caib.notib.core.api.dto.TipusDocumentEnumDto"%>
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

<%    pageContext.setAttribute("tipusDocumentEnumDto", TipusDocumentEnumDto.values()); %>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
    <script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
<style type="text/css">
.title {
	text-align: center;
}
.title > label {
	color: #ff9523;
}
.title > hr {
	margin-top: 0%;
}
.select2-container--bootstrap {
	width: 100% !important;
}
</style>
<script type="text/javascript">
$(document).ready(function() { 
	var entitatId = document.getElementById('id').value;
	if (entitatId != '') {
		var getUrl = "<c:url value="/entitat/"/>" + entitatId + "/tipusDocument";
		 $.get(getUrl).done(function(data) {
			$('.customSelect').webutilInputSelect2(data);
		 });
	} else {
		$('.customSelect').webutilInputSelect2(null);
	}
});
</script>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
        <li role="presentation"><a href="#dadesForm" aria-controls="dadesForm" role="tab" data-toggle="tab"><spring:message code="entitat.form.titol.dades"/></a></li>
        <li role="presentation" class="active"><a href="#configuracioForm" aria-controls="configuracioForm" role="tab" data-toggle="tab"><spring:message code="entitat.form.titol.configuracio"/></a></li>
    </ul>
	<c:set var="formAction"><not:modalUrl value="/entitat"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="entitatCommand" role="form"  enctype="multipart/form-data">
		<br>
		<div class="tab-content">
		<form:hidden path="id"/>
		<div role="tabpanel" class="tab-pane " id="dadesForm">
			<not:inputText name="codi" textKey="entitat.form.camp.codi" required="true"/>
			<not:inputText name="nom" textKey="entitat.form.camp.nom" required="true"/>
			<not:inputSelect name="tipus" textKey="entitat.form.camp.tipus" optionEnum="EntitatTipusEnumDto" required="true"/>
			<not:inputText name="dir3Codi" textKey="entitat.form.camp.codidir3" required="true"/>
			<not:inputText name="apiKey" textKey="entitat.form.camp.apiKey" required="true"/>
			<not:inputTextarea name="descripcio" textKey="entitat.form.camp.descripcio"/>
		</div>
		<div role="tabpanel" class="tab-pane active" id="configuracioForm">
			<div class="container-fluid col-md-12">
				<div class="title">
					<label><spring:message code="entitat.form.camp.conf.aspecte" /></label>
					<hr/>
				</div>
				<not:inputFile name="logoCap" textKey="entitat.form.camp.conf.logocap"/>
				<not:inputFile name="logoPeu" textKey="entitat.form.camp.conf.logopeu"/>
				<not:inputText name="colorFons" textKey="entitat.form.camp.conf.fons"/>
				<not:inputText name="colorLletra" textKey="entitat.form.camp.conf.lletra"/>
				<div class="title">
					<label><spring:message code="entitat.form.camp.conf.tipusdoc" /></label>
					<hr/>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
					<div class="controls col-xs-8">
						<select name="tipusDocName" class="customSelect" multiple>
						<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
							<option value="${enumValue}">${enumValue}</option>
						</c:forEach>
						</select>
					</div>
				</div>
			</div>
		</div>
		</div>
		<div id="modal-botons" class="col-xs-12 text-right">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

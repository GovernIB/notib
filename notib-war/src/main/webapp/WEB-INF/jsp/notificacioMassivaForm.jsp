<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<c:set var="titol"><spring:message code="notificacio.massiva.form.titol.crear"/></c:set>

<html>
<head>
	<title>${titol} 	<div class="text-right col-md-12">
		<a href="<c:url value="/notificacio/massiva/getModelDadesCarregaMassiuCSV"/>"
		class="btn btn-info btn-sm fileDownloadSimpleRichExperience"
		style="position: relative;top: -35px;right: -12px;"
		title="<spring:message code="notificacio.massiva.accio.descarregar.modeloCsv"/>">
		<spring:message code="notificacio.massiva.accio.descarregar.modeloCsv"/>
		<span class="fa fa-download"></span>
		</a>
		</div></title>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
</head>
<body>
<style>
	div.list {
		counter-reset: list-number;
		margin-top: 15px;
		margin-left: 20px;
	}
	div.list div:before {
		counter-increment: list-number;
		content: counter(list-number);

		margin-right: 10px;
		margin-bottom:10px;
		width:35px;
		height:35px;
		display:inline-flex;
		align-items:center;
		justify-content: center;
		font-size:16px;
		background-color:#37a2d6;
		border-radius:50%;
		color:#fff;
	}
</style>

<div class="alert alert-info">
	<strong><spring:message code="notificacio.massiva.form.info.titol"/></strong>
	<div class="list">
		<div><spring:message code="notificacio.massiva.form.info.indicacio1"/></div>
		<div><spring:message code="notificacio.massiva.form.info.indicacio2"/></div>
	</div>
</div>
	<c:set var="formAction"><not:modalUrl value="/notificacio/massiva/new"/></c:set>
	<form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="notificacioMassivaCommand" enctype="multipart/form-data">
		<input type="hidden" name="fitxerCSVGestdocId" value="${notificacioMassivaCommand.fitxerCSVGestdocId}">
		<input type="hidden" name="fitxerCSVNom" value="${notificacioMassivaCommand.fitxerCSVNom}">
		<not:inputFile name="ficheroCsv" textKey="notificacio.massiva.form.camp.arxiuCsv" labelSize="2" inputSize="10"
					   required="true" info="true" messageInfo="notificacio.massiva.form.camp.arxiuCsv.avis"
						fileName="${notificacioMassivaCommand.fitxerCSVNom}"/>
		<input type="hidden" name="fitxerZIPGestdocId" value="${notificacioMassivaCommand.fitxerZIPGestdocId}">
		<input type="hidden" name="fitxerZIPNom" value="${notificacioMassivaCommand.fitxerZIPNom}">
		<not:inputFile name="ficheroZip" textKey="notificacio.massiva.form.camp.arxiuZip" labelSize="2" inputSize="10"
					   required="false" info="true" messageInfo="notificacio.massiva.form.camp.arxiuZip.avis"
					   fileName="${notificacioMassivaCommand.fitxerZIPNom}"/>
		<not:inputDate name="caducitat" textKey="notificacio.massiva.form.camp.caducitat" labelSize="2" inputSize="10"
					   required="true" />
		<not:inputMail name="email" textKey="notificacio.massiva.form.camp.email" labelSize="2" inputSize="10"
					   required="false" inputMaxLength="${emailSize}" showsize="true"/>
		<c:if test="${mostrarPagadorPostal}">
			<not:inputSelect name="pagadorPostalId" textKey="procediment.form.camp.postal" labelSize="2" inputSize="10"
							 optionItems="${pagadorsPostal}" emptyOption="true"
							 optionValueAttribute="id" optionTextAttribute="dir3codi"/>
		</c:if>

		<hr>
		<div class="text-right col-md-12">
			<div class="btn-group">
				<button type="submit" class="btn btn-success saveForm">
					<span class="fa fa-paper-plane"></span>
					<spring:message code="comu.boto.enviar.notificacio" />
				</button>
			</div>
		</div>
	</form:form>
</body>
</html>
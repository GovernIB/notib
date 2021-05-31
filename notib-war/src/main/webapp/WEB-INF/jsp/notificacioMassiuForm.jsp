<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<c:set var="titol"><spring:message code="notificacioMassiu.form.titol.crear"/></c:set>

<html>
<head>
	<title>${titol}</title>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
		<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
</head>
<body>
	<div class="text-right col-md-12">
		<a href="<c:url value="/notificacio/newMassiu/getModelDadesCarregaMassiuCSV"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="notificacioMassiu.accio.descarregar.modeloCsv"/>">
			<spring:message code="notificacioMassiu.accio.descarregar.modeloCsv"/>
			<span class="fa fa-download"></span>
		</a>
	</div>

	<c:set var="formAction"><not:modalUrl value="/notificacio/newMassiuProcessar"/></c:set>
    <form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="notificacioMassiuCommand" enctype="multipart/form-data">
		<not:inputFile name="ficheroCsv" textKey="notificacioMassiu.form.camp.arxiuCsv" labelSize="3" inputSize="6" required="true" info="true" messageInfo="notificacioMassiu.form.camp.arxiuCsv.avis" fileName="${notificacioMassiuCommand.ficheroCsv.originalFilename}"/>
		<not:inputFile name="ficheroZip" textKey="notificacioMassiu.form.camp.arxiuZip" labelSize="3" inputSize="6" required="true" info="true" messageInfo="notificacioMassiu.form.camp.arxiuZip.avis" fileName="${notificacioMassiuCommand.ficheroZip.originalFilename}"/>
		<not:inputDate name="caducitat" textKey="notificacioMassiu.form.camp.caducitat" labelSize="3" inputSize="6" />
		<not:inputText name="email" textKey="notificacioMassiu.form.camp.email" inputMaxLength="${emailSize}" showsize="true" labelSize="3" inputSize="6" required="true"/>
		
		<div class="text-right col-md-12">
			<div class="btn-group">
				<button type="submit" class="btn btn-success saveForm">
					<span class="fa fa-paper-plane"></span>
					<spring:message code="comu.boto.enviar.notificacio" />
<%-- 				<spring:message code="notificacioMassiu.boto.processar.carrega" /> --%>
				</button>
<!-- 				<button type="submit" class="btn btn-success"> -->
<!-- 					<span class="fa fa-save"></span>&nbsp; -->
<%-- 					<spring:message code="comu.boto.guardar"/> --%>
<!-- 				</button> -->
			</div>
		</div>	
	</form:form>
</body>
</html>
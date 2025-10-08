<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="visualitzar.columnes"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<not:modalHead/>
<style type="text/css">
input[type='checkbox'] {
    /*width:30px;*/
    /*height:30px;*/
    background:white;
    border-radius:5px;
    border:2px solid #555;
    top: -2px;
}

input[type='checkbox']:checked{
    background: #abd;
    top: -2px;
}
</style>

</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/notificacio/visualitzar/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="columnesRemesesCommand" role="form">
		<form:hidden path="id"/>
		<div class="row col-xs-12">
			<div class="row col-xs-6">
				<div class="col-md-4">
					<not:inputCheckbox name="dataCreacio" textKey="enviament.list.datacreacio" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="dataEnviament" textKey="enviament.list.dataenviament" labelSize="10" />
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="numRegistre" textKey="notificacio.list.columna.num.registre" labelSize="10" />
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="organEmisor" textKey="notificacio.form.camp.organEmisor" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="procSerCodi" textKey="notificacio.list.columna.procediment" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="numExpedient" textKey="notificacio.list.columna.num.expedient" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="concepte" textKey="notificacio.list.columna.concepte" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="creadaPer" textKey="notificacio.list.columna.enviament.creada" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="interessats" textKey="notificacio.list.columna.interessats" labelSize="10"/>
				</div>
				<div class="col-md-4">
					<not:inputCheckbox name="estat" textKey="notificacio.list.columna.estat" labelSize="10"/>
				</div>
			</div>

		</div>
		<div id="modal-botons">
			<button id="" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/notificacio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	
</body>
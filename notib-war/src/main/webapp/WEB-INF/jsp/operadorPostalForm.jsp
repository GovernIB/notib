<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty operadorPostalCommand.id}"><c:set var="titol"><spring:message code="operadorpostal.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="operadorpostal.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
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
<style>

.datepicker table tr td.today, .datepicker table tr td.today:hover {
	color: #000000;
	background: #a4a4a4 !important;
	background-color: #a4a4a4 !important;
}

.rmodal {
    max-height:400px;
    overflow-y: auto;
}

</style>
<script type="text/javascript">

	var organsGestors = [];
	let organData;
	organsGestors.push({id:"", text:"", estat:"VIGENT"});
	<c:forEach items="${organsGestors}" var="organGestor">
	organData = {id:"${organGestor.codi}", text:"${organGestor.valor}", estat:"${organGestor.estat}"}
	<c:if test="${operadorPostalCommand.organismePagadorCodi == organGestor.codi}">
	organData['selected'] = true;
	</c:if>
	organsGestors.push(organData);
	</c:forEach>
	$(document).ready(function() {
		let $selectOrgan = $('#organismePagadorCodi');
		loadOrgans($selectOrgan, organsGestors, "<spring:message code='notificacio.list.columna.organGestor.obsolet'/>");
	});

</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/operadorPostal/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="operadorPostalCommand" role="form">
		<form:hidden path="id"/>
		<div class="row">
			<div class="col-md-2">
				<not:inputSelect name="organismePagadorCodi"
								 required="true"
								 textKey="operadorpostal.form.camp.organismePagador"
								 inline="false"
								 emptyOption="true"
								 optionMinimumResultsForSearch="0"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="nom"
							   required="true"
							   textKey="operadorpostal.form.camp.nom"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="contracteNum"
							   required="true"
							   textKey="operadorpostal.form.camp.contracteNum"/>
			</div>
			<div class="col-md-2">
				<not:inputDate name="contracteDataVig" textKey="operadorpostal.form.camp.contracteDataVig"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="facturacioClientCodi"
							   required="true"
							   textKey="operadorpostal.form.camp.facturacioClientCodi"/>
			</div>
			<div id="modal-botons">
				<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
				<a href="<c:url value="/operadorPostal"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</div>
		</div>
	</form:form>

</body>
</html>
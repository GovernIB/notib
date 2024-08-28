<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty cieCommand.id}"><c:set var="titol"><spring:message code="cie.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="cie.form.titol.modificar"/></c:set></c:otherwise>
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
	<style type="text/css">
	.modal-body {
		height: 300px !important;
	}

	.datepicker table tr td.today, .datepicker table tr td.today:hover {
		color: #000000;
		background: #a4a4a4 !important;
		background-color: #a4a4a4 !important;
	}

	</style>
	<script type="text/javascript">
		$(document).ready(function() {

			let $selectOrgan = $("#organismePagadorCodi");
			loadOrgans($selectOrgan, organsGestors, "<spring:message code='notificacio.list.columna.organGestor.obsolet'/>");
			let cieAntic = $("#cieAntic");
			debugger;
			if (cieAntic) {
				$("cieAnticForm").show();
				$("cieNouForm").hide();
			} else {
				$("cieAnticForm").hide();
				$("cieNouForm").show();
			}

		});

		var organsGestors = [];
		let organData;
		organsGestors.push({id:"", text:"", estat:"V"});
		<c:forEach items="${organsGestors}" var="organGestor">
			organData = {id:"${organGestor.codi}", text:"${organGestor.valor}", estat:"${organGestor.estat}"}
			<c:if test="${cieCommand.organismePagadorCodi == organGestor.codi}">
				organData['selected'] = true;
			</c:if>
			organsGestors.push(organData);
		</c:forEach>
	</script>
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/cie/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="cieCommand" role="form">
		<form:hidden path="id"/>
		<not:inputCheckbox name="cieAntic" textKey="cie.form.camp.cie.antic"  labelSize="4" />
		<div class="row">
				<div class="col-md-2">
					<not:inputText name="nom" textKey="cie.form.camp.centreImpressio"/>
				</div>
<%--			<c:choose>--%>
<%--				<c:when test="${cieAntic == false}">--%>
					<div id="cieAnticForm">
						<div class="col-md-2">
							<not:inputSelect name="organismePagadorCodi" required="true" textKey="operadorpostal.form.camp.organismePagador" inline="false" emptyOption="true" optionMinimumResultsForSearch="0"/>
						</div>
						<div class="col-md-2">
							<not:inputDate name="contracteDataVig" disabled="false" textKey="cie.form.camp.contracteDataVig" custom="true"/>
						</div>
					</div>
<%--				</c:when>--%>
<%--				<c:otherwise>--%>
					<div id="cieNouForm">
						<div class="col-md-2">
							<not:inputText name="usuari" textKey="cie.form.camp.usuari"/>
						</div>
						<div class="col-md-2">
							<not:inputText name="password" textKey="cie.form.camp.password"/>
						</div>
					</div>
					<div id="modal-botons">
						<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
						<a href="<c:url value="/cie"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
					</div>
<%--				</c:otherwise>--%>
<%--			</c:choose>--%>
		</div>
	</form:form>
	
</body>
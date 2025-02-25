<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(ssc.getRolActual()));
%>
<html>
<head>
	<title><spring:message code="monitor.activemq.missatges.titol" arguments="${queueNom}"/></title>
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

	<script type="text/javascript">
		$(document).ready(function() {
			setInterval(() => window.location.reload(), 10000);

		});
	</script>
<not:modalHead />
</head>
<body>
	<table 	id="taulaMissatges"
		 	data-toggle="datatable"
			data-url="<c:url value="/monitor/activemq/missatges/${queueNom}/datatable"/>"
			data-search-enabled="false"
			data-selection-enabled="false"
			data-default-order="0"
			data-default-dir="asc"
			data-paging="false"
			class="table table-bordered table-striped"
			data-info-type="search"
			style="width:100%">
		<thead>
		<tr>
			<th data-col-name="id"><spring:message code="monitor.activemq.columna.id.missatge"/>
			<th data-col-name="data" data-converter="datetime"><spring:message code="monitor.activemq.columna.data"/>
			<th data-col-name="uuid" data-template="#cellAccionsTemplateUuId"><spring:message code="monitor.activemq.columna.uuid"/>
				<script id="cellAccionsTemplateUuId" type="text/x-jsrender">
					<a href="<c:url value="/enviament/filtrades/{{:uuid}}"/>" target="_blank">{{:uuid}}</a>
				</script>
			</th>
			<th data-col-name="notificacioUuId" data-template="#cellAccionsTemplateNotificiacio"><spring:message code="monitor.activemq.columna.id.notificacio"/>
				<script id="cellAccionsTemplateNotificiacio" type="text/x-jsrender">
					<a href="<c:url value="/notificacio/filtrades/{{:notificacioUuId}}"/>" target="_blank">{{:notificacioUuId}}</a>
				</script>
			</th>
			<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
				<script id="cellAccionsTemplate" type="text/x-jsrender">
					<a class="btn btn-primary" data-toggle="ajax" href="<c:url value="/monitor/activemq/missatges/${queueNom}/{{:id}}/delete"/>"><span class="fa fa-trash"></span>&nbsp;<spring:message code="monitor.activemq.boto.esborrar"/></span>
				</script>
			</th>
		</tr>
		</thead>
	</table>
</body>
</html>

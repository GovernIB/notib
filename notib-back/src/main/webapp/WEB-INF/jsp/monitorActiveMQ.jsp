<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()));
%>
<html>
<head>
	<title><spring:message code="monitor.activemq.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$("#refrescar").on("click", () => window.location.reload());

		});
	</script>
</head>
<body>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="refrescar" class="btn btn-default"><span class="fa fa-reload"></span>Refrescar</a>
			</div>
		</div>
	</script>
	<table
		id="taulaInfoQueues"
		data-toggle="datatable"
		data-url="<c:url value="/monitor/activemq/datatable"/>"
		data-search-enabled="false"
		data-selection-enabled="false"
		data-default-order="0"
		data-default-dir="asc"
		data-paging="false"
		class="table table-bordered table-striped"
		data-info-type="search"
		data-botons-template="#botonsTemplate"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="nom"><spring:message code="monitor.activemq.columna.nom.cua"/></th>
				<th data-col-name="descripcio"><spring:message code="monitor.activemq.columna.descripcio.cua"/></th>
				<th data-col-name="mida"><spring:message code="monitor.activemq.columna.mida.cua"/></th>
<%--				<th data-col-name="consumersCount"><spring:message code="monitor.activemq.columna.counsumers.count"/></th>--%>
				<th data-col-name="enqueueCount"><spring:message code="monitor.activemq.columna.enqueue.count"/></th>
				<th data-col-name="dequeueCount"><spring:message code="monitor.activemq.columna.dequeue.count"/></th>
				<th data-col-name="forwardCount"><spring:message code="monitor.activemq.columna.forward.count"/></th>
				<th data-col-name="inFlightCount"><spring:message code="monitor.activemq.columna.in.flight.count"/></th>
				<th data-col-name="expiredCount"><spring:message code="monitor.activemq.columna.expired.count"/></th>
				<th data-col-name="storeMessageSize"><spring:message code="monitor.activemq.columna.store.message.size.count"/></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu dropdown-menu-right">
								<li><a href="<c:url value="/monitor/activemq/missatges/{{:nom}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-send"></span>&nbsp;<spring:message code="monitor.activemq.boto.missatges"/></span></li>
								<li><a href="<c:url value="/monitor/activemq/missatges/{{:nom}}/buidar"/>" data-toggle="ajax"><span class="fa fa-trash"></span>&nbsp;<spring:message code="monitor.activemq.boto.buidar"/></span></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>

		</thead>
	</table>
	<script>
		$(document).ready(function() {
			setInterval(() => $("#taulaInfoQueues").DataTable().ajax.reload(), 10000); // 30000 milliseconds = 30 seconds
		});
	</script>
</body>
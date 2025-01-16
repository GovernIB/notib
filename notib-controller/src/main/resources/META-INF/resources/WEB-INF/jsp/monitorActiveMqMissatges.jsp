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
</head>
<body>
<table
		id="taulaMissatges"
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
				<a class="btn btn-primary" href="<c:url value="/monitor/activemq/missatges/{{:nom}}"/>" data-toggle="modal" data-processar="true"><span class="fa fa-send"></span>&nbsp;<spring:message code="comu.boto.accions"/></span>
			</script>
		</th>
	</tr>

	</thead>
</table>
</body>
</html>

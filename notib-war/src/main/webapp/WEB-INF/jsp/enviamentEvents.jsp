<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
	<title><spring:message code="enviament.event.list.titol" arguments="${servei.descripcio}"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
<script type="text/javascript">
	$(document).ready(function() {
		$('#events').on('draw.dt', function () {
			webutilModalAdjustHeight();
		});
	});
</script>
</head>
<body>
	<table
		id="events"
		data-toggle="datatable"
		data-url="<c:url value="/notificacio/${notificacioId}/enviament/${enviamentId}/event"/>"
		data-search-enabled="false"
		data-default-dir="asc"
		data-paging="false"
		data-info="false"
		class="table table-striped table-bordered"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="tipus" data-orderable="false"><spring:message code="enviament.event.list.columna.tipus"/></th>
				<th data-col-name="data" data-converter="date" data-orderable="false"><spring:message code="enviament.event.list.columna.data"/></th>
				<th data-col-name="error" data-template="#cellError" data-orderable="false">
					<spring:message code="enviament.event.list.columna.error"/>
					<script id="cellError" type="text/x-jsrender">
						{{if error}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="enviament.event.list.columna.descripcio"/></th>
			</tr>
		</thead>
	</table>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>

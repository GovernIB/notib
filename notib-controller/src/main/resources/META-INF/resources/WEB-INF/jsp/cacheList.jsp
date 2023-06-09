<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="cache.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			<a class="btn btn-warning" href="<c:url value="/cache/all/buidar"/>" data-toggle="ajax" data-confirm="<spring:message code="cache.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="cache.all.boto.esborrar"/></a>
		</p>
	</script>
	<table
		data-toggle="datatable"
		data-url="<c:url value="/cache/datatable"/>"
		data-default-order="0"
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		class="table table-striped table-bordered"
		style="width:100%"
		data-paging-enabled="false">
		<thead>
			<tr>
				<th data-col-name="codi"><spring:message code="cache.list.columna.codi"/></th>
				<th data-col-name="descripcio"><spring:message code="cache.list.columna.descripcio"/></th>
				<th data-col-name="localHeapSize"><spring:message code="cache.list.columna.mida"/></th>
				<th data-col-name="codi" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a class="btn btn-warning" href="<c:url value="/cache/{{:codi}}/buidar"/>" data-toggle="ajax" data-confirm="<spring:message code="cache.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="cache.boto.esborrar"/></a></li>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
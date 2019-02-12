<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="procediment.permis.titol"/></title>
	<meta name="subtitle" content="${procediment.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table 
		id="taulaDades" 
		data-toggle="datatable" 
		data-url="<c:url value="/procediment/${procediment.id}/grup/datatable"/>" 
		data-search-enabled="false" 
		data-paging-enabled="false" 
		data-default-order="1" 
		data-default-dir="asc" 
		data-botons-template="#tableButtonsTemplate" 
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-template="#cellTipusTemplate" ><spring:message code="procediment.grup.columna.tipus"/>
					<script id="cellTipusTemplate" type="text/x-jsrender">
						<td><spring:message code="procediment.grup.columna.tipus.grup"/></td>
					</script>
				</th>
				<th data-col-name="grup.nom"><spring:message code="procediment.permis.columna.principal"/></th>
				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="../../procediment/${procediment.id}/grup/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="../../procediment/${procediment.id}/grup/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.grup.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="../../procediment/${procediment.id}/grup/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.grup.boto.nou.grup"/></a></p>
	</script>
	<a href="<c:url value="/procediment?mantenirPaginacio=true"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
	<div class="clearfix"></div>
</body>
</html>

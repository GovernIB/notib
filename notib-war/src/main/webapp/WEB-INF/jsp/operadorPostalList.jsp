<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<% 
pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
%>
<html>
<head>
	<title><spring:message code="operadorpostal.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			let $selectOrgan = $('#organismePagador');
			loadOrgans($selectOrgan, organsGestors, "<spring:message code='notificacio.list.columna.organGestor.obsolet'/>");
		});

		var organsGestors = [];
		let organData;
		organsGestors.push({id:"", text:"", estat:"VIGENT"});
		<c:forEach items="${organsGestors}" var="organGestor">
		organData = {id:"${organGestor.codi}", text:"${organGestor.valor}", estat:"${organGestor.estat}"}
		<c:if test="${operadorPostalFiltreCommand.organismePagador == organGestor.codi}">
			organData['selected'] = true;
		</c:if>
		organsGestors.push(organData);
		</c:forEach>
	</script>
</head>
<body>
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="operadorPostalFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<not:inputSelect name="organismePagador" placeholderKey="operadorpostal.list.columna.organismePagador"
								 inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="contracteNum" inline="true" placeholderKey="operadorpostal.list.columna.contracteNum"/>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<table
		id="pagadorPostal"
		data-toggle="datatable"
		data-url="<c:url value="/operadorPostal/datatable"/>"
		data-search-enabled="false"
		data-default-order="1"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		data-botons-template="#botonsTemplate"
		style="width:100%"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="organismePagador"><spring:message code="operadorpostal.list.columna.organismePagador"/></th>
				<th data-col-name="nom"><spring:message code="operadorpostal.list.columna.nom"/></th>
				<th data-col-name="contracteNum"><spring:message code="operadorpostal.list.columna.contracteNum"/></th>
				<th data-col-name="contracteDataVig" data-type="date" data-converter="date"><spring:message code="operadorpostal.list.columna.contracteDataVig"/></th>
				<th data-col-name="facturacioClientCodi"><spring:message code="operadorpostal.list.columna.facturacioClientCodi"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${unitatCodiUrlPrefix}operadorPostal/{{:id}}" data-toggle="modal" data-height="400px" data-custom="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="${unitatCodiUrlPrefix}operadorPostal/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="operadorpostal.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="${unitatCodiUrlPrefix}operadorPostal/new" data-toggle="modal" data-height="400px" data-custom="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="operadorpostal.list.boto.nou.operadorpostal"/></a></p>
	</script>
	
</body>
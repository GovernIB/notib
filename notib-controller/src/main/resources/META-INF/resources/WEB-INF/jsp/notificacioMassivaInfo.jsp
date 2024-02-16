<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()));
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(ssc.getRolActual()));
	pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()));
%>

<html>
<head>
<title><spring:message code="notificacio.massiva.info.titol" /></title>
<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
<not:modalHead />
<script type="text/javascript">
$(document).ready(function() {
	$(document.body).on('hidden.bs.modal', function () {
		$('.tab-content').load(location.href + " .tab-content");
	});
});
</script>
<style type="text/css">
.modal-backdrop {
    visibility: hidden !important;
}
.modal.in {
    background-color: rgba(0,0,0,0.5);
}
.btn-certificacio {
	margin-top: 5%;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"> 
				<spring:message code="notificacio.massiva.info.tab.dades" />
			</a>
		</li>
		<li role="presentation">
			<a href="#resum" aria-controls="resum" role="tab" data-toggle="tab">
				<spring:message code="notificacio.massiva.info.tab.resum" />
			</a>
		</li>
	</ul>
	<br />
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane active" id="dades">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<strong><spring:message code="notificacio.massiva.info.seccio.dades" /></strong>
					</h3>
				</div>
				<table class="table table-bordered" style="width: 100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="notificacio.massiva.info.dada.csv.filename" /></strong></td>
							<td>${info.csvFilename}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.massiva.info.dada.zip.filename" /></strong></td>
							<td>${info.zipFilename}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.massiva.info.dada.caducitat" /></strong></td>
							<td><fmt:formatDate value="${info.caducitat}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.massiva.info.data.creacio" /></strong></td>
							<td><fmt:formatDate value="${info.createdDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.massiva.info.dada.email" /></strong></td>
							<td>${info.email}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.massiva.info.dada.creador" /></strong></td>
							<td>${info.createdBy.nom}
								(${info.createdBy.codi})</td>
						</tr>
					</tbody>
				</table>
			</div>
			<c:if test="${not empty info.pagadorPostal}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.postal" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if
								test="${not empty info.pagadorPostal.dir3codi}">
								<tr>
									<td width="30%"><strong><spring:message
												code="notificacio.info.pagador.correus.codi.dir3" /></strong></td>
									<td>${info.pagadorPostal.dir3codi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.contracte" /></strong></td>
									<td>${info.pagadorPostal.contracteNum}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.client" /></strong></td>
									<td>${info.pagadorPostal.facturacioClientCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.vigencia" /></strong></td>
									<td>
									<fmt:formatDate pattern="dd/MM/yyyy" value="${info.pagadorPostal.contracteDataVig}" />
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
			</c:if>
		</div>
		<div role="tabpanel" class="tab-pane" id="resum">
			<table id="events" class="table table-striped table-bordered" style="width: 100%">
				<thead>
					<tr>
						<th><strong><spring:message code="notificacio.massiva.info.resum.enviament.tipus" /></strong></th>
						<th><strong><spring:message code="notificacio.massiva.info.resum.unitat.remisora" /></strong></th>
						<th><strong><spring:message code="notificacio.massiva.info.resum.concepte" /></strong></th>
						<th><strong><spring:message code="notificacio.massiva.info.resum.descripcio" /></strong></th>
						<th><strong><spring:message code="notificacio.massiva.info.resum.prioritat" /></strong></th>
						<th><strong><spring:message code="notificacio.massiva.info.resum.titular" /></strong></th>
<%--						<th><strong><spring:message--%>
<%--								code="notificacio.massiva.info.resum.origen" /></strong></th>--%>
						<th><strong><spring:message code="notificacio.massiva.info.resum.errors" /></strong></th>
						<th><strong><spring:message code="notificacio.massiva.list.accio.errors.execucio.download" /></strong></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${info.summary}" var="notInfo" varStatus="status">
					<tr>
						<td>${notInfo.enviamentTipus}</td>
						<td>${notInfo.codiDir3UnidadRemisora}</td>
						<td>${notInfo.concepto}</td>
						<td>${notInfo.descripcio}</td>
						<td>${notInfo.prioridadServicio}</td>
						<td>${notInfo.nombre} ${notInfo.apellidos}
						<c:choose>
							<c:when test="${not empty notInfo.cifNif}">
								- ${notInfo.cifNif}</td>
							</c:when>
							<c:otherwise>
								- <spring:message code="notificacio.massiva.info.resum.interesat.sense.nif"/></td>
							</c:otherwise>
						</c:choose>
<%--						<td>${notInfo.origen}</td>--%>
						<td>${notInfo.errores}</td>
						<td>${notInfo.errorsExecucio}</td>
						<c:choose>
                            <c:when test="${notInfo.cancelada}">
							    <td><strong><spring:message code="notificacio.massiva.info.resum.cancelada" /></strong></td>
                            </c:when>
                            <c:otherwise>
                                <td></td>
                            </c:otherwise>
                        </c:choose>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacio"/>" class="btn btn-default"
			data-modal-cancel="true"><spring:message code="comu.boto.tancar" /></a>
	</div>
</body>
</html>

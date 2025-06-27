<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
    es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
    pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()));
    pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()));
    pageContext.setAttribute("isRolActualAdministradorLectura", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorLectura(ssc.getRolActual()));
    pageContext.setAttribute("isRolActualUsuari", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuari(ssc.getRolActual()));
%>
<html>
<head>
    <title><spring:message code="accions.massives.list.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <link href="<c:url value="/css/entitat.css"/>" rel="stylesheet" type="text/css">
</head>
<body>
<script id="botonsTemplate" type="text/x-jsrender">
<%--
		<c:if test="${isRolActualAdministrador}">
			<p style="text-align:right"><a id="grup-entitat-nou" class="btn btn-default" href="entitat/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="entitat.list.boto.nova.entitat"/></a></p>
		</c:if>
--%>
	</script>

<table id="accionsMassives"
        data-toggle="datatable"
        data-url="<c:url value="/accions/massives/datatable"/>"
        data-search-enabled="true"
        data-default-order=""
        data-default-dir="desc"
        data-botons-template="#botonsTemplate"
        class="table table-striped table-bordered"
        data-info-type="search"
        style="width:100%">
    <thead>
    <tr>
        <th data-col-name="id" data-visible="false">#</th>
        <th data-col-name="tipus"><spring:message code="accions.massives.tipus"/></th>
        <th data-col-name="createdDate"><spring:message code="accions.massives.data.creacio"/></th>
        <th data-col-name="dataInici"><spring:message code="accions.massives.data.inici"/></th>
        <th data-col-name="dataFi"><spring:message code="accions.massives.data.fi"/></th>
        <th data-col-name="createdByCodi"><spring:message code="accions.massives.codi.usuari"/></th>
        <th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
            <script id="cellAccionsTemplate" type="text/x-jsrender">
                <div class="dropdown">
                <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
                <ul class="dropdown-menu">
<%--                <li><a href="<c:url value="/entitat/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>--%>
<%--                <li><a href="<c:url value="/entitat/{{:id}}/configurar"/>"><span class="fa fa-gear"></span>&nbsp;&nbsp;<spring:message code="comu.boto.configurar"/></a></li>--%>
<%--                <li><a href="<c:url value="/entitat/{{:id}}/reset/actualitzacio/organs"/>" data-toggle="ajax"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="entitat.boto.reset.actualitzacions.organs"/></a></li>--%>
<%--                {{if !activa}}--%>
<%--                <li><a href="<c:url value="/entitat/{{:id}}/enable"/>" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>--%>
<%--                {{else}}--%>
<%--                <li><a href="<c:url value="/entitat/{{:id}}/disable"/>" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>--%>
<%--                {{/if}}--%>
<%--                <li><a href="<c:url value="/entitat/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>--%>
                </ul>
                </div>
            </script>
        </th>
    </tr>
    </thead>
</table>
</body>
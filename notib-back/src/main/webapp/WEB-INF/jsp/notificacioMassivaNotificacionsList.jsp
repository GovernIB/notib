<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()), PageContext.REQUEST_SCOPE);
	pageContext.setAttribute("isRolActualUsuari", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuari(ssc.getRolActual()), PageContext.REQUEST_SCOPE);
	pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()), PageContext.REQUEST_SCOPE);
	pageContext.setAttribute("isRolActualAdministradorLectura", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorLectura(ssc.getRolActual()), PageContext.REQUEST_SCOPE);
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(ssc.getRolActual()), PageContext.REQUEST_SCOPE);
%>

<c:set var="mostraEntitat" value="${isRolActualAdministrador && mostrarColumnaEntitat}" scope="request"/>
<c:set var="entitat" value="${entitat}" scope="request"/>
<c:set var="notificacioEnviamentTipus" value="${notificacioEnviamentTipus}" scope="request"/>
<c:set var="notificacioEstats" value="${notificacioEstats}" scope="request"/>
<c:set var="tipusUsuari" value="${tipusUsuari}" scope="request"/>
<c:set var="nomesAmbErrors" value="${nomesAmbErrors}" scope="request"/>
<c:set var="mostrarColumnaNumExpedient" value="${mostrarColumnaNumExpedient}" scope="request"/>
<c:set var="organsGestorsPermisLectura" value="${organsGestorsPermisLectura}" scope="request"/>

<c:url var="urlDatatable" value="/notificacio/massiva/${notificacioMassivaId}/remeses/datatable" scope="request"/>


<html>
<head>
	<title><spring:message code="notificacio.list.titol"/> <small>${subtitle}</small></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/datatable.accions-massives.js"/>"></script>
	<link href="<c:url value="/css/datatable-accions-massives.css"/>" rel="stylesheet"/>
</head>
<body>
<div id="msg-box"></div>
	<c:import url="includes/notificacioList.jsp"/>
	<hr>
	<a href="<c:url value="/notificacio/massiva"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
</body>
</html>

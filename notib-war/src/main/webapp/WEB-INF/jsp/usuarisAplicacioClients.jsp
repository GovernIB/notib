z	<%@ page language="java" contentType="text/html; charset=UTF-8"
              pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
  pageContext.setAttribute(
          "isRolActualAdministradorEntitat",
          es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
  pageContext.setAttribute(
          "isRolActualAdministradorOrgan",
          es.caib.notib.war.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(request));
  pageContext.setAttribute(
          "isRolActualAdministrador",
          es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
%>

<html>
<head>
  <title><spring:message code="decorator.menu.usuaris.aplicacio.clients"/></title>
  <script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
  <script src="<c:url value="/webjars/bootstrap/3.3.6/js/dropdown.js"/>"></script>
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
</head>
<body>
  <table class="table table-striped table-bordered dataTable">
    <thead>
      <tr>
        <th>Usuari codi</th>
        <th>Tipus API</th>
        <th>Versió</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="value" items="${aplicacionsClients}">
        <c:forEach var="aplicacio" items="${value}">
          <tr>
            <td>${aplicacio.usuariCodi}</td>
            <td>${aplicacio.tipus}</td>
            <td>${aplicacio.versio}</td>
          </tr>
        </c:forEach>
      </c:forEach>
    </tbody>
  </table>

<div id="modal-botons" class="text-right">
  <a href="<c:url value="/notificacio"/>" class="btn btn-default"
     data-modal-cancel="true"><spring:message code="comu.boto.tancar" /></a>
</div>
</body>
</html>

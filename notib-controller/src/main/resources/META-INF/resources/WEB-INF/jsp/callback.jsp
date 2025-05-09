callback pendents<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatOrdreFiltre" %>
<%@ page import="es.caib.notib.client.domini.EnviamentTipus" %><%--<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto" %>--%>
<%--<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatOrdreFiltre" %>--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
    pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()));
    pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()));
//	pageContext.setAttribute("notificacioComunicacioEnumOptions", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(es.caib.notib.logic.intf.dto.EnviamentTipus.class, "notificacio.tipus.enviament.enum."));
//	pageContext.setAttribute("notificacioEstatEnumOptions", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(NotificacioEstatOrdreFiltre.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto."));
    pageContext.setAttribute("notificacioEnviamentTipus", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(EnviamentTipus.class, "es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto."));
%>
<c:set var="ampladaConcepte">
    <c:choose>
        <c:when test="${isRolActualAdministrador}">35%</c:when>
        <c:otherwise>55%</c:otherwise>
    </c:choose>
</c:set>
<c:set var="refresh_state_succes"><spring:message code="notificacio.list.enviament.list.refresca.estat.exitos"/></c:set>
<c:set var="refresh_state_error"><spring:message code="notificacio.list.enviament.list.refresca.estat.error"/></c:set>
<c:set var="notificacioEnviamentTipus" value="${notificacioEnviamentTipus}" scope="request"/>
<html>
<head>
    <title><spring:message code="callback.list.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
    <link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
    <link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <script src="<c:url value="/js/datatable.accions-massives.js"/>"></script>
    <link href="<c:url value="/css/datatable-accions-massives.css"/>" rel="stylesheet"/>

    <script>
        $(document).ready(function () {


        });
    </script>
    <style type="text/css">
        .label-primary {
            background-color: #999999;
        }

        .label-warning {
            background-color: #dddddd;
            color: #333333;
        }
        .div-filter-data-sep {
            padding: 0;
        }
        .dropdown-left {
            right: 0;
            left: auto
        }
    </style>
</head>
<body>

<div id="loading-screen" class="loading-screen" >
    <div id="processing-icon" class="processing-icon">
        <span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: dimgray;margin-top: 10px;"></span>
    </div>
</div>
<script id="botonsTemplate" type="text/x-jsrender">
    <div class="text-right">
        <div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="enviament.list.user.seleccio.tots" />" class="btn btn-default" ><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="enviament.list.user.seleccio.cap" />" class="btn btn-default" ><span class="fa fa-square-o"></span></button>
				<div id="seleccioCount" class="btn-group">
					<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
  						<span class="badge seleccioCount">${fn:length(seleccio)}</span> <spring:message code="enviament.list.user.accions.massives"/> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu dropdown-left">
						<li><a style="cursor: pointer;" id="exportarODS"><spring:message code="enviament.list.user.exportar"/> a <spring:message code="enviament.list.user.exportar.EXCEL"/></a></li>
						<li><a id="reintentarErrors" style="cursor: pointer;" title='<spring:message code="notificacio.list.accio.massiva.reintentar.errors.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reintentar.errors"/></a></li>
						<li><a style="cursor: pointer;" id="updateEstat"><spring:message code="enviament.list.user.actualitzar.estat"/></a></li>
						<li><a style="cursor: pointer;" id="ampliarPlazoOe"><spring:message code="notificacio.list.accio.massiva.ampliar.plazo.oe"/></a></li>
                    </ul>
                </div>
        </div>
        <div class="btn-group">
                <button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
        </div>
    </div>
</script>

<script id="cellFilterTemplate" type="text/x-jsrender">
    <div class="dropdown">
        <button type="submit" id="btnFiltrar" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-search"></span></button>
    </div>
</script>
<script id="rowhrefTemplate" type="text/x-jsrender">enviament/{{:id}}/detall</script>
<div id="cover-spin"></div>
<form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="callbackFiltreCommand">
    <div class="row">
        <div id="div-concepte" class="col-md-2">
            <not:inputText name="usuariCodi" inline="true" placeholderKey="enviament.list.concepte"/>
        </div>
        <div class="col-md-2">
            <not:inputSelect id="estat" name="estat" optionMinimumResultsForSearch="0" optionTextKeyAttribute="text" emptyOption="true"
                             placeholderKey="notificacio.list.filtre.camp.estat" inline="true" templateResultFunction="showEstat" />
        </div>
        <div class="col-md-2">
            <not:inputDate name="dataInici" placeholderKey="enviament.list.dataenviament.inici" inline="true" required="false" />
        </div>
        <div class="col-md-2">
            <not:inputDate name="dataFi" placeholderKey="enviament.list.dataenviament.fi" inline="true" required="false" />
        </div>

        <div class="col-md-2 pull-right flex-justify-end">
            <div id="botons-filtre-simple" class="pull-right form-buttons"  style="text-align: right;">
                <button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default" style="padding: 6px 9px; margin-right:5px;" title="<spring:message code="comu.boto.netejar"/>"><span class="fa fa-eraser icona_ocultable" style="padding: 2px 0px;"></span><span class="text_ocultable"><spring:message code="comu.boto.netejar"/></span></button>
                <button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" title="<spring:message code="comu.boto.filtrar"/>"><span class="fa fa-filter" id="botoFiltrar"></span><span class="text_ocultable"><spring:message code="comu.boto.filtrar"/></span></button>
            </div>
        </div>
    </div>
</form:form>
<table
        id="enviament"
        data-toggle="datatable"
        data-url="<c:url value="/callback/datatable"/>"
        class="table table-striped table-bordered"
        data-default-order="0"
        data-default-dir="desc"
<%--		data-individual-filter="true"--%>
        data-botons-template="#botonsTemplate"
        data-date-template="#dataTemplate"
        data-cell-template="#cellFilterTemplate"
        data-paging-style-x="true"
        data-scroll-overflow="adaptMax"
        data-selection-enabled="true"
        data-save-state="true"
        data-mantenir-paginacio="${mantenirPaginacio}"
        style="width:100%">
    <thead>
    <tr>
        <th data-col-name="id" data-visible="false"></th>
        <th data-col-name="usuariCodi" data-converter="date"><spring:message code="enviament.list.datacreacio"/></th>
        <th data-col-name="notificacioId" data-converter="date"><spring:message code="enviament.list.dataenviament"/></th>
        <th data-col-name="data" data-converter="datetime"><spring:message code="enviament.list.dataprogramada"/></th>
    </tr>
    </thead>
</table>
</body>
</html>

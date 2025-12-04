<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
    es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
    pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()));
    pageContext.setAttribute("isRolActualAdministradorLectura", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorLectura(ssc.getRolActual()));
%>
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
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
    <link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <script src="<c:url value="/js/datatable.accions-massives.js"/>"></script>
    <link href="<c:url value="/css/datatable-accions-massives.css"/>" rel="stylesheet"/>

    <script>
        $(document).ready(function () {

            $("#filtrar").click(() => {
                deselecciona()
            });

            let eventMessages = {
                'confirm-accio-massiva': "<spring:message code="enviament.list.user.confirm.accio.massiva"/>",
                'confirm-accio-massiva-enviar': "<spring:message code="callback.list.confirm.accio.massiva.enviar"/>",
                'confirm-accio-massiva-pausar': "<spring:message code="callback.list.confirm.accio.massiva.pausar"/>",
                'confirm-accio-massiva-activar': "<spring:message code="callback.list.confirm.accio.massiva.activar"/>",
            };

            $('#btn-netejar-filtre').click(function () {
                $(':input', $('#form-filtre')).each(function () {
                    let type = this.type, tag = this.tagName.toLowerCase();
                    if (type == 'text' || type == 'password' || tag == 'textarea') {
                        this.value = '';
                    } else if (type == 'checkbox' || type == 'radio') {
                        this.checked = false;
                    } else if (tag == 'select') {
                        this.selectedIndex = 0;
                    }

                });
                deselecciona();
            });

            initEvents($('#callback'), 'callback', eventMessages)

        });

        function deselecciona() {

            $(".seleccioCount").html(0);
            $.ajax({
                type: 'GET',
                url: "<c:url value="/callback/deselect"/>",
                async: false,
                success: function (data) {
                    $(".seleccioCount").html(data);
                    $('#callback').webutilDatatable('select-none');
                }
            });
        }

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
    <c:if test="${!isRolActualAdministradorLectura}">
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
                                <li><a id="enviarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.enviar"/></a></li>
                                <li><a id="pausarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.pausar"/></a></li>
                                <li><a id="activarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.activar"/></a></li>
                                <li><a id="esborrarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.esborrar"/></a></li>
                            </ul>
                        </div>
                </div>
            </div>
        </script>
    </c:if>

    <script id="cellFilterTemplate" type="text/x-jsrender">
        <div class="dropdown">
            <button type="submit" id="btnFiltrar" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-search"></span></button>
        </div>
    </script>
    <div id="cover-spin"></div>
    <form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="callbackFiltreCommand">
        <div class="row">
            <div class="col-md-2">
                <not:inputText name="usuariCodi" inline="true" placeholderKey="callback.list.codi.aplicacio"/>
            </div>
            <div class="col-md-2">
                <not:inputText name="referenciaRemesa" inline="true" placeholderKey="callback.list.remesa.referencia"/>
            </div>
            <div class="col-md-2">
                <not:inputDate name="dataInici" placeholderKey="callback.filtre.data.creacio.inici" inline="true" required="false" />
            </div>
            <div class="col-md-2">
                <not:inputDate name="dataFi" placeholderKey="callback.filtre.data.creacio.fi" inline="true" required="false" />
            </div>
            <div class="col-md-2">
                <not:inputDate name="dataIniciUltimIntent" placeholderKey="callback.filtre.data.ultim.intent.inici" inline="true" required="false" />
            </div>
            <div class="col-md-2">
                <not:inputDate name="dataFiUltimIntent" placeholderKey="callback.filtre.data.ultim.intent.fi" inline="true" required="false" />
            </div>
            <div class="col-md-2">
                <not:inputSelect name="estat" optionItems="${estats}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"
                             placeholderKey="callback.filtre.estat" textKey="callback.filtre.fi.reintents" required="true" labelSize="0"/>
            </div>
            <div class="col-md-1">
                <not:inputSelect name="fiReintents" optionItems="${fiReintentsList}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"
                                 placeholderKey="callback.filtre.fi.reintents" textKey="callback.filtre.fi.reintents" required="true" labelSize="0"/>
            </div>


            <div class="col-md-2 pull-right flex-justify-end">
                <button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default" style="padding: 6px 9px; margin-right:5px;" title="<spring:message code="comu.boto.netejar"/>"><span class="fa fa-eraser icona_ocultable" style="padding: 2px 0px;"></span><span class="text_ocultable"><spring:message code="comu.boto.netejar"/></span></button>
                <button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" title="<spring:message code="comu.boto.filtrar"/>"><span class="fa fa-filter" id="botoFiltrar"></span><span class="text_ocultable"><spring:message code="comu.boto.filtrar"/></span></button>
            </div>
        </div>
    </form:form>
    <table
            id="callback"
            data-toggle="datatable"
            data-url="<c:url value="/callback/datatable"/>"
            class="table table-striped table-bordered"
            data-default-order="7"
            data-default-dir="desc"
    <%--		data-individual-filter="true"--%>
            <c:if test="${!isRolActualAdministradorLectura}">
                data-botons-template="#botonsTemplate"
            </c:if>
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
            <th data-col-name="notificacioId" data-visible="false"></th>
            <th data-col-name="maxIntents" data-visible="false"></th>
            <th data-col-name="pausat" data-visible="false"></th>
            <th data-col-name="usuariCodi"><spring:message code="callback.list.codi.aplicacio"/></th>
            <th data-col-name="endpoint"><spring:message code="callback.list.endpoint"/></th>
            <th data-col-name="intents" data-template="#intentsTemplate"><spring:message code="callback.list.intent"/>
                <script id="intentsTemplate" type="text/x-jsrender">
                    {{:intents}}/{{:maxIntents}}
                </script>
            </th>
            <th data-col-name="dataCreacio" data-converter="datetime"><spring:message code="callback.list.data.creacio"/></th>
            <th data-col-name="ultimIntent" data-converter="datetime"><spring:message code="callback.list.data.ultim.intent"/></th>
            <th data-col-name="properIntent" data-orderable="false" data-converter="datetime"><spring:message code="callback.list.data.propera.execucio"/></th>
            <th data-col-name="estat"><spring:message code="callback.list.estat"/></th>
            <th data-col-name="pausat" data-template="#cellPausatTemplate" class="th-checkbox">
                <spring:message code="callback.list.pausat"/>
                <script id="cellPausatTemplate" type="text/x-jsrender">
                    {{if pausat}}<span class="fa fa-check"></span>{{/if}}
                </script>
            </th>
            <th data-col-name="notificacioReferencia" data-orderable="false" data-template="#referenciaTemplate"><spring:message code="callback.list.remesa.referencia"/>
                <script id="referenciaTemplate" type="text/x-jsrender">
                    <a href="<c:url value='/notificacio/{{:notificacioId}}/info'/>" data-toggle="modal" data-height="700px" data-processar="true"> {{:notificacioReferencia}}</a>
                </script>
            </th>
            <c:if test="${!isRolActualAdministradorLectura}">
                <th data-orderable="false" data-disable-events="true" data-template="#cellAccionsTemplate" width="60px" style="z-index:99999;">
                    <script id="cellAccionsTemplate" type="text/x-jsrender">
                        <div class="dropdown">
                            <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
                            <ul class="dropdown-menu dropdown-menu-right">
                                <li><a href="<c:url value="/callback/{{:id}}/enviar"/>"><span class="fa fa-paper-plane-o"></span>&nbsp; <spring:message code="callback.boto.enviar"/></a></li>
                                {{if pausat}}
                                    <li><a href="<c:url value="/callback/{{:id}}/activar"/>"><span class="fa fa-play"></span>&nbsp; <spring:message code="callback.boto.activar"/></a></li>
                                {{else}}
                                    <li><a href="<c:url value="/callback/{{:id}}/pausar"/>"><span class="fa fa-pause"></span>&nbsp; <spring:message code="callback.boto.pausar"/></a></li>
                                {{/if}}
                                <li><a href="<c:url value="/callback/{{:id}}/esborrar"/>"><span class="fa fa-paper-trash-o"></span>&nbsp; <spring:message code="callback.boto.esborrar"/></a></li>
                            </ul>
                        </div>
                    </script>
                </th>
            </c:if>
        </tr>
        </thead>
    </table>
</body>
</html>

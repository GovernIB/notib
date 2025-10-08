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
    <title><spring:message code="accions.massives.list.titol"/></title>
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


    <style>
        .elements {
            width: 100%;
            border-collapse: collapse;
            table-layout: auto;
        }
        .fit-content {
            min-width: 20%;
            white-space: nowrap;
        }
        .remaining-space {
            width: 100%;
        }
    </style>
    <script>

        function copyToClipboard(clipBoard) {
            navigator.clipboard.writeText(clipBoard);
        }

        function obrirReferenciaLink(referencia, seleccioTipus) {

            let url = seleccioTipus === "NOTIFICACIO" ? '<c:url value="/notificacio/filtrades/"/>' : '<c:url value="/enviament/filtrades/"/>';
            return seleccioTipus === "NOTIFICACIO" ? '<a href="' + url + referencia + '" target="_blank">' + referencia + '</a>'
                    : '<a href="' + url + referencia + '" target="_blank">' + referencia + '</a>';
        }

        function mostraElementsAccio(td, rowData) {
            var getUrl = "<c:url value="/accions/massives/"/>" + rowData.id + "/detall";
            $.get(getUrl).done(function (data) {
                $(td).empty();
                $(td).append(
                    '<table class="table table-striped table-bordered elements">' +
                    '<caption><spring:message code="notificacio.list.enviament.list.titol"/></caption>' +
                    '<thead>' +
                    '<tr>' +
                    '<th class="fit-content"><spring:message code="accions.massives.referencia"/></th>' +
                    '<th class="fit-content"><spring:message code="accions.massives.data"/></th>' +
                    '<th class="fit-content"><spring:message code="accions.massives.estat"/></th>' +
                    '<th class="remaining-space"><spring:message code="accions.massives.error.desc"/></th>' +
                    '</tr>' +
                    '</thead><tbody></tbody></table>');
                contingutTbody = '';
                for (i = 0; i < data.length; i++) {
                    let detall = data[i];
                    let button = '<button id="copyParametres" class="btn btn-default" title="' + '<spring:message code="comu.clipboard.copy"/>'
                        + '" onclick="copyToClipboard(\'' + detall.errorStacktrace + '\')">' + '<span class="fa fa-clipboard"></span></button>';
                    contingutTbody += '<tr>'
                    // contingutTbody += '<td><a style ="cursor:pointer" onclick="obrirReferenciaLink(\'' + detall.referencia + '\', \'' + detall.seleccioTipus + '\')">' + detall.referencia + '</a></td>';
                    contingutTbody += '<td>' + obrirReferenciaLink(detall.referencia, detall.seleccioTipus) + '</td>';
                    contingutTbody += '<td>' + detall.dataString + '</td>';
                    contingutTbody += '<td>' +
                        (detall.pendent ?
                                '<spring:message code="accions.massives.estat.pendent"/>' :
                                (detall.executadaOk ?
                                        "<span class='fa fa-check-circle'> </span><span> " +
                                        '<spring:message code="accions.massives.estat.finalitzat"/>' +
                                        '</span>' :
                                        '<span class="fa fa-times-circle"> </span><span> ' + '<spring:message code="accions.massives.estat.error"/>' + '</span>'
                                )
                        ) +
                        '</td>';
                    let errors = detall.errorStacktrace ? '<span class="fa fa-warning text-danger" title="' + detall.errorStacktrace +
                                '" style="margin:5px; cursor:pointer"></span>' + '  </span>' + '<span>  ' + button + '</span>' : "";

                    contingutTbody += '<td>' + (detall.errorDesc ? detall.errorDesc + errors : "") + '</td>';
                    contingutTbody += '</tr>';
                }
                $('table tbody', td).append(contingutTbody);
                $('table tbody td').webutilModalEval();
            });
        }

        $(document).ready(function () {

            $('[data-toggle="tooltip"]').tooltip();
            let $taula = $('#accions-massives');
            $taula.on('rowinfo.dataTable', function(e, td, rowData) {
                mostraElementsAccio(td, rowData)
            });

            $("#filtrar").click(() => {
                deselecciona()
            });

            $('#btn-netejar-filtre').click(function () {
                $(':input', $('#form-filtre')).each(function () {
                    let type = this.type, tag = this.tagName.toLowerCase();
                    if (type == 'text' || type == 'password' || tag == 'textarea') {
                        this.value = '';
                    } else if (type == 'checkbox' || type == 'radio') {
                        this.checked = false;
                    } else if (tag == 'select') {
                        this.selectedIndex = -1;
                    }

                });
                deselecciona();
            });

            let eventMessages = {
                'confirm-accio-massiva': "<spring:message code="enviament.list.user.confirm.accio.massiva"/>",
                'confirm-accio-massiva-enviar': "<spring:message code="callback.list.confirm.accio.massiva.enviar"/>",
                'confirm-accio-massiva-pausar': "<spring:message code="callback.list.confirm.accio.massiva.pausar"/>",
                'confirm-accio-massiva-activar': "<spring:message code="callback.list.confirm.accio.massiva.activar"/>",
            };
            initEvents($('#accions-massives'), 'accions-massives', eventMessages);

        });

        function deselecciona() {

            $(".seleccioCount").html(0);
            $.ajax({
                type: 'GET',
                url: "<c:url value="/accions/massives/deselect"/>",
                async: false,
                success: function (data) {
                    $(".seleccioCount").html(data);
                    $('#accions-massives').webutilDatatable('select-none');
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
        .margin {
            margin-right:3px;
        }
    </style>
</head>
<body>

<div id="loading-screen" class="loading-screen" >
    <div id="processing-icon" class="processing-icon">
        <span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: dimgray;margin-top: 10px;"></span>
    </div>
</div>
<%--<c:if test="${!isRolActualAdministradorLectura}">--%>
<%--    <script id="botonsTemplate" type="text/x-jsrender">--%>
<%--        <div class="text-right">--%>
<%--            <div class="btn-group">--%>
<%--                    <button id="seleccioAll" title="<spring:message code="enviament.list.user.seleccio.tots" />" class="btn btn-default" ><span class="fa fa-check-square-o"></span></button>--%>
<%--                        <button id="seleccioNone" title="<spring:message code="enviament.list.user.seleccio.cap" />" class="btn btn-default" ><span class="fa fa-square-o"></span></button>--%>
<%--                        <div id="seleccioCount" class="btn-group">--%>
<%--                            <button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">--%>
<%--                                <span class="badge seleccioCount">${fn:length(seleccio)}</span> <spring:message code="enviament.list.user.accions.massives"/> <span class="caret"></span>--%>
<%--                            </button>--%>
<%--                            <ul class="dropdown-menu dropdown-left">--%>
<%--                                <li><a id="enviarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.enviar"/></a></li>--%>
<%--                                <li><a id="pausarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.pausar"/></a></li>--%>
<%--                                <li><a id="activarCallbacks" style="cursor: pointer;"><spring:message code="callback.boto.activar"/></a></li>--%>
<%--                            </ul>--%>
<%--                        </div>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--    </script>--%>
<%--</c:if>--%>

<%--<script id="cellFilterTemplate" type="text/x-jsrender">--%>
<%--    <div class="dropdown">--%>
<%--        <button type="submit" id="btnFiltrar" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-search"></span></button>--%>
<%--    </div>--%>
<%--</script>--%>
<div id="cover-spin"></div>
<form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="accioMassivaFiltreCommand">
    <div class="row">
        <div class="col-md-2">
            <not:inputSelect name="tipus" optionItems="${tipusAccions}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"
                             placeholderKey="accions.massives.tipus" textKey="accions.massives.tipus" required="true" labelSize="0"/>
        </div>
        <c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
        <c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
        <div class="col-md-4">
            <not:inputSuggest
                    name="usuariCodi"
                    urlConsultaInicial="${urlConsultaInicial}"
                    urlConsultaLlistat="${urlConsultaLlistat}"
                    textKey="accions.massives.codi.usuari"
                    placeholderKey="accions.massives.codi.usuari"
                    suggestValue="codi"
                    suggestText="nom"
                    inline="true"/>
        </div>
<%--        <div class="col-md-2">--%>
<%--            <not:inputText name="referenciaRemesa" inline="true" placeholderKey="callback.list.remesa.referencia"/>--%>
<%--        </div>--%>
        <div class="col-md-2">
            <not:inputDate name="dataInici" placeholderKey="callback.filtre.data.creacio.inici" inline="true" required="false" />
        </div>
        <div class="col-md-2">
            <not:inputDate name="dataFi" placeholderKey="callback.filtre.data.creacio.fi" inline="true" required="false" />
        </div>
        <div class="col-md-2">
            <not:inputSelect name="estat" optionItems="${elementEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"
                             placeholderKey="accions.massives.estat" textKey="accions.massives.estat" required="true" labelSize="0"/>
        </div>
        <div class="col-md-2 pull-right flex-justify-end">
            <button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default" style="padding: 6px 9px; margin-right:5px;" title="<spring:message code="comu.boto.netejar"/>"><span class="fa fa-eraser icona_ocultable" style="padding: 2px 0px;"></span><span class="text_ocultable"><spring:message code="comu.boto.netejar"/></span></button>
            <button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" title="<spring:message code="comu.boto.filtrar"/>"><span class="fa fa-filter" id="botoFiltrar"></span><span class="text_ocultable"><spring:message code="comu.boto.filtrar"/></span></button>
        </div>
    </div>
</form:form>
<table
        id="accions-massives"
        data-toggle="datatable"
        data-url="<c:url value="/accions/massives/datatable"/>"
        class="table table-striped table-bordered"
        data-default-order="5"
        data-default-dir="desc"
        data-row-info="true"
<%--		data-individual-filter="true"--%>
<%--        <c:if test="${!isRolActualAdministradorLectura}">--%>
<%--            data-botons-template="#botonsTemplate"--%>
<%--        </c:if>--%>
        data-date-template="#dataTemplate"
<%--        data-cell-template="#cellFilterTemplate"--%>
        data-paging-style-x="true"
        data-scroll-overflow="adaptMax"
<%--        data-selection-enabled="true"--%>
        data-save-state="true"
        data-mantenir-paginacio="${mantenirPaginacio}"
        style="width:100%">
    <thead>
    <tr>
        <th data-col-name="id" data-visible="false">#</th>
        <th data-col-name="numOk" data-visible="false">#</th>
        <th data-col-name="numPendent" data-visible="false">#</th>
        <th data-col-name="numErrors" data-visible="false">#</th>
        <th data-col-name="tipus" data-template="#cellTipusTemplate">
            <spring:message code="accions.massives.tipus"/>
            <script id="cellTipusTemplate" type="text/x-jsrender">
                {{if tipus == 'MARCAR_PROCESSADES'}}
                   <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.MARCAR_PROCESSADES"/>
                {{else tipus == 'ACTUALITZAR_ESTAT'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.ACTUALITZAR_ESTAT"/>
                {{else tipus == 'ESBORRAR'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.ESBORRAR"/>
                {{else tipus == 'TORNA_ENVIAR_AMB_ERROR'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.TORNA_ENVIAR_AMB_ERROR"/>
                {{else tipus == 'EXPORTAR_FULL_CALCUL'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.EXPORTAR_FULL_CALCUL"/>
                {{else tipus == 'RECUPERAR_ESBORRADES'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.RECUPERAR_ESBORRADES"/>
                {{else tipus == 'DESCARREGA_JUSTIFICANT_ENVIAMENT'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.DESCARREGA_JUSTIFICANT_ENVIAMENT"/>
                {{else tipus == 'DESCARREGA_CERTIFICAT_RECEPCIO'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.DESCARREGA_CERTIFICAT_RECEPCIO"/>
                {{else tipus == 'AMPLIAR_TERMINI'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.AMPLIAR_TERMINI"/>
                {{else tipus == 'TORNA_ACTIVAR_CONSULTES_CANVI_ESTAT'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.TORNA_ACTIVAR_CONSULTES_CANVI_ESTAT"/>
                {{else tipus == 'TORNA_ACTIVAR_CALLBACK'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.TORNA_ACTIVAR_CALLBACK"/>
                {{else tipus == 'REACTIVAR_SIR'}}
                   <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.REACTIVAR_SIR"/>
                {{else tipus == 'REACTIVAR_REGISTRE'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.REACTIVAR_REGISTRE"/>
                {{else tipus == 'ENVIAR_CALLBACK'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.ENVIAR_CALLBACK"/>
                {{else tipus == 'ENVIAR_NOT_MOVIL'}}
                    <spring:message code="es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.ENVIAR_NOT_MOVIL"/>
                {{/if}}
            </script>
        </th>
        <th data-col-name="createdDate" data-converter="datetime"><spring:message code="accions.massives.data.creacio"/></th>
        <th data-col-name="dataInici" data-converter="datetime"><spring:message code="accions.massives.data.inici"/></th>
        <th data-col-name="dataFi" data-converter="datetime"><spring:message code="accions.massives.data.fi"/></th>
        <th data-col-name="numErrors" data-template="#cellNumErrorsTemplate">
            <spring:message code="accions.massives.num.resultats"/>
            <script id="cellNumErrorsTemplate" type="text/x-jsrender">
                {{if numOk > 0}}
                    <span class="label label-success margin">{{:numOk}} <span class="fa fa-check"></span></span>
                {{/if}}
                {{if numErrors > 0}}
                    <span class="label label-danger margin">{{:numErrors}} <span class="fa fa-times"></span></span>
                {{/if}}
                {{if numPendent > 0}}
                    <span class="label label-info">{{:numPendent}} <span class="fa fa-clock-o"></span></span>
                {{/if}}
            </script>
        </th>
        <th data-col-name="progresBar" data-template="#cellProgresBarTemplate">
            <spring:message code="accions.massives.num.progres"/>
            <script id="cellProgresBarTemplate" type="text/x-jsrender">
                <div class="progress">
                  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="{{:progresBar}}" aria-valuemin="0" aria-valuemax="100"
                    style="{{if progresBar > 0}}min-width: 2em;{{/if}} width: {{:progresBar}}%;">
                    {{if progresBar > 0}}
                        {{:progresBar}}%
                    {{/if}}
                  </div>
                </div>
            </script>
        </th>
        <th data-col-name="createdByCodi"><spring:message code="accions.massives.codi.usuari"/></th>
    </tr>
    </thead>
</table>
</body>
</html>
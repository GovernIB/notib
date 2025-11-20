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
    <title><spring:message code="decorator.menu.permisos.usuaris"/></title>
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
<%--    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--%>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
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

        function crearTaula(td, jsonPermisos, titol, codiUsuari) {

            let isTaulaProcediments = titol === "<spring:message code="procediment.permis.directe"/>";
            let taulaId = isTaulaProcediments ? "procediments-" + codiUsuari : "organs-" + codiUsuari;
            let contingutTbody = (!isTaulaProcediments ? '<button style="float:right;" class="btn btn-success" onclick="exportar( \'' + codiUsuari + '\')">Exportar</button>' : '') +
                '<table id="'+ taulaId + '"class="table table-striped table-bordered elements">' +
                '<caption><b>' + titol + '</b></caption>' +
                '<thead>' +
                '<tr>' +
                '<th class="fit-content"><spring:message code="organgestor.list.columna.nom" /></th>' +
                '<th><spring:message code="procediment.permis.columna.tipus" /></th>' +
                '<th><spring:message code="procediment.permis.columna.principal"/></th>' +
                '<th><span class="fa fa-user-plus"></th>' +
                '<th><span class="fa fa-search"></span></th>' +
                '<th><span class="fa fa-check-square-o"></span></th>' +
                '<th><span class="fa fa-cog"></span></th>' +
                '<th><span class="fa fa-globe"></span></th>' +
                '<th><span class="fa fa-gavel"></span></th>' +
                '<th><span class="fa fa-envelope-o"></span></th>' +
                '<th><span class="fa fa-envelope"></span></th>' +
                '<th><span class="fa fa-paper-plane-o"></span></th>' +
                '</tr>' +
                '</tr>' +
                '</thead><tbody>';
            const parsedObject = JSON.parse(jsonPermisos);
            const permisos = new Map(Object.entries(parsedObject));
            let noFiles = true;
            for (const [key, value] of permisos) {
                if (!value) {
                    continue;
                }
                contingutTbody += '<tr>';
                for (i = 0; i < value.length; i++) {
                    let permis = value[i];
                    if (!permis) {
                        continue;
                    }
                    noFiles = false;
                    contingutTbody += '<td>' + permis.organNom + '</td>';
                    contingutTbody += '<td>' + permis.tipus + '</td>';
                    contingutTbody += '<td>' + permis.nomSencerAmbCodi + '</td>';
                    contingutTbody += '<td>' + (permis.administrador ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.read ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.processar ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.administration ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.comuns ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.notificacio ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.comunicacio ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.comunicacioSir ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingutTbody += '<td>' + (permis.comunicacioSenseProcediment ? '<span class="fa fa-check"></span>' : "") + '</td>';
                }
                contingutTbody += '</tr>';
            }
            if (noFiles) {
                contingutTbody += filaSensePermisos();
            }
            contingutTbody += '</tbody></table>';
            td.append(contingutTbody);
        }

        function filaSensePermisos() {
            return '<tr><td>Sense permisos</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>';
            // return "";
        }


        function crearTaulaProcSerOrgan(td, procSerOrgan, titol, codiUsuari) {

            let tableId = "procSerOrgan-" + codiUsuari;
            let tbodyId = "tbody-procSerOrgan-" + codiUsuari;
            let theadId = "thead-procSerOrgan-" + codiUsuari;
            let spanId = "span-" + theadId;
            let contingutTbody =
                '<table id="' + tableId + '" class="table table-striped table-bordered elements">' +
                '<caption><b>' + titol + '</b><a style="margin-left:10px;" onclick="toggleFila( \'' + theadId + '\', \'' + tbodyId + '\');" class="btn btn-default btn-sm btn-rowInfo"><span id="' + spanId + '" class="fa fa-caret-down"></span></a></caption>' +
                '<thead id="' + theadId + '" style="display:none;">' +
                '<tr>' +
                    '<th class="fit-content"><spring:message code="organgestor.list.columna.nom" /></th>' +
                    '<th class="fit-content"><spring:message code="procediment.list.columna.organGestor" /></th>' +
                    '<th><spring:message code="procediment.permis.columna.tipus" /></th>' +
                    '<th><spring:message code="procediment.permis.columna.principal"/></th>' +
                    '<th><span class="fa fa-user-plus"></th>' +
                    '<th><span class="fa fa-search"></span></th>' +
                    '<th><span class="fa fa-check-square-o"></span></th>' +
                    '<th><span class="fa fa-cog"></span></th>' +
                    '<th><span class="fa fa-globe"></span></th>' +
                    '<th><span class="fa fa-gavel"></span></th>' +
                    '<th><span class="fa fa-envelope-o"></span></th>' +
                    '<th><span class="fa fa-envelope"></span></th>' +
                    '<th><span class="fa fa-paper-plane-o"></span></th>' +
                '</tr>' +
                '</thead><tbody id="' + tbodyId + '" style="display:none;">';
            let noFiles = true;
            for (procSer of procSerOrgan) {
                if (!procSer) {
                    continue;
                }
                contingutTbody += '<tr>';
                contingutTbody += '<td>' + procSer.codiValor.valor + '</td>';
                contingutTbody += '<td>' + procSer.codiValor.organGestor + ' - ' + procSer.codiValor.organNom + '</td>';
                contingutTbody += '<td>' + procSer.permis.tipus + '</td>';
                contingutTbody += '<td>' + procSer.permis.principal + '</td>';
                contingutTbody += '<td>' + (procSer.permis.administrador ? '<span class="fa fa-check"></span>' : "")  + '</td>';
                contingutTbody += '<td>' + (procSer.permis.read ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.processar ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.administration ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.comuns ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.notificacio ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.comunicacio ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.comunicacioSir ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '<td>' + (procSer.permis.comunicacioSenseProcediment ? '<span class="fa fa-check"></span>' : "") + '</td>';
                contingutTbody += '</tr>';
                noFiles = false;
            }
            if (noFiles) {
                contingutTbody += filaSensePermisos();
            }
            contingutTbody += '</tbody></table>';
            td.append(contingutTbody);
        }

        function toggleFila(id1, id2) {

            $("#" + id1).toggle();
            if (id2) {
                $("#" + id2).toggle();
            }
            let span = $("#span-" + id1);
            if (!span) {
                return;
            }
            if (span.hasClass("fa-caret-down")) {
                span.removeClass("fa-caret-down");
                span.addClass("fa-caret-up");
            } else {
                span.removeClass("fa-caret-up");
                span.addClass("fa-caret-down");
            }
        }

        function crearTaulaOrgansFills(td, organsFills, organsMap, codiUsuari) {

            if (!organsFills) {
                return;
            }
            let tableId = "organsFills-" + codiUsuari;
            let tbodyId = "tbody-organsFills-" + codiUsuari;
            let theadId = "thead-organsFills-" + codiUsuari;
            let spanId = "span-" + theadId;
            let organsMapJson = JSON.parse(organsMap);
            let organsMapMap = new Map(Object.entries(organsMapJson));
            let organsFillsJson = JSON.parse(organsFills);
            let organsFillsMap = new Map(Object.entries(organsFillsJson));
            let  contingut = '<table id="' + tableId + '" class="table table-striped table-bordered elements">' +
                '<caption><b><spring:message code="es.caib.notib.organs.fills"/></b><a style="margin-left:10px;" onclick="toggleFila( \'' + theadId + '\', \'' + tbodyId + '\');" class="btn btn-default btn-sm btn-rowInfo"><span id="' + spanId + '" class="fa fa-caret-down"></span></a></caption>' +
                '<thead id="' + theadId + '" style="display:none;">' +
                '<tr>' +
                '<th class="fit-content"><spring:message code="organgestor.list.columna.nom" /></th>' +
                '<th class="fit-content"><spring:message code="procediment.list.columna.organGestor" /></th>' +
                '<th><spring:message code="procediment.permis.columna.tipus" /></th>' +
                '<th><spring:message code="procediment.permis.columna.principal"/></th>' +
                '<th><span class="fa fa-user-plus"></th>' +
                '<th><span class="fa fa-search"></span></th>' +
                '<th><span class="fa fa-check-square-o"></span></th>' +
                '<th><span class="fa fa-cog"></span></th>' +
                '<th><span class="fa fa-globe"></span></th>' +
                '<th><span class="fa fa-gavel"></span></th>' +
                '<th><span class="fa fa-envelope-o"></span></th>' +
                '<th><span class="fa fa-envelope"></span></th>' +
                '<th><span class="fa fa-paper-plane-o"></span></th>' +
                '</tr>' +
                '</thead><tbody id="' + tbodyId + '" style="display:none;">';
            if (organsFillsMap.size === 0) {
                divContingut += '<tr><td>Sense permisos</td></tr>';
                td.append(divContingut);
                return;
            }
            let noFiles = true;
            for (key of organsFillsMap.keys()) {
                let fills = organsFillsMap.get(key);
                let permis = organsMapMap.get(key)[0];
                if (!permis) {
                    continue;
                }
                for (let fill of fills) {
                    contingut += '<tr>';
                    contingut += '<td>' + fill + '</td>';
                    contingut += '<td>' + permis.organNom + '</td>';
                    contingut += '<td>' + permis.tipus + '</td>';
                    contingut += '<td>' + permis.principal + '</td>';
                    contingut += '<td>' + (permis.administrador ? '<span class="fa fa-check"></span>' : "")  + '</td>';
                    contingut += '<td>' + (permis.read ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.processar ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.administration ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.comuns ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.notificacio ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.comunicacio ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.comunicacioSir ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '<td>' + (permis.comunicacioSenseProcediment ? '<span class="fa fa-check"></span>' : "") + '</td>';
                    contingut += '</tr>'
                    noFiles = false;
                }
            }
            if (noFiles) {
                contingut += filaSensePermisos();
            }
            contingut += '</tbody></table>';
            td.append(contingut);
        }

        function mostraElementsAccio(td, rowData) {
            let spinner = '<div id="spinner-container" class="loading-screen ocult" style="display:flex;justify-content:center;">' +
                            '<div class="spin-box"><span class="fa fa-spin fa-circle-o-notch  fa-3x"></span></div></div>';
            td.append(spinner);
            let getUrl = "<c:url value="/permisos/usuari/"/>" + rowData.codi;
            $.get(getUrl).done(function (data) {
                $(td).empty();
                if (data.permisosOrgans) {
                    crearTaula(td, data.permisosOrgans, "<spring:message code="organ.permis.directe"/>", rowData.codi);
                }
                if (data.organsFills) {
                    crearTaulaOrgansFills(td, data.organsFills, data.permisosOrgans, rowData.codi)
                }
                if (data.permisosProcediment) {
                    crearTaula(td, data.permisosProcediment, "<spring:message code="procediment.permis.directe"/>", rowData.codi);
                }
                if (data.procSerOrgan) {
                    crearTaulaProcSerOrgan(td, data.procSerOrgan, "<spring:message code="procediment.organ.permis"/>", rowData.codi);
                }
            });
        }

        function exportar(codiUsuari) {

            let wb = XLSX.utils.book_new();
            let tableIDs = ['organs-' + codiUsuari, "organsFills-" + codiUsuari, "procediments-" + codiUsuari, "procSerOrgan-" + codiUsuari];
            tableIDs.forEach(function(id, index) {
                let table = document.getElementById(id);
                let clonedTable = table.cloneNode(true);
                $(clonedTable).find('span').each(function() {
                    let iconClass = $(this).attr('class');
                    if (iconClass.includes('fa-user-plus')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.administrador"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-search')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.consulta"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-check-square-o')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.processar"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-cog')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.gestio"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-globe')) {
                        $(this).replaceWith('<spring:message code="organgestor.permis.form.camp.comuns"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-gavel')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.notificacio"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-envelope-o')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.comunicacio"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-envelope')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.comunicacio.sir"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-paper-plane-o')) {
                        $(this).replaceWith('<spring:message code="procediment.permis.form.camp.comunicacio.sense.procediment"/>'); // Replace with a cross or text
                    } else if (iconClass.includes('fa-check')) {
                        $(this).replaceWith('✔️');
                    }
                });

                let ws = XLSX.utils.table_to_sheet(clonedTable);
                var range = XLSX.utils.decode_range(ws['!ref']);
                var colWidth = [];

                for (var C = range.s.c; C <= range.e.c; ++C) {
                    colWidth[C] = { w: 120 }; // Set fixed width for each column
                }

                ws['!cols'] = colWidth;
                XLSX.utils.book_append_sheet(wb, ws, id);
            });
            XLSX.writeFile(wb, 'permisos_usuari_' + codiUsuari + '.xlsx');
        };

        $(document).ready(function () {

            $('[data-toggle="tooltip"]').tooltip();
            let $taula = $('#permisos-usuaris');
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

            // initEvents($('#permisos-usuaris'), 'permisos-usuaris', eventMessages);

        });

        function deselecciona() {

            $(".seleccioCount").html(0);
            $.ajax({
                type: 'GET',
                url: "<c:url value="/permisos/deselect"/>",
                async: false,
                success: function (data) {
                    $(".seleccioCount").html(data);
                    $('#permisos-usuaris').webutilDatatable('select-none');
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
<div id="cover-spin"></div>
<form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="permisosUsuarisFiltreCommand">
    <div class="row">
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
<%--            <not:inputSelect name="clasePermis" optionItems="${clasesPermis}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"--%>
<%--                             placeholderKey="accions.massives.estat" textKey="accions.massives.estat" required="true" labelSize="0"/>--%>
<%--        </div>--%>
        <div class="col-md-2 pull-right flex-justify-end">
            <button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default" style="padding: 6px 9px; margin-right:5px;" title="<spring:message code="comu.boto.netejar"/>"><span class="fa fa-eraser icona_ocultable" style="padding: 2px 0px;"></span><span class="text_ocultable"><spring:message code="comu.boto.netejar"/></span></button>
            <button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" title="<spring:message code="comu.boto.filtrar"/>"><span class="fa fa-filter" id="botoFiltrar"></span><span class="text_ocultable"><spring:message code="comu.boto.filtrar"/></span></button>
        </div>
    </div>
</form:form>
<table
        id="permisos-usuaris"
        data-toggle="datatable"
        data-url="<c:url value="/permisos/datatable"/>"
        class="table table-striped table-bordered"
        data-default-order="1"
        data-default-dir="desc"
        data-row-info="true"
        data-date-template="#dataTemplate"
        data-paging-style-x="true"
        data-scroll-overflow="adaptMax"
        data-save-state="true"
        data-mantenir-paginacio="${mantenirPaginacio}"
        style="width:100%">
    <thead>
    <tr>
        <th data-col-name="codi" ><spring:message code="usuari.form.camp.codi.usuari"/></th>
        <th data-col-name="nom" ><spring:message code="usuari.form.camp.nom"/></th>
        <th data-col-name="llinatges" ><spring:message code="usuari.form.camp.llinatges"/></th>
        <th data-col-name="nif" ><spring:message code="usuari.form.camp.nif"/></th>
        <th data-col-name="email" ><spring:message code="usuari.form.camp.email"/></th>
        <th data-col-name="emailAlt" ><spring:message code="usuari.form.camp.email.alternatiu"/></th>
<%--        <th data-col-name="id" data-orderable="false" data-disable-events="true" data-template="#cellAccionsTemplate" width="60px" style="z-index:99999;">--%>
<%--            <script id="cellAccionsTemplate" type="text/x-jsrender">--%>
<%--                <div class="dropdown">--%>
<%--                    <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>--%>
<%--                    <ul class="dropdown-menu dropdown-menu-right">--%>
<%--&lt;%&ndash;                        <li><a id="botoExportar" href="<c:url value="/permisos/usuari/{codi}/exportar"/>" target="_blank" rel=”noopener noreferrer”><span class="fa fa-download"></span>&nbsp; <spring:message code="enviament.list.user.exportar"/></a></li>&ndash;%&gt;--%>
<%--                        <li><a id="botoExportar" onclick="exportar()><span class="fa fa-download"></span>&nbsp; <spring:message code="enviament.list.user.exportar"/></a></li>--%>
<%--                    </ul>--%>
<%--            </div>--%>
<%--            </script>--%>
<%--        </th>--%>
<%--    </tr>--%>
    </thead>
</table>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:set var="ampladaConcepte">
    <c:choose>
        <c:when test="${isRolActualAdministrador}">200px</c:when>
        <c:otherwise>300px</c:otherwise>
    </c:choose>
</c:set>
<c:set var="ampladaEnviament">
    <c:choose>
        <c:when test="${isRolActualAdministrador}">160px</c:when>
        <c:otherwise>130px</c:otherwise>
    </c:choose>
</c:set>
<c:set var="refresh_state_succes"><spring:message code="notificacio.list.enviament.list.refresca.estat.exitos"/></c:set>
<c:set var="refresh_state_error"><spring:message code="notificacio.list.enviament.list.refresca.estat.error"/></c:set>

<style type="text/css">
    .horaProcessat {
        font-size: small;
    }
    .datepicker table tr td.today, .datepicker table tr td.today:hover {
        color: #000000;
        background: #a4a4a4 !important;
        background-color: #a4a4a4 !important;
    }
    .panel.panel-default.info-enviament {
        margin-top: 6%;
    }
    .motiu_finalitzada {
        background-color: #e0ead5;
    }
    .info_finalitzada_icon:hover {
        cursor: pointer;
    }
    .info_finalitzada_div {
        text-align: right;
    }
    .info_finalitzada_icon {
        top: -16px;
    }
    .info_finalitzada_link:hover {
        text-decoration: none !important;
    }
    .motiu_finalitzada > a {
        display: none;
    }
    .panel-heading.processarButton {
        background-color: red;
    }
    .not-icon-o {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 22px;
        height: 22px;
        background-color: #999999;
        color: white;
        font-weight: bold;
        font-size: 13px;
        margin: 0 auto;
    }
    .com-icon-o {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 27px;
        height: 27px;
        background-color: #dddddd;
        color: #333333;
        font-weight: bold;
        font-size: 13px;
        margin: 0 auto;
    }
    .label-primary {
        background-color: #999999;
    }
    .label-warning {
        background-color: #dddddd;
        color: #333333;
    }

    #notificacio > tbody td:first-child {
        vertical-align: middle;
    }

    #nomesAmbErrorsBtn, #nomesAmbEntregaPostalBtn {
        margin-right: 2%;
    }

    .cellEstat {
        position: relative;
    }
    .cellEstat:hover > .hover-button {
        display: block;
    }
    .hover-button {
        position: absolute;
        bottom: -5px;
        right: 0px;
        display: none;
    }
    .hover-button > a {
        color: #ccc;
    }
    .hover-button > a:hover {
        color: #888;
    }

    .dropdown-left {
        right: 0;
        left: auto
    }
</style>
<script type="text/javascript">

    function showEstat(element) {

        let bsIconCode = "";
        let translate = element.text;
        let text = ""
        let style = ""
        if (element.id == 'PENDENT') {
            bsIconCode = "fa fa-clock-o";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.PENDENT"/>";
        }
        if (element.id == 'ENVIADA') {
            bsIconCode = "fa fa-send-o";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA"/>";
        }
        if (element.id == 'REGISTRADA') {
            bsIconCode = "fa fa-file-o";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.REGISTRADA"/>";
        }
        if (element.id == 'FINALITZADA') {
            bsIconCode = "fa fa-check";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA"/>";
        }
        if (element.id == 'PROCESSADA') {
            bsIconCode = "fa fa-check-circle";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.PROCESSADA"/>";
        }
        if (element.id == 'EXPIRADA') {
            bsIconCode = "fa fa-asterisk";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.EXPIRADA"/>";
        }
        if (element.id == 'NOTIFICADA') {
            bsIconCode = "fa fa-check-circle";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.NOTIFICADA"/>";
        }
        if (element.id == 'REBUTJADA') {
            bsIconCode = "fa fa-times";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.REBUTJADA"/>";
        }
        if (element.id == 'ENVIAT_SIR') {
            bsIconCode ="label label-primary";
            text = "S"
            style = "display:inline-block; padding:3px";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIAT_SIR"/>";
        }
        if (element.id == 'ENVIADA_AMB_ERRORS') {
            bsIconCode = "fa fa-send-o";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS"/>";
        }
        if (element.id == 'FINALITZADA_AMB_ERRORS') {
            bsIconCode = "fa fa-check";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS"/>";
        }
        if (element.id == 'ENVIANT') {
            bsIconCode = "fa fa-clock-o";
            translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIANT"/>";
        }

        return $('<span style= " ' + style + ' " class="' + bsIconCode + '">' + text + ' </span><span>  ' + translate + '</span>');
    }

    var myHelpers = {
        recuperarEstatEnviament: returnEnviamentsStatusDiv,
        hlpIsUsuari: isRolActualUsuari,
        hlpIsAdministradorEntitat: isRolActualAdministradorEntitat,
        hlpIsAdministradorOrgan: isRolActualAdministradorOrgan};

    $.views.helpers(myHelpers);

    function isRolActualUsuari() {
        return ${isRolActualUsuari};
    }

    function isRolActualAdministradorEntitat() {
        return ${isRolActualAdministradorEntitat};
    }

    function isRolActualAdministradorOrgan() {
        return ${isRolActualAdministradorOrgan};
    }

    var organsGestors = [];
    organsGestors.push({id:"", text:"", estat:"V"});
    <c:forEach items="${organsGestorsPermisLectura}" var="organGestor">
    organsGestors.push({id:"${organGestor.id}", text:"${organGestor.valor}", estat:"${organGestor.estat}"});
    </c:forEach>

    var notificacioEnviamentEstats = [];
    <c:forEach var="estat" items="${notificacioEnviamentEstats}">
    notificacioEnviamentEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
    </c:forEach>
    var comunicacioTipus = [];
    <c:forEach var="tipus" items="${notificacioComunicacioTipus}">
    comunicacioTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
    </c:forEach>
    var enviamentTipus = [];
    <c:forEach var="tipus" items="${notificacioEnviamentTipus}">
    enviamentTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
    </c:forEach>


    function formatDate(data) {
        //Añadir ceros a los numeros de un dígito
        Number.prototype.padLeft = function(base,chr){
            var  len = (String(base || 10).length - String(this).length)+1;
            return len > 0? new Array(len).join(chr || '0')+this : this;
        }
        if (data !== null) {
            //dd/MM/yyyy HH:mm:SS
            var procesDate = new Date(data),
                procesDateFormat = [procesDate.getDate().padLeft(),
                        (procesDate.getMonth()+1).padLeft(),
                        procesDate.getFullYear()].join('/') +' ' +
                    [procesDate.getHours().padLeft(),
                        procesDate.getMinutes().padLeft(),
                        procesDate.getSeconds().padLeft()].join(':');
            return procesDateFormat;
        } else {
            return null;
        }
    }

    function returnEnviamentsStatusDiv(notificacioId) {
        var content = "";
        var getUrl = "<c:url value="/notificacio/"/>" + notificacioId + "/enviament";

        // $.getJSON({
        //     url: getUrl,
        //     success: (user) => {
        //         for (i = 0; i < user.length; i++) {
        //             content += (user[i].notificaEstat) ? notificacioEnviamentEstats[user[i].notificaEstat] + ',' : '';
        //         }
        //         if (content !== undefined && content != '') {
        //             content = "("+content.replace(/,\s*$/, "")+")";
        //         }
        //         $('.estat_' + notificacioId).append(content);
        //     },
        //     error: console.log("No s'han pogut recuperar els enviaments de la notificació: " + notificacioId)
        // })
    }

    function mostraEnviamentsNotificacio(td, rowData) {
        var getUrl = "<c:url value="/notificacio/"/>" + rowData.id + "/enviament";
        $.get(getUrl).done(function(data) {
            $(td).empty();
            $(td).append(
                '<table class="table table-striped table-bordered table-enviaments">' +
                '<caption><spring:message code="notificacio.list.enviament.list.titol"/></caption>' +
                '<thead>' +
                '<tr>' +
                '<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' +
                '<th><spring:message code="notificacio.list.enviament.list.destinataris"/></th>' +
                '<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
                '<th></th>' +
                '</tr>' +
                '</thead><tbody></tbody></table>');
            contingutTbody = '';
            for (i = 0; i < data.length; i++) {
                var nomTitular = '', llinatge1 = '', llinatge2 = '', destinataris = '', nif = '';

                if (data[i].titular.nom != null) {
                    nomTitular = data[i].titular.nom;
                } else if (data[i].titular.raoSocial != null){
                    nomTitular = data[i].titular.raoSocial;
                }
                if (data[i].titular.llinatge1 != null) {
                    llinatge1 = data[i].titular.llinatge1;
                }
                if (data[i].titular.llinatge2 != null) {
                    llinatge2 = data[i].titular.llinatge2;
                }

                $.each(data[i].destinataris, function (index, destinatari) {
                    var nomDest = '', llinatge1Dest = '', llinatge2Dest = '';
                    if (destinatari.nom != null) {
                        nomDest = destinatari.nom;
                    } else if (destinatari.raoSocial != null){
                        nomDest = destinatari.raoSocial;
                    }
                    if (destinatari.llinatge1 != null) {
                        llinatge1Dest = destinatari.llinatge1;
                    }
                    if (destinatari.llinatge2 != null) {
                        llinatge2Dest = destinatari.llinatge2;
                    }
                    if (destinatari.nif != null) {
                        nif = destinatari.nif;
                    } else {
                        nif = destinatari.dir3Codi;
                    }
                    nif = (nif == null) ? "" : "(" + nif + ")";
                    destinataris += nomDest + ' ' + llinatge1Dest + ' ' + llinatge2Dest + ' ' + nif + ', ';
                });
                if (data[i].titular.nif != null) {
                    nif = data[i].titular.nif;
                } else {
                    nif = data[i].titular.dir3Codi;
                }
                nif = (nif == null) ? "" : "(" + nif + ")";
                if (data[i].perEmail) {
                    nif += " - <span class='fa fa-envelope-o'></span> " + data[i].titular.email;
                }
                contingutTbody += '<tr data-toggle="modal" data-href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '"/>" style="cursor: pointer;">';
                contingutTbody += '<td>' + nomTitular + ' ' + llinatge1 + ' ' + llinatge2 + ' '+ nif +'</td>';
                if (destinataris != ''){
                    //Remove last white space
                    destinataris = destinataris.substr(0, destinataris.length-1);
                    //Remove last comma
                    destinataris = destinataris.substr(0, destinataris.length-1);
                } else {
                    destinataris = '<spring:message code="notificacio.list.enviament.list.sensedestinataris"/>';
                }
                contingutTbody += '<td>' + destinataris + '</td>';
                contingutTbody +=  data[i].estatColor ? '<td style="box-shadow: inset 3px 0px 0px ' + data[i].estatColor + ';"> ' +
                    '              <span class="' + data[i].estatIcona + '"></span><span>  </span>' : '<td>';
                contingutTbody += (data[i].notificaEstat) ? notificacioEnviamentEstats[data[i].notificaEstat] : '';
                if (data[i].notificaEstat == "FINALITZADA" && data[i].perEmail) {
                    if (rowData.enviamentTipus == "NOTIFICACIO") {
                        contingutTbody += " (<spring:message code="notificacio.list.enviament.list.finalitzat.avis.email"/>)"
                    } else {
                        contingutTbody += " (<spring:message code="notificacio.list.enviament.list.finalitzat.email"/>)"
                    }
                }
                if (data[i].ultimEventError && data[i].notificaEstat !== "FINALITZADA" && data[i].notificaEstat !== "PROCESSADA") {
                    var errorTitle = '';
                    if (data[i].notificacioErrorDescripcio) {
                        errorTitle = data[i].notificacioErrorDescripcio.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
                    } else {
                        errorTitle = "Descripció de l'error no registrada";
                    }
                    contingutTbody += ' <span class="fa fa-warning text-danger" title="' + errorTitle + '"></span>';
                }
                if (data[i].fiReintents) {
                    contingutTbody += ' <span class="fa fa-warning text-warning" title="' + data[i].fiReintentsDesc + '"></span>';
                }
                if (data[i].errorLastCallback) {
                    contingutTbody += ' <span class="fa fa-exclamation-circle text-primary" title="' + data[i].errorLastCallback + '"></span>';
                }
                if (data[i].callbackFiReintents) {
                    contingutTbody += ' <span class="fa fa-warning text-info" title="' + data[i].callbackFiReintentsDesc + '"></span>';
                }
                if (data[i].notificacioMovilErrorDesc) {
                    contingutTbody += ' <span style="color:#8a6d3b; cursor:pointer;" class="fa fa-mobile fa-lg" title="' + data[i].notificacioMovilErrorDesc + '"></span>';
                }
                contingutTbody += '</td>';
                contingutTbody += '<td width="114px">';
                if (data[i].notificaCertificacioData != null) {
                    contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/certificacioDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.certificacio"/>"><span class="fa fa-download"></span></a>';
                }

                contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '"/>" data-toggle="modal" class="btn btn-default btn-sm"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>';
                contingutTbody += '</td>';
                contingutTbody += '</tr>';
            }
            $('table tbody', td).append(contingutTbody);
            $('table tbody td').webutilModalEval();
        });
    }

    $(function() {
        $(document).on("click", "a.fileDownloadSimpleRichExperience", function() {
            $.fileDownload($(this).attr('href'), {
                preparingMessageHtml: "Estam preparant la descàrrega, per favor esperi...",
                failMessageHtml: "<strong style='color:red'>Ho sentim.<br/>S'ha produït un error intentant descarregar el document.</strong>"
            });
            return false;
        });
    });

    function deselecciona() {

        $(".seleccioCount").html(0);
        $.ajax({
            type: 'GET',
            url: "<c:url value="/notificacio/deselect"/>",
            async: false,
            success: function (data) {
                $(".seleccioCount").html(data);
                $('#notificacio').webutilDatatable('select-none');
            }
        });
    }

    const select2Format = {	theme: 'bootstrap',	width: 'auto', allowClear: true};
    // const formatSelects = () => {
    //     $("#organGestor").select2(select2Format);
    //     $("#procedimentId").select2(select2Format);
    //     $("#serveiId").select2(select2Format);
    // }
    function formatSelects (id) {
        $(id).select2(select2Format);
    }
    $(document).ready(function() {

        let $taula = $('#notificacio');
        $taula.on('rowinfo.dataTable', function(e, td, rowData) {
            mostraEnviamentsNotificacio(td, rowData)
        });

        $taula.on('init.dt', function () {

           //  let rows = this.rows;
           //  for (let row=1; row < rows.length; row++) {
           //      this.rows[row].firstChild.style="border-left: 3px solid red";
           //  }
           // debugger;
            $("#notificacio_wrapper").prepend('<button id="closeAll" class="btn btn-default"><span class="fa fa-caret-square-o-up"></span> <spring:message code="organgestor.arbre.contrau"/></button>');
            $("#notificacio_wrapper").prepend('<button id="expandAll" class="btn btn-default"><span class="fa fa-caret-square-o-down"></span> <spring:message code="organgestor.arbre.expandeix"/> </button>');

            let $btnDesplegarEnvs = $('#btn-desplegar-envs');
            $("#closeAll").on("click", function() {
                var shown = $btnDesplegarEnvs.find("span").hasClass('fa-caret-up');
                $taula.dataTable().api().rows().every( function ( rowIdx, tableLoop, rowLoop ) {
                    var rowData = this.data();
                    let $parentTr = $("#" + this.id());
                    let isCollapsed = $parentTr.find("td:last span").hasClass('fa-caret-up')
                    if (shown) {
                        // accedim d'aquesta manera als tr per no haver de modificar el codi de webutil.datatable
                        $(".table-enviaments").closest("tr").remove();
                    }
                });
                if (shown) {
                    $btnDesplegarEnvs.find("span").removeClass('fa-caret-up');
                    $btnDesplegarEnvs.find("span").addClass('fa-caret-down');
                    $(".btn-rowInfo").find("span").removeClass('fa-caret-up');
                    $(".btn-rowInfo").find("span").addClass('fa-caret-down');
                }
            });
            $("#expandAll").on("click", () => {

                var shown = $btnDesplegarEnvs.find("span").hasClass('fa-caret-up');
                $taula.dataTable().api().rows().every( function ( rowIdx, tableLoop, rowLoop ) {
                    var rowData = this.data();
                    let $parentTr = $("#" + this.id());
                    let isCollapsed = $parentTr.find("td:last span").hasClass('fa-caret-up')
                    if (!shown && !isCollapsed) {
                        $('<tr data-row-info="true"><td colspan="' + $parentTr.children().length + '"></td></tr>').insertAfter($parentTr);
                        mostraEnviamentsNotificacio($('td', $parentTr.next()), rowData)
                    }
                });
                if (!shown) {
                    $btnDesplegarEnvs.find("span").toggleClass('fa-caret-up');
                    $(".btn-rowInfo").find("span").removeClass('fa-caret-down');
                    $(".btn-rowInfo").find("span").addClass('fa-caret-up');
                }
            });
        });

        $("#filtrar").click(() => {
            deselecciona()
        });

        $('#btn-netejar-filtre').click(function() {
            $(':input', $('#form-filtre')).each (function() {
                var type = this.type, tag = this.tagName.toLowerCase();
                if (type == 'text' || type == 'password' || tag == 'textarea') {
                    this.value = '';
                } else if (type == 'checkbox' || type == 'radio') {
                    this.checked = false;
                } else if (tag == 'select') {
                    this.selectedIndex = 0;
                }

            });
            $('#nomesAmbErrorsBtn').removeClass('active');
            $('#nomesAmbErrors').val(false);
            $('#nomesAmbEntregaPostalBtn').removeClass('active');
            $('#nomesAmbEntregaPostal').val(false);
            $('#nomesFiReintentsBtn').removeClass('active');
            $('#nomesFiReintents').val(false);
            // omplirProcediments();
            // omplirServeis();
            deselecciona();
            // $('#form-filtre').submit();
        });

        $('#nomesAmbErrorsBtn').click(function() {
            nomesAmbErrors = !$(this).hasClass('active');
            $('#nomesAmbErrors').val(nomesAmbErrors);
        })

        $('#nomesAmbEntregaPostalBtn').click(function() {
            nomesAmbEntregaPostal = !$(this).hasClass('active');
            $('#nomesAmbEntregaPostal').val(nomesAmbEntregaPostal);
        })

        $('#nomesFiReintentsBtn').click(function() {
            nomesFiReintents = !$(this).hasClass('active');
            $('#nomesFiReintents').val(nomesFiReintents);
        })

        $('#organGestor').on('change', function () {
            omplirProcediments();
            omplirServeis();
        });
        $('#estat').on('select2:unselect', function(e) {
            $('#estat').select2('open');
        });
        $('#organGestor').on('select2:unselect', function(e) {
            $('#organGestor').select2('open');
        });
        $('#procedimentId').on('select2:unselect', function(e) {
            $('#procedimentId').select2('open');
        });
        $('#serveiId').on('select2:unselect', function(e) {
            $('#serveiId').select2('open');
        });

        omplirProcediments();
        omplirServeis();

        loadOrgans($('#organGestor'), organsGestors, "<spring:message code='notificacio.list.columna.organGestor.obsolet'/>");

        $('#organGestor').val(${notificacioFiltreCommand.organGestor})
        // $('#organGestor').select2().trigger('change');
        // $('#estat').select2().trigger('change');
        $('#organGestor').trigger('change');
        $('#estat').trigger('change');

        let eventMessages = {
            'confirm-reintentar-notificacio': "<spring:message code="enviament.list.user.reintentar.notificacio.misatge.avis"/>",
            'confirm-reintentar-errors': "<spring:message code="enviament.list.user.reintentar.errors.misatge.avis"/>",
            'confirm-reintentar-consulta': "<spring:message code="enviament.list.user.reactivar.consulta.misatge.avis"/>",
            'confirm-reintentar-sir': "<spring:message code="enviament.list.user.reactivar.sir.misatge.avis"/>",
            'confirm-update-estat': "<spring:message code="enviament.list.user.actualitzar.estat.misatge.avis"/>",
            'confirm-reactivar-callback': "<spring:message code="enviament.list.user.reactivar.callback.misatge.avis"/>",
            'confirm-enviar-callback': "<spring:message code="enviament.list.user.enviar.callback.misatge.avis"/>",
            'confirm-accio-massiva': "<spring:message code="enviament.list.user.confirm.accio.massiva"/>",
        };
        initEvents($('#notificacio'), 'notificacio', eventMessages);
        formatSelects($('#organGestor'));
    });

    function omplirProcediments() {
        var organGestor = $("#organGestor");
        let organId = $(organGestor).val() == undefined ? "" : $(organGestor).val();
        $.ajax({
            type: 'GET',
            url: "<c:url value="/notificacio/procedimentsOrgan/"/>" + organId,
            success: function(data) {
                // Procediments
                var procediments = data;
                var selProcediments = $("#procedimentId");
                selProcediments.empty();
                if (procediments && procediments.length > 0) {
                    selProcediments.append("<option value=\"\"><spring:message code='notificacio.form.camp.procediment.select'/></option>");
                    var procedimentsComuns = [];
                    var procedimentsOrgan = [];
                    $.each(data, function(i, val) {
                        if(val.comu) {
                            procedimentsComuns.push(val);
                        } else {
                            procedimentsOrgan.push(val);
                        }
                    });

                    // console.debug(procedimentsComuns);
                    // console.debug(procedimentsOrgan);
                    if (procedimentsComuns.length > 0) {
                        selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.comuns'/>'>");
                        $.each(procedimentsComuns, function(index, val) {
                            selProcediments.append("<option value=\"" + val.id + "\">" + val.valor + "</option>");
                        });
                        selProcediments.append("</optgroup>");
                    }
                    if (procedimentsOrgan.length > 0) {
                        selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.organs'/>'>");
                        $.each(procedimentsOrgan, function(index, val) {
                            selProcediments.append("<option value=\"" + val.id + "\">" + val.valor + "</option>");
                        });
                        selProcediments.append("</optgroup>");
                    }
                } else {
                    selProcediments.append("<option value=\"\"><spring:message code='notificacio.form.camp.procediment.buit'/></option>");
                }
                selProcediments.val(${notificacioFiltreCommand.procedimentId})
                formatSelects($('#procedimentId'));
            },
            error: function() {
                console.error("error obtenint els procediments de l'òrgan gestor...");
            }
        });
    }

    function omplirServeis() {
        var organGestor = $("#organGestor");
        let organId = $(organGestor).val() == undefined ? "" : $(organGestor).val();
        $.ajax({
            type: 'GET',
            url: "<c:url value="/notificacio/serveisOrgan/"/>" + organId,
            success: function(data) {
                // Procediments
                var serveis = data;
                var selServeis = $("#serveiId");
                selServeis.empty();
                if (serveis && serveis.length > 0) {
                    selServeis.append("<option value=\"\"><spring:message code='notificacio.form.camp.servei.select'/></option>");
                    var serveisComuns = [];
                    var serveisOrgan = [];
                    $.each(data, function(i, val) {
                        if(val.comu) {
                            serveisComuns.push(val);
                        } else {
                            serveisOrgan.push(val);
                        }
                    });

                    // console.debug(serveisComuns);
                    // console.debug(serveisOrgan);
                    if (serveisComuns.length > 0) {
                        selServeis.append("<optgroup label='<spring:message code='notificacio.form.camp.servei.comuns'/>'>");
                        $.each(serveisComuns, function(index, val) {
                            selServeis.append("<option value=\"" + val.id + "\">" + val.valor + "</option>");
                        });
                        selServeis.append("</optgroup>");
                    }
                    if (serveisOrgan.length > 0) {
                        selServeis.append("<optgroup label='<spring:message code='notificacio.form.camp.servei.organs'/>'>");
                        $.each(serveisOrgan, function(index, val) {
                            selServeis.append("<option value=\"" + val.id + "\">" + val.valor + "</option>");
                        });
                        selServeis.append("</optgroup>");
                    }
                } else {
                    selServeis.append("<option value=\"\"><spring:message code='notificacio.form.camp.servei.buit'/></option>");
                }
                selServeis.val(${notificacioFiltreCommand.serveiId})
                formatSelects($('#serveiId'));
            },
            error: function() {
                console.error("error obtenint els serveis de l'òrgan gestor...");
            }
        });
    }
</script>
<form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="notificacioFiltreCommand">
    <div class="row">
        <c:if test="${mostraEntitat}">
            <div class="col-md-3">
                <not:inputSelect name="entitatId" optionItems="${entitat}"
                                 optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true"
                                 placeholderKey="notificacio.list.filtre.camp.entitat" inline="true"/>
            </div>
        </c:if>
        <div class="col-md-2">
            <not:inputSelect id="enviamentTipus" name="enviamentTipus" optionItems="${notificacioEnviamentTipus}" optionValueAttribute="value"
                             optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.enviament.tipus" inline="true"/>
        </div>
            <%--div class="col-md-2">
                <not:inputSelect name="comunicacioTipus" optionItems="${notificacioComunicacioTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.comunicacio.tipus" inline="true"/>
            </div--%>
        <div class="col-md-4">
            <not:inputText name="concepte" inline="true"  placeholderKey="notificacio.list.filtre.camp.concepte"/>
        </div>
<%--        <div class="col-md-2">--%>
<%--            <not:inputSelect id="estat" name="estat" optionItems="${notificacioEstats}" optionValueAttribute="value"--%>
<%--             optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"/>--%>
<%--        </div>--%>
        <div class="col-md-2">
            <not:inputSelect id="estat" name="estat" optionMinimumResultsForSearch="0"
                 optionTextKeyAttribute="text"
                 emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"
                 templateResultFunction="showEstat" />
        </div>
        <div class="col-md-2">
            <not:inputDate name="dataInici" placeholderKey="notificacio.list.filtre.camp.datainici" inline="true" required="false" />
        </div>
        <div class="col-md-2">
            <not:inputDate name="dataFi" placeholderKey="notificacio.list.filtre.camp.datafi" inline="true" required="false" />
        </div>
    </div>
    <div class="row">

        <div class="col-md-2">
            <not:inputDate name="dataCaducitatInici" placeholderKey="notificacio.list.filtre.camp.data.caducitat.inici" inline="true" required="false" />
        </div>
        <div class="col-md-2">
            <not:inputDate name="dataCaducitatFi" placeholderKey="notificacio.list.filtre.camp.data.caducitat.fi" inline="true" required="false" />
        </div>
        <div class="col-md-2">
            <not:inputText name="titular" inline="true" placeholderKey="notificacio.list.filtre.camp.titular"/>
        </div>
        <div class="col-md-2">
            <not:inputText name="numExpedient" inline="true" placeholderKey="notificacio.list.filtre.camp.numexpedient"/>
        </div>
        <div class="col-md-2">
            <not:inputText name="identificador" inline="true" placeholderKey="notificacio.list.filtre.camp.identificador"/>
        </div>
        <div class="col-md-6">
            <not:inputSelect id="organGestor" name="organGestor" placeholderKey="notificacio.form.camp.organEmisor"
                             inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-6">
            <not:inputSelect id="procedimentId" name="procedimentId" optionValueAttribute="id" optionTextAttribute="descripcio"
                             placeholderKey="notificacio.list.filtre.camp.procediment"
                             inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
        </div>
        <div class="col-md-6">
            <not:inputSelect id="serveiId" name="serveiId" optionValueAttribute="id" optionTextAttribute="descripcio"
                             placeholderKey="notificacio.list.filtre.camp.servei"
                             inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2">
            <not:inputSelect id="tipusUsuari" name="tipusUsuari"  optionValueAttribute="value" optionTextKeyAttribute="text"  emptyOption="true"  placeholderKey="notificacio.list.filtre.camp.tipususuari" inline="true" />
        </div>
        <div class="col-md-4">
            <c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
            <c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
            <not:inputSuggest
                    name="creadaPer"
                    urlConsultaInicial="${urlConsultaInicial}"
                    urlConsultaLlistat="${urlConsultaLlistat}"
                    textKey="notificacio.list.filtre.camp.numexpedient"
                    placeholderKey="notificacio.list.filtre.camp.creadaper"
                    suggestValue="codi"
                    suggestText="nom"
                    inline="true"/>
        </div>
        <div class="col-md-2">
            <not:inputText name="referencia" inline="true" placeholderKey="notificacio.list.filtre.camp.referencia"/>
        </div>
        <div class="col-md-2">
            <not:inputText name="registreNum" inline="true" placeholderKey="notificacio.list.filtre.camp.registre.num"/>
        </div>
        <div class="col-md-2 pull-right form-buttons"  style="text-align: right;">
            <button id="nomesAmbEntregaPostalBtn" title="<spring:message code="notificacio.list.filtre.camp.nomesAmbEntregaPostal"/>" class="btn btn-default pull-left <c:if test="${nomesAmbEntregaPostal}">active</c:if>" data-toggle="button"><span class="fa fa-envelope"></span></button>
            <not:inputHidden name="nomesAmbEntregaPostal"/>
            <button id="nomesAmbErrorsBtn" title="<spring:message code="notificacio.list.filtre.camp.nomesAmbErrors"/>" class="btn btn-default pull-left <c:if test="${nomesAmbErrors}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
            <not:inputHidden name="nomesAmbErrors"/>
            <c:if test="${isRolActualAdministradorEntitat}">
                <button id="nomesFiReintentsBtn" title="<spring:message code="notificacio.list.filtre.camp.fiReintents"/>" class="btn btn-default pull-left <c:if test="${nomesFiReintents}">active</c:if>" data-toggle="button"><span class="fa fa-window-close"></span></button>
                <not:inputHidden name="nomesFiReintents"/>
            </c:if>
            <button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default" style="padding: 6px 9px;" title="<spring:message code="comu.boto.netejar"/>"><span class="fa fa-eraser icona_ocultable" style="padding: 2px 0px;"></span><span class="text_ocultable"><spring:message code="comu.boto.netejar"/></span></button>
            <button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" title="<spring:message code="comu.boto.filtrar"/>"><span class="fa fa-filter" id="botoFiltrar"></span><span class="text_ocultable"><spring:message code="comu.boto.filtrar"/></span></button>
        </div>
    </div>
</form:form>

<script id="botonsTemplate" type="text/x-jsrender">

    </div>
    <div class="text-right">
        <div class="btn-group">
            <a href="<c:url value="/notificacio/visualitzar"/>" data-toggle="modal" data-refresh-pagina="true" class="btn btn-default"><span class="fa fa-eye-slash"></span> <spring:message code="enviament.list.show"/></a>
            <button id="btn-desplegar-envs" class="btn btn-default" style="display:none"><spring:message code="notificacio.list.boto.desplegar"/> <span class="fa fa-caret-down"></span></button>
            <button id="seleccioAll" title="<spring:message code="enviament.list.user.seleccio.tots" />" class="btn btn-default" ><span class="fa fa-check-square-o"></span></button>
            <button id="seleccioNone" title="<spring:message code="enviament.list.user.seleccio.cap" />" class="btn btn-default" ><span class="fa fa-square-o"></span></button>
            <div class="btn-group">
                <button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="badge seleccioCount">${fn:length(seleccio)}</span> <spring:message code="enviament.list.user.accions.massives"/> <span class="caret"></span>
                </button>
                <ul class="dropdown-menu dropdown-left">
                    <li><a id="processarMassiu" href="<c:url value="/notificacio/processar/massiu"/>" data-toggle="modal" data-refresh-pagina="true" title='<spring:message code="notificacio.list.accio.massiva.processar.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.processar"/></a></li>
                    <li><a id="updateEstat" style="cursor: pointer;" title='<spring:message code="notificacio.list.accio.massiva.actualitzar.estat.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.actualitzar.estat"/></a></li>
                    <li><a id="reintentarErrors" style="cursor: pointer;" title='<spring:message code="notificacio.list.accio.massiva.reintentar.errors.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reintentar.errors"/></a></li>
                    <li><a id="eliminar" style="cursor: pointer;" title='<spring:message code="notificacio.list.accio.massiva.eliminar.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.eliminar"/></a></li>
                    <li><a id="exportarODS" style="cursor: pointer;" title='<spring:message code="notificacio.list.accio.massiva.exportar.tooltip"/>' ><spring:message code="notificacio.list.accio.massiva.exportar"/></a></li>
                    <li><a id="descarregarJustificantMassiu" style="cursor: pointer;"><spring:message code="notificacio.list.accio.massiva.descarregar.justificant"/></a></li>
                    <li><a id="descarregarCertificacioMassiu" style="cursor: pointer;"><spring:message code="notificacio.list.accio.massiva.descarregar.certificacio"/></a></li>
                    <li><a id="ampliarPlazoOE" href="<c:url value="/notificacio/ampliacion/plazo/massiu"/>" data-toggle="modal" style="cursor: pointer;"><spring:message code="notificacio.list.accio.massiva.ampliar.plazo.oe"/></a></li>

                    <c:if test="${isRolActualAdministradorEntitat}">
                        <hr/>
                        <li><a style="cursor: pointer;" id="reactivarConsulta" title='<spring:message code="notificacio.list.accio.massiva.reactivar.consultes.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.consultes.notifica"/></a></li>
                <%--        <li><a style="cursor: pointer;" id="reactivarSir" title='<spring:message code="notificacio.list.accio.massiva.reactivar.consultes.sir.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.consultes.sir"/></a></li>--%>
                        <li><a style="cursor: pointer;" id="reactivarCallback" title='<spring:message code="notificacio.list.accio.massiva.reactivar.callbacks.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.callbacks"/></a></li>
                <%--        <li><a style="cursor: pointer;" id="enviarCallback" title='<spring:message code="notificacio.list.accio.massiva.enviar.callbacks.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.enviar.callbacks"/></a></li>--%>
                        <li><a style="cursor: pointer;" id="enviarNotificacionsMovil" title='<spring:message code="notificacio.list.accio.massiva.enviar.notificacions.movil.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.enviar.notificacions.movil"/></a></li>
                <%--        <li><a style="cursor: pointer;" id="reactivarRegistre" title='<spring:message code="notificacio.list.accio.massiva.reactivar.registre.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.registre"/></a></li>--%>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
</script>

<script id="rowhrefTemplate" type="text/x-jsrender"><c:url value="/notificacio/{{:id}}/info"/></script>
<div id="cover-spin"></div>
<table
        id="notificacio"
        data-toggle="datatable"
        data-url="${urlDatatable}"
        data-search-enabled="false"
        data-default-order="0"
        data-default-dir="desc"
        class="table table-striped table-bordered"
        style="width:100%"
        data-row-info="true"
<%--        data-filter="#form-filtre"--%>
        data-save-state="true"
        data-mantenir-paginacio="false"
        data-paging-style-x="true"
        data-rowhref-template="#rowhrefTemplate"
        data-botons-template="#botonsTemplate"
        data-selection-enabled="true"
        data-rowhref-toggle="modal"
>
    <thead>
    <tr>
        <th data-col-name="id" data-visible="false">#</th>
        <th data-col-name="tipusUsuari" data-visible="false">#</th>
        <%--        <th data-col-name="errorLastCallback" data-visible="false">#</th>--%>
        <%--        <th data-col-name="hasEnviamentsPendentsRegistre" data-visible="false">#</th>--%>
        <th data-col-name="notificaError" data-visible="false"></th>
        <th data-col-name="notificaErrorDescripcio" data-visible="false"></th>
        <th data-col-name="enviant" data-visible="false"></th>
        <th data-col-name="justificant" data-visible="false"></th>
        <th data-col-name="enviamentTipus" data-template="#cellEnviamentTipusTemplate" class="enviamentTipusCol" width="5px">

            <script id="cellEnviamentTipusTemplate" type="text/x-jsrender">
                {{if enviamentTipus == 'NOTIFICACIO'}}
                    <div class="not-icon-o" title="<spring:message code="es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto.NOTIFICACIO"/>">N</div>
                {{else enviamentTipus == 'COMUNICACIO'}}
                    <div class="com-icon-o" title="<spring:message code="es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto.COMUNICACIO"/>">C</div>
                {{else}}
                    <div class="com-icon-o" title="<spring:message code="es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto.SIR"/>">S</div>
                {{/if}}
            </script>
        </th>
        <c:if test = "${columnes.dataCreacio == true}">
            <th data-col-name="createdDate" data-converter="datetime"   width="${ampladaEnviament}"><spring:message code="notificacio.list.columna.enviament.creadael"/></th>
        </c:if>
        <c:if test = "${columnes.dataEnviament == true}">
            <th data-col-name="enviadaDate" data-converter="datetime" width="${ampladaEnviament}"><spring:message code="notificacio.list.columna.enviament.data"/></th>
        </c:if>
        <c:if test = "${columnes.numRegistre == true}">
            <th data-col-name="registreNums"><spring:message code="notificacio.list.columna.num.registre"/></th>
        </c:if>
        <c:if test="${mostraEntitat}">
            <th data-col-name="entitatNom" width="170px"><spring:message code="notificacio.list.columna.entitat"/></th>
        </c:if>
        <th data-col-name="organEstat" data-visible="false"></th>
        <c:if test = "${columnes.organEmisor == true}">
            <th data-col-name="organGestorDesc" data-template="#cellOrganGestorTemplate" width="200px"><spring:message code="notificacio.form.camp.organEmisor"/>
                <script id="cellOrganGestorTemplate" type="text/x-jsrender">
                    {{:organGestorDesc}}
                    {{if organEstat != 'V'}}
                        <span class="fa fa-warning text-danger" title="<spring:message code='notificacio.list.columna.organGestor.obsolet'/>"></span>{{/if}}
                </script>
            </th>
        </c:if>
        <th data-col-name="procedimentTipus" data-visible="false"></th>
        <c:if test = "${columnes.procSerCodi == true}">
            <th data-col-name="procedimentDesc" data-template="#cellProcedimentTemplate" width="200px"><spring:message code="notificacio.list.columna.procediment"/>
                <script id="cellProcedimentTemplate" type="text/x-jsrender">
                    {{if procedimentTipus == 'PROCEDIMENT'}}<span class="label label-primary">P</span>{{/if}}
                    {{if procedimentTipus == 'SERVEI'}}<span class="label label-warning">S</span>{{/if}}
                    {{:procedimentDesc}}
                </script>
            </th>
        </c:if>
        <c:if test="${mostrarColumnaNumExpedient}">
            <c:if test = "${columnes.numExpedient == true}">
                <th data-col-name="numExpedient" width="170px"><spring:message code="notificacio.list.columna.num.expedient"/></th>
            </c:if>
        </c:if>
        <c:if test = "${columnes.concepte == true}">
            <th data-col-name="concepte" width="${ampladaConcepte}" ><spring:message code="notificacio.list.columna.concepte"/></th>
        </c:if>
        <c:if test = "${columnes.creadaPer == true}">
            <th data-col-name="createdByComplet" data-converter="String" width="150px"><spring:message code="notificacio.list.columna.enviament.creada"/></th>
        </c:if>
        <c:if test = "${columnes.interessats == true}">
            <th data-col-name="titular"><spring:message code="notificacio.list.columna.interessats"/></th>
        </c:if>
        <th data-col-name="estatDate" data-converter="datetime" data-visible="false"></th>
        <th data-col-name="estatProcessatDate" data-converter="datetime" data-visible="false"></th>
        <th data-col-name="estat" data-visible="false"></th>
        <c:if test = "${columnes.estat == true}">
            <th data-col-name="estatString" data-template="#cellEstatTemplate" <c:if test="${isRolActualAdministradorEntitat}"> data-disable-events="true" </c:if>width="120px"><spring:message code="notificacio.list.columna.estat"/>
                <script id="cellEstatTemplate" type="text/x-jsrender">
                    <div class="cellEstat">
                        {{:estatString}}
                        {^{if ~hlpIsAdministradorEntitat() }}
                            <div class="hover-button"><a href="<c:url value="/notificacio/{{:id}}/updateEstatList"/>"><span class="fa fa-refresh"></span></a></div>
                        {{/if}}
                    </div>
                </script>
            </th>
        </c:if>
        <th data-col-name="permisProcessar" data-visible="false">
        <th data-col-name="documentId" data-visible="false" style="visibility: hidden">
            <%--        <th data-col-name="enviamentId" data-visible="false" style="visibility: hidden">--%>
        <th data-col-name="envCerData" data-visible="false" style="visibility: hidden">
        <th data-col-name="plazoAmpliable" data-visible="false">
        <th data-col-name="id" data-orderable="false" data-disable-events="true" data-template="#cellAccionsTemplate" width="60px" style="z-index:99999;">
            <script id="cellAccionsTemplate" type="text/x-jsrender">
                <div class="dropdown">
                    <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><a href="<c:url value="/notificacio/{{:id}}/info"/>" data-toggle="modal" data-height="700px" data-processar="true"><span class="fa fa-info-circle"></span>&nbsp; <spring:message code="comu.boto.detalls"/></a></li>
                        <li><a href="<c:url value="/notificacio/{{:id}}/documentDescarregar/{{:documentId}}"/>" target="_blank" rel=”noopener noreferrer”><span class="fa fa-download"></span>&nbsp; <spring:message code="notificacio.info.document.descarregar"/></a></li>
                        {^{if envCerData != null }}
                            <li><a href="<c:url value="/notificacio/{{:id}}/enviament/certificacionsDescarregar"/>" download><span class="fa fa-download"></span>&nbsp; <spring:message code="enviament.info.notifica.certificacio.num.descarregar"/></a></li>
                        {{/if}}
                    {^{if (~hlpIsAdministradorEntitat() && estat == 'FINALITZADA') || permisProcessar }}
                        <li><a href="<c:url value="/notificacio/{{:id}}/processar"/>" data-toggle="modal"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a></li>
                    {{/if}}
                    {{if justificant}}
                        <li><a href="<c:url value="/notificacio/{{:id}}/justificant"/>" data-toggle="modal" data-height="700px" data-processar="true"><span class="fa fa-download"></span>&nbsp; <spring:message code="comu.boto.justificant"/></a></li>
                    {{/if}}
                    {^{if (~hlpIsUsuari() || ~hlpIsAdministradorEntitat() || ~hlpIsAdministradorOrgan())  && (enviant || estat == 'PENDENT' || estat == 'REGISTRADA')}}
                        <li><a href="<c:url value="/notificacio/{{:id}}/edit"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.editar"/></a></li>
                        <li><a href="<c:url value="/notificacio/{{:id}}/delete"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
                    {{/if}}
                    {{if plazoAmpliable}}
                        <li><a href="<c:url value="/notificacio/{{:id}}/ampliacion/plazo"/>" data-toggle="modal"><span class="fa fa-calendar-o"></span>&nbsp;<spring:message code="notificacio.list.accio.massiva.ampliar.plazo.oe"/></a></li>
                    {{/if}}
                    </ul>
                </div>
            </script>
        </th>
    </tr>
    </thead>
</table>
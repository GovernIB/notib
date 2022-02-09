<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
    <head>
        <title><spring:message code="organgestor.list.titol"/></title>
        <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
        <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
        <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
        <link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
        <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
        <script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
        <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
        <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
        <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
        <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
        <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
        <script src="<c:url value="/js/webutil.common.js"/>"></script>
        <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
        <script src="<c:url value="/js/webutil.modal.js"/>"></script>
        <link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
        <script src="<c:url value="/js/jstree.min.js"/>"></script>

        <script type="text/javascript">

            let codi;

            $.views.helpers({
                hlpIsAdminOrgan: isAdminOrgan
            });

            let organGestor = null;
            function isAdminOrgan() {
                return ${isRolActualAdministradorOrgan};
            }
            function changedCallback(e, data) {
                $('#panellInfo').css("visibility", "");
                $('#panellInfo').css("display", "none");
                $(".datatable-dades-carregant").css("display", "block");
                let text = data.node.text.split('(');
                text = text[text.length-1].split(')')[0];
                codi = text;
                $("#esborrar").attr('href', "<c:url value="/organgestor/"/>" + text + "/delete");
                $("#procediments").attr('href', "<c:url value="/procediment/organ/"/>" + text);
                $("#serveis").attr('href', "<c:url value="/servei/organ/"/>" + text);
                $("#actualitzar").attr('href', "<c:url value="/organgestor/"/>" + text + "/update");

                let permisUrl = "<c:url value="/organgestor/"/>" + text + "/permisos";
                $('#permisos').webutilDatatable("refresh-url", permisUrl);

                $.ajax({
                    url: "/notib/organgestorArbre/organgestor/" + text,
                    success: organ => {
                        if (!organ) {
                            return;
                        }

                        $('#permis-boto-nou').attr("href", "<c:url value="/organgestor/"/>" + organ.id + "/permis/new");
                        $("#id", $("#panellInfo")).val(organ.id);
                        $("#nom", $("#panellInfo")).val(organ.nom);
                        $("#codi", $("#panellInfo")).val(organ.codi);
                        $("#oficinaNom", $("#panellInfo")).val(organ.oficinaNom);
                        $("#estat", $("#panellInfo")).val(organ.estat);
                        $("#entregaCieActiva", $("#panellInfo")).prop("checked", organ.entregaCieActiva);
                        if (organ.entregaCieActiva) {
                            $('#entrega-cie-form').show();
                        }
                        console.log(organ);
                        $("#permisos").off("draw.dt");
                        $("#permisos").on( "draw.dt", function () {
                            $.each($(".boto-permis"), function( key, permisionLink ) {
                                let link = $(permisionLink).attr("href");
                                let replaced = link.replace("idOrgan", organ.id);
                                $(permisionLink).attr("href", replaced);
                            });
                        });
                    },
                    error: () => console.log("error"),
                    complete: () => {
                        $("#panellInfo").css("display", "block");
                        $(".datatable-dades-carregant").css("display", "none");
                    }
                });
            }

            function checkSelectedNodes() {
                // Se declara esta función para evitar error JS en el arbre.tag durante la llamada a la misma
            }
            function paintSelectedNodes() {
                // Se declara esta función para evitar error JS en el arbre.tag durante la llamada a la misma
            }

            $(document).ready(function() {

                $('#btnNetejar').click(function() {
                    $(':input', $('#filtre')).each (function() {
                        var type = this.type, tag = this.tagName.toLowerCase();
                        if (type == 'text' || type == 'password' || tag == 'textarea') {
                            this.value = '';
                        } else if (type == 'checkbox' || type == 'radio') {
                            this.checked = false;
                        } else if (tag == 'select') {
                            this.selectedIndex = 0;
                        }
                    });
                    $('#btn-entregaCieActiva').removeClass('active');
                    $('#entregaCieActiva').val(false);
                    $('#form-filtre').submit();
                });
                $('#btn-entregaCieActiva').click(function() {
                    let entregaCieActiva = !$(this).hasClass('active');
                    $('#entregaCieActiva').val(entregaCieActiva);
                })
                $(".panel-heading:first").css({"display": "flex", "justify-content": "space-between"})
                $(".panel-heading:first").append("<div><button id='canviarVistaOrganGestor' class='btn btn-primary'><spring:message code='boto.canviar.vista'/></button></div>");
                $("#canviarVistaOrganGestor").click(function(){
                    window.location.replace("/notib/organgestor");
                });

                $("#esborrar").click(e => {
                    e.preventDefault();
                    if (confirm('< spring:message code="organgestor.list.confirmacio.esborrar"/>')) {
                        $.ajax({url: "<c:url value="/organgestorArbre/"/>" + codi + "/delete",
                            success: resposta => {
                                if (resposta && !resposta.error) {
                                    $('#contingut-missatges').append(' <div class="alert alert-success"> <button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + resposta.msg + '</div>');
                                    return;
                                }
                                if (resposta && resposta.error) {
                                    $('#contingut-missatges').append(' <div class="alert alert-danger"> <button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + resposta.msg + '</div>');
                                }
                            },
                            error: err => {
                                console.error(err);
                                $('#contingut-missatges').append(' <div class="alert alert-error"> <button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + "<spring:message code="organgestor.controller.esborrat.ko"/>" + '</div>');
                            }}
                        )
                    }
                });

                $('#entregaCieActiva').change(function() {
                    if (this.checked) {
                        $('#entrega-cie-form').show();
                        return;
                    }
                    $('#entrega-cie-form').hide();
                });

                if (!$('#entregaCieActiva')[0].checked) {
                    $('#entrega-cie-form').hide();
                }
            });
        </script>
    </head>
    <body>
        <c:set var="formActionFiltre"><not:modalUrl value="/organgestorArbre"/></c:set>
        <form:form id="filtre" action="${formActionFiltre}" method="post" cssClass="well" commandName="organGestorFiltreCommand">
            <div class="row">
                <div class="col-md-2">
                    <not:inputText name="codi" inline="true" placeholderKey="organgestor.list.columna.codi"/>
                </div>
                <div class="col-md-3">
                    <not:inputText name="nom" inline="true" placeholderKey="organgestor.list.columna.nom"/>
                </div>
                <div class="col-md-2">
                    <not:inputSelect name="estat" optionItems="${organGestorEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="organgestor.list.columna.estat" inline="true"/>
                </div>
                <c:if test="${setOficina}">
                    <div class="col-md-3">
                        <not:inputSelect name="oficina" textKey="organgestor.list.columna.oficina" required="true" optionItems="${oficines}" optionValueAttribute="codi"
                                         optionTextAttribute="nom" labelSize="0" inline="true" emptyOption="true" optionMinimumResultsForSearch="2" placeholderKey="organgestor.form.camp.oficina.select"/>
                    </div>
                </c:if>
                <div class="col-md-2 pull-right">
                    <div class="pull-right">
                        <button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
                        <button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2">
                    <button id="btn-entregaCie" title="" class="btn btn-default <c:if test="${organGestorFiltreCommand.entregaCie}">active</c:if>" data-toggle="button">
                        <span class="fa fa-envelope"></span> <spring:message code="organgestor.list.columna.cie"/>
                    </button>
                    <not:inputHidden name="entregaCie"/>
                </div>
            </div>
        </form:form>
        <div class="row">
            <!------------------------- TREE ------------------------>
            <div class="col-md-5">
                <c:set var="fullesAtributInfoText"><spring:message code="organgestor.arbre.defecte"/></c:set>
                <c:set var="fillsAtributInfoText"><span style="padding-top: 4.5px; padding-left: 2px;" class="fa fa-warning text-danger pull-right" title="<spring:message code="organgestor.list.columna.organGestor.obsolet"/>"></span></c:set>


                <div style="padding-bottom: 10px;">
                    <button class="btn btn-default" onclick="$('#arbreOrgans').jstree('open_all');"><span class="fa fa-caret-square-o-down"></span> <spring:message code="organgestor.arbre.expandeix"/></button>
                    <button class="btn btn-default" onclick="$('#arbreOrgans').jstree('close_all');"><span class="fa fa-caret-square-o-up"></span> <spring:message code="organgestor.arbre.contrau"/></button>
                </div>

                <not:arbre id="arbreOrgans" atributId="codi" atributNom="nomCodi" arbre="${arbreOrgans}" fulles="${organs}" fullesAtributId="id" fullesAtributNom="nomCodi"
                           fullesAtributPare="codi"  fullesIcona="fa fa-inbox fa-lg" changedCallback="changedCallback" isArbreSeleccionable="${true}"
                           isFullesSeleccionable="${true}" isOcultarCounts="${true}" fullesAtributCssClassCondition="actiu"/>
<%--                <not:arbre id="arbreOrgans" atributId="codi" atributNom="nom" arbre="${arbreOrgans}" fulles="${organs}" fullesAtributId="id" fullesAtributNom="nom"--%>
<%--                           fullesAtributPare="unitatCodi" fullesAtributInfo="perDefecte" fullesAtributInfoText="${fullesAtributInfoText}"  fullesIcona="fa fa-inbox fa-lg"--%>
<%--                           changedCallback="changedCallback" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" fullesAtributCssClassCondition="inactiva"--%>
<%--                           fillsAtributInfoCondition="obsoleta" fillsAtributInfoText="${fillsAtributInfoText}"--%>
<%--                />--%>

            </div>
            <div class="col-md-7" id="panellInfo"<c:if test="${empty codi}"> style="visibility:hidden"</c:if>>
                <div class="panel panel-default">
                    <div class="panel-heading">

                        <h2 id="titolDetall">
                            <c:choose>
                                <c:when test="${!isModificacio}">
                                    <spring:message code="organgestor.form.titol.crear"/>
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="organgestor.form.titol.modificar"/><small>${organGestorCommand.codi}</small>
                                </c:otherwise>
                            </c:choose>
                        </h2>
                    </div>
                    <div class="panel-body">
                        <div class="panel panel-danger" id="panelOrganObsolet" style="display: none;">
                            <div class="panel-heading">
                                <span class="fa fa-warning text-danger"></span>
                                <spring:message code="organgestor.list.columna.organGestor.obsolet"/>
                            </div>
                            <div class="panel-body">

                            </div>
                        </div>

                        <c:set var="formAction"><not:modalUrl value="/organgestorArbre/guardar"/></c:set>
                        <form:form action="${formAction}" method="post" commandName="organGestorCommand" role="form">
                            <div class="flex-column">
                                <not:inputHidden name="id"/>
                                <not:inputHidden name="entitatId"/>
                                <not:inputText generalClass="row" name="codi" disabled="false" textKey="organgestor.list.columna.codi" required="true"/>
                                <not:inputText generalClass="row" name="nom" disabled="false" textKey="organgestor.list.columna.nom" required="true"/>

                                <not:inputText generalClass="row" name="estat" disabled="false" textKey="organgestor.list.columna.estat" required="true"/>

                                <c:if test="${!isModificacio}">
                                    <c:if test="${setLlibre}">
                                        <br/>
                                        <form:hidden path="llibre"/>
                                        <form:hidden path="llibreNom"/>
<%--                                        <div class="form-group">--%>
<%--                                            <label class="control-label col-xs-2" for="selLlibres"><spring:message code="organgestor.form.camp.llibre"/>:</label>--%>
<%--                                            <div class="controls col-xs-10">--%>

<%--                                                <not:inputSelect generalClass="row" name="llibre" optionItems="${llibres}" optionValueAttribute="codi" labelSize="2"--%>
<%--                                                                 optionTextAttribute="nomCurt" required="true" emptyOption="true"--%>
<%--                                                                 textKey="" placeholderKey="organgestor.form.camp.llibre" optionMinimumResultsForSearch="0"/>--%>
<%--                                                <p class="comentari"><spring:message code="organgestor.form.camp.llibre.info"/></p>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                        <div>--%>
                                        <not:inputSelect generalClass="row" name="llibre" optionItems="${llibres}" optionValueAttribute="codi"
                                                 optionTextAttribute="nomCurt" emptyOption="true"
                                                 textKey="organgestor.form.camp.llibre" placeholderKey="organgestor.form.camp.llibre.info" optionMinimumResultsForSearch="0"/>
<%--                                        <p class="comentari"><spring:message code="organgestor.form.camp.llibre.info"/></p>--%>
<%--                                        </div>--%>
                                    </c:if>
                                </c:if>
                                <c:if test="${isModificacio}">
                                    <ul class="list-group">
                                        <li class="list-group-item"><b><spring:message code="organgestor.form.camp.codiDir3"/>:</b> ${organGestorCommand.codi}</li>
                                        <li class="list-group-item"><b><spring:message code="organgestor.form.camp.organisme"/>:</b> ${organGestorCommand.nom}</li>
                                        <li class="list-group-item"><b><spring:message code="organgestor.form.camp.llibre"/>:</b> ${organGestorCommand.llibreNom}</li>
                                    </ul>
                                </c:if>
                                <c:if test="${!setOficina}">
                                    <not:inputText generalClass="row" name="oficinaNom" disabled="true" textKey="organgestor.list.columna.oficina" required="true"/>
                                </c:if>
                                <c:if test="${setOficina}">
                                    <br/>
                                    <form:hidden path="oficina"/>
                                    <form:hidden path="oficinaNom"/>
<%--                                    <div class="form-group">--%>
<%--                                        <label class="control-label col-xs-2" for="selOficines"><spring:message code="organgestor.form.camp.oficina"/>:</label>--%>
<%--                                        <div class="controls col-xs-10">--%>
<%--                                            <select id="selOficines" data-placeholder="<spring:message code="organgestor.form.camp.oficina"/>"></select>--%>
<%--                                            <p class="comentari oficinainfo hidden" style="color: #856404;"><spring:message code="organgestor.form.camp.oficina.info"/></p>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>

                                    <not:inputSelect generalClass="row" name="oficina" optionItems="${oficines}" optionValueAttribute="codi"
                                                     optionTextAttribute="nom" required="true" emptyOption="true"
                                                     textKey="organgestor.form.camp.oficina" placeholderKey="organgestor.form.camp.oficina" optionMinimumResultsForSearch="0"/>
                                </c:if>

                                <not:inputCheckbox name="entregaCieActiva" generalClass="row" textKey="organgestor.form.camp.entregacie"/>
                                <div id="entrega-cie-form" class="flex-column">
                                    <not:inputSelect generalClass="row" name="operadorPostalId" optionItems="${operadorPostalList}" optionValueAttribute="id"
                                                     optionTextAttribute="text" required="true" emptyOption="true"
                                                     textKey="entitat.form.camp.operadorpostal" placeholderKey="entitat.form.camp.operadorpostal" optionMinimumResultsForSearch="0"/>
                                    <not:inputSelect generalClass="row" name="cieId" optionItems="${cieList}" optionValueAttribute="id"
                                                     optionTextAttribute="text" required="true" emptyOption="true"
                                                     textKey="entitat.form.camp.cie" placeholderKey="entitat.form.camp.cie" optionMinimumResultsForSearch="0"/>
                                </div>
                            </div>
                            <div class="flex-space-around">
                                <div>
                                    <a id="esborrar" class="btn btn-default"  href="" data-adjust-height="false" data-height="650px">
                                        <span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/>
                                    </a>
                                </div>
                                <div>
                                    <a id="procediments" class="btn btn-default"  href="" data-toggle="modal" data-adjust-height="false" data-height="650px">
                                        <span class="fa fa-briefcase"></span>&nbsp;&nbsp;<spring:message code="decorator.menu.procediment"/>
                                    </a>
                                </div>
                                <div>
                                    <a id="serveis" class="btn btn-default" href="" data-toggle="modal" data-adjust-height="false" data-height="650px">
                                        <span class="fa fa-briefcase"></span>&nbsp;&nbsp;<spring:message code="decorator.menu.servei"/>
                                    </a>
                                </div>
                                <div>
                                    <a id="actualitzar" class="btn btn-default" href="" data-toggle="ajax" data-adjust-height="false" data-height="650px">
                                        <span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="organgestor.list.boto.actualitzar"/>
                                    </a>
                                </div>
                                <button id="guardar" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
                            </div>
                        </form:form>
                    </div>
                </div>
                <div class="panel panel-default">
                    <div class="panel-heading flex-space-between">
                        <h2><spring:message code="organgestor.permis.titol"/><small></small></h2>
                        <div class="flex-column-center">
                            <a id="permis-boto-nou" class="btn btn-default" href="" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.permis.boto.nou.permis"/></a>
                        </div>
                    </div>
                    <div class="panel-body">
                        <table id="permisos" data-toggle="datatable" data-url="<c:url value="/organgestor/0/permis/datatable"/>" data-search-enabled="false"
                               data-paging-enabled="false" data-default-order="1" data-default-dir="asc" class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th data-col-name="tipus" data-renderer="enum(TipusEnumDto)" width="120px"><spring:message code="procediment.permis.columna.tipus" /></th>
                                <th data-col-name="nomSencerAmbCodi"><spring:message code="procediment.permis.columna.principal"/></th>
                                <th data-col-name="read" data-template="#cellPermisReadTemplate" width="120px">
                                    <spring:message code="procediment.permis.columna.consulta"/>
                                    <script id="cellPermisReadTemplate" type="text/x-jsrender">
                                                    {{if read}}<span class="fa fa-check"></span>{{/if}}
                                                </script>
                                </th>
                                <th data-col-name="processar" data-template="#cellPermisProcessarTemplate" width="120px">
                                    <spring:message code="procediment.permis.columna.processar"/>
                                    <script id="cellPermisProcessarTemplate" type="text/x-jsrender">
                                                    {{if processar}}<span class="fa fa-check"></span>{{/if}}
                                                </script>
                                </th>
                                <th data-col-name="notificacio" data-template="#cellPermisNotificacioTemplate" width="120px">
                                    <spring:message code="procediment.permis.columna.notificacio"/>
                                    <script id="cellPermisNotificacioTemplate" type="text/x-jsrender">
                                                    {{if notificacio}}<span class="fa fa-check"></span>{{/if}}
                                                </script>
                                </th>
                                <th data-col-name="administration" data-template="#cellPermisGestioTemplate" width="120px">
                                    <spring:message code="procediment.permis.columna.gestio"/>
                                    <script id="cellPermisGestioTemplate" type="text/x-jsrender">
                                                    {{if administration}}<span class="fa fa-check"></span>{{/if}}
                                                </script>
                                </th>
                                <th data-col-name="comuns" data-template="#cellPermisComunsTemplate" width="120px">
                                    <spring:message code="organgestor.permis.columna.comuns"/>
                                    <script id="cellPermisComunsTemplate" type="text/x-jsrender">
                                                    {{if comuns}}<span class="fa fa-check"></span>{{/if}}
                                                </script>
                                </th>
                                <th data-col-name="administrador" data-template="#cellPermisAdministradorTemplate" data-class="organ-admin" width="120px">
                                    <spring:message code="procediment.permis.columna.administrador"/>
                                    <script id="cellPermisAdministradorTemplate" type="text/x-jsrender">
                                                    {{if administrador}}<span class="fa fa-check"></span>{{/if}}
                                                </script>
                                </th>
                                <th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="100px">
                                    <script id="cellAccionsTemplate" type="text/x-jsrender">
                                        <div class="dropdown">
                                            <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
                                            <ul class="dropdown-menu">
                                                {^{if ~hlpIsAdminOrgan() && !administrador}}
                                                    <li><a class="boto-permis" href="../modal/organgestor/idOrgan/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
                                                {{else !~hlpIsAdminOrgan()}}
                                                    <li><a class="boto-permis" href="../modal/organgestor/idOrgan/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
                                                {{/if}}
                                                <li><a class="boto-permis" href="../organgestor/idOrgan/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
                                            </ul>
                                        </div>
                                    </script>
                                </th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
            <div class="col-md-7 datatable-dades-carregant" style="display: none; text-align: center; margin-top: 100px;">
                <span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
            </div>
        </div>
    </body>
</html>

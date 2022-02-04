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

            function changedCallback(e, data) {

                $('#panellInfo').css('visibility', '');
                $('#panellInfo').css('display', 'none');
                $(".datatable-dades-carregant").css("display", "block");
                let text = data.node.text.split('(')
                text = text[text.length-1].split(')')[0];
                let organCodi = data.node.codi;
                let permisUrl = "organgestor/" + text + "/permisos";
                $('#permis-boto-nou').attr('href', permisUrl + '/new');
                $('#permisos').webutilDatatable('refresh-url', permisUrl);
                $('#permisos').off('draw.dt');
                $('#permisos').on( 'draw.dt', function () {
                    $.each($('#permisos .dropdown-menu a'), function( key, permisionLink ) {
                        let link = $(permisionLink).attr('href');
                        let replaced = link.replace("bustiaIdString", organCodi); // TODO FALTA CANVIAR
                        $(permisionLink).attr('href', replaced);
                    });
                });
                $.ajax({
                    url: "/notib/organgestorArbre/organgestor/" + text,
                    success: organ => {
                        if (!organ) {
                            return;
                        }
                        $('#nom', $('#panellInfo')).val(organ.organ.nom);
                        $('#codi', $('#panellInfo')).val(organ.organ.codi);
                        $('#oficinaNom', $('#panellInfo')).val(organ.organ.oficinaEntitat);
                        $('#estat', $('#panellInfo')).val(organ.organ.estat);
                        $('#cie', $('#panellInfo')).checked = organ.organ.cie;
                    },
                    error: () => console.log("error"),
                    complete: () => {
                        $('#panellInfo').css('display', 'block');
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
            });
        </script>
    </head>
    <body>
        <form:form id="filtre" action="" method="post" cssClass="well" commandName="organGestorFiltreCommand">
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
                        <not:inputSelect name="oficina" textKey="organgestor.list.columna.oficina" required="true" optionItems="${oficinesEntitat}" optionValueAttribute="codi"
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
                    <button id="btn-entregaCieActiva" title="" class="btn btn-default <c:if test="${organGestorFiltreCommand.entregaCieActiva}">active</c:if>" data-toggle="button">
                        <span class="fa fa-envelope"></span> <spring:message code="organgestor.list.columna.cie"/>
                    </button>
                    <not:inputHidden name="entregaCieActiva"/>
                </div>
            </div>
        </form:form>
        <div class="row">
            <!------------------------- TREE ------------------------>
            <div class="col-md-5">
                <c:set var="fullesAtributInfoText"><spring:message code="organgestor.arbre.defecte"/></c:set>
<%--                <c:set var="fillsAtributInfoText"><span style="padding-top: 4.5px; padding-left: 2px;" class="fa fa-warning text-danger pull-right" title="<spring:message code="unitat.arbre.unitatObsoleta"/>"></span></c:set>--%>


                <div style="padding-bottom: 10px;">
                    <button class="btn btn-default" onclick="$('#arbreOrgans').jstree('open_all');"><span class="fa fa-caret-square-o-down"></span> <spring:message code="organgestor.arbre.expandeix"/></button>
                    <button class="btn btn-default" onclick="$('#arbreOrgans').jstree('close_all');"><span class="fa fa-caret-square-o-up"></span> <spring:message code="organgestor.arbre.contrau"/></button>
                    <a style="float: right;" id="bustia-boto-nova" class="btn btn-default" href="organgestorArbre/new" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="organgestor.list.boto.nou"/></a>
                </div>

                <not:arbre id="arbreOrgans" atributId="codi" atributNom="nomCodi" arbre="${arbreOrgans}" fulles="${organs}" fullesAtributId="id" fullesAtributNom="nomCodi"
                           fullesAtributPare="codi"  fullesIcona="fa fa-inbox fa-lg" changedCallback="changedCallback" isArbreSeleccionable="${true}"
                           isFullesSeleccionable="${true}" isOcultarCounts="${true}"/>
<%--                <not:arbre id="arbreOrgans" atributId="codi" atributNom="nom" arbre="${arbreOrgans}" fulles="${organs}" fullesAtributId="id" fullesAtributNom="nom"--%>
<%--                           fullesAtributPare="unitatCodi" fullesAtributInfo="perDefecte" fullesAtributInfoText="${fullesAtributInfoText}"  fullesIcona="fa fa-inbox fa-lg"--%>
<%--                           changedCallback="changedCallback" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" fullesAtributCssClassCondition="inactiva"--%>
<%--                           fillsAtributInfoCondition="obsoleta" fillsAtributInfoText="${fillsAtributInfoText}"--%>
<%--                />--%>

            </div>
            <div class="col-md-7" id="panellInfo"<c:if test="${empty codi}"> style="visibility:hidden"</c:if>>
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="panel panel-danger" id="panelOrganObsolet" style="display: none;">
                            <div class="panel-heading">
                                <span class="fa fa-warning text-danger"></span>
                                <spring:message code="organgestor.list.columna.organGestor.obsolet"/>
                            </div>
                            <div class="panel-body">

                            </div>
                        </div>

                        <c:set var="formAction"><dis:modalUrl value="/organGestorOrganigrama/modify"/></c:set>
                        <form:form action="${formAction}" method="post" commandName="organGestorCommand" role="form">
                            <div class="row" style="margin-bottom: 10px;">
                                <not:inputText name="codi" disabled="true" textKey="organgestor.list.columna.codi" required="false"/>
                            </div>
                            <div class="row">
                                <not:inputText name="nom" disabled="true" textKey="organgestor.list.columna.nom" required="false"/>
                            </div>
                            <div class="row">
                                <not:inputText name="oficinaNom" disabled="true" textKey="organgestor.list.columna.oficina" required="false"/>
                            </div>
                            <div class="row">
                                <not:inputText name="estat" disabled="true" textKey="organgestor.list.columna.estat" required="false"/>
                            </div>
                            <div class="row">
                                <not:inputCheckbox name="cie" disabled="true" textKey="organgestor.list.columna.cie"/>
                            </div>
                        </form:form>

                        <div class="panel panel-default" style="margin-top: 62px;">
                            <div class="panel-heading">
                                <h2><spring:message code="organgestor.permis.titol"/><small></small></h2>
                            </div>
                            <div class="panel-body">
                                <div class="text-right boto-nou-permis-organigrama" data-toggle="botons-titol">
                                    <a class="btn btn-default" id="permis-boto-nou" href="" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.permis.boto.nou.permis"/></a>
                                </div>
                                <table id="permisos" data-toggle="datatable" data-url="<c:url value="/organgestor/1/permis/datatable"/>" data-search-enabled="false"
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
                                        <%--                                        <th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="100px">--%>
                                        <%--                                            <script id="cellAccionsTemplate" type="text/x-jsrender">--%>
                                        <%--                                                <div class="dropdown">--%>
                                        <%--                                                    <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>--%>
                                        <%--                                                    <ul class="dropdown-menu">--%>
                                        <%--                                                        {^{if ~hlpIsAdminOrgan() && !administrador}}--%>
                                        <%--                                                            <li><a href="../../organgestor/${organGestor.id}/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>--%>
                                        <%--                                                        {{else !~hlpIsAdminOrgan()}}--%>
                                        <%--                                                            <li><a href="../../organgestor/${organGestor.id}/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>--%>
                                        <%--                                                        {{/if}}--%>
                                        <%--                                                        <li><a href="../../organgestor/${organGestor.id}/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>--%>
                                        <%--                                                    </ul>--%>
                                        <%--                                                </div>--%>
                                        <%--                                            </script>--%>
                                        <%--                                        </th>--%>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="panel-heading">
                        <h2><spring:message code="organgestor.form.titol.modificar"/><small>${codi}</small></h2>
                    </div>
                </div>
            </div>
            <div class="col-md-7 datatable-dades-carregant" style="display: none; text-align: center; margin-top: 100px;">
                <span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
            </div>
    </body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

    <title>
        <c:choose>
            <c:when test="${!isModificacio}">
                <spring:message code="organgestor.form.titol.crear"/>
            </c:when>
            <c:otherwise>
                <spring:message code="organgestor.form.titol.modificar"/> - ${organGestorCommand.codi}
            </c:otherwise>
        </c:choose>
    </title>
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

        $(document).on("click", "#expandAll", function() {
            $('#arbreOrgans').jstree().open_all(null, 200);
        });

        $(document).on("click", "#closeAll", function() {
            $('#arbreOrgans').jstree().close_all(null, 300);
        });


        $.views.helpers({
            hlpIsAdminOrgan: isAdminOrgan
        });

        $(document).ready(function() {

            if (!${id} || ${id} === 0) {
                $("#permisosPanel").hide();
                $("#esborrar").hide();
                $("#procediments").hide();
                $("#serveis").hide();
                $("#actualitzar").hide();
            } else {
                $("#permisosPanel").show();
                $("#esborrar").show();
                $("#procediments").show();
                $("#serveis").show();
                $("#actualitzar").show();
                $('#permis-boto-nou').attr('href', "<c:url value="/organgestor/"/>" + ${id} + "/permis/new");

                $("#esborrar").attr('href', "<c:url value="/organgestor/"/>" + "${organGestorCommand.codi}" + "/delete");
                $("#procediments").attr('href', "<c:url value="/procediment/organ/"/>" + "${organGestorCommand.codi}");
                $("#serveis").attr('href', "<c:url value="/servei/organ/"/>" + "${organGestorCommand.codi}");
                $("#actualitzar").attr('href', "<c:url value="/organgestor/"/>" + "${organGestorCommand.codi}" + "/update");
                $("#permisos").off("draw.dt");
                $("#permisos").on( "draw.dt", function () {
                    $.each($(".boto-permis"), function( key, permisionLink ) {
                        let link = $(permisionLink).attr("href");
                        let replaced = link.replace("idOrgan", ${id});
                        $(permisionLink).attr("href", replaced);
                    });
                });
            }

            $("#esborrar").click(e => {
               e.preventDefault();
               if (confirm('< spring:message code="organgestor.list.confirmacio.esborrar"/>')) {
                    $.ajax({url: "<c:url value="/organgestorArbre/"/>" + "${organGestorCommand.codi}" + "/delete",
                       success: resposta => {
                            if (resposta && !resposta.error) {
                               $('#contingut-missatges').append(' <div class="alert alert-success"> <button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + resposta.msg + '</div>');
                               $("#detall").html("")
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

             // $("#guardar").click(e => {
             //     console.log("clicked");
             //     e.preventDefault();
             //    $.ajax({
             //        url: "/organgestorArbre/guardar",
             //        type: "POST",
             //        data: $("#dadesOrgan").serialize(),
             //        dataType: "json",
             //        success: () => {},
             //        error: () => {}
             //    })
             // });
        });

    </script>

    <c:set var="formAction"><not:modalUrl value="/organgestorArbre/guardar"/></c:set>
    <form:form action="${formAction}" method="post" commandName="organGestorCommand" role="form">
<%--    <form:form id="dadesOrgan" action="" method="post" commandName="organGestorCommand" role="form">--%>
        <div class="flex-column">
            <not:inputHidden name="id"/>
            <not:inputHidden name="entitatId"/>
            <not:inputText name="codi" readonly="true" textKey="organgestor.list.columna.codi" required="true"/>
            <not:inputText name="nom" readonly="true" textKey="organgestor.list.columna.nom" required="true"/>

            <not:inputText name="estat" readonly="true" textKey="organgestor.list.columna.estat" required="true"/>

            <c:if test="${setLlibre and !isModificacio}">
                <br/>
                <form:hidden path="llibreNom"/>
                <not:inputSelect name="llibre" required="true" optionItems="${llibres}" optionValueAttribute="codi"
                                 optionTextAttribute="nomCurt" emptyOption="true"
                                 textKey="organgestor.form.camp.llibre" placeholderKey="organgestor.form.camp.llibre.info" optionMinimumResultsForSearch="0"/>
            </c:if>
            <c:if test="${setLlibre and isModificacio}">
                <not:inputText name="llibreNom" disabled="true" textKey="organgestor.form.camp.llibre"/>
            </c:if>
            <c:if test="${!setOficina}">
                <not:inputText name="oficinaNom" disabled="true" textKey="organgestor.list.columna.oficina"/>
            </c:if>
            <c:if test="${setOficina}">
                <br/>
                <form:hidden path="oficinaNom"/>
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
<%--            <div>--%>
<%--                <a id="guardar" class="btn btn-success" href="" data-toggle="modal" data-adjust-height="false" data-height="650px">--%>
<%--                    <span class="fa fa-briefcase"></span>&nbsp;&nbsp;<spring:message code="comu.boto.guardar"/>--%>
<%--                </a>--%>
<%--            </div>--%>
            <button id="guardar" type="" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
        </div>
    </form:form>

    <div id="permisosPanel" class="panel panel-default" style="margin-top:20px; <c:if test="${empty id}">display:none;"</c:if>">
        <div class="panel-heading flex-space-between">
            <h2><spring:message code="organgestor.permis.titol"/><small></small></h2>
            <div class="flex-column-center">
                <a id="permis-boto-nou" class="btn btn-default" href="" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.permis.boto.nou.permis"/></a>
            </div>
        </div>
        <div class="panel-body">
            <table id="permisos" data-toggle="datatable" data-url="<c:url value="/organgestor/${id}/permis/datatable"/>" data-search-enabled="false"
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
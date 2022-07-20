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

            $.views.helpers({
                hlpIsAdminOrgan: isAdminOrgan
            });

            let organGestor = null;
            function isAdminOrgan() {
                return ${isRolActualAdministradorOrgan};
            }
            function changedCallback(e, data) {

                $(".datatable-dades-carregant").css("display", "block");
                $("#detall").css("display", "none");
                let text = data.node.text.split('(');
                text = text[text.length-1].split(')')[0];
                $.ajax({
                    url: "/notib/organgestorArbre/organgestor/" + text,
                    success: organ => {
                        if (organ) {
                            $("#detall").html(organ);
                        }
                    },
                    error: err => console.log(err),
                    complete: () => {
                        $("#detall").css("display", "block");
                        $(".datatable-dades-carregant").css("display", "none");
                    }
                });
            }

            $(document).on("click", "#expandAll", function() {
                $('#arbreOrgans').jstree().open_all(null, 200);
            });

            $(document).on("click", "#closeAll", function() {
                $('#arbreOrgans').jstree().close_all(null, 300);
            });


            $(document).ready(function() {
                if ("${filtresEmpty}" === "false") {
                    $('#arbreOrgans').jstree().open_all(null, 200);
                }
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
                    $('#btn-entregaCie').removeClass('active');
                    $('#entregaCie').val(false);
                    $('#form-filtre').submit();
                });
                $('#btn-entregaCie').click(function() {
                    console.log("entrega cie arbre");
                    let entregaCieActiva = !$(this).hasClass('active');
                    $('#entregaCie').val(entregaCieActiva);
                })
                $(".panel-heading:first").css({"display": "flex", "justify-content": "space-between"})
                $(".panel-heading:first").append("<div><button id='canviarVistaOrganGestor' class='btn btn-primary'><spring:message code='boto.canviar.vista'/></button></div>");
                $("#canviarVistaOrganGestor").click(function(){
                    window.location.replace("/notib/organgestor");
                });
            });
        </script>
        <style>
            #detall .container-custom {width: 100%;}
            #detall .container-foot {display: none;}
        </style>
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
                <c:if test="${setOficina}">
                    <div class="col-md-3">
                        <not:inputSelect name="oficina" textKey="organgestor.list.columna.oficina" required="true" optionItems="${oficinesEntitat}" optionValueAttribute="codi"
                                         optionTextAttribute="nom" labelSize="0" inline="true" emptyOption="true" optionMinimumResultsForSearch="2" placeholderKey="organgestor.form.camp.oficina.select"/>
                    </div>
                </c:if>
                <div class="col-md-3">
                    <not:inputSelect name="codiPare" textKey="organgestor.list.columna.organ.arrel" required="true" optionItems="${organsEntitat}" optionValueAttribute="codi"
                                     optionTextAttribute="codiNom" labelSize="0" inline="true" emptyOption="true" optionMinimumResultsForSearch="2" placeholderKey="organgestor.form.camp.organ.arrel.select"/>
                </div>

            </div>
            <div class="row">
                <div class="col-md-2">
                    <button id="btn-entregaCie" title="" class="btn btn-default <c:if test="${organGestorFiltreCommand.entregaCie}">active</c:if>" data-toggle="button">
                        <span class="fa fa-envelope"></span> <spring:message code="organgestor.list.columna.cie"/>
                    </button>
                    <not:inputHidden name="entregaCie"/>
                </div>
                <div class="col-md-2 pull-right">
                    <div class="pull-right">
                        <button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
                        <button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
                    </div>
                </div>
            </div>
        </form:form>
        <div class="row">
            <!------------------------- TREE ------------------------>
            <div class="col-md-5">
                <c:set var="fullesAtributInfoText"><spring:message code="organgestor.arbre.defecte"/></c:set>
                <c:set var="fillsAtributInfoText"><span style="padding-top: 4.5px; padding-left: 2px;" class="fa fa-warning text-danger pull-right" title="<spring:message code="organgestor.list.columna.organGestor.obsolet"/>"></span></c:set>


                <div style="padding-bottom: 10px;">
                    <button id="expandAll" class="btn btn-default"><span class="fa fa-caret-square-o-down"></span> <spring:message code="organgestor.arbre.expandeix"/></button>
                    <button id="closeAll" class="btn btn-default"><span class="fa fa-caret-square-o-up"></span> <spring:message code="organgestor.arbre.contrau"/></button>
                </div>

                <not:arbre id="arbreOrgans" atributId="codi" atributNom="nomCodi" arbre="${arbreOrgans}" fullesAtributId="id" fullesAtributNom="nomCodi"
                           fullesAtributPare="codi"  fullesIcona="fa fa-inbox fa-lg" changedCallback="changedCallback" isArbreSeleccionable="${true}"
                           isFullesSeleccionable="${true}" isOcultarCounts="${true}" fullesAtributCssClassCondition="actiu"/>
            </div>
            <div class="col-md-7">
                <div style="padding-bottom: 10px; text-align: right">
                    <a id="organ-boto-update"
                            class="btn btn-default" href="organgestor/sync/dir3"
                            data-toggle="modal"
                            data-maximized="false">
                            <span class="fa fa-refresh"></span>&nbsp;<spring:message code="organgestor.list.boto.actualitzar.tots"/>
                    </a>
                </div>
                <div id="detall">

                </div>
                <div class="datatable-dades-carregant" style="display: none; text-align: center; margin-top: 100px;">
                    <span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
                </div>
            </div>
        </div>
    </body>
</html>

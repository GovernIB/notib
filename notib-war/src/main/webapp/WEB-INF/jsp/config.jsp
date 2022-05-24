<%--
  Created by IntelliJ IDEA.
  User: limit
  Date: 9/7/21
  Time: 14:45

  Pagina per a la gestió de les propietats de configuració de l'aplicació.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title><spring:message code="config.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
    <script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
    <link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
    <script src="<c:url value="/js/configProperties/configProperties.js"/>"></script>
</head>
<body>
<script>

    $(document).ready(function() {

        $(".entitats").click(e => {

            e.stopPropagation();

            let div = $(e.currentTarget).parent().parent().next();
            div.empty();
            let span = $(e.currentTarget).find("span");
            if ($(div).is(":visible")) {
                span.removeClass("fa-caret-up");
                span.addClass("fa-caret-down");
                addSeparador(e.target);
                div.toggle();
                return;
            }
            $.ajax({
                type: "GET",
                url: "config/entitat/" + e.currentTarget.id.replace(/\./g,"-"),
                success: entitats => {

                    if (!entitats) {
                        return;
                    }
                    removeSeparador(e.currentTarget);
                    div.toggle();
                    if ($(div).is(":visible")) {
                        span.removeClass("fa-caret-down");
                        span.addClass("fa-caret-up");
                    }

                    for (let entitat of entitats) {
                        let keyReplaced = entitat.key.replaceAll('.', '_');
                        let string = '<div>';
                        let disabled = entitat.jbossProperty || !entitat.value ? 'disabled' : '';
                        let textGray = disabled ? "text-gray" : "";
                        string += '<label id="' + keyReplaced+ '_codi_entitat" for="entitat_config_' + keyReplaced + '" class="col-sm-3 control-label margin-bottom ' + textGray + '" style="word-wrap: break-word;">- ' + entitat.entitatCodi + '</label>';
                        string += '<div class="col-sm-8 margin-bottom">';
                        let placeHolder = "placeholder=" + entitat.key;
                        let configurable = "";
                        if (entitat.typeCode === "INT") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
                        } else if(entitat.typeCode === "FLOAT") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" step="0.01" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
                        } else if(entitat.typeCode === "CREDENTIALS") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="password" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
                        } else if(entitat.typeCode === "BOOL") {
                            let checked = entitat.value === "true" ? 'checked' : '';
                            string += '<input id="' + keyReplaced + '" name="booleanValue" class="visualitzar entitat-input" type="checkbox" ' + disabled + ' ' + checked + '>';
                        } else if (entitat.validValues && entitat.validValues.length > 2) {
                            string += '<select id="' + keyReplaced + '" class="form-control" ' + disabled + '>';
                            let selected = "";
                            string += '<option value=""></option>';
                            entitat.validValues.map(x => {
                                selected = x === entitat.value ? "selected" : "";
                                string += '<option value="' + x + '"' + ' ' + selected + '>' + x + '</option>';
                            });
                            string += '<select>';
                        } else if (entitat.validValues && entitat.validValues.length === 2) {
                            let checked = entitat.validValues[0] === entitat.value ? 'checked="checked"' : "";
                            let checked2 = entitat.validValues[1] === entitat.value ? 'checked="checked"' : "";
                            string += '<div id="' + keyReplaced + '" class="visualitzar entitat-input radio-div">'
                                + '<label id="' + keyReplaced+ '_radio_1" for="' + keyReplaced + '_1" class="radio-inline ' + textGray + '">'
                                + '<input id="' + keyReplaced + '_1" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[0] + '"' + ' ' + checked + ' ' + disabled + '>'
                                + entitat.validValues[0]
                                + '</label>'
                                + '<label id="' + keyReplaced+ '_radio_2" for="' + keyReplaced+ '_2" class="radio-inline ' + textGray + '">'
                                + '<input id="' + keyReplaced + '_2" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[1] + '"' + ' ' + checked2 + ' ' + disabled + '>'
                                + entitat.validValues[1]
                                + '</label></div>';
                        } else {
                            string += '<input id="' + keyReplaced + '" class="form-control" type="text" maxlength="2048" value="'
                                + (entitat.value ? entitat.value : "" )+ '"' + disabled + ' ' + placeHolder + '>';
                        }
                        string +='<div><div id="'+ keyReplaced + '_key" class="display-inline"><span id="' + keyReplaced+ '_key_entitat" class="help-block display-inline ' + textGray + '"> ' + entitat.key + '</span></div>';
                        string += '</div></div>'
                        string += '<div class="col-sm-1 margin-bottom flex-space-between">';
                        if (!entitat.jbossProperty) {
                            let saveDelete = entitat.value ? "" : "no-display";
                            let config = !entitat.value ? "" : "no-display";
                            string += '<button id="' + keyReplaced + '_button_save" name="' + entitat.entitatCodi + '" type="button" class="btn btn-success entitat-save ' + saveDelete + '"><i class="fa fa-save"></i></button>';
                            string += '<button id="' + keyReplaced + '_button_trash" name="' + entitat.entitatCodi + '" type="button" class="btn btn-danger entitat-trash ' + saveDelete + '"><i class="fa fa-trash"></i></button>';
                            string += '<button id="' + keyReplaced + '_button_config" name="' + entitat.entitatCodi + '" type="button" class="btn btn-success entitat-config ' + config + '"><i class="fa fa-pencil"></i></button>';
                        }
                        string += '</div></div>';
                        div.append(string);
                    }

                    $("select", div).select2({
                        theme: "bootstrap",
                        allowClear: true,
                        minimumResultsForSearch: -1,
                        placeholder: ""
                    });

                    $(".entitat-save").click(e =>  entitatSave(e));
                    $(".entitat-trash").click(e => entitatTrash(e));
                    $(".entitat-config").click(e => entitatConfig(e));
                }
            });
        });

        $("#btn-sync").on("click", function () {
            $.get('<c:url value="/config/sync"/>', function( data ) {
                $('#syncModal-body').html(
                    '<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
                    '	<span class="fa fa-circle-o-notch fa-spin fa-3x"></span> <br>' +
                    '   Sincronitzant propietats de l\'aplicació ' +
                    '</div>');
                if (data.status) {
                    let message = "S'han actualitzat satisfactoriament les següents propietats: ";
                    data.editedProperties.forEach( element => message += element + ", ");
                    alert(message);
                    document.location.reload();
                } else {
                    alert("Error actualitzant les propietats desde JBoss.");
                }
            });
        });

        <c:url var="urlEdit" value="/config/update"/>
        $(".form-update-config").submit(function(e) {

            e.preventDefault();
            let formData = new FormData(this);
            let id = "config_" + formData.get("key");
            let spinner = addSpinner(id);

            $.ajax({
                url: "${urlEdit}",
                type: "post",
                processData: false,
                contentType: false,
                enctype: "multipart/form-data",
                data: formData,
                success: data => {
                    removeSpinner(spinner);
                    mostrarMissatge(id, data);
                }
            });
        });
        $('.a-config-group:first').tab('show');
    });
</script>
<div class="text-right" data-toggle="botons-titol">
    <a id="btn-sync" class="btn btn-default" data-toggle="modal" data-target="#syncModal"><span class="fa fa-refresh"></span>&nbsp;Sincronitzar amb JBoss</a>
</div>
<div id="syncModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Sincronitzant propietats</h4>
            </div>
            <div id="syncModal-body" class="modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Tanca</button>
            </div>
        </div>
    </div>
</div>
    <div class="row">
        <div class="col-md-3">
            <ul id="tab-list" class="nav nav-pills nav-stacked">
                <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                    <li role="presentation">
                        <a class="a-config-group" data-toggle="tab" href="#group-${group.key}">${group.description}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="col-md-9">
            <div class="tab-content">
            <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                <c:set var="group" value="${group}" scope="request"/>
                <c:set var="level" value="0" scope="request"/>
                <div id="group-${group.key}" class="tab-pane fade">
                    <jsp:include page="includes/configGroup.jsp"/>
                </div>
            </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>

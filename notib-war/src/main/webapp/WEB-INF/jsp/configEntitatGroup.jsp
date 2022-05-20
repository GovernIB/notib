<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<script type="text/javascript">

    $(document).ready(() => {

        // $(".entitats").unbind("click").click(e => {
        //
        //     e.stopPropagation();
        //
        //     let div = $(e.currentTarget).parent().parent().next();
        //     div.empty();
        //     let span = $(e.currentTarget).find("span");
        //     if ($(div).is(":visible")) {
        //         span.removeClass("fa-caret-up");
        //         span.addClass("fa-caret-down");
        //         addSeparador(e.target);
        //         div.toggle();
        //         return;
        //     }
        //     $.ajax({
        //         type: "GET",
        //         url: "config/entitat/" + e.currentTarget.id.replace(/\./g,"-"),
        //         success: entitats => {
        //
        //             if (!entitats) {
        //                 return;
        //             }
        //             removeSeparador(e.currentTarget);
        //             div.toggle();
        //             if ($(div).is(":visible")) {
        //                 span.removeClass("fa-caret-down");
        //                 span.addClass("fa-caret-up");
        //             }
        //
        //             for (let entitat of entitats) {
        //                 let keyReplaced = entitat.key.replaceAll('.', '_');
        //                 let string = '<div>';
        //                 let disabled = entitat.jbossProperty || !entitat.value ? 'disabled' : '';
        //                 let textGray = disabled ? "text-gray" : "";
        //                 string += '<label id="' + keyReplaced+ '_codi_entitat" for="entitat_config_' + keyReplaced + '" class="col-sm-3 control-label margin-bottom ' + textGray + '" style="word-wrap: break-word;">- ' + entitat.entitatCodi + '</label>';
        //                 string += '<div class="col-sm-8 margin-bottom">';
        //                 let placeHolder = "placeholder=" + entitat.key;
        //                 let configurable = "";
        //                 if (entitat.typeCode === "INT") {
        //                     string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
        //                 } else if(entitat.typeCode === "FLOAT") {
        //                     string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" step="0.01" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
        //                 } else if(entitat.typeCode === "CREDENTIALS") {
        //                     string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="password" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
        //                 } else if(entitat.typeCode === "BOOL") {
        //                     let checked = entitat.value === "true" ? 'checked' : '';
        //                     string += '<input id="' + keyReplaced + '" name="booleanValue" class="visualitzar entitat-input" type="checkbox" ' + disabled + ' ' + checked + '>';
        //                 } else if (entitat.validValues && entitat.validValues.length > 2) {
        //                     string += '<select id="' + keyReplaced + '" class="form-control" ' + disabled + '>';
        //                     let selected = "";
        //                     string += '<option value=""></option>';
        //                     entitat.validValues.map(x => {
        //                         selected = x === entitat.value ? "selected" : "";
        //                         string += '<option value="' + x + '"' + ' ' + selected + '>' + x + '</option>';
        //                     });
        //                     string += '<select>';
        //                 } else if (entitat.validValues && entitat.validValues.length === 2) {
        //                     let checked = entitat.validValues[0] === entitat.value ? 'checked="checked"' : "";
        //                     let checked2 = entitat.validValues[1] === entitat.value ? 'checked="checked"' : "";
        //                     string += '<div id="' + keyReplaced + '" class="visualitzar entitat-input">'
        //                         + '<label id="' + keyReplaced+ '_radio_1" for="' + keyReplaced + '_1" class="radio-inline ' + textGray + '">'
        //                         + '<input id="' + keyReplaced + '_1" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[0] + '"' + ' ' + checked + ' ' + disabled + '>'
        //                         + entitat.validValues[0]
        //                         + '</label>'
        //                         + '<label id="' + keyReplaced+ '_radio_2" for="' + keyReplaced+ '_2" class="radio-inline ' + textGray + '">'
        //                         + '<input id="' + keyReplaced + '_2" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[1] + '"' + ' ' + checked2 + ' ' + disabled + '>'
        //                         + entitat.validValues[1]
        //                         + '</label></div>';
        //                 } else {
        //                     string += '<input id="' + keyReplaced + '" class="form-control" type="text" maxlength="2048" value="'
        //                         + (entitat.value ? entitat.value : "" )+ '"' + disabled + ' ' + placeHolder + '>';
        //                 }
        //                 string +='<div><div id="'+ keyReplaced + '_key" class="display-inline"><span id="' + keyReplaced+ '_key_entitat" class="help-block display-inline ' + textGray + '"> ' + entitat.key + '</span></div>';
        //                 string += '</div></div>'
        //                 string += '<div class="col-sm-1 margin-bottom flex-space-between">';
        //                 if (!entitat.jbossProperty) {
        //                     let saveDelete = entitat.value ? "" : "no-display";
        //                     let config = !entitat.value ? "" : "no-display";
        //                     string += '<button id="' + keyReplaced + '_button_save" name=' + entitat.entitatCodi + ' type="button" class="btn btn-success entitat-save ' + saveDelete + '"><i class="fa fa-save"></i></button>';
        //                     string += '<button id="' + keyReplaced + '_button_trash" name=' + entitat.entitatCodi + ' type="button" class="btn btn-danger entitat-trash ' + saveDelete + '"><i class="fa fa-trash"></i></button>';
        //                     string += '<button id="' + keyReplaced + '_button_config" name=' + entitat.entitatCodi + ' type="button" class="btn btn-success entitat-config ' + config + '"><i class="fa fa-pencil"></i></button>';
        //                 }
        //                 string += '</div></div>';
        //                 div.append(string);
        //             }
        //
        //             $("select", div).select2({
        //                 theme: "bootstrap",
        //                 allowClear: true,
        //                 minimumResultsForSearch: -1,
        //                 placeholder: ""
        //             });
        //         }
        //     });
        // });

        $(".selector2").select2({
            theme: "bootstrap",
            allowClear: true,
            minimumResultsForSearch: -1,
            placeholder: ""
        });

        $(".entitat-save").unbind("click").click(e =>  {
            let configKey = e.currentTarget.id.replace("_button_save", "");
            guardarPropietat(configKey);
        });

        $(".entitat-trash").unbind("click").click(e => {

            let configKey = e.currentTarget.id.replace("_button_trash", "");
            let tag = $("#" + configKey);
            if ($(tag).hasClass("radio-div")) {
                $("#" + configKey + "_1").attr("disabled", true);
                $("#" + configKey + "_2").attr("disabled", true);
                $("#" + configKey + "_radio_1").addClass("text-gray");
                $("#" + configKey + "_radio_2").addClass("text-gray");
            }
            $("#" + configKey + "_button_trash").addClass("no-display");
            $("#" + configKey + "_button_save").addClass("no-display");
            $("#" + configKey + "_button_config").removeClass("no-display");
            $("#" + configKey + "_key_entitat").addClass("text-gray");
            $("#" + configKey + "_codi_entitat").addClass("text-gray");

            let elem = $("#" + configKey);
            if (elem.is(':checkbox')) {
                $(elem).prop("checked", false);
            } else if ($(elem).is("div") ) {
                removeValueRadio(elem);
            } else if ($(elem).is("select")) {
                $(elem).val("").trigger("change");
            } else {
                elem.val("");
            }
            if (!$(elem).hasClass("radio-div")) {
                $(elem).attr("disabled", true);
            }
            guardarPropietat(configKey, true);
        });

        $(".entitat-config").unbind("click").click(e => {
            let current = e.currentTarget;
            let tag = $("#" + current.id.replace("_button_config", ""));
            if ($(tag).hasClass("radio-div")) {
                $("#" + current.id.replace("_button_config", "_1")).first().removeAttr("disabled");
                $("#" + current.id.replace("_button_config", "_2")).first().removeAttr("disabled");
                $("#" + current.id.replace("_button_config", "_radio_1")).removeClass("text-gray");
                $("#" + current.id.replace("_button_config", "_radio_2")).removeClass("text-gray");
            } else {
                $("#" + current.id.replace("_button_config", "")).removeAttr("disabled");
            }
            $(current).addClass("no-display");
            $("#" + current.id.replace("_button_config", "_button_save")).removeClass("no-display");
            $("#" + current.id.replace("_button_config", "_button_trash")).removeClass("no-display");
            $("#" + current.id.replace("_button_config", "_key_entitat")).removeClass("text-gray");
            $("#" + current.id.replace("_button_config", "_codi_entitat")).removeClass("text-gray");

        });
    });

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h${level + 4}>${ group.description }</h${level + 4}>
    </div>
    <div class="panel-body flex-column no-side-padding">
        <c:forEach items="${ group.configs }" var="config" varStatus="status_group">
            <c:set var = "configKey" value = "${fn:replace(config.key,'.','_')}"/>
            <div class="separador padding-top-bottom">
                <label for="config_${config.key}" class="col-sm-3 control-label label-config" style="word-wrap: break-word;">${ config.description }</label>
                <div class="col-sm-8 padding-top-7">
                    <c:choose>
                        <c:when test="${config.typeCode == 'INT'}">
                            <input id="${configKey}" class="form-control entitat-input" type="number" maxlength="2048" value="${config.value}"
                            placeholder="config.key" <c:if test="${!config.value}">disabled</c:if>>
                        </c:when>
                        <c:when test="${config.typeCode == 'FLOAT'}">
                            <input id="${configKey}" class="form-control entitat-input" type="number" step="0.01" maxlength="2048" value="${config.value}"
                                   placeholder="config.key" <c:if test="${!config.value}">disabled</c:if>>
                        </c:when>
                        <c:when test="${config.typeCode == 'CREDENTIALS'}">
                            <input id="${configKey}" class="form-control entitat-input" type="password" maxlength="2048" value="${config.value}"
                                   placeholder="config.key" <c:if test="${!config.value}">disabled</c:if>>
                        </c:when>
                        <c:when test="${config.typeCode == 'BOOL'}">
                            <input id="${configKey}" name="booleanValue" class="visualitzar entitat-input" type="checkbox"
                            <c:if test="${!config.value}">disabled</c:if> <c:if test="${config.value == 'true'}">checked</c:if>>
                        </c:when>
                        <c:when test="${config.validValues != null and fn:length(config.validValues) > 2}">
                            <select id="${configKey}" class="form-control selector2" <c:if test="${!config.value}">disabled</c:if>>
                                <option value=""></option>
                                <c:forEach var="opt" items="${config.validValues}">
                                    <option value="${opt}"></option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:when test="${config.validValues != null and fn:length(config.validValues) == 2}">
                            <label id="${config.key}_1" class="radio-inline">
                                <input id="${configKey}_radio_1" name="${configKey}" type=radio value="${config.validValues[0]}"
                                       <c:if test="${!config.value}">disabled</c:if> <c:if test="${config.validValues[0] == config.value}">checked</c:if>>
                                ${config.validValues[0]}
                            </label>
                            <label id="${configKey}_radio_2" class="radio-inline">
                                <input id="${configKey}_radio_2" name="${configKey}" type=radio value="${config.validValues[1]}"
                                       <c:if test="${!config.value}">disabled</c:if> <c:if test="${config.validValues[1] == config.value}">checked</c:if>>
                                ${config.validValues[1]}
                            </label>
                        </c:when>
                        <c:otherwise>
                            <input id="${configKey}" class="form-control" type="text" maxlength="2048" value="${config.value}"
                                   placeholder="config.key" <c:if test="${!config.value}">disabled</c:if>>
                        </c:otherwise>
                    </c:choose>
                    <div id="${configKey}_key"><span class="help-block display-inline">${config.key}</span></div>
                </div>
                <div class="col-sm-1">
                    <c:if test="${not config.jbossProperty}">
                        <button id="${configKey}_button_save" name="${config.entitatCodi}" type="button"
                                class="btn btn-success entitat-save <c:if test="${!entitat.value}">no-display</c:if>"><i class="fa fa-save"></i></button>
                        <button id="${configKey}_button_trash" name="${config.entitatCodi}" type="button"
                                class="btn btn-danger entitat-trash <c:if test="${!entitat.value}">no-display</c:if>"><i class="fa fa-trash"></i></button>
                        <button id="${configKey}_button_config" name="${config.entitatCodi}" type="button"
                                class="btn btn-success entitat-config <c:if test="${entitat.value}">no-display</c:if>"><i class="fa fa-pencil"></i></button>
                     </c:if>
                </div>
            </div>
        </c:forEach>

        <c:set var="level" value="${level + 1}" scope="request"/>
        <c:forEach items="${ group.innerConfigs }" var="group" varStatus="status_group">
            <c:set var="group" value="${group}" scope="request"/>
            <jsp:include page="configEntitatGroup.jsp"/>
            <c:set var="level" value="${level - 1}" scope="request"/>
        </c:forEach>
    </div>
</div>

<%--
  Created by IntelliJ IDEA.
  User: Limit Tecnologies <limit@limit.es>
  Date: 12/7/21
  Time: 17:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<script type="text/javascript">

    $(document).ready(() => {

        $(".entitats").unbind("click").click(e => {

            e.stopPropagation();

            let div = $(e.currentTarget).parent().parent().next();
            div.empty();
            let span = $(e.currentTarget).find("span");
            if ($(div).is(":visible")) {
                span.removeClass("fa-caret-up");
                span.addClass("fa-caret-down");
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
                    div.toggle();
                    if ($(div).is(":visible")) {
                        span.removeClass("fa-caret-down");
                        span.addClass("fa-caret-up");
                    }

                    for (let entitat of entitats) {
                        let keyReplaced = entitat.key.replaceAll('.', '_');
                        let string = '<div>';
                        string += '<label for="entitat_config_' + keyReplaced + '" class="col-sm-3 control-label margin-bottom" style="word-wrap: break-word;"></label>';
                        string += '<div class="col-sm-8 margin-bottom">';
                        let disabled = entitat.jbossProperty ? 'disabled' : '';
                        let placeHolder = "placeholder=" + entitat.key;
                        let configurable = !entitat.value ? ' entitat-no-configurada' : "";
                        if (entitat.typeCode === "INT") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input' + configurable + '" type="number" maxlength="2048" value="' + entitat.value + '"' + disabled + '' + placeHolder + '>';
                        } else if(entitat.typeCode === "FLOAT") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input' + configurable + '" type="number" step="0.01" maxlength="2048" value="' + entitat.value + '"' + disabled + '' + placeHolder + '>';
                        } else if(entitat.typeCode === "CREDENTIALS") {
                           string += '<input id="' + keyReplaced + '" class="form-control entitat-input' + configurable + '" type="password" maxlength="2048" value="' + entitat.value + '"' + disabled + '' + placeHolder + '>';
                        } else if(entitat.typeCode === "BOOL") {
                           let checked = entitat.value === "true" ? 'checked' : '';
                           string += '<input id="' + keyReplaced + '" name="booleanValue" class="visualitzar entitat-input" type="checkbox" ' + disabled + ' ' + checked + '>';
                        } else if (entitat.validValues && entitat.validValues.length > 2) {
                            string += '<select id="' + keyReplaced + '" class="form-control ' + configurable + '">';
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
                            string += '<div id="' + keyReplaced + '"><label for="' + keyReplaced + '_1" class="radio-inline">'
                                + '<input id="' + keyReplaced + '_1" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[0] + '"' + ' ' + checked + '>'
                                + entitat.validValues[0]
                                + '</label>'
                                + '<label for="' + keyReplaced+ '_2" class="radio-inline">'
                                + '<input id="' + keyReplaced + '_2" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[1] + '"' + ' ' + checked2 + '>'
                                + entitat.validValues[1]
                                + '</label></div>';
                        } else {
                            string += '<input id="' + keyReplaced + '" class="form-control ' + configurable+ '" type="text" maxlength="2048" value="'
                                    + (entitat.value ? entitat.value : "" )+ '"' + disabled + ' ' + placeHolder + '>';
                        }
                        string +='<div><div id="'+ keyReplaced + '_key" class="display-inline"><span class="help-block display-inline"> ' + entitat.key + '</span></div>';
                        string += '</div></div>'
                        string += '<div class="col-sm-1 margin-bottom flex-space-between">';
                        if (!entitat.jbossProperty) {
                            string += '<button id="' + keyReplaced + '_button_save" name=' + entitat.entitatCodi + ' type="button" class="btn btn-success entitat-save"><i class="fa fa-save"></i></button>';
                            string += '<button id="' + keyReplaced + '_button_trash" name=' + entitat.entitatCodi + ' type="button" class="btn btn-danger entitat-trash"><i class="fa fa-trash"></i></button>';
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

                    $(".entitat-input").unbind("change").change(e => {
                        let elem = e.currentTarget;
                        let value = getInputValue(elem);
                        let classe = "entitat-no-configurada";
                        let noConfigurada = $(elem).hasClass(classe);
                        if (value && classe) {
                            $(elem).removeClass(classe);
                            return;
                        }
                        if (!value && !noConfigurada) {
                            $(elem).addClass(classe);
                        }
                    });

                    $(".entitat-save").unbind("click").click(e =>  {
                        let configKey = e.currentTarget.id.replace("_button_save", "");
                        guardarPropietat(configKey);
                    });

                    $(".entitat-trash").unbind("click").click(e => {
                        let configKey = e.currentTarget.id.replace("_button_trash", "");
                        let elem = $("#" + configKey);
                        if (elem.is(':checkbox')) {
                            $(elem).prop("checked", false);
                        } else if ($(elem).is("div") ) {
                            removeValueRadio(elem);
                        } else if ($(elem).is("select")) {
                            let options = $("#" + elem[0].id + " option");
                            $(elem).empty();
                            $(elem).append(options);
                        } else {
                            elem.val("");
                        }
                        guardarPropietat(configKey, true);
                    });
                }
            });
        });
    });

</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h${level + 4}>${ group.description }</h${level + 4}>
    </div>
    <div class="panel-body">
        <c:forEach items="${ group.configs }" var="config" varStatus="status_group">
            <c:set var = "configKey" value = "${fn:replace(config.key,'.','_')}"/>

            <form:form method="post" cssClass="config-form form-update-config form-horizontal" action="config/update" commandName="config_${configKey}">
                <form:hidden path="key"/>
                <div class="form-group">
                    <label for="config_${config.key}" class="col-sm-3 control-label" style="word-wrap: break-word;">${ config.description }</label>
                    <div class="col-sm-8">
                        <c:choose>
                            <c:when test="${config.typeCode == 'INT'}">
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="number" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:when>
                            <c:when test="${config.typeCode == 'FLOAT'}">
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="number" step="0.01" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:when>
                            <c:when test="${config.typeCode == 'CREDENTIALS'}">
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="password" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:when>
                            <c:when test="${config.typeCode == 'BOOL'}">
                            <div class="checkbox checkbox-primary">
                                <label>
                                <form:checkbox path="booleanValue" id="config_${config.key}" cssClass="visualitzar"
                                                   disabled="${config.jbossProperty}"/>
                                </label>
                            </div>
                            </c:when>
                            <c:when test="${config.validValues != null and fn:length(config.validValues) > 2}">
                                <form:select path="value" cssClass="form-control" id="config_${config.key}" disabled="${config.jbossProperty}" style="width:100%" data-toggle="select2"
                                             data-placeholder="${config.description}">
                                    <c:forEach var="opt" items="${config.validValues}">
                                        <form:option value="${opt}"/>
                                    </c:forEach>
                                </form:select>
                            </c:when>
                            <c:when test="${config.validValues != null and fn:length(config.validValues) == 2}">
                                <label id="config_${config.key}_1" class="radio-inline">
                                    <form:radiobutton path="value" value="${config.validValues[0]}"/> ${config.validValues[0]}
                                </label>
                                <label id="config_${config.key}_2" class="radio-inline">
                                    <form:radiobutton path="value" value="${config.validValues[1]}"/> ${config.validValues[1]}
                                </label>
                            </c:when>
                            <c:otherwise>
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="text" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:otherwise>
                        </c:choose>
                        <div id="config_${config.key}_key"><span class="help-block display-inline">${config.key}</span></div>
                    </div>
                    <div class="col-sm-1">
                        <c:if test="${not config.jbossProperty}">
                            <button class="btn btn-success"><i class="fa fa-save"></i></button>
                        </c:if>
                        <c:if test="${config.configurable}">
                            <div class="btn btn-default btn-sm btn-rowInfo entitats" id="${config.key}"><span class="fa fa-caret-down"></span></div>
                        </c:if>
                </div>
                </div>
                <div class="form-group entitats-config"></div>
            </form:form>
        </c:forEach>

        <c:set var="level" value="${level + 1}" scope="request"/>
        <c:forEach items="${ group.innerConfigs }" var="group" varStatus="status_group">
            <c:set var="group" value="${group}" scope="request"/>
            <jsp:include page="configGroup.jsp"/>
            <c:set var="level" value="${level - 1}" scope="request"/>
        </c:forEach>
    </div>
</div>

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
            let entitats = $(e.target).parent().parent().next();
            entitats.toggle();
            let span = $(e.target).find("span");
            if ($(entitats).is(":visible")) {
                span.removeClass("fa-caret-down");
                span.addClass("fa-caret-up");
            } else {
                span.removeClass("fa-caret-up");
                span.addClass("fa-caret-down");
            }
        });
        $(".entitat-save").unbind("click").click(e =>  {

            if($("#entitatCodi").length) {
                $("#entitatCodi").val(e.target.id);
                return;
            }
            $(".form-update-config").append('<input id="entitatCodi" type="hidden" name="entitatCodi" value="' + e.target.id + '" />');
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
                        <a href="#" class="btn btn-default btn-sm btn-rowInfo entitats"><span class="fa fa-caret-down"></span></a>
                    </div>
                </div>
                <div class="form-group entitats-config" >
                    <c:forEach var="entitat" items="${config.entitatsConfig}">
<%--                        <form:hidden path="entitatCodi" value="${entitat.codi}"/>--%>
                        <label for="entitat_config_${entitat.codi}" class="col-sm-3 control-label margin-bottom" style="word-wrap: break-word;">${entitat.codi}</label>
                        <div class="col-sm-8 margin-bottom">
                            <c:choose>
                                <c:when test="${config.typeCode == 'INT'}">
                                    <form:input  id="entitat_config_${entitat.configKey}" cssClass="form-control" path="value" placeholder="${entitat.configKey}"
                                                 type="number" maxlength="2048" disabled="${config.jbossProperty}"/>
                                </c:when>
                                <c:when test="${config.typeCode == 'FLOAT'}">
                                    <form:input  id="entitat_config_${entitat.configKey}" cssClass="form-control" path="value" placeholder="${entitat.configKey}"
                                                 type="number" step="0.01" maxlength="2048" disabled="${config.jbossProperty}"/>
                                </c:when>
                                <c:when test="${config.typeCode == 'CREDENTIALS'}">
                                    <form:input  id="entitat_config_${entitat.configKey}" cssClass="form-control" path="value" placeholder="${entitat.configKey}"
                                                 type="password" maxlength="2048" disabled="${config.jbossProperty}"/>
                                </c:when>
                                <c:when test="${config.typeCode == 'BOOL'}">
                                    <div class="checkbox checkbox-primary">
                                        <label>
                                            <form:checkbox path="EntitatBooleanValue" id="${entitat.configKey}" cssClass="config-form visualitzar"
                                                           disabled="${config.jbossProperty}"/>
                                        </label>
                                    </div>
                                </c:when>
                                <c:when test="${config.validValues != null and fn:length(config.validValues) > 2}">
                                    <form:select path="value" cssClass="form-control" id="entitat_config_${entitat.configKey}" disabled="${config.jbossProperty}" style="width:100%" data-toggle="select2"
                                                 data-placeholder="${config.description}">
                                        <c:forEach var="opt" items="${config.validValues}">
                                            <form:option value="${opt}"/>
                                        </c:forEach>
                                    </form:select>
                                </c:when>
                                <c:when test="${config.validValues != null and fn:length(config.validValues) == 2}">
                                    <label id="entitat_config_${entitat.configKey}_1" class="radio-inline">
                                        <form:radiobutton path="value" value="${config.validValues[0]}"/> ${config.validValues[0]}
                                    </label>
                                    <label id="entitat_config_${entitat.configKey}_2" class="radio-inline">
                                        <form:radiobutton path="value" value="${config.validValues[1]}"/> ${config.validValues[1]}
                                    </label>
                                </c:when>
                                <c:otherwise>
                                    <form:input  id="entitat_config_${entitat.configKey}" cssClass="form-control" path="value" placeholder="${entitat.configKey}"
                                                 type="text" maxlength="2048" disabled="${config.jbossProperty}"/>
                                </c:otherwise>
                            </c:choose>
                            <div id="entitat_config_${entitat.configKey}_key"><span class="help-block display-inline">${entitat.configKey}</span></div>
                        </div>
                        <div class="col-sm-1 margin-bottom">
                            <c:if test="${not config.jbossProperty}">
                                <button id="${entitat.codi}" class="btn btn-success entitat-save"><i class="fa fa-save"></i></button>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
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

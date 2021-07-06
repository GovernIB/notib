<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="value" required="false" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="generalClass" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="messageInfo" required="false" rtexprvalue="true"%>
<%@ attribute name="info" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="inputSize" required="false" rtexprvalue="true"%>
<%@ attribute name="readonly" required="false" rtexprvalue="true"%>
<%@ attribute name="picker" required="false" rtexprvalue="true"%>
<%@ attribute name="labelClass" required="false" rtexprvalue="true"%>
<%@ attribute name="inputClass" required="false" rtexprvalue="true"%>
<%@ attribute name="inputLength" required="false" rtexprvalue="true"%>
<%@ attribute name="inputMaxLength" required="false" rtexprvalue="true"%>
<%@ attribute name="inputMinLength" required="false" rtexprvalue="true"%>
<%@ attribute name="showsize" required="false" rtexprvalue="true"%>
<c:set var="campValue" value="${value}"/>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campInfoText"><c:choose><c:when test="${not empty messageInfo}"><spring:message code="${messageInfo}"/></c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize"><c:choose><c:when test="${not empty inputSize}">${inputSize}</c:when><c:otherwise>${12 - campLabelSize}</c:otherwise></c:choose></c:set>
<c:set var="inputMinLength" value="${(empty inputMinLength) ? 0 : inputMinLength}" />
<c:set var="myReadonly">
	<c:choose>
		<c:when test="${empty readonly}">false</c:when>
		<c:otherwise>${readonly}</c:otherwise>
	</c:choose>
</c:set>
<style>
.info-length {
	font-size: x-small;
}
.comentari {
	font-size: 12px;
	color: #999;
	margin-bottom: 0px;
}
</style>
<script>
$(document).ready(function() {
	if ('${showsize}' && '${inputMaxLength}') {
		//Contador
		var field = '${name}';	
		var fieldSize = 'inputCurrentLength_${name}';
		var fieldSizeClass = $(document.getElementsByClassName(fieldSize)[0]);
		if (fieldSizeClass.val() != undefined && fieldSizeClass.val().length != 0) {
			var size = $(field).val().length;
			$(fieldSizeClass).text(size);
		} else {
			$(fieldSizeClass).text(0);
		};
		
		$(document.getElementById(field)).bind("change paste keyup", function() {
			var size = $(this).val().length;
			$(fieldSizeClass).text(size);
		});
	}
});
</script>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
<c:choose>
	<c:when test="${not inline}">
		<label class="control-label col-xs-${campLabelSize} ${labelClass}" for="${campPath}">${campLabelText}</label>
		<div class="col-xs-${campInputSize} ${inputClass}">
		<c:choose>
			<c:when test="${picker}">
				<div id="${campPath}" class="input-group colorpicker-component">
					<form:input cssClass="form-control ${generalClass}" id="${campPath}"
								type="email"
								maxlength="${inputMaxLength}" minlength="${inputMinLength}"
								value="${campValue}" path="${campPath}"
								disabled="${disabled}" readonly="${myReadonly}"/>
					<span class="input-group-addon"><i></i></span>
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
					<c:if test="${info == true}">
						<p class="comentari col-xs-12 col-xs-offset-">${campInfoText}</p>
					</c:if>
					<c:if test="${not empty inputMaxLength}">
					<p class="info-length text-success">
						<span class="glyphicon glyphicon-info-sign"></span>
						<span class="inputCurrentLength_${name}">${inputLength}</span>
							<spring:message code="notificacio.form.camp.logitud"/>
						<span> ${inputMaxLength}</span>
					</p>
				</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<form:input cssClass="form-control ${generalClass}" id="${campPath}"
							type="email"
							maxlength="${inputMaxLength}" minlength="${inputMinLength}"
							value="${campValue}" path="${campPath}"
							disabled="${disabled}" readonly="${myReadonly}"/>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
				<c:if test="${info == true}">
					<p class="comentari col-xs-12 col-xs-offset-">${campInfoText}</p>
				</c:if>
				<c:if test="${not empty inputMaxLength}">
					<p class="info-length text-success">
						<span class="glyphicon glyphicon-info-sign"></span>
						<span class="inputCurrentLength_${name}">${inputLength}</span>
							<spring:message code="notificacio.form.camp.logitud"/>
						<span> ${inputMaxLength}</span>
					</p>
				</c:if>
			</c:otherwise>
		</c:choose>
		</div>
	</c:when>
	<c:otherwise>
   		<label class="sr-only" for="${campPath}">${campLabelText}</label>
   		<form:input maxlength="${inputMaxLength}" value="${campValue}" path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}" readonly="${myReadonly}"/>
		<c:if test="${not empty inputMaxLength}">
			<p class="info-length text-success">
				<span class="glyphicon glyphicon-info-sign"></span>
				<span class="inputCurrentLength_${name}">${inputLength}</span>
					<spring:message code="notificacio.form.camp.logitud"/>
				<span> ${inputMaxLength}</span>
			</p>
		</c:if>
	</c:otherwise>
</c:choose>
</div>
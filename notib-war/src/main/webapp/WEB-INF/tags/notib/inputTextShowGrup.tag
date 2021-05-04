<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="classe" required="false" rtexprvalue="true"%>
<%@ attribute name="value" required="false" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="readonly" required="false" rtexprvalue="true"%>

<c:set var="campValue" value="${value}"/>
<c:set var="campId" value="${id}"/>
<c:set var="campClass" value="${classe}"/>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<c:set var="myReadonly">
	<c:choose>
		<c:when test="${empty readonly}">false</c:when>
		<c:otherwise>${readonly}</c:otherwise>
	</c:choose>
</c:set>
<div class='form-group'>
	<label class='control-label col-xs-4'></label>
	<div class='col-xs-8'>
		<div class='input-group'>
			<input value="${campValue}" id="${campId}" class="form-control" readonly="${myReadonly}"/>
			<span class='input-group-addon ${campClass}' id='showPermisCheck'>
				<span class='fa fa-angle-down'></span>
			</span>
			<span class='input-group-addon ${campClass}' id='remove'>
				<span class='fa fa-remove'></span>
			</span>
		</div>
	</div>
</div>
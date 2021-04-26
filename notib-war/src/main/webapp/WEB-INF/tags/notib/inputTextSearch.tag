<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="false" rtexprvalue="true"%>
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
<%@ attribute name="inputSize" required="false" rtexprvalue="true"%>
<%@ attribute name="readonly" required="false" rtexprvalue="true"%>
<%@ attribute name="searchButton" required="true" rtexprvalue="true"%>
<%@ attribute name="funcio" required="false" rtexprvalue="true"%>

<c:set var="campValue" value="${value}"/>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize"><c:choose><c:when test="${not empty inputSize}">${inputSize}</c:when><c:otherwise>${12 - campLabelSize}</c:otherwise></c:choose></c:set>
<c:set var="myReadonly">
	<c:choose>
		<c:when test="${empty readonly}">false</c:when>
		<c:otherwise>${readonly}</c:otherwise>
	</c:choose>
</c:set>

<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>  ${campPath}"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
<c:choose>
	<c:when test="${not inline}">
		<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
		<div class="col-xs-${campInputSize}">
		<div class="input-group" id="${searchButton}" onclick="${funcio}">
			<form:input value="${campValue}" path="${campPath}" cssClass="form-control habilitat" id="${campPath}" disabled="${disabled}" readonly="${myReadonly}"/>
			<span class="input-group-addon habilitat"> 
			<a>
				<span class="fa fa-search"></span>
			</a>
			</span>
		</div>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
		</div>
	</c:when>
	<c:otherwise>
   		<label class="sr-only" for="${campPath}">${campLabelText}</label>
   		<form:input value="${campValue}" path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}" readonly="${myReadonly}"/>
			<span class="input-group-addon"> 
			<a id="${searchButton}" onclick="${funcio}">
				<span class="fa fa-search"></span>
			</a>
			</span>
		</c:otherwise>
</c:choose>
</div>
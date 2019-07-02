<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="messageInfo" required="false" rtexprvalue="true"%>
<%@ attribute name="info" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="funcio" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInfoText"><c:choose><c:when test="${not empty messageInfo}"><spring:message code="${messageInfo}"/></c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<c:choose>
	<c:when test="${not inline}">
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label class="control-label col-xs-${campLabelSize}" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
	</label>
	<div class="controls col-xs-${campInputSize}">
		<div class="checkbox checkbox-primary">
  			<label> 
				<form:checkbox path="${campPath}" cssClass="span12 " id="${campPath}" onclick="${funcio}"  autocomplete="off"/>
				
			</label>
		</div>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
		<c:if test="${info == true}">
			<c:if test="${info == true}">
				<p class="comentari col-xs-offset-">${campInfoText}</p>
			</c:if>
		</c:if>
	</div>
</div>
	</c:when>
	<c:otherwise>
		<label class="checkbox">
			<form:checkbox path="${campPath}" id="${campPath}"/>
			<c:choose>
				<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
				<c:when test="${not empty text}">${text}</c:when>
				<c:otherwise>${campPath}</c:otherwise>
			</c:choose>
		</label>
	</c:otherwise>
</c:choose>

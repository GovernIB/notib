<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="messageInfo" required="false" rtexprvalue="true"%>
<%@ attribute name="info" required="false" rtexprvalue="true"%>
<%@ attribute name="orientacio" required="false" rtexprvalue="true"%>
<%@ attribute name="custom" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="inputSize" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}">*</c:if></c:set>
<c:set var="campInfoText"><c:choose><c:when test="${not empty messageInfo}"><spring:message code="${messageInfo}"/></c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> </c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize"><c:choose><c:when test="${not empty inputSize}">${inputSize}</c:when><c:otherwise>${12 - campLabelSize}</c:otherwise></c:choose></c:set>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
<c:choose>
   <c:when test="${not empty orientacio}">
   	<c:set value="${orientacio}" var="orientacio"></c:set>
   </c:when>
   <c:otherwise>
    <c:set value="auto" var="orientacio"></c:set>
   </c:otherwise>
</c:choose>
<c:choose>
   <c:when test="${not empty custom}">
   	<c:set value="${custom}" var="custom"></c:set>
   </c:when>
   <c:otherwise>
    <c:set value="false" var="custom"></c:set>
   </c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not inline}">
		<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
		<div class="col-xs-${campInputSize}">
			<div class="input-group" style="width:100%">
				<form:input path="${campPath}" cssClass="form-control datepicker" id="${campPath}" disabled="${disabled}" data-toggle="datepicker" data-idioma="${idioma}" data-orientacio="${orientacio}" data-custom="${custom}"/>
				<span class="input-group-addon" style="width:1%"><span class="fa fa-calendar"></span></span>
			</div>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			<c:if test="${info == true}">
				<p class="comentari col-xs-12 col-xs-offset-">${campInfoText}</p>
			</c:if>
		</div>
	</c:when>
	<c:otherwise>
		<label class="sr-only" for="${campPath}">${campLabelText}</label>
		<div class="input-group">
			<form:input path="${campPath}" cssClass="form-control datepicker classe" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}" data-toggle="datepicker" data-idioma="${idioma}" data-orientacio="bottom"/>
			<span class="input-group-addon" style="width:1%"><span class="fa fa-calendar"></span></span>
		</div>
	</c:otherwise>
</c:choose>
</div>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="idIcon" required="false" rtexprvalue="true"%>
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
<c:set var="idIcon" value="${idIcon}"/>
<c:set var="campPath" value="${name}"/>
<c:set var="campLabelText"><spring:message code="${textKey}"/></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>


<div class="form-group">
<label class="control-label col-xs-${campLabelSize}">${campLabelText}</label>
	<div class="col-xs-${campInputSize} ">
		<div class="input-group">
			<form:input value="${campValue}" path="" cssClass="form-control ${campPath}" disabled="false" readonly="${myReadonly}"/>
			<span class="input-group-addon" id="${idIcon}"><span class="fa fa-plus"></span></span>
		</div>		
	</div>
</div>
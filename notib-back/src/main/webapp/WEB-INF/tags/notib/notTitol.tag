<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="id" required="true" rtexprvalue="true"%>
<c:set var="campName" value="${name}"/>
<c:set var="campId" value="${id}"/>

<div>
	<h4>${campName} <span class="fa fa-chevron-circle-down" id="${campId}"></span></h4>
	<hr />
</div>
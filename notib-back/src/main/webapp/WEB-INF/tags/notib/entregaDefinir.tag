<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="titol" required="true" rtexprvalue="true"%>

<c:set var="campTitol" value="${titol}"/>

<div class="col-md-12">
	<div class="col-md-6 text-primary">
		<label>${campTitol}</label>
	</div>
</div>




<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<script>
$('input').change(function() {
	if(this.type != "file"){
		this.value = this.value.trim();
	}
	
});
</script>
<c:set var="campPath" value="${name}"/>
<form:hidden path="${campPath}" cssClass="form-control" id="${campPath}"/>
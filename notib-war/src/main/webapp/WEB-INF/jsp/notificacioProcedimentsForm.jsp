<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="notificacio.form.titol.procediments"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
	
<script>  
$(document).ready(function(){ 
	$('#search').keyup(function(){  
		console.log("as");
		search($(this).val());
		
	});
});  

function search(value) {
	 $('#procediments tr').each(function(){  
         var found = 'false';  
         $(this).each(function(){  
              if($(this).text().toLowerCase().indexOf(value.toLowerCase()) >= 0) {  
                   found = 'true';  
              }  
         });  
         if(found == 'true')  {  
              $(this).show();  
         } else {  
              $(this).hide();  
         }  
    });  
}
</script>  
</head>

<body>
	<input type="text" name="search" id="search" class="form-control" placeholder="Cercar"/>
	<br />
	<table class="table table-hover" id="procediments">
		<thead>
			<tr>
				<th scope="col"><spring:message code="notificacio.procediment.codi"/></th>
				<th scope="col"><spring:message code="notificacio.procediment.nom"/></th>
				<th scope="col"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${procediments}" var="procediment">
					<tr>
						<td scope="row" name="codi" width="10%">${procediment.codi}</td>
						<td scope="row" name="nom" width="80%">${procediment.nom}</td>
						<td scope="row" name="id" width="10%">
						<button onclick="window.top.location='/notib/notificacio/new/${procediment.id}';return false;" class="btn btn-default"><spring:message code="notificacio.form.titol.procediments.iniciar"/></button>
						</td>
					</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
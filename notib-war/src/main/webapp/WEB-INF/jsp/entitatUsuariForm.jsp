<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty entitatCommand.id}"><c:set var="titol"><spring:message code="usuari.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="usuari.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title>${titol}</title>
	<not:modalHead/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">

		$(document).ready(function(){
			
			$("#callback").prop( 'disabled', $("#usuariAplicacio:checked").length == 0 );
			
			$("#usuariAplicacio").on( "click",
			function() {
				if( $("#usuariAplicacio:checked").length != 0 ) {
					$("#callback").prop('disabled', false);
				}else{
					$("#callback").prop('disabled', true);
					$("#callback").prop('value', "");
				}
			});
			
			$("#entitatUsuariCommand").submit(function(e){
			    
 			    var $form = $(this); 
			    var response = false;
			    
			    $.ajax({
				    url:'<c:url value="/entitat/${entitatUsuariCommand.entitat}/usuaris/exist/"/>' + $("input[id='usuari.codi']").val(),
				    type:'GET',
				    dataType: 'json',
				    async: false,
				    success: function(json) {
				    	if (json == true) {
				    		response = true;
				    	} else {
				    		if (confirm("L'usuari indicat no existeix. Segur que el vol crear?")) {
				    			response = true;
				    		}
				    	}
				    }
				});
			    
				return response;
				
 			})
			
		});
		
	</script>
</head>

<body>

	<form:form action="create" method="post" cssClass="form-horizontal" commandName="entitatUsuariCommand" role="form" onsubmit="validateUsuariExist();">
		
		<form:hidden path="id"/>
		
		<div class="col-xs-10">
			<not:inputText name="usuari.codi" textKey="entitat.usuari.form.camp.codi" required="true" />
		</div>
		
		<div class="col-xs-10">
			<not:inputCheckbox name="usuariAplicacio" textKey="entitat.usuari.form.camp.usuariaplicacio" />
		</div>
		
		<div class="col-xs-10">
			<not:inputText name="callback" textKey="entitat.usuari.form.camp.callback" required="false" />
		</div>
		
		<div id="modal-botons">
			<button id="btnSubmit" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat/${entitatId}/usuaris"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	
</body>

</html>

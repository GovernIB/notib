<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty procedimentCommand.codi}"><c:set var="titol"><spring:message code="procediment.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="procediment.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<not:modalHead/>
<script type="text/javascript">
$(document).ready(function() {
		
		var count = 0;
		// Show input to add grup
		$('#agrupar').change(function(){
			if ($(this).is(':checked'))
				$('#grups').slideDown("slow");
			else
				$('#grups').slideUp("slow");
			
			webutilModalAdjustHeight();
		});
		
		$('#add').on('click', function () {
			//Input to add
			var grup = "<div class='form-group'><label class='control-label col-xs-4'></label><div class='col-xs-8'><div class='input-group'><input name='grup' id='grup' type='text' class='form-control add' readonly/><span class='input-group-addon' id='remove'><span class='fa fa-remove'></span></span></div></div></div>";
			$("#list").prepend(grup).find("#grup").addClass("grupVal_" + count);

			//Get the value of the input added
			var val = $(".input-group").children().val();
			
			//Assign the value (grup) to the input
			$(".grupVal_" + count).attr("value", val);
			//Add a variable class to identify a specific grup
			$("#list").find("#remove").addClass("grupVal_" + count);
			count++;

			webutilModalAdjustHeight();
		});
		
		//Eliminar grups
		$(document).on('click', "#remove", function () {
			
			var grupId = $(this).parent().children().attr('id'); 
			var grupsClass = $(this).attr('class'); 
			var lastClass = grupsClass.split(' ').pop();
			var parentRemove = $("." + lastClass).parent();
			var parentInput = parentRemove.parent();
			var parentDiv = parentInput.parent();
			
			var grupUrl = "grup/" + grupId + "/delete";
			
			if (confirm('<spring:message code="grup.list.confirmacio.esborrar"/>') && !isNaN(grupId)) 
				$.ajax({
			        type: "GET",
			        url: grupUrl,
			        success: function (data) {
			        	//Remove div parent
						parentDiv.slideUp("normal", function() {
							$(this).remove(); 
							webutilModalAdjustHeight();
						});
			        },
			        error: function (data) {
			        	//Remove div parent
						alert("ERROR ELIMINANT GRUP")
			        }
			    });
			else
				//Remove div parent
				parentDiv.slideUp("normal", function() {
					$(this).remove(); 
					webutilModalAdjustHeight();
				});
				
		});
});				
</script>	

</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/procediment/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand" role="form">
		<form:hidden path="id"/>
		<div class="row">
			<div class="col-md-3">
				<not:inputText name="codi" textKey="procediment.form.camp.codi" required="true"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="nom" textKey="procediment.form.camp.nom" required="true"/>
			</div>
			<div class="col-md-2">
				<not:inputText name="codisia" textKey="procediment.form.camp.codisia" required="true"/>
			</div>
			<c:choose>
			  <c:when test="${entitats != null}">
			    <div class="col-md-2">
					<not:inputSelect name="entitatId" textKey="procediment.form.camp.entitat" optionItems="${entitats}" optionValueAttribute="id" optionTextAttribute="nom" required="true"/>
				</div>
			  </c:when>
			  <c:otherwise>
			    <div class="col-md-2">
			    	<form:hidden path="entitatId" value="${entitatId}"/>
					<not:inputText name="entitatNom" textKey="procediment.form.camp.entitat" value="${entitat.nom}" required="true"></not:inputText>
				</div>
			  </c:otherwise>
			</c:choose>
			<div class="col-md-2">
				<not:inputSelect name="pagadorPostalId" textKey="procediment.form.camp.postal" optionItems="${pagadorsPostal}" optionValueAttribute="id" optionTextAttribute="dir3codi" required="true"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect name="pagadorCieId" textKey="procediment.form.camp.cie" optionItems="${pagadorsCie}" optionValueAttribute="id" optionTextAttribute="dir3codi"/>
			</div>
			<div class="col-md-2">
				<not:inputCheckbox name="agrupar" textKey="procediment.form.camp.agrupar"/>
			</div>
			<div class="col-md-4" id="grups">	 	
				<not:inputTextAddGrup name="grup" idIcon="add" textKey="procediment.form.camp.grups"/>
				<div id="list"></div>
				<c:if test="${grups != null}">
					<c:forEach var="grup" items="${grups}" varStatus="status">
							<not:inputTextShowGrup name="grup" classe="${grup.codi}" id="${grup.id}" value="${grup.nom}" readonly="true"/>
					</c:forEach>
				</c:if>		
			</div>
			<div id="modal-botons">
				<button id="addProcedimentButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
				<a href="<c:url value="/procediments"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</div>	
		</div>
	</form:form>
	
</body>
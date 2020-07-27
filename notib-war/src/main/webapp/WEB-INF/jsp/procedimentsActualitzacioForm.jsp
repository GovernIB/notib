<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="procediment.actualitzacio.auto"/></title>
<%-- 	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/> --%>
<%-- 	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/> --%>
<%-- 	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script> --%>
<%-- 	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script> --%>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<not:modalHead/>
	<script>
		$(document).ready(function() {
			//loading
			$('#form').on("submit", function(){
				$('.loading').fadeIn();
			});
		});

		function refreshProgres() {
			var idIterval = setInterval(function(){
				  
				  progreso +=10;
				  $('#bar').css('width', progress + '%');
				  $('#bar').attr('aria-valuenow', progress);
					
				  if(progreso == 100){
				    clearInterval(idIterval);
				  }
			},200);
		}
	</script>
	<style type="text/css">
		.loading {
			background: rgba( 255, 255, 255, 0.8 );
		  	display: none;
		  	height: 100%;
		  	position: fixed;
		  	width: 100%;
		  	z-index: 9999;
		  	left: 0;
		  	top: 0;
		}
		.loading-gif {
			left: 50%;
		  	margin-left: -32px;
		  	margin-top: -32px;
		  	position: absolute;
		  	top: 50%;
		  	width: 4%;
		}
		.loading-gif img {
			width: 45%;
		}
		.loading-text {
			left: 47%;
		  	margin-left: -32px;
		  	margin-top: -32px;
		  	position: absolute;
		  	top: 55%;
		}
		body {
			min-height: 400px;
		}
		.progress {
			display: none;
		}
		.confirmacio {
			text-align: center;
		}
	</style>
</head>

<body>
	<div class="confirmacio"> 
		<h4><spring:message code="procediemnt.actualitzacio.confirmacio"/></h4>
	</div>
	<div class="loading">
		<div class="loading-gif">
			<img src="<c:url value="/img/ajax-loader.gif"/>"/>
		</div>
		<div class="loading-text">
			<p><spring:message code="procediemnt.actualitzacio.loading"/></p>
		</div>
	</div>
	
	<c:set var="formAction"><not:modalUrl value="/procediment/update/auto"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<div class="progress">
  			<div id="bar" class="progress-bar" role="progressbar progress-bar-striped active" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.actualitzar"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

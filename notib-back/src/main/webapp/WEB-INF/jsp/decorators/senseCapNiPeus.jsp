<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<%-- Idioma principal del document, per a que els lectors de pantalla utilitzin l'entonacio correcta --%>
<c:set var="idiomaDocument" value="${fn:toLowerCase(sessionScopedContext.idiomaUsuari)}"/>
<c:if test="${empty idiomaDocument}">
	<c:set var="idiomaDocument" value="${fn:toLowerCase(pageContext.locale.language)}"/>
</c:if>
<c:if test="${empty idiomaDocument}">
	<c:set var="idiomaDocument" value="ca"/>
</c:if>
<!DOCTYPE html>
<html lang="${idiomaDocument}">
<head>
	<meta charset="utf-8">
	<title><decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
	<script type="text/javascript">const ctxPath = "${pageContext.request.contextPath}"</script>
	<!-- Estils CSS -->
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/estils.css"/>" rel="stylesheet">
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-colorpicker.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-colorpicker.min.js"/>"></script>
	<script type="text/javascript">
		var userLanguage;
		var setIdioma = function() {
			$.ajax({
				type: "GET",
				async: false,
			    url: '<c:url value="/usuari/configuracio/idioma"/>',
			    success: function(data) {
			    	userLanguage =  data;
			    }
			  });
	    }
		setIdioma();
	</script>
	<decorator:head />
</head>
<body style="padding-top: 1em;">
	<nav aria-label="<spring:message code='decorator.footer.utilitat'/>">
		<a id="oawSitemapLinkError" href="<c:url value="/reactapp/sitemap"/>"><spring:message code="decorator.footer.sitemap"/></a>
		<span aria-hidden="true"> | </span>
		<a id="oawAccessibilitatLinkError" href="<c:url value="/reactapp/accessibilitat"/>"><spring:message code="decorator.footer.accessibilitat"/></a>
	</nav>
	<div class="container-nocappeus">
		<div id="contingut-missatges"><not:missatges/></div>
		<decorator:body />
	</div>
</body>
</html>

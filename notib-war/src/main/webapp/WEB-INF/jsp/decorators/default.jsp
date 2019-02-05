<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<%
	pageContext.setAttribute(
			"sessionEntitats",
			es.caib.notib.war.helper.EntitatHelper.findEntitatsAccessibles(request));
	pageContext.setAttribute(
			"entitatActual",
			es.caib.notib.war.helper.EntitatHelper.getEntitatActual(request));
	pageContext.setAttribute(
			"requestParameterCanviEntitat",
			es.caib.notib.war.helper.EntitatHelper.getRequestParameterCanviEntitat());
	pageContext.setAttribute(
			"rolActual",
			es.caib.notib.war.helper.RolHelper.getRolActual(request));
	pageContext.setAttribute(
			"rolsUsuariActual",
			es.caib.notib.war.helper.RolHelper.getRolsUsuariActual(request));
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
	pageContext.setAttribute(
			"isRolActualUsuari",
			es.caib.notib.war.helper.RolHelper.isUsuariActualUsuari(request));
	pageContext.setAttribute(
			"requestParameterCanviRol",
			es.caib.notib.war.helper.RolHelper.getRequestParameterCanviRol());
	pageContext.setAttribute(
			"permisConsulta",
			request.getAttribute("permisConsulta"));
	pageContext.setAttribute(
			"permisNotificacio",
			request.getAttribute("permisNotificacio"));
		
%>
<c:set var="hiHaEntitats" value="${fn:length(sessionEntitats) > 0}"/>
<c:set var="hiHaMesEntitats" value="${fn:length(sessionEntitats) > 1}"/>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Notib - <decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
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
	<decorator:head />
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.png"/>);
	color:#666666;
	padding-top: 120px;
}
</style>
</head>
<body>

	<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<%--button class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button--%>
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<img src="<c:url value="/img/govern-logo.png"/>" alt="Govern de les Illes Balears" height="80px"/>
					</div>
					<div id="app-logo" class="pull-left">
						<img src="<c:url value="/img/logo.png"/>" alt="NOTIB" />
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right">
					
						<c:if test="${hiHaEntitats && isRolActualAdministradorEntitat}">
							<li class="dropdown">
								<c:if test="${hiHaMesEntitats}"><a href="#" data-toggle="dropdown"></c:if>
		         				<span class="fa fa-home"></span> ${entitatActual.nom} <c:if test="${hiHaMesEntitats}"><b class="caret caret-white"></b></c:if>
								<c:if test="${hiHaMesEntitats}"></a></c:if>
								<c:if test="${hiHaMesEntitats}">
									<ul class="dropdown-menu">
										<c:forEach var="entitat" items="${sessionEntitats}" varStatus="status">
											<c:if test="${entitat.id != entitatActual.id}">
											
												<c:url var="urlCanviEntitat" value="/index">
													<c:param name="${requestParameterCanviEntitat}" value="${entitat.id}"/>
												</c:url>
												<li><a href="${urlCanviEntitat}">${entitat.nom}</a></li>
												
											</c:if>
										</c:forEach>
									</ul>
								</c:if>
							</li>
						</c:if>
						
						<li class="dropdown">
							<c:choose>
								<c:when test="${fn:length(rolsUsuariActual) > 1}">
									<a href="#" data-toggle="dropdown">
										<span class="fa fa-bookmark"></span>
										<spring:message code="decorator.menu.rol.${rolActual}"/>
										<b class="caret caret-white"></b>
									</a>
									<ul class="dropdown-menu">
										<c:forEach var="rol" items="${rolsUsuariActual}">
											<c:if test="${rol != rolActual}">
												<li>
													<c:url var="canviRolUrl" value="/index">
														<c:param name="${requestParameterCanviRol}" value="${rol}"/>
													</c:url>
													<a href="${canviRolUrl}"><spring:message code="decorator.menu.rol.${rol}"/></a>
												</li>
											</c:if>
										</c:forEach>
									</ul>
								</c:when>
								<c:otherwise>
									<c:if test="${not empty rolActual}"><span class="fa fa-bookmark"></span>&nbsp;<spring:message code="decorator.menu.rol.${rolActual}"/></c:if>
								</c:otherwise>
							</c:choose>
						</li>
						
						<li>
							<span class="fa fa-user"></span>
							<c:choose>
								<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom}</c:when>
								<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
							</c:choose>
						</li>
						
					</ul>
					<div class="clearfix"></div>
					<div class="btn-group navbar-btn navbar-right">
						<div class="btn-group">
							<c:if test="${isRolActualAdministrador}">
							<div class="btn-group">
								<a href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>							
							</div>
							<div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/entitat"/>"><spring:message code="decorator.menu.entitats"/></a></li>
									<li><a href="<c:url value="/procediment"/>"><spring:message code="decorator.menu.procediment"/></a></li>
									<li><a href="<c:url value="/aplicacio"/>"><spring:message code="decorator.menu.aplicacions"/></a></li>		
								</ul>
							</div>
							</c:if>
							<c:if test="${isRolActualUsuari}">
								<c:if test="${permisConsulta}">
									<div class="btn-group">
										<a href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
									</div>
								</c:if>
								<c:if test="${permisNotificacio}">
									<div class="btn-group">
										<a href="<c:url value="/notificacio/new"/>" class="btn btn-primary"><spring:message code="decorator.menu.altanotificacio"/></a>
									</div>
								</c:if>
							</c:if>
							<c:if test="${isRolActualAdministradorEntitat}">
							<div class="btn-group">
								<a href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
							</div>
							<div class="btn-group">
								<a href="<c:url value="/enviament"/>" class="btn btn-primary"><spring:message code="decorator.menu.enviaments"/></a>
							</div>
							<div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>								
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/notificacio/new"/>"><spring:message code="decorator.menu.altanotificacio"/></a></li>								
									<li><a href="<c:url value="/procediment"/>"><spring:message code="decorator.menu.procediment"/></a></li>
									<li class="divider"></li>
									<li><a href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
									<li><a href="<c:url value="/pagadorPostal"/>"><spring:message code="decorator.menu.pagadorpostal"/></a></li>
									<li><a href="<c:url value="/pagadorCie"/>"><spring:message code="decorator.menu.pagadorcie"/></a></li>
								</ul>
							</div>
							</c:if>
						</div>
						<%-- <c:choose> --%>
							<%-- <c:when test="${isRolActualAdministrador}"> --%>
							<%-- <div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.monitoritzar"/>&nbsp;<span class="caret caret-white"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/integracio"/>"><spring:message code="decorator.menu.integracions"/></a></li>
									<li><a href="<c:url value="/excepcio"/>"><spring:message code="decorator.menu.excepcions"/></a></li>
								</ul>
							</div> --%>
							<%-- </c:when> --%>
						<%-- </c:choose> --%>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h2>
					<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
					<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
					<decorator:title />
					<small><decorator:getProperty property="meta.subtitle"/></small>
					
				</h2>
			</div>
			<div class="panel-body">
				<div id="contingut-missatges"><not:missatges/></div>
    			<decorator:body />
			</div>
		</div>
	</div>
    <div class="container container-foot">
    	<div class="pull-left app-version"><p>NOTIB v<not:versio/></p></div>
        <div class="pull-right govern-footer">
        	<p>
	        	<img src="<c:url value="/img/govern-logo-neg.png"/>" hspace="5" height="30" alt="<spring:message code='decorator.logo.govern'/>" />
	        	<img src="<c:url value="/img/una_manera.png"/>" 	 hspace="5" height="30" alt="<spring:message code='decorator.logo.manera'/>" />
	        	<img src="<c:url value="/img/feder7.png"/>" 	     hspace="5" height="35" alt="<spring:message code='decorator.logo.feder'/>" />
	        	<img src="<c:url value="/img/uenegroma.png"/>"	     hspace="5" height="50" alt="<spring:message code='decorator.logo.ue'/>" />
        	</p>
        </div>
    </div>
</body>
</html>

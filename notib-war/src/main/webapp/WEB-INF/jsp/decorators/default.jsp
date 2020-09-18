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
			"isRolActualAdministradorOrgan",
			es.caib.notib.war.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(request));
	pageContext.setAttribute(
			"requestParameterCanviRol",
			es.caib.notib.war.helper.RolHelper.getRequestParameterCanviRol());
	pageContext.setAttribute(
			"permisNotificacio",
			request.getAttribute("permisNotificacio"));
	pageContext.setAttribute(
			"sessionOrgans",
			es.caib.notib.war.helper.OrganGestorHelper.getOrgansGestorsUsuariActual(request));
	pageContext.setAttribute(
			"organActual",
			es.caib.notib.war.helper.OrganGestorHelper.getOrganGestorUsuariActual(request));
	pageContext.setAttribute(
			"requestParameterCanviOrgan",
			es.caib.notib.war.helper.OrganGestorHelper.getRequestParameterCanviOrgan());
	
//	pageContext.setAttribute(
//			"versioMajorActual",
//			es.caib.notib.war.helper.AplicacioHelper.getVersioMajorActual(request));
		
%>
<c:set var="hiHaEntitats" value="${fn:length(sessionEntitats) > 0}"/>
<c:set var="hiHaMesEntitats" value="${fn:length(sessionEntitats) > 1}"/>
<c:set var="hiHaOrgans" value="${fn:length(sessionOrgans) > 0}"/>
<c:set var="hiHaMesOrgans" value="${fn:length(sessionOrgans) > 1}"/>
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
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />
	
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-colorpicker.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-colorpicker.min.js"/>"></script>
	<decorator:head />
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.png"/>);
	color:#666666;
	padding-top: 120px;
}
.modal-body {
	height: auto !important;
}
.container-custom {
	width: 96%;
}
<c:choose>
	<c:when test="${sessionScope['EntitatHelper.entitatActual'].colorFons!=null  && not empty sessionScope['EntitatHelper.entitatActual'].colorFons}">
		.navbar-app {
			background-color: ${sessionScope['EntitatHelper.entitatActual'].colorFons} !important;
		}
		.navbar-app .list-inline li.dropdown>a {
			background-color: ${sessionScope['EntitatHelper.entitatActual'].colorFons} !important;
		}
	</c:when>
	<c:otherwise>
		<c:if test="${sessionScope['SessionHelper.capsaleraColorFons']!=null  && not empty sessionScope['SessionHelper.capsaleraColorFons']}">
			.navbar-app {
				background-color: ${sessionScope['SessionHelper.capsaleraColorFons']} !important;
			}		
			.navbar-app .list-inline li.dropdown>a { 
				background-color: ${sessionScope['SessionHelper.capsaleraColorFons']} !important;
			}
		</c:if>		
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${sessionScope['EntitatHelper.entitatActual'].colorLletra!=null  && not empty sessionScope['EntitatHelper.entitatActual'].colorLletra}">
		.navbar-app .list-inline li.dropdown>a {
			color: ${sessionScope['EntitatHelper.entitatActual'].colorLletra};
		}
		.caret-white {
			border-top-color: ${sessionScope['EntitatHelper.entitatActual'].colorLletra} !important;
		}
		.list-inline.pull-right {
			color: ${sessionScope['EntitatHelper.entitatActual'].colorLletra} !important;
		}
	</c:when>
	<c:otherwise>
		<c:if test="${sessionScope['SessionHelper.capsaleraColorLletra']!=null  && not empty sessionScope['SessionHelper.capsaleraColorLletra']}">
			.navbar-app .list-inline li.dropdown>a {
				color: ${sessionScope['SessionHelper.capsaleraColorLletra']};
			}	
			.caret-white {
				border-top-color: ${sessionScope['SessionHelper.capsaleraColorLletra']} !important;
			}	
			.list-inline.pull-right {
				color: ${sessionScope['SessionHelper.capsaleraColorLletra']} !important;
			}
		</c:if>		
	</c:otherwise>
</c:choose>

.panel-heading.processarButton {
	height: 65px;
}
.btn-processar {
	margin-top: 10px;
}
</style>
</head>
<body>

	<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container container-custom">
			<div class="navbar-header">
				<%--button class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button--%>
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<c:choose>
							<c:when test="${sessionScope['SessionHelper.capsaleraCapLogo']!=null  && not empty sessionScope['SessionHelper.capsaleraCapLogo'] || sessionScope['EntitatHelper.entitatActual'].logoCapBytes!=null && fn:length(sessionScope['EntitatHelper.entitatActual'].logoCapBytes)!=0}">
								<img src="<c:url value="/entitat/getEntitatLogoCap"/>"  height="65" alt="Govern de les Illes Balears" />
							</c:when>
							<c:otherwise>
								<img src="<c:url value="/img/govern-logo.png"/>"  height="65" alt="Govern de les Illes Balears" />
							</c:otherwise>
						</c:choose>
					</div>
					<div id="app-logo" class="pull-left">
						<img src="<c:url value="/img/logo.png"/>" alt="NOTIB" />
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right">
					
						<c:if test="${hiHaEntitats && isRolActualAdministradorEntitat || isRolActualUsuari || isRolActualAdministradorOrgan}">
							<li class="dropdown">
								<c:if test="${hiHaMesEntitats}"><a href="#" data-toggle="dropdown"></c:if>
		         				<span class="fa fa-home"></span> <span class="truncate" title="${entitatActual.nom}">${entitatActual.nom}</span> <c:if test="${hiHaMesEntitats}"><b class="caret caret-white"></b></c:if>
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
						
						<c:if test="${hiHaOrgans && isRolActualAdministradorOrgan}">
							<li class="dropdown">
								<c:if test="${hiHaMesOrgans}"><a href="#" data-toggle="dropdown"></c:if>
		         				<span class="fa fa-cubes"></span> <span class="truncate" title="${organActual.nom}">${organActual.nom}</span> <c:if test="${hiHaMesOrgans}"><b class="caret caret-white"></b></c:if></span>
								<c:if test="${hiHaMesOrgans}"></a></c:if>
								<c:if test="${hiHaMesOrgans}">
									<ul class="dropdown-menu">
										<c:forEach var="organ" items="${sessionOrgans}" varStatus="status">
											<c:if test="${organ.id != organActual.id}">
											
												<c:url var="urlCanviOrgan" value="">
													<c:param name="${requestParameterCanviOrgan}" value="${organ.id}"/>
												</c:url>
												<li><a href="${urlCanviOrgan}">${organ.nom}</a></li>
												
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
					
						
						
						<li class="dropdown">
							<a href="#" data-toggle="dropdown">
								<span class="fa fa-user"></span>
								<c:choose>
									<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom}</c:when>
									<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
								</c:choose>
								<span class="caret caret-white"></span>
							</a>
							<ul class="dropdown-menu">
								<li>
									<a href="<c:url value="/usuari/configuracio"/>" data-toggle="modal" data-maximized="true">
										<spring:message code="decorator.menu.configuracio.user"/>
									</a>
								</li>
							</ul>
						</li>
				
					</ul>
					<div class="clearfix"></div>
					<div class="btn-toolbar navbar-btn navbar-right">
						<c:if test="${isRolActualUsuari and permisNotificacio}">
							<div class="btn-group">
								<a data-toggle="modal" class="btn btn-primary" href="<c:url value="/notificacio/procediments"/>"><span class="fa fa-plus"></span>&nbsp;<spring:message code="decorator.menu.altanotificacio"/></a>
							</div>
						</c:if>
						<div class="btn-group">
							<c:if test="${isRolActualAdministrador}">
<%-- 
							<div class="btn-group">
								<a href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>							
							</div>
--%>							
							<div class="btn-group">
									<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.monitoritzar"/>&nbsp;<span class="caret caret-white"></span></button>
									<ul class="dropdown-menu">
										<li><a href="<c:url value="/massiu/notificacions"/>"><spring:message code="decorator.menu.callback"/></a></li>							
										<li><a href="<c:url value="/integracio"/>"><spring:message code="decorator.menu.integracions"/></a></li>
										<li><a href="<c:url value="/excepcio"/>"><spring:message code="decorator.menu.excepcions"/></a></li>
										<li><a href="<c:url value="/metrics/list"/>"><spring:message code="decorator.menu.metriques"/></a></li>
									</ul>
								</div>
							<div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/entitat"/>"><spring:message code="decorator.menu.entitats"/></a></li>
									<li><a href="<c:url value="/cache"/>"><spring:message code="decorator.menu.caches"/></a></li>
								</ul>
							</div>
							
							</c:if>
							<c:if test="${isRolActualUsuari}">
							
<%-- 								<c:if test="${permisNotificacio}"> --%>
<!-- 									<div class="btn-group"> -->
<%-- 										<a data-toggle="modal" class="btn btn-primary" href="<c:url value="/notificacio/procediments"/>"><span class="fa fa-plus"></span>&nbsp;<spring:message code="decorator.menu.altanotificacio"/></a> --%>
<!-- 									</div> -->
<%-- 								</c:if> --%>
									<div class="btn-group">
										<a href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
									</div>
									<div class="btn-group">
										<a href="<c:url value="/enviament"/>" class="btn btn-primary"><spring:message code="decorator.menu.enviaments"/></a>
									</div>
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
									<li><a href="<c:url value="/entitat/${entitatActual.id}"/>"><spring:message code="decorator.menu.entitat"/></a></li>
									<li><a href="<c:url value="/entitat/${entitatActual.id}/permis"/>"><spring:message code="decorator.menu.entitat.permisos"/></a></li>
									<li><a href="<c:url value="/entitat/${entitatActual.id}/aplicacio"/>"><spring:message code="decorator.menu.entitat.aplicacions"/></a></li>
									<li class="divider"></li>
									<li><a href="<c:url value="/procediment"/>"><spring:message code="decorator.menu.procediment"/></a></li>
									<li><a href="<c:url value="/organgestor"/>"><spring:message code="decorator.menu.organGestor"/></a></li>
									<li class="divider"></li>
									<li><a href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
									<li><a href="<c:url value="/pagadorPostal"/>"><spring:message code="decorator.menu.pagadorpostal"/></a></li>
									<li><a href="<c:url value="/pagadorCie"/>"><spring:message code="decorator.menu.pagadorcie"/></a></li>
								</ul>
							</div>
							</c:if>
							<c:if test="${isRolActualAdministradorOrgan}">
							<div class="btn-group">
								<a href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
							</div>
							<div class="btn-group">
								<a href="<c:url value="/enviament"/>" class="btn btn-primary"><spring:message code="decorator.menu.enviaments"/></a>
							</div>
							<div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>								
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/procediment"/>"><spring:message code="decorator.menu.procediment"/></a></li>
									<li><a href="<c:url value="/organgestor"/>"><spring:message code="decorator.menu.organGestor"/></a></li>
									<%--<li class="divider"></li>--%>
									<li><a href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
									<%--<li><a href="<c:url value="/pagadorPostal"/>"><spring:message code="decorator.menu.pagadorpostal"/></a></li>  --%>
									<%--<li><a href="<c:url value="/pagadorCie"/>"><spring:message code="decorator.menu.pagadorcie"/></a></li> --%>
								</ul>
							</div>
							</c:if>
						</div>
						<div class="btn-group">
							<a class="btn btn-success" href="https://github.com/GovernIB/notib/raw/${manifestAtributes['Implementation-SCM-Branch']}/doc/pdf/NOTIB_usuari.pdf" rel="noopener noreferrer" target="_blank"><span class="fa fa-download"></span> <spring:message code="decorator.menu.manual.usuari"/></a>
<!-- 									Per a diferents rol, ara sol esta el manual d'usuari -->
<%-- 									<a class="btn btn-primary" href="https://github.com/GovernIB/notib/raw/notib-${versioMajorActual}/doc/pdf/NOTIB_${rolActual}.pdf" } download/><spring:message code="decorator.menu.manual.usuari"/></a> --%>
						</div>
							
					</div>
					
					
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main container-custom">
		<div class="panel panel-default">
				<c:choose>
					<c:when test="${notificacio.permisProcessar}">
						<div class="panel-heading processarButton">
							<h2 class="col-md-8">
								<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
								<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
								<decorator:title />
								<small><decorator:getProperty property="meta.subtitle"/></small>
							</h2>
							<a href="<c:url value="/notificacio/${notificacio.id}/processar"/>"  class="btn btn-info pull-right btn-processar"  data-toggle="modal" data-modal-id="modal-processar"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a>
						</div>
					</c:when>
					<c:otherwise>
						<div class="panel-heading">
							<h2>
								<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
								<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
								<decorator:title />
								<small><decorator:getProperty property="meta.subtitle"/></small>
							</h2>
						</div>
					</c:otherwise>
				</c:choose>
			<div class="panel-body">
				<div id="contingut-missatges"><not:missatges/></div>
    			<decorator:body />
			</div>
		</div>
	</div>
    <div class="container container-foot container-custom">
    	<div class="pull-left app-version"><p>NOTIB v<not:versio/></p></div>
        <div class="pull-right govern-footer">
        	<p>
        		<c:choose>
	        		<c:when test="${sessionScope['SessionHelper.capsaleraPeuLogo']!=null  && not empty sessionScope['SessionHelper.capsaleraPeuLogo'] || sessionScope['EntitatHelper.entitatActual'].logoPeuBytes!=null && fn:length(sessionScope['EntitatHelper.entitatActual'].logoPeuBytes)!=0}">
						<img src="<c:url value="/entitat/getEntitatLogoPeu"/>"  height="65" alt="Govern de les Illes Balears" />
					</c:when>
					<c:otherwise>
						<img src="<c:url value="/img/govern-logo-neg.png"/>" hspace="5" height="30" alt="<spring:message code='decorator.logo.govern'/>" />
					</c:otherwise>
				</c:choose>
	        	<img src="<c:url value="/img/una_manera.png"/>" 	 hspace="5" height="30" alt="<spring:message code='decorator.logo.manera'/>" />
	        	<img src="<c:url value="/img/feder7.png"/>" 	     hspace="5" height="35" alt="<spring:message code='decorator.logo.feder'/>" />
	        	<img src="<c:url value="/img/uenegroma.png"/>"	     hspace="5" height="50" alt="<spring:message code='decorator.logo.ue'/>" />
        	</p>
        </div>
    </div>
     <div class="divider"></div>	

</body>
</html>

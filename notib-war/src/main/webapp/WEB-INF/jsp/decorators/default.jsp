
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
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request),
			PageContext.REQUEST_SCOPE);
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
			"permisNotificacioComunicacioMenu",
			request.getAttribute("permisNotificacioComunicacioMenu"));
	pageContext.setAttribute(
			"permisComunicacioSirMenu",
			request.getAttribute("permisComunicacioSirMenu"));
	pageContext.setAttribute(
			"sessionOrgans",
			es.caib.notib.war.helper.OrganGestorHelper.getOrgansGestorsUsuariActual(request));
	pageContext.setAttribute(
			"organActual",
			es.caib.notib.war.helper.OrganGestorHelper.getOrganGestorUsuariActual(request));
	pageContext.setAttribute(
			"requestParameterCanviOrgan",
			es.caib.notib.war.helper.OrganGestorHelper.getRequestParameterCanviOrgan());
	pageContext.setAttribute(
			"avisos",
			es.caib.notib.war.helper.AvisHelper.getAvisos(request));
	pageContext.setAttribute(
			"organsProcNoSincronitzats",
			es.caib.notib.war.helper.OrganGestorHelper.getOrgansProcNoSincronitzats(request));
	pageContext.setAttribute(
			"organsServNoSincronitzats",
			es.caib.notib.war.helper.OrganGestorHelper.getOrgansServNoSincronitzats(request));
	
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
<%--	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>--%>
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
			    url: '/notib/usuari/configuracio/idioma',  
			    success: function(data) {
			    	userLanguage =  data; 
			    }
			  });
	    }
		setIdioma();

		function bloquejar() {
			// Bloquejar la pantalla
			// var height = $(".panel-body").css('height');
			// var width = $(".panel-body").css('width');
			// var top = $(".panel-body").css('top');
			// $(".loading-screen").css('height', height);
			// $(".loading-screen").css('width', width);
			// $(".loading-screen").css('top', top);
			$("#spinner-container").removeClass("ocult");
			$("#spinner-container").addClass("visible");
			// $(".loading-screen").show();
		}

	</script>
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

.ocult {
	display: none;
}

.visible {
	display: flex;
	justify-content: center;
	flex-direction: column;
}

.loading-screen {
	background-color: rgba(0,0,0,0.4);
	position:absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 9999;
}

.spin-box {
	display:flex;
	justify-content: center;
}

</style>
</head>
<body>

	<div id="spinner-container" class="loading-screen ocult">
		<div class="spin-box">
				<span class="fa fa-spin fa-circle-o-notch  fa-3x"></span>
		</div>
	</div>

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
						<div id="text-logo-div"><span id="text-logo">NOTIB</span></div>
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right">
					
						<c:if test="${hiHaEntitats && isRolActualAdministradorEntitat || isRolActualUsuari || isRolActualAdministradorOrgan}">
							<li class="dropdown">
								<c:if test="${hiHaMesEntitats}"><a id="dd_entitat" href="#" data-toggle="dropdown"></c:if>
		         				<span class="fa fa-home"></span> <span id="dds_entitat" class="truncate" title="${entitatActual.nom}">${entitatActual.nom}</span> <c:if test="${hiHaMesEntitats}"><b class="caret caret-white"></b></c:if>
								<c:if test="${hiHaMesEntitats}"></a></c:if>
								<c:if test="${hiHaMesEntitats}">
									<ul id="ddo_entitat" class="dropdown-menu">
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
								<c:if test="${hiHaMesOrgans}"><a id="dd_organ" href="#" data-toggle="dropdown"></c:if>
		         				<span class="fa fa-cubes"></span> <span id="dds_organ" class="truncate" title="${organActual.nom}">${organActual.nom}</span> <c:if test="${hiHaMesOrgans}"><b class="caret caret-white"></b></c:if></span>
								<c:if test="${hiHaMesOrgans}"></a></c:if>
								<c:if test="${hiHaMesOrgans}">
									<ul id="ddo_organ" class="dropdown-menu">
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
									<a id="dd_rol" href="#" data-toggle="dropdown">
										<span class="fa fa-bookmark"></span>
										<span id="dds_rol" data-rol="${rolActual}"><spring:message code="decorator.menu.rol.${rolActual}"/></span>
										<b class="caret caret-white"></b>
									</a>
									<ul id="ddo_rol" class="dropdown-menu">
										<c:forEach var="rol" items="${rolsUsuariActual}">
											<c:if test="${rol != rolActual}">
												<li>
													<c:url var="canviRolUrl" value="/index">
														<c:param name="${requestParameterCanviRol}" value="${rol}"/>
													</c:url>
													<a id="mr_${rol}" href="${canviRolUrl}"><spring:message code="decorator.menu.rol.${rol}"/></a>
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
							<a id="dd_user" href="#" data-toggle="dropdown">
								<span class="fa fa-user"></span>
								<c:choose>
									<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom}</c:when>
									<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
								</c:choose>
								<span class="caret caret-white"></span>
							</a>
							<ul class="dropdown-menu">
								<li>
									<a id="mu_config" href="<c:url value="/usuari/configuracio"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><spring:message code="decorator.menu.configuracio.user"/></a>
									<a id="mu_manual" href="https://github.com/GovernIB/notib/raw/${manifestAtributes['Implementation-SCM-Branch']}/doc/pdf/NOTIB_usuari.pdf" rel="noopener noreferrer" target="_blank"><span class="fa fa-download"></span> <spring:message code="decorator.menu.manual.usuari"/></a>
								</li>
								<li>
									<a id="mu_logout" href="<c:url value="/usuari/logout"/>">
										<i class="fa fa-power-off"></i> <spring:message code="decorator.menu.accions.desconectar"/>
									</a>
								</li>
							</ul>
						</li>
				
					</ul>
					<div class="clearfix"></div>
					<div class="btn-toolbar navbar-btn navbar-right">
						<div class="btn-group">

							<c:choose>
								<c:when test="${isRolActualAdministrador}">
									<div class="btn-group">
											<button id="m_monitor" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.monitoritzar"/>&nbsp;<span class="caret caret-white"></span></button>
											<ul class="dropdown-menu">
												<li><a id="mo_massiu" href="<c:url value="/massiu/notificacions"> <c:param name="mantenirPaginacio" value="true"/></c:url>"><spring:message code="decorator.menu.callback"/></a></li>
												<li><a id="mo_integracions" href="<c:url value="/integracio"/>"><spring:message code="decorator.menu.integracions"/></a></li>
												<li><a id="mo_excepcions" href="<c:url value="/excepcio"/>"><spring:message code="decorator.menu.excepcions"/></a></li>
												<li><a id="mo_metriques" href="<c:url value="/metrics/list"/>"><spring:message code="decorator.menu.metriques"/></a></li>
												<li><a id="mo_monitor" data-toggle="modal" data-maximized="true" id="botoMonitor" href="<c:url value="/modal/monitor"/>"><spring:message code='monitor.titol' /></a></li>
											</ul>
										</div>
									<div class="btn-group">
										<button id="m_conf" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>
										<ul class="dropdown-menu">
											<li><a id="mc_entitats" href="<c:url value="/entitat"/>"><spring:message code="decorator.menu.entitats"/></a></li>
											<li><a id="mc_caches" href="<c:url value="/cache"/>"><spring:message code="decorator.menu.caches"/></a></li>
											<li><a id="mc_restats" href="<c:url value="/notificacio/refrescarEstatNotifica"/>" title="<spring:message code="decorator.menu.expirades.ajuda"/>" data-toggle="modal" data-height="350px"><spring:message code="decorator.menu.expirades"/> </a></li>
											<li><a id="mc_propietats" href="<c:url value="/config"/>" title="<spring:message code="decorator.menu.config.properties"/>"> <spring:message code="decorator.menu.config.properties"/></a></li>
											<li><a id="mc_scheduler" href="<c:url value="/api/scheduling/restart"/>" title="<spring:message code="decorator.menu.reinici.scheduler"/>"> <spring:message code="decorator.menu.reinici.scheduler"/></a></li>
										</ul>
									</div>
									<a id="ma_avisos" href="<c:url value="/avis"/>" class="btn btn-primary"><spring:message code="decorator.menu.avisos"/></a>
								</c:when>

								<c:when test="${isRolActualUsuari}">
									<div class="btn-group">
										<div class="btn-group">
											<button id="m_env" data-toggle="dropdown" class="btn btn-primary dropdown-toggle">
												<span class="fa fa-plus"></span>&nbsp;<spring:message code="decorator.menu.alta.enviament"/>&nbsp;<span class="caret caret-white"></span>
											</button>
											<ul class="dropdown-menu">
												<c:if test="${permisNotificacioComunicacioMenu}">
													<li><a id="me_notificacio" href="<c:url value="/notificacio/new/notificacio"/>"><spring:message code="decorator.menu.alta.enviament.notificacio"/></a></li>
													<li><a id="me_comunicacio" href="<c:url value="/notificacio/new/comunicacio"/>"><spring:message code="decorator.menu.alta.enviament.comunicacio"/></a></li>
												</c:if>
												<c:if test="${permisComunicacioSirMenu}">
													<li><a id="me_sir" href="<c:url value="/notificacio/new/comunicacioSIR"/>"><spring:message code="decorator.menu.alta.enviament.comunicacio.sir"/></a></li>
												</c:if>
											</ul>
										</div>
									</div>
									<div class="btn-group">
										<button id="m_massiu" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.notificacio.massiva"/>&nbsp;<span class="caret caret-white"></span></button>
										<ul class="dropdown-menu">
											<li><a id="mm_enviament" href="<c:url value="/notificacio/massiva/new"/>"><spring:message code="decorator.menu.notificacio.massiva.nova"/></a></li>
											<li><a id="mm_consulta" href="<c:url value="/notificacio/massiva/"/>"><spring:message code="decorator.menu.notificacio.massiva.consulta"/></a></li>
										</ul>
									</div>
									<div class="btn-group">
										<a id="ml_notificacio" href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
									</div>
									<div class="btn-group">
										<a id="ml_enviament" href="<c:url value="/enviament"><c:param name="mantenirPaginacio" value="false"/></c:url>" class="btn btn-primary"><spring:message code="decorator.menu.enviaments"/></a>
									</div>
								</c:when>

								<c:when test="${isRolActualAdministradorEntitat}">
									<div class="btn-group">
										<a id="ml_notificacio" href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
									</div>
									<div class="btn-group">
										<a id="ml_enviament" href="<c:url value="/enviament"/>" class="btn btn-primary"><spring:message code="decorator.menu.enviaments"/></a>
									</div>
									<div class="btn-group">
										<button id="m_gestio" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.gestio"/>&nbsp;<span class="caret caret-white"></span></button>
										<ul class="dropdown-menu">
											<li><a id="mg_nerror" href="<c:url value="/massiu/registre/notificacionsError"/>"><spring:message code="decorator.menu.massiu.registre"/></a></li>
											<li><a id="mg_massiu" href="<c:url value="/notificacio/massiva/"/>"><spring:message code="decorator.menu.notificacio.massiva.consulta"/></a></li>
										</ul>
									</div>

									<div class="btn-group">
										<button id="m_conf" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>
										<ul class="dropdown-menu">
											<li><a id="mc_entitat" href="<c:url value="/entitat/${entitatActual.id}"/>"><spring:message code="decorator.menu.entitat"/></a></li>
											<li><a id="mc_permisos" href="<c:url value="/entitat/${entitatActual.id}/permis"/>"><spring:message code="decorator.menu.entitat.permisos"/></a></li>
											<li><a id="mc_aplicacions" href="<c:url value="/entitat/${entitatActual.id}/aplicacio"/>"><spring:message code="decorator.menu.entitat.aplicacions"/></a></li>
											<li class="divider"></li>
											<li><a id="mc_procediments" href="<c:url value="/procediment"/>"><spring:message code="decorator.menu.procediment"/><c:if test="${organsProcNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='procediment.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsProcNoSincronitzats}</span></c:if></a></li>
											<li><a id="mc_serveis" href="<c:url value="/servei"/>"><spring:message code="decorator.menu.servei"/><c:if test="${organsServNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='servei.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsServNoSincronitzats}</span></c:if></a></li>
											<li><a id="mc_organs" href="<c:url value="/organgestorArbre"/>"><spring:message code="decorator.menu.organGestor"/></a></li>
											<li class="divider"></li>
											<li><a id="mc_grups" href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
											<li class="divider"></li>
											<li><a id="mc_postals" href="<c:url value="/operadorPostal"/>"><spring:message code="decorator.menu.operadorpostal"/></a></li>
											<li><a id="mc_cies" href="<c:url value="/cie"/>"><spring:message code="decorator.menu.operadorcie"/></a></li>
										</ul>
									</div>
								</c:when>

								<c:when test="${isRolActualAdministradorOrgan}">
									<div class="btn-group">
										<a id="ml_notificacio" href="<c:url value="/notificacio"/>" class="btn btn-primary"><spring:message code="decorator.menu.notificacions"/></a>
									</div>
									<div class="btn-group">
										<a id="ml_enviament" href="<c:url value="/enviament"/>" class="btn btn-primary"><spring:message code="decorator.menu.enviaments"/></a>
									</div>
									<div class="btn-group">
										<button id="m_conf" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.config"/>&nbsp;<span class="caret caret-white"></span></button>
										<ul class="dropdown-menu">
											<li><a id="mc_procediments" href="<c:url value="/procediment"/>"><spring:message code="decorator.menu.procediment"/><c:if test="${organsProcNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='procediment.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsProcNoSincronitzats}</span></c:if></a></li>
											<li><a id="mc_serveis" href="<c:url value="/servei"/>"><spring:message code="decorator.menu.servei"/><c:if test="${organsServNoSincronitzats > 0}"><span class="badge small" title="<spring:message code='servei.actualitzacio.organs.no.sync'/>" style="background-color: #a94442; float: right;">${organsServNoSincronitzats}</span></c:if></a></li>
											<li><a id="mc_organs" href="<c:url value="/organgestor"/>"><spring:message code="decorator.menu.organGestor"/></a></li>
											<li><a id="mc_grups" href="<c:url value="/grup"/>"><spring:message code="decorator.menu.grups"/></a></li>
										</ul>
									</div>
								</c:when>
							</c:choose>
						</div>
<%--
						<div class="btn-group">
							<a class="btn btn-success" href="https://github.com/GovernIB/notib/raw/${manifestAtributes['Implementation-SCM-Branch']}/doc/pdf/NOTIB_usuari.pdf" rel="noopener noreferrer" target="_blank"><span class="fa fa-download"></span> <spring:message code="decorator.menu.manual.usuari"/></a>
<!-- 									Per a diferents rol, ara sol esta el manual d'usuari -->
<!-- 									<a class="btn btn-primary" href="https://github.com/GovernIB/notib/raw/notib-${versioMajorActual}/doc/pdf/NOTIB_${rolActual}.pdf" } download/><spring:message code="decorator.menu.manual.usuari"/></a> -->
						</div>
--%>
							
					</div>
					
					
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main container-custom">
	
		<c:if test="${not empty avisos and not desactivarAvisos}">
			<div id="accordion">
				<c:forEach var="avis" items="${avisos}" varStatus="status">
						<div class="card avisCard ${avis.avisNivell == 'INFO' ? 'avisCardInfo':''} ${avis.avisNivell == 'WARNING' ? 'avisCardWarning':''} ${avis.avisNivell == 'ERROR' ? 'avisCardError':''}">
	
							<div data-toggle="collapse" data-target="#collapse${status.index}" class="card-header avisCardHeader">
								${avis.avisNivell == 'INFO' ? '<span class="fa fa-info-circle text-info"></span>':''} ${avis.avisNivell == 'WARNING' ? '<span class="fa fa-exclamation-triangle text-warning"></span>':''} ${avis.avisNivell == 'ERROR' ? '<span class="fa fa-warning text-danger"></span>':''} ${avis.assumpte}
							<button class="btn btn-default btn-xs pull-right"><span class="fa fa-chevron-down "></span></button>										
							</div>
	
							<div id="collapse${status.index}" class="collapse" data-parent="#accordion">
								<div class="card-body avisCardBody" >${avis.missatge}</div>
							</div>
						</div>
				</c:forEach>
			</div>
		</c:if>
		
		<div class="panel panel-default">
<%--				<c:choose>--%>
<%--					<c:when test="${notificacio.permisProcessar}">--%>
<%--						<div class="panel-heading processarButton">--%>
<%--							<h2 class="col-md-8">--%>
<%--								<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>--%>
<%--								<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>--%>
<%--								<decorator:title />--%>
<%--								<small><decorator:getProperty property="meta.subtitle"/></small>--%>
<%--							</h2>--%>
<%--							<a href="<c:url value="/notificacio/${notificacio.id}/processar"/>"  class="btn btn-info pull-right btn-processar"  data-toggle="modal" data-modal-id="modal-processar"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a>--%>
<%--						</div>--%>
<%--					</c:when>--%>
<%--					<c:otherwise>--%>
						<div class="panel-heading">
							<h2>
								<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
								<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
								<decorator:title />
								<small><decorator:getProperty property="meta.subtitle"/></small>
							</h2>
						</div>
<%--					</c:otherwise>--%>
<%--				</c:choose>--%>
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

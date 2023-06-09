<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(ssc.getRolActual()));
%>
<html>
<head>
	<title><spring:message code="procediment.permis.titol"/></title>
	<meta name="subtitle" content="${procediment.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<style>
		table {table-layout:fixed;}
		/*th {height:120px;}*/
		th:first-child {width: 60px !important;}
		/*th:nth-child(2) {width: 200px !important;}*/
		td {overflow:hidden; text-overflow: ellipsis;}
		td:last-child {text-align: right; overflow: visible;}
		#taulaDades th {writing-mode:vertical-rl;}
		.writing-horitzontal {writing-mode: horizontal-tb !important;}
		.nom-principal {width: 40% !important;}
		.col-organ {width: 20% !important;}
		.th-checkbox {padding-right: 1px !important; max-width: 5px !important;}
		.th-boto-accions {width: 100px !important;}
	</style>
</head>
<body>
	<table 
		id="taulaDades" 
		data-toggle="datatable" 
		data-url="<c:url value="/procediment/${procediment.id}/permis/datatable"/>"
		data-search-enabled="false" 
		data-paging-enabled="false" 
		data-default-order="1" 
		data-default-dir="asc"
		data-botons-template="#tableButtonsTemplate" 
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="tipus" data-renderer="enum(TipusEnumDto)" class="writing-horitzontal"><spring:message code="procediment.permis.columna.tipus"/></th>
				<th data-col-name="nomSencerAmbCodi" class="writing-horitzontal nom-principal"><spring:message code="procediment.permis.columna.principal"/></th>
				<th data-col-name="organ" data-visible="false">#</th>
				<th data-col-name="organCodiNom" class="writing-horitzontal col-organ"><spring:message code="procediment.permis.columna.organ"/></th>
				<th data-col-name="read" data-template="#cellPermisReadTemplate" class="th-checkbox" title="<spring:message code="procediment.permis.form.camp.consulta"/>: &#10;<spring:message code="procediment.permis.consulta.info"/>">
					<span class="fa fa-search padding-icon"></span>
<%--					<spring:message code="procediment.permis.columna.consulta"/>--%>
					<script id="cellPermisReadTemplate" type="text/x-jsrender">
						{{if read}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="processar" data-template="#cellPermisProcessarTemplate" class="th-checkbox" title="<spring:message code="procediment.permis.form.camp.processar"/>: &#10;<spring:message code="procediment.permis.processar.info"/>">
					<span class="fa fa-check-square-o padding-icon"></span>
<%--					<spring:message code="procediment.permis.columna.processar"/>--%>
					<script id="cellPermisProcessarTemplate" type="text/x-jsrender">
						{{if processar}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="administration" data-template="#cellPermisGestioTemplate" class="th-checkbox" title="<spring:message code="procediment.permis.form.camp.gestio"/>: &#10;<spring:message code="procediment.permis.gestio.info"/>">
					<span class="fa fa-cog padding-icon"></span>
<%--					<spring:message code="procediment.permis.columna.gestio"/>--%>
					<script id="cellPermisGestioTemplate" type="text/x-jsrender">
						{{if administration}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="notificacio" data-template="#cellPermisNotificacioTemplate" class="th-checkbox" title="<spring:message code="procediment.permis.form.camp.notificacio"/>: &#10;<spring:message code="procediment.permis.notificacio.info"/>">
					<span class="fa fa-gavel padding-icon"></span>
<%--					<spring:message code="procediment.permis.columna.notificacio"/>--%>
					<script id="cellPermisNotificacioTemplate" type="text/x-jsrender">
						{{if notificacio}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="comunicacio" data-template="#cellPermisComunicacioTemplate" class="th-checkbox" title="<spring:message code="procediment.permis.form.camp.comunicacio"/>: &#10;<spring:message code="procediment.permis.comunicacio.info"/>">
					<span class="fa fa-envelope-o padding-icon"></span>
<%--					<spring:message code="procediment.permis.columna.comunicacio"/>--%>
					<script id="cellPermisComunicacioTemplate" type="text/x-jsrender">
						{{if comunicacio}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="comunicacioSir" data-template="#cellPermisComunicacioSirTemplate" class="th-checkbox" title="<spring:message code="procediment.permis.form.camp.comunicacio.sir"/>: &#10;<spring:message code="procediment.permis.comunicacio.sir.info"/>">
					<span class="fa fa-envelope padding-icon"></span>
<%--					<spring:message code="procediment.permis.columna.comunicacio.sir"/>--%>
					<script id="cellPermisComunicacioSirTemplate" type="text/x-jsrender">
						{{if comunicacioSir}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>

				<th data-col-name="permetEdicio" data-visible="false">#</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" class="th-boto-accions">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						{{if permetEdicio}}
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu dropdown-menu-right">
								{{if organ}}
								<li><a href="../../procediment/${procediment.id}/organ/{{:organ}}/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="../../procediment/${procediment.id}/organ/{{:organ}}/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{else}}
								<li><a href="../../procediment/${procediment.id}/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="../../procediment/${procediment.id}/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="procediment.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
						{{/if}}
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="../../procediment/${procediment.id}/permis/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="procediment.permis.boto.nou.permis"/></a></p>
	</script>
	<a href="<c:url value="/procediment?mantenirPaginacio=true"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
	<div class="clearfix"></div>
</body>
</html>

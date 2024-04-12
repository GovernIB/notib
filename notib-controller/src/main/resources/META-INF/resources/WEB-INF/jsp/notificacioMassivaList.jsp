<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()));
	pageContext.setAttribute("isRolActualUsuari", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuari(ssc.getRolActual()));
	pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()));
	pageContext.setAttribute("isRolActualAdministradorOrgan", es.caib.notib.back.helper.RolHelper.isUsuariActualUsuariAdministradorOrgan(ssc.getRolActual()));
%>
<html>
<head>
	<title><spring:message code="notificacio.massiva.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
<style type="text/css">
	#nomesAmbErrorsBtn {margin-right: 10%;}
	.label-warning {background-color: #e67e22;}
	.estat_info {font-size: 9px}
	.btn-dwl {padding: 2px 6px;}
</style>
<script type="text/javascript">

	var notificacioMassivaEstats = [];
	<c:forEach var="estat" items="${notificacioMassivaEstats}">
	notificacioMassivaEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
	</c:forEach>

	$(document).ready(function() {
		$('#btnNetejar').click(function () {
			$(':input', $('#filtre')).each(function () {
				var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea') {
					this.value = '';
				} else if (type == 'checkbox' || type == 'radio') {
					this.checked = false;
				} else if (tag == 'select') {
					this.selectedIndex = 0;
				}

			});
			$('#nomesAmbErrorsBtn').removeClass('active');
			$('#nomesAmbErrors').val(false);
			$('#form-filtre').submit();
		});
		$('#nomesAmbErrorsBtn').click(function () {
			nomesAmbErrors = !$(this).hasClass('active');
			$('#nomesAmbErrors').val(nomesAmbErrors);
		});

		// $(".botons").css("{display:flex; justify-content:flex-end; padding-top:8px;}");
	});
</script>
</head>
<body>
<div id="msg-box"></div>
	<form:form id="filtre" action="" method="post" cssClass="well" modelAttribute="notificacioMassivaFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<not:inputDate name="dataInici" placeholderKey="notificacio.list.filtre.camp.datainici" inline="true" required="false" />
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataFi" placeholderKey="notificacio.list.filtre.camp.datafi" inline="true" required="false" />
			</div>
<%--			<div class="col-md-2">--%>
<%--				<not:inputSelect name="estatValidacio" optionItems="${notificacioMassivaEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.massiva.list.estat.validacio" inline="true"/>--%>
<%--			</div>--%>
			<div class="col-md-2">
				<not:inputSelect name="estatProces" optionItems="${notificacioMassivaEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.massiva.list.estat.proces" inline="true"/>
			</div>
			<c:if test="${isRolActualAdministradorEntitat}">
			<div class="col-md-3">
				<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
				<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
				<not:inputSuggest
						name="createdByCodi"
						urlConsultaInicial="${urlConsultaInicial}"
						urlConsultaLlistat="${urlConsultaLlistat}"
						textKey="notificacio.list.filtre.camp.numexpedient"
						placeholderKey="notificacio.list.filtre.camp.creadaper"
						suggestValue="codi"
						suggestText="nom"
						inline="true"/>
			</div>
			</c:if>
			<div class="col-md-2 pull-right form-buttons"  style="text-align: right;">
<%--				<button id="nomesAmbErrorsBtn" title="<spring:message code="notificacio.list.filtre.camp.nomesAmbErrors"/>" class="btn btn-default <c:if test="${nomesAmbErrors}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>--%>
<%--				<not:inputHidden name="nomesAmbErrors"/>--%>
				<button id="btnNetejar" type="submit" name="netejar" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	<script id="botonsTemplate" type="text/x-jsrender">
	 	<div class="text-right" style="padding-top:8px;">
	 		<span class="estat_info">[<span class="fa fa-check"></span> <spring:message code="notificacio.massiva.llegenda.ok" />]</span>
	 		<span class="estat_info">[<span class="fa fa-times"></span> <spring:message code="notificacio.massiva.llegenda.error" />]</span>
			<span class="estat_info">[<span class="fa fa-ban"></span> <spring:message code="notificacio.massiva.llegenda.cancel" />]</span>
	 	</div>
	 </script>
	<table
		id="notificacio"
		data-toggle="datatable"
		data-url="<c:url value="/notificacio/massiva/datatable"/>"
		data-search-enabled="false"
		data-default-order="2"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-filter="#filtre"
		data-save-state="true"
		data-botons-template="#botonsTemplate"
		data-mantenir-paginacio="true">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="progress" data-visible="false"></th>
				<th data-col-name="createdDate" data-converter="datetime" width="120px"><spring:message code="notificacio.massiva.list.columna.data"/></th>
				<th data-col-name="csvFilename" data-template="#cellCSV">
					<spring:message code="notificacio.massiva.list.columna.notificacions"/>
					<script id="cellCSV" type="text/x-jsrender">
						{{:csvFilename }}
						<a id="download-csv" title="Descarregar fitxer csv" class="btn btn-default btn-dwl pull-right" href="<c:url value="/notificacio/massiva/{{:id}}/csv/download"/>" target="_blank">
							<span class="fa fa-download"></span>
						</a>
					</script>
				</th>
				<th data-col-name="zipFilename" data-template="#cellZip"  width="350px">
					<spring:message code="notificacio.massiva.list.columna.Documents"/>
					<script id="cellZip" type="text/x-jsrender">
						{{if zipFilename }}
							{{:zipFilename }}
							<a id="download-zip" title="Descarregar fitxer zip" class="btn btn-default btn-dwl pull-right" href="<c:url value="/notificacio/massiva/{{:id}}/zip/download"/>" target="_blank">
								<span class="fa fa-download"></span>
							</a>
						{{/if}}
					</script>
				</th>
				<th data-col-name="totalNotificacions" data-visible="false"></th>
				<th data-col-name="notificacionsValidades" data-visible="false"></th>
				<th data-col-name="notificacionsProcessades" data-visible="false"></th>
				<th data-col-name="notificacionsProcessadesAmbError" data-visible="false"></th>
				<th data-col-name="notificacionsCancelades" data-visible="false"></th>
				<th data-col-name="estatValidacio" data-template="#cellEstatValidacioTemplate" width="200px">
					<spring:message code="notificacio.massiva.list.columna.estat.validacio"/>
					<script id="cellEstatValidacioTemplate" type="text/x-jsrender">
						{{if estatValidacio == 'PENDENT'}}
							<span class="label label-default">
								{{:~eval('notificacioMassivaEstats["' + estatValidacio + '"]')}}
								<span class="estat_info">[{{:totalNotificacions}}]</span>
							</span>
						{{else estatValidacio == 'FINALITZAT_AMB_ERRORS'}}
							<span class="label label-warning">
								{{:~eval('notificacioMassivaEstats["' + estatValidacio + '"]')}}
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsValidades}} / <span class="fa fa-times"></span> {{:totalNotificacions - notificacionsValidades}}]</span>
							</span>
							<a id="download-zip" title="Descarregar csv errors" class="btn btn-default btn-dwl pull-right" href="<c:url value="/notificacio/massiva/{{:id}}/errors/validacio/download"/>" target="_blank">
								<span class="fa fa-download"></span>
							</a>
						{{else estatValidacio == 'FINALITZAT'}}
							<span class="label label-success">
								{{:~eval('notificacioMassivaEstats["' + estatValidacio + '"]')}}
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsValidades}}]</span>
							</span>
						{{else estatValidacio == 'ERRONIA'}}
							<span class="label label-danger">
								{{:~eval('notificacioMassivaEstats["' + estatValidacio + '"]')}}
								<span class="estat_info">[<span class="fa fa-times"></span> {{:totalNotificacions}}]</span>
							</span>
							<a id="download-zip" title="Descarregar csv errors" class="btn btn-default btn-dwl pull-right" href="<c:url value="/notificacio/massiva/{{:id}}/errors/validacio/download"/>" target="_blank">
								<span class="fa fa-download"></span>
							</a>
						{{/if}}
					</script>
				</th>
				<th data-col-name="estatProces" data-template="#cellEstatProcesTemplate" width="100px">
					<spring:message code="notificacio.massiva.list.columna.estat.proces"/>
					<script id="cellEstatProcesTemplate" type="text/x-jsrender">
						{{if estatProces == 'PENDENT'}}
							<span class="label label-default">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}}
								<span class="estat_info">[{{:notificacionsValidades}}]</span>
							</span>
						{{else estatProces == 'EN_PROCES'}}
							<span class="label label-info">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}} ({{:progress}} %)
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsProcessades}}]</span>
							</span>
						{{else estatProces == 'EN_PROCES_AMB_ERRORS'}}
							<span class="label label-warning">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}} ({{:progress}} %)
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsProcessades}} / <span class="fa fa-times"></span> {{:notificacionsProcessadesAmbError}}]</span>
							</span>
						{{else estatProces == 'FINALITZAT'}}
							<span class="label label-success">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}}
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsProcessades}}]</span>
							</span>
						{{else estatProces == 'FINALITZAT_AMB_ERRORS'}}
							<span class="label label-warning">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}}
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsProcessades}} / <span class="fa fa-times"></span> {{:notificacionsProcessadesAmbError}}]</span>
							</span>
						{{else estatProces == 'ERRONIA'}}
							<span class="label label-danger">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}}
								<span class="estat_info">[<span class="fa fa-times"></span> {{:notificacionsValidades}}]</span>
							</span>
						{{else estatProces == 'CANCELADA'}}
							<span class="label label-warning">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}}
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsProcessades}}]</span>
								<span class="estat_info">[<span class="fa fa-ban"></span> {{:notificacionsCancelades}}]</span>
							</span>
						{{else estatProces == 'FINALITZAT_PARCIAL'}}
							<span class="label label-info">
								{{:~eval('notificacioMassivaEstats["' + estatProces + '"]')}}
								<span class="estat_info">[<span class="fa fa-check"></span> {{:notificacionsProcessades}} / <span class="fa fa-times"></span> {{:notificacionsProcessadesAmbError}}]</span>
								<span class="estat_info">[<span class="fa fa-ban"></span> {{:notificacionsCancelades}}]</span>
							</span>
						{{/if}}
					</script>
				</th>
				<c:if test="${isRolActualAdministradorEntitat}">
					<th data-col-name="createdByComplet" data-converter="String" width="150px"><spring:message code="notificacio.massiva.list.columna.creadaPer"/></th>
				</c:if>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="60px">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/resum"/>" data-toggle="modal" data-height="700px" data-processar="true"><span class="fa fa-info-circle"></span>&nbsp; <spring:message code="notificacio.massiva.list.accio.resum"/></a></li>
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/resum/download"/>"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="notificacio.massiva.list.accio.resum.download"/></a></li>
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/errors/validacio/download"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="notificacio.massiva.list.accio.errors.validacio.download"/></a></li>
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/errors/execucio/download"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="notificacio.massiva.list.accio.errors.execucio.download"/></a></li>
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/posposar"/>"><span class="fa fa-clock-o"></span>&nbsp;<spring:message code="notificacio.massiva.list.accio.posposar"/></a></li>
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/reactivar"/>"><span class="fa fa-bolt"></span>&nbsp;<spring:message code="notificacio.massiva.list.accio.reactivar"/></a></li>
								<li><a href="<c:url value="/notificacio/massiva/{{:id}}/remeses"/>"><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="notificacio.massiva.list.accio.remeses"/></a></li>
								{{if estatProces == 'PENDENT' || estatProces == 'EN_PROCES' || estatProces == 'EN_PROCES_AMB_ERRORS'}}
									<li><a href="<c:url value="/notificacio/massiva/{{:id}}/cancelar"/>"><span class="fa fa-ban"></span>&nbsp;<spring:message code="notificacio.massiva.list.accio.cancelar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

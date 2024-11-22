<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	es.caib.notib.back.config.scopedata.SessionScopedContext ssc = (es.caib.notib.back.config.scopedata.SessionScopedContext)request.getAttribute("sessionScopedContext");
	pageContext.setAttribute("isRolActualAdministrador", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministrador(ssc.getRolActual()));
	pageContext.setAttribute("isRolActualAdministradorEntitat", es.caib.notib.back.helper.RolHelper.isUsuariActualAdministradorEntitat(ssc.getRolActual()));
	pageContext.setAttribute("notificacioComunicacioEnumOptions", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(es.caib.notib.logic.intf.dto.NotificacioTipusEnviamentEnumDto.class, "notificacio.tipus.enviament.enum."));
	pageContext.setAttribute("notificacioEstatEnumOptions", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto."));
%>
<c:set var="ampladaConcepte">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">35%</c:when>
		<c:otherwise>55%</c:otherwise>
	</c:choose>
</c:set>
<c:set var="refresh_state_succes"><spring:message code="notificacio.list.enviament.list.refresca.estat.exitos"/></c:set>
<c:set var="refresh_state_error"><spring:message code="notificacio.list.enviament.list.refresca.estat.error"/></c:set>
<html>
<head>
	<title><spring:message code="enviament.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/datatable.accions-massives.js"/>"></script>
	<link href="<c:url value="/css/datatable-accions-massives.css"/>" rel="stylesheet"/>

<script>
$(document).ready(function() {

	var $estatColumn = $('#estat');
	var $entregaPostalColumn = $('#entregaPostal');
	var $enviamentTipusColumn = $('#enviamentTipus');
	$estatColumn.select2({
		width: '100%',
        allowClear:true,
        placeholder: 'Selecciona una opció'//'${placeholderText}'
    });
	$entregaPostalColumn.select2({
		width: '100%',
		allowClear:true,
		placeholder: 'Selecciona una opció'//'${placeholderText}'
	});
	$enviamentTipusColumn.select2({
		width: '100%',
		allowClear:true,
		placeholder: '<spring:message code="notificacio.list.filtre.camp.enviament.tipus"/>'
	});

	function configureColumnSelectFilter($selector) {
		$selector.on('select2:select', function (e) {
			$("#enviament").dataTable().api().ajax.reload();
		});
		$selector.on('select2:unselect', function (e) {
			$("#enviament").dataTable().api().ajax.reload();
		});
		$selector.on('change', function () {
			// console.log($selector.val());
			$selector.val($selector.val());
			$("#btnFiltrar").first().click();
		});
	}

	configureColumnSelectFilter($estatColumn);
	configureColumnSelectFilter($entregaPostalColumn);
	configureColumnSelectFilter($enviamentTipusColumn);

    if("${filtreEnviaments.estat}" != ""){
		$estatColumn.val("${filtreEnviaments.estat}").trigger('change');
    }

	if("${filtreEnviaments.entregaPostal}" != ""){
		$entregaPostalColumn.val("${filtreEnviaments.entregaPostal}").trigger('change');
	}

	if("${filtreEnviaments.enviamentTipus}" != ""){
		$enviamentTipusColumn.val("${filtreEnviaments.enviamentTipus}".toLowerCase()).trigger('change');
	}

    $('.data').datepicker({
		orientation: "bottom",
		dateFormat: 'dd/mm/yy',
		weekStart: 1,
		todayHighlight: true,
		language: "${requestLocale}"
	});

	let eventMessages = {
		'confirm-reintentar-errors': "<spring:message code="enviament.list.user.reintentar.errors.misatge.avis"/>",
		'confirm-reintentar-notificacio': "<spring:message code="enviament.list.user.reintentar.notificacio.misatge.avis"/>",
		'confirm-reintentar-consulta': "<spring:message code="enviament.list.user.reactivar.consulta.misatge.avis"/>",
		'confirm-reintentar-sir': "<spring:message code="enviament.list.user.reactivar.sir.misatge.avis"/>",
		'confirm-update-estat': "<spring:message code="enviament.list.user.actualitzar.estat.misatge.avis"/>",
		'confirm-reactivar-callback': "<spring:message code="enviament.list.user.reactivar.callback.misatge.avis"/>",
		'confirm-accio-massiva': "<spring:message code="enviament.list.user.confirm.accio.massiva"/>",
	};

	initEvents($('#enviament'), 'enviament', eventMessages)

	$("#enviament th").last().empty();
	$("#enviament th").last().css("padding", 0);

	$("#enviament th").keypress(function(event) {
	    if (event.which == 13) {
	        event.preventDefault();
	        $("#btnFiltrar").first().click();
	    }
	});
});


function setCookie(cname,cvalue) {
	var exdays = 30;
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
</script>
<style type="text/css">
    .label-primary {
        background-color: #999999;
    }
    .label-warning {
        background-color: #dddddd;
        color: #333333;
    }
    .div-filter-data-sep {
        padding: 0;
    }
	.dropdown-left {
		right: 0;
		left: auto
	}
</style>
</head>
<body>

	<div id="loading-screen" class="loading-screen" >
		<div id="processing-icon" class="processing-icon">
			<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: dimgray;margin-top: 10px;"></span>
		</div>
	</div>
	<form:form id="enviamentFiltreForm" action="" method="post" cssClass="well hidden" modelAttribute="enviamentFiltreCommand"></form:form>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<a href="<c:url value="/enviament/visualitzar"/>" data-toggle="modal" data-refresh-pagina="true" class="btn btn-default"><span class="fa fa-eye-slash"></span> <spring:message code="enviament.list.show"/></a>
				<button id="seleccioAll" title="<spring:message code="enviament.list.user.seleccio.tots" />" class="btn btn-default" ><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="enviament.list.user.seleccio.cap" />" class="btn btn-default" ><span class="fa fa-square-o"></span></button>
				<div id="seleccioCount" class="btn-group">
					<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
  						<span class="badge seleccioCount">${fn:length(seleccio)}</span> <spring:message code="enviament.list.user.accions.massives"/> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu dropdown-left">
						<li><a style="cursor: pointer;" id="exportarODS"><spring:message code="enviament.list.user.exportar"/> a <spring:message code="enviament.list.user.exportar.EXCEL"/></a></li>
						<li><a id="reintentarErrors" style="cursor: pointer;" title='<spring:message code="notificacio.list.accio.massiva.reintentar.errors.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reintentar.errors"/></a></li>
						<li><a style="cursor: pointer;" id="updateEstat"><spring:message code="enviament.list.user.actualitzar.estat"/></a></li>

						<c:if test="${isRolActualAdministradorEntitat}">
							<hr/>
							<li><a style="cursor: pointer;" id="reactivarConsulta" title='<spring:message code="notificacio.list.accio.massiva.reactivar.consultes.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.consultes.notifica"/></a></li>
							<%--        <li><a style="cursor: pointer;" id="reactivarSir" title='<spring:message code="notificacio.list.accio.massiva.reactivar.consultes.sir.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.consultes.sir"/></a></li>--%>
							<li><a style="cursor: pointer;" id="reactivarCallback" title='<spring:message code="notificacio.list.accio.massiva.reactivar.callbacks.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.callbacks"/></a></li>
							<%--        <li><a style="cursor: pointer;" id="enviarCallback" title='<spring:message code="notificacio.list.accio.massiva.enviar.callbacks.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.enviar.callbacks"/></a></li>--%>
							<li><a style="cursor: pointer;" id="enviarNotificacionsMovil" title='<spring:message code="notificacio.list.accio.massiva.enviar.notificacions.movil.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.enviar.notificacions.movil"/></a></li>
							<%--        <li><a style="cursor: pointer;" id="reactivarRegistre" title='<spring:message code="notificacio.list.accio.massiva.reactivar.registre.tooltip"/>'><spring:message code="notificacio.list.accio.massiva.reactivar.registre"/></a></li>--%>
						</c:if>
					</ul>
				</div>
			</div>
			<div class="btn-group">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
			</div>
		</div>
	</script>

	<script id="cellFilterTemplate" type="text/x-jsrender">
		<div class="dropdown">
			<button type="submit" id="btnFiltrar" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-search"></span></button>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">enviament/{{:id}}/detall</script>
	<div id="cover-spin"></div>
	<table
		id="enviament"
		data-toggle="datatable"
		data-url="<c:url value="/enviament/datatable"/>"
		class="table table-striped table-bordered"
		data-default-order="0"
		data-default-dir="desc"
		data-individual-filter="true"
		data-botons-template="#botonsTemplate"
		data-date-template="#dataTemplate"
		data-cell-template="#cellFilterTemplate"
		data-paging-style-x="true"
		data-scroll-overflow="adaptMax"
		data-selection-enabled="true"
		data-save-state="true"
		data-mantenir-paginacio="${mantenirPaginacio}"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<c:choose>
					<c:when test = "${columnes.dataCreacio == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dataCreacio == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="createdDate" data-converter="date" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.datacreacio"/>
					<script id="dataTemplate" type="text/x-jsrender">
						<div class="from-group">
							<div class="input-group vdivide">
    							<input name="dataCreacioInici" value="${filtreEnviaments.dataCreacioInici}" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon div-filter-data-sep"></div>
    							<input name="dataCreacioFi" value="${filtreEnviaments.dataCreacioFi}" type="text" class="form-control data" placeholder="Final">
							</div>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.dataEnviament == true}">
						<c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dataEnviament == false}">
						<c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="enviadaDate" data-converter="date" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dataenviament"/>
					<script id="dataTemplate" type="text/x-jsrender">
						<div class="from-group">
							<div class="input-group vdivide">
    							<input name="dataEnviamentInici" value="${filtreEnviaments.dataEnviamentInici}" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon div-filter-data-sep"></div>
    							<input name="dataEnviamentFi" value="${filtreEnviaments.dataEnviamentFi}" type="text" class="form-control data" placeholder="Final">
							</div>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.dataProgramada == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dataProgramada == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="enviamentDataProgramada" data-converter="datetime" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dataprogramada"/>
					<script id="dataTemplate" type="text/x-jsrender">
						<div class="from-group input-daterange" data-provide="daterangepicker">
							<div class="input-group vdivide">
    							<input name="dataProgramadaDisposicioInici" value="${filtreEnviaments.dataProgramadaDisposicioInici}" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon div-filter-data-sep"></div>
    							<input name="dataProgramadaDisposicioFi" value="${filtreEnviaments.dataProgramadaDisposicioFi}" type="text" class="form-control data" placeholder="Final">
							</div>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.notIdentificador == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.notIdentificador == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="notificaIdentificador" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codinotifica"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="codiNotifica" value="${filtreEnviaments.codiNotifica}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codinotifica"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.grupCodi == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.grupCodi == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="grupCodi" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codigrup"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="grup" value="${filtreEnviaments.grup}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codigrup"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.dir3Codi == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dir3Codi == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="organEstat" data-visible="false"></th>
				<th data-col-name="organCodiNom" width="360" data-template="#cellOrganGestorTemplate" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dir3codi"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="dir3Codi" value="${filtreEnviaments.dir3Codi}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.dir3codi"/>"/>
						</div>
					</script>
					<script id="cellOrganGestorTemplate" type="text/x-jsrender">
						{{:organCodiNom}}
						{{if organEstat != 'V'}}
							<span class="fa fa-warning text-danger" title="<spring:message code='enviament.list.organGestor.obsolet'/>"></span>{{/if}}
 					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.proCodi == true}">
						<c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.proCodi == false}">
						<c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="procedimentTipus" data-visible="false"></th>
				<th data-col-name="procedimentCodiNom" width="300" data-template="#cellProcedimentTemplate" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codiprocediment"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="codiProcediment" value="${filtreEnviaments.codiProcediment}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codiprocediment"/>"/>
						</div>
					</script>
					<script id="cellProcedimentTemplate" type="text/x-jsrender">
						{{if procedimentTipus == 'PROCEDIMENT'}}<span class="label label-primary">P</span>{{/if}}
						{{if procedimentTipus == 'SERVEI'}}<span class="label label-warning">S</span>{{/if}}
						{{:procedimentCodiNom}}
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.usuari == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.usuari == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="usuariCodi" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.usuari"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="usuari" value="${filtreEnviaments.usuari}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.usuari"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.concepte == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.concepte == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="concepte" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.concepte"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="concepte" value="${filtreEnviaments.concepte}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.concepte"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.descripcio == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.descripcio == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="descripcio"  data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.descripcio"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="descripcio" value="${filtreEnviaments.descripcio}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.descripcio"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.titularNomLlinatge == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.titularNomLlinatge == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="titularNomLlinatge" width="160" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.nomLlinatgetitular"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="titularNomLlinatge" value="${filtreEnviaments.titularNomLlinatge}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.nomLlinatgetitular"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.titularEmail == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.titularEmail == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="titularEmail" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.emailtitular"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="emailTitular" value="${filtreEnviaments.emailTitular}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.emailtitular"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.destinataris == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.destinataris == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="destinataris" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.destinataris"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="destinataris" value="${filtreEnviaments.destinataris}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.destinataris"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.llibreRegistre == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.llibreRegistre == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="llibre" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.llibreregistre"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="registreLlibre" value="${filtreEnviaments.registreLlibre}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.llibreregistre"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.numeroRegistre == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.numeroRegistre == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="registreNumero" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.numeroregistre"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="registreNumero" value="${filtreEnviaments.registreNumero}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.numeroregistre"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.dataRegistre == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dataRegistre == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="registreData" width="230" data-converter="datetime" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dataregistre"/>
					<script type="text/x-jsrender">
						<div class="from-group" data-provide="daterangepicker" style="width: 230px;">
							<div class="input-group vdivide">
    							<input name="dataRegistreInici" value="${filtreEnviaments.dataRegistreInici}" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon"></div>
    							<input name="dataRegistreFi" value="${filtreEnviaments.dataRegistreFi}" type="text" class="form-control data" placeholder="Final">
							</div>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.dataCaducitat == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dataCaducitat == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="notificaDataCaducitat" width="230" data-converter="datetime" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.datacaducitat"/>
					<script type="text/x-jsrender">
						<div class="from-group" data-provide="daterangepicker" style="width: 230px;">
							<div class="input-group vdivide">
    							<input name="dataCaducitatInici" value="${filtreEnviaments.dataCaducitatInici}" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon"></div>
    							<input name="dataCaducitatFi" value="${filtreEnviaments.dataCaducitatFi}" type="text" class="form-control data" placeholder="Final">
							</div>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.enviamentTipus == true}">
						<c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.enviamentTipus == false}">
						<c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="tipusEnviament" class="enviamentTipusCol" width="5px" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.tipusenviament"/>
					<script id="cellEnviamentTipusTemplate" type="text/x-jsrender">
					<div class="from-group" style="padding: 0; font-weight: 100;">
						<select class="form-control" id="enviamentTipus" name="enviamentTipus"
								style="width:100%" data-toggle="select2" data-minimumresults="-1" tabindex="-1" aria-hidden="true" data-select2-eval="true">
							<option value=""></option>
							<c:forEach items="${notificacioComunicacioEnumOptions}" var="opt">
								<option name="enviamentTipus" value="${opt.value != 'buit' ? opt.value : ''}" class="${opt.value != 'buit' ? '' : 'buit'}"><span class="${opt.value != 'buit' ? '' : 'buit'}"><spring:message code="${opt.text}"/></span></option>
							</c:forEach>
						</select>
					</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.codiNotibEnviament == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.codiNotibEnviament == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="codiNotibEnviament" width="240" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.referencia.enviament"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="codiNotibEnviament" value="${filtreEnviaments.codiNotibEnviament}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.referencia.enviament"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.referenciaNotificacio == true}">
						<c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.referenciaNotificacio == false}">
						<c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="referenciaNotificacio" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.identificador.notificacio"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="referenciaNotificacio" value="${filtreEnviaments.referenciaNotificacio}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.identificador.notificacio"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.numCertificacio == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.numCertificacio == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="notificaCertificacioNumSeguiment" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.numerocertificatcorreus"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="numeroCertCorreus" value="${filtreEnviaments.numeroCertCorreus}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.numerocertificatcorreus"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.csvUuid == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.csvUuid == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="csvUuid" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codicsvuuid"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="csvUuid" value="${filtreEnviaments.csvUuid}" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codicsvuuid"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.estat == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.estat == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="estat"  data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.estat"/>
					<script type="text/x-jsrender">
						<div class="from-group" style="padding: 0; font-weight: 100;">
							<select class="form-control" id="estat" name="estat">
								<option name="estat" class=""></option>
    							<c:forEach items="${notificacioEstatEnumOptions}" var="opt">
        							<option name="estat" value="${opt.value != 'buit' ? opt.value : ''}" class="${opt.value != 'buit' ? '' : 'buit'}"><span class="${opt.value != 'buit' ? '' : 'buit'}"><spring:message code="${opt.text}"/></span></option>
    							</c:forEach>
							</select>
						</div>
					</script>
				</th>

				<c:choose>
					<c:when test = "${columnes.entregaPostal == true}">
						<c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.entregaPostal == false}">
						<c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="entregaPostalText" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.entrega.postal"/>
					<script type="text/x-jsrender">
						<div class="from-group" style="padding: 0; font-weight: 100;">
							<select class="form-control" id="entregaPostal" name="entregaPostal">
								<option name="entregaPostal" class=""></option>
								<option name="entregaPostal" value="true">Si</option>
								<option name="entregaPostal" value="false">No</option>
						</select>
                    </div>
					</script>
				</th>

				<th data-col-name="notificacioId" data-visible="false"></th>
				<th data-orderable="false" data-template="#cellAccionsTemplate" width="190">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/notificacio/{{:notificacioId}}/enviament/{{:id}}"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
						<a href="<c:url value="/notificacio/{{:notificacioId}}/info"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detall.remesa"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatOrdreFiltre" %>
<%@ page import="es.caib.notib.client.domini.EnviamentTipus" %><%--<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto" %>--%>
<%--<%@ page import="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatOrdreFiltre" %>--%>
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
//	pageContext.setAttribute("notificacioComunicacioEnumOptions", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(es.caib.notib.logic.intf.dto.EnviamentTipus.class, "notificacio.tipus.enviament.enum."));
//	pageContext.setAttribute("notificacioEstatEnumOptions", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(NotificacioEstatOrdreFiltre.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto."));
	pageContext.setAttribute("notificacioEnviamentTipus", es.caib.notib.back.helper.EnumHelper.getOptionsForEnum(EnviamentTipus.class, "es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto."));
%>
<c:set var="ampladaConcepte">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">35%</c:when>
		<c:otherwise>55%</c:otherwise>
	</c:choose>
</c:set>
<c:set var="refresh_state_succes"><spring:message code="notificacio.list.enviament.list.refresca.estat.exitos"/></c:set>
<c:set var="refresh_state_error"><spring:message code="notificacio.list.enviament.list.refresca.estat.error"/></c:set>
<c:set var="notificacioEnviamentTipus" value="${notificacioEnviamentTipus}" scope="request"/>
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
	$(document).ready(function () {

		let $taula = $('#enviament');
		$taula.on('draw.dt', function () {

			let rows = this.rows;
			for (let row = 1; row < rows.length; row++) {
				let tag = $(this.rows[row]).find(".estatColor")[0];
				if (!tag) {
					console.error("No hi ha color d'estat per la fila " + row);
					return;
				}
				let classes = tag.classList;
				this.rows[row].firstChild.style = "border-left: 3px solid " + classes[classes.length - 1];
			}
		});
		<%--var $estatColumn = $('#estat');--%>
		<%--var $entregaPostalColumn = $('#entregaPostal');--%>
		<%--var $enviamentTipusColumn = $('#enviamentTipus');--%>
		<%--$estatColumn.select2({--%>
		<%--	width: '100%',--%>
		<%--	allowClear: true,--%>
		<%--	placeholder: 'Selecciona una opció'//'${placeholderText}'--%>
		<%--});--%>
		<%--$entregaPostalColumn.select2({--%>
		<%--	width: '100%',--%>
		<%--	allowClear: true,--%>
		<%--	placeholder: 'Selecciona una opció'//'${placeholderText}'--%>
		<%--});--%>
		<%--$enviamentTipusColumn.select2({--%>
		<%--	width: '100%',--%>
		<%--	allowClear: true,--%>
		<%--	placeholder: '<spring:message code="notificacio.list.filtre.camp.enviament.tipus"/>'--%>
		<%--});--%>

		<%--function configureColumnSelectFilter($selector) {--%>
		<%--	$selector.on('select2:select', function (e) {--%>
		<%--		$("#enviament").dataTable().api().ajax.reload();--%>
		<%--	});--%>
		<%--	$selector.on('select2:unselect', function (e) {--%>
		<%--		$("#enviament").dataTable().api().ajax.reload();--%>
		<%--	});--%>
		<%--	$selector.on('change', function () {--%>
		<%--		$selector.val($selector.val());--%>
		<%--		$("#btnFiltrar").first().click();--%>
		<%--	});--%>
		<%--}--%>

		<%--configureColumnSelectFilter($estatColumn);--%>
		<%--configureColumnSelectFilter($entregaPostalColumn);--%>
		<%--configureColumnSelectFilter($enviamentTipusColumn);--%>

		<%--if ("${filtreEnviaments.estat}" != "") {--%>
		<%--	$estatColumn.val("${filtreEnviaments.estat}").trigger('change');--%>
		<%--}--%>

		<%--if ("${filtreEnviaments.entregaPostal}" != "") {--%>
		<%--	$entregaPostalColumn.val("${filtreEnviaments.entregaPostal}").trigger('change');--%>
		<%--}--%>

		<%--if ("${filtreEnviaments.enviamentTipus}" != "") {--%>
		<%--	$enviamentTipusColumn.val("${filtreEnviaments.enviamentTipus}".toLowerCase()).trigger('change');--%>
		<%--}--%>

		<%--$('.data').datepicker({--%>
		<%--	orientation: "bottom",--%>
		<%--	dateFormat: 'dd/mm/yy',--%>
		<%--	weekStart: 1,--%>
		<%--	todayHighlight: true,--%>
		<%--	language: "${requestLocale}"--%>
		<%--});--%>

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

		$("#enviament th").keypress(function (event) {
			if (event.which == 13) {
				event.preventDefault();
				$("#btnFiltrar").first().click();
			}
		});

		$('#btn-netejar-filtre').click(function () {
			$(':input', $('#form-filtre')).each(function () {
				var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea') {
					this.value = '';
				} else if (type == 'checkbox' || type == 'radio') {
					this.checked = false;
				} else if (tag == 'select') {
					this.selectedIndex = 0;
				}

			});

			$('#nomesAmbEntregaPostalBtn').removeClass('active');
			$('#entregaPostal').val(false);
			deselecciona();
		});


		$("#filtreAvancat").on("click", e => {

			e.preventDefault();
			$(".filtreOcult").toggle();
			$("#div-concepte").removeClass("col-md-2").addClass("col-md-4");
			$("#filtreAvancat").hide();
			$("#filtreSimple").show();
			$("#filtreSimpleActiu").val(false);
		});

		$("#filtreSimple").on("click", e => {

			e.preventDefault();
			$(".filtreOcult").toggle();
			$("#div-concepte").removeClass("col-md-4").addClass("col-md-2");
			$("#filtreAvancat").show();
			$("#filtreSimple").hide();
			$("#filtreSimpleActiu").val(true);
		});

		<c:if test="${mostrarFiltreAvancat == true}">
		$("#filtreAvancat").click();
		</c:if>

		$('#nomesAmbEntregaPostalBtn').click(function() {
			entregaPostal = !$(this).hasClass('active');
			$('#entregaPostal').val(entregaPostal);
		})

	});

	function deselecciona() {

		$(".seleccioCount").html(0);
		$.ajax({
			type: 'GET',
			url: "<c:url value="/enviament/deselect"/>",
			async: false,
			success: function (data) {
				$(".seleccioCount").html(data);
				$('#enviament').webutilDatatable('select-none');
			}
		});
	}

	function setCookie(cname, cvalue) {
		var exdays = 30;
		var d = new Date();
		d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
		var expires = "expires=" + d.toGMTString();
		document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
	}

	function getCookie(cname) {
		var name = cname + "=";
		var ca = document.cookie.split(';');
		for (var i = 0; i < ca.length; i++) {
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

	function showEstat(element) {

		let bsIconCode = "";
		let translate = element.text;
		let text = ""
		let style = ""
		if (element.id == 'PENDENT') {
			bsIconCode = "fa fa-clock-o";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.PENDENT"/>";
		}
		if (element.id == 'ENVIADA') {
			bsIconCode = "fa fa-send-o";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA"/>";
		}
		if (element.id == 'REGISTRADA') {
			bsIconCode = "fa fa-file-o";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.REGISTRADA"/>";
		}
		if (element.id == 'FINALITZADA') {
			bsIconCode = "fa fa-check";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA"/>";
		}
		if (element.id == 'PROCESSADA') {
			bsIconCode = "fa fa-check-circle";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.PROCESSADA"/>";
		}
		if (element.id == 'EXPIRADA') {
			bsIconCode = "fa fa-asterisk";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.EXPIRADA"/>";
		}
		if (element.id == 'NOTIFICADA') {
			bsIconCode = "fa fa-check-circle";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.NOTIFICADA"/>";
		}
		if (element.id == 'REBUTJADA') {
			bsIconCode = "fa fa-times";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.REBUTJADA"/>";
		}
		if (element.id == 'ENVIAT_SIR') {
			bsIconCode = "label label-primary";
			text = "S"
			style = "display:inline-block; padding:3px";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIAT_SIR"/>";
		}
		if (element.id == 'ENVIADA_AMB_ERRORS') {
			bsIconCode = "fa fa-send-o";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS"/>";
		}
		if (element.id == 'FINALITZADA_AMB_ERRORS') {
			bsIconCode = "fa fa-check";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS"/>";
		}
		if (element.id == 'ENVIANT') {
			bsIconCode = "fa fa-clock-o";
			translate = "<spring:message code="es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIANT"/>";
		}

		return $('<span style= " ' + style + ' " class="' + bsIconCode + '">' + text + ' </span><span>  ' + translate + '</span>');
	}
	var enviamentTipus = [];
	<c:forEach var="tipus" items="${notificacioEnviamentTipus}">
	enviamentTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
	</c:forEach>


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
						<li><a style="cursor: pointer;" id="ampliarPlazoOe"><spring:message code="notificacio.list.accio.massiva.ampliar.plazo.oe"/></a></li>

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
	<form:form id="form-filtre" action="" method="post" cssClass="well" modelAttribute="enviamentFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<not:inputSelect id="enviamentTipus" name="enviamentTipus" optionItems="${notificacioEnviamentTipus}" optionValueAttribute="value"
								 optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.enviament.tipus" inline="true"/>
			</div>
			<div id="div-concepte" class="col-md-2">
				<not:inputText name="concepte" inline="true" placeholderKey="enviament.list.concepte"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect id="estat" name="estat" optionMinimumResultsForSearch="0" optionTextKeyAttribute="text" emptyOption="true"
								 placeholderKey="notificacio.list.filtre.camp.estat" inline="true" templateResultFunction="showEstat" />
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataEnviamentInici" placeholderKey="enviament.list.dataenviament.inici" inline="true" required="false" />
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataEnviamentFi" placeholderKey="enviament.list.dataenviament.fi" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputDate name="dataCreacioInici" placeholderKey="enviament.list.datacreacio.inici" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputDate name="dataCreacioFi" placeholderKey="enviament.list.datacreacio.fi" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputDate name="dataProgramadaDisposicioInici" placeholderKey="enviament.list.datadisposicio.inici" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputDate name="dataProgramadaDisposicioFi" placeholderKey="enviament.list.datadisposicio.inici" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="codiNotifica" inline="true" placeholderKey="enviament.list.codinotifica"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="grup" inline="true" placeholderKey="enviament.list.codigrup"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="dir3Codi" inline="true" placeholderKey="enviament.list.dir3codi"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="codiProcediment" inline="true" placeholderKey="enviament.list.codiprocediment"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="usuari" inline="true" placeholderKey="enviament.list.usuari"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="descripcio" inline="true" placeholderKey="enviament.list.descripcio"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="titularNomLlinatge" inline="true" placeholderKey="enviament.list.nomLlinatgetitular"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="destinataris" inline="true" placeholderKey="enviament.list.destinataris"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="registreNumero" inline="true" placeholderKey="enviament.list.numeroregistre"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputDate name="dataCaducitatInici" placeholderKey="enviament.list.datacaducitat.inici" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputDate name="dataCaducitatFi" placeholderKey="enviament.list.datacaducitat.fi" inline="true" required="false" />
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="codiNotibEnviament" inline="true" placeholderKey="enviament.list.referencia.enviament"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="referenciaNotificacio" inline="true" placeholderKey="enviament.list.identificador.notificacio"/>
			</div>
			<div class="col-md-2 filtreOcult">
				<not:inputText name="csvUuid" inline="true" placeholderKey="enviament.list.codicsvuuid"/>
			</div>

			<div class="col-md-2 pull-right flex-justify-end">
				<div class="filtreOcult" style="margin-right:10px">
					<button id="nomesAmbEntregaPostalBtn" title="<spring:message code="notificacio.list.filtre.camp.nomesAmbEntregaPostal"/>" class="btn btn-default pull-left <c:if test="${nomesAmbEntregaPostal}">active</c:if>" data-toggle="button"><span class="fa fa-envelope"></span></button>
					<not:inputHidden name="entregaPostal"/>
				</div>
				<div id="botons-filtre-simple" class="pull-right form-buttons"  style="text-align: right;">
					<button id="btn-netejar-filtre" type="submit" name="netejar" value="netejar" class="btn btn-default" style="padding: 6px 9px; margin-right:5px;" title="<spring:message code="comu.boto.netejar"/>"><span class="fa fa-eraser icona_ocultable" style="padding: 2px 0px;"></span><span class="text_ocultable"><spring:message code="comu.boto.netejar"/></span></button>
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" title="<spring:message code="comu.boto.filtrar"/>"><span class="fa fa-filter" id="botoFiltrar"></span><span class="text_ocultable"><spring:message code="comu.boto.filtrar"/></span></button>
				</div>
			</div>
		</div>
		<div class ="col-md-2 row pull-right form-buttons">
			<not:inputHidden name="filtreSimpleActiu"/>
			<span id="filtreAvancat" class="botonsTipusFiltre" title="<spring:message code="notificacio.list.boto.filtre.avancat.tooltip"/>"><spring:message code="notificacio.list.boto.filtre.avancat"/></span>
			<span id="filtreSimple" class="botonsTipusFiltre filtreOcult" style="margin-top:5px" title="<spring:message code="notificacio.list.boto.filtre.simple.tooltip"/>"><spring:message code="notificacio.list.boto.filtre.simple"/></span>
		</div>
	</form:form>
	<table
		id="enviament"
		data-toggle="datatable"
		data-url="<c:url value="/enviament/datatable"/>"
		class="table table-striped table-bordered"
		data-default-order="0"
		data-default-dir="desc"
<%--		data-individual-filter="true"--%>
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
				<th data-col-name="createdDate" data-converter="date" data-visible="<c:out value = "${columnes.dataCreacio == true}"/>" ><spring:message code="enviament.list.datacreacio"/></th>
				<th data-col-name="enviadaDate" data-converter="date" data-visible="<c:out value = "${columnes.dataEnviament == true}"/>" ><spring:message code="enviament.list.dataenviament"/></th>
				<th data-col-name="enviamentDataProgramada" data-converter="datetime" data-visible="<c:out value = "${columnes.dataProgramada == true}"/>" ><spring:message code="enviament.list.dataprogramada"/></th>
				<th data-col-name="notificaIdentificador" data-visible="<c:out value = "${columnes.notIdentificador == true}"/>" ><spring:message code="enviament.list.codinotifica"/></th>
				<th data-col-name="grupCodi" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codigrup"/></th>
				<th data-col-name="organEstat" data-visible="false"></th>
				<th data-col-name="organCodiNom" width="360" data-template="#cellOrganGestorTemplate" data-visible="<c:out value = "${columnes.dir3Codi == true}"/>" ><spring:message code="enviament.list.dir3codi"/>
					<script id="cellOrganGestorTemplate" type="text/x-jsrender">
						{{:organCodiNom}}
						{{if organEstat != 'V'}}
							<span class="fa fa-warning text-danger" title="<spring:message code='enviament.list.organGestor.obsolet'/>"></span>
						{{/if}}
 					</script>
				</th>
				<th data-col-name="procedimentTipus" data-visible="false"></th>
				<th data-col-name="procedimentCodiNom" width="300" data-template="#cellProcedimentTemplate" data-visible="<c:out value = "${columnes.proCodi == true}"/>" ><spring:message code="enviament.list.codiprocediment"/>
					<script id="cellProcedimentTemplate" type="text/x-jsrender">
						{{if procedimentTipus == 'PROCEDIMENT'}}<span class="label label-primary">P</span>{{/if}}
						{{if procedimentTipus == 'SERVEI'}}<span class="label label-warning">S</span>{{/if}}
						{{:procedimentCodiNom}}
					</script>
				</th>
				<th data-col-name="usuariCodi" data-visible="<c:out value = "${columnes.usuari == true}"/>" ><spring:message code="enviament.list.usuari"/></th>
				<th data-col-name="concepte" data-visible="<c:out value = "${columnes.concepte == true}"/>" ><spring:message code="enviament.list.concepte"/></th>
				<th data-col-name="descripcio"  data-visible="<c:out value = "${columnes.descripcio == true}"/>" ><spring:message code="enviament.list.descripcio"/></th>
				<th data-col-name="titularNomLlinatge" width="160" data-visible="<c:out value = "${columnes.titularNomLlinatge == true}"/>" ><spring:message code="enviament.list.nomLlinatgetitular"/></th>
				<th data-col-name="destinataris" data-visible="<c:out value = "${columnes.destinataris == true}}"/>" ><spring:message code="enviament.list.destinataris"/></th>
				<th data-col-name="registreNumero" data-visible="<c:out value = "${columnes.numeroRegistre == true}"/>" ><spring:message code="enviament.list.numeroregistre"/></th>
				<th data-col-name="notificaDataCaducitat" width="230" data-converter="datetime" data-visible="<c:out value = "${columnes.dataCaducitat == true}"/>" ><spring:message code="enviament.list.datacaducitat"/></th>
				<th data-col-name="tipusEnviament" class="enviamentTipusCol" width="5px" data-visible="<c:out value = "${columnes.enviamentTipus == true}"/>" ><spring:message code="enviament.list.tipusenviament"/></th>
				<th data-col-name="codiNotibEnviament" width="240" data-visible="<c:out value = "${columnes.codiNotibEnviament == true}"/>" ><spring:message code="enviament.list.referencia.enviament"/></th>
				<th data-col-name="referenciaNotificacio" data-visible="<c:out value = "${columnes.referenciaNotificacio == true}"/>" ><spring:message code="enviament.list.identificador.notificacio"/></th>
				<th data-col-name="csvUuid" data-visible="<c:out value = "${columnes.csvUuid == true}"/>" ><spring:message code="enviament.list.codicsvuuid"/></th>
				<c:choose>
					<c:when test = "${columnes.estat == true}">
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.estat == false}">
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="estatColor" data-visible="false"></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate"   data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						<div class="estatColor {{:estatColor}}">{{:estat}}</div>
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
				<th data-col-name="plazoAmpliable" data-visible="false"></th>
				<th data-orderable="false" data-template="#cellAccionsTemplate" width="190">
					<script id="cellAccionsTemplate" type="text/x-jsrender">

						<div class="dropdown">
                   			<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
                    		<ul class="dropdown-menu dropdown-menu-right">
								<li><a href="<c:url value="/notificacio/{{:notificacioId}}/enviament/{{:id}}"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								<li><a href="<c:url value="/notificacio/{{:notificacioId}}/info"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detall.remesa"/></a></li>
								{{if plazoAmpliable}}
									<li><a href="<c:url value="/notificacio/{{:notificacioId}}/enviament/{{:id}}/ampliacion/plazo"/>" data-toggle="modal"><span class="fa fa-calendar-o"></span>&nbsp;<spring:message code="notificacio.list.accio.massiva.ampliar.plazo.oe"/></a></li>
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

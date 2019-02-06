<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"isRolActualAdministrador",
		es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
pageContext.setAttribute(
		"notificacioComunicacioEnumOptions",
		es.caib.notib.war.helper.EnumHelper.getOptionsForEnum(
				es.caib.notib.core.api.dto.NotificacioTipusEnviamentEnumDto.class,
				"notificacio.tipus.enviament.enum."));
pageContext.setAttribute(
		"notificacioEstatEnumOptions",
		es.caib.notib.war.helper.EnumHelper.getOptionsForEnum(
				es.caib.notib.core.api.dto.NotificacioEstatEnumDto.class,
				"notificacio.estat.enum."));

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
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<style type="text/css">

thead input {
	width: 100%;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    color: #555;
    padding: 4px 7px;
}
table.dataTable thead tr:nth-child(2) .sorting:after {
    opacity: 0.2;
    content: "\e150";
}
thead tr:nth-child(2) {
	background-color: #eceaec;
}

#enviament tbody tr {
	min-height: 35px;
}
div.dataTables_wrapper {
        width: 100%;
}
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}
.buit {
	color: #8e8e8e;
}
.input-group-addon {
	padding: 0;
}
</style>
<script>
$(document).ready(function() {


	
	$('#notificacio').select2({
		width: '100%',
        allowClear:true,
        placeholder: 'Selecciona una opció'//'${placeholderText}'
    });

	$('#estat').select2({
		width: '100%',
        allowClear:true,
        placeholder: 'Selecciona una opció'//'${placeholderText}'
    });
	
	$('.data').datepicker({
		orientation: "bottom"
	});
	
	$('#enviament').on('selectionchange.dataTable', function (e, accio, ids) {
		
		$.get(
				"enviament/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	$('#enviament').on('draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"enviament/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"enviament/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#enviament').webutilDatatable('select-none');
						$('#enviament').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});
	
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
</head>
<body>

	<form:form id="enviamentFiltreForm" action="" method="post" cssClass="well hidden" commandName="enviamentFiltreCommand"></form:form>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="enviament.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="enviament.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<div class="btn-group">
					<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
  						<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="enviament.list.user.exportar"/> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a href="enviament/export/ODS"><spring:message code="enviament.list.user.exportar.EXCEL"/></a></li> 
					</ul>
				</div>
				<div class="btn-group">
						<a href="<c:url value="/enviament/visualitzar"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-eye-slash"></span> <spring:message code="enviament.list.show"/></a>		
				</div>
			</div>
		</div>
	</script>
	<script id="cellFilterTemplate" type="text/x-jsrender">
		<div class="dropdown">
			<button type="submit" id="btnFiltrar" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-search"></span></button>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">enviament/{{:id}}/detall</script>
	<table
		id="enviament"
		data-toggle="datatable"
		data-url="<c:url value="/enviament/datatable"/>"
		class="table table-striped table-bordered nowrap" 
		data-default-order="3"
		data-default-dir="desc"
		data-individual-filter="true"
		data-botons-template="#botonsTemplate"
		data-date-template="#dataTemplate"
		data-cell-template="#cellFilterTemplate"
		data-paging-style-x="true"
		data-scroll-overflow="adaptMax"
		data-mantenir-paginacio="${mantenirPaginacio}"
		data-selection-enabled="true"
		data-save-state="true"
		style="width:100%">
		<thead>
			<tr>
				<c:choose>
					<c:when test = "${columnes.dataEnviament == true}"> 
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.dataEnviament == false}"> 
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name=createdDate data-converter="datetime" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dataenviament"/>
					<script id="dataTemplate" type="text/x-jsrender">
						<div class="from-group">
							<div class="input-group vdivide">
    							<input name="dataEnviamentInici" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon"></div>
    							<input name="dataEnviamentFi" type="text" class="form-control data" placeholder="Final">
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
				<th data-col-name=notificacio.enviamentDataProgramada data-converter="datetime" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dataprogramada"/>
					<script id="dataTemplate" type="text/x-jsrender">
						<div class="from-group input-daterange" data-provide="daterangepicker">
							<div class="input-group vdivide">
    							<input name="dataProgramadaDisposicioInici" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon"></div>
    							<input name="dataProgramadaDisposicioFi" type="text" class="form-control data" placeholder="Final">
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
							<input name="codiNotifica" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codinotifica"/>"/>
						</div>
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
				<th data-col-name="notificacio.procedimentCodiNotib" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codiprocediment"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="codiProcediment" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codiprocediment"/>"/>
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
				<th data-col-name="notificacio.grupCodi" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codigrup"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="grup" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codigrup"/>"/>
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
				<th data-col-name="notificacio.emisorDir3Codi" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.dir3codi"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="dir3Codi" class="form-control" type="text" placeholder="<spring:message code="enviament.list.dir3codi"/>'"/>
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
				<th data-col-name="notificacio.enviamentTipus" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.tipusenviament" />
					<script type="text/x-jsrender">
						<div class="from-group" style="padding: 0; font-weight: 100;">
							<select class="form-control" id="notificacio" name="tipusEnviament">
    							<c:forEach items="${notificacioComunicacioEnumOptions}" var="opt">
        							<option name="tipusEnviament" value="${opt.value != 'buit' ? opt.value : ''}" class="${opt.value != 'buit' ? '' : 'buit'}"><span class="${opt.value != 'buit' ? '' : 'buit'}"><spring:message code="${opt.text}"/></span></option>
    							</c:forEach>
							</select>
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
				<th data-col-name="notificacio.concepte" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.concepte"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="concepte" class="form-control" type="text" placeholder="<spring:message code="enviament.list.concepte"/>"/>
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
				<th data-col-name="notificacio.descripcio"  data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.descripcio"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="descripcio" class="form-control" type="text" placeholder="<spring:message code="enviament.list.descripcio"/>"/>
						</div>
					</script>
				</th>
				<c:choose>
					<c:when test = "${columnes.titularNif == true}"> 
					  <c:set value="true" var="visible"></c:set>
					</c:when>
					<c:when test = "${columnes.titularNif == false}"> 
					  <c:set value="false" var="visible"></c:set>
					</c:when>
				</c:choose>
				<th data-col-name="titularNif" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.niftitular"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="nifTitular" class="form-control" type="text" placeholder="<spring:message code="enviament.list.niftitular"/>'"/>
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
				<th data-col-name="titularNomLlinatges" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.nomLlinatgetitular"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="titularNomLlinatges" class="form-control" type="text" placeholder="<spring:message code="enviament.list.nomLlinatgetitular"/>'"/>
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
							<input name="emailTitular" class="form-control" type="text" placeholder="<spring:message code="enviament.list.emailtitular"/>'"/>
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
				<th data-col-name="destinatariNomLlinatges" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.destinataris"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="destinatariNomLlinatges" class="form-control" type="text" placeholder="<spring:message code="enviament.list.destinataris"/>'"/>
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
				<th data-col-name="notificacio.caducitat" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.datacaducitat"/>
					<script type="text/x-jsrender">
						<div class="from-group" data-provide="daterangepicker">
							<div class="input-group vdivide">
    							<input name="dataCaducitatInici" type="text" class="form-control data" placeholder="Inici">
    							<div class="input-group-addon"></div>
    							<input name="dataCaducitatFi" type="text" class="form-control data" placeholder="Final">
							</div>
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
				<th data-col-name="notifica.notificaCertificacioArxiuId" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.numerocertificatcorreus"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="numeroCertCorreus" class="form-control" type="text" placeholder="<spring:message code="enviament.list.numerocertificatcorreus"/>'"/>
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
				<th data-col-name="notificacio.csv_uuid" data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.codicsvuuid"/>
					<script type="text/x-jsrender">
						<div class="from-group">
							<input name="csvUuid" class="form-control" type="text" placeholder="<spring:message code="enviament.list.codicsvuuid"/>'"/>
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
				<th data-col-name="notificacio.estat"  data-visible="<c:out value = "${visible}"/>" ><spring:message code="enviament.list.estat"/>
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
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="notificacio.id" data-visible="false"></th>
				<th data-col-name="detalls" data-orderable="false" data-template="#cellAccionsTemplate" width="101">
			 	 	<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/notificacio/{{:notificacio.id}}/enviament/{{:id}}"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

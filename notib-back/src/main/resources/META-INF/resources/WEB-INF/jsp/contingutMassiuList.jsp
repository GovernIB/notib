<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="accio.massiva.reintentar.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<style>
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {		
	cursor: pointer;
	width: 2%;
}	
.not-icon-o {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 22px;
	height: 22px;
	background-color: #999999;
	color: white;
	font-weight: bold;
	font-size: 13px;
	margin: 0 auto;
}
.com-icon-o {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 27px;
	height: 27px;
	background-color: #dddddd;
	color: black;
	font-weight: bold;
	font-size: 13px;
	margin: 0 auto; 
}
#notificacio > tbody td:first-child {
	vertical-align: middle;
}
</style>
<script>
var myHelpers = {recuperarEstatEnviament: returnEnviamentsStatusDiv};

$.views.helpers(myHelpers);

function returnEnviamentsStatusDiv(notificacioId) {
	var content = "";
	var getUrl = "<c:url value="/notificacio/"/>" + notificacioId + "/enviament";

	$.getJSON({
	    url: getUrl,
	    success: (user) => {
	    	for (i = 0; i < user.length; i++) {
				content += (user[i].notificaEstat) ? notificacioEnviamentEstats[user[i].notificaEstat] + ',' : '';
			}
	    	if (content !== undefined && content != '') {
	    		content = "("+content.replace(/,\s*$/, "")+")";
	    	}
	    	$('.estat_' + notificacioId).append(content);
	    },
		error: console.log("No s'han pogut recuperar els enviaments de la notificació: " + notificacioId)
	})
}

function formatDate(data) {
	//Añadir ceros a los numeros de un dígito
	Number.prototype.padLeft = function(base,chr){
		var  len = (String(base || 10).length - String(this).length)+1;
			return len > 0? new Array(len).join(chr || '0')+this : this;
		}
	if (data !== null) {
		//dd/MM/yyyy HH:mm:SS
		var procesDate = new Date(data),
		procesDateFormat = [(procesDate.getMonth()+1).padLeft(),
			procesDate.getDate().padLeft(),
			procesDate.getFullYear()].join('/') +' ' +
           [procesDate.getHours().padLeft(),
        	   procesDate.getMinutes().padLeft(),
        	   procesDate.getSeconds().padLeft()].join(':');
		return procesDateFormat;
	} else {
		return null;
	}
}
var notificacioEstats = [];
<c:forEach var="estat" items="${notificacioEstats}">
notificacioEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>
var notificacioEnviamentEstats = [];
<c:forEach var="estat" items="${notificacioEnviamentEstats}">
notificacioEnviamentEstats["${estat.value}"] = "<spring:message code="${estat.text}"/>";
</c:forEach>
// var comunicacioTipus = [];
// <c:forEach var="tipus" items="${notificacioComunicacioTipus}">
// comunicacioTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
// </c:forEach>
// var enviamentTipus = [];
// <c:forEach var="tipus" items="${notificacioEnviamentTipus}">
// enviamentTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
// </c:forEach>
	$(document).ready(function() {
		
		$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
			$.get(
					accio,
					{ids: ids},
					function(data) {
						$("#seleccioCount").html(data);
					}
			);
		});
		$('#taulaDades').on('draw.dt', function () {
			$('#seleccioAll').on('click', function() {
				$.get(
						"select",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('refresh');
						}
				);
				return false;
			});
			$('#seleccioNone').on('click', function() {
				$.get(
						"deselect",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('select-none');
							$('#taulaDades').webutilDatatable('refresh');
						}
				);
				return false;
			});
		});
		$('#btnNetejar').click(function() {
			$(':input', $('#form-filtre')).each (function() {
				var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea') {
					this.value = '';
				} else if (type == 'checkbox' || type == 'radio') {
					this.checked = false;
				} else if (tag == 'select') {
// 					this.selectedIndex = 0;
					$(this).val(null).trigger('change');
				}
			});
			$('#form-filtre').submit();
		});
	});
</script>

</head>
<body>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			<a type="button" class="btn btn-default" href="./notificacions/reintentar" data-refresh-pagina="false">
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="accio.massiva.reintentar.boto"/>
			</a>
		</div>
	</script>

	<form:form id="form-filtre" action="" method="post" cssClass="well" commandName="notificacioErrorCallbackFiltreCommand" >
		<div class="row">
			<div class="col-md-4">
				<not:inputSelect name="procedimentId" optionItems="${procediments}" optionValueAttribute="id" optionTextAttribute="descripcio" placeholderKey="notificacio.list.filtre.camp.procediment" inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
			</div>
			<div class="col-md-4">
				<not:inputText name="concepte" inline="true" placeholderKey="notificacio.list.filtre.camp.concepte"/>
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataInici" placeholderKey="notificacio.list.filtre.camp.datainici" inline="true" required="false" />
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataFi" placeholderKey="notificacio.list.filtre.camp.datafi" inline="true" required="false" />
			</div>
			<div class="col-md-4">
				<not:inputSelect name="estat" optionItems="${notificacioEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"/>
			</div>
			<div class="col-md-4">
				<not:inputSuggest name="usuari" inline="true" placeholderKey="notificacio.list.filtre.camp.usuari" urlConsultaInicial="../userajax/usuariDades" urlConsultaLlistat="../userajax/usuarisDades" suggestValue="codi" suggestText="nom" minimumInputLength="2" />
			</div>

			<div class="col-md-2 pull-right form-buttons" style="text-align: right;">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	
	<table id="taulaDades" 
		data-toggle="datatable" 
		data-url="<c:url value="/massiu/datatable"/>"
		class="table table-bordered table-striped" 
		data-default-dir="desc"
	   	data-filter="#form-filtre"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-save-state="true"
		data-mantenir-paginacio="${mantenirPaginacio}"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="tipusUsuari" data-visible="false">#</th>
				<th data-col-name="errorLastCallback" data-visible="false">#</th>
				<th data-col-name="notificacio.notificaError" data-visible="false"></th>
				<th data-col-name="notificacio.notificaErrorDescripcio" data-visible="false"></th>
				<th data-col-name="enviamentTipus" data-template="#cellEnviamentTipusTemplate" class="enviamentTipusCol" width="3%">

					<script id="cellEnviamentTipusTemplate" type="text/x-jsrender">
						{{if enviamentTipus == 'NOTIFICACIO'}}
							<div class="not-icon-o" title="<spring:message code="es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto.NOTIFICACIO"/>">N</div>
						{{else}}
							<div class="com-icon-o" title="<spring:message code="es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto.COMUNICACIO"/>">C</div>
						{{/if}}
					</script>
				</th>
				<th data-col-name="createdDate" data-converter="datetime" width="10%"><spring:message code="notificacio.list.columna.enviament.data"/></th>
				<th data-col-name="createdBy.nom" width="10%"><spring:message code="notificacio.list.filtre.camp.usuari"/></th>
				<th data-col-name="procediment.nom"  width="150px"><spring:message code="notificacio.list.columna.procediment"/></th>
				<th data-col-name="concepte" width="15%" ><spring:message code="notificacio.list.columna.concepte"/></th>
				<th data-col-name="estatDate" data-converter="datetime" data-visible="false"></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate"  width="120px">
					<spring:message code="notificacio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<span class="fa fa-clock-o"></span>
						{{else estat == 'ENVIADA'}}
							<span class="fa fa-send-o"></span>
						{{else estat == 'FINALITZADA'}}
							<span class="fa fa-check"></span>
						{{else estat == 'REGISTRADA'}}
							<span class="fa fa-file-o"></span>
						{{else estat == 'PROCESSADA'}}
							<span class="fa fa-check-circle"></span>
						{{/if}}
						{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{if estat == 'PROCESSADA' && estatDate != ''}}
							<br>
							<p class="horaProcessat">{{:~eval('formatDate(' + estatDate+ ')')}}</p>
						{{/if}}
						{{if estat == 'FINALITZADA'}}
							{{:~recuperarEstatEnviament(id)}}
							<p class="estat_{{:id}}"  style="display:inline"></p>
						{{/if}}

						{{if notificaError}}<span class="fa fa-warning text-danger" title="{{>errorNotificaDescripcio}}"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="40px">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/massiu/detallErrorCallback/{{:id}}"/>" class="btn btn-default" data-toggle="modal" data-height="450px" data-processar="true"><span class="fa fa-info-circle"></span>&nbsp; <spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
</body>
</html>
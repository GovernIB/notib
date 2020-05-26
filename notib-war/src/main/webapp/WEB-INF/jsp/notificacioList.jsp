<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministrador(request));
%>
<c:set var="ampladaConcepte">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">200px</c:when>
		<c:otherwise>300px</c:otherwise>
	</c:choose>
</c:set>
<c:set var="ampladaEnviament">
	<c:choose>
		<c:when test="${isRolActualAdministrador}">160px</c:when>
		<c:otherwise>130px</c:otherwise>
	</c:choose>
</c:set>
<c:set var="refresh_state_succes"><spring:message code="notificacio.list.enviament.list.refresca.estat.exitos"/></c:set>
<c:set var="refresh_state_error"><spring:message code="notificacio.list.enviament.list.refresca.estat.error"/></c:set>
<html>
<head>
	<title><spring:message code="notificacio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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

<style type="text/css">
.horaProcessat {
	font-size: small;
}
.datepicker table tr td.today, .datepicker table tr td.today:hover {
	color: #000000;
	background: #a4a4a4 !important;
	background-color: #a4a4a4 !important;
}
.panel.panel-default.info-enviament {
	margin-top: 6%;
}
.motiu_finalitzada {
	background-color: #e0ead5;
}
.info_finalitzada_icon:hover {
	cursor: pointer;
}
.info_finalitzada_div {
	text-align: right;
}
.info_finalitzada_icon {
	top: -16px;
}
.info_finalitzada_link:hover {
	text-decoration: none !important;
}
.motiu_finalitzada > a {
	display: none;
}
.panel-heading.processarButton {
	background-color: red;
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
<script type="text/javascript">
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

//function returnProcessarUrl(permisProcessar, notificacioId) {
//	var url;
//	if(permisProcessar) {
//		url = '<a href="<c:url value="/notificacio/' + notificacioId + '/processar"/>" class="btn btn-info btn-xs pull-right"  data-toggle="modal" data-modal-id="modal-processar"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a>';
//	}
//	if (url !== undefined) {
//		return  url.replace(/"/g, "'");
//	} else {
//		return  url;
//	}
//}
//async function resolve(notificacioId) {
//	var content;
//	var getUrl = "<c:url value="/notificacio/"/>" + notificacioId + "/enviament";
//	
//	const reposResponse = await fetch(getUrl);
//	const userRepos = await reposResponse.json();
//	
//	if (userRepos != null) {
//		for (i = 0; i < userRepos.length; i++) {
//			content = (userRepos[i].notificaEstat) ? notificacioEnviamentEstats[userRepos[i].notificaEstat] + ',' : '';
//		}
//	}
//	if (content !== undefined) {
//		content = "("+content.replace(/,\s*$/, "")+")";
//	}
//	//console.log(content);
//	return content;
//}

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
var comunicacioTipus = [];
<c:forEach var="tipus" items="${notificacioComunicacioTipus}">
comunicacioTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
var enviamentTipus = [];
<c:forEach var="tipus" items="${notificacioEnviamentTipus}">
enviamentTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
$(document).ready(function() {
	$('#notificacio').on('rowinfo.dataTable', function(e, td, rowData) {
		var getUrl = "<c:url value="/notificacio/"/>" + rowData.id + "/enviament";
	    $.get(getUrl).done(function(data) {
	    	$(td).empty();
	    	$(td).append(
	    			'<table class="table teble-striped table-bordered">' +
	    			'<caption><spring:message code="notificacio.list.enviament.list.titol"/></caption>' +
	    			'<thead>' +
	    			'<tr>' +
					'<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' + 
	    			'<th><spring:message code="notificacio.list.enviament.list.destinataris"/></th>' +
	    			'<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
	    			'<th></th>' +
	    			'</tr>' +
					'</thead><tbody></tbody></table>');
	    	contingutTbody = '';
			for (i = 0; i < data.length; i++) {
				var llinatge1 = '', llinatge2 = '', destinataris = '', nif = '';
				if (data[i].titular.llinatge1 != null) {
					llinatge1 = data[i].titular.llinatge1;
				}
				if (data[i].titular.llinatge2 != null) {
					llinatge2 = data[i].titular.llinatge2;
				}
				
				$.each(data[i].destinataris, function (index, destinatari) {
					var llinatge1Dest = '', llinatge2Dest = '';
					if (destinatari.llinatge1 != null) {
						llinatge1Dest = destinatari.llinatge1;
					}
					if (destinatari.llinatge2 != null) {
						llinatge2Dest = destinatari.llinatge2;
					}
					if (destinatari.nif != null) {
						nif = destinatari.nif;
					} else {
						nif = destinatari.dir3Codi;
					}
					destinataris += destinatari.nom + ' ' + llinatge1Dest + ' ' + llinatge2Dest + ' (' + nif + '), ';
				});
				if (data[i].titular.nif != null) {
					nif = data[i].titular.nif;
				} else {
					nif = data[i].titular.dir3Codi;
				}
				contingutTbody += '<tr>';
				contingutTbody += '<td>' + data[i].titular.nom + ' ' + llinatge1 + ' ' + llinatge2 + '('+ nif +') </td>';
				if (destinataris != ''){
					//Remove last white space
					destinataris = destinataris.substr(0, destinataris.length-1);
					//Remove last comma
					destinataris = destinataris.substr(0, destinataris.length-1);
				} else {
					destinataris = '<spring:message code="notificacio.list.enviament.list.sensedestinataris"/>';
				}
				contingutTbody += '<td>' + destinataris + '</td>';
				contingutTbody += '<td>';
				//contingutTbody += (data[i].notificacio.estat) ? notificacioEnviamentEstats[data[i].notificacio.estat] : '';
				contingutTbody += (data[i].notificaEstat) ? notificacioEnviamentEstats[data[i].notificaEstat] : '';
				if (data[i].notificacio.notificaError) {
					var errorTitle = '';
					if (data[i].notificacio.notificaErrorDescripcio) {
						errorTitle = data[i].notificacio.notificaErrorDescripcio;
					}
					var escaped = data[i].notificacio.notificaErrorDescripcio.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
					contingutTbody += ' <span class="fa fa-warning text-danger" title="' + escaped + '"></span>';
				}
				contingutTbody += '</td>';
				contingutTbody += '<td width="114px">';
				if (data[i].notificaCertificacioData != null) {
					contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/certificacioDescarregar"/>" class="btn btn-default btn-sm" title="<spring:message code="enviament.info.accio.descarregar.certificacio"/>"><span class="fa fa-download"></span></a>';
				} else if (data[i].notificacio.estat == 'REGISTRADA' &&
						(data[i].registreEstat == 'DISTRIBUIT' || data[i].registreEstat == 'OFICI_EXTERN' || data[i].registreEstat == 'OFICI_SIR')) {
					contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '/justificantDescarregar"/>" class="btn btn-default btn-sm" title="<spring:message code="enviament.info.accio.descarregar.justificant"/>"><span class="fa fa-download"></span></a>';
				}
				contingutTbody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + data[i].id + '"/>" data-toggle="modal" class="btn btn-default btn-sm"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>';
				contingutTbody += '</td>';
				contingutTbody += '</tr>';
			}
			$('table tbody', td).append(contingutTbody);
			$('table tbody td').webutilModalEval();
		});
	});
	$('#btnNetejar').click(function() {
		$(':input', $('#filtre')).each (function() {
			var type = this.type, tag = this.tagName.toLowerCase();
			if (type == 'text' || type == 'password' || tag == 'textarea') {
				this.value = '';
			} else if (type == 'checkbox' || type == 'radio') {
				this.checked = false;
			} else if (tag == 'select') {
				this.selectedIndex = 0;
			}
		});
		$('#form-filtre').submit();
	});
});
</script>
</head>
<body>
<div id="msg-box"></div>
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="notificacioFiltreCommand">
		<div class="row">
			<c:if test="${isRolActualAdministrador && mostrarColumnaEntitat}">
				<div class="col-md-3">
					<not:inputSelect name="entitatId" optionItems="${entitat}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.entitat" inline="true"/>
				</div>
			</c:if>
			<div class="col-md-2">
				<not:inputSelect name="enviamentTipus" optionItems="${notificacioEnviamentTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.enviament.tipus" inline="true"/>
			</div>
			<%--div class="col-md-2">
				<not:inputSelect name="comunicacioTipus" optionItems="${notificacioComunicacioTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.comunicacio.tipus" inline="true"/>
			</div--%>
			<div class="col-md-4">
				<not:inputText name="concepte" inline="true"  placeholderKey="notificacio.list.filtre.camp.concepte"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect name="estat" optionItems="${notificacioEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.estat" inline="true"/>
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataInici" placeholderKey="notificacio.list.filtre.camp.datainici" inline="true" required="false" />
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataFi" placeholderKey="notificacio.list.filtre.camp.datafi" inline="true" required="false" />
			</div>
			<div class="col-md-2">
				<not:inputText name="titular" inline="true" placeholderKey="notificacio.list.filtre.camp.titular"/>
			</div>
			<div class="col-md-4">
				<not:inputSelect name="procedimentId" optionItems="${procedimentsPermisLectura}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="notificacio.list.filtre.camp.procediment" inline="true"/>
			</div>
			<div class="col-md-2">
				<not:inputSelect name="tipusUsuari" optionItems="${tipusUsuari}" optionValueAttribute="value" optionTextKeyAttribute="text"  emptyOption="true"  placeholderKey="notificacio.list.filtre.camp.tipususuari" inline="true" />
			</div>
			<div class="col-md-2">
				<not:inputText name="numExpedient" inline="true" placeholderKey="notificacio.list.filtre.camp.numexpedient"/>
			</div>
			<div class="col-md-2 pull-right form-buttons">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	<table
		id="notificacio"
		data-toggle="datatable"
		data-url="<c:url value="/notificacio/datatable"/>"
		data-search-enabled="false"
		data-default-order="4"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-row-info="true"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="notificacio.notificaError" data-visible="false"></th>
				<th data-col-name="notificacio.notificaErrorDescripcio" data-visible="false"></th>
				<th data-col-name="enviamentTipus" data-template="#cellEnviamentTipusTemplate" class="enviamentTipusCol" width="5px">

					<script id="cellEnviamentTipusTemplate" type="text/x-jsrender">
						{{if enviamentTipus == 'NOTIFICACIO'}}
							<div class="not-icon-o" title="<spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.NOTIFICACIO"/>">N</div>
						{{else}}
							<div class="com-icon-o" title="<spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.COMUNICACIO"/>">C</div>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name=createdDate data-converter="datetime" width="${ampladaEnviament}"><spring:message code="notificacio.list.columna.enviament.data"/></th>
				<c:if test="${isRolActualAdministrador && mostrarColumnaEntitat}">
					<th data-col-name="entitat.nom" width="170px"><spring:message code="notificacio.list.columna.entitat"/></th>
				</c:if>
				<%--th data-col-name="comunicacioTipus" data-template="#cellComunicacioTipusTemplate">
					<spring:message code="notificacio.list.columna.tipus.comunicacio"/>
					<script id="cellComunicacioTipusTemplate" type="text/x-jsrender">
						{{:~eval('comunicacioTipus["' + comunicacioTipus + '"]')}}
					</script>
				</th--%>
				<th data-col-name="procediment.nom"  width="200px"><spring:message code="notificacio.list.columna.procediment"/></th>
				<c:if test="${mostrarColumnaNumExpedient}">
					<th data-col-name="numExpedient" width="170px"><spring:message code="notificacio.list.columna.num.expedient"/></th>
				</c:if>
				<th data-col-name="concepte" width="${ampladaConcepte}" ><spring:message code="notificacio.list.columna.concepte"/></th>
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
				<th data-col-name=createdBy.codi data-converter="String" width="80px"><spring:message code="notificacio.list.columna.enviament.creada"/></th>
				<th data-col-name="permisProcessar" data-visible="false">
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="60px">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/notificacio/{{:id}}"/>" data-toggle="modal" data-height="700px" data-processar="true"><span class="fa fa-info-circle"></span>&nbsp; <spring:message code="comu.boto.detalls"/></a></li>
							{{if permisProcessar }}
								<li><a href="<c:url value="/notificacio/{{:id}}/processar"/>" data-toggle="modal"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a></li>
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

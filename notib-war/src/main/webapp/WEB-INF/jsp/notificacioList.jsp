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
		<c:when test="${isRolActualAdministrador}">35%</c:when>
		<c:otherwise>55%</c:otherwise>
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
<script type="text/javascript">
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
	    			'<table class="table teble-striped table-bordered"><thead>' +
	    			'<tr>' +
					'<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' + 
	    			//'<th><spring:message code="notificacio.list.enviament.list.destinatari"/></th>' + 
	    			'<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
	    			'<th></th>' +
	    			'</tr>' +
					'</thead><tbody></tbody></table>');
	    	contingutTbody = '';
			for (i = 0; i < data.length; i++) {
				contingutTbody += '<tr>';
				contingutTbody += '<td>' + data[i].titular.nom + '</td>';
				//contingutTbody += '<td>' + data[i].destinatari + '</td>';
				contingutTbody += '<td>';
				contingutTbody += (data[i].notificacio.estat) ? notificacioEnviamentEstats[data[i].notificacio.estat] : '';
				if (data[i].notificacio.notificaError) {
					var errorTitle = '';
					if (data[i].notificacio.notificaErrorDescripcio) {
						errorTitle = data[i].notificacio.notificaErrorDescripcio;
					}
					var escaped = data[i].notificacio.notificaErrorDescripcio.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
					contingutTbody += ' <span class="fa fa-warning text-danger" title="' + escaped + '"></span>';
				}
				contingutTbody += '</td>';
				contingutTbody += '<td width="5%">';
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
			<c:if test="${isRolActualAdministrador}">
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
			<div class="col-md-3">
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
			<div class="col-md-3">
				<not:inputText name="titular" inline="true" placeholderKey="notificacio.list.filtre.camp.titular"/>
			</div>
			<div class="col-md-3">
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
		data-default-order="3"
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
				<th data-col-name=createdDate data-converter="datetime" width="15%"><spring:message code="notificacio.list.columna.enviament.data"/></th>
				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="entitat.nom" width="20%"><spring:message code="notificacio.list.columna.entitat"/></th>
				</c:if>
				<th data-col-name="enviamentTipus" data-template="#cellEnviamentTipusTemplate">
					<spring:message code="notificacio.list.columna.tipus.enviament"/>
					<script id="cellEnviamentTipusTemplate" type="text/x-jsrender">
						{{:~eval('enviamentTipus["' + enviamentTipus + '"]')}}
					</script>
				</th>
				<%--th data-col-name="comunicacioTipus" data-template="#cellComunicacioTipusTemplate">
					<spring:message code="notificacio.list.columna.tipus.comunicacio"/>
					<script id="cellComunicacioTipusTemplate" type="text/x-jsrender">
						{{:~eval('comunicacioTipus["' + comunicacioTipus + '"]')}}
					</script>
				</th--%>
				<th data-col-name="concepte" width="${ampladaConcepte}"><spring:message code="notificacio.list.columna.concepte"/></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="20%">
					<spring:message code="notificacio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<span class="fa fa-clock-o"></span>
						{{else estat == 'ENVIADA'}}
							<span class="fa fa-send-o"></span>
						{{else estat == 'FINALITZADA'}}
							<span class="fa fa-check"></span>
						{{/if}}
						{{:~eval('notificacioEstats["' + estat + '"]')}}
						{{if notificaError}}<span class="fa fa-warning text-danger" title="{{>errorNotificaDescripcio}}"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="permisProcessar" data-visible="false">
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="5%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
							{{if permisProcessar }}
								<li><a href="<c:url value="/notificacio/{{:id}}/processar"/>"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.processar"/></a></li>
							{{/if}}
								<li><a href="<c:url value="/notificacio/{{:id}}"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp; <spring:message code="comu.boto.detalls"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>

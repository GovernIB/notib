<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>

<head>
	<title><spring:message code="notificacio.form.titol" arguments="${servei.descripcio}"/></title>
	<not:modalHead/>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	
	<link href="<c:url value="/css/notificacio.css"/>" rel="stylesheet" type="text/css">
	
	<script type="text/javascript">

		$(document).ready(function(){
			
		    $('#destinataris').on('rowinfo.dataTable', function(e, td, rowData) {
		        $.get("<c:url value="/consulta/${notificacioId}/destinatari/"/>" + rowData.id).done(function(data) {
		        	$(td).append(
		        			
		        			'<table class="table teble-striped table-bordered"><thead>' +
		        			
		        			'<tr>' +
			        			'<th><spring:message code="notificacio.form.destinatari.referencia"/></th>' + 
		                        '<td>' + data.referencia + '</td>' + 
		                        '<th><spring:message code="notificacio.form.destinatari.titular.nom"/></th>' + 
	                            '<td>' + data.titularNom + '</td>' +
	                        '</tr>' +
	                        '<tr>' +
	                        	'<th><spring:message code="notificacio.form.destinatari.titular.llinatges"/></th>' + 
	                            '<td>' + data.titularLlinatges + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.titular.nif"/></th>' + 
	                            '<td>' + data.titularNif + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.titular.telefon"/></th>' + 
	                            '<td>' + data.titularTelefon + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.titular.email"/></th>' + 
	                            '<td>' + data.titularEmail + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.destinatai.nom"/></th>' + 
	                            '<td>' + data.destinatariNom + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.destinatari.llinatges"/></th>' + 
	                            '<td>' + data.destinatariLlinatges + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.destinatari.nif"/></th>' + 
	                            '<td>' + data.destinatariNif + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.destinatari.telefon"/></th>' + 
	                            '<td>' + data.destinatariTelefon + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.destinatari.email"/></th>' + 
	                            '<td>' + data.destinatariEmail + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.tipus"/></th>' + 
	                            '<td>' + data.domiciliTipus + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.concret.tipus"/></th>' + 
	                            '<td>' + data.domiciliConcretTipus + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.via.tipus"/></th>' + 
	                            '<td>' + data.domiciliViaTipus + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.via.nom"/></th>' + 
	                            '<td>' + data.domiciliViaNom + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.numeracio.tipus"/></th>' + 
	                            '<td>' + data.domiciliNumeracioTipus + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.numeracio.numero"/></th>' + 
	                            '<td>' + data.domiciliNumeracioNumero + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.numeracio.puntkm"/></th>' + 
	                            '<td>' + data.domiciliNumeracioPuntKm + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.apartat.correus"/></th>' + 
	                            '<td>' + data.domiciliApartatCorreus + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.bloc"/></th>' + 
	                            '<td>' + data.domiciliBloc + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.portal"/></th>' + 
	                            '<td>' + data.domiciliPortal + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domisili.escala"/></th>' + 
	                            '<td>' + data.domiciliEscala + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.planta"/></th>' + 
	                            '<td>' + data.domiciliPlanta + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.porta"/></th>' + 
	                            '<td>' + data.domiciliPorta + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.complement"/></th>' + 
	                            '<td>' + data.domiciliComplement + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.poblacio"/></th>' + 
	                            '<td>' + data.domiciliPoblacio + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.municipi.codi.ine"/></th>' + 
	                            '<td>' + data.domiciliMunicipiCodiIne + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.municipi.nom"/></th>' + 
	                            '<td>' + data.domiciliMunicipiNom + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.codi.postal"/></th>' + 
	                            '<td>' + data.domiciliCodiPostal + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.provincia.codi"/></th>' + 
	                            '<td>' + data.domiciliProvinciaCodi + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.provincia.nom"/></th>' + 
	                            '<td>' + data.domiciliProvinciaNom + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.pais.codi.iso"/></th>' + 
	                            '<td>' + data.domiciliPaisCodiIso + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.pais.nom"/></th>' + 
	                            '<td>' + data.domiciliPaisNom + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.linea1"/></th>' + 
	                            '<td>' + data.domiciliLinea1 + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.domicili.linea2"/></th>' + 
	                            '<td>' + data.domiciliLinea2 + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.domicili.cie"/></th>' + 
	                            '<td>' + data.domiciliCie + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.deh.obligat"/></th>' + 
	                            '<td>' + data.dehObligat + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.deh.nif"/></th>' + 
	                            '<td>' + data.dehNif + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.deh.procediment.codi"/></th>' + 
	                            '<td>' + data.dehProcedimentCodi + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.servei.tipus"/></th>' + 
	                            '<td>' + data.serveiTipus + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.retard.postal"/></th>' + 
	                            '<td>' + data.retardPostal + '</td>' +
	                            '<th><spring:message code="notificacio.form.destinatari.caducitat"/></th>' + 
	                            '<td>' + data.caducitat + '</td>' +
                            '</tr>' +
                            '<tr>' +
                            	'<th><spring:message code="notificacio.form.destinatari.referencia.notifica"/></th>' + 
                            	'<td>' + data.referenciaNotifica + '</td>' +
                            	'<th></th>' + 
                            	'<td></td>' +
							'</tr>' +
							
							'</thead><tbody></tbody></table>' 
							
		        	);
		        })		        	  
		    });
		    
    		$('#callPDF').click(function(e) {  
    			 $.get("<c:url value="/consulta/showpdf/${notificacioId}"/>");
    		});
			
		});
		
	</script>
	
</head>
<body>
	<c:set var="formAction"><not:modalUrl value="/consulta/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal well" commandName="notificacioCommand" role="form">
		
		<h3 class="notificacio-header"><spring:message code="notificacio.form.dadesnotificacio"/></h3>
		
		<div class="col-xs-6 labelin2">
			<not:inputText name="enviamentTipus" textKey="notificacio.form.camp.tipusenviament" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-6 labelin2">
			<not:inputText name="enviamentDataProgramada" textKey="notificacio.form.camp.enviament.data.programada" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-12">
			<not:inputText name="concepte" textKey="notificacio.form.camp.concepte" disabled="true" labelSize="2"/>
		</div>
		<div class="col-xs-6 labelin2">
			<not:inputText name="procedimentCodiSia" textKey="notificacio.form.camp.procediment.codi.sia" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-6">
			<not:inputText name="estatNotifica" textKey="notificacio.form.camp.estat.notifica" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-12 labelin2">
			<not:inputText name="procedimentDescripcioSia" textKey="notificacio.form.camp.procediment.descripcio.sia" disabled="true" labelSize="2"/>
		</div>
		<div class="col-xs-6">
			<not:inputText name="documentSha1" textKey="notificacio.form.camp.document.sha1" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-6 labelin2">
			<not:inputText name="documentNormalitzat" textKey="notificacio.form.camp.document.normalitzat" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-6">
			<not:inputText name="documentGenerarCsv" textKey="notificacio.form.camp.document.generar.csv" disabled="true" labelSize="4"/>
		</div>
		<div class="col-xs-6">
			<not:inputText name="estat" textKey="notificacio.form.camp.estat" disabled="true" labelSize="4"/>
		</div>
		
	</form:form>
	
	<div class="well">
	
	<h3><spring:message code="notificacio.form.destinataris"/></h3>
	
	<table
		id="destinataris"
		data-toggle="datatable"
		data-url="<c:url value="/consulta/${notificacioId}/destinataris"/>"
		data-search-enabled="false"
		data-default-order="2"
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		data-paging="false"
		data-info="false"
		data-row-info="true"
		class="table table-striped table-bordered"
		style="width:100%">
		
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				
				<th data-col-name="referencia"><spring:message code="notificacio.form.destinataris.referencia"/></th>
				<th data-col-name="titularNom"><spring:message code="notificacio.form.destinataris.titular.nom"/></th>
				<th data-col-name="titularNif"><spring:message code="notificacio.form.destinataris.titular.nif"/></th>
				<th data-col-name="destinatariNom"><spring:message code="notificacio.form.destinataris.destinatari.nom"/></th>
				<th data-col-name="destinatariNif"><spring:message code="notificacio.form.destinataris.destinatari.nif"/></th>
				
				<%--
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/consulta/${notificacioId}/destinatari/{{:id}}"/>" class="btn btn-primary" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="notificacio.form.destinataris.destinatari.informacio"/></a>
					</script>
				</th>
				--%>
				
			</tr>
		</thead>
		
	</table>
	
	</div>
	
	<div class="well">
	
	<h3><spring:message code="notificacio.form.events"/></h3>
	
	<table
		id="events"
		data-toggle="datatable"
		data-url="<c:url value="/consulta/${notificacioId}/events"/>"
		data-search-enabled="false"
		data-default-order="2"
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		data-paging="false"
		data-info="false"
		class="table table-striped table-bordered"
		style="width:100%">
		
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				
				<th data-col-name="tipus"><spring:message code="notificacio.form.events.tipus"/></th>
				<th data-col-name="data" data-converter="date"><spring:message code="notificacio.form.events.data"/></th>
				<th data-col-name="error" data-template="#cellError">
					<spring:message code="notificacio.form.events.error"/>
					<script id="cellError" type="text/x-jsrender">
						{{if error}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="descripcio"><spring:message code="notificacio.form.events.error.descripcio"/></th>
				
				<th data-col-name="destinatari.referencia"><spring:message code="notificacio.form.events.destinatari.referencia"/></th>
				
				<%-- 
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/consulta/{{:id}}"/>" class="btn btn-primary" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="notificacio.form.destinataris.destinatari.informacio"/></a>
					</script>
				</th>
				--%>
				
			</tr>
		</thead>
		
	</table>
	
	</div>
	
	<%-- <div class="well">
	
	<h3><spring:message code="notificacio.form.enviaments"/></h3>
	
	<table
		id="notificacions"
		data-toggle="datatable"
		data-url="<c:url value="/consulta/${notificacioId}/datatable"/>"
		data-search-enabled="false"
		data-default-order="2"
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		data-paging="false"
		data-info="false"
		class="table table-striped table-bordered"
		style="width:100%">
		
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				
				<th data-col-name="desti"><spring:message code="notificacio.form.enviament.list.desti"/></th>
				<th data-col-name="data" data-converter="date"><spring:message code="notificacio.form.enviament.list.data"/></th>
				<th data-col-name="estat"><spring:message code="notificacio.form.enviament.list.estat"/></th>
				
				<th data-col-name="error" data-orderable="false" data-template="#cellErrorTemplate" width="10%">
					<script id="cellErrorTemplate" type="text/x-jsrender">
						{{if error != null}}
							<a href="entitat/{{:id}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-search-plus"></span>&nbsp;&nbsp;<spring:message code="notificacio.form.enviament.list.error"/></a>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name="estat" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						{{if estat != "Finalitzada"}}
							<a href="<c:url value="/consulta/{{:id}}"/>" class="btn btn-primary" data-toggle="modal"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="notificacio.form.enviament.list.reenvia"/></a>
						{{/if}}
					</script>
				</th>
				
			</tr>
		</thead>
		
	</table>
	
	</div> --%>
	
	<script id="botonsTemplate" type="text/x-jsrender"></script>
	
	<div class="well">
		<div class="col-xs-11"> <h3><spring:message code="notificacio.form.document.enviat"/></h3> </div>
		<a><div id="callPDF" > <span class="fa fa-file-pdf-o"></span> </div></a>
	</div>
	
	<div id="modal-botons" class="col-xs-12 well">
		<a href="<c:url value="/consulta"/>" class="btn btn-default" data-modal-cancel="true"> <span class="fa fa-reply"></span>&nbsp;<spring:message code="comu.boto.tornar"/> </a>
	</div>
		
</body>

</html>

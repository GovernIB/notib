<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<title><spring:message code="notificacio.info.titol" /></title>
<script
	src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
<script
	src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
<link
	href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>"
	rel="stylesheet"></link>
<script
	src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
<not:modalHead />
<script type="text/javascript">
$(function() {
    $(document).on("click", "a.fileDownloadSimpleRichExperience", function() {
        $.fileDownload($(this).attr('href'), {
            preparingMessageHtml: "Estam preparant la descàrrega, per favor esperi...",
            failMessageHtml: "<strong style='color:red'>Ho sentim.<br/>S'ha produït un error intentant descarregar el document.</strong>"
        });
        return false; //this is critical to stop the click event which will trigger a normal file download!
    });
});

var eventTipus = [];
<c:forEach var="tipus" items="${eventTipus}">
eventTipus["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>
$(document).ready(function() {
	$('#events').on('rowinfo.dataTable', function(e, td, rowData) {

			$(td).empty();
			$(td).append('<textarea style="width:100%" rows="10">' + rowData['errorDescripcio'] + '</textarea>');
	});
	$('#events').on('draw.dt', function(e, settings) {
		var api = new $.fn.dataTable.Api(settings);
		api.rows().every(function(rowIdx, tableLoop, rowLoop) {
			var data = this.data();
			if (!data.error) {
			$('td:last-child', this.node()).empty();
			}
		});
	});
	$(document.body).on('hidden.bs.modal', function () {
		$('.tab-content').load(location.href + " .tab-content");
	});
});
</script>
<style type="text/css">
.modal-backdrop {
    visibility: hidden !important;
}
.modal.in {
    background-color: rgba(0,0,0,0.5);
}
.btn-certificacio {
	margin-top: 5%;
}
</style>
</head>
<body>
	<c:if test="${notificacio.notificaError}">
		<div class="alert alert-danger well-sm">
			<span class="fa fa-warning text-danger"></span>
			<spring:message code="notificacio.info.error.titol" />
			<button class="btn btn-default btn-xs pull-right"
				data-toggle="collapse" data-target="#collapseError"
				aria-expanded="false" aria-controls="collapseError">
				<span class="fa fa-bars"></span>
			</button>
			<div id="collapseError" class="collapse">
				<br />
				<table class="table table-bordered"
					style="background-color: white; width: 100%">
					<tbody>
						<tr>
							<td width="10%"><strong><spring:message
										code="notificacio.info.error.data" /></strong></td>
							<td><fmt:formatDate value="${notificacio.notificaErrorData}"
									pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.error.error" /></strong></td>
							<td><textarea rows="10" style="width: 100%">${fn:escapeXml(notificacio.notificaErrorDescripcio)}</textarea></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"> 
				<spring:message code="notificacio.info.tab.dades" />
			</a>
		</li>
		<li role="presentation">
			<a href="#events" aria-controls="events" role="tab" data-toggle="tab"> 
				<spring:message code="notificacio.info.tab.events" />
			</a>
		</li>
		<c:if test="${permisGestio == null || permisGestio}">
			<li role="presentation">
				<a href="#accions" aria-controls="accions" role="tab" data-toggle="tab"> 
					<spring:message code="notificacio.info.tab.accions" />
				</a>
			</li>
		</c:if>
	</ul>
	<br />
	<div class="tab-content">
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'dades'}"> active</c:if>"
			id="dades">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<strong><spring:message code="notificacio.info.seccio.dades" /></strong>
					</h3>
				</div>
				<table class="table table-bordered" style="width: 100%">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="notificacio.info.dada.entitat" /></strong></td>
							<td>${notificacio.procediment.organGestorNom}<br>
							<small>${notificacio.procediment.organGestor}</small></td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.concepte" /></strong></td>
							<td>${notificacio.concepte}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.descripcio" /></strong></td>
							<td>${notificacio.descripcio}</td>
						</tr>
						<c:if test="${notificacio.estat != null && notificacio.estat != ''}">
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.estat" /></strong></td>
							<td><spring:message code="es.caib.notib.core.api.dto.NotificacioEstatEnumDto.${notificacio.estat}" /></td>
						</tr>
						</c:if>
						<tr>
							<td><strong><spring:message
										code="notificacio.info.dada.creacio.data" /></strong></td>
							<td><fmt:formatDate value="${notificacio.createdDate}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.creacio.usuari" /></strong></td>
							<td>${notificacio.createdBy.nom}
								(${notificacio.createdBy.codi})</td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.tipus" /></strong></td>
							<td><spring:message code="es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto.${notificacio.enviamentTipus}" /></td>
						</tr>
						<tr>
							<td><strong><spring:message code="notificacio.info.dada.procediment.codi" /></strong></td>
							<td>${notificacio.procediment.nom}<br>${notificacio.procediment.codi}</td>
						</tr>
					</tbody>
				</table>
			</div>
			<c:if test="${not empty notificacio.grup}">
				<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<strong><spring:message
										code="notificacio.info.seccio.grup" /></strong>
							</h3>
						</div>
						<table class="table table-bordered" style="width: 100%">
							<tbody>
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.grup.codi" /></strong>
									</td>
									<td>
									${notificacio.grup.codi}
									</td>
								</tr>
								<tr>
									<td>
									<strong><spring:message code="notificacio.info.grup.nom" /></strong>
									</td>
									<td>
										${notificacio.grup.nom}
									</td>
								</tr>
							</tbody>
						</table>
				</div>
			</c:if>
			<c:if test="${not empty notificacio.document}">
				<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<strong><spring:message code="notificacio.info.seccio.document" /></strong>
							</h3>
						</div>
						<table class="table table-bordered" style="width: 100%">
							<tbody>
								<tr>
									<td width="30%">
										<strong><spring:message	code="notificacio.info.document.arxiu.nom" /></strong>
									</td>
									<td>${notificacio.document.arxiuNom}
										<a id="descarregarDocument" href="<c:url value="/modal/notificacio/${notificacio.id}/documentDescarregar"/>"
											class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience"
											title="<spring:message code="notificacio.info.document.descarregar"/>">
											<spring:message code="notificacio.info.document.descarregar"/>
												<span class="fa fa-download"></span>
										</a>
									</td>
								</tr>
								<tr>
									<td><strong><spring:message code="notificacio.info.document.normalitzat" /></strong></td>
									<td>
									<c:choose>
									    <c:when test="${notificacio.document.normalitzat}">
									       Si
									    </c:when>
									    <c:otherwise>
									        No
									    </c:otherwise>
									</c:choose>
									</td>
								</tr>
								<tr>
									<td><strong><spring:message code="notificacio.info.document.generar.csv" /></strong></td>
									<td>
									<c:choose>
									    <c:when test="${notificacio.document.generarCsv}">
									       Si
									    </c:when>
									    <c:otherwise>
									        No
									    </c:otherwise>
									</c:choose>
									</td>
								</tr>
							</tbody>
						</table>
				</div>
			</c:if>
			<c:if test="${not empty notificacio.procediment.pagadorpostal}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.postal" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if
								test="${not empty notificacio.procediment.pagadorpostal.dir3codi}">
								<tr>
									<td width="30%"><strong><spring:message
												code="notificacio.info.pagador.correus.codi.dir3" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.dir3codi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.contracte" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.contracteNum}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.client" /></strong></td>
									<td>${notificacio.procediment.pagadorpostal.facturacioClientCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.correus.vigencia" /></strong></td>
									<td>
									<fmt:formatDate pattern="dd/MM/yyyy" value="${notificacio.procediment.pagadorpostal.contracteDataVig}" />
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				</c:if>
				<c:if test="${not empty notificacio.procediment.pagadorpostal}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<strong><spring:message code="notificacio.info.seccio.pagador.cie" /></strong>
						</h3>
					</div>
					<table class="table table-bordered" style="width: 100%">
						<tbody>
							<c:if test="${not empty notificacio.procediment.pagadorcie.dir3codi}">
								<tr>
									<td width="30%"><strong><spring:message
												code="notificacio.info.pagador.cie.codi.dir3" /></strong></td>
									<td>${notificacio.procediment.pagadorcie.dir3codi}</td>
								</tr>
								<tr>
									<td><strong><spring:message
												code="notificacio.info.pagador.cie.vigencia" /></strong></td>
									<td>
									<fmt:formatDate pattern="dd/MM/yyyy" value="${notificacio.procediment.pagadorcie.contracteDataVig}" />
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				</c:if>
				<c:if test="${not empty notificacio.enviaments}">
				
					<c:forEach items="${notificacio.enviaments}" var="enviament" varStatus="status">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<strong><spring:message code="notificacio.info.seccio.enviaments" /> ${status.index + 1}</strong>
								</h3>
							</div>
							<table class="table teble-striped table-bordered">
									<tbody>
										<tr>
											<th><spring:message code="notificacio.list.enviament.list.titular"/></th>
											<td>
												<c:choose>
													<c:when test="${not empty enviament.titular.nom}">${enviament.titular.nom}</c:when>
													<c:otherwise>${enviament.titular.raoSocial}</c:otherwise>
												</c:choose>
												${enviament.titular.llinatge1}
												${enviament.titular.llinatge2}
												<c:if test="${not empty enviament.titular.nif}">
													 (${enviament.titular.nif})
												</c:if>
											</td>
										</tr>
						    			<tr>
							    			<th><spring:message code="notificacio.list.enviament.list.destinataris"/></th>
							    			<td>
							    			<c:choose>
							    			<c:when test="${not empty enviament.destinataris}">
							    			<c:set var="destinataris" value=""/>
							    				<c:forEach items="${enviament.destinataris}" var="destinatari">
							    				<c:set var="destinataris" value="${destinataris} ${destinatari.nom} ${destinatari.llinatge1} ${destinatari.llinatge2} (${destinatari.nif}),"/>
							    				</c:forEach>
							    				<c:set var="destinatarisLength" value="${fn:length(destinataris)}"/>
												<c:set var="destinatarisLengthLess" value="${fn:length(destinataris) - 1}"/>
												${fn:substring(destinataris, 0, destinatarisLengthLess)}
							    			</c:when>
							    			<c:otherwise>
							    				<spring:message code="notificacio.list.enviament.list.sensedestinataris"/>
							    			</c:otherwise>
							    			</c:choose>
							    			</td>
							    		</tr>
						    			<tr>
							    			<th><spring:message code="enviament.info.seccio.notifica.registre"/></th>
							    			<%--<td>
							    			<spring:message code="es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.${enviament.notificaEstat}"/>
							    			</td>--%>
							    			<c:choose>
							    				<c:when test="${not empty enviament.registreNumeroFormatat}">
													<td>
													<table class="table table-striped" style="width:100%">
														<tbody>
															<tr>
																<td><strong><spring:message code="enviament.info.seu.registre.num"/></strong></td>
																<td>${enviament.registreNumeroFormatat}</td>
															</tr>
															<tr>
																<td><strong><spring:message code="enviament.info.seu.registre.data"/></strong></td>
																<td><fmt:formatDate value="${enviament.registreData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
															</tr>
															<c:if test="${not empty enviament.registreEstat}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.estat"/></strong></td>
																	<td>${enviament.registreEstat}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.sirRecepcioData}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.data.sir.recepcio"/></strong></td>
																	<td><fmt:formatDate value="${enviament.sirRecepcioData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.sirRegDestiData}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.data.sir.desti"/></strong></td>
																	<td><fmt:formatDate value="${enviament.sirRegDestiData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
																</tr>
															</c:if>
															<%-- Assentament registral o Registre normal (versió anterior) --%>
															<c:if test="${(not empty enviament.registreEstat && (enviament.registreEstat == 'DISTRIBUIT' || enviament.registreEstat == 'OFICI_EXTERN'  || enviament.registreEstat == 'OFICI_SIR')) || (empty enviament.registreEstat && not empty enviament.registreNumeroFormatat)}">
																<tr>
																	<td><strong><spring:message code="enviament.info.seu.registre.justificant"/></strong></td>
																	<td>
																	<a href="<not:modalUrl value="/notificacio/${notificacio.id}/enviament/${enviament.id}/justificantDescarregar"/>" onerror="location.reload();" class="btn btn-default btn-sm pull-right fileDownloadSimpleRichExperience">
																		<spring:message code="enviament.info.accio.descarregar.justificant"/>
																		<span class="fa fa-download"></span>
																	</a>
																	</td>
																<tr>
															</c:if>
														</tbody>
													</table>
													</td>
							    				</c:when>
							    				<c:otherwise>
							    					<td><spring:message code="notificacio.list.enviament.list.noregistrat"/></td>
							    				</c:otherwise>
							    			</c:choose>
							    		</tr>
						    			<tr>
							    			<th><spring:message code="enviament.info.seccio.notifica.certificacio"/></th>
						    				<c:choose>
						    				<c:when test="${not empty enviament.notificaCertificacioData}">
							    				<td>
							    					<table class="table table-striped" style="width:100%">
														<tbody>
															<tr>
																<td><strong><spring:message code="enviament.info.notifica.certificacio.data"/></strong></td>
																<td><fmt:formatDate value="${enviament.notificaCertificacioData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
															</tr>
															<c:if test="${not empty enviament.notificaCertificacioMime}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.mime"/></strong></td>
																	<td>${enviament.notificaCertificacioMime}</td>
																</tr>
															</c:if>
															<tr>
																<td><strong><spring:message code="enviament.info.notifica.certificacio.origen"/></strong></td>
																<td><spring:message code="enviament.datat.origen.enum.${enviament.notificaCertificacioOrigen}"/> (${enviament.notificaCertificacioOrigen})</td>
															</tr>
															<c:if test="${not empty enviament.notificaCertificacioMetadades}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.metadades"/></strong></td>
																	<td>${enviament.notificaCertificacioMetadades}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioCsv}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.csv"/></strong></td>
																	<td>${enviament.notificaCertificacioCsv}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioTipus}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.tipus"/></strong></td>
																	<td>${enviament.notificaCertificacioTipus}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioArxiuTipus}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.arxiu.tipus"/></strong></td>
																	<td>${enviament.notificaCertificacioArxiuTipus}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioNumSeguiment}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.num.seguiment"/></strong></td>
																	<td>${enviament.notificaCertificacioNumSeguiment}</td>
																</tr>
															</c:if>
															<c:if test="${not empty enviament.notificaCertificacioArxiuId}">
																<tr>
																	<td><strong><spring:message code="enviament.info.notifica.certificacio.document"/></strong></td>
																	<td>
																		certificacio_${enviament.notificaIdentificador}.pdf
																		<a href="<not:modalUrl value="/notificacio/${notificacio.id}/enviament/${enviament.id}/certificacioDescarregar"/>" class="btn btn-default btn-sm pull-right btn-certificacio" title="<spring:message code="enviament.info.notifica.certificacio.num.descarregar"/>">
																			<spring:message code="enviament.info.notifica.certificacio.num.descarregar"/>
																			<span class="fa fa-download"></span>
																		</a>
																	</td>
																</tr>
															</c:if>
														</tbody>
													</table>
								    			</td>
								    		</c:when>
								    		<c:otherwise>
								    			<td>
								    				<spring:message code="notificacio.list.enviament.list.sensecertificacio"/>
								    			</td>
								    		</c:otherwise>
							    			</c:choose>
						    			</tr>
									</tbody>
							</table>
						</div>
					</c:forEach>
				</c:if>
		</div>
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'events'}"> active</c:if>"
			id="events">
			<table id="events" data-toggle="datatable"
				data-url="<c:url value="/notificacio/${notificacio.id}/event"/>"
				data-search-enabled="false" data-paging="false" data-info="false"
				data-row-info="true" class="table table-striped table-bordered"
				style="width: 100%">
				<thead>
					<tr>
						<th data-col-name="id" data-visible="false">#</th>
						<th data-col-name="enviamentAssociat" data-visible="false"></th>
						<th data-col-name="errorDescripcio" data-visible="false"></th>
						<th data-col-name="createdBy.nom" data-orderable="false"><spring:message
								code="notificacio.event.list.columna.usuari" /></th>
						<th data-col-name="data" data-converter="datetime"
							data-orderable="false"><spring:message
								code="notificacio.event.list.columna.data" /></th>
						<th data-col-name="tipus" data-template="#cellTipus"
							data-orderable="false"><spring:message
								code="notificacio.event.list.columna.tipus" /> <script
								id="cellTipus" type="text/x-jsrender">
							{{:~eval('eventTipus["' + tipus + '"]')}}
							{{if enviamentAssociat}}<span class="label label-default pull-right" title="<spring:message code="notificacio.event.list.info.associat"/>">E</span>{{/if}}
						</script></th>
						<th data-col-name="error" data-template="#cellResultat"
							data-orderable="false"><spring:message
								code="notificacio.event.list.columna.estat" /> <script
								id="cellResultat" type="text/x-jsrender">
							{{if error}}
								<span class="fa fa-warning text-danger" title="<spring:message code="enviament.event.list.processat.error"/>"></span>
							{{else}}
								<span class="fa fa-check text-success" title="<spring:message code="enviament.event.list.processat.ok"/>"></span>
							{{/if}}
						</script></th>
					</tr>
				</thead>
			</table>
		</div>
		<div role="tabpanel"
			class="tab-pane<c:if test="${pipellaActiva == 'accions'}"> active</c:if>"
			id="accions">
			<c:set var="algunaAccioDisponible" value="${false}" />
			<ul class="list-group">
				<c:if test="${notificacio.estat == 'PENDENT'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.registrar" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									href="<not:modalUrl value="/notificacio/${notificacio.id}/registrar"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-send"></span>
									<spring:message code="notificacio.info.accio.registrar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<c:if test="${notificacio.estat == 'REGISTRADA'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.enviar" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									href="<not:modalUrl value="/notificacio/${notificacio.id}/enviar"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-send"></span>
									<spring:message code="notificacio.info.accio.enviar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<!-- Acció reprendre consulta d'estat  - Estat == ENVIADA && notificaErrorTipus == ERROR_REINTENTS_CONSULTA -->
				<c:if test="${notificacio.estat == 'ENVIADA' && notificacio.notificaErrorTipus == 'ERROR_REINTENTS_CONSULTA'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reactivar.consulta" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a href="<not:modalUrl value="/notificacio/${notificacio.id}/reactivarconsulta"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-play"></span>
									<spring:message code="notificacio.info.accio.reactivar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<!-- Acció reprendre consulta d'estat SIR  - Estat == ENVIADA && notificaErrorTipus == ERROR_REINTENTS_SIR -->
				<c:if test="${notificacio.estat == 'ENVIADA' && notificacio.notificaErrorTipus == 'ERROR_REINTENTS_SIR'}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reactivar.consulta.sir" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a href="<not:modalUrl value="/notificacio/${notificacio.id}/reactivarsir"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-play"></span>
									<spring:message code="notificacio.info.accio.reactivar.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
				<c:if test="${notificacio.tipusUsuari == 'APLICACIO' && notificacio.errorLastCallback}">
					<c:set var="algunaAccioDisponible" value="${true}" />
					<li class="list-group-item">
						<div class="row">
							<div class="col-sm-6" style="height: 100%">
								<strong><spring:message code="notificacio.info.accio.reintent" /></strong>
							</div>
							<div class="col-sm-6 text-right">
								<a
									href="<not:modalUrl value="/notificacio/${notificacio.id}/refrescarEstatClient"/>"
									class="btn btn-default btn-sm"> <span class="fa fa-undo"></span>
									<spring:message code="notificacio.info.accio.reintent.boto" />
								</a>
							</div>
						</div>
					</li>
				</c:if>
			</ul>
			<c:if test="${not algunaAccioDisponible}">
				<div class="alert alert-info well-sm" role="alert">
					<spring:message code="notificacio.info.accio.no.accions" />
				</div>
			</c:if>
		</div>
	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacio"/>" class="btn btn-default"
			data-modal-cancel="true"><spring:message code="comu.boto.tancar" /></a>
	</div>
</body>
</html>

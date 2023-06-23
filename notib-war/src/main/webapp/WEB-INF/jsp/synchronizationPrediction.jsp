<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="organgestor.synchronize.dialog.header" /></c:set>
<c:set var="isAllEmpty" value="${empty substMap and empty splitMap and empty mergeMap and empty unitatsVigents and empty unitatsNew}" />

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet" />
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet" />
	<link href="<c:url value="/css/horizontal-tree.css"/>" rel="stylesheet" />
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.css">
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.js"></script>
	<script src="<c:url value="/js/printThis.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead />
	<script>
		var itervalProgres;
		var writtenLines = 0;
		var title="<spring:message code="organgestor.actualitzacio.auto"/>";
		var content="<spring:message code="organgestor.actualitzacio.cancelarActu"/>";
		var acceptar="<spring:message code="organgestor.actualitzacio.acceptar"/>";
		var cancelar="<spring:message code="organgestor.actualitzacio.cancelar"/>";
		var tancar="<spring:message code="comu.boto.tancar"/>";
		<c:if test="${not isUpdatingOrgans}">
		var isUpdating = false;
		$(document).ready(function() {
			$('#formSync').on("submit", function(){
				// console.log("submitting...");
				$('.loading').fadeIn();
				$('#actualitzacioInfo').fadeIn();
				$('.prediccio').fadeOut();
				$('#autobtn', parent.document).prop('disabled', true);
				$('#cancelbtn', parent.document).toggle(true);
				$('#cancelbtn', parent.document).html(cancelar);
				$.post($(this).attr('action'));
				isUpdating = true;
				$('.close', parent.document).on('click', dismissFunction);
				refreshProgres();

				return false;
			});
			$('.close', parent.document).on('click',function(){
				$.confirm({
					title: title,
					content: content,
					buttons: {
						confirm: {
							text: acceptar,
							action: function () {
								window.top.location.reload();
							}
						},
						cancel: {
							text: cancelar,
							action: function () {
							}
						}
					}
				});
			});
		});
		</c:if>


		function refreshProgres() {
			// console.log("refreshProgres");
			itervalProgres =  setInterval(function(){ getProgres(); }, 500);
		}

		function getProgres() {
			// console.log("getProgres");
			$('.close', parent.document).prop('disabled', true);
			$.ajax({
				type: 'GET',
				url: "<c:url value='/organgestor/update/auto/progres'/>",
				success: function(data) {
					if (data) {
						// console.log("Progres:", data);
						writeInfo(data);
						$('#cancelbtn', parent.document).toggle(true);
						if (data.progres == 100) {
							clearInterval(itervalProgres);
							isUpdating = false;
							$('#bar').css('width', '100%');
							$('#bar').attr('aria-valuenow', 100);
							$('#bar').html('100%');
							$('.close', parent.document).prop('disabled', false);
							$('.loading').hide();
							$('#cancelbtn', parent.document).html(tancar);
						} else {
							if (data.progres > 0) {
								$('.loading').hide();
								$('.progress').show();
								$('#bar').css('width', data.progres + '%');
								$('#bar').attr('aria-valuenow', data.progres);
								$('#bar').html(data.progres + '%');
							}else if(data.progres == 0 && data.numProcedimentsActualitzats == 0 ){
								$('.close', parent.document).prop('disabled', false);
								$('.loading').hide();
							}
						}
					}
				},
				error: function() {
					console.log("error obtenint progrés...");
					clearInterval(itervalProgres);
					$('.loading').hide();
					$('.close', parent.document).prop('disabled', false);
				}
			});
		}

		function writeInfo(data) {
			let info = data.info;
			let index;
			let scroll = writtenLines < info.length;
			// console.log("Scrol?: ", writtenLines, info.length, scroll);
			for (index = writtenLines; index < info.length; index++) {
				$("#bcursor").before("<p class='info-" + info[index].tipus + "'>" + info[index].text + "</p>");
			}
			writtenLines = index;
			if (data.error) {
				$("#bcursor").before("<p class='info-ERROR'>" + data.errorMsg + "</p>");
			}
			//scroll to the bottom of "#actualitzacioInfo"
			if (scroll) {
				var infoDiv = document.getElementById("actualitzacioInfo");
				infoDiv.scrollTop = infoDiv.scrollHeight;
			}
		}
		function dismissFunction() {
			if (!isUpdating) {
				window.top.location.reload();
				return;
			}
			$.confirm({
				title: title,
				content: content,
				buttons: {
					confirm: {
						text: acceptar,
						action: function () {
							window.top.location.reload();
						}
					},
					cancel: {
						text: cancelar,
						action: function () {
						}
					}
				}
			});
		}

		let crearPdf = () => $('#divPredict').printThis();

	</script>
	<style type="text/css">
		.info-TITOL {
			font-size: 13px;
			font-weight: bold;
			border-bottom: solid 2px #CCC;
			margin-bottom: 8px;
		}
		.info-SUBTITOL {
			font-size: 12px;
			font-weight: bold;
			border-bottom: solid 1px #CCC;
			margin-bottom: 6px;
			padding-left: 5px;
		}
		.info-INFO {
			font-size: 10px;
			padding-left: 10px;
		}
		.info-SUBINFO {
			font-size: 9px;
			padding-left: 15px;
		}
		.info-TEMPS {
			font-size: 8px;
			padding-right: 20px;
			text-align: right;
			color: #888;
		}
		.info-SEPARADOR {
			padding-bottom: 10px;
			border-top: dotted 1px #DDD;
		}
		.info-ERROR {
			font-size: 11px;
			padding-left: 10px;
			color: red;
		}
		.loading {
			display: none;
			height: 20px;
			width: 100%;
		}
		.loading-gif {
			left: 50%;
			margin-left: -32px;
			margin-top: -32px;
			position: absolute;
			top: 46px;
			width: 40px;
		}
		.loading-gif img {
			width: 45%;
		}
		body {
			min-height: 400px;
		}
		.progress {
			display: none;
			margin-bottom: 0px !important;
		}
		.info {
			display: none;
			overflow: auto;
			width: 100%;
			max-height: 600px;
			background-color: #EEE;
			position: relative;
			top: 15px;
			padding: 10px;
		}
		.info > p {
			margin: 0 0 4px;
		}
		.blinking-cursor {
			font-weight: 100;
			font-size: 16px;
			color: #222;
			-webkit-animation: 1s blink step-end infinite;
			-moz-animation: 1s blink step-end infinite;
			-ms-animation: 1s blink step-end infinite;
			-o-animation: 1s blink step-end infinite;
			animation: 1s blink step-end infinite;
		}
		@keyframes "blink" {
			from, to {
				color: transparent;
			}
			50% {
				color: black;
			}
		}

		@-moz-keyframes blink {
			from, to {
				color: transparent;
			}
			50% {
				color: black;
			}
		}

		@-webkit-keyframes "blink" {
			from, to {
				color: transparent;
			}
			50% {
				color: black;
			}
		}

		@-ms-keyframes "blink" {
			from, to {
				color: transparent;
			}
			50% {
				color: black;
			}
		}

		@-o-keyframes "blink" {
			from, to {
				color: transparent;
			}
			50% {
				color: black;
			}
		}
	</style>
</head>
<body>

	<div id="divPredict" class="panel-group prediccio">
	
		<!-- If this is first sincronization it shows all currently vigent unitats that will be created in db  -->
<%--		<c:if test="${isFirstSincronization}">--%>
<%--			<div class="panel panel-default">--%>
<%--				<div class="panel-heading">--%>
<%--					<spring:message--%>
<%--						code="organgestor.synchronize.prediction.firstSincroHeader" />--%>
<%--				</div>--%>
<%--				<div class="panel-body">--%>

<%--					<c:if test="${empty unitatsVigents}">--%>
<%--						<spring:message code="organgestor.synchronize.prediction.firstSincroNoUnitatsVigent" />--%>
<%--					</c:if>--%>

<%--					<c:if test="${!empty unitatsVigents}">--%>
<%--						<c:forEach var="unitatVigentFirstSincro" items="${unitatsVigents}">--%>

<%--							<div class=horizontal-left>--%>
<%--								<div id="wrapper">--%>
<%--									<span class="label bg-success border-green overflow-ellipsis create-label"></span>--%>
<%--									<div class="branch lv1">--%>
<%--										<div class="entry sole">--%>
<%--											<span class="label bg-success border-green overflow-ellipsis" title="${unitatVigentFirstSincro.codi} - ${unitatVigentFirstSincro.denominacio}">--%>
<%--													${unitatVigentFirstSincro.codi} - ${unitatVigentFirstSincro.denominacio}--%>
<%--											</span>--%>
<%--										</div>--%>
<%--									</div>--%>
<%--								</div>--%>
<%--							</div>--%>
<%--						</c:forEach>--%>
<%--					</c:if>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</c:if>--%>
<%--		<c:if test="${!isFirstSincronization}">--%>

			<!-- If unitats didn't change from the last time of synchronization show message: no changes -->
			<c:if test="${isAllEmpty}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="organgestor.synchronize.prediction.noChanges" />
					</div>
					<div class="panel-body">
						<spring:message code="organgestor.synchronize.prediction.upToDate" />
					</div>
				</div>
			</c:if>

			<!-- If they exist show unitats that splited  (e.g. unitat A splits to unitats B and C) -->
			<c:if test="${!empty splitMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="organgestor.synchronize.prediction.splits" />
					</div>
					<div class="panel-body">
						<c:forEach var="splitMap" items="${splitMap}">
							<c:set var="key" value="${splitMap.key}" />
							<c:set var="values" value="${splitMap.value}" />
							<c:choose>
								<c:when test="${not empty key.denominacioCooficial}">
									<c:set var="denominacio" value="${key.denominacioCooficial}" />
								</c:when>
								<c:otherwise>
									<c:set var="denominacio" value="${key.denominacio}" />
								</c:otherwise>
							</c:choose>

							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-danger border-red overflow-ellipsis" title="${key.codi} - ${denominacio}"> ${key.codi} - ${denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<c:choose>
												<c:when test="${not empty value.denominacioCooficial}">
													<c:set var="den" value="${value.denominacioCooficial}" />
												</c:when>
												<c:otherwise>
													<c:set var="den" value="${value.denominacio}" />
												</c:otherwise>
											</c:choose>
											<div class="entry">
												<span class="label bg-success border-green overflow-ellipsis" title="${value.codi} - ${den}">${value.codi}- ${den}</span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>


			<!-- If they exist show unitats that merged (e.g. unitats D and E merge to unitat F) -->
			<c:if test="${!empty mergeMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="organgestor.synchronize.prediction.merges" />
					</div>
					<div class="panel-body">
						<c:forEach var="mergeMap" items="${mergeMap}">
							<c:set var="key" value="${mergeMap.key}" />
							<c:set var="values" value="${mergeMap.value}" />
							<c:choose>
								<c:when test="${not empty key.denominacioCooficial}">
									<c:set var="denominacio" value="${key.denominacioCooficial}" />
								</c:when>
								<c:otherwise>
									<c:set var="denominacio" value="${key.denominacio}" />
								</c:otherwise>
							</c:choose>
							<div class=horizontal-right>
								<div id="wrapper">
									<span class="label bg-success border-green right-postion-20 overflow-ellipsis" title="${key.codi} - ${denominacio}"> ${key.codi} -${denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<c:choose>
												<c:when test="${not empty value.denominacioCooficial}">
													<c:set var="den" value="${value.denominacioCooficial}" />
												</c:when>
												<c:otherwise>
													<c:set var="den" value="${value.denominacio}" />
												</c:otherwise>
											</c:choose>
											<div class="entry">
												<span class="label bg-danger border-red overflow-ellipsis" title="${value.codi} - ${den}">${value.codi} - ${den} </span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			
			
			<!-- If they exist show unitats that were substituted by the others  (e.g. unitat G is substituted by unitat H) -->
			<c:if test="${!empty substMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="organgestor.synchronize.prediction.substitucions" />
					</div>
					<div class="panel-body">
						<c:forEach var="substMap" items="${substMap}">
							<c:set var="key" value="${substMap.key}" />
							<c:set var="values" value="${substMap.value}" />
							<c:choose>
								<c:when test="${not empty key.denominacioCooficial}">
									<c:set var="denominacio" value="${key.denominacioCooficial}" />
								</c:when>
								<c:otherwise>
									<c:set var="denominacio" value="${key.denominacio}" />
								</c:otherwise>
							</c:choose>
							<div class=horizontal-right>
								<div id="wrapper">
									<span class="label bg-success border-green right-postion-20 overflow-ellipsis" title="${key.codi} - ${denominacio}"> ${key.codi} -${denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<c:choose>
												<c:when test="${not empty value.denominacioCooficial}">
													<c:set var="den" value="${value.denominacioCooficial}" />
												</c:when>
												<c:otherwise>
													<c:set var="den" value="${value.denominacio}" />
												</c:otherwise>
											</c:choose>
											<div class="entry sole">
												<span class="label bg-danger border-red overflow-ellipsis" title="${value.codi} - ${den}">${value.codi} - ${den} </span></div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>			

			<!-- If they exist show unitats that only had some of their properties changed -->
			<c:if test="${!empty unitatsVigents}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<c:choose>
							<c:when test="${isFirstSincronization}">
								<spring:message code="organgestor.synchronize.prediction.primera.sync.atributesChanged" />
							</c:when>
							<c:otherwise>
								<spring:message code="organgestor.synchronize.prediction.atributesChanged" />
							</c:otherwise>
						</c:choose>

					</div>

					<div class="panel-body">
						<c:forEach var="unitatVigent" items="${unitatsVigents}">
							<c:choose>
								<c:when test="${not empty unitatVigent.denominacioCooficial}">
									<c:set var="denominacio" value="${unitatVigent.denominacioCooficial}" />
								</c:when>
								<c:otherwise>
									<c:set var="denominacio" value="${unitatVigent.denominacio}" />
								</c:otherwise>
							</c:choose>
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis" title="${unitatVigent.codi} -
												<c:choose>
													<c:when test="${not empty unitatVigent.oldDenominacio}">
														${unitatVigent.oldDenominacio}
													</c:when>
													<c:otherwise>
														${denominacio}
													</c:otherwise>
												</c:choose>">
										${unitatVigent.codi} -
										<c:choose>
											<c:when test="${not empty unitatVigent.oldDenominacio}">${unitatVigent.oldDenominacio}</c:when>
											<c:otherwise>${denominacio}</c:otherwise>
										</c:choose>
									</span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-warning border-yellow overflow-ellipsis" title="${unitatVigent.codi} - ${denominacio}">
												${unitatVigent.codi} - ${denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>

						</c:forEach>
					</div>
				</div>
			</c:if>
			
			<!-- If they exist show unitats that are new (are not transitioned from any other unitat) -->
			<c:if test="${!empty unitatsNew}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="organgestor.synchronize.prediction.noves" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatNew" items="${unitatsNew}">
							<c:choose>
								<c:when test="${not empty unitatNew.denominacioCooficial}">
									<c:set var="denominacio" value="${unitatNew.denominacioCooficial}" />
								</c:when>
								<c:otherwise>
									<c:set var="denominacio" value="${unitatNew.denominacio}" />
								</c:otherwise>
							</c:choose>
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis create-label"></span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-success border-green overflow-ellipsis" title="${unitatNew.codi} - ${denominacio}">
												${unitatNew.codi} - ${denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>

			<!-- If they exist show unitats that are extint (are not vigent and has not any transition to any other unitat) -->
			<c:if test="${!empty unitatsExtingides}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="organgestor.synchronize.prediction.extingides" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatExtingida" items="${unitatsExtingides}">
							<c:choose>
								<c:when test="${not empty unitatExtingida.denominacioCooficial}">
									<c:set var="denominacio" value="${unitatExtingida.denominacioCooficial}" />
								</c:when>
								<c:otherwise>
									<c:set var="denominacio" value="${unitatExtingida.denominacio}" />
								</c:otherwise>
							</c:choose>
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-danger border-red right-postion-20 overflow-ellipsis" title="${unitatExtingida.codi} - ${denominacio}">
										${unitatExtingida.codi} - ${denominacio}
									</span>
									<div class="branch lv1">
										<div class="entry sole remove">
											<span class="label bg-success border-green overflow-ellipsis remove-label"></span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
<%--		</c:if>--%>

	</div>

	<div class="progress">
		<div id="bar" class="progress-bar" role="progressbar progress-bar-striped active" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>
	</div>
	<div id="actualitzacioInfo" class="info">
		<span id="bcursor" class="blinking-cursor">|</span>
	</div>

	<c:set var="formAction">
		<not:modalUrl value="/organgestor/saveSynchronize" />
	</c:set>
	<form:form id="formSync" action="${formAction}" method="post" cssClass="form-horizontal" role="form">
		<div id="modal-botons">
			<a id="pdfBtn" class="btn btn-default" onclick="crearPdf()"><spring:message code="comu.boto.descarregar.pdf" /></a>
			<button id="autobtn" type="submit" class="btn btn-success" data-noloading="true"
				<c:if test="${isAllEmpty and !isFirstSincronization}"><c:out value="disabled='disabled'"/></c:if>>
				<span class="fa fa-save"></span>
				<spring:message code="organgestor.list.boto.synchronize" />
			</button>
			<a id="cancelbtn" style="display: none !important;" class="btn btn-default" onclick="dismissFunction()" data-modal-cancel="false"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>

</body>
</html>

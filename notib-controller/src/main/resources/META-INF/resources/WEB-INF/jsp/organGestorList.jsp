<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="organgestor.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	<script type="text/javascript">
	$(document).ready(function() {
		$('#btnNetejar').click(function() {
			$(':input', $('#filtre')).each (function() {
				let type = this.type;
				let tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea') {
					this.value = '';
				} else if (type == 'checkbox' || type == 'radio') {
					this.checked = false;
					$(this).parent().removeClass('active');
				} else if (tag == 'select') {
					this.selectedIndex = this.name === "estat" ? 1 : 0;
				}
			});

			$("#estat").change();
			$('#btn-entregaCie').removeClass('active');
			$('#entregaCieActiva').val(false);
			$('#form-filtre').submit();
		});
		$('#btn-entregaCie').click(function() {
			let entregaCie = !$(this).hasClass('active');
			$('#entregaCie').val(entregaCie);
		})
		$(".panel-heading").css({"display": "flex", "justify-content": "space-between"})
		$(".panel-heading").append("<div><button id='canviarVistaOrganGestor' class='btn btn-primary'><spring:message code='boto.canviar.vista'/></button></div>");
		$("#canviarVistaOrganGestor").click(function(){
			window.location.replace('<c:url value="/organgestorArbre"/>');
		});

		$("#obsolet").on("click", x => {
			$("#vigent").removeClass("active");
			if ($(x).hasClass("active")) {
				$(x).removeClass("active");
			}
			$('#btnFiltrar').click();
		});

		$("#vigent").on("click", x => {
			$("#obsolet").removeClass("active");
			if ($(x).hasClass("active")) {
				$(x).removeClass("active");
			}
			$('#btnFiltrar').click();
		});

		$("#estat").on("change", () => $('#btnFiltrar').click());

		$("#organ-boto-update-oficines").on("click", () => bloquejar());
	});
	// Bloquejar la pantalla
	function bloquejar() {
		$("#spinner-container").removeClass("ocult");
		$("#spinner-container").addClass("visible");
	}
	</script>
	<style>
		.ocult {display: none;}
		.visible {display: flex; justify-content: center; flex-direction: column;}
		.loading-screen {
			background-color: rgba(0,0,0,0.4);
			position:absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			z-index: 9999;
		}
		.spin-box {display:flex; justify-content: center;}
	</style>
</head>
<body>
	<div id="spinner-container" class="loading-screen ocult">
		<div class="spin-box">
			<span class="fa fa-spin fa-circle-o-notch  fa-3x"></span>
		</div>
	</div>

	<div id="botoVistes" class="row">

	</div>
	<form:form id="filtre" action="" method="post" cssClass="well" modelAttribute="organGestorFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<not:inputText name="codi" inline="true" placeholderKey="organgestor.list.columna.codi"/>
			</div>
			<div class="col-md-3">
				<not:inputText name="nom" inline="true" placeholderKey="organgestor.list.columna.nom"/>
			</div>
			<div class="col-md-2">
<%--				<not:inputText name="codiPare" inline="true" placeholderKey="organgestor.list.columna.codi.pare"/>--%>
				<not:inputSelect name="codiPare" textKey="organgestor.list.columna.organ.arrel" required="true" optionItems="${organsEntitat}" optionValueAttribute="codi"
								 optionTextAttribute="codiNom" labelSize="0" inline="true" emptyOption="true" optionMinimumResultsForSearch="2" placeholderKey="organgestor.form.camp.organ.arrel.select"/>
			</div>
			<div class="col-md-2">
<%--				<not:inputRadio name="estat" botons="true" optionItems="${organGestorEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" />--%>
				<not:inputSelect name="estat" optionItems="${organGestorEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"
								 placeholderKey="organgestor.list.columna.estat" textKey="organgestor.list.columna.estat" required="true" labelSize="0"/>
			</div>
			<c:if test="${setOficina}">
				<div class="col-md-3">
					<not:inputSelect 
							name="oficina" 
							textKey="organgestor.list.columna.oficina"
							required="true" 
							optionItems="${oficinesEntitat}" 
							optionValueAttribute="codi" 
							optionTextAttribute="nom"
							labelSize="0" 
							inline="true"
							emptyOption="true"
							optionMinimumResultsForSearch="2"
							placeholderKey="organgestor.form.camp.oficina.select"/>
	<%-- 				<not:inputSelect name="oficina" textKey="organgestor.list.columna.oficina" required="true" optionMinimumResultsForSearch="0"/> --%>
				</div>
			</c:if>

		</div>
		<div class="row">
			<div class="col-md-2">
					<%--				<not:inputCheckbox name="entregaCieActiva" textKey="organgestor.form.camp.entregacie" inline="true" />--%>
				<button id="btn-entregaCie" title="" class="btn btn-default <c:if test="${organGestorFiltreCommand.entregaCie}">active</c:if>" data-toggle="button">
					<span class="fa fa-envelope"></span> <spring:message code="organgestor.list.columna.cie"/>
				</button>
				<not:inputHidden name="entregaCie"/>
			</div>
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="btnFiltrar" type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<table
		id="organGestor"
		data-toggle="datatable"
		data-url="<c:url value="/organgestor/datatable"/>"
		data-search-enabled="false"
		data-default-order="2"
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%"
		data-botons-template="#botonsTemplate"
		data-save-state="true"
		data-mantenir-paginacio="true"
		data-filter="#filtre">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="codi" data-template="#cellOrganGestorTemplate"><spring:message code="organgestor.list.columna.codi"/>
					<script id="cellOrganGestorTemplate" type="text/x-jsrender">
						{{:codi}}
						{{if estat != 'V'}}
							<span class="fa fa-warning text-danger" title="<spring:message code='organgestor.list.columna.organGestor.obsolet'/>"></span>{{/if}}
 					</script>
				</th>
				<th data-col-name="nom"><spring:message code="organgestor.list.columna.nom"/></th>
				<th data-col-name="codiPare"><spring:message code="organgestor.list.columna.codi.pare"/></th>
				<th data-col-name="nomPare" data-orderable="false"><spring:message code="organgestor.list.columna.nom.pare"/></th>
				<c:if test="${setLlibre}">
					<th data-col-name="llibreCodiNom"><spring:message code="procediment.list.columna.llibre"/></th>
				</c:if>
<%--				<c:if test="${setOficina}"> --%>
<%--					<th data-col-name="oficinaCodiNom"><spring:message code="organgestor.list.columna.oficina.sir"/>--%>
<%--				</c:if>--%>
<%--				<c:if test="${!setOficina}"> --%>
<%--					<th data-col-name="oficinaNom"><spring:message code="organgestor.list.columna.oficina"/></th>--%>
<%--				</c:if>--%>

				<th data-col-name="estat" data-template="#cellEstatTemplate">
					<spring:message code="organgestor.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'V'}}
							<spring:message code="es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.V"/>
						{{else estat == 'E'}}
							<spring:message code="es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.E"/>
						{{else estat == 'A'}}
							<spring:message code="es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.A"/>
						{{else estat == 'T'}}
							<spring:message code="es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.T"/>
						{{else}}
							<spring:message code="es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.ALTRES"/>
						{{/if}}
					</script>
				</th>
				<th data-col-name="entregaCieActiva" data-orderable="false" data-template="#cellActivaTemplate">
					<spring:message code="organgestor.list.columna.cie"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if entregaCieActiva}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="100px">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="organgestor/{{:id}}/permis" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="organgestor.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="100px">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="organgestor/{{:id}}" data-toggle="modal" data-adjust-height="false" data-height="400px"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
<%--								<li><a href="organgestor/{{:codi}}/update" data-toggle="ajax"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="organgestor.list.boto.actualitzar"/></a></li>--%>
								<li><a href="procediment/organ/{{:codi}}" data-toggle="modal" data-maximized="true" data-adjust-height="false" data-height="650px"><span class="fa fa-briefcase"></span>&nbsp;&nbsp;<spring:message code="decorator.menu.procediment"/></a></li>
								<li><a href="servei/organ/{{:codi}}" data-toggle="modal" data-maximized="true" data-adjust-height="false" data-height="650px"><span class="fa fa-briefcase"></span>&nbsp;&nbsp;<spring:message code="decorator.menu.servei"/></a></li>
<%--								<li><a href="organgestor/{{:codi}}/delete" data-toggle="ajax" data-confirm="<spring:message code="organgestor.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>--%>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			<c:if test="${setOficina}">
				<a id="organ-boto-update-oficines" class="btn btn-default" href="organgestor/sync/oficines/LLISTA"  onClick="bloquejar()"
					title='<spring:message code="organgestor.list.boto.actualitzar.oficines.tooltip"/>'>
					<span class="fa fa-refresh"></span>&nbsp;<spring:message code="organgestor.list.boto.actualitzar.oficines"/>
			</a>
			</c:if>
			<a id="organ-boto-update" title='<spring:message code="organgestor.list.boto.actualitzar.tots.tooltip"/>'
				class="btn btn-success" href="organgestor/sync/dir3" data-toggle="modal" data-maximized="false">
				<span class="fa fa-refresh"></span>&nbsp;<spring:message code="organgestor.list.boto.actualitzar.tots"/>
			</a>
		</p>
	</script>
	
</body>
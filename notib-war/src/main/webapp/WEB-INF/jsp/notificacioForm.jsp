<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>


<c:set var="enviamentTipus">${notificacioCommand.enviamentTipus}</c:set>
<c:choose>
    <c:when test="${empty notificacioCommand.id}">
		<c:set var="titol">
			<c:choose>
				<c:when test="${enviamentTipus == 'COMUNICACIO'}">
					<spring:message code="notificacio.form.titol.crear.comunicacio"/>
				</c:when>
				<c:when test="${enviamentTipus == 'COMUNICACIO_SIR'}">
					<spring:message code="notificacio.form.titol.crear.comunicacio.sir"/>
				</c:when>
				<c:otherwise>
					<spring:message code="notificacio.form.titol.crear.notificacio"/>
				</c:otherwise>
			</c:choose>
		</c:set>
	</c:when>
    <c:otherwise>
		<c:set var="titol">
			<c:choose>
				<c:when test="${enviamentTipus == 'COMUNICACIO'}">
					<spring:message code="notificacio.form.titol.modificar.comunicacio"/>
				</c:when>
				<c:when test="${enviamentTipus == 'COMUNICACIO_SIR'}">
					<spring:message code="notificacio.form.titol.modificar.comunicacio.sir"/>
				</c:when>
				<c:otherwise>
					<spring:message code="notificacio.form.titol.modificar.notificacio"/>
				</c:otherwise>
			</c:choose>
		</c:set>
	</c:otherwise>
</c:choose>
<c:set var="dadesGenerals"><spring:message code="notificacio.form.titol.dadesgenerals"/></c:set>
<c:set var="document"><spring:message code="notificacio.form.titol.document"/></c:set>
<c:set var="parametresRegistre"><spring:message code="notificacio.form.titol.parametresregistre"/></c:set>
<c:set var="enviaments"><spring:message code="notificacio.form.titol.enviaments"/></c:set>
<c:choose>
	<c:when test="${enviamentTipus != 'COMUNICACIO_SIR'}">
		<c:set var="titular"><spring:message code="notificacio.form.titol.enviaments.titular"/></c:set>
		<c:set var="destinatarisTitol" scope="request"><spring:message code="notificacio.form.titol.enviaments.destinataris"/></c:set>
		<c:set var="documentAvisKey">notificacio.for.camp.document.avis</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titular"><spring:message code="notificacio.form.titol.enviaments.titularComunicacioSir"/></c:set>
		<c:set var="destinatarisTitol" scope="request"><spring:message code="notificacio.form.titol.enviaments.destinatarisComunicacioSir"/></c:set>
		<c:set var="documentAvisKey">notificacio.for.camp.document.avis.sir</c:set>
	</c:otherwise>
</c:choose>
<c:set var="metodeEntrega"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega"/></c:set>
<c:set var="entregaPostal"><spring:message code="notificacio.form.titol.entregapostal"/></c:set>
<c:set var="entregaPostalDades"><spring:message code="notificacio.form.titol.entregapostal.dades"/></c:set>
<c:set var="entregaDireccio"><spring:message code="notificacio.form.titol.entregadireccio"/></c:set>
<c:set var="entitatDir3Codi">${entitat.dir3Codi}</c:set>


<c:url value="/notificacio/nivellsAdministracions" 	var="urlNivellAdministracions" scope="request"/>
<c:url value="/notificacio/comunitatsAutonomes" 	var="urlComunitatsAutonomes" scope="request"/>
<c:url value="/notificacio/provincies" 				var="urlProvincies"/>
<c:url value="/notificacio/localitats" 				var="urlLocalitats"/>
<c:url value="/notificacio/cercaUnitats"			var="urlCercaUnitats" scope="request"/>
<c:url value="/notificacio/paisos"					var="urlPaisos"/>
<c:url value="/notificacio/cercaUnitats"			var="urlCercaUnitats"/>
<c:url value="/entitat/organigrama/" 				var="urlOrganigrama" scope="request"/>

<html>
<head>
    <title>${titol}</title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
    <script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/formEnviament.js"/>"></script>
	<script src="<c:url value="/js/bootbox.all.min.js"/>"></script>
<style type="text/css">
.help-block {
	font-size: x-small;
}
#entregaPostal .help-block {
/* 	font-size: 8px; */
}
.inputcss {
	width: calc(100% - 175px);
	float: left;
	height: 44px;
}
.labelcss {
	width: 175px;
	float: left;
}
.select2-container--bootstrap {
	width: 100% !important;
}
.delete {
	top: 27px;
}
.dest .form-group {
	margin-bottom: 0px;
}
.separacioEnviaments {
    border-top: 8px solid #eee;
}

.form-horizontal .control-label{
    text-align: left;
}
.title {
	margin-top: 2%;
	font-size: larger;
}
.title > label {
	color: #ff9523;
}
.subtitle {
	margin-top: 2%;
	font-size: larger;
}
.subtitle > label {
	color: #0BB60B;
}
.title > hr {
	margin-top: 0%;
}
.title-envios {
	color: #ffffff;
	margin-top: 1%;
	font-size: larger;
}
.title-envios > hr {
	margin-top: 0%;
	height: 1px;
	background-color: #696666;
}
.newEnviament {
	padding: 1%;
	border-radius: 5px;
}
.container-envios {
	background-color: #EFE8EA;
}
.title-container {
	text-align: center;
	background-color: #696666;
	width: 12%;
}
.datepicker table tr td.today, .datepicker table tr td.today:hover {
	color: #000000;
	background: #a4a4a4 !important;
	background-color: #a4a4a4 !important;
}

.entregaPostalInfo {
	display: none;
}
.loading {
	background: rgba( 255, 255, 255, 0.8 );
  	display: none;
  	height: 100%;
  	position: fixed;
  	width: 100%;
  	z-index: 9999;
  	left: 0;
  	top: 0;
}
.loading-gif {
	left: 50%;
  	margin-left: -32px;
  	margin-top: -32px;
  	position: absolute;
  	top: 50%;
  	width: 4%;
}
.loading-gif img {
	width: 45%;
}
.loading-text {
	left: 47%;
  	margin-left: -32px;
  	margin-top: -32px;
  	position: absolute;
  	top: 55%;
}
.avis-metodo-envio {
	text-align: center;
	color: #373737;
	padding: 1% 0 0 0;
}
.avis-metodo-envio span:before {
	color: #ff9f59;
	font-size: 15px;
	margin-right: 6px;
}
[id^="select2-procedimentId-result-"] {
	padding: 6px 20px;
}
.select2-results__group {
	font-size: 14px !important;
}
.unselectable {
    cursor: not-allowed;
}
.select2-results .select2-results__option[aria-disabled="true"] {
    display: none;
}
#tooltip {
    position: absolute;
    border: 1px solid #ffeeba;
    border-radius: 3px;
    background: #fff3cd;
    padding: 2px 10px;
    color: #856404;
    display: none;
}
.warningClass {
	border: 2px solid orange;
}
.alerta-sense-nif {
	display: none;
	margin-right: 15px;
	margin-left: 15px;
}
.validating-block {
	font-size: x-small;
}


#entregaPostalCaducada {
	display: none;
	color:red;
	margin-top:15px;
}

</style>
</head>
<body>

<script type="text/javascript">
	var viewModel = {
		ambEntregaCIEInternal: 10,
		ambEntregaDEHInternal: '${ambEntregaDeh}' === 'true',
		ambEntregaCIEListener: function(val) {},
		set ambEntregaCIE(val) {
			this.ambEntregaCIEInternal = val;
			this.ambEntregaCIEListener(val);
		},
		get ambEntregaCIE() {
			return this.ambEntregaCIEInternal;
		},
		get ambEntregaDEH() {
			return this.ambEntregaDEHInternal;
		},
		registerListener: function(listener) {
			this.ambEntregaCIEListener = listener;
		}
	}

	// events al canviar els valors de les variables
	viewModel.ambEntregaCIEListener = function(val) {
		if (val || this.ambEntregaDEH) {
			console.debug("Mostra formulari entrega cie");
			$(".entrega-activa").show();
			$(".entrega-inactiva").hide();
		} else {
			$(".entrega-activa").hide();
			$(".entrega-inactiva").show();
			console.debug("Oculta formulari entrega cie");
		}
		console.log(val);
		if (val) {
			console.debug("Entrega CIE activa");
			$(".entrega-cie-activa").show();
		} else {
			$(".entrega-cie-activa").hide();
		}
	};

	// valors inicials
	$(document).ready(function() {
		viewModel.ambEntregaCIE = false;
	});


	//////
	//////

	if ('${notificacioCommand != null && notificacioCommand.procedimentId != null}') {
		var procedimentIdAux = '${notificacioCommand.procedimentId}';
	} else {
		var procedimentIdAux = null;
	}
	if ('${notificacioCommand != null && notificacioCommand.serveiId != null}') {
		var serveiIdAux = '${notificacioCommand.serveiId}';
	} else {
		var serveiIdAux = null;
	}
	var interessatsTipus = new Array();
	var interessatTipusOptions = "";
	var numDocuments = 1;
	<c:forEach items="${interessatTipusDest}" var="it" varStatus="status">
	interessatTipusOptions = interessatTipusOptions + "<option value=${it.value}" + (${status.index == 0} ? " selected='selected'" : "") + "><spring:message code='${it.text}'/></option>";
	</c:forEach>
	var locale = "${requestLocale}";
	var consultarFocusout = true;
	var personaMaxSizes = {
		nom: '${nomSize}',
		llinatge1: '${llinatge1Size}',
		llinatge2: '${llinatge2Size}',
		email: '${emailSize}',
		telefon: '${telefonSize}'
	}
	var textMessages = {
		"notificacio.form.camp.logitud": "<spring:message code="notificacio.form.camp.logitud"/>",
		"comu.placeholder.seleccio": "<spring:message code='comu.placeholder.seleccio'/>",
		"notificacio.form.dir3.cercar.noMinimOrgansFiltre": "<spring:message code='notificacio.form.dir3.cercar.noMinimOrgansFiltre'/>",
		"comu.si": "<spring:message code="comu.si"/>",
		"comu.no": "<spring:message code="comu.no"/>",
		"comu.boto.seleccionar": "<spring:message code="comu.boto.seleccionar"/>",
		"notificacio.sir.emprar.valib": "<spring:message code="notificacio.sir.emprar.valib"/>",
		"notificacio.sir.emprar.valib.text": "<spring:message code="notificacio.sir.emprar.valib.text"/>",
	};

	var destinatariHTMLTemplate =' \
    <div class="col-md-12 destinatariForm destenv_#num_enviament# personaForm_#num_enviament#_#num_destinatari#"> \
		<div class="col-md-3"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].interessatTipus"><spring:message code="notificacio.form.camp.interessatTipus"/></label> \
				<div class="controls col-xs-12"> \
					<select id="enviaments[#num_enviament#].destinataris[#num_destinatari#].interessatTipus" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].interessatTipus" class="form-control interessat" style="width:100%"> \
						' + interessatTipusOptions + ' \
					</select> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 nif"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].nif"><spring:message code="notificacio.form.camp.titular.nif"/></label> \
				<div class="col-xs-12"> \
					<input id="enviaments[#num_enviament#].destinataris[#num_destinatari#].nif" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].nif" class="form-control " type="text" value=""> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 rao nomInput"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].nomInput"><spring:message code="notificacio.form.camp.titular.nom.sol"/> *</label> \
				<div class="col-xs-12"> \
					<input maxlength="${nomSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].nomInput" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].nomInput" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
						<span class="glyphicon glyphicon-info-sign"></span> \
						<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].nomInput">0</span> \
							<spring:message code="notificacio.form.camp.logitud"/> \
						<span> ${nomSize}</span> \
					</p> \
				</div> \
				\
			</div> \
		</div> \
		\<div class="col-md-3 rao raoSocialInput"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].raoSocialInput"><spring:message code="notificacio.form.camp.titular.rao.social"/> *</label> \
				<div class="col-xs-12"> \
					<input maxlength="${raoSocialSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].raoSocialInput" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].raoSocialInput" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
						<span class="glyphicon glyphicon-info-sign"></span> \
						<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].raoSocialInput">0</span> \
							<spring:message code="notificacio.form.camp.logitud"/> \
						<span> ${raoSocialSize}</span> \
					</p> \
				</div> \
				\
			</div> \
		</div> \
		<div class="col-md-3 llinatge1 hidden"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1"><spring:message code="notificacio.form.camp.titular.llinatge1"/> *</label> \
				<div class="col-xs-12"> \
					<input maxlength="${llinatge1Size}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge1">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${llinatge1Size}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 llinatge2 hidden"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2"><spring:message code="notificacio.form.camp.titular.llinatge2"/></label> \
				<div class="col-xs-12"> \
					<input maxlength="${llinatge2Size}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].llinatge2">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${llinatge2Size}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon"><spring:message code="notificacio.form.camp.titular.telefon"/></label> \
				<div class="col-xs-12"> \
					<input maxlength="${telefonSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].telefon">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${telefonSize}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-4"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].email"><spring:message code="notificacio.form.camp.titular.email"/></label> \
				<div class="col-xs-12"> \
					<input maxlength="${emailSize}" id="enviaments[#num_enviament#].destinataris[#num_destinatari#].email" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].email" class="form-control " type="text" value=""> \
					<p class="info-length text-success"> \
					<span class="glyphicon glyphicon-info-sign"></span> \
					<span class="inputCurrentLength_enviaments[#num_enviament#].destinataris[#num_destinatari#].email">0</span> \
						<spring:message code="notificacio.form.camp.logitud"/> \
					<span> ${emailSize}</span> \
					</p> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-3 dir3Codi"> \
		<div class="form-group"> \
			<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi"><spring:message code="notificacio.form.camp.titular.dir3codi"/></label> \
			<div class="col-xs-12"> \
				<div class="input-group" id="$searchOrgan#num_enviament##num_destinatari#" onclick="obrirModalOrganismesDestinatari(#num_enviament#,#num_destinatari#,&quot;${urlOrganigrama}&quot;,&quot;${urlComunitatsAutonomes}&quot;,&quot;${urlNivellAdministracions}&quot;,&quot;${urlCercaUnitats}&quot;)"> \
					<input id="searchOrgan#num_enviament##num_destinatari#" class="form-control " readonly="true" type="text" value=""> \
					<span class="input-group-addon habilitat">  \
						<a><span class="fa fa-search"></span></a> \
					</span> \
				</div> \
			</div> \
		</div> \
	</div> \
		<div class="col-md-3 hidden"> \
			<div class="form-group"> \
				<label class="control-label col-xs-12 " for="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi"><spring:message code="notificacio.form.camp.titular.dir3codi"/></label> \
				<div class="col-xs-12"> \
					<input id="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi" name="enviaments[#num_enviament#].destinataris[#num_destinatari#].dir3Codi" class="form-control " type="text" value=""> \
				</div> \
			</div> \
		</div> \
		<div class="col-md-2 offset-col-md-2"> \
			<div class="float-right"> \
				<input type="button" class="btn btn-danger btn-group delete" name="destinatarisDelete[#num_enviament#][#num_destinatari#]" onclick="destinatarisDelete(this.id)" id="destinatarisDelete[#num_enviament#][#num_destinatari#]" value="<spring:message code="notificacio.form.boto.eliminar.destinatari"/>"> \
			</div> \
		</div> \
		<div class="col-md-12"> \
			<hr style="border-top: 1px dotted #BBB"> \
		</div> \
	</div>';


	$(document).ready(function() {
		$(document).on('change','select.paisos', function() {
			var provincia = $(this).closest("#entregaPostal").find("select[class*='provincies']");
			var poblacioSelect = $(this).closest("#entregaPostal").find("div[class*='poblacioSelect']");
			var poblacioText = $(this).closest("#entregaPostal").find("div[class*='poblacioText']");
			if ($(this).val() != 'ES') {
				$(poblacioSelect).addClass('hidden');
				$(poblacioText).removeClass('hidden');
				$(provincia).prop('disabled', 'disabled');
				$(provincia).parent().parent().addClass('hidden');
			} else {
				$(poblacioSelect).removeClass('hidden');
				$(poblacioText).addClass('hidden');
				$(provincia).removeAttr('disabled');
				$(provincia).parent().parent().removeClass('hidden');
			}
		});

		// Validacio firma document fisic
		if (${validaFirmaWebEnabled} == true) {
			$('input[type="file"]').on('change', (event) => {
				let file = event.currentTarget;
				let $file = $(file);
				if (file.files.length == 0)
					return;

				let fitxer = file.files[0];
				let formData = new FormData();
				formData.append('fitxer', fitxer, fitxer.name);
				$file.prop("disabled", true);

				let metadadesId = $file.closest(".row").next(".doc-metadades").prop("id");
				let id = metadadesId.charAt(metadadesId.length - 1);

				$file.closest(".fileinput").next(".validating-block").remove();
				$('<p class="validating-block text-info"><span class="fa fa-spin fa-circle-o-notch"></span>&nbsp;<spring:message code="notificacio.form.valid.document.validant"/></p>').insertAfter($file.closest(".fileinput"));

				$.ajax({
					type: "POST",
					enctype: 'multipart/form-data',
					url: "<c:url value='/notificacio/valida/firma'/>",
					data: formData,
					processData: false,
					contentType: false,
					cache: false,
					timeout: 600000,
					success: function (data) {

						console.log("SUCCESS : ", data);
						$file.prop("disabled", false);
						$file.closest(".fileinput").next(".validating-block").remove();

						if (data.mediaType == 'application/pdf') {
							$('#documents\\[' + id + '\\]\\.modoFirma').prop('checked', data.signed);
						}
						if (data.error) {
							$file.closest(".form-group").addClass("has-error");
							$file.closest(".fileinput").next(".help-block").remove();
							<%--$('<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span id="arxiu' + id + '.errors"><spring:message code="notificacio.form.valid.document.firma"/></span></p>').insertAfter($file.closest(".fileinput"));--%>
							$('<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span id="arxiu' + id + '.errors"><spring:message code="notificacio.form.valid.document.error"/> ' + data.errorMsg + '</span></p>').insertAfter($file.closest(".fileinput"));
						} else {
							$file.closest(".form-group").removeClass("has-error");
							$file.closest(".fileinput").next(".help-block").remove();

							if (data.signed) {
								$('<p class="validating-block text-success"><span class="fa fa-check"></span>&nbsp;<spring:message code="notificacio.form.valid.document.firma.ok"/></p>').insertAfter($file.closest(".fileinput"));
							} else {
								$('<p class="validating-block text-success"><span class="fa fa-check"></span>&nbsp;<spring:message code="notificacio.form.valid.document.sense.firma"/></p>').insertAfter($file.closest(".fileinput"));
							}
						}
						// $('documents\\[' + id + '\\]\\.arxiuGestdocId').val(data.arxiuGestdocId);
						// $('documents\\[' + id + '\\]\\.arxiuNom').val(data.nom);
						// $('documents\\[' + id + '\\]\\.mediaType').val(data.mediaType);
						// $('documents\\[' + id + '\\]\\.mida').val(data.mida);

					},
					error: function (e) {

						console.log("ERROR : ", e);
						$file.prop("disabled", false);

					}
				});

			});
		}

		//Consulta al arxiu de los identificadores CSV o Uuid 
		//para comprobar si existe el documento y sus metadatos	
		$(".docArxiu").focusout(function() {
			
			if (!consultarFocusout)
				return;
			
			let inputElement = $(this);
			let inputElementValue = $(this).val().trim();
			let indexId = $(this).attr("id").split("[")[1].substring(0,1);

			resetWarningIErrorsDocArxiu(indexId);
			
			let esCsv = $(this).attr("id").toLowerCase().includes("csv");
			
			if (inputElementValue == '' || inputElementValue == null) {
				return;
			}
			
			if (!esCsv) { //Uuid validació
				var uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
				if (!inputElementValue.match(uuidPattern)){
					inputElement.parent().closest('.form-group').addClass('has-error');
					inputElement.parent().append('<div id="id_err_' + indexId + '"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<spring:message code="notificacio.form.camp.error.valor"/></p></div>');
					return;
				}
			}

			let url = esCsv ? "consultaDocumentIMetadadesCsv" : "consultaDocumentIMetadadesUuid";
			console.log("<c:url value="/notificacio/"/>" + url + "/" + inputElementValue);
			$.ajax({
				type: 'POST',
				contentType: "application/json",
				data: inputElementValue,
				url: "<c:url value="/notificacio/"/>" + url + "/consulta",
				success: function(data) {

					consultarFocusout = false;

					if (!data.validacioIdCsv) {
						inputElement.parent().closest('.form-group').addClass('has-error');
						inputElement.parent().append('<div id="id_err_' + indexId + '"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<spring:message code="notificacio.form.camp.error.valor"/></p></div>');
						return;
					}

					if (!data.documentExistent){
						inputElement.parent().closest('.form-group').addClass('has-error');
						inputElement.parent().append('<div id="document_err_' + indexId + '"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<spring:message code="notificacio.form.camp.error.document.inexistent"/></p></div>');
					}
					else if (!data.metadadesExistents){ //document pero sin metadades
						inputElement.addClass('warningClass');
						inputElement.parent().append('<div id="metadades_war_' + indexId + '"><p class="help-block" style="color: orange;"><span class="fa fa-exclamation-triangle"></span>&nbsp;<spring:message code="notificacio.form.camp.error.metadades.inexistent"/></p></div>');
					}
					else { //document y metadades
						if (data.origen != null) {
							$("#documents\\[" +indexId+ "\\]\\.origen").val(data.origen).trigger("change.select2");
							$("#documents\\[" +indexId+ "\\]\\.origen").prop('disabled', true);
						}
						if (data.validesa != null) {
							$("#documents\\[" +indexId+ "\\]\\.validesa").val(data.validesa).trigger("change.select2");
							$("#documents\\[" +indexId+ "\\]\\.validesa").prop('disabled', true);
						}
						if (data.tipoDocumental != null) {
							$("#documents\\[" +indexId+ "\\]\\.tipoDocumental").val(data.tipoDocumental).trigger("change.select2");
							$("#documents\\[" +indexId+ "\\]\\.tipoDocumental").prop('disabled', true);
						}
						if (data.modoFirma != null) {
							$("#documents\\[" +indexId+ "\\]\\.modoFirma").prop('checked', data.modoFirma);
							$("#documents\\[" +indexId+ "\\]\\.modoFirma").prop('disabled', true);
						}
					}
				},
				error: function() {
					consultarFocusout = false;
					console.log("error obtenint el document CSV i les seves metadades...");
				}
				});
		});
		
		$("#saveForm").click(function(event) {

			event.stopImmediatePropagation();
			event.preventDefault();

			let continuar = true;

			// Comprovarem si s'està intentant enviar una comunicació o comunicació sense nif.
			// En cas afirmatiu mostrarem un confirm
			const isComunicacio = ${enviamentTipus == 'COMUNICACIO'};
			const isNotificacio = ${enviamentTipus == 'NOTIFICACIO'};

			const comunicacioSenseNifMsg = "<spring:message code="notificacio.form.comunicacio.sense.nif.confirm" />";
			const notificacioSenseNifMsg = "<spring:message code="notificacio.form.notificacio.sense.nif.confirm" />";
			const comunicacioSenseNifNiPostalMsg = "<spring:message code="notificacio.form.comunicacio.sense.nif.ni.postal.confirm" />";
			const notificacioSenseNifNiPostalMsg = "<spring:message code="notificacio.form.notificacio.sense.nif.ni.postal.confirm" />";

			if (isComunicacio || isNotificacio) {

				let enviamentsSenseNif = [];
				$(".enviamentsForm").each(function (index) {
					let tipus = $(".interessat", $(this)).val();
					let numDestinataris = $(".destinatariForm", $(this)).size();
					let entregaPostalActiva = viewModel.ambEntregaCIE && $(".entrega-cie-activa", $(this)).closest("input").attr('checked');
					if (tipus == "FISICA_SENSE_NIF" && numDestinataris == 0 && !entregaPostalActiva) {
						enviamentsSenseNif.push(index);
					}
				});

				if (enviamentsSenseNif.length > 0) {
					continuar = false;
					let missatge = "";
					if (isNotificacio) {
						missatge = viewModel.ambEntregaCIE ? notificacioSenseNifNiPostalMsg : notificacioSenseNifMsg;
					} else {
						missatge = viewModel.ambEntregaCIE ? comunicacioSenseNifNiPostalMsg : comunicacioSenseNifMsg;
					}
					missatge += "<ul>";
					enviamentsSenseNif.forEach(function(item) {
						missatge += "<li>Enviament " + (item + 1) + " - Titular: " + $("#enviaments\\[" + item +"\\]\\.titular\\.nomInput").val() + " " + $("#enviaments\\[" + item +"\\]\\.titular\\.llinatge1").val() + "</li>";
					});
					missatge += "</ul>";

					bootbox.confirm({
						title: "Enviar?",
						size: "large",
						message: missatge,
						locale: 'ca',
						callback: function(result){
							if (result) {
								for (indexId = 0; indexId < 5; indexId++) {
									activarCampsMetadades(indexId);
								}
								$("#form").submit();
							}
						}
					})
				}
			}

			if (continuar) {
				for (indexId = 0; indexId < 5; indexId++) {
					activarCampsMetadades(indexId);
				}
				$("#form").submit();
			}
		});
		
		$(".docArxiu").on('change', function() {
			consultarFocusout = true;
			let indexId = $(this).attr("id").split("[")[1].substring(0,1);
			activarCampsMetadades(indexId);
		});


		$('.customSelect').webutilInputSelect2(null);

		// Documents
		var isWindowReload = [true, true, true, true, true];
		$('.tipusDocument').on('change', function() {
			let id = $(this).attr("id").split("_")[1];
			if (!isWindowReload[id]) {
				resetWarningIErrorsDocArxiu(id);
			} else {
				isWindowReload[id] = false;
			}
	
			activarCampsMetadades(id);
			if ($(this).val() == 'CSV') {
				$('#input-origen-csv_' + id).removeClass('hidden');
				$('#input-origen-uuid_' + id).addClass('hidden');
				$('#documentArxiuUuid\\[' + id + '\\]').val('');
				$('#input-origen-url_' + id).addClass('hidden');
				$('#documentArxiuUrl\\[' + id + '\\]').val('');
				$('#input-origen-arxiu_' + id).addClass('hidden');
				$('#arxiu\\[' + id + '\\]').val('');
				$('#metadades_' + id).removeClass('hidden');
			} else if ($(this).val() == 'UUID') {
				$('#input-origen-csv_' + id).addClass('hidden');
				$('#documentArxiuCsv\\[' + id + '\\]').val('');
				$('#input-origen-uuid_' + id).removeClass('hidden');
				$('#input-origen-url_' + id).addClass('hidden');
				$('#documentArxiuUrl\\[' + id + '\\]').val('');
				$('#input-origen-arxiu_' + id).addClass('hidden');
				$('#arxiu\\[' + id + '\\]').val('');
				$('#metadades_' + id).removeClass('hidden');
			} else if ($(this).val() == 'URL') {
				$('#input-origen-csv_' + id).addClass('hidden');
				$('#documentArxiuCsv\\[' + id + '\\]').val('');
				$('#input-origen-uuid_' + id).addClass('hidden');
				$('#documentArxiuUuid\\[' + id + '\\]').val('');
				$('#input-origen-url_' + id).removeClass('hidden');
				$('#input-origen-arxiu_' + id).addClass('hidden');
				$('#arxiu\\[' + id + '\\]').val('');
				$('#metadades_' + id).removeClass('hidden');
			} else if ($(this).val() == 'ARXIU'){
				$('#input-origen-csv_' + id).addClass('hidden');
				$('#documentArxiuCsv\\[' + id + '\\]').val('');
				$('#input-origen-uuid_' + id).addClass('hidden');
				$('#documentArxiuUuid\\[' + id + '\\]').val('');
				$('#input-origen-url_' + id).addClass('hidden');
				$('#documentArxiuUrl\\[' + id + '\\]').val('');
				$('#input-origen-arxiu_' + id).removeClass('hidden');
				$('#metadades_' + id).removeClass('hidden');
			} else if ($(this).val() == ''){
				$('#input-origen-csv_' + id).addClass('hidden');
				$('#documentArxiuCsv\\[' + id + '\\]').val('');
				$('#input-origen-uuid_' + id).removeClass('hidden');
				$('#documentArxiuUuid\\[' + id + '\\]').val('');
				$('#input-origen-url_' + id).addClass('hidden');
				$('#documentArxiuUrl\\[' + id + '\\]').val('');
				$('#input-origen-arxiu_' + id).addClass('hidden');
				$('#arxiu\\[' + id + '\\]').val('');
				$('#metadades_' + id).addClass('hidden');
			}
			webutilModalAdjustHeight();
		});

		var nom_documents = [
			"${nomDocument_0}",
			"${nomDocument_1}",
			"${nomDocument_2}",
			"${nomDocument_3}",
			"${nomDocument_4}",
		];

		for (let i = 0; i < 5; i++) {
			let document_arxiuNom = $('input[name="documents\\[' + i + '\\].arxiuNom"]').val();
			let tipusDocumentDefault = $('#tipusDocumentDefault' + i).val();
			let tipusDocumentSelected = $('#tipusDocumentSelected_' + i).val();
			if (tipusDocumentSelected !== '') {
				if (i > 0) {
					$("#document" + (i + 1)).removeClass("hidden");
					numDocuments++;
				}
				$("#tipusDocument_" + i).val(tipusDocumentSelected).trigger("change");
			} else if (tipusDocumentDefault !== '' && (i == 0 || document_arxiuNom !== '')) {
				if (i > 0) {
					$("#document" + (i + 1)).removeClass("hidden");
					numDocuments++;
				}
				$("#tipusDocument_" + i).val(tipusDocumentDefault).trigger("change");
				if (tipusDocumentDefault === 'CSV') {
					$('#documentArxiuCsv\\[' + i + '\\]').val(nom_documents[i]);
				} else if (tipusDocumentDefault === 'UUID') {
					$('#documentArxiuUuid\\[' + i + '\\]').val(nom_documents[i]);
				} else if (tipusDocumentDefault === 'URL') {
					$('#documentArxiuUrl\\[' + i + '\\]').val(nom_documents[i]);
				}
				// $("#tipusDocument_" + i).val(tipusDocumentDefault[i]).trigger("change");
			}
		}


		$('#addDocument').click(function() {
			let tipusDocumentDefault = $('#tipusDocumentDefault' + numDocuments).val();
			console.debug(tipusDocumentDefault);
			$("#tipusDocument_" + numDocuments).val(tipusDocumentDefault).trigger("change");
			$('#document' + (numDocuments + 1)).removeClass('hidden');
			numDocuments++;
			if (numDocuments == 5)
				$('#addDocument').addClass('hidden');
			$('#removeDocument').removeClass('hidden');
		});

		$('#removeDocument').click(function() {
			$('#document' + numDocuments).addClass('hidden');
			var documentFields = numDocuments - 1;

			$('#document' + numDocuments).find('input[name="documents[' + documentFields + '].id"]').val('');
			$('#document' + numDocuments).find('input[name="documents[' + documentFields + '].arxiuGestdocId"]').val('');
			$('#document' + numDocuments).find('input[name="documents[' + documentFields + '].arxiuNom"]').val('');
			$('#document' + numDocuments).find('input[name="documents[' + documentFields + '].mediaType"]').val('');
			$('#document' + numDocuments).find('input[name="documents[' + documentFields + '].mida"]').val('');
			numDocuments--;
			$('#tipusDocument_' + numDocuments).val('').trigger('change');

			if (numDocuments == 1)
				$('#removeDocument').addClass('hidden');
			$('#addDocument').removeClass('hidden');
		});

		if ($('#document2').is(":visible")) {
			$('#removeDocument').removeClass('hidden');
		}

		$(document).on('change','select.enviamentTipus', function() {
			var dadesNormalitzat = $(this).closest("#entregaPostal").find("div[class*='normalitzat']");
			var dadesSenseNormalitzar = $(this).closest("#entregaPostal").find("div[class*='senseNormalitzar']");

			if ($(this).val() == 'SENSE_NORMALITZAR') {
				$(dadesNormalitzat).addClass('hidden');
				$(dadesSenseNormalitzar).removeClass('hidden');
			} else {
				$(dadesNormalitzat).removeClass('hidden');
				$(dadesSenseNormalitzar).addClass('hidden');
			}
		});

		$('input[name="arxiu[0]"]').change(function(){
			var arxiuGestdocId = $('input[name="documents[0].arxiuGestdocId"]');
			var arxiuNom =  $('input[name="documents[0].arxiuNom"]');
			if($(this).val() == ''){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}else if($(this).val() != '' && $(this).val() != arxiuNom){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}
		});
		$('input[name="arxiu[1]"]').change(function(){
			var arxiuGestdocId = $('input[name="documents[1].arxiuGestdocId"]');
			var arxiuNom =  $('input[name="documents[1].arxiuNom"]');
			if($(this).val() == ''){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}else if($(this).val() != '' && $(this).val() != arxiuNom){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}
		});
		$('input[name="arxiu[2]"]').change(function(){
			var arxiuGestdocId = $('input[name="documents[2].arxiuGestdocId"]');
			var arxiuNom =  $('input[name="documents[2].arxiuNom"]');
			if($(this).val() == ''){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}else if($(this).val() != '' && $(this).val() != arxiuNom){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}
		});
		$('input[name="arxiu[3]"]').change(function(){
			var arxiuGestdocId = $('input[name="documents[3].arxiuGestdocId"]');
			var arxiuNom =  $('input[name="documents[3].arxiuNom"]');
			if($(this).val() == ''){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}else if($(this).val() != '' && $(this).val() != arxiuNom){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}
		});
		$('input[name="arxiu[4]"]').change(function(){
			var arxiuGestdocId = $('input[name="documents[4].arxiuGestdocId"]');
			var arxiuNom =  $('input[name="documents[4].arxiuNom"]');
			if($(this).val() == ''){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}else if($(this).val() != '' && $(this).val() != arxiuNom){
				arxiuGestdocId.val("");
				arxiuNom.val("");
			}
		});

		var numPlus = 1;

		$(".container-envios").find('.enviamentsForm').each(function() {
			if($(this).find('.eliminar_enviament').attr('id') != 'enviamentDelete_0') {
				$(this).find('.eliminar_enviament').removeClass('hidden');
			}
		});

		// var agrupable = $("#procedimentId").children(":selected").attr("class");
		// var procedimentId = $("#procedimentId").children(":selected").attr("value");

		$('#organGestor').on('change', function() {
			//### seleccionat per defecte si només hi ha un (empty + òrgan)
			let num_organs = $('#organGestor').children('option').length - 1;
			if (num_organs === 1) {
				let selDir3 = $('#organGestor > option')[1].value;
				$("#organGestor").val(selDir3).trigger("change.select2");
			}else if(num_organs === 2){
				$('#organGestor > option').each(function() {
					if(this.value === $('#entitatDir3Codi').val())
						$('#organGestor option:eq(1)').attr('selected', 'selected');
					$('#organGestor').trigger('change.select2');
				});
			}

			let organ = $(this).val();
			if (organ == undefined || organ == "") {
				organ = "-";
			}


			$("#organGestor").closest(".form-group").removeClass("has-error");
			$("#organGestor").closest(".form-group").find(".help-block").remove();

			$.ajax({
				type: 'GET',
				url: "<c:url value="/notificacio/organ/oficina/"/>"+ organ,
				success: data => {
					let e = "${enviamentTipus}"
					if (data && !data.codi && e === 'COMUNICACIO_SIR') {
						$("#organGestor").closest(".form-group").addClass("has-error");
						let msg = "<spring:message code="notificacio.form.valid.organ.sense.oficina"/>"
						$('<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp' + msg+ '</p>').insertAfter($("#organGestor").closest(".form-group").find(".select2-container"));
					}
				},
				error: () => console.log("error obtenint els dies de caducitat a partir de la data")
			});
			// if (num_organs > 0) {
				loadProcediments(organ);
				loadServeis(organ, '${notificacioCommand.tipusProcSer}' == 'PROCEDIMENT' );
			// }

		});
		$('#procedimentId').on('change', function() {
			updateOrgansForProcSerChange($(this).val());
		});
		$('#serveiId').on('change', function() {
			updateOrgansForProcSerChange($(this).val());
		});

		$('#rd_procediment').on('change', function () {
			if ($(this).prop('checked')) {
				$("#serveiRow").hide();
				$("#procedimentRow").show();
				$('#serveiId').val("").trigger('change.select2');;
				loadProcediments(getOrganVal());
			}
		});

		$('#rd_servei').on('change', function () {
			if ($(this).prop('checked')) {
				$("#procedimentRow").hide();
				$("#serveiRow").show();
				$('#procedimentId').val("").trigger('change.select2');;
				loadServeis(getOrganVal(), false);
			}
		});
		$('#organGestor').trigger('change');

		if ($('#rd_procediment').prop('checked')) {
			$("#serveiRow").hide();
		} else {
			$("#procedimentRow").hide();
		}

		//Eliminar grups
		$(document).on('click', "#remove", function () {
			var grupId = $(this).parent().children().attr('id');
			var grupsClass = $(this).attr('class');
			var lastClass = grupsClass.split(' ').pop();
			var parentRemove = $("." + lastClass).parent();
			var parentInput = parentRemove.parent();
			var parentDiv = parentInput.parent();

			parentDiv.slideUp("normal", function() {
				$(this).remove();
				webutilModalAdjustHeight();
			});

		});
		$(document).on('change', '.interessat', function() {
			let closest = $(this).closest('.destinatariForm, .personaForm');
			let llinatge1 = closest.find('.llinatge1');
			let llinatge2 = closest.find('.llinatge2');
			let enviamentTipus = $('input#enviamentTipus').val();
			let nif = closest.find('.nif');
			let docTipus = closest.find('.docTipus');
			let nifAlert = closest.find('.alerta-sense-nif');
			let nifLabel = nif.find('label');
			let dir3codi = closest.find('.dir3Codi');
			let nifLabelText = "<spring:message code='notificacio.form.camp.titular.nif'/>";
			let noNifLabelText = "<spring:message code='notificacio.form.camp.titular.sense.nif'/>";
			let cifLabelText = "<spring:message code='notificacio.form.camp.titular.cif'/>";
			let administracioLabelText = "<spring:message code='notificacio.form.camp.titular.administracio'/>";
			let email = closest.find('.email');
			let emailLabel = email.find('label');
			let emailLabelText = "<spring:message code='notificacio.form.camp.titular.email'/>";
			let incapacitat = closest.find('.incapacitat');
			let raoSocial = closest.find('.rao');
			let index = closest.find(".rowId input").val();
			let raoSocialDesc = raoSocial.find('input').val();
			let dir3Desc = closest.find('.codiDir3 input').val();
			let raoSocialInput = closest.find(".raoSocialInput");
			let nomInput = closest.find(".nomInput");
			let dir3Label = dir3codi.find('label');
			let dir3LabelText = "<spring:message code='notificacio.form.camp.titular.dir3codi'/>";
			// console.log($(this));
			if ($(this).val() == 'ADMINISTRACIO') {
				$(llinatge1).addClass('hidden');
				$(llinatge2).addClass('hidden');
				$(dir3codi).removeClass('hidden');
				$(dir3Label).html(dir3LabelText + " *");
				$(incapacitat).addClass('hidden');
				$(raoSocial).addClass('hidden');
				$(docTipus).addClass('hidden');
				if(enviamentTipus == 'COMUNICACIO_SIR'){
					$(nifLabel).text(administracioLabelText);
					$(nif).addClass('hidden');
				}else{
					$(nifLabel).text(administracioLabelText + " *");
					$(nif).removeClass('hidden');
				}
				$(emailLabel).text(emailLabelText);
				$(raoSocialInput).hide();
				$(nomInput).hide();
			} else if ($(this).val() == 'FISICA') {
				$(llinatge1).removeClass('hidden');
				$(llinatge2).removeClass('hidden');
				$(nif).removeClass('hidden');
				$(nifLabel).text(nifLabelText + " *");
				$(nifAlert).hide();
				$(docTipus).addClass('hidden');
				$(dir3codi).addClass('hidden');
				$(incapacitat).removeClass('hidden');
				$(raoSocial).removeClass('hidden');
				$(emailLabel).text(emailLabelText);
				$(raoSocialInput).hide();
				$(nomInput).show();
			} else if ($(this).val() == 'FISICA_SENSE_NIF') {
				$(llinatge1).removeClass('hidden');
				$(llinatge2).removeClass('hidden');
				$(nif).removeClass('hidden');
				$(nifLabel).text(noNifLabelText);
				$(nifAlert).show();
				$(docTipus).removeClass('hidden');
				$(dir3codi).addClass('hidden');
				$(incapacitat).removeClass('hidden');
				$(raoSocial).removeClass('hidden');
				$(emailLabel).text(emailLabelText + " *");
				$(raoSocialInput).hide();
				$(nomInput).show();
			} else {
				console.log("juridica");
				$(llinatge1).addClass('hidden');
				$(llinatge2).addClass('hidden');
				$(nif).removeClass('hidden');
				$(nifAlert).hide();
				$(docTipus).addClass('hidden');
				$(dir3codi).addClass('hidden');
				$(nifLabel).text(cifLabelText + " *");
				$(incapacitat).removeClass('hidden');
				$(raoSocial).removeClass('hidden');
				$(emailLabel).text(emailLabelText);
				$(raoSocialInput).show();
				$(nomInput).hide();
				$(dir3codi).removeClass('hidden');
				$(dir3Label).html(dir3LabelText);
			}

			if((raoSocialDesc != null && raoSocialDesc != "") && (dir3Desc != null && dir3Desc != "")){
				document.getElementById("searchOrganTit" + index).getElementsByTagName('input')[0].value = dir3Desc+'-'+raoSocialDesc;
				$(dir3codi).find('.help-block').addClass('hidden')
				$(dir3codi).find('.form-group').removeClass('has-error')
			}else if(document.getElementById("enviaments["+ index+ "].titular.dir3Codi.errors") != null && document.getElementById("enviaments["+ index+ "].titular.dir3Codi.errors").innerText != '' ){
				$(dir3codi).find('.help-block').removeClass('hidden')
				$(dir3codi).find('.form-group').addClass('has-error')
			}

			// clearDocuments(numDocuments);

		});

		$(document).on('input', ".titularNif", function () {
			$(this).closest('.enviamentsForm').find('.nifemisor').val($(this).val());
		});

		$('.interessat').trigger('change');
		// $('.tipusDocument').trigger('change');
		$('.enviamentTipus').trigger('change');

		//Contado descripció
		var fieldDescripcio = $('#descripcio');
		if (fieldDescripcio.val().length != 0) {
			var size = $(fieldDescripcio).val().length;
			$('.textAreaCurrentLength').text(size);
		} else {
			$('.textAreaCurrentLength').text(0);
		};;

		$(fieldDescripcio).bind("change paste keyup", function() {
			var size = $(this).val().length;
			$('.textAreaCurrentLength').text(size);
		});

		//loading
		$('#form').on("submit", function(){
			$('.loading').fadeIn();
		});

		$("#rOrgans").on("dblclick", "tr", function() {
			if(!$(this).hasClass('unselectable')){
				seleccionar($(this));
			}
		});
		$("#rOrgans").on("click", ".select", function() {
			if(!$(this).hasClass('unselectable')){
				seleccionar($(this).closest("tr"));
			}
		});

		//### #432
		$("#descripcio").on("keydown", function (e) {
			if (e.keyCode != 13) return;
			var warning = "<spring:message code='notificacio.form.camp.descripcio.write.validacio'/>";
			makeTooltip(warning);
			return false;
		});
		$("#descripcio").on("change paste", function (e) {
			var warning = "<spring:message code='notificacio.form.camp.descripcio.paste.validacio'/>";
			var e = $(this);
			setTimeout(function(){
				while (/\r?\n|\r/.test($.trim(e.val()))) {
					makeTooltip(warning);
					e.val($.trim(e.val()).replace(/\r?\n|\r\r\n/, ' '));
				}
			}, 0);
		});

		$("#o_provincia").select2({
			theme: 'bootstrap',
			width: 'auto',
			allowClear: true,
	        placeholder: "<spring:message code='comu.placeholder.seleccio'/>"
		});
		$("#o_localitat").select2({
			theme: 'bootstrap',
			width: 'auto',
			allowClear: true,
	        placeholder: "<spring:message code='comu.placeholder.seleccio'/>"
		});
		
		$("input[name=idioma][value=" + locale.toUpperCase()+ "]").prop('checked', true);

		// Data caducitat
		$("#caducitat").change( () => updateCaducitatDiesNaturals($("#caducitat").val()));

		$("#caducitatDiesNaturals").change(function () {
			let val = $("#caducitatDiesNaturals").val();
			val = val > 99999999 ? 99999999 : val;
			$("#caducitatDiesNaturals").val(val);
			updateCaducitatAmbDies($("#caducitatDiesNaturals").val());
		});

		$(window).keydown(function(event){
			if(event.keyCode == 13) {
				event.preventDefault();
				return false;
			}
		});
	});

	function updateCaducitatDiesNaturals(data) {
		var campsData = data.split("/");
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/caducitatDiesNaturals/"/>" + campsData[0] + "/" + campsData[1] + "/" + campsData[2],
			success: function (data) {
				$("#caducitatDiesNaturals").val(data);
			},
			error: function () {
				console.log("error obtenint els dies de caducitat a partir de la data");
			}
		});
	}

	function updateCaducitatAmbDies(dies) {
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/caducitatData/"/>" + dies,
			success: function (data) {
				$("#caducitat").val(data);
			},
			error: function () {
				console.log("error obtenint la data de caducitat a partir dels dies");
			}
		});
	}

	function loadProcediments(organ) {
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/organ/"/>" + organ + "/procediments",
			success: function(data) {
				var select2Options = {
					theme: 'bootstrap',
					width: 'auto'};
				// Procediments
				var procediments = data;
				var selProcediments = $("#procedimentId");
				selProcediments.empty();
				if (procediments && procediments.length > 0) {
					selProcediments.append("<option value=\"\"><spring:message code='notificacio.form.camp.procediment.select'/></option>");
					var procedimentsComuns = [];
					var procedimentsOrgan = [];
					$.each(data, function(i, val) {
						if(val.comu) {
							procedimentsComuns.push(val);
						} else {
							procedimentsOrgan.push(val);
						}
					});
					if (procedimentsComuns.length > 0) {
						selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.comuns'/>'>");
						$.each(procedimentsComuns, function(index, val) {
							selProcediments.append("<option value=\"" + val.codi + "\">" + val.valor + "</option>");
						});
						selProcediments.append("</optgroup>");
					}
					var isOnlyOneProcedimentOrgan = (procedimentsOrgan.length < 2);
					if (procedimentsOrgan.length > 0) {
						selProcediments.append("<optgroup label='<spring:message code='notificacio.form.camp.procediment.organs'/>'>");
						$.each(procedimentsOrgan, function(index, val) {
							if (isOnlyOneProcedimentOrgan) {
								selProcediments.append("<option value='" + val.codi + "' selected>" + val.valor + "</option>");
								$("#organGestor").val(val.organGestor).trigger("change.select2");
							} else {
								selProcediments.append("<option value='" + val.codi + "'>" + val.valor + "</option>");
							}
						});
						selProcediments.append("</optgroup>");
						selProcediments.trigger('change.select2');
					}
					if (selProcediments.children('option').length == 2) {
						$('#procedimentId option:eq(1)').attr('selected', 'selected');
						selProcediments.trigger('change');
					}
				} else {
					selProcediments.append("<option value=\"\"><spring:message code='notificacio.form.camp.procediment.buit'/></option>");
				}
				selProcediments.select2(select2Options);
				// selProcediments.val(selProcediments.attr('data-enum-value'));
				// selProcediments.trigger('change');

				let numProcediments = $('#procedimentId').children('option').length - 1;
				if (numProcediments > 1) {
					if ('${notificacioCommand != null && procedimentIdAux != null}') {
						$("#procedimentId").val('${notificacioCommand.procedimentId}');
						$('#procedimentId').trigger('change');
						procedimentIdAux = null;
					}
				}
			},
			error: function() {
				console.log("error obtenint els procediments de l'òrgan gestor...");
			}
		});
	}

	function loadServeis(organ, carregaInicial) {
		console.log("loadServeis");
		$.ajax({
			type: 'GET',
			url: "<c:url value="/notificacio/organ/"/>" + organ + "/serveis",
			success: function(data) {
				var select2Options = {
					theme: 'bootstrap',
					width: 'auto'};
				// Procediments
				var servei = data;
				var selServeis = $("#serveiId");
				selServeis.empty();
				if (servei && servei.length > 0) {
					selServeis.append("<option value=\"\"><spring:message code='notificacio.form.camp.servei.select'/></option>");
					var serveisComuns = [];
					var serveisOrgan = [];
					$.each(data, function(i, val) {
						if(val.comu) {
							serveisComuns.push(val);
						} else {
							serveisOrgan.push(val);
						}
					});
					if (serveisComuns.length > 0) {
						selServeis.append("<optgroup label='<spring:message code='notificacio.form.camp.servei.comuns'/>'>");
						$.each(serveisComuns, function(index, val) {
							selServeis.append("<option value=\"" + val.codi + "\">" + val.valor + "</option>");
						});
						selServeis.append("</optgroup>");
					}
					var isOnlyOneServeiOrgan = (serveisOrgan.length < 2);
					if (serveisOrgan.length > 0) {
						selServeis.append("<optgroup label='<spring:message code='notificacio.form.camp.servei.organs'/>'>");
						$.each(serveisOrgan, function(index, val) {
							if (isOnlyOneServeiOrgan && !carregaInicial) {
								selServeis.append("<option value='" + val.codi + "' selected>" + val.valor + "</option>");
								$("#organGestor").val(val.organGestor).trigger("change.select2");
							} else {
								selServeis.append("<option value='" + val.codi + "'>" + val.valor + "</option>");
							}
						});
						selServeis.append("</optgroup>");
						selServeis.trigger('change.select2');
					}
					if (selServeis.children('option').length == 2 && !carregaInicial) {
						$('#procedimentId option:eq(1)').attr('selected', 'selected');
						selServeis.trigger('change');
					}
				} else {
					selServeis.append("<option value=\"\"><spring:message code='notificacio.form.camp.servei.buit'/></option>");
				}
				selServeis.select2(select2Options);
				// selProcediments.val(selProcediments.attr('data-enum-value'));
				// selProcediments.trigger('change');

				let numServeis = $('#serveiId').children('option').length - 1;
				if (numServeis > 1) {
					if ('${notificacioCommand != null && serveiIdAux != null}') {
						$("#serveiId").val('${notificacioCommand.serveiId}');
						$('#serveiId').trigger('change');
						serveiIdAux = null;
					}
				}
			},
			error: function() {
				console.log("error obtenint els serveis de l'òrgan gestor...");
			}
		});
	}

	function updateOrgansForProcSerChange(procediment) {
		if (procediment == null) {
			alert("No s'ha pogut trobar el procediment o servei de la notificació, segurament degut a que " +
					"els permisos que hi tens assignats són insuficients.")
			return;
		}
		if (procediment == '') {
			$("#organGestor").prop("disabled", false);
		} else {
			$.ajax({
				type: 'GET',
				url: "<c:url value="/notificacio/procediment/"/>" + procediment + "/dades",
				success: function(data) {
					var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
					// Òrgan gestor
					if (!data.comu) {
						$("#organGestor").val(data.organCodi).trigger("change.select2");
					} else if (data.organsDisponibles.length) {
						if (data.organsDisponibles.length == 1) {
							$("#organGestor").val(data.organsDisponibles[0]).trigger("change.select2");
						}
					}
					// Caducitat
					$("#caducitat").val(data.caducitat);
					$("#caducitatDiesNaturals").val(data.caducitatDiesNaturals);
					// Retard
					$("#retard").val(data.retard);
					// Grups
					var grups = data.grups;
					var selGrups = $("#grupId");
					selGrups.empty();
					selGrups.append("<option value=\"\"></option>");
					if (data.agrupable && grups && grups.length > 0) {
						$.each(grups, function(i, val) {
							selGrups.append("<option value=\"" + val.id + "\">" + val.nom + "</option>");
						});
						selGrups.select2(select2Options);
						$("#grups").removeClass("hidden");
					} else {
						$("#grups").addClass("hidden");
					}

					viewModel.ambEntregaCIE = data.entregaCieActiva;
					if (!data.entregaCieVigent) {
						$("#entregaPostalCaducada").show();
					} else {
						$("#entregaPostalCaducada").hide();
					}

					// TODO: Afegir formats de fulla i sobre
					// Format fulla
// 					var selFormatFulla = $("#grupId");
// 					selFormatFulla.empty();
// 					selFormatFulla.append("<option value=\"\"></option>");
// 					var formatsFulla = data.formatsFulla;
// 					if (grups && grups.ength > 0) {
// 						$.each(grups, function(i, val) {
// 							selGrups.append("<option value=\"" + val.id + "\">" + val.nom + "</option>");
// 						});
// 					}
// 					selGrups.select2(select2Options);
					<%--
                                        // Format sobre
                                        <div class="col-md-3 formatFulla">
                                            <c:choose>
                                                <c:when test="${not empty formatsFulla}">
                                                    <not:inputSelect name="enviaments[${j}].entregaPostal.formatFulla" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatfulla" optionItems="${formatsFulla}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <not:inputText name="enviaments[${j}].entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" labelClass="labelcss" inputClass="inputcss"/>
                                                </c:otherwise>
                                            </c:choose>
                                            </div>
                                            <div class="col-md-3 formatSobre">
                                            <c:choose>
                                                <c:when test="${not empty formatsSobre}">
                                                    <not:inputSelect name="enviaments[${j}].entregaPostal.formatSobre" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatsobre" optionItems="${formatsSobre}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <not:inputText name="enviaments[${j}].entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" labelClass="labelcss" inputClass="inputcss"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                    --%>
				},
				error: function() {
					console.log("error obtenint la informació del procediment o servei...");
				}
			});
		}
	}

	function getOrganVal() {
		var organ = $('#organGestor').val();
		if (organ == undefined || organ == "") {
			organ = "-";
		}
		return organ;
	}

</script>
<div class="loading">
		<div class="loading-gif">
		<img src="<c:url value="/img/ajax-loader.gif"/>"/>
		</div>
		<div class="loading-text">
		<p><spring:message code="notificacio.form.loading"/></p>
		</div>
	</div>
    <c:set var="formAction"><not:modalUrl value="/notificacio/newOrModify"/></c:set>
    <form:form action="${formAction}" id="form" method="post" cssClass="form-horizontal" commandName="notificacioCommand" enctype="multipart/form-data">
		<form:hidden path="enviamentTipus" id="enviamentTipus"/>
		<input type="hidden" name="id" value="${notificacioCommand.id}">
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-address-book"></span>
				<label><spring:message code="notificacio.form.titol.dadesgenerals" /></label>
				<hr/>
			</div>
<%-- 			<form:hidden path="procedimentId" value="${procediment.id}" /> --%>
			<form:hidden path="emisorDir3Codi" id="emisorDir3Codi" value="${entitat.dir3Codi}" />
			
			<!-- CONCEPTE -->
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="concepte" textKey="notificacio.form.camp.concepte" labelSize="2" required="true" showsize="true"
								   inputMaxLength="${concepteSize}" inputMinLength="3"/>
				</div>
			</div>
			
			<!-- DESCRIPCIÓ -->
			<div class="row">
				<div class="col-md-12">
					<not:inputTextarea name="descripcio" textKey="notificacio.form.camp.descripcio" labelSize="2" inputMaxLength="${descripcioSize}"/>
				</div>
			</div>
			
			<!-- ORGAN -->
			<div class="row">
				<div class="col-md-12">
					<not:inputSelect 
						name="organGestor" 
						textKey="notificacio.form.camp.organEmisor"
						required="true" 
						optionItems="${organsGestors}" 
						optionValueAttribute="codi" 
						optionTextAttribute="valor"
						labelSize="2" 
						emptyOption="true"
						optionMinimumResultsForSearch="2"
						emptyOptionTextKey="notificacio.form.camp.organ.emisor.select"/>
				</div>
			</div>

			<div class="row" <c:if test="${enviamentTipus == 'NOTIFICACIO'}">style="display: none;"</c:if>>
				<div class="col-xs-2"> </div>
				<div class="form-group col-xs-10">
					<div class="btn-group" data-toggle="buttons" style="padding-left: 15px;">
						<label class="btn btn-warning <c:if test="${notificacioCommand.tipusProcSer=='PROCEDIMENT'}">active</c:if>">
<%--							<input type="radio" name="tipusProcSer" id="rd_procediment" value="PROCEDIMENT" <c:if test="${notificacioCommand.tipusProcSer=='PROCEDIMENT'}">checked</c:if>><spring:message code="notificacio.form.camp.procediment"/>--%>
							<form:radiobutton path="tipusProcSer" id="rd_procediment" value="PROCEDIMENT" /><spring:message code="notificacio.form.camp.procediment"/>
						</label>
						<label class="btn btn-warning <c:if test="${notificacioCommand.tipusProcSer=='SERVEI'}">active</c:if>">
<%--							<input type="radio" name="tipusProcSer" id="rd_servei" value="SERVEI" <c:if test="${notificacioCommand.tipusProcSer!='PROCEDIMENT'}">checked</c:if>><spring:message code="notificacio.form.camp.servei"/>--%>
							<form:radiobutton path="tipusProcSer" id="rd_servei" value="SERVEI" /><spring:message code="notificacio.form.camp.servei"/>
						</label>
					</div>
				</div>
			</div>
			<!-- PROCEDIMENT -->
			<div id="procedimentRow" class="row">
				<div class="col-md-12">
					<not:inputSelect 
						name="procedimentId" 
						textKey="notificacio.form.camp.procediment" 
						required="${enviamentTipus == 'NOTIFICACIO'}"
						optionItems="${procediments}" 
						optionValueAttribute="codi"
						optionTextAttribute="codi"
						labelSize="2"
						emptyOption="true"
						optionMinimumResultsForSearch="2"
						emptyOptionTextKey="notificacio.form.camp.procediment.select"/>
				</div>
			</div>

			<!-- SERVEI -->
			<div id="serveiRow" class="row">
				<div class="col-md-12">
					<not:inputSelect
							name="serveiId"
							textKey="notificacio.form.camp.servei"
							required="${enviamentTipus == 'NOTIFICACIO'}"
							optionItems="${serveis}"
							optionValueAttribute="codi"
							optionTextAttribute="codi"
							labelSize="2"
							emptyOption="true"
							optionMinimumResultsForSearch="2"
							emptyOptionTextKey="notificacio.form.camp.servei.select"/>
				</div>
			</div>
			
			<!-- GRUP -->
			<div id="grups" class="row <c:if test='${empty grups}'>hidden</c:if>">
				<div class="col-md-12">
					<not:inputSelect name="grupId" textKey="notificacio.form.camp.grup" optionItems="${grups}" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2" />
				</div>
			</div>
			<c:if test="${enviamentTipus == 'NOTIFICACIO'}">
				<!-- DATA ENVIAMENT PROGRAMADA -->
				<div class="row" id="rowDataProgramada">
					<div class="col-md-12">
						<not:inputDate name="enviamentDataProgramada" textKey="notificacio.form.camp.dataProgramada" info="true" messageInfo="notificacio.form.camp.dataProgramada.info" labelSize="2" inputSize="6" />
					</div>
				</div>

				<!-- RETARD -->
				<div class="row" id="rowRetard">
					<div class="col-md-12">
						<not:inputText name="retard" textKey="notificacio.form.camp.retard" info="true" messageInfo="notificacio.form.camp.retard.info" value="10" labelSize="2" inputSize="6"/>
					</div>
				</div>

				<!-- CADUCITAT -->
				<div class="row" id="rowCaducitat">
					<div class="col-md-12">
<%--						<not:inputDate name="caducitat" textKey="notificacio.form.camp.caducitat" info="true" messageInfo="notificacio.form.camp.caducitat.info" orientacio="bottom" labelSize="2" inputSize="6" required="true" />--%>
						<div class="form-group">
							<label class="control-label col-xs-2" for="enviamentDataProgramada"><spring:message code="notificacio.form.camp.caducitat"/></label>
							<div class="col-xs-6">
								<div class="col-xs-2" style="margin-right: 15px; margin-bottom: -15px;">
									<not:inputText name="caducitatDiesNaturals" inline="true"/>
								</div>
								<div class="col-xs-10" style="width: calc(83.33333333% - 15px); margin-bottom: -15px;">
									<not:inputDate name="caducitat" textKey="notificacio.form.camp.caducitat" info="true" messageInfo="notificacio.form.camp.caducitat.info" inline="true" orientacio="bottom" labelSize="2" inputSize="6" required="true" />
	<%--								<div class="input-group" style="width:100%">--%>
	<%--									<form:input path="caducitat" name="enviamentDataProgramada" class="form-control datepicker" data-idioma="ca" data-toggle="datepicker" data-custom="false" data-orientacio="auto" type="text" value="" data-datepicker-eval="true" />--%>
	<%--									<span class="input-group-addon" style="width:1%"><span class="fa fa-calendar"></span></span>--%>
	<%--								</div>--%>
	<%--								<p class="comentari col-xs-12 col-xs-offset-"><spring:message code="notificacio.form.camp.caducitat.info"/></p>--%>
								</div>
								<p class="comentari col-xs-12 col-xs-offset-"><spring:message code="notificacio.form.camp.dies.naturals"/></p>
								<p class="comentari col-xs-12 col-xs-offset-" style="padding-top:5px !important"><spring:message code="notificacio.form.camp.caducitat.info"/></p>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<!-- NÚMERO D'EXPEDIENT -->
			<div class="row">
				<div class="col-md-12">
					<not:inputText name="numExpedient" textKey="notificacio.form.camp.expedient" labelSize="2" inputSize="6" />
				</div>
			</div>

			<!-- IDIOMA -->
			<div class="row">
				<div class="col-md-6">
					<div class="form-group">
						<label class="control-label col-xs-4" for="idioma"><spring:message code="notificacio.form.camp.idioma" /></label>
						<div class="controls col-xs-8">
							<div class="col-xs-6">
								<form:radiobutton path="idioma" value="CA" checked="checked"/>
								<spring:message code="es.caib.notib.core.api.dto.idiomaEnumDto.CA" />
							</div>
							<div class="col-xs-6">
								<form:radiobutton path="idioma" value="ES" />
								<spring:message code="es.caib.notib.core.api.dto.idiomaEnumDto.ES" />
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- ENVIAMENT -->
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-vcard"></span>
				<label><spring:message code="notificacio.form.titol.enviaments" /></label>
				<hr/>
			</div>
			<c:set var="envios" value="${notificacioCommand.enviaments}"/>
			<div class="container-envios">
				<div class="newEnviament">
				<c:forEach items="${envios}" var="enviament" varStatus="status">
					<c:set var="j" value="${status.index}" scope="request"/>
					<c:set var="k" value="${status.index + 1}" />
						<div class="row enviamentsForm formEnviament enviamentForm_${j}">
							<div class="col-md-12">
								<label class="envio[${k}] badge badge-light">Enviament ${k}</label>
							</div>
							<cdiv>
							<input type="hidden" name="enviaments[${j}].id" value="${enviament.id}"/>
						
							<!-- TIPUS DE SERVEI -->
							<c:if test="${enviamentTipus != 'COMUNICACIO_SIR'}">
							<div class="col-md-6">
								<div class="form-group">
									<label class="control-label col-xs-4" for="enviaments[${j}].serveiTipus"><spring:message code="notificacio.form.camp.serveitipus" /></label>
									<div class="controls col-xs-8">
										<div class="col-xs-6">
											<form:radiobutton path="enviaments[${j}].serveiTipus" value="NORMAL" checked="checked"/>
											<spring:message code="es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto.NORMAL" />
										</div>
										<div class="col-xs-6">
											<form:radiobutton path="enviaments[${j}].serveiTipus" value="URGENT" />
											<spring:message code="es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto.URGENT" />
										</div>
									</div>
								</div>
							</div>
							</c:if>
							
							<!-- TITULAR -->
							<div class="titular">
								<div class="col-md-12 title-envios">
									<div class="title-container">
										<label id="labelTitular">${titular}</label>
									</div>
									<hr/>
								</div>
<%-- 								<input class="col-md-6 rowId hidden" type="input" id="rowId" value="${j}"/> --%>
								
								<div class="personaForm">
									<div class='rowId'><input class='hidden' value="${j}"/></div>
									<div style="clear: both">
										<div class="alert alert-warning alerta-sense-nif">
											<span class="fa fa-warning"></span>
											<c:choose>
												<c:when test="${enviamentTipus == 'COMUNICACIO'}"><spring:message code="notificacio.form.camp.titular.sense.nif.alerta.comunicacio" /></c:when>
												<c:when test="${enviamentTipus == 'NOTIFICACIO'}"><spring:message code="notificacio.form.camp.titular.sense.nif.alerta.notificacio" /></c:when>
											</c:choose>
											<button type="button" class="close-alertes pull-right" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
										</div>
										<input type="hidden" name="enviaments[${j}].titular.id" value="${enviament.titular.id}"/>
										<!--  TIPUS INTERESSAT -->
										<div class="col-md-6 interessatTipus<c:if test="${enviamentTipus == 'COMUNICACIO_SIR'}"> hidden</c:if>">
											<not:inputSelect name="enviaments[${j}].titular.interessatTipus" generalClass="interessat" textKey="notificacio.form.camp.interessatTipus" labelSize="4" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
										</div>

										<!-- TIPUS DOCUMENT -->
										<div class="col-md-6 docTipus hidden">
											<not:inputSelect name="enviaments[${j}].titular.documentTipus" textKey="notificacio.form.camp.titular.tipus.document" optionItems="${documentTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
										</div>

										<!-- NIF -->
										<div class="col-md-6 nif">
											<not:inputText name="enviaments[${j}].titular.nif" generalClass="titularNif" textKey="notificacio.form.camp.titular.nif"/>
										</div>
										
										<!-- NOM / RAÓ SOCIAL -->
										<div class="col-md-6 rao nomInput">
											<not:inputText name="enviaments[${j}].titular.nomInput" textKey="notificacio.form.camp.titular.nom.sol" required="true" inputMaxLength="${nomSize}" showsize="true"/>
										</div>
										<div class="col-md-6 rao raoSocialInput">
											<not:inputText name="enviaments[${j}].titular.raoSocialInput" textKey="notificacio.form.camp.titular.rao.social" required="true" inputMaxLength="${raoSocialSize}" showsize="true"/>
										</div>
										<!-- PRIMER LLINATGE -->
										<div class="col-md-6 llinatge1">
											<not:inputText name="enviaments[${j}].titular.llinatge1" textKey="notificacio.form.camp.titular.llinatge1" required="true" inputMaxLength="${llinatge1Size}" showsize="true"/>
										</div>
										
										<!-- SEGON LLINATGE -->
										<div class="col-md-6 llinatge2">
											<not:inputText name="enviaments[${j}].titular.llinatge2" textKey="notificacio.form.camp.titular.llinatge2" inputMaxLength="${llinatge2Size}" showsize="true"/>
										</div>
										
										<!-- EMAIL -->
										<c:if test="${enviamentTipus != 'COMUNICACIO_SIR'}">
										<div class="col-md-6 email">
											<not:inputText name="enviaments[${j}].titular.email" textKey="notificacio.form.camp.titular.email" inputMaxLength="${emailSize}" showsize="true"/>
										</div>
										</c:if>
										
										<!-- TELÈFON -->
<%--										<div class="col-md-6">--%>
<%--											<not:inputText name="enviaments[${j}].titular.telefon" textKey="notificacio.form.camp.titular.telefon" inputMaxLength="${telefonSize}" showsize="true"/>--%>
<%--										</div>--%>
										
										<!-- CODI DIR3 -->
										<div class="col-md-6 dir3Codi hidden">
											<not:inputTextSearch  funcio="obrirModalOrganismes('Tit-${j}', '${urlOrganigrama}', '${urlComunitatsAutonomes}','${urlNivellAdministracions}', '${urlCercaUnitats}')" searchButton="searchOrganTit${j}" textKey="notificacio.form.camp.titular.dir3codi" required="true" readonly="true" value=""/>
<%-- 											value="${fn:join(enviaments[j].titular.dir3Codi, enviaments[j].titular.nom)} "/> --%>
										</div>
										
										<div class="col-md-6 codiDir3 hidden">
<%-- 											<not:inputTextSearch  funcio="obrirModalOrganismes(${j}, '${urlOrganigrama}', '${urlComunitatsAutonomes}','${urlNivellAdministracions}','${urlCercaUnitats}')" name="enviaments[${j}].titular.dir3Codi" searchButton="searchOrgan" textKey="notificacio.form.camp.titular.dir3codi" required="true"/> --%>
											<not:inputText name="enviaments[${j}].titular.dir3Codi" textKey="notificacio.form.camp.titular.dir3codi" required="true"/>
										</div>
										
										<!-- INCAPACITAT -->
										<c:if test="${isTitularAmbIncapacitat}">
											<div class="col-md-12 incapacitat">
												<not:inputCheckbox name="enviaments[${j}].titular.incapacitat" textKey="notificacio.form.camp.titular.incapacitat" funcio="mostrarDestinatari(this.id, ${isMultiplesDestinataris})"/>
											</div>
										</c:if>
									</div>
								</div>
							</div>
							
							<!-- DESTINATARIS -->
							<c:if test="${enviamentTipus != 'COMUNICACIO_SIR'}">
								<c:set var="enviament" value="${enviament}" scope="request" />
								<c:import url="includes/destinatariForm.jsp"/>
								<div class="col-md-12 separacio"></div>
							</c:if>
							
							<div class="metodeEntrega">
								<div class="col-md-12 title-envios">
									<div class="title-container entrega">
										<label> ${metodeEntrega} </label>
									</div>
									<hr/>
								</div>
								<div class="col-md-12 avis-metodo-envio entrega-inactiva">
									<p class="comentari"><span class="fa fa-info-circle"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega.info.cap"/></span></p>
								</div>
								<div class="col-md-12">
									<div class="entregaPostal_info_${j} entregaPostalInfo alert alert-info" role="alert">
										<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
									  	<strong><spring:message code="notificacio.form.camp.logitud.info"/></strong>
									</div>
									<div class="entrega-activa" style="margin-bottom:15px;">
										<p class="comentari"><spring:message code="notificacio.form.titol.enviaments.metodeEntrega.info"/></p>
									</div>
									<div class="entrega-cie-activa">
										<span id="entregaPostalCaducada">* <spring:message code="notificacio.form.camp.entregapostal.caducada"/></span>
										<not:inputCheckbox name="enviaments[${j}].entregaPostal.activa" textKey="notificacio.form.camp.entregapostal.activa" labelSize="4" funcio="mostrarEntregaPostal(this.id)" />

									</div>
								</div>
								<!-- ENTREGA POSTAL -->
								<div id="entregaPostal" class="entregaPostal_${j}" <c:if test="${!enviament.entregaPostal.activa}">style="display:none"</c:if>>
									<div class="col-md-12">
										<div class="col-md-12">
											<not:inputSelect name="enviaments[${j}].entregaPostal.domiciliConcretTipus" generalClass="enviamentTipus" textKey="notificacio.form.camp.entregapostal.tipus" required="true"
															 optionItems="${entregaPostalTipus}" optionValueAttribute="value" optionTextKeyAttribute="text"  labelClass="labelcss" inputClass="inputcss"/>
										</div>	
										<div class="normalitzat">
											<div class="col-md-4">
												<not:inputSelect name="enviaments[${j}].entregaPostal.viaTipus" generalClass="tipusVia" textKey="notificacio.form.camp.entregapostal.tipusvia" labelClass="labelcss" inputClass="inputcss" required="true" />
											</div>
											<div class="col-md-8">
												<not:inputText name="enviaments[${j}].entregaPostal.viaNom" textKey="notificacio.form.camp.entregapostal.vianom" labelClass="labelcss" inputClass="inputcss" required="true" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.apartatCorreus" textKey="notificacio.form.camp.entregapostal.apartatcorreus" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.numeroCasa"
															   textKey="notificacio.form.camp.entregapostal.numerocasa"
															   labelClass="labelcss" inputClass="inputcss"
															   required="true"/>
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.puntKm"
															   textKey="notificacio.form.camp.entregapostal.puntkm"
															   labelClass="labelcss" inputClass="inputcss"
															   required="true"/>
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.portal" textKey="notificacio.form.camp.entregapostal.portal" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.escala" textKey="notificacio.form.camp.entregapostal.escala" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.planta" textKey="notificacio.form.camp.entregapostal.planta" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.porta" textKey="notificacio.form.camp.entregapostal.porta" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.bloc" textKey="notificacio.form.camp.entregapostal.bloc" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaPostal.codiPostal"
															   textKey="notificacio.form.camp.entregapostal.codipostal"
															   labelClass="labelcss" inputClass="inputcss"
															   required="true"/>
											</div>
											<div class="col-md-6">
												<not:inputSelect name="enviaments[${j}].entregaPostal.paisCodi" generalClass="paisos" emptyOption="true" textKey="notificacio.form.camp.entregapostal.paiscodi" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<div class="col-md-6">
												<not:inputSelect name="enviaments[${j}].entregaPostal.provincia"
																 generalClass="provincies" emptyOption="true"
																 textKey="notificacio.form.camp.entregapostal.provincia"
																 labelClass="labelcss" inputClass="inputcss"
																 required="true"/>
											</div>
											<div class="col-md-6 poblacioSelect">
												<not:inputSelect name="enviaments[${j}].entregaPostal.municipiCodi"
																 generalClass="localitats" emptyOption="true"
																 textKey="notificacio.form.camp.entregapostal.municipi"
																 labelClass="labelcss" inputClass="inputcss"
																 required="true"/>
											</div>
											<script>
												actualitzarEntrega('${j}', '${urlPaisos}', '${urlProvincies}', '${urlLocalitats}'); // TODO: Averiguar si això està aquí per algún motiu
											</script>
											<div class="col-md-6">
												<not:inputText name="enviaments[${j}].entregaPostal.poblacio" textKey="notificacio.form.camp.entregapostal.poblacio" labelClass="labelcss" inputClass="inputcss"/>
											</div>
											<div class="col-md-12">
												<not:inputText name="enviaments[${j}].entregaPostal.complement" textKey="notificacio.form.camp.entregapostal.complement" labelClass="labelcss" inputClass="inputcss" />
											</div>
											<div class="col-md-3 formatFulla">
											<c:choose>
												<c:when test="${not empty formatsFulla}">
													<not:inputSelect name="enviaments[${j}].entregaPostal.formatFulla" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatfulla" optionItems="${formatsFulla}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
												</c:when>
												<c:otherwise>
													<not:inputText name="enviaments[${j}].entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" labelClass="labelcss" inputClass="inputcss"/>
												</c:otherwise>
											</c:choose>
											</div>
											<div class="col-md-3 formatSobre">
											<c:choose>
												<c:when test="${not empty formatsSobre}">
													<not:inputSelect name="enviaments[${j}].entregaPostal.formatSobre" emptyOption="true" textKey="notificacio.form.camp.entregapostal.formatsobre" optionItems="${formatsSobre}" optionValueAttribute="codi" optionTextAttribute="codi" labelClass="labelcss" inputClass="inputcss"/>
												</c:when>
												<c:otherwise>
													<not:inputText name="enviaments[${j}].entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" labelClass="labelcss" inputClass="inputcss"/>
												</c:otherwise>
											</c:choose>	
											</div>
										</div>	
										<div class="senseNormalitzar hidden">
											<div class="col-md-6">
												<not:inputTextarea name="enviaments[${j}].entregaPostal.linea1" textKey="notificacio.form.camp.entregapostal.linea1" required="true"/>
											</div>
											<div class="col-md-6">
												<not:inputTextarea name="enviaments[${j}].entregaPostal.linea2" textKey="notificacio.form.camp.entregapostal.linea2" required="true"/>
											</div>
											<div class="col-md-6">
												<not:inputText name="enviaments[${j}].entregaPostal.codiPostalNorm" textKey="notificacio.form.camp.entregapostal.codipostal" required="true"/>
											</div>
											<p class="comentari col-xs-12 col-xs-offset-"><spring:message code="notificacio.form.camp.entregapostal.linea.info"/></p>
										</div>
									</div>
								</div>
								<input class="enviaments[${j}].entregaPostal.paisCodi hidden" value="${enviament.entregaPostal.paisCodi}"/>
								<input class="enviaments[${j}].entregaPostal.provincia hidden" value="${enviament.entregaPostal.provincia}"/>
								<input class="enviaments[${j}].entregaPostal.municipiCodi hidden" value="${enviament.entregaPostal.municipiCodi}"/>
								<c:if test="${ambEntregaDeh}">
									<c:set var="entregaDehActiva" value="${enviament.entregaDeh.activa}"></c:set>
									<div class="col-md-12">
										<not:inputCheckbox name="enviaments[${j}].entregaDeh.activa" textKey="notificacio.form.camp.entregadeh.activa" labelSize="4" funcio="mostrarEntregaDeh(this.id)" />
									</div>
									<!-- ENTREGA DEH -->
									<div id="entregaDeh" class="entregaDeh_${j}" <c:if test="${!entregaDehActiva}">style="display:none"</c:if>>
										<div class="col-md-12">
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaDeh.emisorNif" generalClass="nifemisor" textKey="notificacio.form.camp.entregadeh.emisorNif" labelClass="labelcss" inputClass="inputcss" readonly="true"/>
											</div>
											<div class="col-md-12">
												<not:inputCheckbox name="enviaments[${j}].entregaDeh.obligat" textKey="notificacio.form.camp.entregadeh.obligat" labelSize="2" />
											</div>
											<div class="col-md-4">
												<not:inputText name="enviaments[${j}].entregaDeh.procedimentCodi" generalClass="procedimentcodi" value="${procediment.codi}" textKey="notificacio.form.camp.entregadeh.codiprocediment" labelClass="labelcss" inputClass="inputcss" readonly="true"/>
											</div>
										</div>
									</div>
								</c:if>
								<div class="col-md-12 text-right">
									<div class="btn-group">
										<input type="button" class="btn btn-default formEnviament eliminar_enviament hidden" name="enviamentDelete[${j}]" onclick="enviamentDelete(this.id)" id="enviamentDelete_${j}" value="<spring:message code="notificacio.form.boto.eliminar.enviament"/>" />
									</div>
								</div>
							</div>
						</div>
						</div>
				</c:forEach>
				</div>
				<c:if test="${enviamentTipus != 'COMUNICACIO_SIR'}">
					<div class="text-left vt10">
						<div class="btn-group">
							<input type="button" class="btn btn-default" id="addEnviament"
								   onclick="addEnvio('${urlOrganigrama}', '${urlComunitatsAutonomes}', '${urlNivellAdministracions}', '${urlCercaUnitats}', '${urlPaisos}', '${urlProvincies}', '${urlLocalitats}', personaMaxSizes)"
								   value="<spring:message code="notificacio.form.boto.nou.enviament"/>" />
						</div>
					</div>
				</c:if>
			</div>

			<!-- DOCUMENT -->
			<div class="container-fluid">
				<div class="title">
					<span class="fa fa-file"></span>
					<label><spring:message code="notificacio.form.titol.document" /></label>
					<hr/>
				</div>
				
				<!-- TIPUS DE DOCUMENT -->
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
							<form:hidden path="tipusDocumentSelected[0]" id="tipusDocumentSelected_0" value="${tipusDocument[0]}"/>
							<div class="controls col-xs-8">
								<form:hidden path="tipusDocumentDefault[0]"/>
								<select id="tipusDocument_0" name="tipusDocument[0]" class="customSelect tipusDocument">
								<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
									<option value="${enumValue}" <c:if test="${not empty tipusDocument[0] && tipusDocument[0] == enumValue}">selected</c:if>><spring:message code="tipus.document.enum.${enumValue}"/></option>
								</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<input type="hidden" name="documents[0].id" value="${notificacioCommand.documents[0].id}">
					<input type="hidden" name="documents[0].arxiuGestdocId" value="${notificacioCommand.documents[0].arxiuGestdocId}">
					<input type="hidden" name="documents[0].arxiuNom" value="${notificacioCommand.documents[0].arxiuNom}">
					<input type="hidden" name="documents[0].mediaType" value="${notificacioCommand.documents[0].mediaType}">
					<input type="hidden" name="documents[0].mida" value="${notificacioCommand.documents[0].mida}">
					<!-- CSV -->
					<div id="input-origen-csv_0" class="col-md-6">
						<not:inputText name="documentArxiuCsv[0]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3" info="true" messageInfo="notificacio.for.camp.document.avis" />
					</div>
					
					<!-- UUID -->
					<div id="input-origen-uuid_0" class="col-md-6 hidden">
						<not:inputText name="documentArxiuUuid[0]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
					</div>
					
					<!-- URL -->
					<div id="input-origen-url_0" class="col-md-6 hidden">
						<not:inputText name="documentArxiuUrl[0]" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
					</div>
					
					<!-- FITXER -->
					<div id="input-origen-arxiu_0" class="col-md-6 hidden">
						<c:choose>
							<c:when test="${notificacioCommand.tipusDocumentDefault == 'ARXIU'}">
								<not:inputFile name="arxiu[0]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="test"/>
							</c:when>
							<c:otherwise>
								<not:inputFile name="arxiu[0]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_0}"/>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div id="metadades_0" class="row doc-metadades hidden">
					<div class="col-md-4 col-md-offset-2">
						<not:inputSelect name="documents[0].origen" textKey="notificacio.form.camp.origen" labelSize="4" optionItems="${origens}" optionValueAttribute="value" optionTextKeyAttribute="text" />
					</div>
					<div class="col-md-4 col-md-offset-2">
						<not:inputSelect name="documents[0].validesa" textKey="notificacio.form.camp.validesa" labelSize="4" optionItems="${valideses}" optionValueAttribute="value" optionTextKeyAttribute="text" />
					</div>
					<div class="col-md-4 col-md-offset-2">
						<not:inputSelect name="documents[0].tipoDocumental" textKey="notificacio.form.camp.tipoDocumental" labelSize="4"
										 optionItems="${tipusDocumentals}" optionMinimumResultsForSearch="2"
										 optionValueAttribute="value" optionTextKeyAttribute="text" />
					</div>
					<div class="col-md-4 col-md-offset-2">
						<not:inputCheckbox name="documents[0].modoFirma" textKey="notificacio.form.camp.modoFirma"  labelSize="4" />
					</div>
					<hr/>
				</div>
	
				<div id="docs-addicionals"<c:if test="${enviamentTipus != 'COMUNICACIO_SIR'}"> class="hidden"</c:if>>
					<!-- DOCUMENT 2 -->
					<div id="document2" class="row hidden">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
								<form:hidden path="tipusDocumentSelected[1]" id="tipusDocumentSelected_1" value="${tipusDocument[1]}"/>
								<div class="controls col-xs-8">
									<form:hidden path="tipusDocumentDefault[1]"/>
									<select id="tipusDocument_1" name="tipusDocument[1]" class="customSelect tipusDocument">
										<option value=""></option>
										<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
											<option value="${enumValue}" <c:if test="${not empty tipusDocument[1] && tipusDocument[1] == enumValue}">selected</c:if>><spring:message code="tipus.document.enum.${enumValue}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
						<input type="hidden" name="documents[1].id" value="${notificacioCommand.documents[1].id}">
						<input type="hidden" name="documents[1].arxiuGestdocId" value="${notificacioCommand.documents[1].arxiuGestdocId}">
						<input type="hidden" name="documents[1].arxiuNom" value="${notificacioCommand.documents[1].arxiuNom}">
						<input type="hidden" name="documents[1].mediaType" value="${notificacioCommand.documents[1].mediaType}">
						<input type="hidden" name="documents[1].mida" value="${notificacioCommand.documents[1].mida}">
						<!-- CSV -->
						<div id="input-origen-csv_1" class="col-md-6">
							<not:inputText name="documentArxiuCsv[1]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3" info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- UUID -->
						<div id="input-origen-uuid_1" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUuid[1]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- URL -->
						<div id="input-origen-url_1" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUrl[1]" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- FITXER -->
						<div id="input-origen-arxiu_1" class="col-md-6 hidden">
							<c:choose>
								<c:when test="${notificacioCommand.tipusDocumentDefault == 'ARXIU'}">
									<not:inputFile name="arxiu[1]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_1}"/>
								</c:when>
								<c:otherwise>
									<not:inputFile name="arxiu[1]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_1}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div id="metadades_1" class="row doc-metadades hidden">
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[1].origen" textKey="notificacio.form.camp.origen" labelSize="4" optionItems="${origens}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[1].validesa" textKey="notificacio.form.camp.validesa" labelSize="4" optionItems="${valideses}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[1].tipoDocumental" textKey="notificacio.form.camp.tipoDocumental" labelSize="4" optionItems="${tipusDocumentals}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputCheckbox name="documents[1].modoFirma" textKey="notificacio.form.camp.modoFirma"  labelSize="4" />
						</div>
						<hr/>
					</div>
					<!-- DOCUMENT 3 -->
					<div id="document3" class="row hidden">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
								<form:hidden path="tipusDocumentSelected[2]" id="tipusDocumentSelected_2" value="${tipusDocument[2]}"/>
								<div class="controls col-xs-8">
									<form:hidden path="tipusDocumentDefault[2]"/>
									<select id="tipusDocument_2" name="tipusDocument[2]" class="customSelect tipusDocument">
										<option value=""></option>
										<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
											<option value="${enumValue}" <c:if test="${not empty tipusDocument[2] && tipusDocument[2] == enumValue}">selected</c:if>><spring:message code="tipus.document.enum.${enumValue}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
						<input type="hidden" name="documents[2].id" value="${notificacioCommand.documents[2].id}">
						<input type="hidden" name="documents[2].arxiuGestdocId" value="${notificacioCommand.documents[2].arxiuGestdocId}">
						<input type="hidden" name="documents[2].arxiuNom" value="${notificacioCommand.documents[2].arxiuNom}">
						<input type="hidden" name="documents[2].mediaType" value="${notificacioCommand.documents[2].mediaType}">
						<input type="hidden" name="documents[2].mida" value="${notificacioCommand.documents[2].mida}">
						<!-- CSV -->
						<div id="input-origen-csv_2" class="col-md-6">
							<not:inputText name="documentArxiuCsv[2]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3" info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- UUID -->
						<div id="input-origen-uuid_2" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUuid[2]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- URL -->
						<div id="input-origen-url_2" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUrl[2]" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- FITXER -->
						<div id="input-origen-arxiu_2" class="col-md-6 hidden">
							<c:choose>
								<c:when test="${notificacioCommand.tipusDocumentDefault == 'ARXIU'}">
									<not:inputFile name="arxiu[2]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_2}"/>
								</c:when>
								<c:otherwise>
									<not:inputFile name="arxiu[2]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_2}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div id="metadades_2" class="row doc-metadades hidden">
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[2].origen" textKey="notificacio.form.camp.origen" labelSize="4" optionItems="${origens}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[2].validesa" textKey="notificacio.form.camp.validesa" labelSize="4" optionItems="${valideses}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[2].tipoDocumental" textKey="notificacio.form.camp.tipoDocumental" labelSize="4"
											 optionItems="${tipusDocumentals}" optionMinimumResultsForSearch="2"
											 optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputCheckbox name="documents[2].modoFirma" textKey="notificacio.form.camp.modoFirma"  labelSize="4" />
						</div>
						<hr/>
					</div>
					<!-- DOCUMENT 4 -->
					<div id="document4" class="row hidden">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
								<form:hidden path="tipusDocumentSelected[3]" id="tipusDocumentSelected_3" value="${tipusDocument[3]}"/>
								<div class="controls col-xs-8">
									<form:hidden path="tipusDocumentDefault[3]"/>
									<select id="tipusDocument_3" name="tipusDocument[3]" class="customSelect tipusDocument">
										<option value=""></option>
										<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
											<option value="${enumValue}" <c:if test="${not empty tipusDocument[3] && tipusDocument[3] == enumValue}">selected</c:if>><spring:message code="tipus.document.enum.${enumValue}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
						<input type="hidden" name="documents[3].id" value="${notificacioCommand.documents[3].id}">
						<input type="hidden" name="documents[3].arxiuGestdocId" value="${notificacioCommand.documents[3].arxiuGestdocId}">
						<input type="hidden" name="documents[3].arxiuNom" value="${notificacioCommand.documents[3].arxiuNom}">
						<input type="hidden" name="documents[3].mediaType" value="${notificacioCommand.documents[3].mediaType}">
						<input type="hidden" name="documents[3].mida" value="${notificacioCommand.documents[3].mida}">
						<!-- CSV -->
						<div id="input-origen-csv_3" class="col-md-6">
							<not:inputText name="documentArxiuCsv[3]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3" info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- UUID -->
						<div id="input-origen-uuid_3" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUuid[3]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- URL -->
						<div id="input-origen-url_3" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUrl[3]" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- FITXER -->
						<div id="input-origen-arxiu_3" class="col-md-6 hidden">
							<c:choose>
								<c:when test="${notificacioCommand.tipusDocumentDefault == 'ARXIU'}">
									<not:inputFile name="arxiu[3]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_3}"/>
								</c:when>
								<c:otherwise>
									<not:inputFile name="arxiu[3]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_3}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div id="metadades_3" class="row doc-metadades hidden">
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[3].origen" textKey="notificacio.form.camp.origen" labelSize="4" optionItems="${origens}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[3].validesa" textKey="notificacio.form.camp.validesa" labelSize="4" optionItems="${valideses}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[3].tipoDocumental" textKey="notificacio.form.camp.tipoDocumental" labelSize="4" optionItems="${tipusDocumentals}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputCheckbox name="documents[3].modoFirma" textKey="notificacio.form.camp.modoFirma"  labelSize="4" />
						</div>
						<hr/>
					</div>
					<!-- DOCUMENT 5 -->
					<div id="document5" class="row hidden">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-xs-4"><spring:message code="entitat.form.camp.conf.tipusdoc"/></label>
								<form:hidden path="tipusDocumentSelected[4]" id="tipusDocumentSelected_4" value="${tipusDocument[4]}"/>
								<div class="controls col-xs-8">
									<form:hidden path="tipusDocumentDefault[4]"/>
									<select id="tipusDocument_4" name="tipusDocument[4]" class="customSelect tipusDocument">
										<option value=""></option>
										<c:forEach items="${tipusDocumentEnumDto}" var="enumValue">
											<option value="${enumValue}" <c:if test="${not empty tipusDocument[4] && tipusDocument[4] == enumValue}">selected</c:if>><spring:message code="tipus.document.enum.${enumValue}"/></option>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
						<input type="hidden" name="documents[4].id" value="${notificacioCommand.documents[4].id}">
						<input type="hidden" name="documents[4].arxiuGestdocId" value="${notificacioCommand.documents[4].arxiuGestdocId}">
						<input type="hidden" name="documents[4].arxiuNom" value="${notificacioCommand.documents[4].arxiuNom}">
						<input type="hidden" name="documents[4].mediaType" value="${notificacioCommand.documents[4].mediaType}">
						<input type="hidden" name="documents[4].mida" value="${notificacioCommand.documents[4].mida}">
						<!-- CSV -->
						<div id="input-origen-csv_4" class="col-md-6">
							<not:inputText name="documentArxiuCsv[4]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3" info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- UUID -->
						<div id="input-origen-uuid_4" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUuid[4]" generalClass="docArxiu" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- URL -->
						<div id="input-origen-url_4" class="col-md-6 hidden">
							<not:inputText name="documentArxiuUrl[4]" textKey="notificacio.form.camp.csvuuid" labelSize="3"  info="true" messageInfo="notificacio.for.camp.document.avis" />
						</div>
	
						<!-- FITXER -->
						<div id="input-origen-arxiu_4" class="col-md-6 hidden">
							<c:choose>
								<c:when test="${notificacioCommand.tipusDocumentDefault == 'ARXIU'}">
									<not:inputFile name="arxiu[4]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_4}"/>
								</c:when>
								<c:otherwise>
									<not:inputFile name="arxiu[4]" textKey="notificacio.form.camp.arxiu" labelSize="3"  info="true" messageInfo="${documentAvisKey}" fileName="${nomDocument_4}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div id="metadades_4" class="row doc-metadades hidden">
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[4].origen" textKey="notificacio.form.camp.origen" labelSize="4" optionItems="${origens}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[4].validesa" textKey="notificacio.form.camp.validesa" labelSize="4" optionItems="${valideses}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputSelect name="documents[4].tipoDocumental" textKey="notificacio.form.camp.tipoDocumental" labelSize="4" optionItems="${tipusDocumentals}" optionValueAttribute="value" optionTextKeyAttribute="text" />
						</div>
						<div class="col-md-4 col-md-offset-2">
							<not:inputCheckbox name="documents[4].modoFirma" textKey="notificacio.form.camp.modoFirma"  labelSize="4" />
						</div>
						<hr/>
					</div>
				</div>

				<c:if test="${enviamentTipus == 'COMUNICACIO_SIR'}">
				<div id="btn-documents" class="text-left vt10">
					<input type="button" class="btn btn-default" id="addDocument" value="<spring:message code="notificacio.form.boto.nou.document"/>" />
					<input type="button" class="btn btn-danger hidden" id="removeDocument" value="<spring:message code="notificacio.form.boto.remove.document"/>" />
				</div>
				</c:if>
	
				<!--  DOCUMENT NOTMALITZAT -->
				<c:if test="${enviamentTipus != 'COMUNICACIO_SIR'}">
				<div id="normalitzat" class="row">
					<div class="col-md-12">
						<not:inputCheckbox name="documents[0].normalitzat" textKey="notificacio.form.camp.normalitzat" info="true" messageInfo="notificacio.form.camp.normalitzat.info" labelSize="2" />
					</div>
				</div>
				</c:if>
			</div>

			<div class="col-md-12">
				<hr>
			</div>
			<div class="text-right col-md-12">
				<div class="btn-group">
					<c:choose>
						<c:when test="${ empty referer }">
							<a href="<c:url value="/notificacio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
						</c:when>
						<c:otherwise>
							<a href="${ referer }" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
						</c:otherwise>
					</c:choose>

				</div>
				<div class="btn-group">
					<button id="saveForm" type="button" class="btn btn-success saveForm">
						<span class="fa fa-paper-plane"></span>
						<spring:message code="comu.boto.enviar.notificacio" />
					</button>
				</div>
			</div>
		</div>
	</form:form>



	<div class="modal fade" id="organismesModal" role="dialog">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><spring:message code="notificacio.form.dir3.cercar.organismes"/></h4>
				</div>
				<div class="modal-body body" style="padding-top: 0px;">
					
				<div id='dialeg_organs' style='padding: 0px;'>
					<input type="hidden" id="titular" value="">
					<input type="hidden" id="organigrama" value="">
					 

					 
					<div class="row margebaix" style="margin-top:20px;">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.codi" /></label>
								<div class="forminput">
									<input type="text" id="o_codi" class="form-control">
								</div>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.denominacio" /></label>
								<div class="forminput">
									<input type="text" id="o_denominacio" class="form-control">
								</div>
							</div>
						</div>
					
					</div>
					<div class="row margebaix">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.nivell.administracio" /></label>
								<div class="forminput">
									<select id="o_nivellAdmin" class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
<!-- 						<div class="col-sm-1" style="width: 1%;  margin-top:33px; margin-left:-25px"> -->
<!-- 							<span onclick="limpiarNivellAdmin()" class="fa fa-trash"></span> -->
<!-- 						</div> -->
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.comunitat.autonoma" /></label>
								<div class="forminput">
									<select id="o_comunitat" type="search" onchange="comunitatAutonomaChange(this.value, '${urlProvincies}')" class="form-control">
										<option value=""></option>
				    				</select>
				    				
								</div>
							</div>
						</div>
<!-- 						<div class="col-sm-1" style="width: 1%;  margin-top:33px; margin-left:-25px"> -->
<!-- 							<span onclick="limpiarComunitat()" class="fa fa-trash"></span> -->
<!-- 						</div> -->
					</div>
					<div class="row margebaix">
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.provincia" /></label>
								<div class="forminput">
									<select id="o_provincia" onchange="provinciesChange(this.value, '${urlLocalitats}')"class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
<!-- 						<div class="col-sm-1" style="width: 1%;  margin-top:33px; margin-left:-25px"> -->
<!-- 							<span onclick="limpiarProvincia(false)" class="fa fa-trash"></span> -->
<!-- 						</div> -->
						<div class="col-sm-6">
							<div class="form-group">
								<label class="formlabel"><spring:message code="notificacio.form.dir3.cercar.localitat" /></label>
								<div class="forminput">
									<select id="o_localitat" class="form-control">
										<option value=""></option>
				    				</select>
								</div>
							</div>
						</div>
<!-- 						<div class="col-sm-1" style="width: 1%;  margin-top:33px; margin-left:-25px"> -->
<!-- 							<span onclick="limpiarLocalitat(false)" class="fa fa-trash"></span> -->
<!-- 						</div> -->
					</div>
				
					<div id="results" class="row" style="background-color: white; height: 240px; border: 1px solid #CCC; margin: 0px; overflow-y: scroll" >
						<div class="loading-screen" style="text-align: center; width:100%; height: 0%;;">
								<div class="processing-icon" style="position: relative; top: 40px; text-align: center;">
									<span class="fa fa-spin fa-circle-o-notch  fa-3x" style="color: burlywood;margin-top: 10px;"></span>
								</div>
							</div>
						<table id="tOficines" class="table table-bordered dataTable dinamicTable" style="margin-top:0px !important">
							<thead>
								<tr class="capsalera" style="font-weight: bold;" >
									<td width="85%"><spring:message code="notificacio.form.dir3.cercar.titol" /></td>
									<td><spring:message code="notificacio.form.dir3.cercar.sir" /></td>
									<td></td>
								</tr>
							</thead>
							<tbody id="rOrgans">
							</tbody>
						
						</table>
					</div>
					<div id="resultatsTotal"  class="hidden subtitle">
						<label><spring:message code="comu.resultats" /></label><label id="total"></label>
						
					</div>
				</div>
			</div>
				
				<div class="modal-footer">
					<button id="btnNetejar" onclick="netejar(true, '${urlCercaUnitats}')" type="submit" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="cerrarModal" type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.cancelar" /></button>
					<button id="loadOrgansGestors" onclick="loadOrgansGestors('${urlCercaUnitats}')" name="accio" value="filtrar" type="button" class="btn btn-info"> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</div>
</body> 
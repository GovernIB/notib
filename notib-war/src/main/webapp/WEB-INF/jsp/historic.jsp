<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ page import="es.caib.notib.core.api.dto.HistoricTipusEnumDto" %> --%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="decorator.menu.accions.estadistiques"/></title>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/chartjs/2.9.3/Chart.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
		// COMMON FUNCTIONS
        function getRandomColor() {
            var letters = '0123456789ABCDEF'.split('');
            var color = '#';
            for (var i = 0; i < 6; i++ ) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        }

		var metricsDefinition = {
			'NOTIFICACIONS_TOTAL': {
				'attrname' : 'numNotTotal',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_TOTAL"/>"
			},
			'NOTIFICACIONS_CORRECTES': {
				'attrname' : 'numNotCorrectes',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_CORRECTES"/>"
			},
			'NOTIFICACIONS_AMB_ERROR': {
				'attrname' : 'numNotAmbError',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_AMB_ERROR"/>"
			},
			'NOTIFICACIONS_PROCEDIMENT_COMU': {
				'attrname' : 'numNotProcedimentComu',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_PROCEDIMENT_COMU"/>"
			},
			'NOTIFICACIONS_AMB_GRUP': {
				'attrname' : 'numNotAmbGrup',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_AMB_GRUP"/>"
			},
			'NOTIFICACIONS_ORIGEN_API': {
				'attrname' : 'numNotOrigenApi',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_ORIGEN_API"/>"
			},
			'NOTIFICACIONS_ORIGEN_WEB': {
				'attrname' : 'numNotOrigenWeb',
				'text': "<spring:message code="historic.metriques.enum.NOTIFICACIONS_ORIGEN_WEB"/>"
			},
			'COMUNICACIONS_TOTAL': {
				'attrname' : 'numComTotal',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_TOTAL"/>"
			},
			'COMUNICACIONS_CORRECTES': {
				'attrname' : 'numComCorrectes',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_CORRECTES"/>"
			},
			'COMUNICACIONS_AMB_ERROR': {
				'attrname' : 'numComAmbError',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_AMB_ERROR"/>"
			},
			'COMUNICACIONS_PROCEDIMENT_COMU': {
				'attrname' : 'numComProcedimentComu',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_PROCEDIMENT_COMU"/>"
			},
			'COMUNICACIONS_AMB_GRUP': {
				'attrname' : 'numComAmbGrup',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_AMB_GRUP"/>"
			},
			'COMUNICACIONS_ORIGEN_API': {
				'attrname' : 'numComOrigenApi',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_ORIGEN_API"/>"
			},
			'COMUNICACIONS_ORIGEN_WEB': {
				'attrname' : 'numComOrigenWeb',
				'text': "<spring:message code="historic.metriques.enum.COMUNICACIONS_ORIGEN_WEB"/>"
			},
			'ENVIAMENTS': {
				'attrname' : 'numEnviaments',
				'text': "<spring:message code="historic.metriques.enum.ENVIAMENTS"/>"
			},
			'PROCEDIMENTS': {
				'attrname' : 'numProcediments',
				'text': "<spring:message code="historic.metriques.enum.PROCEDIMENTS"/>"
			},
			'GRUPS': {
				'attrname' : 'numGrups',
				'text': "<spring:message code="historic.metriques.enum.GRUPS"/>"
			},
			'PERMISOS_CONSULTA': {
				'attrname' : 'numPermConsulta',
				'text': "<spring:message code="historic.metriques.enum.PERMISOS_CONSULTA"/>"
			},
			'PERMISOS_NOTIFICACIO': {
				'attrname' : 'numPermNotificacio',
				'text': "<spring:message code="historic.metriques.enum.PERMISOS_NOTIFICACIO"/>"
			},
			'PERMISOS_GESTIO': {
				'attrname' : 'numPermGestio',
				'text': "<spring:message code="historic.metriques.enum.PERMISOS_GESTIO"/>"
			},
			'PERMISOS_PROCESSAR': {
				'attrname' : 'numPermProcessar',
				'text': "<spring:message code="historic.metriques.enum.PERMISOS_PROCESSAR"/>"
			},
			'PERMISOS_ADMINISTRAR': {
				'attrname' : 'numPermAdministrar',
				'text': "<spring:message code="historic.metriques.enum.PERMISOS_ADMINISTRAR"/>"
			}
		}
		
		var showingTables = ${ historicFiltreCommand.showingTables };
		var showDadesOrganGestor = ${showDadesOrganGestor};
		var showDadesProcediment = ${showDadesProcediment};
		var showDadesEstat = ${showDadesEstat};
		var showDadesGrup = ${showDadesGrup};
		var showDadesUsuari = ${showDadesUsuari};
		
		var showingDadesActuals = ${ showingDadesActuals };
		
		<c:if test="${empty historicFiltreCommand.organGestorsCodis}">
			var isAnyOrganSelected = false; 
		</c:if>;
		<c:if test="${not empty historicFiltreCommand.organGestorsCodis}">
			var isAnyOrganSelected = true; 
		</c:if>;
	
		var language = '${requestLocale}';
		// Només acceptam es i ca com a llengues //
		if (language.startsWith("es")) {
			language = "es";
		} else {
			language = "ca";
		}
		
		var usuarisSeleccionats = [];
		<c:forEach items="${ usuarisSeleccionats }" var="codiUsuari">
			usuarisSeleccionats.push("${ codiUsuari }");
	    </c:forEach>
		console.log(usuarisSeleccionats);
		
		function dataTableHistoric (selector) {
			return $(selector).DataTable({
				language: {
					url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
				},
				paging: true,
				pageLength: 10,
				order: [[ 0, "desc" ]],
				pagingStyle: 'page',
				lengthMenu: [10, 20, 50, 100, 250],
				dom: '<"row"<"col-md-6"i><"col-md-6"<"botons">>>' + 't<"row"<"col-md-3"l><"col-md-9"p>>',
				select: {
					style: 'multi',
					selector: 'td:first-child',
					info: false
				}
			});
		}
		
		function chartPie(canvas, data, labels, backgroundColors, title) {
			return new Chart(canvas, {
			    type: 'doughnut',
			    data: {
			        datasets: [{
			            data: data,
			            backgroundColor: backgroundColors
			        }],

			        // These labels appear in the legend and in the tooltips when hovering different arcs
			        labels: labels
			    },
		        options: {
		            title: {
						display: true,
						text: title
		            }
		          }
			});
		}
		
		function Taules () {
			
			this.buildTableActualsPerOrganGestor = function(data) {
				var mapOrgansGestors = {};
				
				data.forEach(function(serie){
					var organGestor = serie.metaExpedient.organGestor;
					var organGestorId = organGestor != null ? organGestor.id : 'Comu';
					if (organGestorId in mapOrgansGestors) {
						mapOrgansGestors[organGestorId].dades.push(serie);
						
					} else {
						mapOrgansGestors[organGestorId] = {
								organGestor: organGestor,
								dades: [serie]
						}
					}
				});
				
				var tableHeader = '<table id="table-per-organs-' + this.taules.length+ '" class="table table-bordered table-striped table-hover style="width:100%" >' +
				'<thead>' +
					'<tr>';
				
				tableHeader += '</tr></thead><tbody>';
				
				function sumListAttr(list, attrname) {
					var sum = 0;
					list.forEach(function(serie){
						sum += serie[attrname];
					});
					return sum;
				}
				var tableBody = '';
				for (var organGestorId in mapOrgansGestors) {
					var dataOrganGestor = mapOrgansGestors[organGestorId];
					var organGestorNom = dataOrganGestor.organGestor != null ? dataOrganGestor.organGestor.nom : 'Comú';
					var row = '<tr>';
					row += '<td>' + organGestorNom + '</td>';
					
					row += '</tr>';
					tableBody += row;
				}

				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
		
			}
			
			this.taules = [];
			this.cleanTaules = function() {
				this.taules.forEach(function (dataTable) {
					dataTable.destroy();
				});
				this.taules = [];
			}
			
			this.addTaula = function (dataTable) {
				this.taules.push(dataTable);
			}
			
			this.addTaulaAcualsPerMetaExpedient = function (data, selectorDiv) {
				var htmlTable = this.buildTableActualsPerMetaExpedient(data);
				$(selectorDiv).append(htmlTable);
				var dataTable = dataTableHistoric("#table-per-metaexpedients-" + this.taules.length);
				this.addTaula(dataTable);
			}
			
			this.addTaulaAcualsPerOrganGestor = function (data, selectorDiv) {
				var title = '<h2><spring:message code="historic.taula.titol.perorgan"/></h2>'
				var htmlTable = this.buildTableActualsPerOrganGestor(data);
				$(selectorDiv).append(title + htmlTable);	
				
				dataTable = dataTableHistoric("#table-per-organs-" + this.taules.length);
				this.addTaula(dataTable);
			}
		}
		
		function chartLine(canvas, labels, datasets, title) {
			return new Chart(canvas, {
			    type: 'line',
			    data: {
			        labels: labels,
			        datasets: datasets, 
			    },
			    options: {
			        scales: {
			            yAxes: [{
			                ticks: {
			                    beginAtZero: true
			                }
			            }]
			        },
		            title: {
						display: true,
						text: title
		            }
			    }
			});
		}

		
		function modalLoading() {
			var modalId = "modal-loading";
			this.show = function () {
				$("#" + modalId).modal('show');
			};
			
			this.hide = function () {
				$("#" + modalId).modal('hide');
			};
			
			$('body').append(
					'	<div id="' + modalId + '" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
					'		<div class="modal-dialog modal-sm">' +
					'			<div class="modal-content">' +
// 					'				<div class="modal-header">' +
// 					'					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
// 					'					<h4 class="modal-title"></h4>' +
// 					'				</div>' +
					'				<div class="modal-body" style="padding:0">' +
					'					<iframe frameborder="0" height="100" width="100%"></iframe>' +
					'					<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
					'						<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>' + 
// 					(plugin.settings.missatgeLoading != null ? '<p>' + plugin.settings.missatgeLoading + '</p>' : '') +	
					'					</div>' +
					'				</div>' +
// 					'				<div class="modal-footer"></div>' +
					'			</div>' +
					'		</div>' +
					'	</div>');
		}
		
		function buildTableMetric(data, metric, idTable) {
			var columns = Object.keys(data);
			var tableHeader = '<table id="' + idTable + '" class="table table-bordered table-striped table-hover style="width:100%">' +
					'<thead>' +
						'<tr>';
			var tableFooter = '</tbody></table>';
			tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
			
			columns.forEach(function(c){
				tableHeader += '<th>' + c + '</th>';
			});
			
			tableHeader += '</tr></thead><tbody>';
			if (columns.length == 0) {
				return tableHeader + tableFooter;
			}
			var dates = [];
			columns.forEach(function(c){
				data[c] = data[c].sort((a, b) => (a.data > b.data) ? 1 : -1);
				if (data[c].length > dates.length) {
					dates = data[c].map(item => item.data);	
				}
			});
						
			var tableBody = '';
			for (var i = 0; i < dates.length; i++ ){
				var date = dates[i];
				var row = '<tr>';
				row += '<td data-sort="' + date + '">' +  new Date(date).toLocaleDateString("es") + '</td>'
				columns.forEach(function(c){
					var attrname = metricsDefinition[metric]['attrname'];
					var value = data[c][i][attrname] != null ? data[c][i][attrname] : 0;
					row += '<td>' + value + '</td>';
				});
				row += '</tr>';
				tableBody += row;
			}
			
			return tableHeader + tableBody + tableFooter;
		}
		
		function createChartMetric(data, metric, colors) {
			var columns = Object.keys(data);
			
			columns.forEach(function(c){
				data[c] = data[c].sort((a, b) => (a.data > b.data) ? 1 : -1);	
			});
			
			var dates = data[columns[0]].map(item => new Date(item.data).toLocaleDateString("es"));
			
			var datasets = []
			columns.forEach(function(c){
				var attrname = metricsDefinition[metric]['attrname'];
				var dataset = data[c].map(item => item[attrname] != null ? item[attrname] : 0)
				var color = (colors == null || colors[c] == null) ? getRandomColor() : colors[c]
				datasets.push({
    				'data': dataset,
    				'label': c,
    				'backgroundColor': "rgba(0,0,0,0.0)",
    				'borderColor': color
    				});	
			});

			var ctx = 'chart-' + metric;
			var labels = dates
			var chart = chartLine(ctx, labels, datasets, metricsDefinition[metric]["text"]);
		}
		
		
		function buildTableCurrent(data, metriques, getRegistreName) {

			var idTable = "table-organgestors";
			var tableHeader = '<table id="' + idTable + '" class="table table-bordered table-striped table-hover style="width:100%">' +
					'<thead>' +
						'<tr>';
			tableHeader += '<th><spring:message code="historic.taula.header.organgestor"/></th>';
			for (var metrica in metriques) {
				tableHeader += '<th>' + metricsDefinition[metrica]['text']+ '</th>';
			}
			tableHeader += '</tr></thead><tbody>';
			
			var tableBody = '';
			data.forEach(function(registre){
				var row = '<tr>';
				row += '<td>' +  getRegistreName(registre) + '</td>'
				for (var metrica in metriques) {
					var attrname = metricsDefinition[metrica]['attrname'];
					var value = registre[attrname] != null ? registre[attrname] : 0;
					row += '<td>' + value + '</td>';
				}
				row += '</tr>';
				tableBody += row;
			});
			var tableFooter = '</tbody></table>';
			return tableHeader + tableBody + tableFooter;
		}

		function createChartCurrent(data, metric, getRegistreName) {
			var labels = []
			var values = []
			var colors = []
			data.forEach(function(registre){
				labels.push(getRegistreName(registre));
				
				var attrname = metricsDefinition[metrica]['attrname'];
				var value = registre[attrname] != null ? registre[attrname] : 0;
				values.push(value);
				
				colors.push(getRandomColor());
			});
			
			var ctx = 'chart-current-' + metric;
			var chart = chartPie(ctx, values, labels, colors, metricsDefinition[metric]["text"]);
		}
		
		/**
		* 	CODI SECCIÓ ORGANS GESTORS
		*/
		function seccioOrgansGestors() {
			var taules = new Taules();
			var loading = new modalLoading();
			var metriques = [
				'NOTIFICACIONS_TOTAL',
				'NOTIFICACIONS_CORRECTES',
				'NOTIFICACIONS_AMB_ERROR',
				'NOTIFICACIONS_PROCEDIMENT_COMU',
				'NOTIFICACIONS_AMB_GRUP',
				'NOTIFICACIONS_ORIGEN_API',
				'NOTIFICACIONS_ORIGEN_WEB',
				'COMUNICACIONS_TOTAL',
				'COMUNICACIONS_CORRECTES',
				'COMUNICACIONS_AMB_ERROR',
				'COMUNICACIONS_PROCEDIMENT_COMU',
				'COMUNICACIONS_AMB_GRUP',
				'COMUNICACIONS_ORIGEN_API',
				'COMUNICACIONS_ORIGEN_WEB',
				'ENVIAMENTS',
				'PROCEDIMENTS',
				'GRUPS'
				];
			var selectorContainer = '#div-dades-organ';
			var $container = $(selectorContainer);
			
			var getOrganName = function (registre){
				return registre['codi'] + ' - ' + registre['nom'];
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");

				if ( showingTables ) {
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-procediment-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-dades-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				
				if ( showingTables ) {
					buildTableCurrent(response, getOrganName);
					$container.append(title + htmlTable);
					
				} else {
					metriques.forEach(function(metric){
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(response, metric, getColumnName);
					});
					
				}
			}
			
			function updateContentOrganGestors() {
				if ( showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "GET",
						url: 'historic/organgestors/actual',
						success: function(response) {
							loading.hide();
							viewActuals(response);
						}
					});
				}
				else if ( !showingDadesActuals ) {
					if (!isAnyOrganSelected){
						alert("Es necessari seleccionar algún òrgan gestor.");
						return;
					}
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/organgestors',
						success: function(response) {
							loading.hide();
							viewHistoric(response);						
						}
					});
				}
			}
			updateContentOrganGestors();
		}
		
		/**
		* 	CODI SECCIÓ PROCEDIMENTS
		*/
		function seccioProcediment() {
			var taules = new Taules();
			var loading = new modalLoading();
			var metriques = [
				'NOTIFICACIONS_TOTAL',
				'NOTIFICACIONS_CORRECTES',
				'NOTIFICACIONS_AMB_ERROR',
				'NOTIFICACIONS_PROCEDIMENT_COMU',
				'NOTIFICACIONS_AMB_GRUP',
				'NOTIFICACIONS_ORIGEN_API',
				'NOTIFICACIONS_ORIGEN_WEB',
				'COMUNICACIONS_TOTAL',
				'COMUNICACIONS_CORRECTES',
				'COMUNICACIONS_AMB_ERROR',
				'COMUNICACIONS_PROCEDIMENT_COMU',
				'COMUNICACIONS_AMB_GRUP',
				'COMUNICACIONS_ORIGEN_API',
				'COMUNICACIONS_ORIGEN_WEB',
				'ENVIAMENTS',
				'GRUPS'
				];
			var selectorContainer = '#div-dades-procediment';
			var $container = $(selectorContainer);
			var getColumnName = function (registre) {
				return registre['codiSia'] + ' - ' + registre['nom'];
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");
				if($.isEmptyObject(data)){
					alert("No data queried");
					return;
				}
				if ( showingTables ) {
					var dataTable = dataTableHistoric("#table-procediments");								
					taules.addTaula(dataTable);
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-procediment-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-procediment-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				loading.hide();
				if ( showingTables ) {
					buildTableCurrent(response, getColumnName);
					$container.append(title + htmlTable);
					
				} else {
					metriques.forEach(function(metric){
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(response, metric, getColumnName);
					});
				}
			}
			
			function updateContentProcediments() {
				if ( showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "GET",
						url: 'historic/procediments/actual',
						success: function(response) {
							loading.hide();
							viewActuals(response);
						}
					});
				}
				else if ( !showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/procediments',
						success: function(response) {
							loading.hide();
							viewHistoric(response);						
						}
					});
				}
			}
			
			updateContentProcediments();
		}
		
		/**
		* 	CODI SECCIÓ ESTAT
		*/
		function seccioEstat() {
			var taules = new Taules();
			var loading = new modalLoading();
			var selectorContainer = '#div-dades-estat';
			var $container = $(selectorContainer);
			var metriques = [
				'NOTIFICACIONS_TOTAL',
				'NOTIFICACIONS_CORRECTES',
				'NOTIFICACIONS_AMB_ERROR',
				'NOTIFICACIONS_PROCEDIMENT_COMU',
				'NOTIFICACIONS_AMB_GRUP',
				'NOTIFICACIONS_ORIGEN_API',
				'NOTIFICACIONS_ORIGEN_WEB',
				'COMUNICACIONS_TOTAL',
				'COMUNICACIONS_CORRECTES',
				'COMUNICACIONS_AMB_ERROR',
				'COMUNICACIONS_PROCEDIMENT_COMU',
				'COMUNICACIONS_AMB_GRUP',
				'COMUNICACIONS_ORIGEN_API',
				'COMUNICACIONS_ORIGEN_WEB'
				];
			var getEstatName = function (registre){
				return registre['estat'];
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");

				if ( showingTables ) {
					var dataTable = dataTableHistoric("#table-estats");								
					taules.addTaula(dataTable);
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-estat-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-estat-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				loading.hide();
				if ( showingTables ) {
					buildTableCurrent(response, getEstatName);
					$container.append(title + htmlTable);
					
				} else {
					for (var metric in response) {
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(response, metric, getEstatName);
					}
					
				}
			}
			
			function updateContentEstat() {
				
				if ( showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "GET",
						url: 'historic/estat/actual',
						success: function(response) {
							loading.hide();
							viewActuals(response);
						}
					});
				}
				else if ( !showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/estat',
						success: function(response) {
							loading.hide();
							viewHistoric(response);						
						}
					});
				}
			}
			
			updateContentEstat();
		}
		
		/**
		* 	CODI SECCIÓ GRUP
		*/
		function seccioGrup() {
			var taules = new Taules();
			var loading = new modalLoading();
			var selectorContainer = '#div-dades-grup';
			var $container = $(selectorContainer);
			var metriques = [
				'NOTIFICACIONS_TOTAL',
				'NOTIFICACIONS_CORRECTES',
				'NOTIFICACIONS_AMB_ERROR',
				'NOTIFICACIONS_PROCEDIMENT_COMU',
				'NOTIFICACIONS_AMB_GRUP',
				'NOTIFICACIONS_ORIGEN_API',
				'NOTIFICACIONS_ORIGEN_WEB',
				'COMUNICACIONS_TOTAL',
				'COMUNICACIONS_CORRECTES',
				'COMUNICACIONS_AMB_ERROR',
				'COMUNICACIONS_PROCEDIMENT_COMU',
				'COMUNICACIONS_AMB_GRUP',
				'COMUNICACIONS_ORIGEN_API',
				'COMUNICACIONS_ORIGEN_WEB'
			];
			var getEstatName = function (registre){
				return registre['estat'];
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");
				if($.isEmptyObject(data)){
					alert("No hay registros para esta consulta");
					return;
				}
				if ( showingTables ) {
					var dataTable = dataTableHistoric("#table-estats");								
					taules.addTaula(dataTable);
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-estat-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-estat-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				loading.hide();
				if ( showingTables ) {
					buildTableCurrent(response, getEstatName);
					$container.append(title + htmlTable);
					
				} else {
					for (var metric in response) {
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(response, metric, getEstatName);
					}
				}
			}
			
			function updateContent() {
				
				if ( showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "GET",
						url: 'historic/grups/actual',
						success: function(response) {
							loading.hide();
							viewActuals(response);
						}
					});
				} else if ( !showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/grups',
						success: function(response) {
							loading.hide();
							viewHistoric(response);						
						}
					});
				}
			}
			
			updateContent();
		}
		
		/**
		* 	CODI SECCIÓ USUARIS
		*/
		function seccioUsuaris() {
			var taules = new Taules();
			var loading = new modalLoading();
			var selectorContainer = '#div-dades-usuaris';
			var $container = $(selectorContainer);
			var metriques = [
				'NOTIFICACIONS_TOTAL',
				'NOTIFICACIONS_CORRECTES',
				'NOTIFICACIONS_AMB_ERROR',
				'NOTIFICACIONS_PROCEDIMENT_COMU',
				'NOTIFICACIONS_AMB_GRUP',
				'NOTIFICACIONS_ORIGEN_API',
				'NOTIFICACIONS_ORIGEN_WEB',
				'COMUNICACIONS_TOTAL',
				'COMUNICACIONS_CORRECTES',
				'COMUNICACIONS_AMB_ERROR',
				'COMUNICACIONS_PROCEDIMENT_COMU',
				'COMUNICACIONS_AMB_GRUP',
				'COMUNICACIONS_ORIGEN_API',
				'COMUNICACIONS_ORIGEN_WEB'
			];
			var getEstatName = function (registre){
				return registre['estat'];
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");

				if ( showingTables ) {
					var dataTable = dataTableHistoric("#table-estats");								
					taules.addTaula(dataTable);
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-usuaris-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-usuaris-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				loading.hide();
				if ( showingTables ) {
					buildTableCurrent(response, getEstatName);
					$container.append(title + htmlTable);
					
				} else {
					for (var metric in response) {
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(response, metric, getEstatName);
					}
				}
			}
			
			function updateContent(usuaris) {
				if (usuaris.length == 0){
					return;
				}
				if ( showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/usuaris/actual',
						data: {
							usuaris: usuaris
						},
						success: function(response) {
							loading.hide();
							viewActuals(response);
						}
					});
				} else if ( !showingDadesActuals ) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/usuaris',
						data: {
							usuaris: usuaris
						},
						success: function(response) {
							loading.hide();
							viewHistoric(response);						
						}
					});
				}
			}
			
			updateContent(usuarisSeleccionats);
			$("form#form-estadistics-usuaris").on('submit', function(){
				var usuaris = $("#input-usuaris").find(':selected').map(function(){return $(this).val();}).get();;
				var names = $("#input-usuaris").find(':selected').map(function(){return $(this).html();}).get();;
				updateContent(usuaris);
				return false;
			});	
		}
		
		
		$(function () {
			
			if (showDadesOrganGestor) {
				seccioOrgansGestors();
			}
			if (showDadesProcediment) {
				seccioProcediment();
			}
			if (showDadesEstat) {
				seccioEstat();
			}
			if (showDadesGrup) {
				seccioGrup();
			}
			if (showDadesUsuari) {
				seccioUsuaris();
			}
			
			$(".form-filtre-visualitzacio").on('change', 'input:radio', function (event) {
				$("#historicFiltreCommand").submit();
			});
						
		});
		
	</script>
</head>
<body>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansInicial"/>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansLlistat"/>
<c:url value="/metaexpedientajax/metaexpedient" var="urlConsultaMetaExpedientsInicial"/>
<c:url value="/metaexpedientajax/metaexpedients" var="urlConsultaMetaExpedientsLlistat"/>
	<form:form action="" method="post" cssClass="well" commandName="historicFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<not:inputDate name="dataInici" inline="true" placeholderKey="historic.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<not:inputDate name="dataFi" inline="true" placeholderKey="historic.filtre.data.fi"/>
			</div>		
			<div class="col-md-4">
				<not:inputSelect 	name="organGestorsCodis" 
									optionItems="${organsGestorsPermisLectura}" 
									optionValueAttribute="codi" optionTextAttribute="nom" 
									placeholderKey="historic.filtre.organsGestors" 
									inline="true" 
									emptyOption="true" 
									optionMinimumResultsForSearch="0"/>
			</div>
			<div class="col-md-4">
				<not:inputSelect 	name="procedimentsIds" 
									optionItems="${procedimentsPermisLectura}" 
									optionValueAttribute="id" 
									optionTextAttribute="descripcio" 
									placeholderKey="historic.filtre.procediments" 
									inline="true" 
									emptyOption="true" 
									optionMinimumResultsForSearch="0"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<not:inputSelect name="dadesMostrar" 
								 optionEnum="HistoricDadesMostrarEnum" 
								 emptyOption="false" 
								 inline="true"/>
			</div>
			<div class="col-md-4">
				
			</div>
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
		<div class="row form-filtre-visualitzacio">
			<div class="col-md-2">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.showingTables == false}">active</c:if>"> 
						<form:radiobutton path="showingTables" value="false"/>
						<i class="fa fa-bar-chart" aria-hidden="true"></i> <spring:message code="historic.filtre.mostraGrafics"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.showingTables == true}">active</c:if>"> 
						<form:radiobutton path="showingTables" value="true"/>
						<i class="fa fa-table" aria-hidden="true"></i> <spring:message code="historic.filtre.mostraTaules"/>
					</label> 
				</div>
			</div>
			<div class="col-md-3">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == null}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament"/>
						<i class="fa fa-clock-o"></i> <spring:message code="historic.filtre.mostraDadesActuals"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == 'DIARI'}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament" value="DIARI"/>
						<i class="fa fa-calendar"></i> <spring:message code="historic.filtre.mostraDadesPerDia"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == 'MENSUAL'}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament" value="MENSUAL"/>						 
						<i class="fa fa-calendar-o"></i> <spring:message code="historic.filtre.mostraDadesPerMes"/>
					</label>
			
				</div>
			</div>		
		</div>
	</form:form>		
	<c:if test="${showDadesOrganGestor}">
		<h1><spring:message code="historic.titol.seccio.organGestor"/></h1>
		<div class="row">
			<div id="div-dades-organ" class="col-md-12">
			</div>
		</div>

	</c:if>
	<c:if test="${showDadesProcediment}">
		<h1><spring:message code="historic.titol.seccio.procediment"/></h1>
		<div class="row">
			<div id="div-dades-procediment" class="col-md-12">
			</div>
		</div>
	</c:if>
	<c:if test="${showDadesEstat}">
		<h1><spring:message code="historic.titol.seccio.estat"/></h1>
		<div class="row">
			<div id="div-dades-estat" class="col-md-12">
			</div>
		</div>
	</c:if>
	<c:if test="${showDadesGrup}">
		<h1><spring:message code="historic.titol.seccio.grup"/></h1>
		<div class="row">
			<div id="div-dades-grup" class="col-md-12">
			</div>
		</div>
	</c:if>
	<c:if test="${showDadesUsuari}">
		<h1><spring:message code="historic.titol.seccio.usuari"/></h1>
		<form id="form-estadistics-usuaris" class="well">
			<div class="row">
				<div class="col-md-8">
					<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
					<c:set var="placeholderText"><spring:message code="historic.filtre.select.usuari"/></c:set>
					<select name="usuaris" cssClass="form-control" id="input-usuaris"
								style="width:100%" data-toggle="suggest"
								data-placeholder="${placeholderText}"
								data-minimum-input-length="3"
								data-url-llistat="${urlConsultaLlistat}" 
								data-url-inicial="${urlConsultaInicial}"
							    multiple="true"
							    data-placeholder="${placeholderText}"
								data-current-value="${fn:join(usuarisSeleccionats, ",")}" 
								data-suggest-value="codi"
								data-suggest-text="nom"
								data-suggest-text-addicional="nif" 
								data-url-param-addicional=""> </select>
				</div>
				<div class="col-offset-2 col-md-2 pull-right">
					<div class="pull-right">
						<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
							<span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/>
						</button>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div id="div-dades-usuaris" class="col-md-12">
			</div>
		</div>
	</c:if>

	<form action="historic/exportar" method="post" class="well">
		<div class="row">
			<div class="col-md-8"></div>
			<div class="col-md-2">
				<select name="format" class="form-control" style="width:100%"
						data-minimumresults="-1"
						data-toggle="select2">
							<option value="json">json</option>
							<option value="xlsx">xlsx</option>
							<option value="odf">odf</option>
							<option value="xml">xml</option>
				</select>
			</div>
			<div class="col-md-2">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
						<span class="fa fa-download"></span>&nbsp; <spring:message code="historic.exportacio.boto.exportar"/>
					</button>
				</div>
			</div>
		</div>
	</form>

</body>
</html>
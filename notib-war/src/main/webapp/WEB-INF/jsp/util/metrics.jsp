<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code='metriques.titol' /></title>
	
	<script src="<c:url value='/js/metrics/metrics-watcher.js'/>"></script>
	<link href="<c:url value='/css/metrics-watcher-style.css'/>" rel="stylesheet">
	<script src="<c:url value='/js/Blob.js'/>"></script>
	<script src="<c:url value='/js/FileSaver.min.js'/>"></script>
	
	<script src="<c:url value='/js/metrics/chart.min.js'/>"></script>
	<link href="<c:url value='/css/metrics/chart.min.css'/>" rel="stylesheet">
	<%--  plugins --%>
	<script src="<c:url value='/js/metrics/chartjs-plugin-annotation.min.js'/>"></script>
	<script src="<c:url value='/js/metrics/chartjs-plugin-datalabels.min.js'/>"></script>
	<script src="<c:url value='/js/metrics/metrics.js'/>"></script>

<style type="text/css">	
.progress-title {
	position: absolute; 
	right: 42px;
}
.metricsWatcher .progress {
	margin-bottom: 10px;
}
#headingOne {
	cursor: pointer;
}
#headingTwo {
	cursor: pointer;
}
.pes {
	margin-bottom: 2px;
}
.max {
	margin-bottom: 0px;
}
.timers {
	margin-bottom: 0px !important; 
	margin-top: 10px !important; 
	cursor: pointer;
}
.mitja {
	background-image: linear-gradient(
						45deg, 
						rgba(255, 255, 255, 0.25) 25%, 
						transparent 25%, 
						transparent 50%, 
						rgba(255, 255, 255, 0.25) 50%, 
						rgba(255, 255, 255, 0.25) 75%, 
						transparent 75%, 
						transparent);
}
#llegenda {
	padding: 15px;
}
.ocult {
	display: none;
}
.collapse.in {
	display: flex;
	flex-direction: row;
	flex-wrap: wrap;
	justify-content: center;
	margin: 1%;
	background-color: #fffafa;
	height: 300px;
}
.loaded.collapse.in {
	height: 340px;
}
.chart-container {
	width: 30%;
	margin-left: 40px;
	margin-right: 40px;
}
.counterTitle {
	font-size: 17px;
	font-weight: bold;
	margin-bottom: 1%;
}
.loaded {
	border: 1px solid aliceblue;
	box-shadow: 1px 1px 7px #fdfdfd;
	border-radius: 7px;
}
</style>
	
<script>
	
var metricsData = ${metriques};
$(document).ready(function() {
	var missatges = [
		"<spring:message code="metriques.timers.pes"/>",
		"<spring:message code="metriques.timers.temps.mig"/>",
		"<spring:message code="metriques.timers.temps.maxim"/>",
		"<spring:message code="metriques.timers.frequencia"/>",//Frequència
		"<spring:message code="metriques.timers.mitjana"/>",//Mitja
		"<spring:message code="metriques.timers.duracio"/>",//Duració
		"<spring:message code="metriques.timers.percentils"/>",//Percentils
	];
	// console.log(missatges[0]);
	// Event del botó exportar: desa les mètriques en un fitxer de text (metrics.json)
	$('#exportar').click(function() {
		var blob = new Blob([JSON.stringify(metricsData)], {type: "text/plain;charset=utf-8"});
		saveAs(blob, "metrics.json");
	});
	// Event del botó importar: click al imput tipus file, per tal d'obrir el diàleg de selecció de fitxer.
	// Un cop seleccionat el fitxer, es llença l'event change de l'imput tipus file.
	$('#importar').click(function() {
		$('#files').click();
		$('#netejar').trigger('click');
	});
	
	// Event per a carregar el fitxer. TODO: Comprovar que el fitxer és JSON
	$('#files').on('change', function(event){
		var files = event.target.files; // FileList object
	    // Loop through the FileList and render image files as thumbnails.
	    if(files && files[0]) { 
	    	var reader = new FileReader();
	    	
	    	// Closure to capture the file information.
	        reader.onload = (function(theFile) {
	          return function(e) {
	            metricsData = JSON.parse(e.target.result);//reader.result);
		      	$('#counters').empty();
		      	$('#timers').empty();
		      	$('#timers').append(
						"<h4 class='ocult'><spring:message code='metriques.timers.generics'/></h4>" +
						"<div id='timers-generics' class='ocult'></div>");
		      	drawTimersGraph(metricsData, missatges);				
	          };
	        })(files[0]);
	    	
	      	reader.readAsText(files[0]);
	   	}
	});
	// Al fer clic en un timer, es desplegarà la informació extesa d'aquest
	$('#timers').delegate(".timers", "click", function(event) {
		event.stopPropagation();
		var divTimerId = $(this).data('id');
		var divTimerSelector = document.getElementById($(this).data('id'));
		if (!$(divTimerSelector).hasClass('loaded')) {
			drawTimerDetailedInfo(divTimerId, metricsData.timers[divTimerId], missatges);
 			$(divTimerSelector).addClass('loaded');
		}
		$(divTimerSelector).find('.chart-container').collapse('toggle');
		$(divTimerSelector).collapse('toggle');
	});
		
	// Afegim la informació gràfica dels timers (sense detalls)
	drawTimersGraph(metricsData, missatges);
});

</script>
</head>
<body>
	<div id="ufo-sightings"></div>
	<div class=" row well">
		<div class="col-md-12">
			<div class="pull-right">
				<input type="file" id="files" name="files" style="display:none;"/>
				<button id="importar" type="button" name="accio" value="importar" class="btn btn-default"><spring:message code="comu.boto.importar"/></button>
				<button id="exportar" type="button" name="accio" value="exportar" class="btn btn-default"><spring:message code="comu.boto.exportar"/></button>
			</div>
		</div>
	</div>
	<!-- Nav tabs -->
  	<ul class="nav nav-tabs" role="tablist">
    	<li role="presentation" class="active"><a href="#timers-tab" aria-controls="timers-tab" role="tab" data-toggle="tab">Timers</a></li>
  	</ul>

  	<!-- Tab panes -->
	<div class="tab-content">
  		<div role="tabpanel" class="tab-pane fade in active" id="timers-tab">
  			<div id="llegenda">
  				<div class='counterTitle'><spring:message code="metriques.timers.llegenda"/></div>
				<canvas class='llegendaChart collapse' width='100' height='100'></canvas>
			</div>
      		<div id="timers" class="panel-body">
				<h4 class="ocult"><spring:message code="metriques.timers.generics"/></h4>
				<div id="timers-generics" class="ocult"></div>
      		</div>
  		</div>
	</div>
	
</body>
</html>

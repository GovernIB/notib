<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="es.caib.notib.enviaments.proces.titol"/></title>
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
	<not:modalHead/>

<script type="text/javascript">
var itervalProgres;
writtenLines = 0;
$(document).ready(function() {
	$('.actualitzar-btn').on('click', function() {
		$('.actualitzar-btn-container').addClass('hidden');
		$('.content').removeClass('hidden');
		submit();
	});
});

function submit() {
	refreshProgres();
	$('.containerDetailButton').on('click', function() {
		$('.progresContainerDetail').slideToggle("slow");
	});
	
	$.post("<c:url value='/notificacio/refrescarEstatNotifica'/>");
}

function refreshProgres() {
	itervalProgres =  setInterval(function(){
			getProgres(); 
		}, 100);
}

function getProgres() {
	$.ajax({
		type: 'GET',
		url: "<c:url value='/notificacio/refrescarEstatNotifica/estat'/>",
		success: function(data) {
			if (data) {
				// console.log(data);
				let info = data.info;
				let index;
				for (index = writtenLines; index < info.length; index++) {
					if (info[index].tipus != 'ERROR') {
						$('.customProgressTitle').empty();
					}
					
					if (info[index].tipus != 'ERROR') {
						$('.customProgressTitle').append("<div class='info-" + info[index].tipus + "'>" + info[index].text + " [" + data.progres + "%]</div>");
					}
					
					$('.customProgressPercentage').css('width', data.progres + '%');
					
					if (info[index].tipus == 'ERROR' || info[index].tipus == 'TITOL' || info[index].tipus == 'SUB_INFO' || info[index].tipus == 'WARNING') {
						$('#actualitzacioInfo').append("<div class='info-" + info[index].tipus + "'><div>" + info[index].text + "</div></div>");
					} else {
						$('#actualitzacioInfo').append("<div class='info-" + info[index].tipus + "'><div class='info'>" + info[index].text + "</div><div class='percentage'>[" + data.progres + "%]</div></div>");
					}
				}
				writtenLines = index;
				if (data.progres == 100) {
					clearInterval(itervalProgres);
				}
			}
		},
		error: function() {
			console.log("error obtenint progrés...");
		}
	});
}
</script>

<style type="text/css">

.info-TITOL {
	font-size: 14px;
	font-weight: bold;
	text-align: left;
	margin-bottom: 2%;
}

.info-INFO, .info-SUB_INFO {
	font-size: 13px;
	display: flex;
	justify-content: center;
}

.info-ERROR {
	font-size: 13px;
	color: #ff0000;
}

.info-WARNING {
	font-size: 13px;
	font-weight: bold;
	color: #e08a0a;
}

.detall {
	text-align: center;
	width: 100%;
	background-color: #F5F5F5;
	padding: 10px;
}

.info {
	width: 70%;
	text-align: left;
}

.percentage {
	width: 10%;
}

.info > div {
	margin: 0 0 4px;
}

.containerDetailButton {
	width: 100%;
	margin-top: 1px;
	padding: 1px;
	font-size: 10px;
}

.progresContainerDetail {
	margin-top: 2%;
	display: none;
}

.progresContainer {
	width: 100%;
	height: 60px;
	background-color: #002133;
	border-radius: 4px;
	padding: 1%;
}

.customProgressTitle {
	color: #FFFFFF;
	text-align: center;
}

.customProgressPercentageTotal {
	width: 100%;
	height: 6px;
	background-color: #FFFFFF;
	border-radius: 3px;
	margin-top: 1%;
}

.customProgressPercentage {
	width: 0%;
	height: 6px;
	background-color: #c8a95b;
	border-radius: 3px;
	margin-top: 1%;
}

.actualitzar-btn-container {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 315px;
}

.actualitzar-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 60px;
	width: 50%;
}
</style>
</head>

<body>
	<form:form id="formUpdateAuto" method="post" cssClass="form-horizontal" role="form">
		<div class="actualitzar-btn-container">
			<a class="btn btn-default actualitzar-btn"><spring:message code="es.caib.notib.enviaments.proces.actualitzar"/></a>
		</div>
		<div role="tabpanel" class="tab-pane content hidden">
			<div class="progresContainer">
				<div class="customProgressTitle"></div>
				<div class="customProgressPercentageTotal">
					<div class="customProgressPercentage"></div>
				</div>
			</div>
			<div class="containerDetailButton btn btn-default"><spring:message code="es.caib.notib.enviaments.proces.detall"/></div>
			<div class="progresContainerDetail">
				<div id="actualitzacioInfo" class="detall">
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>

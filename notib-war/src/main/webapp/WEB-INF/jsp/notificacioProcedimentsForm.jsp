<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="notificacio.form.titol.procediments"/></title>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<not:modalHead/>
<style type="text/css">
.search {
	padding-left: 0;
}
.sort {
	cursor: pointer;
}
.search_btn {
	margin-top: 1%;
}
.filtre {
	min-height: 120px;
	padding: 19px;
	margin-bottom: 20px;
	background-color: #f5f5f5;
	border: 1px solid #e3e3e3;
	box-shadow: inset 0 1px 1px rgba(0,0,0,.05);
}
</style>
<script>  
$(document).ready(function() {
	var select2 = $('#organGestorSelect');
	var select2Options = {
			theme: 'bootstrap',
			width: 'auto',
			allowClear: true};
	select2.select2(select2Options);
	
	$('#search').keyup(function(){  
		console.log("as");
		 var value = $(this).val().toLowerCase();
		$('#procediments tr').filter(function() {
		      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
	    });	
	});
	
	
	$( "#organGestorSelect" ).change(function() {
		var value = $(this).find('option:selected').val();
		$('#procediments tr').filter(function() {
				$(this).toggle($(this).find("td:eq(3)").text() == value);  
		    });
	});
	$( "#netejar" ).click(function() {
		$('#procediments tr').filter(function() {
			$(this).toggle($(this).find("td:eq(3)").text() != null);  
			$("#comuBtn").prop("checked", false);
			$('#search').val('');
	    });
	});
	
	
	$('#comuBtn').on('click', function(event){
		
		var checked = $(this).is(':checked');
		
		$('#procediments tr').filter(function() {
			if(checked){
				$(this).toggle($(this).find("td:eq(2)").text() == "true");
			}else{
				$(this).toggle($(this).find("td:eq(2)").text() != null);
			}
		      
	    });
	});

	
	var table = $('table');
});    
function sortTable(n) {
	var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
	table = document.getElementById("procediments_table");
	switching = true;
	dir = "asc";
	while (switching) {
	  switching = false; 
	  rows = table.rows;
	  for (i = 1; i < (rows.length - 1); i++) {
	    shouldSwitch = false;
	    x = rows[i].getElementsByTagName("TD")[n];
	    y = rows[i + 1].getElementsByTagName("TD")[n];
	    if (dir == "asc") {
	      if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
	        shouldSwitch = true;
	        break;
	      }
	    } else if (dir == "desc") {
	      if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
	        shouldSwitch = true;
	        break;
	      }
	    }
	  }
	  if (shouldSwitch) {
	    rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
	    switching = true;
	    switchcount ++;
	  } else {
	    if (switchcount == 0 && dir == "asc") {
	      dir = "desc";
	      switching = true;
	    }
	  }
	}
}
</script>  
</head>

<body>
	<div class="filtre">
		<div class="col-xs-12 row">
			<div class="col-xs-6 search">
				<input type="text" name="search" id="search" class="form-control" placeholder="<spring:message code="notificacio.form.titol.procediments.cercar"/>"/>
			</div>
			<div class="col-xs-6 search">
				<select class="form-control" id='organGestorSelect' data-placeholder="<spring:message code="organgestor.form.camp.organisme"/>">
					  	<option></option>
					    <c:forEach items="${organsGestors}" var="organ">
				            <option value="${organ.codi}">${organ.nom}</option>
				    </c:forEach>
				</select>
			</div>
		</div>
		<div class="col-xs-12 row search_btn">
			<div class="col-xs-10 search">
				 <label class="control-label" for="comuBtn"><spring:message code="procediment.form.camp.comu"/>:</label>  <input type="checkbox" id="comuBtn" >
			</div>
			<div class="pull-right form-buttons">
				 <input type="button" name="netejar" id="netejar" value="<spring:message code="comu.boto.netejar"/>" class="btn btn-default"/>
			</div>
		</div>
	</div>
	<table class="table table-hover" id="procediments_table">
		<thead>
			<tr>
				<th scope="col"><spring:message code="notificacio.procediment.codi"/>  <span class="fa fa-sort sort"  onclick="sortTable(0)"></span></th>
				<th scope="col"><spring:message code="notificacio.procediment.nom"/>  <span class="fa fa-sort sort"  onclick="sortTable(1)"></span></th>
				<th scope="col"></th>
			</tr>
		</thead>
		<tbody id="procediments">
			<c:forEach items="${procediments}" var="procediment">
					<tr>
						<td scope="row" id="codi" name="codi" width="10%">${procediment.codi}</td>
						<td scope="row" id="nom" name="nom" width="80%">${procediment.nom}</td>
						<td style="display:none;" scope="row" name="comu" >${procediment.comu}</td>
						<td style="display:none;" scope="row" name="organGestor" >${procediment.organGestor}</td>
						<td scope="row" name="id" width="10%">
						<button onclick="window.top.location='/notib/notificacio/new/${procediment.id}';return false;" class="btn btn-default"><spring:message code="notificacio.form.titol.procediments.iniciar"/></button>
						</td>
					</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
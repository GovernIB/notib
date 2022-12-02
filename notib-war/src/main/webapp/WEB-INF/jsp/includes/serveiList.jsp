<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
pageContext.setAttribute(
			"isRolActualAdministradorEntitat",
			es.caib.notib.war.helper.RolHelper.isUsuariActualAdministradorEntitat(request));
%>
<script type="text/javascript">

var myHelpers = {hlpIsAdministradorEntitat: isRolActualAdministradorEntitat};

$.views.helpers(myHelpers);

function isRolActualAdministradorEntitat() {
	return ${isRolActualAdministradorEntitat};
}

var organsGestors = [];
organsGestors.push({id:"", text:"", estat:"V"});
<c:forEach items="${organsGestors}" var="organGestor">
organsGestors.push({id:"${organGestor.codi}", text:"${organGestor.valor}", estat:"${organGestor.estat}"});
</c:forEach>

$(document).ready(function() {
	$('#nomesAmbErrors').val(false);
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
		$('#btn-entregaCieActiva').removeClass('active');
		$('#entregaCieActiva').val(false);
		$('#btn-comu').removeClass('active');
		$('#comu').val(false);
	});

	loadOrgans($('#organGestor'), organsGestors, "<spring:message code='notificacio.list.columna.organGestor.obsolet'/>");


	$('#btn-entregaCieActiva').click(function() {
		let entregaCieActiva = !$(this).hasClass('active');
		$('#entregaCieActiva').val(entregaCieActiva);
	})
	$('#btn-comu').click(function() {
		let entregaCieActiva = !$(this).hasClass('active');
		$('#comu').val(entregaCieActiva);
	})
});
</script>

<c:if test="${not simplifiedView}">
<form:form id="filtre" action="" method="post" cssClass="well" commandName="procSerFiltreCommand">
	<div class="row">
		<div class="col-md-2">
			<not:inputText name="codi" inline="true" placeholderKey="procediment.list.columna.codi"/>
		</div>
		<div class="col-md-3">
			<not:inputText name="nom" inline="true" placeholderKey="procediment.list.columna.nom"/>
		</div>
		<c:if test="${not simplifiedView}">
			<div class="col-md-5">
				<not:inputSelect name="organGestor" placeholderKey="notificacio.list.filtre.camp.organGestor" inline="true" emptyOption="true" optionMinimumResultsForSearch="0"/>
			</div>
		</c:if>
		<div class="col-md-1">
			<not:inputSelect name="estat" optionItems="${procedimentEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" inline="true" emptyOption="true"
							 placeholderKey="organgestor.list.columna.estat" textKey="organgestor.list.columna.estat" required="true" labelSize="0"/>
		</div>
	</div>
	<div class="row">
		<div class="col-md-4">
			<div class="btn-group" role="group">
				<button id="btn-comu" title="" class="btn btn-default <c:if test="${procSerFiltreCommand.comu}">active</c:if>" data-toggle="button">
					<span class="fa fa-globe"></span> <spring:message code="procediment.filter.form.camp.comu"/>
				</button>
				<not:inputHidden name="comu"/>
				<button id="btn-entregaCieActiva" title="" class="btn btn-default <c:if test="${procSerFiltreCommand.entregaCieActiva}">active</c:if>" data-toggle="button">
					<span class="fa fa-envelope"></span> <spring:message code="organgestor.list.columna.cie"/>
				</button>
				<not:inputHidden name="entregaCieActiva"/>
			</div>
		</div>
		<div class="col-md-2 pull-right">
			<div class="pull-right">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</div>
</form:form>
</c:if>
<table
	id="servei"
	data-toggle="datatable"
	data-url="<c:url value="/servei/datatable"/>"
	data-search-enabled="false"
	data-default-order="3"
	data-default-dir="desc"
	class="table table-striped table-bordered"
	data-botons-template="#botonsTemplate"
	data-save-state="true"
	data-mantenir-paginacio="true"
	style="width:100%"
	data-filter="#filtre">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false" width="4%">#</th>
			<th data-col-name="codi"><spring:message code="procediment.list.columna.codi"/></th>
			<th data-col-name="nom"><spring:message code="procediment.list.columna.nom"/></th>

			<c:if test="${not simplifiedView}">
			<th data-col-name="organGestorEstat" data-visible="false"></th>
				<th data-col-name="organGestorDesc" data-template="#cellOrganGestorTemplate"><spring:message code="procediment.list.columna.organGestor"/>
					<script id="cellOrganGestorTemplate" type="text/x-jsrender">
						{{:organGestorDesc}}
						{{if organGestorEstat != 'V'}}
							<span class="fa fa-warning text-danger" title="<spring:message code='procediment.list.columna.organGestor.obsolet'/>"></span>{{/if}}
					</script>
				</th>
			</c:if>
			<th data-col-name="retard"><spring:message code="procediment.list.columna.retard"/></th>
			<th data-col-name="caducitat"><spring:message code="procediment.list.columna.caducitat"/></th>
			<th data-col-name="entregaCieActiva" data-template="#cellActivaTemplate">
				<spring:message code="organgestor.list.columna.cie"/>
				<script id="cellActivaTemplate" type="text/x-jsrender">
					{{if entregaCieActiva > 0}}<span class="fa fa-check"></span>{{/if}}
					{{if entregaCieActiva == 2}}<span class="label label-info"><spring:message code="procediment.list.columna.cie.organ"/></span>{{/if}}
					{{if entregaCieActiva == 3}}<span class="label label-info"><spring:message code="procediment.list.columna.cie.entitat"/></span>{{/if}}
				</script>
			</th>
			<th data-col-name="comu" data-template="#cellComuTemplate">
				<spring:message code="procediment.list.columna.comu"/>
				<script id="cellComuTemplate" type="text/x-jsrender">
					{{if comu}}<span class="fa fa-check"></span>{{/if}}
				</script>
			</th>

			<th data-col-name="requireDirectPermission" data-template="#cellrRequireDirectPermissionTemplate">
				<spring:message code="procediment.list.columna.requireDirectPermission"/>
				<script id="cellrRequireDirectPermissionTemplate" type="text/x-jsrender">
					{{if requireDirectPermission}}<span class="fa fa-check"></span>{{/if}}
				</script>
			</th>
			<th data-col-name="actiu" data-template="#cellActiuTemplate">
				<spring:message code="procediment.list.columna.actiu"/>
				<script id="cellActiuTemplate" type="text/x-jsrender">
					{{if actiu}}<span class="fa fa-check"></span>{{/if}}
				</script>
			</th>
			<th data-col-name="agrupar" data-visible="false" id="agrupable"></th>

			<c:if test="${not simplifiedView}">
				<th data-col-name="grupsCount" data-template="#cellGrupsTemplate" data-orderable="false" width="10%">
					<script id="cellGrupsTemplate" type="text/x-jsrender">
					{{if agrupar != true}}
						<button class="btn btn-default" disabled><span class="fa fa-users"></span>&nbsp;<spring:message code="procediment.list.boto.grups"/>&nbsp;</a>
					{{else}}
						<a href="${unitatCodiUrlPrefix}servei/{{:id}}/grup" class="btn btn-default"><span class="fa fa-users"></span>&nbsp;<spring:message code="procediment.list.boto.grups"/>&nbsp;<span class="badge">{{:grupsCount}}</span></button>
					{{/if}}
					</script>
				</th>
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="10%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="${unitatCodiUrlPrefix}servei/{{:id}}/permis" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="procediment.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
					{^{if (~hlpIsAdministradorEntitat()) || (!~hlpIsAdministradorEntitat() && !comu) }}
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${unitatCodiUrlPrefix}servei/{{:codi}}/update" data-toggle="ajax"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="servei.list.boto.servei.actualitzar"/></a></li>
								<li><a href="${unitatCodiUrlPrefix}servei/{{:id}}" data-toggle="modal" data-maximized="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !actiu}}
								<li><a href="${unitatCodiUrlPrefix}servei/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="${unitatCodiUrlPrefix}servei/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="${unitatCodiUrlPrefix}servei/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="servei.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					{{/if}}
					</script>
				</th>
			</c:if>
		</tr>
	</thead>
</table>

<script id="botonsTemplate" type="text/x-jsrender">
<c:if test="${not simplifiedView}">
	<p style="text-align:right">
		<c:if test="${isRolActualAdministradorEntitat}">
			<a id="procediment-boto-cache" class="btn btn-warning" href="${unitatCodiUrlPrefix}procediment/cache/refrescar"><span class="fa fa-trash"></span>&nbsp;<spring:message code="procediment.list.boto.cache"/></a>
<%--			<c:if test="${!isCodiDir3Entitat}">--%>
				<a id="procediment-boto-update"
					class="btn btn-default" href="${unitatCodiUrlPrefix}servei/update/auto"
					data-toggle="modal"
					data-maximized="false">
					<span class="fa fa-refresh"></span>&nbsp;<spring:message code="servei.list.boto.servei.auto"/>
				</a>
<%--			</c:if>--%>
		</c:if>
		<a id="procediment-boto-nou" class="btn btn-default" href="${unitatCodiUrlPrefix}servei/new" data-toggle="modal" data-maximized="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="servei.list.boto.nou.procediment"/></a>
	</p>
</c:if>
</script>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="inputSize" required="false" rtexprvalue="true"%>
<%@ attribute name="logoMenu" required="false" rtexprvalue="true"%>
<%@ attribute name="fileEntitat" required="false" rtexprvalue="true"%>
<%@ attribute name="messageInfo" required="false" rtexprvalue="true"%>
<%@ attribute name="info" required="false" rtexprvalue="true"%>
<%@ attribute name="fileName" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}">*</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campInfoText"><c:choose><c:when test="${not empty messageInfo}"><spring:message code="${messageInfo}"/></c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize"><c:choose><c:when test="${not empty inputSize}">${inputSize}</c:when><c:otherwise>${12 - campLabelSize}</c:otherwise></c:choose></c:set>
<c:set var="campInputSizeDiv">${campInputSize / 2}</c:set>
<style>
.info-length {
	font-size: x-small;
}
.comentari {
	font-size: 12px;
	color: #999;
	margin-bottom: 0px;
}
</style>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-${campInputSize}">
				<div class="fileinput fileinput-new input-group" style="width:100%" data-provides="fileinput">
					<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
					<span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span><input type="file" id="${campPath}" name="${campPath}"></span>
					<a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Netejar</a>
				</div>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
				<c:if test="${info == true}">
					<p class="comentari col-xs-12 col-xs-offset-">${campInfoText}</p>
				</c:if>
			</div>
			<c:if test="${fileEntitat}">
				<label class="control-label col-xs-${campLabelSize}" for="${campPath}"></label>
				<div class="col-xs-8 img-exists">
					<div class="col-xs-4 icon">
						<c:choose>
							<c:when test="${logoMenu}">
								<a href="<c:url value="/entitat/getEntitatLogoCap"/>"><spring:message code="entitat.form.camp.conf.logocapactual"/></a>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/entitat/getEntitatLogoPeu"/>"><spring:message code="entitat.form.camp.conf.logopeuactual"/></a>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-xs-4">
						<c:choose>
							<c:when test="${logoMenu}">
								<form:checkbox path="eliminarLogoCap"/>
								<spring:message code="entitat.form.camp.conf.logo.eliminar" />
							</c:when>
							<c:otherwise>
								<form:checkbox path="eliminarLogoPeu"/>
								<spring:message code="entitat.form.camp.conf.logo.eliminar" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</c:if>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
    		<label class="sr-only" for="${campPath}">${campLabelText}</label>
    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
				<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
				<span class="input-group-addon btn btn-default btn-file"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span><input type="file" id="${campPath}" name="${campPath}"></span>
				<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
			</div>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			<c:if test="${info == true}">
				<p class="comentari col-xs-12 col-xs-offset-">${campInfoText}</p>
			</c:if>
  		</div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	let fileName = "${fileName}";
	if (fileName) {
		$fileinput = $(document.getElementById('${campPath}')).closest('.fileinput');
		$fileinput.removeClass('fileinput-new');
		$fileinput.addClass('fileinput-exists');
		$('.fileinput-filename', $fileinput).append(fileName);
	}
});
</script>

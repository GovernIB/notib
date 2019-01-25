<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="tipus" required="true" rtexprvalue="true"%>
<%@ attribute name="titol" required="true" rtexprvalue="true"%>
<%@ attribute name="optionItems" required="false" rtexprvalue="true"%>

<c:set var="campTipus" value="${tipus}"/>
<c:set var="campTitol" value="${titol}"/>

<div class="personaTitol">
	<div class="col-md-8">
		<div>
			<label class="text-primary">${campTitol}</label>
		</div>
	</div>
	<c:if test="${campTipus == 'destinataris'}">
	<div class="col-md-4">
		<div class="text-right">
			<input type="button" class="btn btn-default" id="addDestinatri" value="<spring:message code="notificacio.form.boto.nou.destinatari"/>"/>
		</div>
	</div>
	</c:if>
	<div class="col-md-12 separacio"></div>
</div>
<div>
<c:if test="${campTipus == 'titular'}">
	<div class="personaForm destinatariForm">
		<div class="col-md-6">
			<not:inputText name="titular.nif" textKey="notificacio.form.camp.${campTipus}.nif" required="true" />
		</div>
		<div class="col-md-6">
			<not:inputText name="titular.nom" textKey="notificacio.form.camp.${campTipus}.nom" required="true" />
		</div>
		<div class="col-md-6">
			<not:inputText name="titular.llinatge1" textKey="notificacio.form.camp.${campTipus}.llinatge1" required="true" />
		</div>
		<div class="col-md-6">
			<not:inputText name="titular.llinatge2" textKey="notificacio.form.camp.${campTipus}.llinatge2" />
		</div>
		<div class="col-md-6">
			<not:inputText name="titular.email" textKey="notificacio.form.camp.${campTipus}.email" />
		</div>
		<div class="col-md-6">
			<not:inputText name="titular.telefon" textKey="notificacio.form.camp.${campTipus}.telefon" />
		</div>
		<div class="col-md-6">
			<not:inputText name="titular.dir3codi" textKey="notificacio.form.camp.${campTipus}.dir3codi" />
		</div>
		<div class="col-md-12">
			<hr>
		</div>
	</div>
</c:if>
</div>




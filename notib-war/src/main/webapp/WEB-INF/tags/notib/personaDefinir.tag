<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="tipus" required="true" rtexprvalue="true"%>
<%@ attribute name="titol" required="true" rtexprvalue="true"%>
<c:set var="campTipus" value="${tipus}"/>
<c:set var="campTitol" value="${titol}"/>

<div class="col-md-12">
	<div class="col-md-6">
		<label>${campTitol}</label>
	</div>
	<c:if test="${campTipus == 'destinatari'}">
		<div class="col-md-6">
			<input id="addDestinatariButton" type="button" class="btn btn-light" value="Afegir"/>
		</div>
	</c:if>
</div>

<div class="<c:if test="${campTipus == 'destinatari'}">personaForm</c:if> col-md-12">
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.nif" textKey="notificacio.form.camp.${campTipus}.nif" required="true" />
	</div>
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.nom" textKey="notificacio.form.camp.${campTipus}.nom" required="true" />
	</div>
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.llinatge1" textKey="notificacio.form.camp.${campTipus}.llinatge1" required="true" />
	</div>
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.llinatge2" textKey="notificacio.form.camp.${campTipus}.llinatge2" />
	</div>
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.email" textKey="notificacio.form.camp.${campTipus}.email" />
	</div>
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.telefon" textKey="notificacio.form.camp.${campTipus}.telefon" />
	</div>
	<div class="col-md-6">
		<not:inputText name="enviament.${campTipus}.dir3codi" textKey="notificacio.form.camp.${campTipus}.dir3codi" />
	</div>
</div>


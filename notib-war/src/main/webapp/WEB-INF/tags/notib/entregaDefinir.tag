<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="titol" required="true" rtexprvalue="true"%>

<c:set var="campTitol" value="${titol}"/>

<div class="col-md-12">
	<div class="col-md-6 text-primary">
		<label>${campTitol}</label>
	</div>
</div>

<div class="col-md-12">
	<div class="col-md-12">
		<not:inputSelect name="entregaPostal.tipus" textKey="notificacio.form.camp.entregapostal.tipus" required="true" labelSize="2"/>
	</div>
	<div class="col-md-6">
		<not:inputSelect name="entregaPostal.tipusVia" textKey="notificacio.form.camp.entregapostal.tipusvia" required="true" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.viaNom" textKey="notificacio.form.camp.entregapostal.vianom" required="true" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.numeroCasa" textKey="notificacio.form.camp.entregapostal.numerocasa" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.portal" textKey="notificacio.form.camp.entregapostal.portal" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.escala" textKey="notificacio.form.camp.entregapostal.escala" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.planta" textKey="notificacio.form.camp.entregapostal.planta" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.porta" textKey="notificacio.form.camp.entregapostal.porta" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.bloc" textKey="notificacio.form.camp.entregapostal.bloc" />
	</div>
	<div class="col-md-12">
		<not:inputText name="entregaPostal.complement" textKey="notificacio.form.camp.entregapostal.complement" labelSize="2"/>
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.codiPostal" textKey="notificacio.form.camp.entregapostal.codipostal" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.poblacio" textKey="notificacio.form.camp.entregapostal.poblacio" />
	</div>
	<div class="col-md-12">
		<not:inputText name="entregaPostal.paisCodi" textKey="notificacio.form.camp.entregapostal.paiscodi" labelSize="2" inputSize="4"/>
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.formatSobre" textKey="notificacio.form.camp.entregapostal.formatsobre" />
	</div>
	<div class="col-md-6">
		<not:inputText name="entregaPostal.formatFulla" textKey="notificacio.form.camp.entregapostal.formatfulla" />
	</div>
</div>


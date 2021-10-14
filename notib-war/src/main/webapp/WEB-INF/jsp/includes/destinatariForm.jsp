<%--
  Created by IntelliJ IDEA.
  User: Limit Tecnologies <limit@limit.es>
  Date: 27/8/21
  Time: 15:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="destinatari">
    <div class="col-md-12 title-envios">
        <div class="title-container">
            <label id="labelDestinataris"> ${destinatarisTitol} </label>
        </div>
        <hr/>
    </div>
    <div class="newDestinatari_${j} dest">
        <c:if test="${!empty enviament.destinataris}">
            <c:set value="${enviament.destinataris}" var="destinataris"></c:set>
            <c:forEach items="${destinataris}" var="destinatari" varStatus="status">
                <c:set var="i" value="${status.index}" />
                <div class="col-md-12 destinatariForm destenv_${j} personaForm_${j}_${i}">
                        <%-- 												<input id="isMultiple" class="hidden" value="${isMultiplesDestinataris}"> --%>
                    <input type="hidden" name="enviaments[${j}].destinataris[${i}].id" value="${destinatari.id}"/>
                    <!-- TIPUS INTERESSAT -->
                    <div class="col-md-3 interessatTipus">
                        <not:inputSelect name="enviaments[${j}].destinataris[${i}].interessatTipus" generalClass="interessat" textKey="notificacio.form.camp.interessatTipus" labelSize="12" inputSize="12" optionItems="${interessatTipus}" optionValueAttribute="value" optionTextKeyAttribute="text" />
                    </div>
                    <!-- NIF -->
                    <div class="col-md-3 nif">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].nif" textKey="notificacio.form.camp.titular.nif" labelSize="12" inputSize="12" />
                    </div>
                    <!-- NOM / RAÓ SOCIAL -->
                    <div class="col-md-3 rao">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].nom" textKey="notificacio.form.camp.titular.nom" labelSize="12" inputSize="12" required="true" inputMaxLength="${concepteSize}" showsize="true"/>
                    </div>
                    <!-- PRIMER LLINATGE -->
                    <div class="col-md-3 llinatge1">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].llinatge1"
                                       textKey="notificacio.form.camp.titular.llinatge1"
                                       labelSize="12" inputSize="12" required="true"
                                       inputMaxLength="${concepteSize}" showsize="true"/>
                    </div>
                    <!-- SEGON LLINATGE -->
                    <div class="col-md-3 llinatge2">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].llinatge2" textKey="notificacio.form.camp.titular.llinatge2" labelSize="12" inputSize="12" inputMaxLength="${concepteSize}" showsize="true"/>
                    </div>
                    <!-- TELÈFON -->
                    <div class="col-md-3">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].telefon" textKey="notificacio.form.camp.titular.telefon" labelSize="12" inputSize="12" inputMaxLength="${concepteSize}" showsize="true"/>
                    </div>
                    <!-- EMAIL -->
                    <div class="col-md-4">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].email" textKey="notificacio.form.camp.titular.email" labelSize="12" inputSize="12" inputMaxLength="${concepteSize}" showsize="true"/>
                    </div>
                    <!-- CODI DIR3 -->
                    <div class="col-md-3 dir3Codi hidden">
                        <not:inputTextSearch  funcio="obrirModalOrganismesDestinatari(${j},${i}, '${urlOrganigrama}', '${urlComunitatsAutonomes}','${urlNivellAdministracions}','${urlCercaUnitats}')" searchButton="searchOrgan${j}${i}" textKey="notificacio.form.camp.titular.dir3codi" labelSize="12" inputSize="12" readonly="true" value="${destinatari.dir3Codi}-${destinatari.nom}"/>
                    </div>
                    <div class="col-md-3 hidden">
                        <not:inputText name="enviaments[${j}].destinataris[${i}].dir3Codi" textKey="notificacio.form.camp.titular.dir3codi" labelSize="12" inputSize="12"/>
                    </div>
                    <!-- ELIMINAR DESTINATARI -->
                    <div class="col-md-2 offset-col-md-2">
                        <div class="float-right">
                            <input type="button" class="btn btn-danger btn-group delete" name="destinatarisDelete[${j}][${i}]" onclick="destinatarisDelete(this.id)" id="destinatarisDelete[${j}][${i}]" value="<spring:message code="notificacio.form.boto.eliminar.destinatari"/>"/>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <hr style="border-top: 1px dotted #BBB">
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>

    <c:set var="addHidden" value="${isMultiplesDestinataris || empty enviament.destinataris}"/>
    <!-- AFEGIR NOU DESTINATARI -->
    <div class="col-md-12">
        <div class="text-left">
            <input type="button" class="btn btn-default addDestinatari<c:if test="addHidden"> hidden</c:if>"
                   name="enviaments[${j}]" id="enviaments[${j}]"
                   onclick="addDestinatari(this.id, ${isMultiplesDestinataris}, destinatariHTMLTemplate)"
                   value="<spring:message code="notificacio.form.boto.nou.destinatari"/>" />
        </div>
    </div>

</div>
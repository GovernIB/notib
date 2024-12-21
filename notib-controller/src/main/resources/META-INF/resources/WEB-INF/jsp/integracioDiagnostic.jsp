<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="integracio.diagnostic.titol"/></c:set>
<html>
<head>
    <title>${titol}</title>
    <not:modalHead/>

    <script>
        $(document).ready(function() {
            diagnostic();
            $('button[name=btnRefrescarDiagnostic]').click(() => {
                diagnostic();
                return false;
            });
        })

        function diagnostic() {
            let integracions = $(".integracio");
            for(let i=0; i<integracions.length; i++) {
                let integracio = integracions.eq(i).data('codi');
                $("#span-refresh-" + integracio).empty().addClass('fa-circle-o-notch');
                $("#span-refresh-" + integracio).addClass('fa-spin');
                $("#integracio_" + integracio + "_info").empty();
                $("#span-refresh-" + integracio).removeClass("fa-check");
                $("#span-refresh-" + integracio).removeClass("fa-times");
                $("#span-refresh-" + integracio).removeClass("text-success");
                $("#span-refresh-" + integracio).removeClass("text-danger");
                $.ajax({
                    method: "GET",
                    url: "<c:url value='/integracio/diagnostic'/>/" + integracio,
                    async: true,
                    success: function(data){
                        $("#span-refresh-" + integracio).removeClass('fa-circle-o-notch');
                        $("#span-refresh-" + integracio).removeClass('fa-spin');
                        $("#span-refresh-" + integracio).removeClass('fa-refresh');
                        let clase;
                        let textNode;
                        console.log("int " + integracio);
                        console.log(data);
                        if (!data.diagnosticsEntitat || Object.keys(data.diagnosticsEntitat).length === 0) {
                            if (data.correcte) {
                                clase = "fa-check text-succes";
                                textNode = document.createTextNode("    " + data.prova);
                            } else {
                                clase = "fa-times text-danger";
                                textNode = document.createTextNode("    " + data.errMsg);
                            }
                            $("#span-refresh-" + integracio).addClass(clase);
                            $('#integracio_' + integracio + '_info').append(textNode);
                        } else {
                            const map = new Map(Object.entries(data.diagnosticsEntitat));
                            $('#integracio_' + integracio + '_info').append(document.createTextNode(data.prova));
                            map.forEach((diagnostic, codi) => {
                                let div = crearDiagnosticInfo(diagnostic, codi)
                                $('#integracio_' + integracio + '_info').append(div);
                            });
                        }
                    }
                });
            }
        }

        function crearDiagnosticInfo(diagnostic, codi) {

            let div = document.createElement("div");
            let span = document.createElement("span");
            let clase;
            let textNode;
            if (diagnostic && diagnostic.correcte) {
                clase = "fa fa-check text-succes";
                textNode = document.createTextNode("    " + codi);
            } else if (diagnostic.errMsg) {
                clase = "fa fa-times text-danger";
                textNode = document.createTextNode("    " + codi + "    " + diagnostic.errMsg);
            } else {
                textNode = document.createTextNode("    " + codi);
            }
            let p = document.createElement("p");
            $(span).addClass(clase);
            p.append(span);
            if (clase) {
                p.append(textNode);
            } else {
                let b = document.createElement("b");
                b.append(textNode)
                p.append(b);
            }
            div.append(p);
            if (!diagnostic.diagnosticsEntitat) {
                return div;
            }
            const map = new Map(Object.entries(diagnostic.diagnosticsEntitat));
            map.forEach((diagnostic, codi) => {
                let divEntitat = crearDiagnosticInfo(diagnostic, codi)
                div.append(divEntitat);
            });
            return div;
        }
    </script>

</head>
<body>
    <ul class="nav nav-tabs" role="tablist">
        <c:forEach var="integracio" items="${integracions}">
            <c:if test="${not empty integracio and integracio.codi != 'EMAIL'}">
                <dl class="dl-horizontal">
                    <dt class="integracio" id="integracio_${integracio.codi}" data-codi="${integracio.codi}"><spring:message code="${integracio.nom}"/></dt>
                    <dd><span id="span-refresh-${integracio.codi}" class="ml-2 fa fa-refresh "></span>
                        <p id="integracio_${integracio.codi}_info" style="display:inline;"></p></dd>
                </dl>
            </c:if>
        </c:forEach>
    </ul>
    <div id="modal-botons">
        <button name="btnRefrescarDiagnostic" type="button" id="btnRefrescarDiagnostic" class="btn btn-success"> <span class="fa fa-refresh"></span> <spring:message code="comu.boto.refrescar"/> </button>
        <a href="<c:url value="/integracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
    </div>

</body>
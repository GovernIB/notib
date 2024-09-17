<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
<head>
    <title><spring:message code='decorator.menu.recording' /></title>
    <not:modalHead/>
</head>
<body>
<button id="startButton" onclick="startJFR()">Start</button>
<button id="stopButton" style="display:none;" onclick="stopJFR()">Stop</button>
<a id="descargarButton" href="<c:url value="/recording/download"/>" >Descarregar</a>
<pre id="analysisResult" class="hidden"></pre>

<script>
    let recording = "${isRecording}";

    $(document).ready(function() {

        console.log("recording" + recording);
        if (recording === "Started") {
            $("#stopButton").toggle();
            $("#startButton").toggle();
        }
    });

    function startJFR() {
        $.get("<c:url value="/recording/start"/>", function(data) {
            if (data === "Started") {
                recording = true;
                $("#stopButton").toggle();
                $("#startButton").toggle();
            }
        });
    }

    function stopJFR() {
        $.get("<c:url value="/recording/stop"/>", function(data) {
            if (data === "Stopped") {
                recording = false;
                $("#startButton").toggle();
                $("#stopButton").toggle();
            }
        });
    }

    <%--function downloadJFR() {--%>
    <%--    $.get("<c:url value="/recording/download"/>", function(data) {--%>
    <%--        window.location.href = data;--%>
    <%--    });--%>
    <%--}--%>

</script>
<div id="modal-botons" class="well">
    <button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
</div>
</body>
</html>

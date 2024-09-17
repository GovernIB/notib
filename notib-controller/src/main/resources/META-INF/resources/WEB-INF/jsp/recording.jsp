<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/notib" prefix="not"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
<head>
    <title><spring:message code='decorator.menu.recording' /></title>
    <not:modalHead/>
    <style type="text/css">
        body.loading {
            overflow: hidden;
        }
        body.loading .wait {
            display: block;
        }
        .table > tbody > tr > td {
            margin-bottom: 0px;
            margin-top: 0px !important;
            padding-bottom: 0px;
            padding-top: 0px;
            border-bottom: 1px solid #cccccc;
            overflow: auto;
            padding: 2px 10px !important;
        }
        .table {
            table-layout:fixed;
            border-collapse: collapse;
        }

        .table td {
            text-overflow:ellipsis;
            overflow:hidden;
            white-space:nowrap;
        }

        .monitor_hilo {
            width: 650px;
        }

        .contingut-carregant {
            text-align: center;
            padding: 8px;
        }

        .min_width {
            width: 95px;
        }

        .top-buffer {
            margin-top: 10px;
        }

    </style>

</head>
<body>
<button id="jfrButton" onclick="toggleJFR()">Start/Stop</button>
<div id="counter">0</div>
<button id="analyzeButton" class="hidden" onclick="analyzeJFR()">Info</button>
<pre id="analysisResult" class="hidden"></pre>

<script>
    var recording = false;
    var count = 0;
    var counterInterval;

    function toggleJFR() {
        if (!recording) {
            startJFR();
        } else {
            stopJFR();
        }
    }

    function startJFR() {
        $.get("<c:url value="/recording/start"/>", function(data) {
            if (data === "Started") {
                recording = true;
                startCounter();
            }
        });
    }

    function stopJFR() {
        $.get("<c:url value="/recording/stop"/>", function(data) {
            if (data === "Stopped") {
                recording = false;
                stopCounter();
                $("#downloadButton").removeClass("hidden");
                $("#analyzeButton").removeClass("hidden");
            }
        });
    }

    function startCounter() {
        counterInterval = setInterval(function() {
            count++;
            $("#counter").text(count);
        }, 1000);
    }

    function stopCounter() {
        clearInterval(counterInterval);
    }

    function analyzeJFR() {
        $.get("<c:url value="/recording/info"/>", function(data) {
            $("#analysisResult").text(data).removeClass("hidden");
        });
    }
</script>
<div id="modal-botons" class="well">
    <button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
</div>
</body>
</html>

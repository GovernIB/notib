
function guardarFilesSeleccionades(table) {
    var idsSelectedRows = sessionStorage.getItem('rowIdsStore');
    if (!(idsSelectedRows))
        return;

    var rowids = JSON.parse(idsSelectedRows);
    for (var id in rowids) {
        var selectedRowId = document.getElementById(id);
        var $cell = $('td:first', $(selectedRowId));
        $(selectedRowId).addClass('selected');
        $cell.empty().append('<span class="fa fa-check-square-o"></span>');
    }
}

function seleccionarFila(id) {
    var isSelected = $(document.getElementById(id)).hasClass('selected')
    var idsSelectedRows = sessionStorage.getItem('rowIdsStore');
    if (!(idsSelectedRows)) {
        clearSeleccio();
    }
    var rowKeys = JSON.parse(idsSelectedRows);
    if (isSelected === false && rowKeys.hasOwnProperty(id)) {
        delete rowKeys[id];
    } else if (isSelected) {
        rowKeys[id] = true;
    }
    sessionStorage.setItem('rowIdsStore', JSON.stringify(rowKeys));
}



function clearSeleccio() {
    sessionStorage.setItem('rowIdsStore', "{}");
}

let deseleccionar = () => {
    $('#seleccioNone').click();
    $(".seleccioCount").html(0);
};

let selectionAll = false;
function initEvents($table, url_prefix, eventMessages) {
    $table.on('selectionchange.dataTable', function (e, accio, ids) {
        if (!selectionAll && (accio === "select" || accio === "deselect")) {
            $.get(
                url_prefix + "/" + accio,
                {ids: ids},
                function (data) {
                    $(".seleccioCount").html(data);
                }
            );
        }
    });

    $table.on("draw.dt", () => {
        selectionAll = false;
    });

    $table.on('init.dt', function () {

        $('#seleccioAll').on('click', function() {
            $('#seleccioAll').attr("disabled", true);
            $('#seleccioNone').attr("disabled", true);
            $('#cover-spin').show(0);
            $.get(
                url_prefix + "/seleccionar/all",
                indexes => {
                    $(".seleccioCount").html(indexes);
                    $table.webutilDatatable('select-all');
                    $('#seleccioAll').attr("disabled", false);
                    $('#seleccioNone').attr("disabled", false);
                    $('#cover-spin').hide();
                }
            );
            return false;
        });
        $('#seleccioNone').on('click', function() {
            $('#seleccioAll').attr("disabled", true);
            $('#seleccioNone').attr("disabled", true);
            $('#cover-spin').show(0);
            $.get(
                url_prefix + "/deselect",
                function(data) {
                    $(".seleccioCount").html(data);
                    $table.webutilDatatable('select-none');
                    $table.webutilDatatable('refresh');
                    $('#seleccioAll').attr("disabled", false);
                    $('#seleccioNone').attr("disabled", false);
                    $('#cover-spin').hide();
                }
            );
            return false;
        });
        $('#btnNetejar').click(function() {
            $(':input').val('');
            event.preventDefault();
            $("#btnFiltrar").first().click();
        });

        $('#reintentarNotificacio').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-reintentar-notificacio'])){

                $.get(
                    url_prefix + "/reintentar/notificacio",
                    () => {
                        $table.DataTable().rows().deselect();
                        $table.DataTable().ajax.reload(null, false);
                        webutilRefreshMissatges();
                    }
                );

                webutilRefreshMissatges(); // mostra el missatge de que s'està executant el procés en segon plà
            }
            return false;
        });

        $('#reintentarErrors').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-reintentar-errors'])){

                $.get(
                    url_prefix + "/reactivar/notificacionsError",
                    () => {
                        $table.DataTable().rows().deselect();
                        $table.DataTable().ajax.reload(null, false);
                        webutilRefreshMissatges();
                    }
                );

                webutilRefreshMissatges(); // mostra el missatge de que s'està executant el procés en segon plà
            }
            return false;
        });

        $('#reactivarConsulta').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-reintentar-consulta'])){
                location.href = url_prefix + "/reactivar/consulta";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#reactivarSir').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-reintentar-sir'])){
                $.get(
                    url_prefix + "/reactivar/sir",
                    () => {
                        $table.DataTable().ajax.reload(null, true);
                        webutilRefreshMissatges();
                    }
                );
                // location.href =  url_prefix + "/reactivar/sir";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#updateEstat').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-update-estat'])){
                $.get(
                    url_prefix + "/actualitzarestat",
                    () => {
                        $table.DataTable().ajax.reload(null, true);
                        webutilRefreshMissatges();
                    }
                );
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
                webutilRefreshMissatges(); // mostra el missatge de que s'està executant el procés en segon plà
            }
            return false;
        });

        $('#reactivarCallback').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-reactivar-callback'])){
                location.href =  url_prefix + "/reactivar/callback";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#enviarCallbacks').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-enviar-callback'])){
                location.href =  url_prefix + "/enviar";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#pausarCallbacks').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-enviar-callback'])){
                location.href =  url_prefix + "/pausar";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#activarCallbacks').on('click', function() {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            if(confirm(eventMessages['confirm-enviar-callback'])){
                location.href =  url_prefix + "/activar";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $("#exportarODS").on("click", (e) => {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            // location.href = "notificacio/export/ODS";
            location.href = url_prefix + "/export/ODS";
            setTimeout(() => $table.DataTable().rows().deselect(), 100);
        });

        $("#eliminar").on("click", () => {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            location.href = url_prefix + "/eliminar";
        });

        $("#descarregarJustificantMassiu").on("click", () => {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            location.href = url_prefix + "/descarregar/justificant/massiu";
        });

        $("#descarregarCertificacioMassiu").on("click", () => {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            location.href = url_prefix + "/descarregar/certificacio/massiu";
        });

        $("#reactivarRegistre").on("click", () => {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            location.href =  url_prefix + "/reactivar/registre";
            setTimeout(() => $table.DataTable().rows().deselect(), 100);
        });

        $("#enviarNotificacionsMovil").on("click", () => {

            let count = Number($(".seleccioCount").html());
            if (count == 0 || count > 100 && !confirm(eventMessages["confirm-accio-massiva"])) {
                return;
            }
            location.href =  url_prefix + "/enviar/notificacio/movil";
            setTimeout(() => $table.DataTable().rows().deselect(), 100);
        });

    });
}
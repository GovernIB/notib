
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



function initEvents($table, url_prefix, eventMessages) {

    $table.on('selectionchange.dataTable', function (e, accio, ids) {
        if (accio === "select" || accio === "deselect") {
            $.get(
                url_prefix + "/" + accio,
                {ids: ids},
                function (data) {
                    $(".seleccioCount").html(data);
                }
            );
        }
    });

    $table.on('init.dt', function () {
        $('#seleccioAll').on('click', function() {
            $.get(
                url_prefix + "/select",
                function(data) {
                    $(".seleccioCount").html(data);
                    $table.webutilDatatable('refresh');
                }
            );
            return false;
        });
        $('#seleccioNone').on('click', function() {
            $.get(
                url_prefix + "/deselect",
                function(data) {
                    $(".seleccioCount").html(data);
                    $table.webutilDatatable('select-none');
                    $table.webutilDatatable('refresh');
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
            if(confirm(eventMessages['confirm-reintentar-consulta'])){
                location.href = url_prefix + "/reactivar/consulta";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#reactivarSir').on('click', function() {
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
            if(confirm(eventMessages['confirm-reactivar-callback'])){
                location.href =  url_prefix + "/reactivar/callback";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $('#enviarCallback').on('click', function() {
            if(confirm(eventMessages['confirm-enviar-callback'])){
                location.href =  url_prefix + "/enviar/callback";
                setTimeout(() => $table.DataTable().rows().deselect(), 100);
            }
            return false;
        });

        $("#exportarODS").on("click", () => {
            // location.href = "notificacio/export/ODS";
            location.href = url_prefix + "/export/ODS";
            setTimeout(() => $table.DataTable().rows().deselect(), 100);
        });

        $("#eliminar").on("click", () => {
            location.href = url_prefix + "/eliminar";
        });
        $("#reintentarRegistre").on("click", () => {
            location.href =  url_prefix + "/reintentar/registre";
            setTimeout(() => $table.DataTable().rows().deselect(), 100);
        });
    });
}
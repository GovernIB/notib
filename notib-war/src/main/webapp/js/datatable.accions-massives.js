
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
        console.debug(accio);
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

        $('#reactivarConsulta').on('click', function() {
            if(confirm(eventMessages['confirm-reintentar-consulta'])){
                location.href = url_prefix + "/reactivar/consulta";
                $table.DataTable().rows().deselect();
            }
            return false;
        });

        $('#reactivarSir').on('click', function() {
            if(confirm(eventMessages['confirm-reintentar-sir'])){
                location.href =  url_prefix + "/reactivar/sir";
                $table.DataTable().rows().deselect();
            }
            return false;
        });
        $('#updateEstat').on('click', function() {
            if(confirm(eventMessages['confirm-update-estat'])){
                $.get(
                    url_prefix + "/actualitzarestat",
                    () => {
                        $table.DataTable().rows().deselect();
                        $table.DataTable().ajax.reload(null, true);
                        webutilRefreshMissatges();
                    }
                );
                webutilRefreshMissatges(); // mostra el missatge de que s'està executant el procés en segon plà
            }
            return false;
        });

        $('#reactivarCallback').on('click', function() {
            if(confirm(eventMessages['confirm-reactivar-callback'])){
                location.href =  url_prefix + "/reactivar/callback";
                $table.DataTable().rows().deselect();
            }
            return false;
        });

        $("#exportarODS").on("click", () => {
            location.href = "notificacio/export/ODS";
            $table.DataTable().rows().deselect();
        });

        $("#eliminar").on("click", () => {
            location.href = "/notificacio/eliminar";
            $table.DataTable().rows().deselect();
        });
        $("#reintentarRegistre").on("click", () => {
            location.href =  url_prefix + "/reintentar/registre";
            $table.DataTable().rows().deselect();
        });
    });
}
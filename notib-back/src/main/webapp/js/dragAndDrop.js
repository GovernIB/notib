function dropHandler(ev, id) {

    // Evitar el comportament per defecte (Evitar que el s'obri/executi)
    ev.preventDefault();
    if (ev.dataTransfer.items) {
        // Usar la interfaz DataTransferItemList para acceder a el/los archivos)
        for (let i = 0; i < ev.dataTransfer.items.length; i++) {
            // Si los elementos arrastrados no son ficheros, rechazarlos
            if (ev.dataTransfer.items[i].kind === "file") {
                let file = ev.dataTransfer.items[i].getAsFile();
                let input = document.getElementById(id);
                input.files = ev.dataTransfer.files;
                actualitzarInput(input, file.name);
            }
        }
    } else {
        // Usar la interfaz DataTransfer para acceder a el/los archivos
        for (let i = 0; i < ev.dataTransfer.files.length; i++) {
            let input = document.getElementById(id);
            input.files = ev.dataTransfer.files;
            actualitzarInput(input, ev.dataTransfer.files[i].name)
        }
    }

    // Pasar el evento a removeDragData para limpiar
    removeDragData(ev);
    event.target.closest(".dropzone").classList.remove("dropHighlight");
}

function actualitzarInput(input, name) {

    let id = "#tipusDocument_" + input.id.split("[")[1].substr(0,1);
    $(id).val("ARXIU");
    $(id).change();
    let div = input.closest(".fileinput");
    div.closest(".fileinput").classList.remove("fileinput-new");
    div.closest(".fileinput").classList.add("fileinput-exists");
    $('.fileinput-filename', div).text(name);
    const event = new Event('change');
    input.dispatchEvent(event);
}

function dragOverHandler(ev) {
    // Prevent default behavior (Prevent file from being opened)
    ev.preventDefault();
}

function removeDragData(ev) {
    if (ev.dataTransfer.items) {
        // Use DataTransferItemList interface to remove the drag data
        ev.dataTransfer.items.clear();
    } else {
        // Use DataTransfer interface to remove the drag data
        ev.dataTransfer.clearData();
    }
}

function highlight(event) {
    event.preventDefault();
    event.target.closest(".dropzone").classList.add("dropHighlight");
}

function removeHighlight(event) {
    // Check if the mouse is still within the dropzone
    let rect = event.target.closest(".dropzone").getBoundingClientRect();
    let x = event.clientX;
    let y = event.clientY;
    if (x < rect.left || x > rect.right || y < rect.top || y > rect.bottom) {
        event.target.closest(".dropzone").classList.remove("dropHighlight");
    }
}



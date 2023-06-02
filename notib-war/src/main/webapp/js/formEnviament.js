//Reset de los warning, errores que se muestran tras la consulta al arxiu para CSV y Uuid
//o cuando no se introduce documento ya que es obligatorio
function resetWarningIErrorsDocArxiu(indexId){
    $('#document_err_'+indexId).remove();
    $('#metadades_war_'+indexId).remove();
    $('#id_err_'+indexId).remove();
    let inputElementCsv = $("#documentArxiuCsv\\[" + indexId + "\\]");
    let inputElementUuid = $("#documentArxiuUuid\\[" + indexId + "\\]");
    inputElementCsv.parent().closest('.form-group').removeClass('has-error');
    inputElementCsv.removeClass('warningClass');
    inputElementCsv.parent().closest('.form-group').find("p.help-block").remove();
    inputElementUuid.parent().closest('.form-group').removeClass('has-error');
    inputElementUuid.removeClass('warningClass');
    inputElementUuid.parent().closest('.form-group').find("p.help-block").remove();
    let inputElementArxiu = $("#arxiu\\[" + indexId + "\\]");
    inputElementArxiu.parent().closest('.form-group').removeClass('has-error');
    inputElementArxiu.parent().closest('.form-group').find("p.help-block").remove();
}

function activarCampsMetadades(indexId){
    $("#documents\\[" +indexId+ "\\]\\.origen").prop('disabled', false);
    $("#documents\\[" +indexId+ "\\]\\.validesa").prop('disabled', false);
    $("#documents\\[" +indexId+ "\\]\\.tipoDocumental").prop('disabled', false);
    $("#documents\\[" +indexId+ "\\]\\.modoFirma").prop('disabled', false);
}


var t, makeTooltip = function(warning) {
    var pos = $("#descripcio").position();
    clearTimeout(t);
    $('#tooltip').remove();
    $('<p id="tooltip">' + warning + '</p>').insertBefore("#descripcio");
    $('#tooltip').css({
        top : pos.top - 30,
        left: pos.left
    }).fadeIn("fast");

    t = setTimeout(function() {
        $('#tooltip').fadeOut(300, function() {
            $(this).remove();
        });
    }, 4000);
};

function loadOrganigrama(urlOrganigrama){
    $.ajax({
        type: 'GET',
        url: urlOrganigrama + document.getElementById('emisorDir3Codi').value,
        success: function(data) {
            if (Object.keys(data).length > 0) {
                $("#organigrama").val(cercaCodiEnOrganigrama(data));
            }
        },
        error: function() {
            console.log("error obtenint l'organigrama...");
        }
    });
}


function replaceAll(string, search, replace) {
    return string.split(search).join(replace);
}

function addEnvio(urlOrganigrama,
                  urlComunitatsAutonomes,
                  urlNivellAdministracions,
                  urlCercaUnitats,
                  urlPaisos, urlProvincies, urlLocalitats,
                  personaMaxSizes) {
    var number;
    var num;
    var numPlus
    var enviamentForm = $(".enviamentsForm").last().clone();

    var classList = enviamentForm.attr('class').split(/\s+/);
    $.each(classList, function(index, classname) {
        if (classname.startsWith('enviamentForm_')) {
            number = classname.substring(14);
            num = parseInt(number);
        }
    });

    //Titol enviament
    if (num != null) {
        numPlus = num + 1;
        enviamentForm.removeClass('enviamentForm_' + num).addClass('enviamentForm_' + numPlus);
        var badge = enviamentForm.find('.envio\\['+numPlus+'\\]');
        badge.removeClass('envio[' + numPlus + ']').addClass('envio[' + (numPlus + 1) + ']');
        badge[0].innerText = "Enviament " + (numPlus + 1);
        var destinataris = enviamentForm.find('.newDestinatari_' + num);
        destinataris.removeClass('newDestinatari_' + num).addClass('newDestinatari_' + numPlus);
        destinataris.empty();
        enviamentForm.find(':input').each(function() {
            this.name= this.name.replace(number,numPlus);
            this.id= this.id.replace(number,numPlus);
            this.value = this.value.replace(number,numPlus);
            $(this).attr('data-select2-id', numPlus);

            if($(this).attr("id") == "envioTooltip") {
                this.value= this.value.replace(number,numPlus);
                $(this).tooltip();
            }

            if($(this).hasClass('formEnviament')) {
                this.name= this.name.replace("[" + number, "[" + numPlus);
                this.id= this.id.replace("[" + number, "[" + numPlus);
            }

            if ($(this).attr('type') == 'hidden') {
                var hiddenId = parseInt($(this).val());
                if (typeof hiddenId == 'number') {
                    $(this).val(''); //remove hidden id (new)
                }
            }
        });
        enviamentForm.find('#entregaPostal').removeClass('entregaPostal_' + num).addClass('entregaPostal_' + numPlus);
        var searchOrgan = enviamentForm.find('#searchOrganTit' + number);
        searchOrgan.attr("id", "searchOrganTit" + numPlus);
        searchOrgan[0].onclick = null;
        searchOrgan.click(function() {
            obrirModalOrganismes('Tit-' + numPlus, urlOrganigrama, urlComunitatsAutonomes, urlNivellAdministracions, urlCercaUnitats);
        });

        //select
        $(enviamentForm).find("span.select2").remove();
        $(enviamentForm).find('p').remove();
        $(enviamentForm).find('div').removeClass('has-error');
        $(enviamentForm).find("select").select2({theme: 'bootstrap', width: 'auto'});
        $(enviamentForm).find("select").attr('data-select2-eval', 'true');
        $(enviamentForm).appendTo(".newEnviament").slideDown("slow").find("input[type='text']").not(".procedimentcodi").val("");

        //Remove last button addEnviament
        if($(enviamentForm).find('.eliminar_enviament').attr('id') != 'entregaPostal[0]') {
            $(enviamentForm).find('.eliminar_enviament').removeClass('hidden');
        }
        //Show button addDestinatari
        $(enviamentForm).find('.addDestinatari').removeClass('hidden');
        //Inicialitzar chechbox incapacitat
        $(enviamentForm).find('input:checkbox').removeAttr('checked');
        //Inicialitzar deh
        $(enviamentForm).find('.entregaDeh_'+numPlus).hide();
        //Inicialitzar entregapostal
        $(enviamentForm).find('.entregaPostal_'+numPlus).hide();
        $(enviamentForm).find('.entregaPostal_info_'+number).css('display','none');

        actualitzarEntrega(numPlus, urlPaisos, urlProvincies, urlLocalitats);
        webutilModalAdjustHeight();

        addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.nom', personaMaxSizes.nom);
        addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.llinatge1', personaMaxSizes.llinatge1);
        addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.llinatge2', personaMaxSizes.llinatge2);
        addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.email', personaMaxSizes.email);
        addContadorAddicionalEnviament('enviaments[' + numPlus + '].titular.telefon', personaMaxSizes.telefon);

        $('.interessat').trigger('change');
    }

}

function inputFieldAddCharsCounter(fieldId) {
    //Contador
    var fieldSize = 'inputCurrentLength_' + fieldId;
    var fieldSizeClass = $(document.getElementsByClassName(fieldSize)[0]);
    if (fieldSizeClass.val() != undefined && fieldSizeClass.val().length != 0) {
        var size = $(fieldId).val().length;
        $(fieldSizeClass).text(size);
    } else {
        $(fieldSizeClass).text(0);
    };

    $(document.getElementById(fieldId)).bind("change paste keyup", function() {
        var size = $(this).val().length;
        $(fieldSizeClass).text(size);
    });
}

function addContadorAddicionalEnviament(fieldId, inputMaxLength) {
    var p = '<p class="info-length text-success"> \
				<span class="glyphicon glyphicon-info-sign"></span> \
				<span class="inputCurrentLength_' + fieldId + '">0</span>'
				    + textMessages['notificacio.form.camp.logitud'] +
				'<span> ' + inputMaxLength + '</span> \
			</p>';
    var inputField = $(document.getElementById(fieldId));
    $(p).insertAfter(inputField);

    //Contador
    inputFieldAddCharsCounter(fieldId);
}

function destinatarisDelete(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".destinatariForm");
    var classParent = $(parent).attr('class');
    var destinatari_id_num = parseInt(className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']')));
    var enviament_id_num = parseInt(className.substring(className.indexOf('[') + 1, className.indexOf(']')));
    var destinatariRoot = $(element).closest(".dest");
    var numDest = destinatariRoot.find(".destinatariForm").size();

    $(parent).closest(".destinatari").find('.addDestinatari').removeClass('hidden');
    $(parent).remove();

    // Reanomenar destinataris posteriors
    if (numDest > (destinatari_id_num + 1)) {
        for(var i = (destinatari_id_num + 1); i < numDest; i++) {
            reanumeraDestinatari($(destinatariRoot).find('.destinatariForm:nth-child(' + i + ')'), i);
        }
    }
}

function enviamentDelete(id) {
    var element = document.getElementById(id);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');
    var enviament_id_num = parseInt(id.substring(16));
    var enviamentRoot = $(element).closest(".newEnviament");
    var numEnv = enviamentRoot.find(".enviamentsForm").size();

    $(parent).remove();

    // Reenumerar enviaments posteriors
    if (numEnv > (enviament_id_num + 1)) {
        for(var i = (enviament_id_num + 1); i < numEnv; i++) {
            reanumeraEnviament($(enviamentRoot).find('.enviamentsForm:nth-child(' + i + ')'), i);
        }
    }
}

function reanumeraDestinatari(destinatari, index) {
    var nouIndex = index - 1;

    var classList = destinatari.attr('class').split(/\s+/);
    $.each(classList, function(index, classname) {
        if (classname.startsWith('personaForm_')) {
            destinatari.removeClass(classname).addClass(classname.substring(0, classname.lastIndexOf('_') + 1) + nouIndex);
        }
    });

    destinatari.find(':input').each(function() {
        this.name= this.name.replace('destinataris[' + index,'destinataris[' +nouIndex);
        this.id= this.id.replace('destinataris[' + index,'destinataris[' + nouIndex);

        if($(this).hasClass('formEnviament')) {
            this.name= this.name.replace("[" + index, "[" + nouIndex);
            this.id= this.id.replace("[" + index, "[" + nouIndex);
        }
        if($(this).hasClass('delete')) {
            this.name= this.name.substring(0, this.name.lastIndexOf('[') + 1) + nouIndex + ']';
            this.id= this.id.substring(0, this.id.lastIndexOf('[') + 1) + nouIndex + ']';
        }
    });
}

function reanumeraEnviament(enviament, index) {
    var nouIndex = index - 1;
    enviament.removeClass('enviamentForm_' + index).addClass('enviamentForm_' + nouIndex);
    var badge = enviament.find('.envio\\[' + (index + 1) + '\\]');
    badge.removeClass('envio[' + (index + 1) + ']').addClass('envio[' + index + ']');
    badge[0].innerText = "Enviament " + index;
    var destinataris = enviament.find('.newDestinatari_' + index);
    destinataris.removeClass('newDestinatari_' + index).addClass('newDestinatari_' + nouIndex);
    destinataris.find('.destinatariForm').each(function(index) {
        $(this).removeClass('destenv_' + index).addClass('destenv_' + nouIndex);
        var classList = $(this).attr('class').split(/\s+/);
        $.each(classList, function(index, classname) {
            if (classname.startsWith('personaForm_')) {
                $(this).removeClass(classname).addClass('personaForm_' + nouIndex + classname.substring(classname.lastIndexOf('_'), classname.length));
            }
        });
    });
    enviament.find('#entregaPostal').removeClass('entregaPostal_' + index).addClass('entregaPostal_' + nouIndex);
    enviament.find(':input').each(function() {
        this.name= this.name.replace('enviaments[' + index,'enviaments[' +nouIndex);
        this.id= this.id.replace('enviaments[' + index,'enviaments[' + nouIndex);
        if ($(this).attr('data--id') == index) {
            $(this).attr('data-select2-id', nouIndex);
        }

        if($(this).hasClass('formEnviament')) {
            this.name= this.name.replace("[" + index, "[" + nouIndex);
            this.id= this.id.replace("[" + index, "[" + nouIndex);
        }
        if($(this).hasClass('delete')) {
            this.name= this.name.replace("destinatarisDelete[" + index, "destinatarisDelete[" + nouIndex);
            this.id= this.id.replace("destinatarisDelete[" + index, "destinatarisDelete[" + nouIndex);
        }
        if($(this).hasClass('eliminar_enviament')) {
            this.name= this.name.replace("enviamentDelete[" + index, "enviamentDelete[" + nouIndex);
            this.id= this.id.replace("enviamentDelete_" + index, "enviamentDelete_" + nouIndex);
        }
    });

//     if($(enviament).find('.eliminar_enviament').attr('id') != 'entregaPostal[0]') {
// 		$(enviament).find('.eliminar_enviament').removeClass('hidden');
//     }
// 	actualitzarEntrega(nouIndex);
}

function mostrarEntregaPostal(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');
    var concepteLength = $('#concepte').val().length;

    var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));

    if ($(element).is(':checked') && concepteLength > 50) {
        var longitidInfo = $('.entregaPostal_info_' + enviament_id_num);
        $(longitidInfo).slideDown(1000);
    };

    if($('.entregaPostal_'+enviament_id_num).css('display') != 'none') {
        $('.entregaPostal_'+enviament_id_num).hide();
    } else {
        $('.entregaPostal_'+enviament_id_num).show();
    }
}

function mostrarDestinatari(enviament_id, isMultiplesDestinatarisActiu) {
    var enviament_id_num = enviament_id.substring(enviament_id.indexOf( '[' ) + 1, enviament_id.indexOf( ']' ));
    enviament_id_num = parseInt(enviament_id_num);

    if ($("div[class*=' personaForm_" + enviament_id_num + "']").hasClass("hidden")) {
        $("div[class*=' personaForm_" + enviament_id_num + "']").removeClass("hidden").show();

        if (!isMultiplesDestinatarisActiu) {
            $("div[class*=' personaForm_" + enviament_id_num + "']").closest('div.destinatari').find('.addDestinatari').addClass('hidden');
        }
    }
}


function obrirModalOrganismes(index, urlOrganigrama, urlComunitatsAutonomes, urlNivellAdministracions, urlCercaUnitats){
    var from = index;
    if (index.includes('-'))
        from = index.split('-')[0];

    $("#organismesModal").modal();

    var indexEnviament;
    var indexDestinatari;
    // Titular
    if (from == "Tit") {
        $("#titular").val(index);
        indexEnviament = index.split('-')[1];
    } else {
        // Destinatario
        $("#titular").val(index);
        indexEnviament = index.split('-')[0];
        indexDestinatari = index.split('-')[1];
    }

    webutilModalAdjustHeight();

    loadSelect2CodiValorFromAjax($("#o_nivellAdmin"), urlNivellAdministracions);
    loadSelect2CodiValorFromAjax($("#o_comunitat"), urlComunitatsAutonomes);

    let dir3CodiDesc;
    if (from == 'Tit') {
        dir3CodiDesc = document.getElementById("searchOrganTit" + indexEnviament).getElementsByTagName('input')[0];
    } else {
        dir3CodiDesc = document.getElementById("searchOrgan" + indexEnviament + indexDestinatari);
    }

    if(dir3CodiDesc.value == '' || dir3CodiDesc.value == null){
        netejar(true, urlCercaUnitats);
    }else{
        netejar(false, urlCercaUnitats);
    }

    $(".loading-screen").hide();
    loadOrganigrama(urlOrganigrama);

}

function obrirModalOrganismesDestinatari(indexEnviament, indexDestinatari, urlOrganigrama, urlComunitatsAutonomes, urlNivellAdministracions, urlCercaUnitats) {
    let index = indexEnviament + "-" + indexDestinatari;
    obrirModalOrganismes(index, urlOrganigrama, urlComunitatsAutonomes, urlNivellAdministracions, urlCercaUnitats);
}

// function searchCodiChange(text){
// 	var searchNom = $('#searchNom');
// 	if(text.trim().length ==0){
// 		searchNom.removeAttr('disabled');
// 	}else{
// 		searchNom.prop("disabled", true);
// 	}

// };

// function searchNomChange(text){
// 	var searchCodi = $('#searchCodi');
// 	if(text.trim().length ==0){
// 		searchCodi.removeAttr('disabled');
// 	}else{
// 		searchCodi.prop("disabled", true);
// 	}

// };

function loadSelect2CodiValorFromAjax($select, urlNivellAdministracions) {
    let value = $select.val();
    $.ajax({
        type: 'GET',
        url: urlNivellAdministracions,
        dataType: 'json',
        async: false,
        data: {	}
    }).done(function(data){
        var list_html = '<option value=""></option>';
        if (data.length > 0) {
            $.each(data, function(i, item) {
                list_html += '<option value=' + data[i].codi + '>' + data[i].valor + '</option>';
            });
        }
        $select.html(list_html);
        if(value !=null && value != ''){
            $select.val(value).trigger('change');
        }
        $select.select2({
            theme: 'bootstrap',
            width: 'auto',
            allowClear: true,
            placeholder: textMessages['comu.placeholder.seleccio']
        });
    }).fail(function(jqXHR, textStatus) {
// 		refreshAlertes();
    });
}

function loadSelect2IdDescripcioFromAjax($select, url) {
    let value = $select.val();
    $.ajax({
        type: 'GET',
        url: url,
        dataType: 'json',
        async: false,
        data: {	}
    }).done(function(data){
        var list_html = '<option value=""></option>';
        if (data.length > 0) {
            $.each(data, function(i, item) {
                list_html += '<option value=' + data[i].id + '>' + data[i].descripcio + '</option>';
            });
        }
        $select.html(list_html);
        if(value !=null && value != ''){
            $select.val(value).trigger('change');
        }
        $select.select2({
            theme: 'bootstrap',
            width: 'auto',
            allowClear: true,
            placeholder: textMessages['comu.placeholder.seleccio']
        });
    }).fail(function(jqXHR, textStatus) {
// 		refreshAlertes();
    });
}
function loadProvincies(codiCA, value, urlProvincies) {
    var provincia = $('#o_provincia');
    if (codiCA != null && codiCA != '') {
        $(".loading-screen").show();
        loadSelect2IdDescripcioFromAjax($("#o_provincia"), urlProvincies + '/' + codiCA);
        $(".loading-screen").hide();
    } else {
        var list_html = '<option value=""></option>';
        $("#o_provincia").html(list_html);
        $("#o_provincia").select2({
            theme: 'bootstrap',
            width: 'auto',
            allowClear: true,
            placeholder: textMessages['comu.placeholder.seleccio']
        });
    }


}

function comunitatAutonomaChange(value, urlProvincies){
    if(value.trim().length !=0){
        loadProvincies(value,$('#o_provincia').val()!=null?$('#o_provincia').val():'', urlProvincies);
    }else{
        limpiarProvincia(true);
    }
};

function provinciesChange(value, urlLocalitats){
    if(value.trim().length !=0){
        loadLocalitats(value, urlLocalitats);
    }else{
        limpiarLocalitat(true);
    }
};

function loadLocalitats(codiProvincia, urlLocalitats) {
    if (codiProvincia != null && codiProvincia != '') {
        $(".loading-screen").show();
        loadSelect2IdDescripcioFromAjax($("#o_localitat"), urlLocalitats + '/' + codiProvincia);
        $(".loading-screen").hide();
    } else {
        var list_html = '<option value=""></option>';
        $("#o_localitat").html(list_html);
        $("#o_localitat").select2({
            theme: 'bootstrap',
            width: 'auto',
            allowClear: true,
            placeholder: textMessages['comu.placeholder.seleccio']
        });
    }
}

// function mbloquejar() {
// // 	var height = $("#dialeg_organs").css('height');
// 	var width = $("#dialeg_organs").css('width');
// 	var top = $("#dialeg_organs").css('top');
// // 	$(".mloading-screen").css('height', height);
// 	$(".mloading-screen").css('width', width);
// 	$(".mloading-screen").css('top', top);
// 	$(".mloading-screen").show();
// }

// function mdesbloquejar() {
// 	$(".mloading-screen").hide();
// }

function netejar(reload, urlCercaUnitats) {
    if(reload){
        limpiarNivellAdmin();
        limpiarComunitat();
        limpiarProvincia(true);
        limpiarLocalitat(true);
        $("#o_codi").val("");
        $("#o_denominacio").val("");
        $("#rOrgans").html('');
        $("#resultatsTotal").addClass('hidden');
    }else{
        loadOrgansGestors(urlCercaUnitats);
    }
    $(".loading-screen").hide();
}

function limpiarNivellAdmin() {
    $('#o_nivellAdmin').val(null).trigger('change');
}
function limpiarComunitat() {
    $('#o_comunitat').val(null).trigger('change');
}
function limpiarProvincia(borrarLlistat) {
    $("#o_provincia").val("").trigger('change');
    if(borrarLlistat){
        $("#o_provincia").html("");
    }
}
function limpiarLocalitat(borrarLlistat) {
    $("#o_localitat").val("").trigger('change');
    if(borrarLlistat){
        $("#o_localitat").html("");
    }
}


function seleccionar(fila){
    var from = $('#titular').val().split('-')[0];
    var index = $('#titular').val().split('-')[1] != undefined?$('#titular').val().split('-')[1]:from;

// 	var organSelect = document.getElementById('selOrganismes');
    if(fila.size()>0){
// 		var organSeleccionatValue = organSelect.options[organSelect.selectedIndex].value;
// 		var organSeleccionatText = organSelect.options[organSelect.selectedIndex].text;

        let codi = fila.data('codi');
        let denominacio = fila.data('denominacio');
        let ocodi = codi + '-' + denominacio;
        let cif = fila.data('cif');
        setPersonaAdministracio(from, index, codi, denominacio, ocodi, cif)

        $('#cerrarModal').click();
    }
}

function setPersonaAdministracio (from, index, codi, denominacio, ocodi, cif) {
    var dir3Codi;
    var raoSocial;
    var dir3CodiDesc;
    var organCif;
    if(from === 'Tit'){
        dir3Codi = document.getElementById("enviaments[" + index + "].titular.dir3Codi");
        raoSocial = document.getElementById("enviaments[" + index + "].titular.raoSocialInput");
        dir3CodiDesc =  document.getElementById("searchOrganTit" + index).getElementsByTagName('input')[0];
        organCif = document.getElementById("enviaments[" + index + "].titular.nif");
    }else{
        dir3Codi = document.getElementById("enviaments[" + from + "].destinataris[" + index + "].dir3Codi");
        raoSocial = document.getElementById("enviaments[" + from + "].destinataris[" + index + "].raoSocialInput");
        dir3CodiDesc =  document.getElementById("searchOrgan" + from + index);
        organCif = document.getElementById("enviaments[" + from + "].destinataris[" + index + "].nif");
    }
    // console.log("denominacio: " + denominacio);
    dir3Codi.value = codi;
    raoSocial.value = denominacio;
    dir3CodiDesc.value = ocodi;
    organCif.value = cif;
}
// function netejarFiltre(){
// 	var searchCodi = $('#searchCodi');
// 	var searchNom = $('#searchNom');
// 	var selOrganismes = $('#selOrganismes');

// 	searchCodi.removeAttr('disabled');
// 	searchCodi.val('');
// 	searchNom.removeAttr('disabled');
// 	searchNom.val('');

// 	selOrganismes.empty();
// 	selOrganismes.append("<option value=\"\"></option>");

// };


function cercaCodiEnOrganigrama(fills){
    var array = new Array();
    if(fills != null && fills != undefined){
        $.each(fills, function(key, obj) {
            array.push(key);
            if(obj.fills != undefined && obj.fills != null){
                $.each(obj.fills, function(key, obj) {
                    cercaCodiEnOrganigrama(obj.fills);
                });
            }
        });

    }
    return array;
}

function loadOrgansGestors(urlCercaUnitats){

    let codi = $("#o_codi").val();
    let denominacio = $("#o_denominacio").val();
    let nivellAdmin = $("#o_nivellAdmin").val();
    let codiComunitat = $('#o_comunitat').val();
    let codiProvincia = $('#o_provincia').val()!=null?$('#o_provincia').val():'';
    let codiLocalitat = $("#o_localitat").val()!=null?$('#o_localitat').val():'';

    // if ((codi || denominacio) && !codiComunitat || codiComunitat && !codi && !denominacio || !codi && !denominacio && !codiComunitat) {
    if (!(codi || denominacio)) {
        alert(textMessages['notificacio.form.dir3.cercar.noMinimOrgansFiltre']);
        return false;
    } else if (codi && codi.length < 5) {
        alert(textMessages['notificacio.form.dir3.cercar.noMinimCodiFiltre']);
        return false;
    } else if (denominacio && denominacio.length < 5) {
        alert(textMessages['notificacio.form.dir3.cercar.noMinimDenominacioFiltre']);
        return false;
    }

    $(".loading-screen").show();
    $.ajax({
        type: 'GET',
        url: urlCercaUnitats + '?codi='+codi.trim()+ '&denominacio=' + denominacio + '&nivellAdministracio=' + nivellAdmin +
                                '&comunitatAutonoma=' + codiComunitat + '&provincia=' + codiProvincia + '&municipi=' + codiLocalitat,
        success: (data) => {

            let list_html = '';
            $("#resultatsTotal").removeClass('hidden');
            if (data.length === 0) {
                $("#total").text("0");
            } else {
                $.each(data, (i, item) => {
                    let enviamentTipus = $('input#enviamentTipus').val();
                    let local = ($('#organigrama').val().indexOf(item.codi) != -1) && !isPermesComunicacionsSirPropiaEntitat;
                    let clase = (i % 2 == 0 ? 'even' : 'odd');
                    let socSir = (item.sir ? textMessages['comu.si'] : textMessages['comu.no']);
                    let comSir = enviamentTipus === 'COMUNICACIO_SIR' && !local && item.sir;
                    if (enviamentTipus === 'COMUNICACIO_SIR' && !comSir) {
                        clase += ' unselectable';
                    }
                    list_html += '<tr class="' + clase + '" data-codi="' + item.codi + '" data-denominacio="' + (item.nom ? item.nom : item.nomEs) + '" data-cif="' + item.cif + '">' +
                        '<td width="85%">' + item.codi + ' - ' + (item.nom ? item.nom : item.nomEs) + '</td>' +
                        '<td>' + (socSir) + '</td>' +
                        '<td>';
                    if (enviamentTipus === 'NOTIFICACIO' || enviamentTipus === 'COMUNICACIO' || comSir) {
                        list_html += '<button type="button" class="select btn btn-success">' + textMessages['comu.boto.seleccionar'] + '</button>';
                    } else if (item.sir) {
                        list_html += '<div style="cursor:pointer; color:#AAA;" title="' + textMessages["notificacio.sir.emprar.valib"] + '"><span  class="fa fa-warning text-danger" ></span> '
                            + textMessages["notificacio.sir.emprar.valib.text"] + '</div>';
                    }
                    list_html += '</td></tr>';
                });
            }
            $("#rOrgans").html(list_html);
            $("#total").text(data.length);
            $(".loading-screen").hide();
        },
        error: err => {
            console.log("error obtenint les administracions...");
            alert(err);
            $(".loading-screen").hide();
        }
    });

};



function mostrarEntregaDeh(className) {
    var element = document.getElementById(className);
    var parent = $(element).closest(".enviamentsForm");
    var classParent = $(parent).attr('class');

    var enviament_id_num = className.substring(className.lastIndexOf('[') + 1, className.lastIndexOf(']'));
    if($('.entregaDeh_' + enviament_id_num).css('display') != 'none') {
        $('.entregaDeh_'+enviament_id_num).hide();
    } else {
        $('.entregaDeh_'+enviament_id_num).show();
    }
}

function actualitzarEntrega(j, urlPaisos, urlProvincies, urlLocalitats) {
    var selPaisos = document.getElementById("enviaments[" + j + "].entregaPostal.paisCodi");
    var selProvincies = document.getElementById("enviaments[" + j + "].entregaPostal.provincia");
    var selLocalitats = document.getElementById("enviaments[" + j + "].entregaPostal.municipiCodi");
    var selPoblacio =  document.getElementById("enviaments[" + j + "].entregaPostal.poblacio");

    $.ajax({
        type: 'GET',
        url: urlPaisos,
        success: function(data) {
            $(selPaisos).empty();
            $(selPaisos).append("<option value=\"\"></option>");
            if (data && data.length > 0) {
                $.each(data, function(i, val) {
                    if (val.alfa2Pais == 'ES') {
                        $(selPaisos).append("<option value=\"" + val.alfa2Pais + "\" selected>" + val.descripcioPais + "</option>");
                    } else {
                        $(selPaisos).append("<option value=\"" + val.alfa2Pais + "\">" + val.descripcioPais + "</option>");
                    }

                });
                var paisCodi = document.getElementsByClassName('enviaments[' + j + '].entregaPostal.paisCodi');

                if (paisCodi !== undefined && paisCodi[0] !== undefined) {
                    $(selPaisos).val(paisCodi[0].value).change();
                }
            }
            var select2Options = {
                theme: 'bootstrap',
                width: 'auto'};
            $(selPaisos).select2(select2Options);
        },
        error: function() {
            console.log("error obtenint les provincies...");
        }
    });
    //Provincies
    $.ajax({
        type: 'GET',
        url: urlProvincies,
        success: function(data) {
            $(selProvincies).empty();
            $(selProvincies).append("<option value=\"\"></option>");
            if (data && data.length > 0) {
                $.each(data, function(i, val) {
                    $(selProvincies).append("<option value=\"" + val.id + "\">" + val.descripcio + "</option>");
                });

                var provinciaCodi = document.getElementsByClassName('enviaments[' + j + '].entregaPostal.provincia');

                if (provinciaCodi !== undefined && provinciaCodi[0] !== undefined) {
                    $(selProvincies).val(provinciaCodi[0].value).change();
                }
            }
            var select2Options = {
                theme: 'bootstrap',
                width: 'auto'};
            $(selProvincies).select2(select2Options);
        },
        error: function() {
            console.log("error obtenint les provincies...");
        }
    });

    //Localitats
    $(selProvincies).on('change', function() {
        var provincia = $(this);
        if ($(this).val() == '') {
            $(selLocalitats).find('option').remove();
        } else {
            $.ajax({
                type: 'GET',
                url: urlLocalitats + "/" + $(provincia).val(),
                success: function(data) {
                    $(selLocalitats).empty();
                    $(selLocalitats).append("<option value=\"\"></option>");
                    if (data && data.length > 0) {
                        $.each(data, function(i, val) {
                            $(selLocalitats).append("<option value=\"" + val.id + "\">" + val.descripcio + "</option>");
                        });

                        var municipiCodi = document.getElementsByClassName('enviaments[' + j + '].entregaPostal.municipiCodi');

                        if (municipiCodi !== undefined && municipiCodi[0] !== undefined) {
                            $(selLocalitats).val(municipiCodi[0].value).change();
                        }
                    }
                    var select2Options = {
                        theme: 'bootstrap',
                        width: 'auto'};
                    $(selLocalitats).select2(select2Options);
                },
                error: function() {
                    console.log("error obtenint les provincies...");
                }
            });
        }
    });

}


function addDestinatari(enviament_id, isMultipleDestinatarisActiu, destinatariHTMLTemplate) {
    var isMultiple = isMultipleDestinatarisActiu;
    var num_enviament = parseInt(enviament_id.substring(enviament_id.indexOf( '[' ) + 1, enviament_id.indexOf( ']' )));
    var num_destinatari = $('div.destenv_' + num_enviament).size();

    let htmlTemplate = destinatariHTMLTemplate; // feim una còpia de la plantilla
    htmlTemplate = replaceAll(htmlTemplate, "#num_enviament#", num_enviament);
    htmlTemplate = replaceAll(htmlTemplate, "#num_destinatari#", num_destinatari);

    $('div.newDestinatari_' + num_enviament).append(htmlTemplate);
    $('#enviaments\\[' + num_enviament + '\\]\\.destinataris\\[' + num_destinatari + '\\]\\.interessatTipus').select2({theme: 'bootstrap', width: 'auto', minimumResultsForSearch: Infinity});

    if (!isMultiple) {
        $("div[class*=' personaForm_" + num_enviament + "']").closest('div.destinatari').find('.addDestinatari').addClass('hidden');
    }
    $('.interessat').trigger('change');

    inputFieldAddCharsCounter('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].nom');
    inputFieldAddCharsCounter('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].llinatge1');
    inputFieldAddCharsCounter('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].llinatge2');
    inputFieldAddCharsCounter('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].telefon');
    inputFieldAddCharsCounter('enviaments[' + num_enviament + '].destinataris[' + num_destinatari + '].email');
}

function comptarCaracters(idCamp) {
    var fieldConcepte = $('#' + idCamp);
    if (fieldConcepte.val().length != 0) {
        var size = $(fieldConcepte).val().length;
        $('.inputCurrentLength').text(size);
    } else {
        $('.inputCurrentLength').text(0);
    };

    //$(fieldConcepte).bind("change paste keyup", function() {
    //	var size = $(this).val().length;
    //	$('.inputCurrentLength').text(size);
    //});
}

function clearDocuments(numDocuments) {
    for (var i = numDocuments - 1; i > 0; i--) {
        $('#tipusDocument_' + i).val('').trigger('change');
    }
}
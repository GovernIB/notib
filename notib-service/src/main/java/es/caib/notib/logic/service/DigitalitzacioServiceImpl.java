package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.DigitalitzacioService;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.EncryptionUtil;
import es.caib.notib.logic.utils.NotibLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.wss4j.dom.util.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DigitalitzacioServiceImpl implements DigitalitzacioService {

    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private AplicacioService aplicacioService;
//    @Autowired
//    private ConfigHelper configHelper;

    @Override
    public List<DigitalitzacioPerfil> getPerfilsDisponibles() {

        NotibLogger.getInstance().info("Recuperant perfils disponibles", log, LoggingTipus.DIGITALITZACIO);
        var perfils = pluginHelper.digitalitzacioPerfilsDisponibles();
        NotibLogger.getInstance().info("Recuperant perfils disponibles " + perfils, log, LoggingTipus.DIGITALITZACIO);
        return perfils;
    }

    @Override
    public DigitalitzacioTransaccioResposta iniciarDigitalitzacio(String codiPerfil, String urlReturn) {

        NotibLogger.getInstance().info("Iniciant el procés d'escaneig", log, LoggingTipus.DIGITALITZACIO);
        var usuariActual = aplicacioService.getUsuariActual();
        var idioma = usuariActual.getIdioma();
        if (idioma != null) {
            idioma = idioma.toLowerCase();
        }
        var resposta = pluginHelper.digitalitzacioIniciarProces(idioma, codiPerfil, usuariActual, urlReturn);
        NotibLogger.getInstance().info("Resposta de la inicialitzacio " + resposta, log, LoggingTipus.DIGITALITZACIO);
        return resposta;
    }

    @Override
    public DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {

        NotibLogger.getInstance().info("Recuperant resultat escaneig", log, LoggingTipus.DIGITALITZACIO);
        var resposta = pluginHelper.digitalitzacioRecuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
        NotibLogger.getInstance().info("Recuperar resultat scan: " + resposta, log, LoggingTipus.DIGITALITZACIO);
        return resposta;
    }

    @Override
    public void tancarTransaccio(String idTransaccio) {

        NotibLogger.getInstance().info("Tancant la transacció: " + idTransaccio, log, LoggingTipus.DIGITALITZACIO);
        pluginHelper.digitalitzacioTancarTransaccio(idTransaccio);
    }
}

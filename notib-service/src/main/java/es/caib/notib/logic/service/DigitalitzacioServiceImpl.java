package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.DigitalitzacioService;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class DigitalitzacioServiceImpl implements DigitalitzacioService {

    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private AplicacioService aplicacioService;

    @Override
    public List<DigitalitzacioPerfil> getPerfilsDisponibles() {

        NotibLogger.getInstance().info("Recuperant perfils disponibles", log, LoggingTipus.DIGITALITZACIO);
        return pluginHelper.digitalitzacioPerfilsDisponibles();
    }

    @Override
    public DigitalitzacioTransaccioResposta iniciarDigitalitzacio(String codiPerfil, String urlReturn) {

        NotibLogger.getInstance().info("Iniciant el procés d'escaneig", log, LoggingTipus.DIGITALITZACIO);
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        String idioma = usuariActual.getIdioma();

        if (idioma != null) {
            idioma = idioma.toLowerCase();
        }
        return pluginHelper.digitalitzacioIniciarProces(idioma, codiPerfil, usuariActual, urlReturn);
    }

    @Override
    public DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {

        NotibLogger.getInstance().info("Recuperant resultat escaneig", log, LoggingTipus.DIGITALITZACIO);
        return pluginHelper.digitalitzacioRecuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
    }

    @Override
    public void tancarTransaccio(String idTransaccio) {

        NotibLogger.getInstance().info("Tancant la transacció: " + idTransaccio, log, LoggingTipus.DIGITALITZACIO);
        pluginHelper.digitalitzacioTancarTransaccio(idTransaccio);
    }
}

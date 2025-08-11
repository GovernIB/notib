package es.caib.notib.logic.cacheable;

import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class CacheBridge {


    @Autowired
    private CacheHelper cacheHelper;

    public DadesUsuari findUsuariAmbCodi(String usuariCodi) {

        var dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
        if (dadesUsuari == null) {
            cacheHelper.evictUsuariAmbCodi(usuariCodi);
            NotibLogger.getInstance().info("[CacheBridge] Buidada cache ja que no hi ha dades per l'usuari (usuariCodi=" + usuariCodi + ")", log, LoggingTipus.USUARIS);
            dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
            if (dadesUsuari == null) {
                log.error("[CacheBridge] Error buscant l'usuari " + usuariCodi + ". No s'ha trobat informacio de l'usuari un cop buidada la cache");
                throw new NotFoundException(usuariCodi, DadesUsuari.class);
            }
        }
        return dadesUsuari;
    }

    public Optional<DadesUsuari> findOptionalUsuariAmbCodi(String usuariCodi) {

        var dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
        if (dadesUsuari == null) {
            cacheHelper.evictUsuariAmbCodi(usuariCodi);
            NotibLogger.getInstance().info("[CacheBridge] Buidada cache ja que no hi ha dades per l'usuari (usuariCodi=" + usuariCodi + ")", log, LoggingTipus.USUARIS);
            dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
            if (dadesUsuari == null) {
                log.error("[CacheBridge] Error buscant l'usuari " + usuariCodi + ". No s'ha trobat informacio de l'usuari un cop buidada la cache");
                return Optional.empty();
            }
        }
        return Optional.of(dadesUsuari);
    }
}

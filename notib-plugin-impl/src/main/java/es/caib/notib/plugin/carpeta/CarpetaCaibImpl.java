package es.caib.notib.plugin.carpeta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CarpetaCaibImpl implements CarpetaPlugin {

    @Override
    public void enviarNotificacioMobil(MissatgeCarpetaParams params) {

        log.info("Enviant avís a CARPETA");

        log.info("Avís enviat a CARPETA");
    }
}

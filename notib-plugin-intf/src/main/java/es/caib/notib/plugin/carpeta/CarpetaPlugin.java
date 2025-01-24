package es.caib.notib.plugin.carpeta;

import es.caib.notib.plugin.SalutPlugin;

public interface CarpetaPlugin extends SalutPlugin {

    RespostaSendNotificacioMovil enviarNotificacioMobil(MissatgeCarpetaParams params) throws Exception;
    boolean existeixNif(String nif);
}

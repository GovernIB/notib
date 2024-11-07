package es.caib.notib.plugin.carpeta;

public interface CarpetaPlugin {

    RespostaSendNotificacioMovil enviarNotificacioMobil(MissatgeCarpetaParams params) throws Exception;
    boolean existeixNif(String nif);
}

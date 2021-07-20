package es.caib.notib.core.helper;

public class PropertiesConstants {

	// 1. Enviament de notificacions pendents al registre y notific@
    public static final String REGISTRAR_ENVIAMENTS_PENDENTS_RATE = "es.caib.notib.tasca.registre.enviaments.periode";
    public static final String REGISTRAR_ENVIAMENTS_PENDENTS_INITIAL_DELAY = "es.caib.notib.tasca.registre.enviaments.retard.inicial";

    // 2. Enviament de notificacions registrades a Notific@
    public static final String NOTIFICA_ENVIAMENTS_REGISTRATS_RATE = "es.caib.notib.tasca.notifica.enviaments.periode";
    public static final String NOTIFICA_ENVIAMENTS_REGISTRATS_INITIAL_DELAY = "es.caib.notib.tasca.notifica.enviaments.retard.inicial";

    // 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
    public static final String ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_RATE = "es.caib.notib.tasca.enviament.actualitzacio.estat.periode";
    public static final String ENVIAMENT_REFRESCAR_ESTAT_PENDENTS_INITIAL_DELAY = "es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial";

    // 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
    public static final String ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_RATE = "es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode";
    public static final String ENVIAMENT_REFRESCAR_ESTAT_ENVIAT_SIR_INITIAL_DELAY = "es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial";

    // 5. Actualització dels procediments a partir de la informació de Rolsac
    public static final String ACTUALITZAR_PROCEDIMENTS_CRON = "es.caib.notib.actualitzacio.procediments.cron";

    // 6. Refrescar notificacions expirades
    public static final String REFRESCAR_NOTIFICACIONS_EXPIRADES_CRON = "es.caib.notib.refrescar.notificacions.expirades.cron";
    
    // 7. Callback de client
    public static final String PROCESSAR_PENDENTS_RATE = "es.caib.notib.tasca.callback.pendents.periode";
    public static final String PROCESSAR_PENDENTS_INITIAL_DELAY = "es.caib.notib.tasca.callback.pendents.retard.inicial";
}

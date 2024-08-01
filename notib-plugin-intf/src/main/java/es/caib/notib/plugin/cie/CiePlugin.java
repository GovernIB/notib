package es.caib.notib.plugin.cie;

public interface CiePlugin {

    RespostaCie enviar(EnviamentCie enviament);
    RespostaCie cancelar(EnviamentCie enviament);
    EstatCie consultarEstat(String identificador);
    RespostaCie altaRemitent(RemitentCie enviament);
    RespostaCie modificarRemitent(RemitentCie enviament);
    Boolean borrarRemitent(String idRemitent);

}

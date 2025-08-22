package es.caib.notib.plugin.cie;

import es.caib.notib.plugin.SalutPlugin;

public interface CiePlugin extends SalutPlugin {

    RespostaCie enviar(EnviamentCie enviament);
    RespostaCie cancelar(EnviamentCie enviament);
    InfoCie consultarEstat(EnviamentCie identificador);
    RespostaCie altaRemitent(RemitentCie enviament);
    RespostaCie modificarRemitent(RemitentCie enviament);
    Boolean borrarRemitent(String idRemitent);

}

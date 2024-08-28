package es.caib.notib.plugin.cie;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;

public interface CiePlugin {

    RespostaCie enviar(EnviamentCie enviament);
    RespostaCie cancelar(EnviamentCie enviament);
    EstatCie consultarEstat(String identificador);
    RespostaCie altaRemitent(RemitentCie enviament);
    RespostaCie modificarRemitent(RemitentCie enviament);
    Boolean borrarRemitent(String idRemitent);

}

package es.caib.notib.ejb;


import es.caib.notib.logic.intf.statemachine.dto.ConsultaNotificaDto;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;

@Primary
@Stateless
public class NotificaService extends AbstractService<es.caib.notib.logic.intf.service.NotificaService> implements es.caib.notib.logic.intf.service.NotificaService {

    @Override
    public void enviarNotifica(String enviamentUuid, EnviamentNotificaRequest enviamentNotificaRequest) {
        getDelegateService().enviarNotifica(enviamentUuid, enviamentNotificaRequest);
    }

    @Override
    public void consultaEstatEnviament(ConsultaNotificaDto enviament) {
        getDelegateService().consultaEstatEnviament(enviament);
    }
}

package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.statemachine.dto.ConsultaNotificaDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import org.springframework.security.access.prepost.PreAuthorize;

public interface NotificaService {

    @PreAuthorize("isAuthenticated()")
    void enviarNotifica(String enviamentUuid, EnviamentNotificaRequest enviamentNotificaRequest);

    @PreAuthorize("isAuthenticated()")
    void consultaEstatEnviament(ConsultaNotificaDto enviament);
}

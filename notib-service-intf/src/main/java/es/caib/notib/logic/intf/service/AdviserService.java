package es.caib.notib.logic.intf.service;


import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public interface AdviserService {

    @PreAuthorize("isAuthenticated()")
    ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio);

}

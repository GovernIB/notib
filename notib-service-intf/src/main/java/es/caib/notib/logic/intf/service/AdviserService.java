package es.caib.notib.logic.intf.service;


import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AdviserService {


    @PreAuthorize("isAuthenticated()")
    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio);

}

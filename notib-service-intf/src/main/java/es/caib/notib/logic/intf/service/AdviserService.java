package es.caib.notib.logic.intf.service;


import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;

public interface AdviserService {

    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio);

}

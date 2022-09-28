package es.caib.notib.adviser;

import es.caib.notib.logic.intf.ws.adviser.Adviser;
import es.caib.notib.logic.intf.ws.adviser.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.SincronizarEnvio;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AdviserEndpoint {

    @PayloadRoot(namespace = Adviser.NAMESPACE_URI, localPart = "sincronizarEnvio")
    @ResponsePayload
    public ResultadoSincronizarEnvio sincronizarEnvio(@RequestPayload SincronizarEnvio sincronizarEnvio) {
        return null;
    }
}

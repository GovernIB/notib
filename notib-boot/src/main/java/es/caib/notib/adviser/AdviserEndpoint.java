package es.caib.notib.adviser;

import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.ws.adviser.AdviserServiceWsV2;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Endpoint
public class AdviserEndpoint {

    @Autowired
    AdviserService adviserService;

    @PayloadRoot(namespace = AdviserServiceWsV2.NAMESPACE_URI, localPart = "sincronizarEnvio")
    @ResponsePayload
    public JAXBElement<ResultadoSincronizarEnvio> sincronizarEnvio(@RequestPayload JAXBElement<SincronizarEnvio> sincronizarEnvio) {
        var resp = adviserService.sincronizarEnvio(sincronizarEnvio.getValue());
        return new JAXBElement<ResultadoSincronizarEnvio>(
                new QName(AdviserServiceWsV2.NAMESPACE_URI, "resultadoSincronizarEnvio"),
                ResultadoSincronizarEnvio.class,
                resp);
    }

//    @PayloadRoot(namespace = AdviserService.NAMESPACE_URI, localPart = "sincronizarEnvio")
//    @ResponsePayload
//    public ResultadoSincronizarEnvio sincronizarEnvio(@RequestPayload SincronizarEnvio sincronizarEnvio) {
//        return adviserService.sincronizarEnvio(sincronizarEnvio);
//    }

}

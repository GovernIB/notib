package es.caib.notib.adviser;

import es.caib.notib.logic.intf.ws.adviser.Adviser;
import es.caib.notib.logic.intf.ws.adviser.AdviserWsV2PortType;
import es.caib.notib.logic.intf.ws.adviser.Opciones;
import es.caib.notib.logic.intf.ws.adviser.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.SincronizarEnvio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.ws.Holder;

@Endpoint
public class AdviserEndpoint {

    @Autowired
    private AdviserWsV2PortType adviserWsV2PortType;

    @PayloadRoot(namespace = Adviser.NAMESPACE_URI, localPart = "sincronizarEnvio")
    @ResponsePayload
    public ResultadoSincronizarEnvio sincronizarEnvio(@RequestPayload SincronizarEnvio sincronizarEnvio) {
        ResultadoSincronizarEnvio resultadoSincronizarEnvio = new ResultadoSincronizarEnvio();
        Holder<String> identificador = new Holder<>(sincronizarEnvio.getIdentificador());
        Holder<String> codigoRespuesta = new Holder<>();
        Holder<String> descripcionRespuesta = new Holder<>();
        Holder<Opciones> opcionesResultadoSincronizarEnvio = new Holder<>();

        adviserWsV2PortType.sincronizarEnvio(
                sincronizarEnvio.getOrganismoEmisor(),
                identificador,
                sincronizarEnvio.getTipoEntrega(),
                sincronizarEnvio.getModoNotificacion(),
                sincronizarEnvio.getEstado(),
                sincronizarEnvio.getFechaEstado(),
                sincronizarEnvio.getReceptor(),
                sincronizarEnvio.getAcusePDF(),
                sincronizarEnvio.getAcuseXML(),
                sincronizarEnvio.getOpcionesSincronizarEnvio(),
                codigoRespuesta,
                descripcionRespuesta,
                opcionesResultadoSincronizarEnvio);

        resultadoSincronizarEnvio.setIdentificador(identificador.value);
        resultadoSincronizarEnvio.setCodigoRespuesta(codigoRespuesta.value);
        resultadoSincronizarEnvio.setDescripcionRespuesta(descripcionRespuesta.value);
        resultadoSincronizarEnvio.setOpcionesResultadoSincronizarEnvio(opcionesResultadoSincronizarEnvio.value);
        return resultadoSincronizarEnvio;
    }
}

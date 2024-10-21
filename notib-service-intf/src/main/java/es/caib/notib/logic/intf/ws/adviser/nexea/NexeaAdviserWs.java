package es.caib.notib.logic.intf.ws.adviser.nexea;

import es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.math.BigInteger;

@WebService(
        name = NexeaAdviserWs.SERVICE_NAME,
        serviceName = NexeaAdviserWs.SERVICE_NAME + "Service",
        portName = NexeaAdviserWs.SERVICE_NAME + "Port",
        targetNamespace = NexeaAdviserWs.NAMESPACE_URI)
public interface NexeaAdviserWs {

    String SERVICE_NAME = "NexeaAdviserWs";
    // TODO S'HA DE CANVIAR AQUESTA URL
    String NAMESPACE_URI = "http://nexea.es/ws/adviserwsv2/1.0/";
    String NAMESPACE_URI_PARAMETERS = "http://nexea.es/ws/adviserwsv2/1.0/sincronizarEnvio";
    String NAMESPACE_URI_PARAMETERS_COMMON = "http://nexea.es/ws/adviserwsv2/1.0/common";
    String SYNC_ENVIO_OE_OK = "200";
    String CODI_OK = "000";
    String CODI_OK_DEC = "OK";
    String CODI_ERROR = "3000";
    String CODI_ERROR_IDENTIFICADOR_INCORRECTE = "3001";
    String CODI_ERROR_IDENTIFICADOR_INEXISTENT = "3002";

    /**
     *
     * @param descripcionRespuesta
     * @param organismoEmisor
     * @param estado
     * @param modoNotificacion
     * @param acusePDF
     * @param tipoEntrega
     * @param fechaEstado
     * @param opcionesResultadoSincronizarEnvio
     * @param opcionesSincronizarEnvio
     * @param receptor
     * @param acuseXML
     * @param codigoRespuesta
     * @param identificador
     */
    @WebMethod(action = "sincronizarEnvio")
    @RequestWrapper(localName = "sincronizarEnvio", targetNamespace = NexeaAdviserWs.NAMESPACE_URI, className = "es.caib.notib.logic.intf.ws.adviser.sincronizarEnvio.SincronizarEnvio")
    @ResponseWrapper(localName = "resultadoSincronizarEnvio", targetNamespace = NexeaAdviserWs.NAMESPACE_URI, className = "es.caib.notib.logic.intf.ws.adviser.sincronizarEnvio.ResultadoSincronizarEnvio")
    void sincronizarEnvio(
        @WebParam(name = "organismoEmisor", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        String organismoEmisor,
        @WebParam(name = "identificador", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS, mode = WebParam.Mode.INOUT)
        Holder<String> identificador,
        @WebParam(name = "tipoEntrega", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        BigInteger tipoEntrega,
        @WebParam(name = "modoNotificacion", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        BigInteger modoNotificacion,
        @WebParam(name = "estado", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        String estado,
        @WebParam(name = "fechaEstado", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        XMLGregorianCalendar fechaEstado,
        @WebParam(name = "receptor", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        Receptor receptor,
        @WebParam(name = "acusePDF", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        Acuse acusePDF,
        @WebParam(name = "acuseXML", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        Acuse acuseXML,
        @WebParam(name = "opcionesSincronizarEnvio", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS)
        Opciones opcionesSincronizarEnvio,
        @WebParam(name = "codigoRespuesta", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS, mode = WebParam.Mode.OUT)
        Holder<String> codigoRespuesta,
        @WebParam(name = "descripcionRespuesta", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS, mode = WebParam.Mode.OUT)
        Holder<String> descripcionRespuesta,
        @WebParam(name = "opcionesResultadoSincronizarEnvio", targetNamespace = NexeaAdviserWs.NAMESPACE_URI_PARAMETERS, mode = WebParam.Mode.OUT)
        Holder<Opciones> opcionesResultadoSincronizarEnvio);
}

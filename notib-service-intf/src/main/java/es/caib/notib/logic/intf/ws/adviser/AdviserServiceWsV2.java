package es.caib.notib.logic.intf.ws.adviser;


import es.caib.notib.logic.intf.ws.adviser.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.math.BigInteger;

@WebService(
        name = AdviserServiceWsV2.SERVICE_NAME,
        serviceName = AdviserServiceWsV2.SERVICE_NAME + "Service",
        portName = AdviserServiceWsV2.SERVICE_NAME + "Port",
        targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
public interface AdviserServiceWsV2 {

    public static final String SERVICE_NAME = "AdviserWsV2";
    public static final String NAMESPACE_URI = "http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/sincronizarEnvio";


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
    @RequestWrapper(localName = "sincronizarEnvio", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI, className = "es.caib.notib.logic.intf.ws.adviser.sincronizarEnvio.SincronizarEnvio")
    @ResponseWrapper(localName = "resultadoSincronizarEnvio", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI, className = "es.caib.notib.logic.intf.ws.adviser.sincronizarEnvio.ResultadoSincronizarEnvio")
    public void sincronizarEnvio(
            @WebParam(name = "organismoEmisor", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            String organismoEmisor,
            @WebParam(name = "identificador", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI, mode = WebParam.Mode.INOUT)
            Holder<String> identificador,
            @WebParam(name = "tipoEntrega", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            BigInteger tipoEntrega,
            @WebParam(name = "modoNotificacion", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            BigInteger modoNotificacion,
            @WebParam(name = "estado", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            String estado,
            @WebParam(name = "fechaEstado", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            XMLGregorianCalendar fechaEstado,
            @WebParam(name = "receptor", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            Receptor receptor,
            @WebParam(name = "acusePDF", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            Acuse acusePDF,
            @WebParam(name = "acuseXML", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            Acuse acuseXML,
            @WebParam(name = "opcionesSincronizarEnvio", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI)
            Opciones opcionesSincronizarEnvio,
            @WebParam(name = "codigoRespuesta", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI, mode = WebParam.Mode.OUT)
            Holder<String> codigoRespuesta,
            @WebParam(name = "descripcionRespuesta", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI, mode = WebParam.Mode.OUT)
            Holder<String> descripcionRespuesta,
            @WebParam(name = "opcionesResultadoSincronizarEnvio", targetNamespace = AdviserServiceWsV2.NAMESPACE_URI, mode = WebParam.Mode.OUT)
            Holder<Opciones> opcionesResultadoSincronizarEnvio);
}

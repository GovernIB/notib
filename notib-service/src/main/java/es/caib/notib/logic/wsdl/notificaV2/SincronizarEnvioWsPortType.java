
package es.caib.notib.logic.wsdl.notificaV2;

import es.caib.notib.logic.wsdl.notificaV2.common.Opciones;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.InfoEnvioLigero;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.RespuestaInfoEnvioLigero;
import es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.Acuse;
import es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.Receptor;
import es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.RespuestaSincronizarEnvioOE;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.math.BigInteger;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 *
 */
@WebService(name = "SincronizarEnvioWsPortType", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/")
@XmlSeeAlso({
        es.caib.notib.logic.wsdl.notificaV2.common.ObjectFactory.class,
        es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.ObjectFactory.class,
        es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.ObjectFactory.class
})
public interface SincronizarEnvioWsPortType {


    /**
     *
     * @param descripcionRespuesta
     * @param organismoEmisor
     * @param estado
     * @param motivo
     * @param modoNotificacion
     * @param acusePDF
     * @param tipoEntrega
     * @param fechaEstado
     * @param opcionesSincronizarEnvioOE
     * @param receptor
     * @param opcionesRespuestaSincronizarOE
     * @param acuseXML
     * @param codigoRespuesta
     * @param identificador
     */
    @WebMethod(action = "sincronizarEnvioOE")
    @RequestWrapper(localName = "sincronizarEnvioOE", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", className = "es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOe.SincronizarEnvioOE")
    @ResponseWrapper(localName = "respuestaSincronizarEnvioOE", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", className = "es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOe.RespuestaSincronizarEnvioOE")
    public RespuestaSincronizarEnvioOE sincronizarEnvioOE(
            @WebParam(name = "organismoEmisor", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            String organismoEmisor,
            @WebParam(name = "identificador", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            String identificador,
            @WebParam(name = "tipoEntrega", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            BigInteger tipoEntrega,
            @WebParam(name = "modoNotificacion", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            BigInteger modoNotificacion,
            @WebParam(name = "estado", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", mode = WebParam.Mode.INOUT)
            Holder<String> estado,
            @WebParam(name = "fechaEstado", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", mode = WebParam.Mode.INOUT)
            Holder<XMLGregorianCalendar> fechaEstado,
            @WebParam(name = "motivo", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            String motivo,
            @WebParam(name = "receptor", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            Receptor receptor,
            @WebParam(name = "acusePDF", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            Acuse acusePDF,
            @WebParam(name = "acuseXML", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            Acuse acuseXML,
            @WebParam(name = "opcionesSincronizarEnvioOE", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE")
            Opciones opcionesSincronizarEnvioOE,
            @WebParam(name = "codigoRespuesta", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", mode = WebParam.Mode.OUT)
            Holder<String> codigoRespuesta,
            @WebParam(name = "descripcionRespuesta", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", mode = WebParam.Mode.OUT)
            Holder<String> descripcionRespuesta,
            @WebParam(name = "opcionesRespuestaSincronizarOE", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", mode = WebParam.Mode.OUT)
            Holder<Opciones> opcionesRespuestaSincronizarOE);


    /**
     *
     * @param infoEnvioLigero
     * @return returns es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.InfoEnvioLigero
     */
    @WebMethod(action = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero")
    @WebResult(name = "infoEnvioLigero", targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero", partName = "respuestaInfoEnvioLigero")
    public RespuestaInfoEnvioLigero infoEnvioLigero(
        @WebParam(name = "infoEnvioLigero", targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero", partName = "infoEnvioLigero")
        InfoEnvioLigero infoEnvioLigero);


    /**
     *
     * @param descripcionRespuesta
     * @param estado
     * @param certificacion
     * @param codigoDir3
     * @param nivelDetalle
     * @param datados
     * @param opcionesRespuestaInfoEnvioLigero
     * @param opcionesInfoEnvioLigero
     * @param referenciaEmisor
     * @param codigoRespuesta
     * @param identificador
     */
//    @WebMethod(action = "infoEnvioLigero")
//    @RequestWrapper(localName = "infoEnvioLigero", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", className = "es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.InfoEnvioLigero")
//    @ResponseWrapper(localName = "respuestaInfoEnvioLigero", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", className = "es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.RespuestaInfoEnvioLigero")
//    public RespuestaInfoEnvioLigero infoEnvioLigero(
//        @WebParam(name = "identificador", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.INOUT)
//        Holder<String> identificador,
//        @WebParam(name = "referenciaEmisor", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero")
//        String referenciaEmisor,
//        @WebParam(name = "codigoDir3", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero")
//        String codigoDir3,
//        @WebParam(name = "nivelDetalle", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero")
//        BigInteger nivelDetalle,
//        @WebParam(name = "opcionesInfoEnvioLigero", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero")
//        Opciones opcionesInfoEnvioLigero,
//        @WebParam(name = "codigoRespuesta", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.OUT)
//        Holder<String> codigoRespuesta,
//        @WebParam(name = "descripcionRespuesta", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.OUT)
//        Holder<String> descripcionRespuesta,
//        @WebParam(name = "estado", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.OUT)
//        Holder<String> estado,
//        @WebParam(name = "datados", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.OUT)
//        Holder<Datados> datados,
//        @WebParam(name = "certificacion", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.OUT)
//        Holder<Certificacion> certificacion,
//        @WebParam(name = "opcionesRespuestaInfoEnvioLigero", targetNamespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", mode = WebParam.Mode.OUT)
//        Holder<Opciones> opcionesRespuestaInfoEnvioLigero);

}

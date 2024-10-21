package es.caib.notib.api.externa.ws;


import es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor;
import org.jboss.ws.api.annotation.WebContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

import static es.caib.notib.api.externa.config.ServiceInstancesConfig.getNexeaAdviserServiceInstance;


/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebService(
        name = es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs.SERVICE_NAME,
        serviceName = es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs.SERVICE_NAME + "Service",
        portName = es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs.SERVICE_NAME + "Port",
        targetNamespace = es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs.NAMESPACE_URI)
@WebContext(contextRoot = "/notibapi/externa", urlPattern = "/adviser/entrega/postal")
public class NexeaAdviserWs implements es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs {

    private static ApplicationContext applicationContext;

    @Override
    @PermitAll
    @WebMethod
    public void sincronizarEnvio(
            @WebParam(name = "organismoEmisor", targetNamespace = "")
            String organismoEmisor,
            @WebParam(name = "identificador", targetNamespace = "")
            Holder<String> identificador,
            @WebParam(name = "tipoEntrega", targetNamespace = "")
            BigInteger tipoEntrega,
            @WebParam(name = "modoNotificacion", targetNamespace = "")
            BigInteger modoNotificacion,
            @WebParam(name = "estado", targetNamespace = "")
            String estado,
            @WebParam(name = "fechaEstado", targetNamespace = "")
            XMLGregorianCalendar fechaEstado,
            @WebParam(name = "receptor", targetNamespace = "")
            Receptor receptor,
            @WebParam(name = "acusePDF", targetNamespace = "")
            Acuse acusePDF,
            @WebParam(name = "acuseXML", targetNamespace = "")
            Acuse acuseXML,
            @WebParam(name = "opcionesSincronizarEnvio", targetNamespace = "")
            Opciones opcionesSincronizarEnvio,
            @WebParam(mode = WebParam.Mode.OUT, name = "codigoRespuesta", targetNamespace = "")
            Holder<String> codigoRespuesta,
            @WebParam(mode = WebParam.Mode.OUT, name = "descripcionRespuesta", targetNamespace = "")
            Holder<String> descripcionRespuesta,
            @WebParam(mode = WebParam.Mode.OUT, name = "opcionesResultadoSincronizarEnvio", targetNamespace = "")
            Holder<Opciones> opcionesResultadoSincronizarEnvio) {

        getNexeaAdviserServiceInstance().sincronizarEnvio(
                organismoEmisor,
                identificador,
                tipoEntrega,
                modoNotificacion,
                estado,
                fechaEstado,
                receptor,
                acusePDF,
                acuseXML,
                opcionesSincronizarEnvio,
                codigoRespuesta,
                descripcionRespuesta,
                opcionesResultadoSincronizarEnvio);
    }

}

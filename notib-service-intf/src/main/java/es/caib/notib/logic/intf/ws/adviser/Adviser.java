
package es.caib.notib.logic.intf.ws.adviser;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(
        name = Adviser.SERVICE_NAME,
        serviceName = Adviser.SERVICE_NAME + "Service",
        portName = Adviser.SERVICE_NAME + "Port",
        targetNamespace = Adviser.NAMESPACE_URI)
public interface Adviser {

    public static final String SERVICE_NAME = "AdviserWsV2";
    public static final String NAMESPACE_URI = "http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/";

    @WebMethod
    @WebResult(name = "resultadoSincronizarEnvio")
    public ResultadoSincronizarEnvio sincronizarEnvio(@WebParam(name = "sincronizarEnvio")  SincronizarEnvio sincronizarEnvio);

}

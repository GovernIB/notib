package es.caib.notib.api.externa.ws;

import es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs;
import org.apache.cxf.interceptor.transform.TransformInInterceptor;

import java.util.Collections;

/**
 * Interceptor d'entrada CXF que tolera l'element wrapper <sincronizarEnvio> sense qualificar.
 *
 * Nexea envia <sincronizarEnvio> sense xmlns (namespace buit), però el contracte del servei
 * espera {http://nexea.es/ws/adviserwsv2/1.0/sincronizarEnvio}sincronizarEnvio, i CXF respon
 * "Unexpected wrapper element sincronizarEnvio found". Aquest interceptor reescriu, abans de
 * parsejar el missatge (fase POST_STREAM, abans de StaxInInterceptor), l'element sense namespace
 * cap a l'element qualificat. Si el client ja envia l'element qualificat, no es toca res.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NexeaSincronizarEnvioInInterceptor extends TransformInInterceptor {

    public NexeaSincronizarEnvioInInterceptor() {
        super();
        setInTransformElements(Collections.singletonMap(
                "sincronizarEnvio",
                "{" + NexeaAdviserWs.NAMESPACE_URI + "}sincronizarEnvio"));
    }

}

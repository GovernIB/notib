
package es.caib.notib.plugin.cie.nexea;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "Exception", targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios")
public class Exception
    extends java.lang.Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private es.caib.notib.plugin.cie.nexea.altaremesaenvios.Exception faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public Exception(String message, es.caib.notib.plugin.cie.nexea.altaremesaenvios.Exception faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public Exception(String message, es.caib.notib.plugin.cie.nexea.altaremesaenvios.Exception faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.altaremesaenvios.Exception
     */
    public es.caib.notib.plugin.cie.nexea.altaremesaenvios.Exception getFaultInfo() {
        return faultInfo;
    }

}

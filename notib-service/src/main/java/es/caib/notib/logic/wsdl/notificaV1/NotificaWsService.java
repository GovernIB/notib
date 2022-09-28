
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b14002
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "NotificaWsService", targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", wsdlLocation = "file:/home/siona/git/notib/notib-core/src/main/resources/es/caib/notib/core/wsdl/NotificaWS.wsdl")
public class NotificaWsService
    extends Service
{

    private final static URL NOTIFICAWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException NOTIFICAWSSERVICE_EXCEPTION;
    private final static QName NOTIFICAWSSERVICE_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", "NotificaWsService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/home/siona/git/notib/notib-core/src/main/resources/es/caib/notib/core/wsdl/NotificaWS.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        NOTIFICAWSSERVICE_WSDL_LOCATION = url;
        NOTIFICAWSSERVICE_EXCEPTION = e;
    }

    public NotificaWsService() {
        super(__getWsdlLocation(), NOTIFICAWSSERVICE_QNAME);
    }

    public NotificaWsService(WebServiceFeature... features) {
        super(__getWsdlLocation(), NOTIFICAWSSERVICE_QNAME, features);
    }

    public NotificaWsService(URL wsdlLocation) {
        super(wsdlLocation, NOTIFICAWSSERVICE_QNAME);
    }

    public NotificaWsService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, NOTIFICAWSSERVICE_QNAME, features);
    }

    public NotificaWsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public NotificaWsService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns NotificaWsPortType
     */
    @WebEndpoint(name = "NotificaWsPort")
    public NotificaWsPortType getNotificaWsPort() {
        return super.getPort(new QName("https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", "NotificaWsPort"), NotificaWsPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns NotificaWsPortType
     */
    @WebEndpoint(name = "NotificaWsPort")
    public NotificaWsPortType getNotificaWsPort(WebServiceFeature... features) {
        return super.getPort(new QName("https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", "NotificaWsPort"), NotificaWsPortType.class, features);
    }

    private static URL __getWsdlLocation() {
        if (NOTIFICAWSSERVICE_EXCEPTION!= null) {
            throw NOTIFICAWSSERVICE_EXCEPTION;
        }
        return NOTIFICAWSSERVICE_WSDL_LOCATION;
    }

}

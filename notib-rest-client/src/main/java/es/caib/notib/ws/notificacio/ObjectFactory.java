
package es.caib.notib.ws.notificacio;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.notib.ws.notificacio package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Consulta_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consulta");
    private final static QName _ConsultaCertificacioResponse_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consultaCertificacioResponse");
    private final static QName _ConsultaEstatResponse_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consultaEstatResponse");
    private final static QName _AltaResponse_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "altaResponse");
    private final static QName _Alta_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "alta");
    private final static QName _NotificacioCertificacio_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "notificacioCertificacio");
    private final static QName _NotificacioWsServiceException_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "NotificacioWsServiceException");
    private final static QName _ConsultaEstat_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consultaEstat");
    private final static QName _ConsultaResponse_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consultaResponse");
    private final static QName _Notificacio_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "notificacio");
    private final static QName _NotificacioEstat_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "notificacioEstat");
    private final static QName _ConsultaCertificacio_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consultaCertificacio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.notib.ws.notificacio
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsultaCertificacio }
     * 
     */
    public ConsultaCertificacio createConsultaCertificacio() {
        return new ConsultaCertificacio();
    }

    /**
     * Create an instance of {@link ConsultaEstat }
     * 
     */
    public ConsultaEstat createConsultaEstat() {
        return new ConsultaEstat();
    }

    /**
     * Create an instance of {@link ConsultaResponse }
     * 
     */
    public ConsultaResponse createConsultaResponse() {
        return new ConsultaResponse();
    }

    /**
     * Create an instance of {@link Notificacio_Type }
     * 
     */
    public Notificacio_Type createNotificacio_Type() {
        return new Notificacio_Type();
    }

    /**
     * Create an instance of {@link NotificacioEstat }
     * 
     */
    public NotificacioEstat createNotificacioEstat() {
        return new NotificacioEstat();
    }

    /**
     * Create an instance of {@link Alta }
     * 
     */
    public Alta createAlta() {
        return new Alta();
    }

    /**
     * Create an instance of {@link NotificacioCertificacio }
     * 
     */
    public NotificacioCertificacio createNotificacioCertificacio() {
        return new NotificacioCertificacio();
    }

    /**
     * Create an instance of {@link NotificacioWsServiceException }
     * 
     */
    public NotificacioWsServiceException createNotificacioWsServiceException() {
        return new NotificacioWsServiceException();
    }

    /**
     * Create an instance of {@link AltaResponse }
     * 
     */
    public AltaResponse createAltaResponse() {
        return new AltaResponse();
    }

    /**
     * Create an instance of {@link ConsultaCertificacioResponse }
     * 
     */
    public ConsultaCertificacioResponse createConsultaCertificacioResponse() {
        return new ConsultaCertificacioResponse();
    }

    /**
     * Create an instance of {@link ConsultaEstatResponse }
     * 
     */
    public ConsultaEstatResponse createConsultaEstatResponse() {
        return new ConsultaEstatResponse();
    }

    /**
     * Create an instance of {@link Consulta }
     * 
     */
    public Consulta createConsulta() {
        return new Consulta();
    }

    /**
     * Create an instance of {@link NotificacioDestinatari }
     * 
     */
    public NotificacioDestinatari createNotificacioDestinatari() {
        return new NotificacioDestinatari();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Consulta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consulta")
    public JAXBElement<Consulta> createConsulta(Consulta value) {
        return new JAXBElement<Consulta>(_Consulta_QNAME, Consulta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaCertificacioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consultaCertificacioResponse")
    public JAXBElement<ConsultaCertificacioResponse> createConsultaCertificacioResponse(ConsultaCertificacioResponse value) {
        return new JAXBElement<ConsultaCertificacioResponse>(_ConsultaCertificacioResponse_QNAME, ConsultaCertificacioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaEstatResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consultaEstatResponse")
    public JAXBElement<ConsultaEstatResponse> createConsultaEstatResponse(ConsultaEstatResponse value) {
        return new JAXBElement<ConsultaEstatResponse>(_ConsultaEstatResponse_QNAME, ConsultaEstatResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "altaResponse")
    public JAXBElement<AltaResponse> createAltaResponse(AltaResponse value) {
        return new JAXBElement<AltaResponse>(_AltaResponse_QNAME, AltaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Alta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "alta")
    public JAXBElement<Alta> createAlta(Alta value) {
        return new JAXBElement<Alta>(_Alta_QNAME, Alta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificacioCertificacio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "notificacioCertificacio")
    public JAXBElement<NotificacioCertificacio> createNotificacioCertificacio(NotificacioCertificacio value) {
        return new JAXBElement<NotificacioCertificacio>(_NotificacioCertificacio_QNAME, NotificacioCertificacio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificacioWsServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "NotificacioWsServiceException")
    public JAXBElement<NotificacioWsServiceException> createNotificacioWsServiceException(NotificacioWsServiceException value) {
        return new JAXBElement<NotificacioWsServiceException>(_NotificacioWsServiceException_QNAME, NotificacioWsServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaEstat }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consultaEstat")
    public JAXBElement<ConsultaEstat> createConsultaEstat(ConsultaEstat value) {
        return new JAXBElement<ConsultaEstat>(_ConsultaEstat_QNAME, ConsultaEstat.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consultaResponse")
    public JAXBElement<ConsultaResponse> createConsultaResponse(ConsultaResponse value) {
        return new JAXBElement<ConsultaResponse>(_ConsultaResponse_QNAME, ConsultaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notificacio_Type }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "notificacio")
    public JAXBElement<Notificacio_Type> createNotificacio(Notificacio_Type value) {
        return new JAXBElement<Notificacio_Type>(_Notificacio_QNAME, Notificacio_Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificacioEstat }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "notificacioEstat")
    public JAXBElement<NotificacioEstat> createNotificacioEstat(NotificacioEstat value) {
        return new JAXBElement<NotificacioEstat>(_NotificacioEstat_QNAME, NotificacioEstat.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaCertificacio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consultaCertificacio")
    public JAXBElement<ConsultaCertificacio> createConsultaCertificacio(ConsultaCertificacio value) {
        return new JAXBElement<ConsultaCertificacio>(_ConsultaCertificacio_QNAME, ConsultaCertificacio.class, null, value);
    }

}

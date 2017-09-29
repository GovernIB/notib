
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
    private final static QName _AltaResponse_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "altaResponse");
    private final static QName _InformacioEnviament_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "informacioEnviament");
    private final static QName _NotificacioServiceWsException_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "NotificacioServiceWsException");
    private final static QName _Alta_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "alta");
    private final static QName _ConsultaResponse_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "consultaResponse");
    private final static QName _Notificacio_QNAME = new QName("http://www.caib.es/notib/ws/notificacio", "notificacio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.notib.ws.notificacio
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsultaResponse }
     * 
     */
    public ConsultaResponse createConsultaResponse() {
        return new ConsultaResponse();
    }

    /**
     * Create an instance of {@link Notificacio }
     * 
     */
    public Notificacio createNotificacio() {
        return new Notificacio();
    }

    /**
     * Create an instance of {@link Alta }
     * 
     */
    public Alta createAlta() {
        return new Alta();
    }

    /**
     * Create an instance of {@link NotificacioServiceWsException }
     * 
     */
    public NotificacioServiceWsException createNotificacioServiceWsException() {
        return new NotificacioServiceWsException();
    }

    /**
     * Create an instance of {@link AltaResponse }
     * 
     */
    public AltaResponse createAltaResponse() {
        return new AltaResponse();
    }

    /**
     * Create an instance of {@link InformacioEnviament }
     * 
     */
    public InformacioEnviament createInformacioEnviament() {
        return new InformacioEnviament();
    }

    /**
     * Create an instance of {@link Consulta }
     * 
     */
    public Consulta createConsulta() {
        return new Consulta();
    }

    /**
     * Create an instance of {@link Persona }
     * 
     */
    public Persona createPersona() {
        return new Persona();
    }

    /**
     * Create an instance of {@link EntregaPostal }
     * 
     */
    public EntregaPostal createEntregaPostal() {
        return new EntregaPostal();
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Certificacio }
     * 
     */
    public Certificacio createCertificacio() {
        return new Certificacio();
    }

    /**
     * Create an instance of {@link EntregaDeh }
     * 
     */
    public EntregaDeh createEntregaDeh() {
        return new EntregaDeh();
    }

    /**
     * Create an instance of {@link PagadorPostal }
     * 
     */
    public PagadorPostal createPagadorPostal() {
        return new PagadorPostal();
    }

    /**
     * Create an instance of {@link Enviament }
     * 
     */
    public Enviament createEnviament() {
        return new Enviament();
    }

    /**
     * Create an instance of {@link ParametresSeu }
     * 
     */
    public ParametresSeu createParametresSeu() {
        return new ParametresSeu();
    }

    /**
     * Create an instance of {@link PagadorCie }
     * 
     */
    public PagadorCie createPagadorCie() {
        return new PagadorCie();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "altaResponse")
    public JAXBElement<AltaResponse> createAltaResponse(AltaResponse value) {
        return new JAXBElement<AltaResponse>(_AltaResponse_QNAME, AltaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InformacioEnviament }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "informacioEnviament")
    public JAXBElement<InformacioEnviament> createInformacioEnviament(InformacioEnviament value) {
        return new JAXBElement<InformacioEnviament>(_InformacioEnviament_QNAME, InformacioEnviament.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificacioServiceWsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "NotificacioServiceWsException")
    public JAXBElement<NotificacioServiceWsException> createNotificacioServiceWsException(NotificacioServiceWsException value) {
        return new JAXBElement<NotificacioServiceWsException>(_NotificacioServiceWsException_QNAME, NotificacioServiceWsException.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "consultaResponse")
    public JAXBElement<ConsultaResponse> createConsultaResponse(ConsultaResponse value) {
        return new JAXBElement<ConsultaResponse>(_ConsultaResponse_QNAME, ConsultaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notificacio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/notib/ws/notificacio", name = "notificacio")
    public JAXBElement<Notificacio> createNotificacio(Notificacio value) {
        return new JAXBElement<Notificacio>(_Notificacio_QNAME, Notificacio.class, null, value);
    }

}

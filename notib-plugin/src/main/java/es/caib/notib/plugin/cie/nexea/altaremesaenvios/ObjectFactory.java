
package es.caib.notib.plugin.cie.nexea.altaremesaenvios;

import es.caib.notib.plugin.cie.nexea.cancelarenvio.CancelarEnvio;
import es.caib.notib.plugin.cie.nexea.infoenvioligero.InfoEnvioLigero;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.altaremesaenvios package. 
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

    private final static QName _ResultadoAltaRemesaEnvios_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", "resultadoAltaRemesaEnvios");
    private final static QName _CancelarEnvio_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", "cancelarEnvio");
    private final static QName _InfoEnvioLigero_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", "infoEnvioLigero");
    private final static QName _AltaRemesaEnvios_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", "altaRemesaEnvios");
    private final static QName _Exception_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", "Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.altaremesaenvios
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResultadoAltaRemesaEnvios }
     * 
     */
    public ResultadoAltaRemesaEnvios createResultadoAltaRemesaEnvios() {
        return new ResultadoAltaRemesaEnvios();
    }

    /**
     * Create an instance of {@link AltaRemesaEnvios }
     * 
     */
    public AltaRemesaEnvios createAltaRemesaEnvios() {
        return new AltaRemesaEnvios();
    }

    /**
     * Create an instance of {@link java.lang.Exception }
     * 
     */
    public java.lang.Exception createException() {
        return new java.lang.Exception();
    }

    /**
     * Create an instance of {@link Opcion }
     * 
     */
    public Opcion createOpcion() {
        return new Opcion();
    }

    /**
     * Create an instance of {@link Envios }
     * 
     */
    public Envios createEnvios() {
        return new Envios();
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
     * Create an instance of {@link OrganismoPagadorPostal }
     * 
     */
    public OrganismoPagadorPostal createOrganismoPagadorPostal() {
        return new OrganismoPagadorPostal();
    }

    /**
     * Create an instance of {@link Envio }
     * 
     */
    public Envio createEnvio() {
        return new Envio();
    }

    /**
     * Create an instance of {@link Opciones }
     * 
     */
    public Opciones createOpciones() {
        return new Opciones();
    }

    /**
     * Create an instance of {@link Destinatarios }
     * 
     */
    public Destinatarios createDestinatarios() {
        return new Destinatarios();
    }

    /**
     * Create an instance of {@link Documento }
     * 
     */
    public Documento createDocumento() {
        return new Documento();
    }

    /**
     * Create an instance of {@link EntregaDEH }
     * 
     */
    public EntregaDEH createEntregaDEH() {
        return new EntregaDEH();
    }

    /**
     * Create an instance of {@link ResultadoEnvio }
     * 
     */
    public ResultadoEnvio createResultadoEnvio() {
        return new ResultadoEnvio();
    }

    /**
     * Create an instance of {@link OrganismoPagadorCIE }
     * 
     */
    public OrganismoPagadorCIE createOrganismoPagadorCIE() {
        return new OrganismoPagadorCIE();
    }

    /**
     * Create an instance of {@link ResultadoEnvios }
     * 
     */
    public ResultadoEnvios createResultadoEnvios() {
        return new ResultadoEnvios();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultadoAltaRemesaEnvios }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", name = "resultadoAltaRemesaEnvios")
    public JAXBElement<ResultadoAltaRemesaEnvios> createResultadoAltaRemesaEnvios(ResultadoAltaRemesaEnvios value) {
        return new JAXBElement<ResultadoAltaRemesaEnvios>(_ResultadoAltaRemesaEnvios_QNAME, ResultadoAltaRemesaEnvios.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelarEnvio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", name = "cancelarEnvio")
    public JAXBElement<CancelarEnvio> createCancelarEnvio(CancelarEnvio value) {
        return new JAXBElement<CancelarEnvio>(_CancelarEnvio_QNAME, CancelarEnvio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoEnvioLigero }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", name = "infoEnvioLigero")
    public JAXBElement<InfoEnvioLigero> createInfoEnvioLigero(InfoEnvioLigero value) {
        return new JAXBElement<InfoEnvioLigero>(_InfoEnvioLigero_QNAME, InfoEnvioLigero.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaRemesaEnvios }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", name = "altaRemesaEnvios")
    public JAXBElement<AltaRemesaEnvios> createAltaRemesaEnvios(AltaRemesaEnvios value) {
        return new JAXBElement<AltaRemesaEnvios>(_AltaRemesaEnvios_QNAME, AltaRemesaEnvios.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link java.lang.Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", name = "Exception")
    public JAXBElement<java.lang.Exception> createException(java.lang.Exception value) {
        return new JAXBElement<java.lang.Exception>(_Exception_QNAME, java.lang.Exception.class, null, value);
    }

}


package es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.gob.administracionelectronica.notifica.ws.sincronizarenvio._1_0.infoenvioligero package. 
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

    private final static QName _InfoEnvioLigero_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", "infoEnvioLigero");
    private final static QName _RespuestaInfoEnvioLigero_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", "respuestaInfoEnvioLigero");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.gob.administracionelectronica.notifica.ws.sincronizarenvio._1_0.infoenvioligero
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InfoEnvioLigero }
     * 
     */
    public InfoEnvioLigero createInfoEnvioLigero() {
        return new InfoEnvioLigero();
    }

    /**
     * Create an instance of {@link RespuestaInfoEnvioLigero }
     * 
     */
    public RespuestaInfoEnvioLigero createRespuestaInfoEnvioLigero() {
        return new RespuestaInfoEnvioLigero();
    }

    /**
     * Create an instance of {@link Datados }
     * 
     */
    public Datados createDatados() {
        return new Datados();
    }

    /**
     * Create an instance of {@link Datado }
     * 
     */
    public Datado createDatado() {
        return new Datado();
    }

    /**
     * Create an instance of {@link Certificacion }
     * 
     */
    public Certificacion createCertificacion() {
        return new Certificacion();
    }

    /**
     * Create an instance of {@link Acuse }
     * 
     */
    public Acuse createAcuse() {
        return new Acuse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoEnvioLigero }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", name = "infoEnvioLigero")
    public JAXBElement<InfoEnvioLigero> createInfoEnvioLigero(InfoEnvioLigero value) {
        return new JAXBElement<InfoEnvioLigero>(_InfoEnvioLigero_QNAME, InfoEnvioLigero.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaInfoEnvioLigero }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero", name = "respuestaInfoEnvioLigero")
    public JAXBElement<RespuestaInfoEnvioLigero> createRespuestaInfoEnvioLigero(RespuestaInfoEnvioLigero value) {
        return new JAXBElement<RespuestaInfoEnvioLigero>(_RespuestaInfoEnvioLigero_QNAME, RespuestaInfoEnvioLigero.class, null, value);
    }

}

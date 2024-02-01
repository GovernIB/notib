
package es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOe;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.gob.administracionelectronica.notifica.ws.sincronizarenvio._1_0.sincronizarenviooe package. 
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

    private final static QName _SincronizarEnvioOE_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", "sincronizarEnvioOE");
    private final static QName _RespuestaSincronizarEnvioOE_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", "respuestaSincronizarEnvioOE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.gob.administracionelectronica.notifica.ws.sincronizarenvio._1_0.sincronizarenviooe
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SincronizarEnvioOE }
     * 
     */
    public SincronizarEnvioOE createSincronizarEnvioOE() {
        return new SincronizarEnvioOE();
    }

    /**
     * Create an instance of {@link RespuestaSincronizarEnvioOE }
     * 
     */
    public RespuestaSincronizarEnvioOE createRespuestaSincronizarEnvioOE() {
        return new RespuestaSincronizarEnvioOE();
    }

    /**
     * Create an instance of {@link Receptor }
     * 
     */
    public Receptor createReceptor() {
        return new Receptor();
    }

    /**
     * Create an instance of {@link Acuse }
     * 
     */
    public Acuse createAcuse() {
        return new Acuse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SincronizarEnvioOE }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", name = "sincronizarEnvioOE")
    public JAXBElement<SincronizarEnvioOE> createSincronizarEnvioOE(SincronizarEnvioOE value) {
        return new JAXBElement<SincronizarEnvioOE>(_SincronizarEnvioOE_QNAME, SincronizarEnvioOE.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaSincronizarEnvioOE }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE", name = "respuestaSincronizarEnvioOE")
    public JAXBElement<RespuestaSincronizarEnvioOE> createRespuestaSincronizarEnvioOE(RespuestaSincronizarEnvioOE value) {
        return new JAXBElement<RespuestaSincronizarEnvioOE>(_RespuestaSincronizarEnvioOE_QNAME, RespuestaSincronizarEnvioOE.class, null, value);
    }

}

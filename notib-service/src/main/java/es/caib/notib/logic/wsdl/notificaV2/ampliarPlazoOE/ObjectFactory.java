
package es.caib.notib.logic.wsdl.notificaV2.ampliarPlazoOE;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.gob.administracionelectronica.notifica.ws.sincronizarenvio._1_0.ampliarplazooe package. 
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

    private final static QName _AmpliarPlazoOE_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/ampliarPlazoOE", "ampliarPlazoOE");
    private final static QName _RespuestaAmpliarPlazoOE_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/ampliarPlazoOE", "respuestaAmpliarPlazoOE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.gob.administracionelectronica.notifica.ws.sincronizarenvio._1_0.ampliarplazooe
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AmpliarPlazoOE }
     * 
     */
    public AmpliarPlazoOE createAmpliarPlazoOE() {
        return new AmpliarPlazoOE();
    }

    /**
     * Create an instance of {@link RespuestaAmpliarPlazoOE }
     * 
     */
    public RespuestaAmpliarPlazoOE createRespuestaAmpliarPlazoOE() {
        return new RespuestaAmpliarPlazoOE();
    }

    /**
     * Create an instance of {@link Envios }
     * 
     */
    public Envios createEnvios() {
        return new Envios();
    }

    /**
     * Create an instance of {@link AmpliacionesPlazo }
     * 
     */
    public AmpliacionesPlazo createAmpliacionesPlazo() {
        return new AmpliacionesPlazo();
    }

    /**
     * Create an instance of {@link AmpliacionPlazo }
     * 
     */
    public AmpliacionPlazo createAmpliacionPlazo() {
        return new AmpliacionPlazo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AmpliarPlazoOE }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/ampliarPlazoOE", name = "ampliarPlazoOE")
    public JAXBElement<AmpliarPlazoOE> createAmpliarPlazoOE(AmpliarPlazoOE value) {
        return new JAXBElement<AmpliarPlazoOE>(_AmpliarPlazoOE_QNAME, AmpliarPlazoOE.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaAmpliarPlazoOE }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/ampliarPlazoOE", name = "respuestaAmpliarPlazoOE")
    public JAXBElement<RespuestaAmpliarPlazoOE> createRespuestaAmpliarPlazoOE(RespuestaAmpliarPlazoOE value) {
        return new JAXBElement<RespuestaAmpliarPlazoOE>(_RespuestaAmpliarPlazoOE_QNAME, RespuestaAmpliarPlazoOE.class, null, value);
    }

}

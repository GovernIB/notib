
package es.caib.notib.plugin.cie.nexea.cancelarenvio;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.cancelarenvio package. 
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

    private final static QName _ResultadocancelarEnvio_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/cancelarEnvio", "resultadocancelarEnvio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.cancelarenvio
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResultadocancelarEnvio }
     * 
     */
    public ResultadocancelarEnvio createResultadocancelarEnvio() {
        return new ResultadocancelarEnvio();
    }

    /**
     * Create an instance of {@link CancelarEnvio }
     * 
     */
    public CancelarEnvio createCancelarEnvio() {
        return new CancelarEnvio();
    }

    /**
     * Create an instance of {@link Filtros }
     * 
     */
    public Filtros createFiltros() {
        return new Filtros();
    }

    /**
     * Create an instance of {@link Filtro }
     * 
     */
    public Filtro createFiltro() {
        return new Filtro();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultadocancelarEnvio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/cancelarEnvio", name = "resultadocancelarEnvio")
    public JAXBElement<ResultadocancelarEnvio> createResultadocancelarEnvio(ResultadocancelarEnvio value) {
        return new JAXBElement<ResultadocancelarEnvio>(_ResultadocancelarEnvio_QNAME, ResultadocancelarEnvio.class, null, value);
    }

}

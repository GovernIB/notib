
package es.caib.notib.logic.wsdl.adviser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.notib.logic.wsdl.adviser package. 
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

    private final static QName _SincronizarEnvio_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/sincronizarEnvio", "sincronizarEnvio");
    private final static QName _ResultadoSincronizarEnvio_QNAME = new QName("http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/sincronizarEnvio", "resultadoSincronizarEnvio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.notib.logic.wsdl.adviser
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Opcion }
     * 
     */
    public Opcion createOpcion() {
        return new Opcion();
    }

    /**
     * Create an instance of {@link Opciones }
     * 
     */
    public Opciones createOpciones() {
        return new Opciones();
    }

    /**
     * Create an instance of {@link ResultadoSincronizarEnvio }
     * 
     */
    public ResultadoSincronizarEnvio createResultadoSincronizarEnvio() {
        return new ResultadoSincronizarEnvio();
    }

    /**
     * Create an instance of {@link SincronizarEnvio }
     * 
     */
    public SincronizarEnvio createSincronizarEnvio() {
        return new SincronizarEnvio();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SincronizarEnvio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/sincronizarEnvio", name = "sincronizarEnvio")
    public JAXBElement<SincronizarEnvio> createSincronizarEnvio(SincronizarEnvio value) {
        return new JAXBElement<SincronizarEnvio>(_SincronizarEnvio_QNAME, SincronizarEnvio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultadoSincronizarEnvio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/sincronizarEnvio", name = "resultadoSincronizarEnvio")
    public JAXBElement<ResultadoSincronizarEnvio> createResultadoSincronizarEnvio(ResultadoSincronizarEnvio value) {
        return new JAXBElement<ResultadoSincronizarEnvio>(_ResultadoSincronizarEnvio_QNAME, ResultadoSincronizarEnvio.class, null, value);
    }

}

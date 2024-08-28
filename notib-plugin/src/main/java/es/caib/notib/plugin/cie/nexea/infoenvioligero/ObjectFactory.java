
package es.caib.notib.plugin.cie.nexea.infoenvioligero;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.infoenvioligero package. 
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

    private final static QName _ResultadoinfoEnvioLigero_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero", "resultadoinfoEnvioLigero");
    private final static QName _InfoEnvioLigero_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero", "infoEnvioLigero");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.administracionelectronica_gob_es.notifica.ws.notificaws_v2._1_0.infoenvioligero
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
     * Create an instance of {@link ResultadoinfoEnvioLigero }
     * 
     */
    public ResultadoinfoEnvioLigero createResultadoinfoEnvioLigero() {
        return new ResultadoinfoEnvioLigero();
    }

    /**
     * Create an instance of {@link Datados }
     * 
     */
    public Datados createDatados() {
        return new Datados();
    }

    /**
     * Create an instance of {@link Campo }
     * 
     */
    public Campo createCampo() {
        return new Campo();
    }

    /**
     * Create an instance of {@link Datado }
     * 
     */
    public Datado createDatado() {
        return new Datado();
    }

    /**
     * Create an instance of {@link Acuse }
     * 
     */
    public Acuse createAcuse() {
        return new Acuse();
    }

    /**
     * Create an instance of {@link CamposP }
     * 
     */
    public CamposP createCamposP() {
        return new CamposP();
    }

    /**
     * Create an instance of {@link Certificacion }
     * 
     */
    public Certificacion createCertificacion() {
        return new Certificacion();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultadoinfoEnvioLigero }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero", name = "resultadoinfoEnvioLigero")
    public JAXBElement<ResultadoinfoEnvioLigero> createResultadoinfoEnvioLigero(ResultadoinfoEnvioLigero value) {
        return new JAXBElement<ResultadoinfoEnvioLigero>(_ResultadoinfoEnvioLigero_QNAME, ResultadoinfoEnvioLigero.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoEnvioLigero }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero", name = "infoEnvioLigero")
    public JAXBElement<InfoEnvioLigero> createInfoEnvioLigero(InfoEnvioLigero value) {
        return new JAXBElement<InfoEnvioLigero>(_InfoEnvioLigero_QNAME, InfoEnvioLigero.class, null, value);
    }

}

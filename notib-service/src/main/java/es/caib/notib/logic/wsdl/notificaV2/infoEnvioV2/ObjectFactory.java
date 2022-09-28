
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.*;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2 package. 
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

    private final static QName _ResultadoInfoEnvioV2_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2", "resultadoInfoEnvioV2");
    private final static QName _InfoEnvioV2_QNAME = new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2", "infoEnvioV2");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2 }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2 createInfoEnvioV2() {
        return new es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2();
    }

    /**
     * Create an instance of {@link ResultadoInfoEnvioV2 }
     * 
     */
    public ResultadoInfoEnvioV2 createResultadoInfoEnvioV2() {
        return new ResultadoInfoEnvioV2();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados createDatados() {
        return new Datados();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion createOpcion() {
        return new Opcion();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datado }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datado createDatado() {
        return new Datado();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona createPersona() {
        return new Persona();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal createEntregaPostal() {
        return new EntregaPostal();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion createCertificacion() {
        return new Certificacion();
    }

    /**
     * Create an instance of {@link Procedimiento }
     * 
     */
    public Procedimiento createProcedimiento() {
        return new Procedimiento();
    }

    /**
     * Create an instance of {@link OrganismoPagadorPostal }
     * 
     */
    public OrganismoPagadorPostal createOrganismoPagadorPostal() {
        return new OrganismoPagadorPostal();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR createCodigoDIR() {
        return new CodigoDIR();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones createOpciones() {
        return new Opciones();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios createDestinatarios() {
        return new Destinatarios();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario createDestinatario() {
        return new Destinatario();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento createDocumento() {
        return new Documento();
    }

    /**
     * Create an instance of {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH }
     * 
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH createEntregaDEH() {
        return new EntregaDEH();
    }

    /**
     * Create an instance of {@link OrganismoPagadorCIE }
     * 
     */
    public OrganismoPagadorCIE createOrganismoPagadorCIE() {
        return new OrganismoPagadorCIE();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultadoInfoEnvioV2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2", name = "resultadoInfoEnvioV2")
    public JAXBElement<ResultadoInfoEnvioV2> createResultadoInfoEnvioV2(ResultadoInfoEnvioV2 value) {
        return new JAXBElement<ResultadoInfoEnvioV2>(_ResultadoInfoEnvioV2_QNAME, ResultadoInfoEnvioV2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2", name = "infoEnvioV2")
    public JAXBElement<es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2> createInfoEnvioV2(es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2 value) {
        return new JAXBElement<es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2>(_InfoEnvioV2_QNAME, InfoEnvioV2.class, null, value);
    }

}


package es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import es.caib.notib.logic.wsdl.notificaV2.common.Opciones;


/**
 * <p>Java class for SincronizarEnvioOE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SincronizarEnvioOE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="organismoEmisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoEntrega" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE}TipoEntrega"/>
 *         &lt;element name="modoNotificacion" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE}TipoModoNotificacion"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaEstado" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptor" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE}Receptor" minOccurs="0"/>
 *         &lt;element name="acusePDF" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE}Acuse" minOccurs="0"/>
 *         &lt;element name="acuseXML" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/sincronizarEnvioOE}Acuse" minOccurs="0"/>
 *         &lt;element name="opcionesSincronizarEnvioOE" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/common}Opciones" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SincronizarEnvioOE", propOrder = {
    "organismoEmisor",
    "identificador",
    "tipoEntrega",
    "modoNotificacion",
    "estado",
    "fechaEstado",
    "motivo",
    "receptor",
    "acusePDF",
    "acuseXML",
    "opcionesSincronizarEnvioOE"
})
public class SincronizarEnvioOE {

    @XmlElement(required = true)
    protected String organismoEmisor;
    @XmlElement(required = true)
    protected String identificador;
    @XmlElement(required = true)
    protected BigInteger tipoEntrega;
    @XmlElement(required = true)
    protected BigInteger modoNotificacion;
    @XmlElement(required = true)
    protected String estado;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaEstado;
    protected String motivo;
    protected Receptor receptor;
    protected Acuse acusePDF;
    protected Acuse acuseXML;
    protected Opciones opcionesSincronizarEnvioOE;

    /**
     * Gets the value of the organismoEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismoEmisor() {
        return organismoEmisor;
    }

    /**
     * Sets the value of the organismoEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismoEmisor(String value) {
        this.organismoEmisor = value;
    }

    /**
     * Gets the value of the identificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

    /**
     * Gets the value of the tipoEntrega property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTipoEntrega() {
        return tipoEntrega;
    }

    /**
     * Sets the value of the tipoEntrega property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTipoEntrega(BigInteger value) {
        this.tipoEntrega = value;
    }

    /**
     * Gets the value of the modoNotificacion property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getModoNotificacion() {
        return modoNotificacion;
    }

    /**
     * Sets the value of the modoNotificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setModoNotificacion(BigInteger value) {
        this.modoNotificacion = value;
    }

    /**
     * Gets the value of the estado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Sets the value of the estado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Gets the value of the fechaEstado property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaEstado() {
        return fechaEstado;
    }

    /**
     * Sets the value of the fechaEstado property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaEstado(XMLGregorianCalendar value) {
        this.fechaEstado = value;
    }

    /**
     * Gets the value of the motivo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Sets the value of the motivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivo(String value) {
        this.motivo = value;
    }

    /**
     * Gets the value of the receptor property.
     * 
     * @return
     *     possible object is
     *     {@link Receptor }
     *     
     */
    public Receptor getReceptor() {
        return receptor;
    }

    /**
     * Sets the value of the receptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Receptor }
     *     
     */
    public void setReceptor(Receptor value) {
        this.receptor = value;
    }

    /**
     * Gets the value of the acusePDF property.
     * 
     * @return
     *     possible object is
     *     {@link Acuse }
     *     
     */
    public Acuse getAcusePDF() {
        return acusePDF;
    }

    /**
     * Sets the value of the acusePDF property.
     * 
     * @param value
     *     allowed object is
     *     {@link Acuse }
     *     
     */
    public void setAcusePDF(Acuse value) {
        this.acusePDF = value;
    }

    /**
     * Gets the value of the acuseXML property.
     * 
     * @return
     *     possible object is
     *     {@link Acuse }
     *     
     */
    public Acuse getAcuseXML() {
        return acuseXML;
    }

    /**
     * Sets the value of the acuseXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link Acuse }
     *     
     */
    public void setAcuseXML(Acuse value) {
        this.acuseXML = value;
    }

    /**
     * Gets the value of the opcionesSincronizarEnvioOE property.
     * 
     * @return
     *     possible object is
     *     {@link Opciones }
     *     
     */
    public Opciones getOpcionesSincronizarEnvioOE() {
        return opcionesSincronizarEnvioOE;
    }

    /**
     * Sets the value of the opcionesSincronizarEnvioOE property.
     * 
     * @param value
     *     allowed object is
     *     {@link Opciones }
     *     
     */
    public void setOpcionesSincronizarEnvioOE(Opciones value) {
        this.opcionesSincronizarEnvioOE = value;
    }

}

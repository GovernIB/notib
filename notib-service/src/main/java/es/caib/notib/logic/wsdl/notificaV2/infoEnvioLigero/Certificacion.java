
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.notib.logic.wsdl.notificaV2.common.Opciones;


/**
 * <p>Java class for Certificacion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Certificacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acusePDF" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero}Acuse" minOccurs="0"/>
 *         &lt;element name="acuseXML" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero}Acuse" minOccurs="0"/>
 *         &lt;element name="fechaCertificacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="origen" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="metadatos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opcionesCertificacion" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/common}Opciones" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Certificacion", propOrder = {
    "acusePDF",
    "acuseXML",
    "fechaCertificacion",
    "origen",
    "metadatos",
    "opcionesCertificacion"
})
public class Certificacion {

    protected Acuse acusePDF;
    protected Acuse acuseXML;
    @XmlElement(required = true)
    protected String fechaCertificacion;
    @XmlElement(required = true)
    protected String origen;
    protected String metadatos;
    protected Opciones opcionesCertificacion;

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
     * Gets the value of the fechaCertificacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaCertificacion() {
        return fechaCertificacion;
    }

    /**
     * Sets the value of the fechaCertificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaCertificacion(String value) {
        this.fechaCertificacion = value;
    }

    /**
     * Gets the value of the origen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Sets the value of the origen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigen(String value) {
        this.origen = value;
    }

    /**
     * Gets the value of the metadatos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetadatos() {
        return metadatos;
    }

    /**
     * Sets the value of the metadatos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetadatos(String value) {
        this.metadatos = value;
    }

    /**
     * Gets the value of the opcionesCertificacion property.
     * 
     * @return
     *     possible object is
     *     {@link Opciones }
     *     
     */
    public Opciones getOpcionesCertificacion() {
        return opcionesCertificacion;
    }

    /**
     * Sets the value of the opcionesCertificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Opciones }
     *     
     */
    public void setOpcionesCertificacion(Opciones value) {
        this.opcionesCertificacion = value;
    }

}

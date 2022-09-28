
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Certificacion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Certificacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="contenidoCertificacion" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaCertificacion" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="origen" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="csv" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="metadatos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opcionesCertificacion" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Opciones" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Certificacion", propOrder = {

})
public class Certificacion {

    @XmlElement(required = true)
    protected byte[] contenidoCertificacion;
    @XmlElement(required = true)
    protected String hash;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaCertificacion;
    @XmlElement(required = true)
    protected String origen;
    protected String csv;
    @XmlElement(required = true)
    protected String mime;
    @XmlElement(required = true)
    protected String size;
    protected String metadatos;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones opcionesCertificacion;

    /**
     * Gets the value of the contenidoCertificacion property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContenidoCertificacion() {
        return contenidoCertificacion;
    }

    /**
     * Sets the value of the contenidoCertificacion property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContenidoCertificacion(byte[] value) {
        this.contenidoCertificacion = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the fechaCertificacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaCertificacion() {
        return fechaCertificacion;
    }

    /**
     * Sets the value of the fechaCertificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaCertificacion(XMLGregorianCalendar value) {
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
     * Gets the value of the csv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsv() {
        return csv;
    }

    /**
     * Sets the value of the csv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsv(String value) {
        this.csv = value;
    }

    /**
     * Gets the value of the mime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMime() {
        return mime;
    }

    /**
     * Sets the value of the mime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMime(String value) {
        this.mime = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSize(String value) {
        this.size = value;
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
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones getOpcionesCertificacion() {
        return opcionesCertificacion;
    }

    /**
     * Sets the value of the opcionesCertificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     *     
     */
    public void setOpcionesCertificacion(Opciones value) {
        this.opcionesCertificacion = value;
    }

}

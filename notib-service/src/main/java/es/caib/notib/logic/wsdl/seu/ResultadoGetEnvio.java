
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for resultadoGetEnvio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoGetEnvio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="envio_destinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="concepto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="organismo_remisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha_notificacion" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fecha_actualizacion" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="vinculo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipo_envio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="canal_envio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoGetEnvio", propOrder = {

})
public class ResultadoGetEnvio {

    @XmlElement(name = "envio_destinatario", required = true, nillable = true)
    protected String envioDestinatario;
    @XmlElement(required = true, nillable = true)
    protected String estado;
    @XmlElement(required = true, nillable = true)
    protected String concepto;
    @XmlElement(name = "organismo_remisor", required = true, nillable = true)
    protected String organismoRemisor;
    @XmlElement(name = "fecha_notificacion", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaNotificacion;
    @XmlElement(name = "fecha_actualizacion", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaActualizacion;
    @XmlElement(required = true, nillable = true)
    protected String vinculo;
    @XmlElement(name = "tipo_envio", required = true, nillable = true)
    protected String tipoEnvio;
    @XmlElement(name = "canal_envio", required = true, nillable = true)
    protected String canalEnvio;

    /**
     * Gets the value of the envioDestinatario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnvioDestinatario() {
        return envioDestinatario;
    }

    /**
     * Sets the value of the envioDestinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnvioDestinatario(String value) {
        this.envioDestinatario = value;
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
     * Gets the value of the concepto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Sets the value of the concepto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcepto(String value) {
        this.concepto = value;
    }

    /**
     * Gets the value of the organismoRemisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismoRemisor() {
        return organismoRemisor;
    }

    /**
     * Sets the value of the organismoRemisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismoRemisor(String value) {
        this.organismoRemisor = value;
    }

    /**
     * Gets the value of the fechaNotificacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaNotificacion() {
        return fechaNotificacion;
    }

    /**
     * Sets the value of the fechaNotificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaNotificacion(XMLGregorianCalendar value) {
        this.fechaNotificacion = value;
    }

    /**
     * Gets the value of the fechaActualizacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaActualizacion() {
        return fechaActualizacion;
    }

    /**
     * Sets the value of the fechaActualizacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaActualizacion(XMLGregorianCalendar value) {
        this.fechaActualizacion = value;
    }

    /**
     * Gets the value of the vinculo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVinculo() {
        return vinculo;
    }

    /**
     * Sets the value of the vinculo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVinculo(String value) {
        this.vinculo = value;
    }

    /**
     * Gets the value of the tipoEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoEnvio() {
        return tipoEnvio;
    }

    /**
     * Sets the value of the tipoEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoEnvio(String value) {
        this.tipoEnvio = value;
    }

    /**
     * Gets the value of the canalEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanalEnvio() {
        return canalEnvio;
    }

    /**
     * Sets the value of the canalEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanalEnvio(String value) {
        this.canalEnvio = value;
    }

}

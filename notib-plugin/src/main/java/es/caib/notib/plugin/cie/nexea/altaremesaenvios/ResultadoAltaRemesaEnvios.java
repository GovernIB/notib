
package es.caib.notib.plugin.cie.nexea.altaremesaenvios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoAltaRemesaEnvios complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoAltaRemesaEnvios">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoOrganismoEmisor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaCreacion" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" minOccurs="0"/>
 *         &lt;element name="resultadoEnvios" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}ResultadoEnvios" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoAltaRemesaEnvios", propOrder = {

})
public class ResultadoAltaRemesaEnvios {

    @XmlElement(required = true)
    protected String codigoRespuesta;
    @XmlElement(required = true)
    protected String descripcionRespuesta;
    protected String codigoOrganismoEmisor;
    protected Object fechaCreacion;
    protected ResultadoEnvios resultadoEnvios;

    /**
     * Gets the value of the codigoRespuesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    /**
     * Sets the value of the codigoRespuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRespuesta(String value) {
        this.codigoRespuesta = value;
    }

    /**
     * Gets the value of the descripcionRespuesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionRespuesta() {
        return descripcionRespuesta;
    }

    /**
     * Sets the value of the descripcionRespuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionRespuesta(String value) {
        this.descripcionRespuesta = value;
    }

    /**
     * Gets the value of the codigoOrganismoEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoOrganismoEmisor() {
        return codigoOrganismoEmisor;
    }

    /**
     * Sets the value of the codigoOrganismoEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoOrganismoEmisor(String value) {
        this.codigoOrganismoEmisor = value;
    }

    /**
     * Gets the value of the fechaCreacion property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Sets the value of the fechaCreacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setFechaCreacion(Object value) {
        this.fechaCreacion = value;
    }

    /**
     * Gets the value of the resultadoEnvios property.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoEnvios }
     *     
     */
    public ResultadoEnvios getResultadoEnvios() {
        return resultadoEnvios;
    }

    /**
     * Sets the value of the resultadoEnvios property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoEnvios }
     *     
     */
    public void setResultadoEnvios(ResultadoEnvios value) {
        this.resultadoEnvios = value;
    }

}

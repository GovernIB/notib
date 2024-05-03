
package es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidacionSimpleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidacionSimpleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoResultado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descResultado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="excepcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidacionSimpleInfo", propOrder = {
    "codigoResultado",
    "descResultado",
    "excepcion"
})
public class ValidacionSimpleInfo {

    @XmlElement(required = true)
    protected String codigoResultado;
    @XmlElement(required = true)
    protected String descResultado;
    protected String excepcion;

    /**
     * Gets the value of the codigoResultado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoResultado() {
        return codigoResultado;
    }

    /**
     * Sets the value of the codigoResultado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoResultado(String value) {
        this.codigoResultado = value;
    }

    /**
     * Gets the value of the descResultado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescResultado() {
        return descResultado;
    }

    /**
     * Sets the value of the descResultado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescResultado(String value) {
        this.descResultado = value;
    }

    /**
     * Gets the value of the excepcion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExcepcion() {
        return excepcion;
    }

    /**
     * Sets the value of the excepcion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExcepcion(String value) {
        this.excepcion = value;
    }

}

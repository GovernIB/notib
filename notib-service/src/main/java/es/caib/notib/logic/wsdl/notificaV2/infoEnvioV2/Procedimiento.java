
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Procedimiento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Procedimiento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoSia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionSia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Procedimiento", propOrder = {

})
public class Procedimiento {

    @XmlElement(required = true)
    protected String codigoSia;
    @XmlElement(required = true)
    protected String descripcionSia;

    /**
     * Gets the value of the codigoSia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoSia() {
        return codigoSia;
    }

    /**
     * Sets the value of the codigoSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoSia(String value) {
        this.codigoSia = value;
    }

    /**
     * Gets the value of the descripcionSia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionSia() {
        return descripcionSia;
    }

    /**
     * Sets the value of the descripcionSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionSia(String value) {
        this.descripcionSia = value;
    }

}

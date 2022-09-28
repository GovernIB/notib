
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for opciones_emision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="opciones_emision">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="retardo_postal_deh" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="caducidad" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "opciones_emision", propOrder = {

})
public class OpcionesEmision {

    @XmlElement(name = "retardo_postal_deh", required = true, type = Integer.class, nillable = true)
    protected Integer retardoPostalDeh;
    @XmlElement(required = true, nillable = true)
    protected String caducidad;

    /**
     * Gets the value of the retardoPostalDeh property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRetardoPostalDeh() {
        return retardoPostalDeh;
    }

    /**
     * Sets the value of the retardoPostalDeh property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRetardoPostalDeh(Integer value) {
        this.retardoPostalDeh = value;
    }

    /**
     * Gets the value of the caducidad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaducidad() {
        return caducidad;
    }

    /**
     * Sets the value of the caducidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaducidad(String value) {
        this.caducidad = value;
    }

}

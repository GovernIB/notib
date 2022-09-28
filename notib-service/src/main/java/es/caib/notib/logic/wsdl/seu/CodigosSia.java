
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for codigos_sia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="codigos_sia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigo_sia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "codigos_sia", propOrder = {

})
public class CodigosSia {

    @XmlElement(name = "codigo_sia", required = true, nillable = true)
    protected String codigoSia;

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

}


package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for consulta_cie complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consulta_cie">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="organismo_emisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consulta_cie", propOrder = {
    "organismoEmisor"
})
public class ConsultaCie {

    @XmlElement(name = "organismo_emisor", required = true)
    protected String organismoEmisor;

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

}

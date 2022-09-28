
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for consultaCies complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultaCies">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="consulta_cies" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}consulta_cie" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaCies", propOrder = {
    "consultaCies"
})
public class ConsultaCies {

    @XmlElement(name = "consulta_cies")
    protected ConsultaCie consultaCies;

    /**
     * Gets the value of the consultaCies property.
     * 
     * @return
     *     possible object is
     *     {@link ConsultaCie }
     *     
     */
    public ConsultaCie getConsultaCies() {
        return consultaCies;
    }

    /**
     * Sets the value of the consultaCies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsultaCie }
     *     
     */
    public void setConsultaCies(ConsultaCie value) {
        this.consultaCies = value;
    }

}

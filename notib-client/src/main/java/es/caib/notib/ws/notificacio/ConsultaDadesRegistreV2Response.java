
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for consultaDadesRegistreV2Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultaDadesRegistreV2Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.caib.es/notib/ws/notificacio}respostaConsultaDadesRegistreV2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaDadesRegistreV2Response", propOrder = {
    "_return"
})
public class ConsultaDadesRegistreV2Response {

    @XmlElement(name = "return")
    protected RespostaConsultaDadesRegistreV2 _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link RespostaConsultaDadesRegistreV2 }
     *     
     */
    public RespostaConsultaDadesRegistreV2 getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespostaConsultaDadesRegistreV2 }
     *     
     */
    public void setReturn(RespostaConsultaDadesRegistreV2 value) {
        this._return = value;
    }

}

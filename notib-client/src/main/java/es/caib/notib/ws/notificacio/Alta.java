
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="notificacio" type="{http://www.caib.es/notib/ws/notificacio}notificacio"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alta", propOrder = {
    "notificacio"
})
public class Alta {

    @XmlElement(required = true)
    protected Notificacio notificacio;

    /**
     * Gets the value of the notificacio property.
     * 
     * @return
     *     possible object is
     *     {@link Notificacio }
     *     
     */
    public Notificacio getNotificacio() {
        return notificacio;
    }

    /**
     * Sets the value of the notificacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Notificacio }
     *     
     */
    public void setNotificacio(Notificacio value) {
        this.notificacio = value;
    }

}

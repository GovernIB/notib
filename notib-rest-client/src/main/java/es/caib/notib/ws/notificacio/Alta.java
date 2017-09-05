
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para alta complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
    protected Notificacio_Type notificacio;

    /**
     * Obtiene el valor de la propiedad notificacio.
     * 
     * @return
     *     possible object is
     *     {@link Notificacio_Type }
     *     
     */
    public Notificacio_Type getNotificacio() {
        return notificacio;
    }

    /**
     * Define el valor de la propiedad notificacio.
     * 
     * @param value
     *     allowed object is
     *     {@link Notificacio_Type }
     *     
     */
    public void setNotificacio(Notificacio_Type value) {
        this.notificacio = value;
    }

}

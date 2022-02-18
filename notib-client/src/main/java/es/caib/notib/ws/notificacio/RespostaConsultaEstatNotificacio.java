
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for respostaConsultaEstatNotificacio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaEstatNotificacio">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}notificacioEstatEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respostaConsultaEstatNotificacio", propOrder = {
    "estat"
})
public class RespostaConsultaEstatNotificacio
    extends RespostaBase
{

    protected NotificacioEstatEnum estat;

    /**
     * Gets the value of the estat property.
     * 
     * @return
     *     possible object is
     *     {@link NotificacioEstatEnum }
     *     
     */
    public NotificacioEstatEnum getEstat() {
        return estat;
    }

    /**
     * Sets the value of the estat property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificacioEstatEnum }
     *     
     */
    public void setEstat(NotificacioEstatEnum value) {
        this.estat = value;
    }

}

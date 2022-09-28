
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for infoEnvio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="infoEnvio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="info_envio" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}info_enviament" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoEnvio", propOrder = {
    "infoEnvio"
})
public class InfoEnvio {

    @XmlElement(name = "info_envio")
    protected InfoEnviament infoEnvio;

    /**
     * Gets the value of the infoEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link InfoEnviament }
     *     
     */
    public InfoEnviament getInfoEnvio() {
        return infoEnvio;
    }

    /**
     * Sets the value of the infoEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoEnviament }
     *     
     */
    public void setInfoEnvio(InfoEnviament value) {
        this.infoEnvio = value;
    }

}

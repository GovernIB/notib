
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for info_enviament complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="info_enviament">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="envio_destinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "info_enviament", propOrder = {
    "envioDestinatario"
})
public class InfoEnviament {

    @XmlElement(name = "envio_destinatario", required = true)
    protected String envioDestinatario;

    /**
     * Gets the value of the envioDestinatario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnvioDestinatario() {
        return envioDestinatario;
    }

    /**
     * Sets the value of the envioDestinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnvioDestinatario(String value) {
        this.envioDestinatario = value;
    }

}

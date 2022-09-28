
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for altaEnvio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="altaEnvio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="envio_type" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_envio" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "altaEnvio", propOrder = {
    "envioType"
})
public class AltaEnvio {

    @XmlElement(name = "envio_type")
    protected TipoEnvio envioType;

    /**
     * Gets the value of the envioType property.
     * 
     * @return
     *     possible object is
     *     {@link TipoEnvio }
     *     
     */
    public TipoEnvio getEnvioType() {
        return envioType;
    }

    /**
     * Sets the value of the envioType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoEnvio }
     *     
     */
    public void setEnvioType(TipoEnvio value) {
        this.envioType = value;
    }

}

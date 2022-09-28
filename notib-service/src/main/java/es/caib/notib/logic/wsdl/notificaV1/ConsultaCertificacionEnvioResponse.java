
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for consultaCertificacionEnvioResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultaCertificacionEnvioResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}resultado_certificacion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaCertificacionEnvioResponse", propOrder = {
    "_return"
})
public class ConsultaCertificacionEnvioResponse {

    @XmlElement(name = "return")
    protected ResultadoCertificacion _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoCertificacion }
     *     
     */
    public ResultadoCertificacion getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoCertificacion }
     *     
     */
    public void setReturn(ResultadoCertificacion value) {
        this._return = value;
    }

}

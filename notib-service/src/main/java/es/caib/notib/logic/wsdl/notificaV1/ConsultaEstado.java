
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for consultaEstado complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultaEstado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificador_envio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaEstado", propOrder = {
    "identificadorEnvio"
})
public class ConsultaEstado {

    @XmlElement(name = "identificador_envio")
    protected String identificadorEnvio;

    /**
     * Gets the value of the identificadorEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorEnvio() {
        return identificadorEnvio;
    }

    /**
     * Sets the value of the identificadorEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorEnvio(String value) {
        this.identificadorEnvio = value;
    }

}

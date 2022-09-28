
package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultadoEnvio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultadoEnvio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nifTitular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultadoEnvio", propOrder = {

})
public class ResultadoEnvio {

    @XmlElement(required = true)
    protected String identificador;
    protected String nifTitular;

    /**
     * Gets the value of the identificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

    /**
     * Gets the value of the nifTitular property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNifTitular() {
        return nifTitular;
    }

    /**
     * Sets the value of the nifTitular property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNifTitular(String value) {
        this.nifTitular = value;
    }

}

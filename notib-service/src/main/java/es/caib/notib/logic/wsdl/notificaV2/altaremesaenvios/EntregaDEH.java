
package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntregaDEH complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EntregaDEH">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="obligado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="codigoProcedimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntregaDEH", propOrder = {

})
public class EntregaDEH {

    protected boolean obligado;
    protected String codigoProcedimiento;

    /**
     * Gets the value of the obligado property.
     * 
     */
    public boolean isObligado() {
        return obligado;
    }

    /**
     * Sets the value of the obligado property.
     * 
     */
    public void setObligado(boolean value) {
        this.obligado = value;
    }

    /**
     * Gets the value of the codigoProcedimiento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoProcedimiento() {
        return codigoProcedimiento;
    }

    /**
     * Sets the value of the codigoProcedimiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoProcedimiento(String value) {
        this.codigoProcedimiento = value;
    }

}

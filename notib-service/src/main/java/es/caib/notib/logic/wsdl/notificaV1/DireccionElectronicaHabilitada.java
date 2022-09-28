
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for direccion_electronica_habilitada complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="direccion_electronica_habilitada">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="obligado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="nif" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigo_procedimiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "direccion_electronica_habilitada", propOrder = {

})
public class DireccionElectronicaHabilitada {

    protected boolean obligado;
    @XmlElement(required = true)
    protected String nif;
    @XmlElement(name = "codigo_procedimiento", required = true, nillable = true)
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
     * Gets the value of the nif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNif() {
        return nif;
    }

    /**
     * Sets the value of the nif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNif(String value) {
        this.nif = value;
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

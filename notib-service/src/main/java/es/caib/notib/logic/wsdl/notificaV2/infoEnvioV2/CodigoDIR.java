
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodigoDIR complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodigoDIR">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigo">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="9"/>
 *               &lt;whiteSpace value="collapse"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="descripcionCodigoDIR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nifDIR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodigoDIR", propOrder = {

})
public class CodigoDIR {

    @XmlElement(required = true)
    protected String codigo;
    protected String descripcionCodigoDIR;
    protected String nifDIR;

    /**
     * Gets the value of the codigo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Sets the value of the codigo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigo(String value) {
        this.codigo = value;
    }

    /**
     * Gets the value of the descripcionCodigoDIR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionCodigoDIR() {
        return descripcionCodigoDIR;
    }

    /**
     * Sets the value of the descripcionCodigoDIR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionCodigoDIR(String value) {
        this.descripcionCodigoDIR = value;
    }

    /**
     * Gets the value of the nifDIR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNifDIR() {
        return nifDIR;
    }

    /**
     * Sets the value of the nifDIR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNifDIR(String value) {
        this.nifDIR = value;
    }

}

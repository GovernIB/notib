
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipo_pais complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo_pais">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigo_iso3166" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_pais", propOrder = {

})
public class TipoPais {

    @XmlElement(name = "codigo_iso3166", required = true, nillable = true)
    protected String codigoIso3166;
    @XmlElement(required = true, nillable = true)
    protected String nombre;

    /**
     * Gets the value of the codigoIso3166 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoIso3166() {
        return codigoIso3166;
    }

    /**
     * Sets the value of the codigoIso3166 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoIso3166(String value) {
        this.codigoIso3166 = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

}

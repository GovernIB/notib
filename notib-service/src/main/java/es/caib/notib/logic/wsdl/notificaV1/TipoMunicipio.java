
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipo_municipio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo_municipio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigo_ine" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "tipo_municipio", propOrder = {

})
public class TipoMunicipio {

    @XmlElement(name = "codigo_ine", required = true, nillable = true)
    protected String codigoIne;
    @XmlElement(required = true, nillable = true)
    protected String nombre;

    /**
     * Gets the value of the codigoIne property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoIne() {
        return codigoIne;
    }

    /**
     * Sets the value of the codigoIne property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoIne(String value) {
        this.codigoIne = value;
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

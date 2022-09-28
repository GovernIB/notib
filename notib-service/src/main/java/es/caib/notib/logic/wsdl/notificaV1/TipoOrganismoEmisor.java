
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipoOrganismoEmisor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipoOrganismoEmisor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigo_dir3" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "tipoOrganismoEmisor", propOrder = {

})
public class TipoOrganismoEmisor {

    @XmlElement(name = "codigo_dir3", required = true)
    protected String codigoDir3;
    @XmlElement(required = true)
    protected String nombre;

    /**
     * Gets the value of the codigoDir3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoDir3() {
        return codigoDir3;
    }

    /**
     * Sets the value of the codigoDir3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoDir3(String value) {
        this.codigoDir3 = value;
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

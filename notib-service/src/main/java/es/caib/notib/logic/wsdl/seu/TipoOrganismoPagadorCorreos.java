
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for tipoOrganismoPagadorCorreos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipoOrganismoPagadorCorreos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigo_dir3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numero_contrato_correos" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigo_cliente_facturacion_correos" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha_vigencia" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipoOrganismoPagadorCorreos", propOrder = {

})
public class TipoOrganismoPagadorCorreos {

    @XmlElement(name = "codigo_dir3", required = true)
    protected String codigoDir3;
    @XmlElement(name = "numero_contrato_correos", required = true)
    protected String numeroContratoCorreos;
    @XmlElement(name = "codigo_cliente_facturacion_correos", required = true)
    protected String codigoClienteFacturacionCorreos;
    @XmlElement(name = "fecha_vigencia", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fechaVigencia;

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
     * Gets the value of the numeroContratoCorreos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroContratoCorreos() {
        return numeroContratoCorreos;
    }

    /**
     * Sets the value of the numeroContratoCorreos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroContratoCorreos(String value) {
        this.numeroContratoCorreos = value;
    }

    /**
     * Gets the value of the codigoClienteFacturacionCorreos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoClienteFacturacionCorreos() {
        return codigoClienteFacturacionCorreos;
    }

    /**
     * Sets the value of the codigoClienteFacturacionCorreos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoClienteFacturacionCorreos(String value) {
        this.codigoClienteFacturacionCorreos = value;
    }

    /**
     * Gets the value of the fechaVigencia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaVigencia() {
        return fechaVigencia;
    }

    /**
     * Sets the value of the fechaVigencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaVigencia(XMLGregorianCalendar value) {
        this.fechaVigencia = value;
    }

}

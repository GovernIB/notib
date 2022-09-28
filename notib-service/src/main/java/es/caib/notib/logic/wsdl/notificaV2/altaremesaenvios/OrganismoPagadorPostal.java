
package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for OrganismoPagadorPostal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganismoPagadorPostal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoDIR3Postal" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}CodigoDIR"/>
 *         &lt;element name="numContratoPostal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codClienteFacturacionPostal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaVigenciaPostal" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganismoPagadorPostal", propOrder = {

})
public class OrganismoPagadorPostal {

    @XmlElement(required = true)
    protected String codigoDIR3Postal;
    @XmlElement(required = true)
    protected String numContratoPostal;
    @XmlElement(required = true)
    protected String codClienteFacturacionPostal;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fechaVigenciaPostal;

    /**
     * Gets the value of the codigoDIR3Postal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoDIR3Postal() {
        return codigoDIR3Postal;
    }

    /**
     * Sets the value of the codigoDIR3Postal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoDIR3Postal(String value) {
        this.codigoDIR3Postal = value;
    }

    /**
     * Gets the value of the numContratoPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumContratoPostal() {
        return numContratoPostal;
    }

    /**
     * Sets the value of the numContratoPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumContratoPostal(String value) {
        this.numContratoPostal = value;
    }

    /**
     * Gets the value of the codClienteFacturacionPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodClienteFacturacionPostal() {
        return codClienteFacturacionPostal;
    }

    /**
     * Sets the value of the codClienteFacturacionPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodClienteFacturacionPostal(String value) {
        this.codClienteFacturacionPostal = value;
    }

    /**
     * Gets the value of the fechaVigenciaPostal property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaVigenciaPostal() {
        return fechaVigenciaPostal;
    }

    /**
     * Sets the value of the fechaVigenciaPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaVigenciaPostal(XMLGregorianCalendar value) {
        this.fechaVigenciaPostal = value;
    }

}

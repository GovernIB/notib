
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for comunicacion_sede complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="comunicacion_sede">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="organismo_remisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificador_destinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comunicacion_sede", propOrder = {

})
public class ComunicacionSede {

    @XmlElement(name = "organismo_remisor", required = true)
    protected String organismoRemisor;
    @XmlElement(name = "identificador_destinatario", required = true)
    protected String identificadorDestinatario;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fecha;

    /**
     * Gets the value of the organismoRemisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismoRemisor() {
        return organismoRemisor;
    }

    /**
     * Sets the value of the organismoRemisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismoRemisor(String value) {
        this.organismoRemisor = value;
    }

    /**
     * Gets the value of the identificadorDestinatario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorDestinatario() {
        return identificadorDestinatario;
    }

    /**
     * Sets the value of the identificadorDestinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorDestinatario(String value) {
        this.identificadorDestinatario = value;
    }

    /**
     * Gets the value of the fecha property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFecha() {
        return fecha;
    }

    /**
     * Sets the value of the fecha property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFecha(XMLGregorianCalendar value) {
        this.fecha = value;
    }

}

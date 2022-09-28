
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for get_envios_agrupados2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get_envios_agrupados2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="nif" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nif_representante" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="organismo_remisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigos_sia" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfCodigos_sia"/>
 *         &lt;element name="fecha_desde" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fecha_hasta" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="rea" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get_envios_agrupados2", propOrder = {

})
public class GetEnviosAgrupados2 {

    @XmlElement(required = true)
    protected String nif;
    @XmlElement(name = "nif_representante", required = true, nillable = true)
    protected String nifRepresentante;
    @XmlElement(name = "organismo_remisor", required = true, nillable = true)
    protected String organismoRemisor;
    @XmlElement(name = "codigos_sia", required = true, nillable = true)
    protected ArrayOfCodigosSia codigosSia;
    @XmlElement(name = "fecha_desde", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaDesde;
    @XmlElement(name = "fecha_hasta", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaHasta;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean rea;

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
     * Gets the value of the nifRepresentante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNifRepresentante() {
        return nifRepresentante;
    }

    /**
     * Sets the value of the nifRepresentante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNifRepresentante(String value) {
        this.nifRepresentante = value;
    }

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
     * Gets the value of the codigosSia property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCodigosSia }
     *     
     */
    public ArrayOfCodigosSia getCodigosSia() {
        return codigosSia;
    }

    /**
     * Sets the value of the codigosSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCodigosSia }
     *     
     */
    public void setCodigosSia(ArrayOfCodigosSia value) {
        this.codigosSia = value;
    }

    /**
     * Gets the value of the fechaDesde property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaDesde() {
        return fechaDesde;
    }

    /**
     * Sets the value of the fechaDesde property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaDesde(XMLGregorianCalendar value) {
        this.fechaDesde = value;
    }

    /**
     * Gets the value of the fechaHasta property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaHasta() {
        return fechaHasta;
    }

    /**
     * Sets the value of the fechaHasta property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaHasta(XMLGregorianCalendar value) {
        this.fechaHasta = value;
    }

    /**
     * Gets the value of the rea property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRea() {
        return rea;
    }

    /**
     * Sets the value of the rea property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRea(Boolean value) {
        this.rea = value;
    }

}

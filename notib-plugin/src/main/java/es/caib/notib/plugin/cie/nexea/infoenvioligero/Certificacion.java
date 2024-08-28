
package es.caib.notib.plugin.cie.nexea.infoenvioligero;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Certificacion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Certificacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="acusePDF" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero}Acuse" form="qualified"/>
 *         &lt;element name="fechaCertificacion" type="{http://www.w3.org/2001/XMLSchema}dateTime" form="qualified"/>
 *         &lt;element name="origen" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Certificacion", propOrder = {

})
public class Certificacion {

    @XmlElement(required = true)
    protected Acuse acusePDF;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaCertificacion;
    @XmlElement(required = true)
    protected String origen;

    /**
     * Gets the value of the acusePDF property.
     * 
     * @return
     *     possible object is
     *     {@link Acuse }
     *     
     */
    public Acuse getAcusePDF() {
        return acusePDF;
    }

    /**
     * Sets the value of the acusePDF property.
     * 
     * @param value
     *     allowed object is
     *     {@link Acuse }
     *     
     */
    public void setAcusePDF(Acuse value) {
        this.acusePDF = value;
    }

    /**
     * Gets the value of the fechaCertificacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaCertificacion() {
        return fechaCertificacion;
    }

    /**
     * Sets the value of the fechaCertificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaCertificacion(XMLGregorianCalendar value) {
        this.fechaCertificacion = value;
    }

    /**
     * Gets the value of the origen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Sets the value of the origen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigen(String value) {
        this.origen = value;
    }

}

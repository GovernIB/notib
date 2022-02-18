
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for sir complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sir">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataRecepcio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataRegistreDesti" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sir", propOrder = {
    "dataRecepcio",
    "dataRegistreDesti"
})
public class Sir {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataRecepcio;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataRegistreDesti;

    /**
     * Gets the value of the dataRecepcio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRecepcio() {
        return dataRecepcio;
    }

    /**
     * Sets the value of the dataRecepcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRecepcio(XMLGregorianCalendar value) {
        this.dataRecepcio = value;
    }

    /**
     * Gets the value of the dataRegistreDesti property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRegistreDesti() {
        return dataRegistreDesti;
    }

    /**
     * Sets the value of the dataRegistreDesti property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRegistreDesti(XMLGregorianCalendar value) {
        this.dataRegistreDesti = value;
    }

}

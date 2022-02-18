
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for datat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="datat">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="errorDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}enviamentEstatEnum" minOccurs="0"/>
 *         &lt;element name="numSeguiment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="origen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptorNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptorNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datat", propOrder = {
    "data",
    "errorDescripcio",
    "estat",
    "numSeguiment",
    "origen",
    "receptorNif",
    "receptorNom"
})
public class Datat {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar data;
    protected String errorDescripcio;
    protected EnviamentEstatEnum estat;
    protected String numSeguiment;
    protected String origen;
    protected String receptorNif;
    protected String receptorNom;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setData(XMLGregorianCalendar value) {
        this.data = value;
    }

    /**
     * Gets the value of the errorDescripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDescripcio() {
        return errorDescripcio;
    }

    /**
     * Sets the value of the errorDescripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDescripcio(String value) {
        this.errorDescripcio = value;
    }

    /**
     * Gets the value of the estat property.
     * 
     * @return
     *     possible object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public EnviamentEstatEnum getEstat() {
        return estat;
    }

    /**
     * Sets the value of the estat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public void setEstat(EnviamentEstatEnum value) {
        this.estat = value;
    }

    /**
     * Gets the value of the numSeguiment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumSeguiment() {
        return numSeguiment;
    }

    /**
     * Sets the value of the numSeguiment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumSeguiment(String value) {
        this.numSeguiment = value;
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

    /**
     * Gets the value of the receptorNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceptorNif() {
        return receptorNif;
    }

    /**
     * Sets the value of the receptorNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceptorNif(String value) {
        this.receptorNif = value;
    }

    /**
     * Gets the value of the receptorNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceptorNom() {
        return receptorNom;
    }

    /**
     * Sets the value of the receptorNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceptorNom(String value) {
        this.receptorNom = value;
    }

}

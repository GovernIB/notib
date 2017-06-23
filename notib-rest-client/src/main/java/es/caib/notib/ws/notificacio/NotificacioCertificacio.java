
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for notificacioCertificacio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificacioCertificacio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arxiuContingut" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arxiuTipus" type="{http://www.caib.es/notib/ws/notificacio}certificacioArxiuTipusEnum" minOccurs="0"/>
 *         &lt;element name="dataActualitzacio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="numSeguiment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipus" type="{http://www.caib.es/notib/ws/notificacio}certificacioTipusEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificacioCertificacio", propOrder = {
    "arxiuContingut",
    "arxiuTipus",
    "dataActualitzacio",
    "numSeguiment",
    "tipus"
})
public class NotificacioCertificacio {

    protected String arxiuContingut;
    @XmlSchemaType(name = "string")
    protected CertificacioArxiuTipusEnum arxiuTipus;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataActualitzacio;
    protected String numSeguiment;
    @XmlSchemaType(name = "string")
    protected CertificacioTipusEnum tipus;

    /**
     * Gets the value of the arxiuContingut property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArxiuContingut() {
        return arxiuContingut;
    }

    /**
     * Sets the value of the arxiuContingut property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArxiuContingut(String value) {
        this.arxiuContingut = value;
    }

    /**
     * Gets the value of the arxiuTipus property.
     * 
     * @return
     *     possible object is
     *     {@link CertificacioArxiuTipusEnum }
     *     
     */
    public CertificacioArxiuTipusEnum getArxiuTipus() {
        return arxiuTipus;
    }

    /**
     * Sets the value of the arxiuTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificacioArxiuTipusEnum }
     *     
     */
    public void setArxiuTipus(CertificacioArxiuTipusEnum value) {
        this.arxiuTipus = value;
    }

    /**
     * Gets the value of the dataActualitzacio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataActualitzacio() {
        return dataActualitzacio;
    }

    /**
     * Sets the value of the dataActualitzacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataActualitzacio(XMLGregorianCalendar value) {
        this.dataActualitzacio = value;
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
     * Gets the value of the tipus property.
     * 
     * @return
     *     possible object is
     *     {@link CertificacioTipusEnum }
     *     
     */
    public CertificacioTipusEnum getTipus() {
        return tipus;
    }

    /**
     * Sets the value of the tipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificacioTipusEnum }
     *     
     */
    public void setTipus(CertificacioTipusEnum value) {
        this.tipus = value;
    }

}

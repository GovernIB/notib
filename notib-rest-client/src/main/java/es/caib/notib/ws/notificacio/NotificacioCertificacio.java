
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para notificacioCertificacio complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
     * Obtiene el valor de la propiedad arxiuContingut.
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
     * Define el valor de la propiedad arxiuContingut.
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
     * Obtiene el valor de la propiedad arxiuTipus.
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
     * Define el valor de la propiedad arxiuTipus.
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
     * Obtiene el valor de la propiedad dataActualitzacio.
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
     * Define el valor de la propiedad dataActualitzacio.
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
     * Obtiene el valor de la propiedad numSeguiment.
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
     * Define el valor de la propiedad numSeguiment.
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
     * Obtiene el valor de la propiedad tipus.
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
     * Define el valor de la propiedad tipus.
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

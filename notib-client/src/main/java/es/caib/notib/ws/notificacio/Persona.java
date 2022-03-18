
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for persona complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="persona">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="incapacitat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="interessatTipus" type="{http://www.caib.es/notib/ws/notificacio}interessatTipusEnumDto" minOccurs="0"/>
 *         &lt;element name="llinatge1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="llinatge2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentTipus" type="{http://www.caib.es/notib/ws/notificacio}documentTipusEnumDto" minOccurs="0"/>
 *         &lt;element name="nif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="raoSocial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "persona", propOrder = {
    "dir3Codi",
    "email",
    "incapacitat",
    "interessatTipus",
    "llinatge1",
    "llinatge2",
    "documentTipus",
    "nif",
    "nom",
    "raoSocial",
    "telefon"
})
public class Persona {

    protected String dir3Codi;
    protected String email;
    protected boolean incapacitat;
    protected InteressatTipusEnumDto interessatTipus;
    protected String llinatge1;
    protected String llinatge2;
    protected DocumentTipusEnumDto documentTipus;
    protected String nif;
    protected String nom;
    protected String raoSocial;
    protected String telefon;

    /**
     * Gets the value of the dir3Codi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDir3Codi() {
        return dir3Codi;
    }

    /**
     * Sets the value of the dir3Codi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDir3Codi(String value) {
        this.dir3Codi = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the incapacitat property.
     * 
     */
    public boolean isIncapacitat() {
        return incapacitat;
    }

    /**
     * Sets the value of the incapacitat property.
     * 
     */
    public void setIncapacitat(boolean value) {
        this.incapacitat = value;
    }

    /**
     * Gets the value of the interessatTipus property.
     * 
     * @return
     *     possible object is
     *     {@link InteressatTipusEnumDto }
     *     
     */
    public InteressatTipusEnumDto getInteressatTipus() {
        return interessatTipus;
    }

    /**
     * Sets the value of the interessatTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link InteressatTipusEnumDto }
     *     
     */
    public void setInteressatTipus(InteressatTipusEnumDto value) {
        this.interessatTipus = value;
    }

    /**
     * Gets the value of the llinatge1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLlinatge1() {
        return llinatge1;
    }

    /**
     * Sets the value of the llinatge1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLlinatge1(String value) {
        this.llinatge1 = value;
    }

    /**
     * Gets the value of the llinatge2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLlinatge2() {
        return llinatge2;
    }

    /**
     * Sets the value of the llinatge2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLlinatge2(String value) {
        this.llinatge2 = value;
    }

    /**
     * Gets the value of the documentTipus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public DocumentTipusEnumDto getDocumentTipus() {
        return documentTipus;
    }
    /**
     * Sets the value of the documentTipus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentTipus(DocumentTipusEnumDto value) {
        this.documentTipus = value;
    }

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
     * Gets the value of the nom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets the value of the nom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNom(String value) {
        this.nom = value;
    }

    /**
     * Gets the value of the raoSocial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRaoSocial() {
        return raoSocial;
    }

    /**
     * Sets the value of the raoSocial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRaoSocial(String value) {
        this.raoSocial = value;
    }

    /**
     * Gets the value of the telefon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefon() {
        return telefon;
    }

    /**
     * Sets the value of the telefon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefon(String value) {
        this.telefon = value;
    }

}


package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for parametresSeu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parametresSeu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="avisText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisTextMobil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisTitol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expedientIdentificadorEni" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expedientSerieDocumental" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expedientTitol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expedientUnitatOrganitzativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oficiText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oficiTitol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreLlibre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreOficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parametresSeu", propOrder = {
    "avisText",
    "avisTextMobil",
    "avisTitol",
    "expedientIdentificadorEni",
    "expedientSerieDocumental",
    "expedientTitol",
    "expedientUnitatOrganitzativa",
    "idioma",
    "oficiText",
    "oficiTitol",
    "registreLlibre",
    "registreOficina"
})
public class ParametresSeu {

    protected String avisText;
    protected String avisTextMobil;
    protected String avisTitol;
    protected String expedientIdentificadorEni;
    protected String expedientSerieDocumental;
    protected String expedientTitol;
    protected String expedientUnitatOrganitzativa;
    protected String idioma;
    protected String oficiText;
    protected String oficiTitol;
    protected String registreLlibre;
    protected String registreOficina;

    /**
     * Gets the value of the avisText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvisText() {
        return avisText;
    }

    /**
     * Sets the value of the avisText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvisText(String value) {
        this.avisText = value;
    }

    /**
     * Gets the value of the avisTextMobil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvisTextMobil() {
        return avisTextMobil;
    }

    /**
     * Sets the value of the avisTextMobil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvisTextMobil(String value) {
        this.avisTextMobil = value;
    }

    /**
     * Gets the value of the avisTitol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvisTitol() {
        return avisTitol;
    }

    /**
     * Sets the value of the avisTitol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvisTitol(String value) {
        this.avisTitol = value;
    }

    /**
     * Gets the value of the expedientIdentificadorEni property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedientIdentificadorEni() {
        return expedientIdentificadorEni;
    }

    /**
     * Sets the value of the expedientIdentificadorEni property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedientIdentificadorEni(String value) {
        this.expedientIdentificadorEni = value;
    }

    /**
     * Gets the value of the expedientSerieDocumental property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedientSerieDocumental() {
        return expedientSerieDocumental;
    }

    /**
     * Sets the value of the expedientSerieDocumental property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedientSerieDocumental(String value) {
        this.expedientSerieDocumental = value;
    }

    /**
     * Gets the value of the expedientTitol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedientTitol() {
        return expedientTitol;
    }

    /**
     * Sets the value of the expedientTitol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedientTitol(String value) {
        this.expedientTitol = value;
    }

    /**
     * Gets the value of the expedientUnitatOrganitzativa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedientUnitatOrganitzativa() {
        return expedientUnitatOrganitzativa;
    }

    /**
     * Sets the value of the expedientUnitatOrganitzativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedientUnitatOrganitzativa(String value) {
        this.expedientUnitatOrganitzativa = value;
    }

    /**
     * Gets the value of the idioma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Sets the value of the idioma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdioma(String value) {
        this.idioma = value;
    }

    /**
     * Gets the value of the oficiText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOficiText() {
        return oficiText;
    }

    /**
     * Sets the value of the oficiText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOficiText(String value) {
        this.oficiText = value;
    }

    /**
     * Gets the value of the oficiTitol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOficiTitol() {
        return oficiTitol;
    }

    /**
     * Sets the value of the oficiTitol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOficiTitol(String value) {
        this.oficiTitol = value;
    }

    /**
     * Gets the value of the registreLlibre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreLlibre() {
        return registreLlibre;
    }

    /**
     * Sets the value of the registreLlibre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreLlibre(String value) {
        this.registreLlibre = value;
    }

    /**
     * Gets the value of the registreOficina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreOficina() {
        return registreOficina;
    }

    /**
     * Sets the value of the registreOficina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreOficina(String value) {
        this.registreOficina = value;
    }

}

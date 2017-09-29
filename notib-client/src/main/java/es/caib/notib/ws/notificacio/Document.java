
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for document complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arxiuNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contingutBase64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="generarCsv" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="metadades" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="normalitzat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "document", propOrder = {
    "arxiuNom",
    "contingutBase64",
    "generarCsv",
    "hash",
    "metadades",
    "normalitzat",
    "url"
})
public class Document {

    protected String arxiuNom;
    protected String contingutBase64;
    protected boolean generarCsv;
    protected String hash;
    protected String metadades;
    protected boolean normalitzat;
    protected String url;

    /**
     * Gets the value of the arxiuNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArxiuNom() {
        return arxiuNom;
    }

    /**
     * Sets the value of the arxiuNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArxiuNom(String value) {
        this.arxiuNom = value;
    }

    /**
     * Gets the value of the contingutBase64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContingutBase64() {
        return contingutBase64;
    }

    /**
     * Sets the value of the contingutBase64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContingutBase64(String value) {
        this.contingutBase64 = value;
    }

    /**
     * Gets the value of the generarCsv property.
     * 
     */
    public boolean isGenerarCsv() {
        return generarCsv;
    }

    /**
     * Sets the value of the generarCsv property.
     * 
     */
    public void setGenerarCsv(boolean value) {
        this.generarCsv = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the metadades property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetadades() {
        return metadades;
    }

    /**
     * Sets the value of the metadades property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetadades(String value) {
        this.metadades = value;
    }

    /**
     * Gets the value of the normalitzat property.
     * 
     */
    public boolean isNormalitzat() {
        return normalitzat;
    }

    /**
     * Sets the value of the normalitzat property.
     * 
     */
    public void setNormalitzat(boolean value) {
        this.normalitzat = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

}

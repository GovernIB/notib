
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arxiuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arxiuNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contingutBase64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="csv" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modoFirma" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="normalitzat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="origen" type="{http://www.caib.es/notib/ws/notificacio}origenEnum" minOccurs="0"/>
 *         &lt;element name="tipoDocumental" type="{http://www.caib.es/notib/ws/notificacio}tipusDocumentalEnum" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validesa" type="{http://www.caib.es/notib/ws/notificacio}validesaEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentV2", propOrder = {
    "arxiuId",
    "arxiuNom",
    "contingutBase64",
    "csv",
    "modoFirma",
    "normalitzat",
    "origen",
    "tipoDocumental",
    "url",
    "uuid",
    "validesa"
})
public class DocumentV2 {

    protected String arxiuId;
    protected String arxiuNom;
    protected String contingutBase64;
    protected String csv;
    protected Boolean modoFirma;
    protected boolean normalitzat;
    protected OrigenEnum origen;
    protected TipusDocumentalEnum tipoDocumental;
    protected String url;
    protected String uuid;
    protected ValidesaEnum validesa;

    /**
     * Gets the value of the arxiuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArxiuId() {
        return arxiuId;
    }

    /**
     * Sets the value of the arxiuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArxiuId(String value) {
        this.arxiuId = value;
    }

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
     * Gets the value of the csv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsv() {
        return csv;
    }

    /**
     * Sets the value of the csv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsv(String value) {
        this.csv = value;
    }

    /**
     * Gets the value of the modoFirma property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isModoFirma() {
        return modoFirma;
    }

    /**
     * Sets the value of the modoFirma property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setModoFirma(Boolean value) {
        this.modoFirma = value;
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
     * Gets the value of the origen property.
     * 
     * @return
     *     possible object is
     *     {@link OrigenEnum }
     *     
     */
    public OrigenEnum getOrigen() {
        return origen;
    }

    /**
     * Sets the value of the origen property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrigenEnum }
     *     
     */
    public void setOrigen(OrigenEnum value) {
        this.origen = value;
    }

    /**
     * Gets the value of the tipoDocumental property.
     * 
     * @return
     *     possible object is
     *     {@link TipusDocumentalEnum }
     *     
     */
    public TipusDocumentalEnum getTipoDocumental() {
        return tipoDocumental;
    }

    /**
     * Sets the value of the tipoDocumental property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipusDocumentalEnum }
     *     
     */
    public void setTipoDocumental(TipusDocumentalEnum value) {
        this.tipoDocumental = value;
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

    /**
     * Gets the value of the uuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the validesa property.
     * 
     * @return
     *     possible object is
     *     {@link ValidesaEnum }
     *     
     */
    public ValidesaEnum getValidesa() {
        return validesa;
    }

    /**
     * Sets the value of the validesa property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidesaEnum }
     *     
     */
    public void setValidesa(ValidesaEnum value) {
        this.validesa = value;
    }

}

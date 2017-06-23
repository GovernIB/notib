
package es.caib.notib.ws.notificacio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for notificacio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificacio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cifEntitat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="concepte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinataris" type="{http://www.caib.es/notib/ws/notificacio}notificacioDestinatari" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="documentArxiuNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentContingutBase64" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentGenerarCsv" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="documentNormalitzat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="documentSha1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enviamentDataProgramada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="enviamentTipus" type="{http://www.caib.es/notib/ws/notificacio}notificaEnviamentTipusEnumDto" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}notificacioEstatEnumDto" minOccurs="0"/>
 *         &lt;element name="pagadorCieCodiDir3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pagadorCieDataVigencia" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="pagadorCorreusCodiClientFacturacio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pagadorCorreusCodiDir3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pagadorCorreusContracteNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pagadorCorreusDataVigencia" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="procedimentCodiSia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="procedimentDescripcioSia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuAvisText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuAvisTextMobil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuAvisTitol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuExpedientIdentificadorEni" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuExpedientSerieDocumental" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuExpedientTitol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuExpedientUnitatOrganitzativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuIdioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuOficiText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuOficiTitol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuRegistreLlibre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuRegistreOficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificacio", propOrder = {
    "cifEntitat",
    "concepte",
    "destinataris",
    "documentArxiuNom",
    "documentContingutBase64",
    "documentGenerarCsv",
    "documentNormalitzat",
    "documentSha1",
    "enviamentDataProgramada",
    "enviamentTipus",
    "estat",
    "pagadorCieCodiDir3",
    "pagadorCieDataVigencia",
    "pagadorCorreusCodiClientFacturacio",
    "pagadorCorreusCodiDir3",
    "pagadorCorreusContracteNum",
    "pagadorCorreusDataVigencia",
    "procedimentCodiSia",
    "procedimentDescripcioSia",
    "seuAvisText",
    "seuAvisTextMobil",
    "seuAvisTitol",
    "seuExpedientIdentificadorEni",
    "seuExpedientSerieDocumental",
    "seuExpedientTitol",
    "seuExpedientUnitatOrganitzativa",
    "seuIdioma",
    "seuOficiText",
    "seuOficiTitol",
    "seuRegistreLlibre",
    "seuRegistreOficina"
})
public class Notificacio_Type {

    protected String cifEntitat;
    protected String concepte;
    @XmlElement(nillable = true)
    protected List<NotificacioDestinatari> destinataris;
    protected String documentArxiuNom;
    protected String documentContingutBase64;
    protected boolean documentGenerarCsv;
    protected boolean documentNormalitzat;
    protected String documentSha1;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar enviamentDataProgramada;
    @XmlSchemaType(name = "string")
    protected NotificaEnviamentTipusEnumDto enviamentTipus;
    @XmlSchemaType(name = "string")
    protected NotificacioEstatEnumDto estat;
    protected String pagadorCieCodiDir3;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pagadorCieDataVigencia;
    protected String pagadorCorreusCodiClientFacturacio;
    protected String pagadorCorreusCodiDir3;
    protected String pagadorCorreusContracteNum;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pagadorCorreusDataVigencia;
    protected String procedimentCodiSia;
    protected String procedimentDescripcioSia;
    protected String seuAvisText;
    protected String seuAvisTextMobil;
    protected String seuAvisTitol;
    protected String seuExpedientIdentificadorEni;
    protected String seuExpedientSerieDocumental;
    protected String seuExpedientTitol;
    protected String seuExpedientUnitatOrganitzativa;
    protected String seuIdioma;
    protected String seuOficiText;
    protected String seuOficiTitol;
    protected String seuRegistreLlibre;
    protected String seuRegistreOficina;

    /**
     * Gets the value of the cifEntitat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCifEntitat() {
        return cifEntitat;
    }

    /**
     * Sets the value of the cifEntitat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCifEntitat(String value) {
        this.cifEntitat = value;
    }

    /**
     * Gets the value of the concepte property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConcepte() {
        return concepte;
    }

    /**
     * Sets the value of the concepte property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcepte(String value) {
        this.concepte = value;
    }

    /**
     * Gets the value of the destinataris property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinataris property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinataris().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NotificacioDestinatari }
     * 
     * 
     */
    public List<NotificacioDestinatari> getDestinataris() {
        if (destinataris == null) {
            destinataris = new ArrayList<NotificacioDestinatari>();
        }
        return this.destinataris;
    }
    

    public void setDestinataris(List<NotificacioDestinatari> destinataris) {
		this.destinataris = destinataris;
	}

	/**
     * Gets the value of the documentArxiuNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentArxiuNom() {
        return documentArxiuNom;
    }

    /**
     * Sets the value of the documentArxiuNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentArxiuNom(String value) {
        this.documentArxiuNom = value;
    }

    /**
     * Gets the value of the documentContingutBase64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentContingutBase64() {
        return documentContingutBase64;
    }

    /**
     * Sets the value of the documentContingutBase64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentContingutBase64(String value) {
        this.documentContingutBase64 = value;
    }

    /**
     * Gets the value of the documentGenerarCsv property.
     * 
     */
    public boolean isDocumentGenerarCsv() {
        return documentGenerarCsv;
    }

    /**
     * Sets the value of the documentGenerarCsv property.
     * 
     */
    public void setDocumentGenerarCsv(boolean value) {
        this.documentGenerarCsv = value;
    }

    /**
     * Gets the value of the documentNormalitzat property.
     * 
     */
    public boolean isDocumentNormalitzat() {
        return documentNormalitzat;
    }

    /**
     * Sets the value of the documentNormalitzat property.
     * 
     */
    public void setDocumentNormalitzat(boolean value) {
        this.documentNormalitzat = value;
    }

    /**
     * Gets the value of the documentSha1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentSha1() {
        return documentSha1;
    }

    /**
     * Sets the value of the documentSha1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentSha1(String value) {
        this.documentSha1 = value;
    }

    /**
     * Gets the value of the enviamentDataProgramada property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEnviamentDataProgramada() {
        return enviamentDataProgramada;
    }

    /**
     * Sets the value of the enviamentDataProgramada property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEnviamentDataProgramada(XMLGregorianCalendar value) {
        this.enviamentDataProgramada = value;
    }

    /**
     * Gets the value of the enviamentTipus property.
     * 
     * @return
     *     possible object is
     *     {@link NotificaEnviamentTipusEnumDto }
     *     
     */
    public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
        return enviamentTipus;
    }

    /**
     * Sets the value of the enviamentTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificaEnviamentTipusEnumDto }
     *     
     */
    public void setEnviamentTipus(NotificaEnviamentTipusEnumDto value) {
        this.enviamentTipus = value;
    }

    /**
     * Gets the value of the estat property.
     * 
     * @return
     *     possible object is
     *     {@link NotificacioEstatEnumDto }
     *     
     */
    public NotificacioEstatEnumDto getEstat() {
        return estat;
    }

    /**
     * Sets the value of the estat property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificacioEstatEnumDto }
     *     
     */
    public void setEstat(NotificacioEstatEnumDto value) {
        this.estat = value;
    }

    /**
     * Gets the value of the pagadorCieCodiDir3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPagadorCieCodiDir3() {
        return pagadorCieCodiDir3;
    }

    /**
     * Sets the value of the pagadorCieCodiDir3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPagadorCieCodiDir3(String value) {
        this.pagadorCieCodiDir3 = value;
    }

    /**
     * Gets the value of the pagadorCieDataVigencia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPagadorCieDataVigencia() {
        return pagadorCieDataVigencia;
    }

    /**
     * Sets the value of the pagadorCieDataVigencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPagadorCieDataVigencia(XMLGregorianCalendar value) {
        this.pagadorCieDataVigencia = value;
    }

    /**
     * Gets the value of the pagadorCorreusCodiClientFacturacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPagadorCorreusCodiClientFacturacio() {
        return pagadorCorreusCodiClientFacturacio;
    }

    /**
     * Sets the value of the pagadorCorreusCodiClientFacturacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPagadorCorreusCodiClientFacturacio(String value) {
        this.pagadorCorreusCodiClientFacturacio = value;
    }

    /**
     * Gets the value of the pagadorCorreusCodiDir3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPagadorCorreusCodiDir3() {
        return pagadorCorreusCodiDir3;
    }

    /**
     * Sets the value of the pagadorCorreusCodiDir3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPagadorCorreusCodiDir3(String value) {
        this.pagadorCorreusCodiDir3 = value;
    }

    /**
     * Gets the value of the pagadorCorreusContracteNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPagadorCorreusContracteNum() {
        return pagadorCorreusContracteNum;
    }

    /**
     * Sets the value of the pagadorCorreusContracteNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPagadorCorreusContracteNum(String value) {
        this.pagadorCorreusContracteNum = value;
    }

    /**
     * Gets the value of the pagadorCorreusDataVigencia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPagadorCorreusDataVigencia() {
        return pagadorCorreusDataVigencia;
    }

    /**
     * Sets the value of the pagadorCorreusDataVigencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPagadorCorreusDataVigencia(XMLGregorianCalendar value) {
        this.pagadorCorreusDataVigencia = value;
    }

    /**
     * Gets the value of the procedimentCodiSia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedimentCodiSia() {
        return procedimentCodiSia;
    }

    /**
     * Sets the value of the procedimentCodiSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedimentCodiSia(String value) {
        this.procedimentCodiSia = value;
    }

    /**
     * Gets the value of the procedimentDescripcioSia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedimentDescripcioSia() {
        return procedimentDescripcioSia;
    }

    /**
     * Sets the value of the procedimentDescripcioSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedimentDescripcioSia(String value) {
        this.procedimentDescripcioSia = value;
    }

    /**
     * Gets the value of the seuAvisText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuAvisText() {
        return seuAvisText;
    }

    /**
     * Sets the value of the seuAvisText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuAvisText(String value) {
        this.seuAvisText = value;
    }

    /**
     * Gets the value of the seuAvisTextMobil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuAvisTextMobil() {
        return seuAvisTextMobil;
    }

    /**
     * Sets the value of the seuAvisTextMobil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuAvisTextMobil(String value) {
        this.seuAvisTextMobil = value;
    }

    /**
     * Gets the value of the seuAvisTitol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuAvisTitol() {
        return seuAvisTitol;
    }

    /**
     * Sets the value of the seuAvisTitol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuAvisTitol(String value) {
        this.seuAvisTitol = value;
    }

    /**
     * Gets the value of the seuExpedientIdentificadorEni property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuExpedientIdentificadorEni() {
        return seuExpedientIdentificadorEni;
    }

    /**
     * Sets the value of the seuExpedientIdentificadorEni property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuExpedientIdentificadorEni(String value) {
        this.seuExpedientIdentificadorEni = value;
    }

    /**
     * Gets the value of the seuExpedientSerieDocumental property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuExpedientSerieDocumental() {
        return seuExpedientSerieDocumental;
    }

    /**
     * Sets the value of the seuExpedientSerieDocumental property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuExpedientSerieDocumental(String value) {
        this.seuExpedientSerieDocumental = value;
    }

    /**
     * Gets the value of the seuExpedientTitol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuExpedientTitol() {
        return seuExpedientTitol;
    }

    /**
     * Sets the value of the seuExpedientTitol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuExpedientTitol(String value) {
        this.seuExpedientTitol = value;
    }

    /**
     * Gets the value of the seuExpedientUnitatOrganitzativa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuExpedientUnitatOrganitzativa() {
        return seuExpedientUnitatOrganitzativa;
    }

    /**
     * Sets the value of the seuExpedientUnitatOrganitzativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuExpedientUnitatOrganitzativa(String value) {
        this.seuExpedientUnitatOrganitzativa = value;
    }

    /**
     * Gets the value of the seuIdioma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuIdioma() {
        return seuIdioma;
    }

    /**
     * Sets the value of the seuIdioma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuIdioma(String value) {
        this.seuIdioma = value;
    }

    /**
     * Gets the value of the seuOficiText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuOficiText() {
        return seuOficiText;
    }

    /**
     * Sets the value of the seuOficiText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuOficiText(String value) {
        this.seuOficiText = value;
    }

    /**
     * Gets the value of the seuOficiTitol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuOficiTitol() {
        return seuOficiTitol;
    }

    /**
     * Sets the value of the seuOficiTitol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuOficiTitol(String value) {
        this.seuOficiTitol = value;
    }

    /**
     * Gets the value of the seuRegistreLlibre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuRegistreLlibre() {
        return seuRegistreLlibre;
    }

    /**
     * Sets the value of the seuRegistreLlibre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuRegistreLlibre(String value) {
        this.seuRegistreLlibre = value;
    }

    /**
     * Gets the value of the seuRegistreOficina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuRegistreOficina() {
        return seuRegistreOficina;
    }

    /**
     * Sets the value of the seuRegistreOficina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuRegistreOficina(String value) {
        this.seuRegistreOficina = value;
    }

}

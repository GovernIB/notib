
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
 * <p>Clase Java para notificacio complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
 *         &lt;element name="documentacioFisicaCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *         &lt;element name="registreCodiAssumpte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreDataCaptura" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="registreEnviar" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="registreIdioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreLlibre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreOficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreOrigenCiutadaAdmin" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="registreTipusAssumpte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registreTipusDocumental" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "documentacioFisicaCodi",
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
    "registreCodiAssumpte",
    "registreDataCaptura",
    "registreEnviar",
    "registreIdioma",
    "registreLlibre",
    "registreOficina",
    "registreOrigenCiutadaAdmin",
    "registreTipusAssumpte",
    "registreTipusDocumental",
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
    protected String documentacioFisicaCodi;
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
    protected String registreCodiAssumpte;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registreDataCaptura;
    protected boolean registreEnviar;
    protected String registreIdioma;
    protected String registreLlibre;
    protected String registreOficina;
    protected int registreOrigenCiutadaAdmin;
    protected String registreTipusAssumpte;
    protected String registreTipusDocumental;
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
     * Obtiene el valor de la propiedad cifEntitat.
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
     * Define el valor de la propiedad cifEntitat.
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
     * Obtiene el valor de la propiedad concepte.
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
     * Define el valor de la propiedad concepte.
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

    /**
     * Obtiene el valor de la propiedad documentArxiuNom.
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
     * Define el valor de la propiedad documentArxiuNom.
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
     * Obtiene el valor de la propiedad documentContingutBase64.
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
     * Define el valor de la propiedad documentContingutBase64.
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
     * Obtiene el valor de la propiedad documentGenerarCsv.
     * 
     */
    public boolean isDocumentGenerarCsv() {
        return documentGenerarCsv;
    }

    /**
     * Define el valor de la propiedad documentGenerarCsv.
     * 
     */
    public void setDocumentGenerarCsv(boolean value) {
        this.documentGenerarCsv = value;
    }

    /**
     * Obtiene el valor de la propiedad documentNormalitzat.
     * 
     */
    public boolean isDocumentNormalitzat() {
        return documentNormalitzat;
    }

    /**
     * Define el valor de la propiedad documentNormalitzat.
     * 
     */
    public void setDocumentNormalitzat(boolean value) {
        this.documentNormalitzat = value;
    }

    /**
     * Obtiene el valor de la propiedad documentSha1.
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
     * Define el valor de la propiedad documentSha1.
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
     * Obtiene el valor de la propiedad documentacioFisicaCodi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentacioFisicaCodi() {
        return documentacioFisicaCodi;
    }

    /**
     * Define el valor de la propiedad documentacioFisicaCodi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentacioFisicaCodi(String value) {
        this.documentacioFisicaCodi = value;
    }

    /**
     * Obtiene el valor de la propiedad enviamentDataProgramada.
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
     * Define el valor de la propiedad enviamentDataProgramada.
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
     * Obtiene el valor de la propiedad enviamentTipus.
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
     * Define el valor de la propiedad enviamentTipus.
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
     * Obtiene el valor de la propiedad estat.
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
     * Define el valor de la propiedad estat.
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
     * Obtiene el valor de la propiedad pagadorCieCodiDir3.
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
     * Define el valor de la propiedad pagadorCieCodiDir3.
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
     * Obtiene el valor de la propiedad pagadorCieDataVigencia.
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
     * Define el valor de la propiedad pagadorCieDataVigencia.
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
     * Obtiene el valor de la propiedad pagadorCorreusCodiClientFacturacio.
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
     * Define el valor de la propiedad pagadorCorreusCodiClientFacturacio.
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
     * Obtiene el valor de la propiedad pagadorCorreusCodiDir3.
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
     * Define el valor de la propiedad pagadorCorreusCodiDir3.
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
     * Obtiene el valor de la propiedad pagadorCorreusContracteNum.
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
     * Define el valor de la propiedad pagadorCorreusContracteNum.
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
     * Obtiene el valor de la propiedad pagadorCorreusDataVigencia.
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
     * Define el valor de la propiedad pagadorCorreusDataVigencia.
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
     * Obtiene el valor de la propiedad procedimentCodiSia.
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
     * Define el valor de la propiedad procedimentCodiSia.
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
     * Obtiene el valor de la propiedad procedimentDescripcioSia.
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
     * Define el valor de la propiedad procedimentDescripcioSia.
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
     * Obtiene el valor de la propiedad registreCodiAssumpte.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreCodiAssumpte() {
        return registreCodiAssumpte;
    }

    /**
     * Define el valor de la propiedad registreCodiAssumpte.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreCodiAssumpte(String value) {
        this.registreCodiAssumpte = value;
    }

    /**
     * Obtiene el valor de la propiedad registreDataCaptura.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegistreDataCaptura() {
        return registreDataCaptura;
    }

    /**
     * Define el valor de la propiedad registreDataCaptura.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegistreDataCaptura(XMLGregorianCalendar value) {
        this.registreDataCaptura = value;
    }

    /**
     * Obtiene el valor de la propiedad registreEnviar.
     * 
     */
    public boolean isRegistreEnviar() {
        return registreEnviar;
    }

    /**
     * Define el valor de la propiedad registreEnviar.
     * 
     */
    public void setRegistreEnviar(boolean value) {
        this.registreEnviar = value;
    }

    /**
     * Obtiene el valor de la propiedad registreIdioma.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreIdioma() {
        return registreIdioma;
    }

    /**
     * Define el valor de la propiedad registreIdioma.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreIdioma(String value) {
        this.registreIdioma = value;
    }

    /**
     * Obtiene el valor de la propiedad registreLlibre.
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
     * Define el valor de la propiedad registreLlibre.
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
     * Obtiene el valor de la propiedad registreOficina.
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
     * Define el valor de la propiedad registreOficina.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreOficina(String value) {
        this.registreOficina = value;
    }

    /**
     * Obtiene el valor de la propiedad registreOrigenCiutadaAdmin.
     * 
     */
    public int getRegistreOrigenCiutadaAdmin() {
        return registreOrigenCiutadaAdmin;
    }

    /**
     * Define el valor de la propiedad registreOrigenCiutadaAdmin.
     * 
     */
    public void setRegistreOrigenCiutadaAdmin(int value) {
        this.registreOrigenCiutadaAdmin = value;
    }

    /**
     * Obtiene el valor de la propiedad registreTipusAssumpte.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreTipusAssumpte() {
        return registreTipusAssumpte;
    }

    /**
     * Define el valor de la propiedad registreTipusAssumpte.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreTipusAssumpte(String value) {
        this.registreTipusAssumpte = value;
    }

    /**
     * Obtiene el valor de la propiedad registreTipusDocumental.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreTipusDocumental() {
        return registreTipusDocumental;
    }

    /**
     * Define el valor de la propiedad registreTipusDocumental.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreTipusDocumental(String value) {
        this.registreTipusDocumental = value;
    }

    /**
     * Obtiene el valor de la propiedad seuAvisText.
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
     * Define el valor de la propiedad seuAvisText.
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
     * Obtiene el valor de la propiedad seuAvisTextMobil.
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
     * Define el valor de la propiedad seuAvisTextMobil.
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
     * Obtiene el valor de la propiedad seuAvisTitol.
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
     * Define el valor de la propiedad seuAvisTitol.
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
     * Obtiene el valor de la propiedad seuExpedientIdentificadorEni.
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
     * Define el valor de la propiedad seuExpedientIdentificadorEni.
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
     * Obtiene el valor de la propiedad seuExpedientSerieDocumental.
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
     * Define el valor de la propiedad seuExpedientSerieDocumental.
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
     * Obtiene el valor de la propiedad seuExpedientTitol.
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
     * Define el valor de la propiedad seuExpedientTitol.
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
     * Obtiene el valor de la propiedad seuExpedientUnitatOrganitzativa.
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
     * Define el valor de la propiedad seuExpedientUnitatOrganitzativa.
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
     * Obtiene el valor de la propiedad seuIdioma.
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
     * Define el valor de la propiedad seuIdioma.
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
     * Obtiene el valor de la propiedad seuOficiText.
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
     * Define el valor de la propiedad seuOficiText.
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
     * Obtiene el valor de la propiedad seuOficiTitol.
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
     * Define el valor de la propiedad seuOficiTitol.
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
     * Obtiene el valor de la propiedad seuRegistreLlibre.
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
     * Define el valor de la propiedad seuRegistreLlibre.
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
     * Obtiene el valor de la propiedad seuRegistreOficina.
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
     * Define el valor de la propiedad seuRegistreOficina.
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

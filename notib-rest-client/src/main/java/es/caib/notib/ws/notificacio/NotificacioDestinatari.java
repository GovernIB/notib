
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for notificacioDestinatari complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificacioDestinatari">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="caducitat" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dehNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dehObligat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dehProcedimentCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatariEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatariLlinatges" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatariNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatariNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatariTelefon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliApartatCorreus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliBloc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliCie" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="domiciliCodiPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliComplement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliConcretTipus" type="{http://www.caib.es/notib/ws/notificacio}domiciliConcretTipusEnum" minOccurs="0"/>
 *         &lt;element name="domiciliEscala" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliLinea1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliLinea2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliMunicipiCodiIne" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliMunicipiNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliNumeracioNumero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliNumeracioPuntKm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliNumeracioTipus" type="{http://www.caib.es/notib/ws/notificacio}domiciliNumeracioTipusEnum" minOccurs="0"/>
 *         &lt;element name="domiciliPaisCodiIso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliPaisNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliPlanta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliPoblacio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliPorta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliPortal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliProvinciaCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliProvinciaNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliTipus" type="{http://www.caib.es/notib/ws/notificacio}domiciliTipusEnum" minOccurs="0"/>
 *         &lt;element name="domiciliViaNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domiciliViaTipus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificaIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retardPostal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serveiTipus" type="{http://www.caib.es/notib/ws/notificacio}serveiTipusEnum" minOccurs="0"/>
 *         &lt;element name="seuEstat" type="{http://www.caib.es/notib/ws/notificacio}notificacioSeuEstatEnumDto" minOccurs="0"/>
 *         &lt;element name="seuRegistreData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="seuRegistreNumero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titularEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titularLlinatges" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titularNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titularNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titularTelefon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificacioDestinatari", propOrder = {
    "caducitat",
    "dehNif",
    "dehObligat",
    "dehProcedimentCodi",
    "destinatariEmail",
    "destinatariLlinatges",
    "destinatariNif",
    "destinatariNom",
    "destinatariTelefon",
    "domiciliApartatCorreus",
    "domiciliBloc",
    "domiciliCie",
    "domiciliCodiPostal",
    "domiciliComplement",
    "domiciliConcretTipus",
    "domiciliEscala",
    "domiciliLinea1",
    "domiciliLinea2",
    "domiciliMunicipiCodiIne",
    "domiciliMunicipiNom",
    "domiciliNumeracioNumero",
    "domiciliNumeracioPuntKm",
    "domiciliNumeracioTipus",
    "domiciliPaisCodiIso",
    "domiciliPaisNom",
    "domiciliPlanta",
    "domiciliPoblacio",
    "domiciliPorta",
    "domiciliPortal",
    "domiciliProvinciaCodi",
    "domiciliProvinciaNom",
    "domiciliTipus",
    "domiciliViaNom",
    "domiciliViaTipus",
    "notificaIdentificador",
    "referencia",
    "retardPostal",
    "serveiTipus",
    "seuEstat",
    "seuRegistreData",
    "seuRegistreNumero",
    "titularEmail",
    "titularLlinatges",
    "titularNif",
    "titularNom",
    "titularTelefon"
})
public class NotificacioDestinatari {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar caducitat;
    protected String dehNif;
    protected boolean dehObligat;
    protected String dehProcedimentCodi;
    protected String destinatariEmail;
    protected String destinatariLlinatges;
    protected String destinatariNif;
    protected String destinatariNom;
    protected String destinatariTelefon;
    protected String domiciliApartatCorreus;
    protected String domiciliBloc;
    protected Integer domiciliCie;
    protected String domiciliCodiPostal;
    protected String domiciliComplement;
    @XmlSchemaType(name = "string")
    protected DomiciliConcretTipusEnum domiciliConcretTipus;
    protected String domiciliEscala;
    protected String domiciliLinea1;
    protected String domiciliLinea2;
    protected String domiciliMunicipiCodiIne;
    protected String domiciliMunicipiNom;
    protected String domiciliNumeracioNumero;
    protected String domiciliNumeracioPuntKm;
    @XmlSchemaType(name = "string")
    protected DomiciliNumeracioTipusEnum domiciliNumeracioTipus;
    protected String domiciliPaisCodiIso;
    protected String domiciliPaisNom;
    protected String domiciliPlanta;
    protected String domiciliPoblacio;
    protected String domiciliPorta;
    protected String domiciliPortal;
    protected String domiciliProvinciaCodi;
    protected String domiciliProvinciaNom;
    @XmlSchemaType(name = "string")
    protected DomiciliTipusEnum domiciliTipus;
    protected String domiciliViaNom;
    protected String domiciliViaTipus;
    protected String notificaIdentificador;
    protected String referencia;
    protected int retardPostal;
    @XmlSchemaType(name = "string")
    protected ServeiTipusEnum serveiTipus;
    @XmlSchemaType(name = "string")
    protected NotificacioSeuEstatEnumDto seuEstat;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar seuRegistreData;
    protected String seuRegistreNumero;
    protected String titularEmail;
    protected String titularLlinatges;
    protected String titularNif;
    protected String titularNom;
    protected String titularTelefon;

    /**
     * Gets the value of the caducitat property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCaducitat() {
        return caducitat;
    }

    /**
     * Sets the value of the caducitat property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCaducitat(XMLGregorianCalendar value) {
        this.caducitat = value;
    }

    /**
     * Gets the value of the dehNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDehNif() {
        return dehNif;
    }

    /**
     * Sets the value of the dehNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDehNif(String value) {
        this.dehNif = value;
    }

    /**
     * Gets the value of the dehObligat property.
     * 
     */
    public boolean isDehObligat() {
        return dehObligat;
    }

    /**
     * Sets the value of the dehObligat property.
     * 
     */
    public void setDehObligat(boolean value) {
        this.dehObligat = value;
    }

    /**
     * Gets the value of the dehProcedimentCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDehProcedimentCodi() {
        return dehProcedimentCodi;
    }

    /**
     * Sets the value of the dehProcedimentCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDehProcedimentCodi(String value) {
        this.dehProcedimentCodi = value;
    }

    /**
     * Gets the value of the destinatariEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariEmail() {
        return destinatariEmail;
    }

    /**
     * Sets the value of the destinatariEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariEmail(String value) {
        this.destinatariEmail = value;
    }

    /**
     * Gets the value of the destinatariLlinatges property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariLlinatges() {
        return destinatariLlinatges;
    }

    /**
     * Sets the value of the destinatariLlinatges property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariLlinatges(String value) {
        this.destinatariLlinatges = value;
    }

    /**
     * Gets the value of the destinatariNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariNif() {
        return destinatariNif;
    }

    /**
     * Sets the value of the destinatariNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariNif(String value) {
        this.destinatariNif = value;
    }

    /**
     * Gets the value of the destinatariNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariNom() {
        return destinatariNom;
    }

    /**
     * Sets the value of the destinatariNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariNom(String value) {
        this.destinatariNom = value;
    }

    /**
     * Gets the value of the destinatariTelefon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariTelefon() {
        return destinatariTelefon;
    }

    /**
     * Sets the value of the destinatariTelefon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariTelefon(String value) {
        this.destinatariTelefon = value;
    }

    /**
     * Gets the value of the domiciliApartatCorreus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliApartatCorreus() {
        return domiciliApartatCorreus;
    }

    /**
     * Sets the value of the domiciliApartatCorreus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliApartatCorreus(String value) {
        this.domiciliApartatCorreus = value;
    }

    /**
     * Gets the value of the domiciliBloc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliBloc() {
        return domiciliBloc;
    }

    /**
     * Sets the value of the domiciliBloc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliBloc(String value) {
        this.domiciliBloc = value;
    }

    /**
     * Gets the value of the domiciliCie property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDomiciliCie() {
        return domiciliCie;
    }

    /**
     * Sets the value of the domiciliCie property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDomiciliCie(Integer value) {
        this.domiciliCie = value;
    }

    /**
     * Gets the value of the domiciliCodiPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliCodiPostal() {
        return domiciliCodiPostal;
    }

    /**
     * Sets the value of the domiciliCodiPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliCodiPostal(String value) {
        this.domiciliCodiPostal = value;
    }

    /**
     * Gets the value of the domiciliComplement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliComplement() {
        return domiciliComplement;
    }

    /**
     * Sets the value of the domiciliComplement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliComplement(String value) {
        this.domiciliComplement = value;
    }

    /**
     * Gets the value of the domiciliConcretTipus property.
     * 
     * @return
     *     possible object is
     *     {@link DomiciliConcretTipusEnum }
     *     
     */
    public DomiciliConcretTipusEnum getDomiciliConcretTipus() {
        return domiciliConcretTipus;
    }

    /**
     * Sets the value of the domiciliConcretTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DomiciliConcretTipusEnum }
     *     
     */
    public void setDomiciliConcretTipus(DomiciliConcretTipusEnum value) {
        this.domiciliConcretTipus = value;
    }

    /**
     * Gets the value of the domiciliEscala property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliEscala() {
        return domiciliEscala;
    }

    /**
     * Sets the value of the domiciliEscala property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliEscala(String value) {
        this.domiciliEscala = value;
    }

    /**
     * Gets the value of the domiciliLinea1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliLinea1() {
        return domiciliLinea1;
    }

    /**
     * Sets the value of the domiciliLinea1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliLinea1(String value) {
        this.domiciliLinea1 = value;
    }

    /**
     * Gets the value of the domiciliLinea2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliLinea2() {
        return domiciliLinea2;
    }

    /**
     * Sets the value of the domiciliLinea2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliLinea2(String value) {
        this.domiciliLinea2 = value;
    }

    /**
     * Gets the value of the domiciliMunicipiCodiIne property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliMunicipiCodiIne() {
        return domiciliMunicipiCodiIne;
    }

    /**
     * Sets the value of the domiciliMunicipiCodiIne property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliMunicipiCodiIne(String value) {
        this.domiciliMunicipiCodiIne = value;
    }

    /**
     * Gets the value of the domiciliMunicipiNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliMunicipiNom() {
        return domiciliMunicipiNom;
    }

    /**
     * Sets the value of the domiciliMunicipiNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliMunicipiNom(String value) {
        this.domiciliMunicipiNom = value;
    }

    /**
     * Gets the value of the domiciliNumeracioNumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliNumeracioNumero() {
        return domiciliNumeracioNumero;
    }

    /**
     * Sets the value of the domiciliNumeracioNumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliNumeracioNumero(String value) {
        this.domiciliNumeracioNumero = value;
    }

    /**
     * Gets the value of the domiciliNumeracioPuntKm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliNumeracioPuntKm() {
        return domiciliNumeracioPuntKm;
    }

    /**
     * Sets the value of the domiciliNumeracioPuntKm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliNumeracioPuntKm(String value) {
        this.domiciliNumeracioPuntKm = value;
    }

    /**
     * Gets the value of the domiciliNumeracioTipus property.
     * 
     * @return
     *     possible object is
     *     {@link DomiciliNumeracioTipusEnum }
     *     
     */
    public DomiciliNumeracioTipusEnum getDomiciliNumeracioTipus() {
        return domiciliNumeracioTipus;
    }

    /**
     * Sets the value of the domiciliNumeracioTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DomiciliNumeracioTipusEnum }
     *     
     */
    public void setDomiciliNumeracioTipus(DomiciliNumeracioTipusEnum value) {
        this.domiciliNumeracioTipus = value;
    }

    /**
     * Gets the value of the domiciliPaisCodiIso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliPaisCodiIso() {
        return domiciliPaisCodiIso;
    }

    /**
     * Sets the value of the domiciliPaisCodiIso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliPaisCodiIso(String value) {
        this.domiciliPaisCodiIso = value;
    }

    /**
     * Gets the value of the domiciliPaisNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliPaisNom() {
        return domiciliPaisNom;
    }

    /**
     * Sets the value of the domiciliPaisNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliPaisNom(String value) {
        this.domiciliPaisNom = value;
    }

    /**
     * Gets the value of the domiciliPlanta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliPlanta() {
        return domiciliPlanta;
    }

    /**
     * Sets the value of the domiciliPlanta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliPlanta(String value) {
        this.domiciliPlanta = value;
    }

    /**
     * Gets the value of the domiciliPoblacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliPoblacio() {
        return domiciliPoblacio;
    }

    /**
     * Sets the value of the domiciliPoblacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliPoblacio(String value) {
        this.domiciliPoblacio = value;
    }

    /**
     * Gets the value of the domiciliPorta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliPorta() {
        return domiciliPorta;
    }

    /**
     * Sets the value of the domiciliPorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliPorta(String value) {
        this.domiciliPorta = value;
    }

    /**
     * Gets the value of the domiciliPortal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliPortal() {
        return domiciliPortal;
    }

    /**
     * Sets the value of the domiciliPortal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliPortal(String value) {
        this.domiciliPortal = value;
    }

    /**
     * Gets the value of the domiciliProvinciaCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliProvinciaCodi() {
        return domiciliProvinciaCodi;
    }

    /**
     * Sets the value of the domiciliProvinciaCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliProvinciaCodi(String value) {
        this.domiciliProvinciaCodi = value;
    }

    /**
     * Gets the value of the domiciliProvinciaNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliProvinciaNom() {
        return domiciliProvinciaNom;
    }

    /**
     * Sets the value of the domiciliProvinciaNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliProvinciaNom(String value) {
        this.domiciliProvinciaNom = value;
    }

    /**
     * Gets the value of the domiciliTipus property.
     * 
     * @return
     *     possible object is
     *     {@link DomiciliTipusEnum }
     *     
     */
    public DomiciliTipusEnum getDomiciliTipus() {
        return domiciliTipus;
    }

    /**
     * Sets the value of the domiciliTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DomiciliTipusEnum }
     *     
     */
    public void setDomiciliTipus(DomiciliTipusEnum value) {
        this.domiciliTipus = value;
    }

    /**
     * Gets the value of the domiciliViaNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliViaNom() {
        return domiciliViaNom;
    }

    /**
     * Sets the value of the domiciliViaNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliViaNom(String value) {
        this.domiciliViaNom = value;
    }

    /**
     * Gets the value of the domiciliViaTipus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomiciliViaTipus() {
        return domiciliViaTipus;
    }

    /**
     * Sets the value of the domiciliViaTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomiciliViaTipus(String value) {
        this.domiciliViaTipus = value;
    }

    /**
     * Gets the value of the notificaIdentificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificaIdentificador() {
        return notificaIdentificador;
    }

    /**
     * Sets the value of the notificaIdentificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificaIdentificador(String value) {
        this.notificaIdentificador = value;
    }

    /**
     * Gets the value of the referencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Sets the value of the referencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencia(String value) {
        this.referencia = value;
    }

    /**
     * Gets the value of the retardPostal property.
     * 
     */
    public int getRetardPostal() {
        return retardPostal;
    }

    /**
     * Sets the value of the retardPostal property.
     * 
     */
    public void setRetardPostal(int value) {
        this.retardPostal = value;
    }

    /**
     * Gets the value of the serveiTipus property.
     * 
     * @return
     *     possible object is
     *     {@link ServeiTipusEnum }
     *     
     */
    public ServeiTipusEnum getServeiTipus() {
        return serveiTipus;
    }

    /**
     * Sets the value of the serveiTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServeiTipusEnum }
     *     
     */
    public void setServeiTipus(ServeiTipusEnum value) {
        this.serveiTipus = value;
    }

    /**
     * Gets the value of the seuEstat property.
     * 
     * @return
     *     possible object is
     *     {@link NotificacioSeuEstatEnumDto }
     *     
     */
    public NotificacioSeuEstatEnumDto getSeuEstat() {
        return seuEstat;
    }

    /**
     * Sets the value of the seuEstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificacioSeuEstatEnumDto }
     *     
     */
    public void setSeuEstat(NotificacioSeuEstatEnumDto value) {
        this.seuEstat = value;
    }

    /**
     * Gets the value of the seuRegistreData property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSeuRegistreData() {
        return seuRegistreData;
    }

    /**
     * Sets the value of the seuRegistreData property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSeuRegistreData(XMLGregorianCalendar value) {
        this.seuRegistreData = value;
    }

    /**
     * Gets the value of the seuRegistreNumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuRegistreNumero() {
        return seuRegistreNumero;
    }

    /**
     * Sets the value of the seuRegistreNumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuRegistreNumero(String value) {
        this.seuRegistreNumero = value;
    }

    /**
     * Gets the value of the titularEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularEmail() {
        return titularEmail;
    }

    /**
     * Sets the value of the titularEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularEmail(String value) {
        this.titularEmail = value;
    }

    /**
     * Gets the value of the titularLlinatges property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularLlinatges() {
        return titularLlinatges;
    }

    /**
     * Sets the value of the titularLlinatges property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularLlinatges(String value) {
        this.titularLlinatges = value;
    }

    /**
     * Gets the value of the titularNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularNif() {
        return titularNif;
    }

    /**
     * Sets the value of the titularNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularNif(String value) {
        this.titularNif = value;
    }

    /**
     * Gets the value of the titularNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularNom() {
        return titularNom;
    }

    /**
     * Sets the value of the titularNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularNom(String value) {
        this.titularNom = value;
    }

    /**
     * Gets the value of the titularTelefon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularTelefon() {
        return titularTelefon;
    }

    /**
     * Sets the value of the titularTelefon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularTelefon(String value) {
        this.titularTelefon = value;
    }

}

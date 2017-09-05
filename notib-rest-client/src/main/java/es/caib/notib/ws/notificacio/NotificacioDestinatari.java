
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para notificacioDestinatari complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
 *         &lt;element name="destinatariLlinatge1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatariLlinatge2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 *         &lt;element name="titularLlinatge1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titularLlinatge2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "destinatariLlinatge1",
    "destinatariLlinatge2",
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
    "titularLlinatge1",
    "titularLlinatge2",
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
    protected String destinatariLlinatge1;
    protected String destinatariLlinatge2;
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
    protected String titularLlinatge1;
    protected String titularLlinatge2;
    protected String titularNif;
    protected String titularNom;
    protected String titularTelefon;

    /**
     * Obtiene el valor de la propiedad caducitat.
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
     * Define el valor de la propiedad caducitat.
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
     * Obtiene el valor de la propiedad dehNif.
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
     * Define el valor de la propiedad dehNif.
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
     * Obtiene el valor de la propiedad dehObligat.
     * 
     */
    public boolean isDehObligat() {
        return dehObligat;
    }

    /**
     * Define el valor de la propiedad dehObligat.
     * 
     */
    public void setDehObligat(boolean value) {
        this.dehObligat = value;
    }

    /**
     * Obtiene el valor de la propiedad dehProcedimentCodi.
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
     * Define el valor de la propiedad dehProcedimentCodi.
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
     * Obtiene el valor de la propiedad destinatariEmail.
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
     * Define el valor de la propiedad destinatariEmail.
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
     * Obtiene el valor de la propiedad destinatariLlinatge1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariLlinatge1() {
        return destinatariLlinatge1;
    }

    /**
     * Define el valor de la propiedad destinatariLlinatge1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariLlinatge1(String value) {
        this.destinatariLlinatge1 = value;
    }

    /**
     * Obtiene el valor de la propiedad destinatariLlinatge2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatariLlinatge2() {
        return destinatariLlinatge2;
    }

    /**
     * Define el valor de la propiedad destinatariLlinatge2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatariLlinatge2(String value) {
        this.destinatariLlinatge2 = value;
    }

    /**
     * Obtiene el valor de la propiedad destinatariNif.
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
     * Define el valor de la propiedad destinatariNif.
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
     * Obtiene el valor de la propiedad destinatariNom.
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
     * Define el valor de la propiedad destinatariNom.
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
     * Obtiene el valor de la propiedad destinatariTelefon.
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
     * Define el valor de la propiedad destinatariTelefon.
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
     * Obtiene el valor de la propiedad domiciliApartatCorreus.
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
     * Define el valor de la propiedad domiciliApartatCorreus.
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
     * Obtiene el valor de la propiedad domiciliBloc.
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
     * Define el valor de la propiedad domiciliBloc.
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
     * Obtiene el valor de la propiedad domiciliCie.
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
     * Define el valor de la propiedad domiciliCie.
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
     * Obtiene el valor de la propiedad domiciliCodiPostal.
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
     * Define el valor de la propiedad domiciliCodiPostal.
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
     * Obtiene el valor de la propiedad domiciliComplement.
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
     * Define el valor de la propiedad domiciliComplement.
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
     * Obtiene el valor de la propiedad domiciliConcretTipus.
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
     * Define el valor de la propiedad domiciliConcretTipus.
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
     * Obtiene el valor de la propiedad domiciliEscala.
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
     * Define el valor de la propiedad domiciliEscala.
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
     * Obtiene el valor de la propiedad domiciliLinea1.
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
     * Define el valor de la propiedad domiciliLinea1.
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
     * Obtiene el valor de la propiedad domiciliLinea2.
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
     * Define el valor de la propiedad domiciliLinea2.
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
     * Obtiene el valor de la propiedad domiciliMunicipiCodiIne.
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
     * Define el valor de la propiedad domiciliMunicipiCodiIne.
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
     * Obtiene el valor de la propiedad domiciliMunicipiNom.
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
     * Define el valor de la propiedad domiciliMunicipiNom.
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
     * Obtiene el valor de la propiedad domiciliNumeracioNumero.
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
     * Define el valor de la propiedad domiciliNumeracioNumero.
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
     * Obtiene el valor de la propiedad domiciliNumeracioPuntKm.
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
     * Define el valor de la propiedad domiciliNumeracioPuntKm.
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
     * Obtiene el valor de la propiedad domiciliNumeracioTipus.
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
     * Define el valor de la propiedad domiciliNumeracioTipus.
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
     * Obtiene el valor de la propiedad domiciliPaisCodiIso.
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
     * Define el valor de la propiedad domiciliPaisCodiIso.
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
     * Obtiene el valor de la propiedad domiciliPaisNom.
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
     * Define el valor de la propiedad domiciliPaisNom.
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
     * Obtiene el valor de la propiedad domiciliPlanta.
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
     * Define el valor de la propiedad domiciliPlanta.
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
     * Obtiene el valor de la propiedad domiciliPoblacio.
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
     * Define el valor de la propiedad domiciliPoblacio.
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
     * Obtiene el valor de la propiedad domiciliPorta.
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
     * Define el valor de la propiedad domiciliPorta.
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
     * Obtiene el valor de la propiedad domiciliPortal.
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
     * Define el valor de la propiedad domiciliPortal.
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
     * Obtiene el valor de la propiedad domiciliProvinciaCodi.
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
     * Define el valor de la propiedad domiciliProvinciaCodi.
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
     * Obtiene el valor de la propiedad domiciliProvinciaNom.
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
     * Define el valor de la propiedad domiciliProvinciaNom.
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
     * Obtiene el valor de la propiedad domiciliTipus.
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
     * Define el valor de la propiedad domiciliTipus.
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
     * Obtiene el valor de la propiedad domiciliViaNom.
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
     * Define el valor de la propiedad domiciliViaNom.
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
     * Obtiene el valor de la propiedad domiciliViaTipus.
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
     * Define el valor de la propiedad domiciliViaTipus.
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
     * Obtiene el valor de la propiedad notificaIdentificador.
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
     * Define el valor de la propiedad notificaIdentificador.
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
     * Obtiene el valor de la propiedad referencia.
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
     * Define el valor de la propiedad referencia.
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
     * Obtiene el valor de la propiedad retardPostal.
     * 
     */
    public int getRetardPostal() {
        return retardPostal;
    }

    /**
     * Define el valor de la propiedad retardPostal.
     * 
     */
    public void setRetardPostal(int value) {
        this.retardPostal = value;
    }

    /**
     * Obtiene el valor de la propiedad serveiTipus.
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
     * Define el valor de la propiedad serveiTipus.
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
     * Obtiene el valor de la propiedad seuEstat.
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
     * Define el valor de la propiedad seuEstat.
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
     * Obtiene el valor de la propiedad seuRegistreData.
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
     * Define el valor de la propiedad seuRegistreData.
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
     * Obtiene el valor de la propiedad seuRegistreNumero.
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
     * Define el valor de la propiedad seuRegistreNumero.
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
     * Obtiene el valor de la propiedad titularEmail.
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
     * Define el valor de la propiedad titularEmail.
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
     * Obtiene el valor de la propiedad titularLlinatge1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularLlinatge1() {
        return titularLlinatge1;
    }

    /**
     * Define el valor de la propiedad titularLlinatge1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularLlinatge1(String value) {
        this.titularLlinatge1 = value;
    }

    /**
     * Obtiene el valor de la propiedad titularLlinatge2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitularLlinatge2() {
        return titularLlinatge2;
    }

    /**
     * Define el valor de la propiedad titularLlinatge2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitularLlinatge2(String value) {
        this.titularLlinatge2 = value;
    }

    /**
     * Obtiene el valor de la propiedad titularNif.
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
     * Define el valor de la propiedad titularNif.
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
     * Obtiene el valor de la propiedad titularNom.
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
     * Define el valor de la propiedad titularNom.
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
     * Obtiene el valor de la propiedad titularTelefon.
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
     * Define el valor de la propiedad titularTelefon.
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

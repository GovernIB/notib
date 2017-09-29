
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entregaPostal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entregaPostal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="apartatCorreus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bloc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cie" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="codiPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="complement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="escala" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="formatFulla" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="formatSobre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linea1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linea2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="municipiCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroCasa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroQualificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paisCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="planta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="poblacio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="portal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="provinciaCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="puntKm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipus" type="{http://www.caib.es/notib/ws/notificacio}entregaPostalTipusEnum" minOccurs="0"/>
 *         &lt;element name="viaNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="viaTipus" type="{http://www.caib.es/notib/ws/notificacio}entregaPostalViaTipusEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entregaPostal", propOrder = {
    "apartatCorreus",
    "bloc",
    "cie",
    "codiPostal",
    "complement",
    "escala",
    "formatFulla",
    "formatSobre",
    "linea1",
    "linea2",
    "municipiCodi",
    "numeroCasa",
    "numeroQualificador",
    "paisCodi",
    "planta",
    "poblacio",
    "porta",
    "portal",
    "provinciaCodi",
    "puntKm",
    "tipus",
    "viaNom",
    "viaTipus"
})
public class EntregaPostal {

    protected String apartatCorreus;
    protected String bloc;
    protected Integer cie;
    protected String codiPostal;
    protected String complement;
    protected String escala;
    protected String formatFulla;
    protected String formatSobre;
    protected String linea1;
    protected String linea2;
    protected String municipiCodi;
    protected String numeroCasa;
    protected String numeroQualificador;
    protected String paisCodi;
    protected String planta;
    protected String poblacio;
    protected String porta;
    protected String portal;
    protected String provinciaCodi;
    protected String puntKm;
    protected EntregaPostalTipusEnum tipus;
    protected String viaNom;
    protected EntregaPostalViaTipusEnum viaTipus;

    /**
     * Gets the value of the apartatCorreus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApartatCorreus() {
        return apartatCorreus;
    }

    /**
     * Sets the value of the apartatCorreus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApartatCorreus(String value) {
        this.apartatCorreus = value;
    }

    /**
     * Gets the value of the bloc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBloc() {
        return bloc;
    }

    /**
     * Sets the value of the bloc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBloc(String value) {
        this.bloc = value;
    }

    /**
     * Gets the value of the cie property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCie() {
        return cie;
    }

    /**
     * Sets the value of the cie property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCie(Integer value) {
        this.cie = value;
    }

    /**
     * Gets the value of the codiPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiPostal() {
        return codiPostal;
    }

    /**
     * Sets the value of the codiPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiPostal(String value) {
        this.codiPostal = value;
    }

    /**
     * Gets the value of the complement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplement() {
        return complement;
    }

    /**
     * Sets the value of the complement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplement(String value) {
        this.complement = value;
    }

    /**
     * Gets the value of the escala property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEscala() {
        return escala;
    }

    /**
     * Sets the value of the escala property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEscala(String value) {
        this.escala = value;
    }

    /**
     * Gets the value of the formatFulla property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatFulla() {
        return formatFulla;
    }

    /**
     * Sets the value of the formatFulla property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatFulla(String value) {
        this.formatFulla = value;
    }

    /**
     * Gets the value of the formatSobre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatSobre() {
        return formatSobre;
    }

    /**
     * Sets the value of the formatSobre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatSobre(String value) {
        this.formatSobre = value;
    }

    /**
     * Gets the value of the linea1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinea1() {
        return linea1;
    }

    /**
     * Sets the value of the linea1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinea1(String value) {
        this.linea1 = value;
    }

    /**
     * Gets the value of the linea2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinea2() {
        return linea2;
    }

    /**
     * Sets the value of the linea2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinea2(String value) {
        this.linea2 = value;
    }

    /**
     * Gets the value of the municipiCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMunicipiCodi() {
        return municipiCodi;
    }

    /**
     * Sets the value of the municipiCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMunicipiCodi(String value) {
        this.municipiCodi = value;
    }

    /**
     * Gets the value of the numeroCasa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroCasa() {
        return numeroCasa;
    }

    /**
     * Sets the value of the numeroCasa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroCasa(String value) {
        this.numeroCasa = value;
    }

    /**
     * Gets the value of the numeroQualificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroQualificador() {
        return numeroQualificador;
    }

    /**
     * Sets the value of the numeroQualificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroQualificador(String value) {
        this.numeroQualificador = value;
    }

    /**
     * Gets the value of the paisCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisCodi() {
        return paisCodi;
    }

    /**
     * Sets the value of the paisCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisCodi(String value) {
        this.paisCodi = value;
    }

    /**
     * Gets the value of the planta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlanta() {
        return planta;
    }

    /**
     * Sets the value of the planta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlanta(String value) {
        this.planta = value;
    }

    /**
     * Gets the value of the poblacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoblacio() {
        return poblacio;
    }

    /**
     * Sets the value of the poblacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoblacio(String value) {
        this.poblacio = value;
    }

    /**
     * Gets the value of the porta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPorta() {
        return porta;
    }

    /**
     * Sets the value of the porta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPorta(String value) {
        this.porta = value;
    }

    /**
     * Gets the value of the portal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortal() {
        return portal;
    }

    /**
     * Sets the value of the portal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortal(String value) {
        this.portal = value;
    }

    /**
     * Gets the value of the provinciaCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvinciaCodi() {
        return provinciaCodi;
    }

    /**
     * Sets the value of the provinciaCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvinciaCodi(String value) {
        this.provinciaCodi = value;
    }

    /**
     * Gets the value of the puntKm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPuntKm() {
        return puntKm;
    }

    /**
     * Sets the value of the puntKm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPuntKm(String value) {
        this.puntKm = value;
    }

    /**
     * Gets the value of the tipus property.
     * 
     * @return
     *     possible object is
     *     {@link EntregaPostalTipusEnum }
     *     
     */
    public EntregaPostalTipusEnum getTipus() {
        return tipus;
    }

    /**
     * Sets the value of the tipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntregaPostalTipusEnum }
     *     
     */
    public void setTipus(EntregaPostalTipusEnum value) {
        this.tipus = value;
    }

    /**
     * Gets the value of the viaNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViaNom() {
        return viaNom;
    }

    /**
     * Sets the value of the viaNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViaNom(String value) {
        this.viaNom = value;
    }

    /**
     * Gets the value of the viaTipus property.
     * 
     * @return
     *     possible object is
     *     {@link EntregaPostalViaTipusEnum }
     *     
     */
    public EntregaPostalViaTipusEnum getViaTipus() {
        return viaTipus;
    }

    /**
     * Sets the value of the viaTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntregaPostalViaTipusEnum }
     *     
     */
    public void setViaTipus(EntregaPostalViaTipusEnum value) {
        this.viaTipus = value;
    }

}


package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.OrganismoPagadorCIE;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.OrganismoPagadorPostal;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntregaPostal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EntregaPostal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="organismoPagadorPostal" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}OrganismoPagadorPostal" minOccurs="0"/>
 *         &lt;element name="organismoPagadorCIE" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}OrganismoPagadorCIE" minOccurs="0"/>
 *         &lt;element name="tipoDomicilio" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}TipoDomicilio"/>
 *         &lt;element name="tipoVia" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="nombreVia" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="numeroCasa" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="puntoKilometrico" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="portal" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="puerta" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="escalera" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="planta" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="bloque" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="complemento" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="codigoPostal" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="apartadoCorreos" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="municipio" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[0-9]{5}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="provincia" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[0-9]{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="pais" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="poblacion" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="linea1" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="linea2" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="opcionesCIE" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Opciones" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntregaPostal", propOrder = {

})
public class EntregaPostal {

    protected OrganismoPagadorPostal organismoPagadorPostal;
    protected OrganismoPagadorCIE organismoPagadorCIE;
    @XmlElement(required = true)
    protected BigInteger tipoDomicilio;
    protected String tipoVia;
    protected String nombreVia;
    protected String numeroCasa;
    protected String puntoKilometrico;
    protected String portal;
    protected String puerta;
    protected String escalera;
    protected String planta;
    protected String bloque;
    protected String complemento;
    protected String codigoPostal;
    protected String apartadoCorreos;
    protected String municipio;
    protected String provincia;
    protected String pais;
    protected String poblacion;
    protected String linea1;
    protected String linea2;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones opcionesCIE;

    /**
     * Gets the value of the organismoPagadorPostal property.
     * 
     * @return
     *     possible object is
     *     {@link OrganismoPagadorPostal }
     *     
     */
    public OrganismoPagadorPostal getOrganismoPagadorPostal() {
        return organismoPagadorPostal;
    }

    /**
     * Sets the value of the organismoPagadorPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganismoPagadorPostal }
     *     
     */
    public void setOrganismoPagadorPostal(OrganismoPagadorPostal value) {
        this.organismoPagadorPostal = value;
    }

    /**
     * Gets the value of the organismoPagadorCIE property.
     * 
     * @return
     *     possible object is
     *     {@link OrganismoPagadorCIE }
     *     
     */
    public OrganismoPagadorCIE getOrganismoPagadorCIE() {
        return organismoPagadorCIE;
    }

    /**
     * Sets the value of the organismoPagadorCIE property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganismoPagadorCIE }
     *     
     */
    public void setOrganismoPagadorCIE(OrganismoPagadorCIE value) {
        this.organismoPagadorCIE = value;
    }

    /**
     * Gets the value of the tipoDomicilio property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTipoDomicilio() {
        return tipoDomicilio;
    }

    /**
     * Sets the value of the tipoDomicilio property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTipoDomicilio(BigInteger value) {
        this.tipoDomicilio = value;
    }

    /**
     * Gets the value of the tipoVia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoVia() {
        return tipoVia;
    }

    /**
     * Sets the value of the tipoVia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoVia(String value) {
        this.tipoVia = value;
    }

    /**
     * Gets the value of the nombreVia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreVia() {
        return nombreVia;
    }

    /**
     * Sets the value of the nombreVia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreVia(String value) {
        this.nombreVia = value;
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
     * Gets the value of the puntoKilometrico property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPuntoKilometrico() {
        return puntoKilometrico;
    }

    /**
     * Sets the value of the puntoKilometrico property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPuntoKilometrico(String value) {
        this.puntoKilometrico = value;
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
     * Gets the value of the puerta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPuerta() {
        return puerta;
    }

    /**
     * Sets the value of the puerta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPuerta(String value) {
        this.puerta = value;
    }

    /**
     * Gets the value of the escalera property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEscalera() {
        return escalera;
    }

    /**
     * Sets the value of the escalera property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEscalera(String value) {
        this.escalera = value;
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
     * Gets the value of the bloque property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBloque() {
        return bloque;
    }

    /**
     * Sets the value of the bloque property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBloque(String value) {
        this.bloque = value;
    }

    /**
     * Gets the value of the complemento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * Sets the value of the complemento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplemento(String value) {
        this.complemento = value;
    }

    /**
     * Gets the value of the codigoPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Sets the value of the codigoPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoPostal(String value) {
        this.codigoPostal = value;
    }

    /**
     * Gets the value of the apartadoCorreos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApartadoCorreos() {
        return apartadoCorreos;
    }

    /**
     * Sets the value of the apartadoCorreos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApartadoCorreos(String value) {
        this.apartadoCorreos = value;
    }

    /**
     * Gets the value of the municipio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Sets the value of the municipio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMunicipio(String value) {
        this.municipio = value;
    }

    /**
     * Gets the value of the provincia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Sets the value of the provincia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvincia(String value) {
        this.provincia = value;
    }

    /**
     * Gets the value of the pais property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPais() {
        return pais;
    }

    /**
     * Sets the value of the pais property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPais(String value) {
        this.pais = value;
    }

    /**
     * Gets the value of the poblacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoblacion() {
        return poblacion;
    }

    /**
     * Sets the value of the poblacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoblacion(String value) {
        this.poblacion = value;
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
     * Gets the value of the opcionesCIE property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones getOpcionesCIE() {
        return opcionesCIE;
    }

    /**
     * Sets the value of the opcionesCIE property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     *     
     */
    public void setOpcionesCIE(Opciones value) {
        this.opcionesCIE = value;
    }

}

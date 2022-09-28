
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipo_domicilio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo_domicilio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="tipo_domicilio_concreto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipo_via" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombre_via" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numero_casa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="punto_kilometrico" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="apartado_correos" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="calificador_numero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bloque" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="portal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="escalera" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="planta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="puerta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="poblacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="municipio" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_municipio"/>
 *         &lt;element name="codigo_postal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="provincia" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_provincia"/>
 *         &lt;element name="pais" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_pais"/>
 *         &lt;element name="linea_1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="linea_2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cie" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_domicilio", propOrder = {

})
public class TipoDomicilio {

    @XmlElement(name = "tipo_domicilio_concreto", required = true, nillable = true)
    protected String tipoDomicilioConcreto;
    @XmlElement(name = "tipo_via", required = true, nillable = true)
    protected String tipoVia;
    @XmlElement(name = "nombre_via", required = true, nillable = true)
    protected String nombreVia;
    @XmlElement(name = "numero_casa", required = true, nillable = true)
    protected String numeroCasa;
    @XmlElement(name = "punto_kilometrico", required = true, nillable = true)
    protected String puntoKilometrico;
    @XmlElement(name = "apartado_correos", required = true, nillable = true)
    protected String apartadoCorreos;
    @XmlElement(name = "calificador_numero", required = true, nillable = true)
    protected String calificadorNumero;
    @XmlElement(required = true, nillable = true)
    protected String bloque;
    @XmlElement(required = true, nillable = true)
    protected String portal;
    @XmlElement(required = true, nillable = true)
    protected String escalera;
    @XmlElement(required = true, nillable = true)
    protected String planta;
    @XmlElement(required = true, nillable = true)
    protected String puerta;
    @XmlElement(required = true, nillable = true)
    protected String complemento;
    @XmlElement(required = true, nillable = true)
    protected String poblacion;
    @XmlElement(required = true, nillable = true)
    protected TipoMunicipio municipio;
    @XmlElement(name = "codigo_postal", required = true, nillable = true)
    protected String codigoPostal;
    @XmlElement(required = true, nillable = true)
    protected TipoProvincia provincia;
    @XmlElement(required = true, nillable = true)
    protected TipoPais pais;
    @XmlElement(name = "linea_1", required = true, nillable = true)
    protected String linea1;
    @XmlElement(name = "linea_2", required = true, nillable = true)
    protected String linea2;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer cie;

    /**
     * Gets the value of the tipoDomicilioConcreto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDomicilioConcreto() {
        return tipoDomicilioConcreto;
    }

    /**
     * Sets the value of the tipoDomicilioConcreto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDomicilioConcreto(String value) {
        this.tipoDomicilioConcreto = value;
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
     * Gets the value of the calificadorNumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalificadorNumero() {
        return calificadorNumero;
    }

    /**
     * Sets the value of the calificadorNumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalificadorNumero(String value) {
        this.calificadorNumero = value;
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
     * Gets the value of the municipio property.
     * 
     * @return
     *     possible object is
     *     {@link TipoMunicipio }
     *     
     */
    public TipoMunicipio getMunicipio() {
        return municipio;
    }

    /**
     * Sets the value of the municipio property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoMunicipio }
     *     
     */
    public void setMunicipio(TipoMunicipio value) {
        this.municipio = value;
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
     * Gets the value of the provincia property.
     * 
     * @return
     *     possible object is
     *     {@link TipoProvincia }
     *     
     */
    public TipoProvincia getProvincia() {
        return provincia;
    }

    /**
     * Sets the value of the provincia property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoProvincia }
     *     
     */
    public void setProvincia(TipoProvincia value) {
        this.provincia = value;
    }

    /**
     * Gets the value of the pais property.
     * 
     * @return
     *     possible object is
     *     {@link TipoPais }
     *     
     */
    public TipoPais getPais() {
        return pais;
    }

    /**
     * Sets the value of the pais property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPais }
     *     
     */
    public void setPais(TipoPais value) {
        this.pais = value;
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

}

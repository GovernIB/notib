
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Procedimiento;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for resultadoInfoEnvioV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoInfoEnvioV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="concepto">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;whiteSpace value="preserve"/>
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="descripcion" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;whiteSpace value="preserve"/>
 *               &lt;maxLength value="1000"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="codigoOrganismoEmisor" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}CodigoDIR"/>
 *         &lt;element name="codigoOrganismoEmisorRaiz" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}CodigoDIR" minOccurs="0"/>
 *         &lt;element name="tipoEnvio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaCreacion" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fechaPuestaDisposicion" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fechaCaducidad" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="retardo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="procedimiento" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Procedimiento" minOccurs="0"/>
 *         &lt;element name="documento" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Documento"/>
 *         &lt;element name="referenciaEmisor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titular" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Persona"/>
 *         &lt;element name="destinatarios" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Destinatarios" minOccurs="0"/>
 *         &lt;element name="entregaPostal" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}EntregaPostal" minOccurs="0"/>
 *         &lt;element name="entregaDEH" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}EntregaDEH" minOccurs="0"/>
 *         &lt;element name="datados" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Datados" minOccurs="0"/>
 *         &lt;element name="certificacion" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Certificacion" minOccurs="0"/>
 *         &lt;element name="opcionesEnvio" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Opciones" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoInfoEnvioV2", propOrder = {

})
public class ResultadoInfoEnvioV2 {

    @XmlElement(required = true)
    protected String identificador;
    @XmlElement(required = true)
    protected String estado;
    @XmlElement(required = true)
    protected String concepto;
    protected String descripcion;
    @XmlElement(required = true)
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR codigoOrganismoEmisor;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR codigoOrganismoEmisorRaiz;
    @XmlElement(required = true)
    protected String tipoEnvio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaCreacion;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaPuestaDisposicion;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fechaCaducidad;
    protected BigInteger retardo;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Procedimiento procedimiento;
    @XmlElement(required = true)
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento documento;
    protected String referenciaEmisor;
    @XmlElement(required = true)
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona titular;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios destinatarios;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal entregaPostal;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH entregaDEH;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados datados;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion certificacion;
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones opcionesEnvio;

    /**
     * Gets the value of the identificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

    /**
     * Gets the value of the estado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Sets the value of the estado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Gets the value of the concepto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Sets the value of the concepto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcepto(String value) {
        this.concepto = value;
    }

    /**
     * Gets the value of the descripcion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Sets the value of the descripcion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    /**
     * Gets the value of the codigoOrganismoEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR getCodigoOrganismoEmisor() {
        return codigoOrganismoEmisor;
    }

    /**
     * Sets the value of the codigoOrganismoEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     *     
     */
    public void setCodigoOrganismoEmisor(es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR value) {
        this.codigoOrganismoEmisor = value;
    }

    /**
     * Gets the value of the codigoOrganismoEmisorRaiz property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR getCodigoOrganismoEmisorRaiz() {
        return codigoOrganismoEmisorRaiz;
    }

    /**
     * Sets the value of the codigoOrganismoEmisorRaiz property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     *     
     */
    public void setCodigoOrganismoEmisorRaiz(CodigoDIR value) {
        this.codigoOrganismoEmisorRaiz = value;
    }

    /**
     * Gets the value of the tipoEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoEnvio() {
        return tipoEnvio;
    }

    /**
     * Sets the value of the tipoEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoEnvio(String value) {
        this.tipoEnvio = value;
    }

    /**
     * Gets the value of the fechaCreacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Sets the value of the fechaCreacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaCreacion(XMLGregorianCalendar value) {
        this.fechaCreacion = value;
    }

    /**
     * Gets the value of the fechaPuestaDisposicion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaPuestaDisposicion() {
        return fechaPuestaDisposicion;
    }

    /**
     * Sets the value of the fechaPuestaDisposicion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaPuestaDisposicion(XMLGregorianCalendar value) {
        this.fechaPuestaDisposicion = value;
    }

    /**
     * Gets the value of the fechaCaducidad property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaCaducidad() {
        return fechaCaducidad;
    }

    /**
     * Sets the value of the fechaCaducidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaCaducidad(XMLGregorianCalendar value) {
        this.fechaCaducidad = value;
    }

    /**
     * Gets the value of the retardo property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRetardo() {
        return retardo;
    }

    /**
     * Sets the value of the retardo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRetardo(BigInteger value) {
        this.retardo = value;
    }

    /**
     * Gets the value of the procedimiento property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Procedimiento }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Procedimiento getProcedimiento() {
        return procedimiento;
    }

    /**
     * Sets the value of the procedimiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Procedimiento }
     *     
     */
    public void setProcedimiento(Procedimiento value) {
        this.procedimiento = value;
    }

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Documento }
     *     
     */
    public void setDocumento(Documento value) {
        this.documento = value;
    }

    /**
     * Gets the value of the referenciaEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenciaEmisor() {
        return referenciaEmisor;
    }

    /**
     * Sets the value of the referenciaEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenciaEmisor(String value) {
        this.referenciaEmisor = value;
    }

    /**
     * Gets the value of the titular property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona getTitular() {
        return titular;
    }

    /**
     * Sets the value of the titular property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Persona }
     *     
     */
    public void setTitular(Persona value) {
        this.titular = value;
    }

    /**
     * Gets the value of the destinatarios property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios getDestinatarios() {
        return destinatarios;
    }

    /**
     * Sets the value of the destinatarios property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatarios }
     *     
     */
    public void setDestinatarios(Destinatarios value) {
        this.destinatarios = value;
    }

    /**
     * Gets the value of the entregaPostal property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal getEntregaPostal() {
        return entregaPostal;
    }

    /**
     * Sets the value of the entregaPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaPostal }
     *     
     */
    public void setEntregaPostal(EntregaPostal value) {
        this.entregaPostal = value;
    }

    /**
     * Gets the value of the entregaDEH property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH getEntregaDEH() {
        return entregaDEH;
    }

    /**
     * Sets the value of the entregaDEH property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.EntregaDEH }
     *     
     */
    public void setEntregaDEH(EntregaDEH value) {
        this.entregaDEH = value;
    }

    /**
     * Gets the value of the datados property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados getDatados() {
        return datados;
    }

    /**
     * Sets the value of the datados property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados }
     *     
     */
    public void setDatados(Datados value) {
        this.datados = value;
    }

    /**
     * Gets the value of the certificacion property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion getCertificacion() {
        return certificacion;
    }

    /**
     * Sets the value of the certificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion }
     *     
     */
    public void setCertificacion(Certificacion value) {
        this.certificacion = value;
    }

    /**
     * Gets the value of the opcionesEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones getOpcionesEnvio() {
        return opcionesEnvio;
    }

    /**
     * Sets the value of the opcionesEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opciones }
     *     
     */
    public void setOpcionesEnvio(Opciones value) {
        this.opcionesEnvio = value;
    }

}

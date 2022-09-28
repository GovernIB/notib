
package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Documento;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opciones;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for altaRemesaEnvios complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="altaRemesaEnvios">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoOrganismoEmisor" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}CodigoDIR"/>
 *         &lt;element name="tipoEnvio" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}TipoEnvio"/>
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
 *         &lt;element name="fechaEnvioProgramado" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="procedimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documento" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Documento"/>
 *         &lt;element name="envios" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Envios"/>
 *         &lt;element name="opcionesRemesa" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Opciones" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "altaRemesaEnvios", propOrder = {

})
public class AltaRemesaEnvios {

    @XmlElement(required = true)
    protected String codigoOrganismoEmisor;
    @XmlElement(required = true)
    protected BigInteger tipoEnvio;
    @XmlElement(required = true)
    protected String concepto;
    protected String descripcion;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fechaEnvioProgramado;
    protected String procedimiento;
    @XmlElement(required = true)
    protected es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Documento documento;
    @XmlElement(required = true)
    protected es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envios envios;
    protected es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opciones opcionesRemesa;

    /**
     * Gets the value of the codigoOrganismoEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoOrganismoEmisor() {
        return codigoOrganismoEmisor;
    }

    /**
     * Sets the value of the codigoOrganismoEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoOrganismoEmisor(String value) {
        this.codigoOrganismoEmisor = value;
    }

    /**
     * Gets the value of the tipoEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTipoEnvio() {
        return tipoEnvio;
    }

    /**
     * Sets the value of the tipoEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTipoEnvio(BigInteger value) {
        this.tipoEnvio = value;
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
     * Gets the value of the fechaEnvioProgramado property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaEnvioProgramado() {
        return fechaEnvioProgramado;
    }

    /**
     * Sets the value of the fechaEnvioProgramado property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaEnvioProgramado(XMLGregorianCalendar value) {
        this.fechaEnvioProgramado = value;
    }

    /**
     * Gets the value of the procedimiento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedimiento() {
        return procedimiento;
    }

    /**
     * Sets the value of the procedimiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedimiento(String value) {
        this.procedimiento = value;
    }

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Documento }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Documento getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Documento }
     *     
     */
    public void setDocumento(Documento value) {
        this.documento = value;
    }

    /**
     * Gets the value of the envios property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envios }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envios getEnvios() {
        return envios;
    }

    /**
     * Sets the value of the envios property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envios }
     *     
     */
    public void setEnvios(Envios value) {
        this.envios = value;
    }

    /**
     * Gets the value of the opcionesRemesa property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opciones }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opciones getOpcionesRemesa() {
        return opcionesRemesa;
    }

    /**
     * Sets the value of the opcionesRemesa property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opciones }
     *     
     */
    public void setOpcionesRemesa(Opciones value) {
        this.opcionesRemesa = value;
    }

}

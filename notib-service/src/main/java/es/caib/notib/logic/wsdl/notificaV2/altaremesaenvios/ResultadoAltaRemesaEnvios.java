
package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for resultadoAltaRemesaEnvios complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoAltaRemesaEnvios">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoOrganismoEmisor" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}CodigoDIR" minOccurs="0"/>
 *         &lt;element name="fechaCreacion" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="resultadoEnvios" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}ResultadoEnvios" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoAltaRemesaEnvios", propOrder = {

})
public class ResultadoAltaRemesaEnvios {

    @XmlElement(required = true)
    protected String codigoRespuesta;
    @XmlElement(required = true)
    protected String descripcionRespuesta;
    protected String codigoOrganismoEmisor;
    @XmlElementRef(name = "fechaCreacion", namespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaCreacion;
    protected es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios resultadoEnvios;

    /**
     * Gets the value of the codigoRespuesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    /**
     * Sets the value of the codigoRespuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRespuesta(String value) {
        this.codigoRespuesta = value;
    }

    /**
     * Gets the value of the descripcionRespuesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionRespuesta() {
        return descripcionRespuesta;
    }

    /**
     * Sets the value of the descripcionRespuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionRespuesta(String value) {
        this.descripcionRespuesta = value;
    }

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
     * Gets the value of the fechaCreacion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Sets the value of the fechaCreacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaCreacion(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaCreacion = value;
    }

    /**
     * Gets the value of the resultadoEnvios property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios getResultadoEnvios() {
        return resultadoEnvios;
    }

    /**
     * Sets the value of the resultadoEnvios property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios }
     *     
     */
    public void setResultadoEnvios(ResultadoEnvios value) {
        this.resultadoEnvios = value;
    }

}

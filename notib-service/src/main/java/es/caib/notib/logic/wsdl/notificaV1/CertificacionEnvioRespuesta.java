
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for certificacion_envio_respuesta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="certificacion_envio_respuesta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador_envio" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}identificador_envio"/>
 *         &lt;element name="pdf_certificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xml_certificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="certificacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha_actualizacion" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ncc_id_externo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certificacion_envio_respuesta", propOrder = {

})
public class CertificacionEnvioRespuesta {

    @XmlElement(name = "identificador_envio", required = true)
    protected IdentificadorEnvio identificadorEnvio;
    @XmlElement(name = "pdf_certificado", required = true, nillable = true)
    protected String pdfCertificado;
    @XmlElement(name = "xml_certificado", required = true, nillable = true)
    protected String xmlCertificado;
    @XmlElement(required = true)
    protected String certificacion;
    @XmlElement(name = "fecha_actualizacion", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaActualizacion;
    @XmlElement(name = "ncc_id_externo", required = true, nillable = true)
    protected String nccIdExterno;

    /**
     * Gets the value of the identificadorEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificadorEnvio }
     *     
     */
    public IdentificadorEnvio getIdentificadorEnvio() {
        return identificadorEnvio;
    }

    /**
     * Sets the value of the identificadorEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificadorEnvio }
     *     
     */
    public void setIdentificadorEnvio(IdentificadorEnvio value) {
        this.identificadorEnvio = value;
    }

    /**
     * Gets the value of the pdfCertificado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfCertificado() {
        return pdfCertificado;
    }

    /**
     * Sets the value of the pdfCertificado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfCertificado(String value) {
        this.pdfCertificado = value;
    }

    /**
     * Gets the value of the xmlCertificado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlCertificado() {
        return xmlCertificado;
    }

    /**
     * Sets the value of the xmlCertificado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlCertificado(String value) {
        this.xmlCertificado = value;
    }

    /**
     * Gets the value of the certificacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificacion() {
        return certificacion;
    }

    /**
     * Sets the value of the certificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificacion(String value) {
        this.certificacion = value;
    }

    /**
     * Gets the value of the fechaActualizacion property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaActualizacion() {
        return fechaActualizacion;
    }

    /**
     * Sets the value of the fechaActualizacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaActualizacion(XMLGregorianCalendar value) {
        this.fechaActualizacion = value;
    }

    /**
     * Gets the value of the nccIdExterno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNccIdExterno() {
        return nccIdExterno;
    }

    /**
     * Sets the value of the nccIdExterno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNccIdExterno(String value) {
        this.nccIdExterno = value;
    }

}


package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for tipo_envio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo_envio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="organismo_emisor" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipoOrganismoEmisor"/>
 *         &lt;element name="organismo_pagador_correos" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipoOrganismoPagadorCorreos"/>
 *         &lt;element name="organismo_pagador_cie" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipoOrganismoPagadorCIE"/>
 *         &lt;element name="documento" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}documento"/>
 *         &lt;element name="tipo_envio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha_envio_programado" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="concepto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinatarios" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfTipo_destinatario"/>
 *         &lt;element name="procedimiento" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_procedimiento"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_envio", propOrder = {

})
public class TipoEnvio {

    @XmlElement(name = "organismo_emisor", required = true)
    protected TipoOrganismoEmisor organismoEmisor;
    @XmlElement(name = "organismo_pagador_correos", required = true, nillable = true)
    protected TipoOrganismoPagadorCorreos organismoPagadorCorreos;
    @XmlElement(name = "organismo_pagador_cie", required = true, nillable = true)
    protected TipoOrganismoPagadorCIE organismoPagadorCie;
    @XmlElement(required = true)
    protected Documento documento;
    @XmlElement(name = "tipo_envio", required = true)
    protected String tipoEnvio;
    @XmlElement(name = "fecha_envio_programado", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaEnvioProgramado;
    @XmlElement(required = true)
    protected String concepto;
    @XmlElement(required = true)
    protected ArrayOfTipoDestinatario destinatarios;
    @XmlElement(required = true, nillable = true)
    protected TipoProcedimiento procedimiento;

    /**
     * Gets the value of the organismoEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link TipoOrganismoEmisor }
     *     
     */
    public TipoOrganismoEmisor getOrganismoEmisor() {
        return organismoEmisor;
    }

    /**
     * Sets the value of the organismoEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoOrganismoEmisor }
     *     
     */
    public void setOrganismoEmisor(TipoOrganismoEmisor value) {
        this.organismoEmisor = value;
    }

    /**
     * Gets the value of the organismoPagadorCorreos property.
     * 
     * @return
     *     possible object is
     *     {@link TipoOrganismoPagadorCorreos }
     *     
     */
    public TipoOrganismoPagadorCorreos getOrganismoPagadorCorreos() {
        return organismoPagadorCorreos;
    }

    /**
     * Sets the value of the organismoPagadorCorreos property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoOrganismoPagadorCorreos }
     *     
     */
    public void setOrganismoPagadorCorreos(TipoOrganismoPagadorCorreos value) {
        this.organismoPagadorCorreos = value;
    }

    /**
     * Gets the value of the organismoPagadorCie property.
     * 
     * @return
     *     possible object is
     *     {@link TipoOrganismoPagadorCIE }
     *     
     */
    public TipoOrganismoPagadorCIE getOrganismoPagadorCie() {
        return organismoPagadorCie;
    }

    /**
     * Sets the value of the organismoPagadorCie property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoOrganismoPagadorCIE }
     *     
     */
    public void setOrganismoPagadorCie(TipoOrganismoPagadorCIE value) {
        this.organismoPagadorCie = value;
    }

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link Documento }
     *     
     */
    public Documento getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento }
     *     
     */
    public void setDocumento(Documento value) {
        this.documento = value;
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
     * Gets the value of the destinatarios property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTipoDestinatario }
     *     
     */
    public ArrayOfTipoDestinatario getDestinatarios() {
        return destinatarios;
    }

    /**
     * Sets the value of the destinatarios property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTipoDestinatario }
     *     
     */
    public void setDestinatarios(ArrayOfTipoDestinatario value) {
        this.destinatarios = value;
    }

    /**
     * Gets the value of the procedimiento property.
     * 
     * @return
     *     possible object is
     *     {@link TipoProcedimiento }
     *     
     */
    public TipoProcedimiento getProcedimiento() {
        return procedimiento;
    }

    /**
     * Sets the value of the procedimiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoProcedimiento }
     *     
     */
    public void setProcedimiento(TipoProcedimiento value) {
        this.procedimiento = value;
    }

}

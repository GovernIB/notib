
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for info_envio2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="info_envio2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="organismo_emisor" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipoOrganismoEmisor"/>
 *         &lt;element name="mime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipo_envio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="concepto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinatarios" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfTipo_destinatario2"/>
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
@XmlType(name = "info_envio2", propOrder = {

})
public class InfoEnvio2 {

    @XmlElement(name = "organismo_emisor", required = true)
    protected TipoOrganismoEmisor organismoEmisor;
    @XmlElement(required = true)
    protected String mime;
    @XmlElement(name = "tipo_envio", required = true)
    protected String tipoEnvio;
    @XmlElement(required = true)
    protected String concepto;
    @XmlElement(required = true)
    protected ArrayOfTipoDestinatario2 destinatarios;
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
     * Gets the value of the mime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMime() {
        return mime;
    }

    /**
     * Sets the value of the mime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMime(String value) {
        this.mime = value;
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
     *     {@link ArrayOfTipoDestinatario2 }
     *     
     */
    public ArrayOfTipoDestinatario2 getDestinatarios() {
        return destinatarios;
    }

    /**
     * Sets the value of the destinatarios property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTipoDestinatario2 }
     *     
     */
    public void setDestinatarios(ArrayOfTipoDestinatario2 value) {
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

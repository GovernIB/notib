
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipo_destinatario2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo_destinatario2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="referencia_emisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="titular" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_persona_destinatario"/>
 *         &lt;element name="destinatario" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}tipo_persona_destinatario"/>
 *         &lt;element name="servicio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="opciones_emision" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}opciones_emision"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_destinatario2", propOrder = {

})
public class TipoDestinatario2 {

    @XmlElement(name = "referencia_emisor", required = true)
    protected String referenciaEmisor;
    @XmlElement(required = true)
    protected TipoPersonaDestinatario titular;
    @XmlElement(required = true)
    protected TipoPersonaDestinatario destinatario;
    @XmlElement(required = true)
    protected String servicio;
    @XmlElement(name = "opciones_emision", required = true, nillable = true)
    protected OpcionesEmision opcionesEmision;

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
     *     {@link TipoPersonaDestinatario }
     *     
     */
    public TipoPersonaDestinatario getTitular() {
        return titular;
    }

    /**
     * Sets the value of the titular property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPersonaDestinatario }
     *     
     */
    public void setTitular(TipoPersonaDestinatario value) {
        this.titular = value;
    }

    /**
     * Gets the value of the destinatario property.
     * 
     * @return
     *     possible object is
     *     {@link TipoPersonaDestinatario }
     *     
     */
    public TipoPersonaDestinatario getDestinatario() {
        return destinatario;
    }

    /**
     * Sets the value of the destinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPersonaDestinatario }
     *     
     */
    public void setDestinatario(TipoPersonaDestinatario value) {
        this.destinatario = value;
    }

    /**
     * Gets the value of the servicio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicio() {
        return servicio;
    }

    /**
     * Sets the value of the servicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicio(String value) {
        this.servicio = value;
    }

    /**
     * Gets the value of the opcionesEmision property.
     * 
     * @return
     *     possible object is
     *     {@link OpcionesEmision }
     *     
     */
    public OpcionesEmision getOpcionesEmision() {
        return opcionesEmision;
    }

    /**
     * Sets the value of the opcionesEmision property.
     * 
     * @param value
     *     allowed object is
     *     {@link OpcionesEmision }
     *     
     */
    public void setOpcionesEmision(OpcionesEmision value) {
        this.opcionesEmision = value;
    }

}

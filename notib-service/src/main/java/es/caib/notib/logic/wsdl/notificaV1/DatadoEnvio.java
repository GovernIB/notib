
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for datado_envio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="datado_envio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador_envio" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}identificador_envio"/>
 *         &lt;element name="datado" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfTipoIntento"/>
 *         &lt;element name="estado_actual" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcion_estado_actual" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "datado_envio", propOrder = {

})
public class DatadoEnvio {

    @XmlElement(name = "identificador_envio", required = true)
    protected IdentificadorEnvio identificadorEnvio;
    @XmlElement(required = true)
    protected ArrayOfTipoIntento datado;
    @XmlElement(name = "estado_actual", required = true)
    protected String estadoActual;
    @XmlElement(name = "descripcion_estado_actual", required = true)
    protected String descripcionEstadoActual;
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
     * Gets the value of the datado property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTipoIntento }
     *     
     */
    public ArrayOfTipoIntento getDatado() {
        return datado;
    }

    /**
     * Sets the value of the datado property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTipoIntento }
     *     
     */
    public void setDatado(ArrayOfTipoIntento value) {
        this.datado = value;
    }

    /**
     * Gets the value of the estadoActual property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoActual() {
        return estadoActual;
    }

    /**
     * Sets the value of the estadoActual property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoActual(String value) {
        this.estadoActual = value;
    }

    /**
     * Gets the value of the descripcionEstadoActual property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionEstadoActual() {
        return descripcionEstadoActual;
    }

    /**
     * Sets the value of the descripcionEstadoActual property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionEstadoActual(String value) {
        this.descripcionEstadoActual = value;
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

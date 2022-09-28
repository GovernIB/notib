
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultado_get_certificacion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultado_get_certificacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="contenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="acuse_o_sobre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigo_respuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcion_respuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultado_get_certificacion", propOrder = {

})
public class ResultadoGetCertificacion {

    @XmlElement(required = true)
    protected String contenido;
    @XmlElement(required = true)
    protected String hash;
    @XmlElement(name = "acuse_o_sobre", required = true)
    protected String acuseOSobre;
    @XmlElement(required = true)
    protected String fecha;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the contenido property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Sets the value of the contenido property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContenido(String value) {
        this.contenido = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the acuseOSobre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcuseOSobre() {
        return acuseOSobre;
    }

    /**
     * Sets the value of the acuseOSobre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcuseOSobre(String value) {
        this.acuseOSobre = value;
    }

    /**
     * Gets the value of the fecha property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Sets the value of the fecha property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFecha(String value) {
        this.fecha = value;
    }

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

}

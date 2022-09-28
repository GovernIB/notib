
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoGetEnviosAgrupados2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoGetEnviosAgrupados2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="porOrganismoRemisor" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfPorOrganismoRemisor2"/>
 *         &lt;element name="porCodigoSia" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfPorCodigoSia2"/>
 *         &lt;element name="porEstado" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}PorEstado2"/>
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
@XmlType(name = "resultadoGetEnviosAgrupados2", propOrder = {

})
public class ResultadoGetEnviosAgrupados2 {

    @XmlElement(required = true)
    protected ArrayOfPorOrganismoRemisor2 porOrganismoRemisor;
    @XmlElement(required = true)
    protected ArrayOfPorCodigoSia2 porCodigoSia;
    @XmlElement(required = true)
    protected PorEstado2 porEstado;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the porOrganismoRemisor property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPorOrganismoRemisor2 }
     *     
     */
    public ArrayOfPorOrganismoRemisor2 getPorOrganismoRemisor() {
        return porOrganismoRemisor;
    }

    /**
     * Sets the value of the porOrganismoRemisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPorOrganismoRemisor2 }
     *     
     */
    public void setPorOrganismoRemisor(ArrayOfPorOrganismoRemisor2 value) {
        this.porOrganismoRemisor = value;
    }

    /**
     * Gets the value of the porCodigoSia property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPorCodigoSia2 }
     *     
     */
    public ArrayOfPorCodigoSia2 getPorCodigoSia() {
        return porCodigoSia;
    }

    /**
     * Sets the value of the porCodigoSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPorCodigoSia2 }
     *     
     */
    public void setPorCodigoSia(ArrayOfPorCodigoSia2 value) {
        this.porCodigoSia = value;
    }

    /**
     * Gets the value of the porEstado property.
     * 
     * @return
     *     possible object is
     *     {@link PorEstado2 }
     *     
     */
    public PorEstado2 getPorEstado() {
        return porEstado;
    }

    /**
     * Sets the value of the porEstado property.
     * 
     * @param value
     *     allowed object is
     *     {@link PorEstado2 }
     *     
     */
    public void setPorEstado(PorEstado2 value) {
        this.porEstado = value;
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

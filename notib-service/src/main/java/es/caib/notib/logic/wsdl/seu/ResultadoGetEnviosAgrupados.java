
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoGetEnviosAgrupados complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoGetEnviosAgrupados">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="porOrganismoRemisor" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfPorOrganismoRemisor"/>
 *         &lt;element name="porCodigoSia" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfPorCodigoSia"/>
 *         &lt;element name="porEstado" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}PorEstado"/>
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
@XmlType(name = "resultadoGetEnviosAgrupados", propOrder = {

})
public class ResultadoGetEnviosAgrupados {

    @XmlElement(required = true)
    protected ArrayOfPorOrganismoRemisor porOrganismoRemisor;
    @XmlElement(required = true)
    protected ArrayOfPorCodigoSia porCodigoSia;
    @XmlElement(required = true)
    protected PorEstado porEstado;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the porOrganismoRemisor property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPorOrganismoRemisor }
     *     
     */
    public ArrayOfPorOrganismoRemisor getPorOrganismoRemisor() {
        return porOrganismoRemisor;
    }

    /**
     * Sets the value of the porOrganismoRemisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPorOrganismoRemisor }
     *     
     */
    public void setPorOrganismoRemisor(ArrayOfPorOrganismoRemisor value) {
        this.porOrganismoRemisor = value;
    }

    /**
     * Gets the value of the porCodigoSia property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPorCodigoSia }
     *     
     */
    public ArrayOfPorCodigoSia getPorCodigoSia() {
        return porCodigoSia;
    }

    /**
     * Sets the value of the porCodigoSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPorCodigoSia }
     *     
     */
    public void setPorCodigoSia(ArrayOfPorCodigoSia value) {
        this.porCodigoSia = value;
    }

    /**
     * Gets the value of the porEstado property.
     * 
     * @return
     *     possible object is
     *     {@link PorEstado }
     *     
     */
    public PorEstado getPorEstado() {
        return porEstado;
    }

    /**
     * Sets the value of the porEstado property.
     * 
     * @param value
     *     allowed object is
     *     {@link PorEstado }
     *     
     */
    public void setPorEstado(PorEstado value) {
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

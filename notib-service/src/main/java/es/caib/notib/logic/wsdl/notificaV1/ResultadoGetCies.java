
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoGetCies complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoGetCies">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="cies" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfCie"/>
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
@XmlType(name = "resultadoGetCies", propOrder = {

})
public class ResultadoGetCies {

    @XmlElement(required = true)
    protected ArrayOfCie cies;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the cies property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCie }
     *     
     */
    public ArrayOfCie getCies() {
        return cies;
    }

    /**
     * Sets the value of the cies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCie }
     *     
     */
    public void setCies(ArrayOfCie value) {
        this.cies = value;
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


package es.caib.notib.logic.wsdl.notificaV2.getcies;

import es.caib.notib.logic.wsdl.notificaV2.getcies.Cies;

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
 *         &lt;element name="cies" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/getCies}Cies"/>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    protected es.caib.notib.logic.wsdl.notificaV2.getcies.Cies cies;
    @XmlElement(required = true)
    protected String codigoRespuesta;
    @XmlElement(required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the cies property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.getcies.Cies }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.getcies.Cies getCies() {
        return cies;
    }

    /**
     * Sets the value of the cies property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.getcies.Cies }
     *     
     */
    public void setCies(Cies value) {
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

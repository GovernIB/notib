
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoInfoEnvio2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoInfoEnvio2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="infoEnvio" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}info_envio2"/>
 *         &lt;element name="certificada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "resultadoInfoEnvio2", propOrder = {

})
public class ResultadoInfoEnvio2 {

    @XmlElement(required = true)
    protected InfoEnvio2 infoEnvio;
    protected boolean certificada;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the infoEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link InfoEnvio2 }
     *     
     */
    public InfoEnvio2 getInfoEnvio() {
        return infoEnvio;
    }

    /**
     * Sets the value of the infoEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoEnvio2 }
     *     
     */
    public void setInfoEnvio(InfoEnvio2 value) {
        this.infoEnvio = value;
    }

    /**
     * Gets the value of the certificada property.
     * 
     */
    public boolean isCertificada() {
        return certificada;
    }

    /**
     * Sets the value of the certificada property.
     * 
     */
    public void setCertificada(boolean value) {
        this.certificada = value;
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
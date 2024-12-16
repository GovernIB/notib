
package es.caib.notib.logic.wsdl.notificaV2.ampliarPlazoOE;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RespuestaAmpliarPlazoOE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RespuestaAmpliarPlazoOE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ampliacionesPlazo" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/ampliarPlazoOE}AmpliacionesPlazo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaAmpliarPlazoOE", propOrder = {
    "codigoRespuesta",
    "descripcionRespuesta",
    "ampliacionesPlazo"
})
public class RespuestaAmpliarPlazoOE {

    @XmlElement(required = true)
    protected String codigoRespuesta;
    @XmlElement(required = true)
    protected String descripcionRespuesta;
    protected AmpliacionesPlazo ampliacionesPlazo;

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

    /**
     * Gets the value of the ampliacionesPlazo property.
     * 
     * @return
     *     possible object is
     *     {@link AmpliacionesPlazo }
     *     
     */
    public AmpliacionesPlazo getAmpliacionesPlazo() {
        return ampliacionesPlazo;
    }

    /**
     * Sets the value of the ampliacionesPlazo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmpliacionesPlazo }
     *     
     */
    public void setAmpliacionesPlazo(AmpliacionesPlazo value) {
        this.ampliacionesPlazo = value;
    }

}

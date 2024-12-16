
package es.caib.notib.logic.wsdl.notificaV2.ampliarPlazoOE;

import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AmpliarPlazoOE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AmpliarPlazoOE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="envios" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/ampliarPlazoOE}Envios"/>
 *         &lt;element name="organismoEmisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plazo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmpliarPlazoOE", propOrder = {
    "envios",
    "organismoEmisor",
    "plazo",
    "motivo"
})
@Setter
public class AmpliarPlazoOE {

    @XmlElement(required = true)
    protected Envios envios;
    @XmlElement(required = true)
    protected String organismoEmisor;
    @XmlElement(required = true)
    protected String plazo;
    @XmlElement(required = true)
    protected String motivo;

    /**
     * Gets the value of the envios property.
     * 
     * @return
     *     possible object is
     *     {@link Envios }
     *     
     */
    public Envios getEnvios() {
        return envios;
    }

    /**
     * Sets the value of the envios property.
     * 
     * @param value
     *     allowed object is
     *     {@link Envios }
     *     
     */
    public void setEnvios(Envios value) {
        this.envios = value;
    }

    /**
     * Gets the value of the organismoEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismoEmisor() {
        return organismoEmisor;
    }

    /**
     * Sets the value of the organismoEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismoEmisor(String value) {
        this.organismoEmisor = value;
    }

    /**
     * Gets the value of the plazo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlazo() {
        return plazo;
    }

    /**
     * Sets the value of the plazo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlazo(String value) {
        this.plazo = value;
    }

    /**
     * Gets the value of the motivo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Sets the value of the motivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivo(String value) {
        this.motivo = value;
    }

}

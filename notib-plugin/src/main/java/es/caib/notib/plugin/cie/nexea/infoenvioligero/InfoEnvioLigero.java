
package es.caib.notib.plugin.cie.nexea.infoenvioligero;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;


/**
 * <p>Java class for infoEnvioLigero complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="infoEnvioLigero">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="referenciaEmisor" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="codigoDir3" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="campos" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero}CamposP" minOccurs="0" form="qualified"/>
 *         &lt;element name="nivelDetalle" type="{http://www.w3.org/2001/XMLSchema}integer" form="qualified"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoEnvioLigero", propOrder = {

})
public class InfoEnvioLigero {

    @XmlElement(required = true)
    protected String identificador;
    @XmlElement(required = true)
    protected String referenciaEmisor;
    @XmlElement(required = true)
    protected String codigoDir3;
    protected CamposP campos;
    @XmlElement(required = true)
    protected BigInteger nivelDetalle;

    /**
     * Gets the value of the identificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

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
     * Gets the value of the codigoDir3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoDir3() {
        return codigoDir3;
    }

    /**
     * Sets the value of the codigoDir3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoDir3(String value) {
        this.codigoDir3 = value;
    }

    /**
     * Gets the value of the campos property.
     * 
     * @return
     *     possible object is
     *     {@link CamposP }
     *     
     */
    public CamposP getCampos() {
        return campos;
    }

    /**
     * Sets the value of the campos property.
     * 
     * @param value
     *     allowed object is
     *     {@link CamposP }
     *     
     */
    public void setCampos(CamposP value) {
        this.campos = value;
    }

    /**
     * Gets the value of the nivelDetalle property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNivelDetalle() {
        return nivelDetalle;
    }

    /**
     * Sets the value of the nivelDetalle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNivelDetalle(BigInteger value) {
        this.nivelDetalle = value;
    }

}

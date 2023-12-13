
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.notib.logic.wsdl.notificaV2.common.Opciones;


/**
 * <p>Java class for InfoEnvioLigero complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InfoEnvioLigero">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referenciaEmisor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoDir3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nivelDetalle" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero}NivelDetalle"/>
 *         &lt;element name="opcionesInfoEnvioLigero" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/common}Opciones" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoEnvioLigero", propOrder = {
    "identificador",
    "referenciaEmisor",
    "codigoDir3",
    "nivelDetalle",
    "opcionesInfoEnvioLigero"
})
public class InfoEnvioLigero {

    protected String identificador;
    protected String referenciaEmisor;
    protected String codigoDir3;
    @XmlElement(required = true)
    protected BigInteger nivelDetalle;
    protected Opciones opcionesInfoEnvioLigero;

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

    /**
     * Gets the value of the opcionesInfoEnvioLigero property.
     * 
     * @return
     *     possible object is
     *     {@link Opciones }
     *     
     */
    public Opciones getOpcionesInfoEnvioLigero() {
        return opcionesInfoEnvioLigero;
    }

    /**
     * Sets the value of the opcionesInfoEnvioLigero property.
     * 
     * @param value
     *     allowed object is
     *     {@link Opciones }
     *     
     */
    public void setOpcionesInfoEnvioLigero(Opciones value) {
        this.opcionesInfoEnvioLigero = value;
    }

}

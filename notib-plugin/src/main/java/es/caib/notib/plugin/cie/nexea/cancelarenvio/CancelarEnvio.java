
package es.caib.notib.plugin.cie.nexea.cancelarenvio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cancelarEnvio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cancelarEnvio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="referenciaEmisor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="codigoDir3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="filtros" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/cancelarEnvio}Filtros" minOccurs="0" form="qualified"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cancelarEnvio", propOrder = {

})
public class CancelarEnvio {

    protected String identificador;
    protected String referenciaEmisor;
    protected String codigoDir3;
    protected Filtros filtros;

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
     * Gets the value of the filtros property.
     * 
     * @return
     *     possible object is
     *     {@link Filtros }
     *     
     */
    public Filtros getFiltros() {
        return filtros;
    }

    /**
     * Sets the value of the filtros property.
     * 
     * @param value
     *     allowed object is
     *     {@link Filtros }
     *     
     */
    public void setFiltros(Filtros value) {
        this.filtros = value;
    }

}

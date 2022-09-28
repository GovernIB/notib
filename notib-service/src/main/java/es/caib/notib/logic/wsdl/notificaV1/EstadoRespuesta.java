
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for estado_respuesta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="estado_respuesta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificador_envio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ncc_id_externo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "estado_respuesta", propOrder = {

})
public class EstadoRespuesta {

    @XmlElement(name = "identificador_envio", required = true)
    protected String identificadorEnvio;
    @XmlElement(required = true)
    protected String estado;
    @XmlElement(name = "ncc_id_externo", required = true, nillable = true)
    protected String nccIdExterno;

    /**
     * Gets the value of the identificadorEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorEnvio() {
        return identificadorEnvio;
    }

    /**
     * Sets the value of the identificadorEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorEnvio(String value) {
        this.identificadorEnvio = value;
    }

    /**
     * Gets the value of the estado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Sets the value of the estado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Gets the value of the nccIdExterno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNccIdExterno() {
        return nccIdExterno;
    }

    /**
     * Sets the value of the nccIdExterno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNccIdExterno(String value) {
        this.nccIdExterno = value;
    }

}

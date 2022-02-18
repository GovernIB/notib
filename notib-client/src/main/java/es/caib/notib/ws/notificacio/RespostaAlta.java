
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for respostaAlta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaAlta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="errorDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}notificacioEstatEnum" minOccurs="0"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referencies" type="{http://www.caib.es/notib/ws/notificacio}enviamentReferencia" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respostaAlta", propOrder = {
    "error",
    "errorDescripcio",
    "estat",
    "identificador",
    "referencies"
})
public class RespostaAlta {

    protected boolean error;
    protected String errorDescripcio;
    protected NotificacioEstatEnum estat;
    protected String identificador;
    @XmlElement(nillable = true)
    protected List<EnviamentReferencia> referencies;

    /**
     * Gets the value of the error property.
     * 
     */
    public boolean isError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     */
    public void setError(boolean value) {
        this.error = value;
    }

    /**
     * Gets the value of the errorDescripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDescripcio() {
        return errorDescripcio;
    }

    /**
     * Sets the value of the errorDescripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDescripcio(String value) {
        this.errorDescripcio = value;
    }

    /**
     * Gets the value of the estat property.
     * 
     * @return
     *     possible object is
     *     {@link NotificacioEstatEnum }
     *     
     */
    public NotificacioEstatEnum getEstat() {
        return estat;
    }

    /**
     * Sets the value of the estat property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificacioEstatEnum }
     *     
     */
    public void setEstat(NotificacioEstatEnum value) {
        this.estat = value;
    }

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
     * Gets the value of the referencies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referencies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnviamentReferencia }
     * 
     * 
     */
    public List<EnviamentReferencia> getReferencies() {
        if (referencies == null) {
            referencies = new ArrayList<EnviamentReferencia>();
        }
        return this.referencies;
    }

}

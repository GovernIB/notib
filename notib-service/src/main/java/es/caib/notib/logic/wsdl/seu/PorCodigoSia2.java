
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PorCodigoSia2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PorCodigoSia2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoSia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionSia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="estado_notificado" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="estado_pendiente" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totales" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PorCodigoSia2", propOrder = {

})
public class PorCodigoSia2 {

    @XmlElement(required = true)
    protected String codigoSia;
    @XmlElement(required = true)
    protected String descripcionSia;
    @XmlElement(name = "estado_notificado")
    protected int estadoNotificado;
    @XmlElement(name = "estado_pendiente")
    protected int estadoPendiente;
    protected int totales;

    /**
     * Gets the value of the codigoSia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoSia() {
        return codigoSia;
    }

    /**
     * Sets the value of the codigoSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoSia(String value) {
        this.codigoSia = value;
    }

    /**
     * Gets the value of the descripcionSia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionSia() {
        return descripcionSia;
    }

    /**
     * Sets the value of the descripcionSia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionSia(String value) {
        this.descripcionSia = value;
    }

    /**
     * Gets the value of the estadoNotificado property.
     * 
     */
    public int getEstadoNotificado() {
        return estadoNotificado;
    }

    /**
     * Sets the value of the estadoNotificado property.
     * 
     */
    public void setEstadoNotificado(int value) {
        this.estadoNotificado = value;
    }

    /**
     * Gets the value of the estadoPendiente property.
     * 
     */
    public int getEstadoPendiente() {
        return estadoPendiente;
    }

    /**
     * Sets the value of the estadoPendiente property.
     * 
     */
    public void setEstadoPendiente(int value) {
        this.estadoPendiente = value;
    }

    /**
     * Gets the value of the totales property.
     * 
     */
    public int getTotales() {
        return totales;
    }

    /**
     * Sets the value of the totales property.
     * 
     */
    public void setTotales(int value) {
        this.totales = value;
    }

}

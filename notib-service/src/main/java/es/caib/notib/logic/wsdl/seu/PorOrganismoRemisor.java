
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PorOrganismoRemisor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PorOrganismoRemisor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="organismoRemisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreOrganismoRemisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "PorOrganismoRemisor", propOrder = {

})
public class PorOrganismoRemisor {

    @XmlElement(required = true)
    protected String organismoRemisor;
    @XmlElement(required = true)
    protected String nombreOrganismoRemisor;
    @XmlElement(name = "estado_notificado")
    protected int estadoNotificado;
    @XmlElement(name = "estado_pendiente")
    protected int estadoPendiente;
    protected int totales;

    /**
     * Gets the value of the organismoRemisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismoRemisor() {
        return organismoRemisor;
    }

    /**
     * Sets the value of the organismoRemisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismoRemisor(String value) {
        this.organismoRemisor = value;
    }

    /**
     * Gets the value of the nombreOrganismoRemisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreOrganismoRemisor() {
        return nombreOrganismoRemisor;
    }

    /**
     * Sets the value of the nombreOrganismoRemisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreOrganismoRemisor(String value) {
        this.nombreOrganismoRemisor = value;
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

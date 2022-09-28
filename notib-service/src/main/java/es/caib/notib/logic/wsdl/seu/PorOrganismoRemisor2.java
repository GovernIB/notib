
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PorOrganismoRemisor2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PorOrganismoRemisor2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="organismoRemisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreOrganismoRemisor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="notificaciones_realizadas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="comunicaciones_realizadas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="notificaciones_pendientes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="comunicaciones_pendientes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="total_organismo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PorOrganismoRemisor2", propOrder = {

})
public class PorOrganismoRemisor2 {

    @XmlElement(required = true)
    protected String organismoRemisor;
    @XmlElement(required = true)
    protected String nombreOrganismoRemisor;
    @XmlElement(name = "notificaciones_realizadas")
    protected int notificacionesRealizadas;
    @XmlElement(name = "comunicaciones_realizadas")
    protected int comunicacionesRealizadas;
    @XmlElement(name = "notificaciones_pendientes")
    protected int notificacionesPendientes;
    @XmlElement(name = "comunicaciones_pendientes")
    protected int comunicacionesPendientes;
    @XmlElement(name = "total_organismo")
    protected int totalOrganismo;

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
     * Gets the value of the notificacionesRealizadas property.
     * 
     */
    public int getNotificacionesRealizadas() {
        return notificacionesRealizadas;
    }

    /**
     * Sets the value of the notificacionesRealizadas property.
     * 
     */
    public void setNotificacionesRealizadas(int value) {
        this.notificacionesRealizadas = value;
    }

    /**
     * Gets the value of the comunicacionesRealizadas property.
     * 
     */
    public int getComunicacionesRealizadas() {
        return comunicacionesRealizadas;
    }

    /**
     * Sets the value of the comunicacionesRealizadas property.
     * 
     */
    public void setComunicacionesRealizadas(int value) {
        this.comunicacionesRealizadas = value;
    }

    /**
     * Gets the value of the notificacionesPendientes property.
     * 
     */
    public int getNotificacionesPendientes() {
        return notificacionesPendientes;
    }

    /**
     * Sets the value of the notificacionesPendientes property.
     * 
     */
    public void setNotificacionesPendientes(int value) {
        this.notificacionesPendientes = value;
    }

    /**
     * Gets the value of the comunicacionesPendientes property.
     * 
     */
    public int getComunicacionesPendientes() {
        return comunicacionesPendientes;
    }

    /**
     * Sets the value of the comunicacionesPendientes property.
     * 
     */
    public void setComunicacionesPendientes(int value) {
        this.comunicacionesPendientes = value;
    }

    /**
     * Gets the value of the totalOrganismo property.
     * 
     */
    public int getTotalOrganismo() {
        return totalOrganismo;
    }

    /**
     * Sets the value of the totalOrganismo property.
     * 
     */
    public void setTotalOrganismo(int value) {
        this.totalOrganismo = value;
    }

}

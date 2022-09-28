
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PorEstado2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PorEstado2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="notificaciones_realizadas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="notificaciones_pendientes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="comunicaciones_realizadas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="comunicaciones_pendientes" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "PorEstado2", propOrder = {

})
public class PorEstado2 {

    @XmlElement(name = "notificaciones_realizadas")
    protected int notificacionesRealizadas;
    @XmlElement(name = "notificaciones_pendientes")
    protected int notificacionesPendientes;
    @XmlElement(name = "comunicaciones_realizadas")
    protected int comunicacionesRealizadas;
    @XmlElement(name = "comunicaciones_pendientes")
    protected int comunicacionesPendientes;
    protected int totales;

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

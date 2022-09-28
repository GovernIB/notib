
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PorEstado complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PorEstado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
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
@XmlType(name = "PorEstado", propOrder = {

})
public class PorEstado {

    @XmlElement(name = "estado_notificado")
    protected int estadoNotificado;
    @XmlElement(name = "estado_pendiente")
    protected int estadoPendiente;
    protected int totales;

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


package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entregaDeh complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entregaDeh">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="obligat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="procedimentCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entregaDeh", propOrder = {
    "obligat",
    "procedimentCodi"
})
public class EntregaDeh {

    protected boolean obligat;
    protected String procedimentCodi;

    /**
     * Gets the value of the obligat property.
     * 
     */
    public boolean isObligat() {
        return obligat;
    }

    /**
     * Sets the value of the obligat property.
     * 
     */
    public void setObligat(boolean value) {
        this.obligat = value;
    }

    /**
     * Gets the value of the procedimentCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedimentCodi() {
        return procedimentCodi;
    }

    /**
     * Sets the value of the procedimentCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedimentCodi(String value) {
        this.procedimentCodi = value;
    }

}

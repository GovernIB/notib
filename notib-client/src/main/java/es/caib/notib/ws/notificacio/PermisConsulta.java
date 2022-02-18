
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for permisConsulta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="permisConsulta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codiDir3Entitat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="permisConsulta" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="procedimentCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usuariCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "permisConsulta", propOrder = {
    "codiDir3Entitat",
    "permisConsulta",
    "procedimentCodi",
    "usuariCodi"
})
public class PermisConsulta {

    protected String codiDir3Entitat;
    protected boolean permisConsulta;
    protected String procedimentCodi;
    protected String usuariCodi;

    /**
     * Gets the value of the codiDir3Entitat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiDir3Entitat() {
        return codiDir3Entitat;
    }

    /**
     * Sets the value of the codiDir3Entitat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiDir3Entitat(String value) {
        this.codiDir3Entitat = value;
    }

    /**
     * Gets the value of the permisConsulta property.
     * 
     */
    public boolean isPermisConsulta() {
        return permisConsulta;
    }

    /**
     * Sets the value of the permisConsulta property.
     * 
     */
    public void setPermisConsulta(boolean value) {
        this.permisConsulta = value;
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

    /**
     * Gets the value of the usuariCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuariCodi() {
        return usuariCodi;
    }

    /**
     * Sets the value of the usuariCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuariCodi(String value) {
        this.usuariCodi = value;
    }

}


package es.caib.notib.ws.notificacio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enviament complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enviament">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="destinataris" type="{http://www.caib.es/notib/ws/notificacio}persona" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="entregaDeh" type="{http://www.caib.es/notib/ws/notificacio}entregaDeh" minOccurs="0"/>
 *         &lt;element name="entregaPostal" type="{http://www.caib.es/notib/ws/notificacio}entregaPostal" minOccurs="0"/>
 *         &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serveiTipus" type="{http://www.caib.es/notib/ws/notificacio}serveiTipusEnum" minOccurs="0"/>
 *         &lt;element name="titular" type="{http://www.caib.es/notib/ws/notificacio}persona" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enviament", propOrder = {
    "destinataris",
    "entregaDeh",
    "entregaPostal",
    "referencia",
    "serveiTipus",
    "titular"
})
public class Enviament {

    @XmlElement(nillable = true)
    protected List<Persona> destinataris;
    protected EntregaDeh entregaDeh;
    protected EntregaPostal entregaPostal;
    protected String referencia;
    protected ServeiTipusEnum serveiTipus;
    protected Persona titular;

    /**
     * Gets the value of the destinataris property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinataris property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinataris().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Persona }
     * 
     * 
     */
    public List<Persona> getDestinataris() {
        if (destinataris == null) {
            destinataris = new ArrayList<Persona>();
        }
        return this.destinataris;
    }

    /**
     * Gets the value of the entregaDeh property.
     * 
     * @return
     *     possible object is
     *     {@link EntregaDeh }
     *     
     */
    public EntregaDeh getEntregaDeh() {
        return entregaDeh;
    }

    /**
     * Sets the value of the entregaDeh property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntregaDeh }
     *     
     */
    public void setEntregaDeh(EntregaDeh value) {
        this.entregaDeh = value;
    }

    /**
     * Gets the value of the entregaPostal property.
     * 
     * @return
     *     possible object is
     *     {@link EntregaPostal }
     *     
     */
    public EntregaPostal getEntregaPostal() {
        return entregaPostal;
    }

    /**
     * Sets the value of the entregaPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntregaPostal }
     *     
     */
    public void setEntregaPostal(EntregaPostal value) {
        this.entregaPostal = value;
    }

    /**
     * Gets the value of the referencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Sets the value of the referencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencia(String value) {
        this.referencia = value;
    }

    /**
     * Gets the value of the serveiTipus property.
     * 
     * @return
     *     possible object is
     *     {@link ServeiTipusEnum }
     *     
     */
    public ServeiTipusEnum getServeiTipus() {
        return serveiTipus;
    }

    /**
     * Sets the value of the serveiTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServeiTipusEnum }
     *     
     */
    public void setServeiTipus(ServeiTipusEnum value) {
        this.serveiTipus = value;
    }

    /**
     * Gets the value of the titular property.
     * 
     * @return
     *     possible object is
     *     {@link Persona }
     *     
     */
    public Persona getTitular() {
        return titular;
    }

    /**
     * Sets the value of the titular property.
     * 
     * @param value
     *     allowed object is
     *     {@link Persona }
     *     
     */
    public void setTitular(Persona value) {
        this.titular = value;
    }

}

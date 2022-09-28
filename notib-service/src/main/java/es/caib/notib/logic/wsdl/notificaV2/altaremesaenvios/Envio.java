
package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Destinatarios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.EntregaDEH;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.EntregaPostal;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Persona;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Envio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Envio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="referenciaEmisor" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="titular" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Persona"/>
 *         &lt;element name="destinatarios" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Destinatarios" minOccurs="0"/>
 *         &lt;element name="entregaPostal" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}EntregaPostal" minOccurs="0"/>
 *         &lt;element name="entregaDEH" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}EntregaDEH" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Envio", propOrder = {

})
public class Envio {

    protected String referenciaEmisor;
    @XmlElement(required = true)
    protected Persona titular;
    protected Destinatarios destinatarios;
    protected EntregaPostal entregaPostal;
    protected EntregaDEH entregaDEH;

    /**
     * Gets the value of the referenciaEmisor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenciaEmisor() {
        return referenciaEmisor;
    }

    /**
     * Sets the value of the referenciaEmisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenciaEmisor(String value) {
        this.referenciaEmisor = value;
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

    /**
     * Gets the value of the destinatarios property.
     * 
     * @return
     *     possible object is
     *     {@link Destinatarios }
     *     
     */
    public Destinatarios getDestinatarios() {
        return destinatarios;
    }

    /**
     * Sets the value of the destinatarios property.
     * 
     * @param value
     *     allowed object is
     *     {@link Destinatarios }
     *     
     */
    public void setDestinatarios(Destinatarios value) {
        this.destinatarios = value;
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
     * Gets the value of the entregaDEH property.
     * 
     * @return
     *     possible object is
     *     {@link EntregaDEH }
     *     
     */
    public EntregaDEH getEntregaDEH() {
        return entregaDEH;
    }

    /**
     * Sets the value of the entregaDEH property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntregaDEH }
     *     
     */
    public void setEntregaDEH(EntregaDEH value) {
        this.entregaDEH = value;
    }

}

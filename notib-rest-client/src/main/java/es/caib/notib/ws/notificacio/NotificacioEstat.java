
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para notificacioEstat complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notificacioEstat">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}notificacioEstatEnum" minOccurs="0"/>
 *         &lt;element name="numSeguiment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="origen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptorNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptorNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificacioEstat", propOrder = {
    "data",
    "estat",
    "numSeguiment",
    "origen",
    "receptorNif",
    "receptorNom"
})
public class NotificacioEstat {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar data;
    @XmlSchemaType(name = "string")
    protected NotificacioEstatEnum estat;
    protected String numSeguiment;
    protected String origen;
    protected String receptorNif;
    protected String receptorNom;

    /**
     * Obtiene el valor de la propiedad data.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getData() {
        return data;
    }

    /**
     * Define el valor de la propiedad data.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setData(XMLGregorianCalendar value) {
        this.data = value;
    }

    /**
     * Obtiene el valor de la propiedad estat.
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
     * Define el valor de la propiedad estat.
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
     * Obtiene el valor de la propiedad numSeguiment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumSeguiment() {
        return numSeguiment;
    }

    /**
     * Define el valor de la propiedad numSeguiment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumSeguiment(String value) {
        this.numSeguiment = value;
    }

    /**
     * Obtiene el valor de la propiedad origen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Define el valor de la propiedad origen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigen(String value) {
        this.origen = value;
    }

    /**
     * Obtiene el valor de la propiedad receptorNif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceptorNif() {
        return receptorNif;
    }

    /**
     * Define el valor de la propiedad receptorNif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceptorNif(String value) {
        this.receptorNif = value;
    }

    /**
     * Obtiene el valor de la propiedad receptorNom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceptorNom() {
        return receptorNom;
    }

    /**
     * Define el valor de la propiedad receptorNom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceptorNom(String value) {
        this.receptorNom = value;
    }

}

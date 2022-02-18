
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for respostaConsultaEstatEnviament complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaEstatEnviament">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="certificacio" type="{http://www.caib.es/notib/ws/notificacio}certificacio" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}enviamentEstatEnum" minOccurs="0"/>
 *         &lt;element name="estatData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="estatDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estatOrigen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptorNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="receptorNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respostaConsultaEstatEnviament", propOrder = {
    "certificacio",
    "estat",
    "estatData",
    "estatDescripcio",
    "estatOrigen",
    "receptorNif",
    "receptorNom"
})
public class RespostaConsultaEstatEnviament
    extends RespostaBase
{

    protected Certificacio certificacio;
    protected EnviamentEstatEnum estat;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar estatData;
    protected String estatDescripcio;
    protected String estatOrigen;
    protected String receptorNif;
    protected String receptorNom;

    /**
     * Gets the value of the certificacio property.
     * 
     * @return
     *     possible object is
     *     {@link Certificacio }
     *     
     */
    public Certificacio getCertificacio() {
        return certificacio;
    }

    /**
     * Sets the value of the certificacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificacio }
     *     
     */
    public void setCertificacio(Certificacio value) {
        this.certificacio = value;
    }

    /**
     * Gets the value of the estat property.
     * 
     * @return
     *     possible object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public EnviamentEstatEnum getEstat() {
        return estat;
    }

    /**
     * Sets the value of the estat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public void setEstat(EnviamentEstatEnum value) {
        this.estat = value;
    }

    /**
     * Gets the value of the estatData property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEstatData() {
        return estatData;
    }

    /**
     * Sets the value of the estatData property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEstatData(XMLGregorianCalendar value) {
        this.estatData = value;
    }

    /**
     * Gets the value of the estatDescripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstatDescripcio() {
        return estatDescripcio;
    }

    /**
     * Sets the value of the estatDescripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstatDescripcio(String value) {
        this.estatDescripcio = value;
    }

    /**
     * Gets the value of the estatOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstatOrigen() {
        return estatOrigen;
    }

    /**
     * Sets the value of the estatOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstatOrigen(String value) {
        this.estatOrigen = value;
    }

    /**
     * Gets the value of the receptorNif property.
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
     * Sets the value of the receptorNif property.
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
     * Gets the value of the receptorNom property.
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
     * Sets the value of the receptorNom property.
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

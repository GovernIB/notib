
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for respostaConsultaDadesRegistre complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaDadesRegistre">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="dataRegistre" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="justificant" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="numRegistre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numRegistreFormatat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "respostaConsultaDadesRegistre", propOrder = {
    "dataRegistre",
    "justificant",
    "numRegistre",
    "numRegistreFormatat"
})
public class RespostaConsultaDadesRegistre
    extends RespostaBase
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataRegistre;
    protected byte[] justificant;
    protected int numRegistre;
    protected String numRegistreFormatat;

    /**
     * Gets the value of the dataRegistre property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRegistre() {
        return dataRegistre;
    }

    /**
     * Sets the value of the dataRegistre property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRegistre(XMLGregorianCalendar value) {
        this.dataRegistre = value;
    }

    /**
     * Gets the value of the justificant property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getJustificant() {
        return justificant;
    }

    /**
     * Sets the value of the justificant property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setJustificant(byte[] value) {
        this.justificant = value;
    }

    /**
     * Gets the value of the numRegistre property.
     * 
     */
    public int getNumRegistre() {
        return numRegistre;
    }

    /**
     * Sets the value of the numRegistre property.
     * 
     */
    public void setNumRegistre(int value) {
        this.numRegistre = value;
    }

    /**
     * Gets the value of the numRegistreFormatat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumRegistreFormatat() {
        return numRegistreFormatat;
    }

    /**
     * Sets the value of the numRegistreFormatat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumRegistreFormatat(String value) {
        this.numRegistreFormatat = value;
    }

}

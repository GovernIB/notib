
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for respostaConsultaDadesRegistreV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaDadesRegistreV2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="dataRecepcioSir" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataRegistre" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataRegistreDestiSir" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="enviamentSir" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="justificant" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="llibre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numRegistre" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numRegistreFormatat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "respostaConsultaDadesRegistreV2", propOrder = {
    "dataRecepcioSir",
    "dataRegistre",
    "dataRegistreDestiSir",
    "enviamentSir",
    "justificant",
    "llibre",
    "numRegistre",
    "numRegistreFormatat",
    "oficina"
})
public class RespostaConsultaDadesRegistreV2
    extends RespostaBase
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataRecepcioSir;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataRegistre;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataRegistreDestiSir;
    protected boolean enviamentSir;
    protected byte[] justificant;
    protected String llibre;
    protected int numRegistre;
    protected String numRegistreFormatat;
    protected String oficina;

    /**
     * Gets the value of the dataRecepcioSir property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRecepcioSir() {
        return dataRecepcioSir;
    }

    /**
     * Sets the value of the dataRecepcioSir property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRecepcioSir(XMLGregorianCalendar value) {
        this.dataRecepcioSir = value;
    }

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
     * Gets the value of the dataRegistreDestiSir property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRegistreDestiSir() {
        return dataRegistreDestiSir;
    }

    /**
     * Sets the value of the dataRegistreDestiSir property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRegistreDestiSir(XMLGregorianCalendar value) {
        this.dataRegistreDestiSir = value;
    }

    /**
     * Gets the value of the enviamentSir property.
     * 
     */
    public boolean isEnviamentSir() {
        return enviamentSir;
    }

    /**
     * Sets the value of the enviamentSir property.
     * 
     */
    public void setEnviamentSir(boolean value) {
        this.enviamentSir = value;
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
     * Gets the value of the llibre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLlibre() {
        return llibre;
    }

    /**
     * Sets the value of the llibre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLlibre(String value) {
        this.llibre = value;
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

    /**
     * Gets the value of the oficina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOficina() {
        return oficina;
    }

    /**
     * Sets the value of the oficina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOficina(String value) {
        this.oficina = value;
    }

}

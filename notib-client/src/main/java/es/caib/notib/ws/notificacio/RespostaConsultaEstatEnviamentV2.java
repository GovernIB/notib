
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for respostaConsultaEstatEnviamentV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaEstatEnviamentV2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="adressaPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="certificacio" type="{http://www.caib.es/notib/ws/notificacio}certificacio" minOccurs="0"/>
 *         &lt;element name="datat" type="{http://www.caib.es/notib/ws/notificacio}datat" minOccurs="0"/>
 *         &lt;element name="dehNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dehObligat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="entragaPostalActiva" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enviamentSir" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}enviamentEstatEnum" minOccurs="0"/>
 *         &lt;element name="estatData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="estatDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interessat" type="{http://www.caib.es/notib/ws/notificacio}persona" minOccurs="0"/>
 *         &lt;element name="notificaIndentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registre" type="{http://www.caib.es/notib/ws/notificacio}registre" minOccurs="0"/>
 *         &lt;element name="representants" type="{http://www.caib.es/notib/ws/notificacio}persona" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sir" type="{http://www.caib.es/notib/ws/notificacio}sir" minOccurs="0"/>
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
@XmlType(name = "respostaConsultaEstatEnviamentV2", propOrder = {
    "adressaPostal",
    "certificacio",
    "datat",
    "dehNif",
    "dehObligat",
    "entragaPostalActiva",
    "enviamentSir",
    "estat",
    "estatData",
    "estatDescripcio",
    "interessat",
    "notificaIndentificador",
    "referencia",
    "identificador",
    "registre",
    "representants",
    "sir"
})
public class RespostaConsultaEstatEnviamentV2
    extends RespostaBase
{

    protected String adressaPostal;
    protected Certificacio certificacio;
    protected Datat datat;
    protected String dehNif;
    protected boolean dehObligat;
    protected boolean entragaPostalActiva;
    protected boolean enviamentSir;
    protected EnviamentEstatEnum estat;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar estatData;
    protected String estatDescripcio;
    protected Persona interessat;
    protected String notificaIndentificador;
    protected String referencia;
    protected String identificador;
    protected Registre registre;
    @XmlElement(nillable = true)
    protected List<Persona> representants;
    protected Sir sir;

    /**
     * Gets the value of the adressaPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdressaPostal() {
        return adressaPostal;
    }

    /**
     * Sets the value of the adressaPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdressaPostal(String value) {
        this.adressaPostal = value;
    }

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
     * Gets the value of the datat property.
     * 
     * @return
     *     possible object is
     *     {@link Datat }
     *     
     */
    public Datat getDatat() {
        return datat;
    }

    /**
     * Sets the value of the datat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Datat }
     *     
     */
    public void setDatat(Datat value) {
        this.datat = value;
    }

    /**
     * Gets the value of the dehNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDehNif() {
        return dehNif;
    }

    /**
     * Sets the value of the dehNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDehNif(String value) {
        this.dehNif = value;
    }

    /**
     * Gets the value of the dehObligat property.
     * 
     */
    public boolean isDehObligat() {
        return dehObligat;
    }

    /**
     * Sets the value of the dehObligat property.
     * 
     */
    public void setDehObligat(boolean value) {
        this.dehObligat = value;
    }

    /**
     * Gets the value of the entragaPostalActiva property.
     * 
     */
    public boolean isEntragaPostalActiva() {
        return entragaPostalActiva;
    }

    /**
     * Sets the value of the entragaPostalActiva property.
     * 
     */
    public void setEntragaPostalActiva(boolean value) {
        this.entragaPostalActiva = value;
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
     * Gets the value of the interessat property.
     * 
     * @return
     *     possible object is
     *     {@link Persona }
     *     
     */
    public Persona getInteressat() {
        return interessat;
    }

    /**
     * Sets the value of the interessat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Persona }
     *     
     */
    public void setInteressat(Persona value) {
        this.interessat = value;
    }

    /**
     * Gets the value of the notificaIndentificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificaIndentificador() {
        return notificaIndentificador;
    }

    /**
     * Sets the value of the notificaIndentificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificaIndentificador(String value) {
        this.notificaIndentificador = value;
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
     * Gets the value of the identificador property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

    /**
     * Gets the value of the registre property.
     * 
     * @return
     *     possible object is
     *     {@link Registre }
     *     
     */
    public Registre getRegistre() {
        return registre;
    }

    /**
     * Sets the value of the registre property.
     * 
     * @param value
     *     allowed object is
     *     {@link Registre }
     *     
     */
    public void setRegistre(Registre value) {
        this.registre = value;
    }

    /**
     * Gets the value of the representants property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the representants property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRepresentants().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Persona }
     * 
     * 
     */
    public List<Persona> getRepresentants() {
        if (representants == null) {
            representants = new ArrayList<Persona>();
        }
        return this.representants;
    }

    /**
     * Gets the value of the sir property.
     * 
     * @return
     *     possible object is
     *     {@link Sir }
     *     
     */
    public Sir getSir() {
        return sir;
    }

    /**
     * Sets the value of the sir property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sir }
     *     
     */
    public void setSir(Sir value) {
        this.sir = value;
    }

}

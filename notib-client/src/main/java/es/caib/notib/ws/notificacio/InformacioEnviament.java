
package es.caib.notib.ws.notificacio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for informacioEnviament complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="informacioEnviament">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="certificacio" type="{http://www.caib.es/notib/ws/notificacio}certificacio" minOccurs="0"/>
 *         &lt;element name="concepte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataCaducitat" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataCreacio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataPostaDisposicio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destiDir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destiDir3Descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinataris" type="{http://www.caib.es/notib/ws/notificacio}persona" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="emisorArrelDir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emisorArrelDir3Descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emisorDir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emisorDir3Descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="entregaDeh" type="{http://www.caib.es/notib/ws/notificacio}entregaDeh" minOccurs="0"/>
 *         &lt;element name="entregaPostal" type="{http://www.caib.es/notib/ws/notificacio}entregaPostal" minOccurs="0"/>
 *         &lt;element name="enviamentTipus" type="{http://www.caib.es/notib/ws/notificacio}enviamentTipusEnum" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}enviamentEstatEnum" minOccurs="0"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificaError" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="notificaErrorData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="notificaErrorDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificaEstat" type="{http://www.caib.es/notib/ws/notificacio}enviamentEstatEnum" minOccurs="0"/>
 *         &lt;element name="procedimentCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="procedimentDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retard" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="seuError" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="seuErrorData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="seuErrorDescripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seuEstat" type="{http://www.caib.es/notib/ws/notificacio}enviamentEstatEnum" minOccurs="0"/>
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
@XmlType(name = "informacioEnviament", propOrder = {
    "certificacio",
    "concepte",
    "dataCaducitat",
    "dataCreacio",
    "dataPostaDisposicio",
    "descripcio",
    "destiDir3Codi",
    "destiDir3Descripcio",
    "destinataris",
    "emisorArrelDir3Codi",
    "emisorArrelDir3Descripcio",
    "emisorDir3Codi",
    "emisorDir3Descripcio",
    "entregaDeh",
    "entregaPostal",
    "enviamentTipus",
    "estat",
    "identificador",
    "notificaError",
    "notificaErrorData",
    "notificaErrorDescripcio",
    "notificaEstat",
    "procedimentCodi",
    "procedimentDescripcio",
    "referencia",
    "retard",
    "seuError",
    "seuErrorData",
    "seuErrorDescripcio",
    "seuEstat",
    "titular"
})
public class InformacioEnviament {

    protected Certificacio certificacio;
    protected String concepte;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataCaducitat;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataCreacio;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataPostaDisposicio;
    protected String descripcio;
    protected String destiDir3Codi;
    protected String destiDir3Descripcio;
    @XmlElement(nillable = true)
    protected List<Persona> destinataris;
    protected String emisorArrelDir3Codi;
    protected String emisorArrelDir3Descripcio;
    protected String emisorDir3Codi;
    protected String emisorDir3Descripcio;
    protected EntregaDeh entregaDeh;
    protected EntregaPostal entregaPostal;
    protected EnviamentTipusEnum enviamentTipus;
    protected EnviamentEstatEnum estat;
    protected String identificador;
    protected boolean notificaError;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar notificaErrorData;
    protected String notificaErrorDescripcio;
    protected EnviamentEstatEnum notificaEstat;
    protected String procedimentCodi;
    protected String procedimentDescripcio;
    protected String referencia;
    protected Integer retard;
    protected boolean seuError;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar seuErrorData;
    protected String seuErrorDescripcio;
    protected EnviamentEstatEnum seuEstat;
    protected Persona titular;

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
     * Gets the value of the concepte property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConcepte() {
        return concepte;
    }

    /**
     * Sets the value of the concepte property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConcepte(String value) {
        this.concepte = value;
    }

    /**
     * Gets the value of the dataCaducitat property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataCaducitat() {
        return dataCaducitat;
    }

    /**
     * Sets the value of the dataCaducitat property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataCaducitat(XMLGregorianCalendar value) {
        this.dataCaducitat = value;
    }

    /**
     * Gets the value of the dataCreacio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataCreacio() {
        return dataCreacio;
    }

    /**
     * Sets the value of the dataCreacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataCreacio(XMLGregorianCalendar value) {
        this.dataCreacio = value;
    }

    /**
     * Gets the value of the dataPostaDisposicio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataPostaDisposicio() {
        return dataPostaDisposicio;
    }

    /**
     * Sets the value of the dataPostaDisposicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataPostaDisposicio(XMLGregorianCalendar value) {
        this.dataPostaDisposicio = value;
    }

    /**
     * Gets the value of the descripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcio() {
        return descripcio;
    }

    /**
     * Sets the value of the descripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcio(String value) {
        this.descripcio = value;
    }

    /**
     * Gets the value of the destiDir3Codi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestiDir3Codi() {
        return destiDir3Codi;
    }

    /**
     * Sets the value of the destiDir3Codi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestiDir3Codi(String value) {
        this.destiDir3Codi = value;
    }

    /**
     * Gets the value of the destiDir3Descripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestiDir3Descripcio() {
        return destiDir3Descripcio;
    }

    /**
     * Sets the value of the destiDir3Descripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestiDir3Descripcio(String value) {
        this.destiDir3Descripcio = value;
    }

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
     * Gets the value of the emisorArrelDir3Codi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmisorArrelDir3Codi() {
        return emisorArrelDir3Codi;
    }

    /**
     * Sets the value of the emisorArrelDir3Codi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmisorArrelDir3Codi(String value) {
        this.emisorArrelDir3Codi = value;
    }

    /**
     * Gets the value of the emisorArrelDir3Descripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmisorArrelDir3Descripcio() {
        return emisorArrelDir3Descripcio;
    }

    /**
     * Sets the value of the emisorArrelDir3Descripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmisorArrelDir3Descripcio(String value) {
        this.emisorArrelDir3Descripcio = value;
    }

    /**
     * Gets the value of the emisorDir3Codi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmisorDir3Codi() {
        return emisorDir3Codi;
    }

    /**
     * Sets the value of the emisorDir3Codi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmisorDir3Codi(String value) {
        this.emisorDir3Codi = value;
    }

    /**
     * Gets the value of the emisorDir3Descripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmisorDir3Descripcio() {
        return emisorDir3Descripcio;
    }

    /**
     * Sets the value of the emisorDir3Descripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmisorDir3Descripcio(String value) {
        this.emisorDir3Descripcio = value;
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
     * Gets the value of the enviamentTipus property.
     * 
     * @return
     *     possible object is
     *     {@link EnviamentTipusEnum }
     *     
     */
    public EnviamentTipusEnum getEnviamentTipus() {
        return enviamentTipus;
    }

    /**
     * Sets the value of the enviamentTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnviamentTipusEnum }
     *     
     */
    public void setEnviamentTipus(EnviamentTipusEnum value) {
        this.enviamentTipus = value;
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
     * Gets the value of the notificaError property.
     * 
     */
    public boolean isNotificaError() {
        return notificaError;
    }

    /**
     * Sets the value of the notificaError property.
     * 
     */
    public void setNotificaError(boolean value) {
        this.notificaError = value;
    }

    /**
     * Gets the value of the notificaErrorData property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNotificaErrorData() {
        return notificaErrorData;
    }

    /**
     * Sets the value of the notificaErrorData property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNotificaErrorData(XMLGregorianCalendar value) {
        this.notificaErrorData = value;
    }

    /**
     * Gets the value of the notificaErrorDescripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificaErrorDescripcio() {
        return notificaErrorDescripcio;
    }

    /**
     * Sets the value of the notificaErrorDescripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificaErrorDescripcio(String value) {
        this.notificaErrorDescripcio = value;
    }

    /**
     * Gets the value of the notificaEstat property.
     * 
     * @return
     *     possible object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public EnviamentEstatEnum getNotificaEstat() {
        return notificaEstat;
    }

    /**
     * Sets the value of the notificaEstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public void setNotificaEstat(EnviamentEstatEnum value) {
        this.notificaEstat = value;
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
     * Gets the value of the procedimentDescripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedimentDescripcio() {
        return procedimentDescripcio;
    }

    /**
     * Sets the value of the procedimentDescripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedimentDescripcio(String value) {
        this.procedimentDescripcio = value;
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
     * Gets the value of the retard property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRetard() {
        return retard;
    }

    /**
     * Sets the value of the retard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRetard(Integer value) {
        this.retard = value;
    }

    /**
     * Gets the value of the seuError property.
     * 
     */
    public boolean isSeuError() {
        return seuError;
    }

    /**
     * Sets the value of the seuError property.
     * 
     */
    public void setSeuError(boolean value) {
        this.seuError = value;
    }

    /**
     * Gets the value of the seuErrorData property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSeuErrorData() {
        return seuErrorData;
    }

    /**
     * Sets the value of the seuErrorData property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSeuErrorData(XMLGregorianCalendar value) {
        this.seuErrorData = value;
    }

    /**
     * Gets the value of the seuErrorDescripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeuErrorDescripcio() {
        return seuErrorDescripcio;
    }

    /**
     * Sets the value of the seuErrorDescripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeuErrorDescripcio(String value) {
        this.seuErrorDescripcio = value;
    }

    /**
     * Gets the value of the seuEstat property.
     * 
     * @return
     *     possible object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public EnviamentEstatEnum getSeuEstat() {
        return seuEstat;
    }

    /**
     * Sets the value of the seuEstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnviamentEstatEnum }
     *     
     */
    public void setSeuEstat(EnviamentEstatEnum value) {
        this.seuEstat = value;
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


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
 * <p>Java class for notificacioV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificacioV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="caducitat" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="caducitatDiesNaturals" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="concepte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="document" type="{http://www.caib.es/notib/ws/notificacio}documentV2" minOccurs="0"/>
 *         &lt;element name="document2" type="{http://www.caib.es/notib/ws/notificacio}documentV2" minOccurs="0"/>
 *         &lt;element name="document3" type="{http://www.caib.es/notib/ws/notificacio}documentV2" minOccurs="0"/>
 *         &lt;element name="document4" type="{http://www.caib.es/notib/ws/notificacio}documentV2" minOccurs="0"/>
 *         &lt;element name="document5" type="{http://www.caib.es/notib/ws/notificacio}documentV2" minOccurs="0"/>
 *         &lt;element name="emisorDir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enviamentDataProgramada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="enviamentTipus" type="{http://www.caib.es/notib/ws/notificacio}enviamentTipusEnum" minOccurs="0"/>
 *         &lt;element name="enviaments" type="{http://www.caib.es/notib/ws/notificacio}enviament" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="grupCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idioma" type="{http://www.caib.es/notib/ws/notificacio}idiomaEnumDto" minOccurs="0"/>
 *         &lt;element name="numExpedient" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organGestor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="procedimentCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retard" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@XmlType(name = "notificacioV2", propOrder = {
    "caducitat",
    "caducitatDiesNaturals",
    "concepte",
    "descripcio",
    "document",
    "document2",
    "document3",
    "document4",
    "document5",
    "emisorDir3Codi",
    "enviamentDataProgramada",
    "enviamentTipus",
    "enviaments",
    "grupCodi",
    "idioma",
    "numExpedient",
    "organGestor",
    "procedimentCodi",
    "retard",
    "usuariCodi"
})
public class NotificacioV2 {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar caducitat;
    protected Integer caducitatDiesNaturals;
    protected String concepte;
    protected String descripcio;
    protected DocumentV2 document;
    protected DocumentV2 document2;
    protected DocumentV2 document3;
    protected DocumentV2 document4;
    protected DocumentV2 document5;
    protected String emisorDir3Codi;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar enviamentDataProgramada;
    protected EnviamentTipusEnum enviamentTipus;
    @XmlElement(nillable = true)
    protected List<Enviament> enviaments;
    protected String grupCodi;
    protected IdiomaEnumDto idioma;
    protected String numExpedient;
    protected String organGestor;
    protected String procedimentCodi;
    protected Integer retard;
    protected String usuariCodi;

    /**
     * Gets the value of the caducitat property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCaducitat() {
        return caducitat;
    }

    /**
     * Sets the value of the caducitat property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCaducitat(XMLGregorianCalendar value) {
        this.caducitat = value;
    }

    /**
     * Gets the value of the caducitatDiesNaturals property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCaducitatDiesNaturals() {
        return caducitatDiesNaturals;
    }

    /**
     * Sets the value of the caducitatDiesNaturals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCaducitatDiesNaturals(Integer value) {
        this.caducitatDiesNaturals = value;
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
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentV2 }
     *     
     */
    public DocumentV2 getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentV2 }
     *     
     */
    public void setDocument(DocumentV2 value) {
        this.document = value;
    }

    /**
     * Gets the value of the document2 property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentV2 }
     *     
     */
    public DocumentV2 getDocument2() {
        return document2;
    }

    /**
     * Sets the value of the document2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentV2 }
     *     
     */
    public void setDocument2(DocumentV2 value) {
        this.document2 = value;
    }

    /**
     * Gets the value of the document3 property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentV2 }
     *     
     */
    public DocumentV2 getDocument3() {
        return document3;
    }

    /**
     * Sets the value of the document3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentV2 }
     *     
     */
    public void setDocument3(DocumentV2 value) {
        this.document3 = value;
    }

    /**
     * Gets the value of the document4 property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentV2 }
     *     
     */
    public DocumentV2 getDocument4() {
        return document4;
    }

    /**
     * Sets the value of the document4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentV2 }
     *     
     */
    public void setDocument4(DocumentV2 value) {
        this.document4 = value;
    }

    /**
     * Gets the value of the document5 property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentV2 }
     *     
     */
    public DocumentV2 getDocument5() {
        return document5;
    }

    /**
     * Sets the value of the document5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentV2 }
     *     
     */
    public void setDocument5(DocumentV2 value) {
        this.document5 = value;
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
     * Gets the value of the enviamentDataProgramada property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEnviamentDataProgramada() {
        return enviamentDataProgramada;
    }

    /**
     * Sets the value of the enviamentDataProgramada property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEnviamentDataProgramada(XMLGregorianCalendar value) {
        this.enviamentDataProgramada = value;
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
     * Gets the value of the enviaments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the enviaments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnviaments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Enviament }
     * 
     * 
     */
    public List<Enviament> getEnviaments() {
        if (enviaments == null) {
            enviaments = new ArrayList<Enviament>();
        }
        return this.enviaments;
    }

    /**
     * Gets the value of the grupCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrupCodi() {
        return grupCodi;
    }

    /**
     * Sets the value of the grupCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrupCodi(String value) {
        this.grupCodi = value;
    }

    /**
     * Gets the value of the idioma property.
     * 
     * @return
     *     possible object is
     *     {@link IdiomaEnumDto }
     *     
     */
    public IdiomaEnumDto getIdioma() {
        return idioma;
    }

    /**
     * Sets the value of the idioma property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdiomaEnumDto }
     *     
     */
    public void setIdioma(IdiomaEnumDto value) {
        this.idioma = value;
    }

    /**
     * Gets the value of the numExpedient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumExpedient() {
        return numExpedient;
    }

    /**
     * Sets the value of the numExpedient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumExpedient(String value) {
        this.numExpedient = value;
    }

    /**
     * Gets the value of the organGestor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganGestor() {
        return organGestor;
    }

    /**
     * Sets the value of the organGestor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganGestor(String value) {
        this.organGestor = value;
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


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
 * <p>Java class for notificacio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificacio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="caducitat" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="concepte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="document" type="{http://www.caib.es/notib/ws/notificacio}document" minOccurs="0"/>
 *         &lt;element name="emisorDir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enviamentDataProgramada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="enviamentTipus" type="{http://www.caib.es/notib/ws/notificacio}enviamentTipusEnum" minOccurs="0"/>
 *         &lt;element name="enviaments" type="{http://www.caib.es/notib/ws/notificacio}enviament" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pagadorCie" type="{http://www.caib.es/notib/ws/notificacio}pagadorCie" minOccurs="0"/>
 *         &lt;element name="pagadorPostal" type="{http://www.caib.es/notib/ws/notificacio}pagadorPostal" minOccurs="0"/>
 *         &lt;element name="parametresSeu" type="{http://www.caib.es/notib/ws/notificacio}parametresSeu" minOccurs="0"/>
 *         &lt;element name="procedimentCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retard" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificacio", propOrder = {
    "caducitat",
    "concepte",
    "descripcio",
    "document",
    "emisorDir3Codi",
    "enviamentDataProgramada",
    "enviamentTipus",
    "enviaments",
    "pagadorCie",
    "pagadorPostal",
    "parametresSeu",
    "procedimentCodi",
    "retard"
})
public class Notificacio {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar caducitat;
    protected String concepte;
    protected String descripcio;
    protected Document document;
    protected String emisorDir3Codi;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar enviamentDataProgramada;
    protected EnviamentTipusEnum enviamentTipus;
    @XmlElement(nillable = true)
    protected List<Enviament> enviaments;
    protected PagadorCie pagadorCie;
    protected PagadorPostal pagadorPostal;
    protected ParametresSeu parametresSeu;
    protected String procedimentCodi;
    protected Integer retard;

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
     *     {@link Document }
     *     
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link Document }
     *     
     */
    public void setDocument(Document value) {
        this.document = value;
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
     * Gets the value of the pagadorCie property.
     * 
     * @return
     *     possible object is
     *     {@link PagadorCie }
     *     
     */
    public PagadorCie getPagadorCie() {
        return pagadorCie;
    }

    /**
     * Sets the value of the pagadorCie property.
     * 
     * @param value
     *     allowed object is
     *     {@link PagadorCie }
     *     
     */
    public void setPagadorCie(PagadorCie value) {
        this.pagadorCie = value;
    }

    /**
     * Gets the value of the pagadorPostal property.
     * 
     * @return
     *     possible object is
     *     {@link PagadorPostal }
     *     
     */
    public PagadorPostal getPagadorPostal() {
        return pagadorPostal;
    }

    /**
     * Sets the value of the pagadorPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link PagadorPostal }
     *     
     */
    public void setPagadorPostal(PagadorPostal value) {
        this.pagadorPostal = value;
    }

    /**
     * Gets the value of the parametresSeu property.
     * 
     * @return
     *     possible object is
     *     {@link ParametresSeu }
     *     
     */
    public ParametresSeu getParametresSeu() {
        return parametresSeu;
    }

    /**
     * Sets the value of the parametresSeu property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParametresSeu }
     *     
     */
    public void setParametresSeu(ParametresSeu value) {
        this.parametresSeu = value;
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

}

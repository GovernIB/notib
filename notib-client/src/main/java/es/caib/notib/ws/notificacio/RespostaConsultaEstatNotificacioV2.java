
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for respostaConsultaEstatNotificacioV2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaEstatNotificacioV2">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="concepte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataCreada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataEnviada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataFinalitzada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataProcessada" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="emisorDir3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estat" type="{http://www.caib.es/notib/ws/notificacio}notificacioEstatEnum" minOccurs="0"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numExpedient" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organGestorDir3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="procediment" type="{http://www.caib.es/notib/ws/notificacio}procediment" minOccurs="0"/>
 *         &lt;element name="tipus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "respostaConsultaEstatNotificacioV2", propOrder = {
    "concepte",
    "dataCreada",
    "dataEnviada",
    "dataFinalitzada",
    "dataProcessada",
    "emisorDir3",
    "estat",
    "identificador",
    "numExpedient",
    "organGestorDir3",
    "procediment",
    "tipus"
})
public class RespostaConsultaEstatNotificacioV2
    extends RespostaBase
{

    protected String concepte;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataCreada;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataEnviada;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataFinalitzada;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataProcessada;
    protected String emisorDir3;
    protected NotificacioEstatEnum estat;
    protected String identificador;
    protected String numExpedient;
    protected String organGestorDir3;
    protected Procediment procediment;
    protected String tipus;

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
     * Gets the value of the dataCreada property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataCreada() {
        return dataCreada;
    }

    /**
     * Sets the value of the dataCreada property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataCreada(XMLGregorianCalendar value) {
        this.dataCreada = value;
    }

    /**
     * Gets the value of the dataEnviada property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataEnviada() {
        return dataEnviada;
    }

    /**
     * Sets the value of the dataEnviada property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataEnviada(XMLGregorianCalendar value) {
        this.dataEnviada = value;
    }

    /**
     * Gets the value of the dataFinalitzada property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFinalitzada() {
        return dataFinalitzada;
    }

    /**
     * Sets the value of the dataFinalitzada property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFinalitzada(XMLGregorianCalendar value) {
        this.dataFinalitzada = value;
    }

    /**
     * Gets the value of the dataProcessada property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataProcessada() {
        return dataProcessada;
    }

    /**
     * Sets the value of the dataProcessada property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataProcessada(XMLGregorianCalendar value) {
        this.dataProcessada = value;
    }

    /**
     * Gets the value of the emisorDir3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmisorDir3() {
        return emisorDir3;
    }

    /**
     * Sets the value of the emisorDir3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmisorDir3(String value) {
        this.emisorDir3 = value;
    }

    /**
     * Gets the value of the estat property.
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
     * Sets the value of the estat property.
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
     * Gets the value of the organGestorDir3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganGestorDir3() {
        return organGestorDir3;
    }

    /**
     * Sets the value of the organGestorDir3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganGestorDir3(String value) {
        this.organGestorDir3 = value;
    }

    /**
     * Gets the value of the procediment property.
     * 
     * @return
     *     possible object is
     *     {@link Procediment }
     *     
     */
    public Procediment getProcediment() {
        return procediment;
    }

    /**
     * Sets the value of the procediment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Procediment }
     *     
     */
    public void setProcediment(Procediment value) {
        this.procediment = value;
    }

    /**
     * Gets the value of the tipus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipus() {
        return tipus;
    }

    /**
     * Sets the value of the tipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipus(String value) {
        this.tipus = value;
    }

}


package es.caib.notib.logic.wsdl.adviser;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Receptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Receptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nifReceptor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreReceptor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vinculoReceptor" type="{http://administracionelectronica.gob.es/notifica/ws/adviserwsv2/1.0/sincronizarEnvio}TipoVinculo"/>
 *         &lt;element name="nifRepresentante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreRepresentante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="csvRepresetante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Receptor", propOrder = {
    "nifReceptor",
    "nombreReceptor",
    "vinculoReceptor",
    "nifRepresentante",
    "nombreRepresentante",
    "csvRepresetante"
})
public class Receptor {

    @XmlElement(required = true)
    protected String nifReceptor;
    @XmlElement(required = true)
    protected String nombreReceptor;
    @XmlElement(required = true)
    protected BigInteger vinculoReceptor;
    protected String nifRepresentante;
    protected String nombreRepresentante;
    protected String csvRepresetante;

    /**
     * Gets the value of the nifReceptor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNifReceptor() {
        return nifReceptor;
    }

    /**
     * Sets the value of the nifReceptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNifReceptor(String value) {
        this.nifReceptor = value;
    }

    /**
     * Gets the value of the nombreReceptor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreReceptor() {
        return nombreReceptor;
    }

    /**
     * Sets the value of the nombreReceptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreReceptor(String value) {
        this.nombreReceptor = value;
    }

    /**
     * Gets the value of the vinculoReceptor property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVinculoReceptor() {
        return vinculoReceptor;
    }

    /**
     * Sets the value of the vinculoReceptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVinculoReceptor(BigInteger value) {
        this.vinculoReceptor = value;
    }

    /**
     * Gets the value of the nifRepresentante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNifRepresentante() {
        return nifRepresentante;
    }

    /**
     * Sets the value of the nifRepresentante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNifRepresentante(String value) {
        this.nifRepresentante = value;
    }

    /**
     * Gets the value of the nombreRepresentante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreRepresentante() {
        return nombreRepresentante;
    }

    /**
     * Sets the value of the nombreRepresentante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreRepresentante(String value) {
        this.nombreRepresentante = value;
    }

    /**
     * Gets the value of the csvRepresetante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsvRepresetante() {
        return csvRepresetante;
    }

    /**
     * Sets the value of the csvRepresetante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsvRepresetante(String value) {
        this.csvRepresetante = value;
    }

}


package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="contenido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hash_sha1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="normalizado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="generar_csv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documento", propOrder = {

})
public class Documento {

    @XmlElement(required = true)
    protected String contenido;
    @XmlElement(name = "hash_sha1", required = true)
    protected String hashSha1;
    @XmlElement(required = true)
    protected String normalizado;
    @XmlElement(name = "generar_csv", required = true)
    protected String generarCsv;

    /**
     * Gets the value of the contenido property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Sets the value of the contenido property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContenido(String value) {
        this.contenido = value;
    }

    /**
     * Gets the value of the hashSha1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHashSha1() {
        return hashSha1;
    }

    /**
     * Sets the value of the hashSha1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHashSha1(String value) {
        this.hashSha1 = value;
    }

    /**
     * Gets the value of the normalizado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNormalizado() {
        return normalizado;
    }

    /**
     * Sets the value of the normalizado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNormalizado(String value) {
        this.normalizado = value;
    }

    /**
     * Gets the value of the generarCsv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerarCsv() {
        return generarCsv;
    }

    /**
     * Sets the value of the generarCsv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerarCsv(String value) {
        this.generarCsv = value;
    }

}

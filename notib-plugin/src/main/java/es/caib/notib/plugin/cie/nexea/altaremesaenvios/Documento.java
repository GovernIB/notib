
package es.caib.notib.plugin.cie.nexea.altaremesaenvios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Documento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Documento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="contenido" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="enlaceDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="metadatos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="opcionesDocumento" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Opciones" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Documento", propOrder = {

})
public class Documento {

    protected byte[] contenido;
    @XmlElement(required = true)
    protected String hash;
    protected String enlaceDocumento;
    protected String metadatos;
    protected Opciones opcionesDocumento;

    /**
     * Gets the value of the contenido property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContenido() {
        return contenido;
    }

    /**
     * Sets the value of the contenido property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContenido(byte[] value) {
        this.contenido = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the enlaceDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnlaceDocumento() {
        return enlaceDocumento;
    }

    /**
     * Sets the value of the enlaceDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnlaceDocumento(String value) {
        this.enlaceDocumento = value;
    }

    /**
     * Gets the value of the metadatos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetadatos() {
        return metadatos;
    }

    /**
     * Sets the value of the metadatos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetadatos(String value) {
        this.metadatos = value;
    }

    /**
     * Gets the value of the opcionesDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link Opciones }
     *     
     */
    public Opciones getOpcionesDocumento() {
        return opcionesDocumento;
    }

    /**
     * Sets the value of the opcionesDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link Opciones }
     *     
     */
    public void setOpcionesDocumento(Opciones value) {
        this.opcionesDocumento = value;
    }

}

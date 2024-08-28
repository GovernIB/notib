
package es.caib.notib.plugin.cie.nexea.infoenvioligero;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoinfoEnvioLigero complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoinfoEnvioLigero">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="descripcionRespuesta" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="datados" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero}Datados" minOccurs="0" form="qualified"/>
 *         &lt;element name="certificacion" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioLigero}Certificacion" minOccurs="0" form="qualified"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoinfoEnvioLigero", propOrder = {

})
public class ResultadoinfoEnvioLigero {

    @XmlElement(required = true)
    protected String codigoRespuesta;
    @XmlElement(required = true)
    protected String descripcionRespuesta;
    @XmlElement(required = true)
    protected String identificador;
    @XmlElement(required = true)
    protected String estado;
    protected Datados datados;
    protected Certificacion certificacion;

    /**
     * Gets the value of the codigoRespuesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    /**
     * Sets the value of the codigoRespuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRespuesta(String value) {
        this.codigoRespuesta = value;
    }

    /**
     * Gets the value of the descripcionRespuesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionRespuesta() {
        return descripcionRespuesta;
    }

    /**
     * Sets the value of the descripcionRespuesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionRespuesta(String value) {
        this.descripcionRespuesta = value;
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
     * Gets the value of the estado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Sets the value of the estado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Gets the value of the datados property.
     * 
     * @return
     *     possible object is
     *     {@link Datados }
     *     
     */
    public Datados getDatados() {
        return datados;
    }

    /**
     * Sets the value of the datados property.
     * 
     * @param value
     *     allowed object is
     *     {@link Datados }
     *     
     */
    public void setDatados(Datados value) {
        this.datados = value;
    }

    /**
     * Gets the value of the certificacion property.
     * 
     * @return
     *     possible object is
     *     {@link Certificacion }
     *     
     */
    public Certificacion getCertificacion() {
        return certificacion;
    }

    /**
     * Sets the value of the certificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificacion }
     *     
     */
    public void setCertificacion(Certificacion value) {
        this.certificacion = value;
    }

}

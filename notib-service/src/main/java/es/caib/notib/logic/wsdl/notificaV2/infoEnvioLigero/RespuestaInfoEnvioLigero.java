
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.notib.logic.wsdl.notificaV2.common.Opciones;


/**
 * <p>Java class for RespuestaInfoEnvioLigero complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RespuestaInfoEnvioLigero">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcionRespuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datados" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero}Datados" minOccurs="0"/>
 *         &lt;element name="certificacion" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero}Certificacion" minOccurs="0"/>
 *         &lt;element name="opcionesRespuestaInfoEnvioLigero" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/common}Opciones" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaInfoEnvioLigero", propOrder = {
    "codigoRespuesta",
    "descripcionRespuesta",
    "identificador",
    "estado",
    "datados",
    "certificacion",
    "opcionesRespuestaInfoEnvioLigero"
})
public class RespuestaInfoEnvioLigero {

    @XmlElement(required = true)
    protected String codigoRespuesta;
    @XmlElement(required = true)
    protected String descripcionRespuesta;
    protected String identificador;
    protected String estado;
    protected Datados datados;
    protected Certificacion certificacion;
    protected Opciones opcionesRespuestaInfoEnvioLigero;

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

    /**
     * Gets the value of the opcionesRespuestaInfoEnvioLigero property.
     * 
     * @return
     *     possible object is
     *     {@link Opciones }
     *     
     */
    public Opciones getOpcionesRespuestaInfoEnvioLigero() {
        return opcionesRespuestaInfoEnvioLigero;
    }

    /**
     * Sets the value of the opcionesRespuestaInfoEnvioLigero property.
     * 
     * @param value
     *     allowed object is
     *     {@link Opciones }
     *     
     */
    public void setOpcionesRespuestaInfoEnvioLigero(Opciones value) {
        this.opcionesRespuestaInfoEnvioLigero = value;
    }

}

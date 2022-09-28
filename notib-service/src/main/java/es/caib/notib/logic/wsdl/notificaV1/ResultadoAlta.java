
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultado_alta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultado_alta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="identificadores" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfIdentificador_envio"/>
 *         &lt;element name="codigo_csv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigo_respuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcion_respuesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultado_alta", propOrder = {

})
public class ResultadoAlta {

    @XmlElement(required = true)
    protected ArrayOfIdentificadorEnvio identificadores;
    @XmlElement(name = "codigo_csv", required = true, nillable = true)
    protected String codigoCsv;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the identificadores property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfIdentificadorEnvio }
     *     
     */
    public ArrayOfIdentificadorEnvio getIdentificadores() {
        return identificadores;
    }

    /**
     * Sets the value of the identificadores property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfIdentificadorEnvio }
     *     
     */
    public void setIdentificadores(ArrayOfIdentificadorEnvio value) {
        this.identificadores = value;
    }

    /**
     * Gets the value of the codigoCsv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCsv() {
        return codigoCsv;
    }

    /**
     * Sets the value of the codigoCsv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCsv(String value) {
        this.codigoCsv = value;
    }

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

}

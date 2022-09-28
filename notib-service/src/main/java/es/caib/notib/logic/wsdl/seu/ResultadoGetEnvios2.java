
package es.caib.notib.logic.wsdl.seu;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultadoGetEnvios2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultadoGetEnvios2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="resultadoGetEnvios2" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}ArrayOfResultadoGetEnvio2"/>
 *         &lt;element name="hayMasResultados" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "resultadoGetEnvios2", propOrder = {

})
public class ResultadoGetEnvios2 {

    @XmlElement(required = true)
    protected ArrayOfResultadoGetEnvio2 resultadoGetEnvios2;
    protected boolean hayMasResultados;
    @XmlElement(name = "codigo_respuesta", required = true)
    protected String codigoRespuesta;
    @XmlElement(name = "descripcion_respuesta", required = true)
    protected String descripcionRespuesta;

    /**
     * Gets the value of the resultadoGetEnvios2 property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfResultadoGetEnvio2 }
     *     
     */
    public ArrayOfResultadoGetEnvio2 getResultadoGetEnvios2() {
        return resultadoGetEnvios2;
    }

    /**
     * Sets the value of the resultadoGetEnvios2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfResultadoGetEnvio2 }
     *     
     */
    public void setResultadoGetEnvios2(ArrayOfResultadoGetEnvio2 value) {
        this.resultadoGetEnvios2 = value;
    }

    /**
     * Gets the value of the hayMasResultados property.
     * 
     */
    public boolean isHayMasResultados() {
        return hayMasResultados;
    }

    /**
     * Sets the value of the hayMasResultados property.
     * 
     */
    public void setHayMasResultados(boolean value) {
        this.hayMasResultados = value;
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

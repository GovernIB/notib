
package es.caib.notib.logic.wsdl.notificaV1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for cie complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cie">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="centro_impresion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fecha_vigencia" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="codigo_unidad_relacionada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombre_unidad_relacionada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="orden" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cie", propOrder = {

})
public class Cie {

    protected int id;
    @XmlElement(name = "centro_impresion", required = true)
    protected String centroImpresion;
    @XmlElement(name = "fecha_vigencia", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fechaVigencia;
    @XmlElement(name = "codigo_unidad_relacionada", required = true)
    protected String codigoUnidadRelacionada;
    @XmlElement(name = "nombre_unidad_relacionada", required = true)
    protected String nombreUnidadRelacionada;
    protected int orden;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the centroImpresion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCentroImpresion() {
        return centroImpresion;
    }

    /**
     * Sets the value of the centroImpresion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCentroImpresion(String value) {
        this.centroImpresion = value;
    }

    /**
     * Gets the value of the fechaVigencia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaVigencia() {
        return fechaVigencia;
    }

    /**
     * Sets the value of the fechaVigencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaVigencia(XMLGregorianCalendar value) {
        this.fechaVigencia = value;
    }

    /**
     * Gets the value of the codigoUnidadRelacionada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoUnidadRelacionada() {
        return codigoUnidadRelacionada;
    }

    /**
     * Sets the value of the codigoUnidadRelacionada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoUnidadRelacionada(String value) {
        this.codigoUnidadRelacionada = value;
    }

    /**
     * Gets the value of the nombreUnidadRelacionada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreUnidadRelacionada() {
        return nombreUnidadRelacionada;
    }

    /**
     * Sets the value of the nombreUnidadRelacionada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreUnidadRelacionada(String value) {
        this.nombreUnidadRelacionada = value;
    }

    /**
     * Gets the value of the orden property.
     * 
     */
    public int getOrden() {
        return orden;
    }

    /**
     * Sets the value of the orden property.
     * 
     */
    public void setOrden(int value) {
        this.orden = value;
    }

}


package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for OrganismoPagadorCIE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganismoPagadorCIE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codigoDIR3CIE" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}CodigoDIR"/>
 *         &lt;element name="fechaVigenciaCIE" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganismoPagadorCIE", propOrder = {

})
public class OrganismoPagadorCIE {

    @XmlElement(required = true)
    protected es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR codigoDIR3CIE;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fechaVigenciaCIE;

    /**
     * Gets the value of the codigoDIR3CIE property.
     * 
     * @return
     *     possible object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     *     
     */
    public es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR getCodigoDIR3CIE() {
        return codigoDIR3CIE;
    }

    /**
     * Sets the value of the codigoDIR3CIE property.
     * 
     * @param value
     *     allowed object is
     *     {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.CodigoDIR }
     *     
     */
    public void setCodigoDIR3CIE(CodigoDIR value) {
        this.codigoDIR3CIE = value;
    }

    /**
     * Gets the value of the fechaVigenciaCIE property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaVigenciaCIE() {
        return fechaVigenciaCIE;
    }

    /**
     * Sets the value of the fechaVigenciaCIE property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaVigenciaCIE(XMLGregorianCalendar value) {
        this.fechaVigenciaCIE = value;
    }

}

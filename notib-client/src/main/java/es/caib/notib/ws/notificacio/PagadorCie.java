
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for pagadorCie complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pagadorCie">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contracteDataVigencia" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dir3Codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagadorCie", propOrder = {
    "contracteDataVigencia",
    "dir3Codi"
})
public class PagadorCie {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar contracteDataVigencia;
    protected String dir3Codi;

    /**
     * Gets the value of the contracteDataVigencia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getContracteDataVigencia() {
        return contracteDataVigencia;
    }

    /**
     * Sets the value of the contracteDataVigencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setContracteDataVigencia(XMLGregorianCalendar value) {
        this.contracteDataVigencia = value;
    }

    /**
     * Gets the value of the dir3Codi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDir3Codi() {
        return dir3Codi;
    }

    /**
     * Sets the value of the dir3Codi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDir3Codi(String value) {
        this.dir3Codi = value;
    }

}

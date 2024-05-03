
package es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InfoCertificadoInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InfoCertificadoInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Campo" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="idCampo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="valorCampo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoCertificadoInfo", propOrder = {
    "campo"
})
public class InfoCertificadoInfo {

    @XmlElement(name = "Campo", required = true)
    protected List<Campo> campo;

    /**
     * Gets the value of the campo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the campo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCampo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Campo }
     * 
     * 
     */
    public List<Campo> getCampo() {
        if (campo == null) {
            campo = new ArrayList<Campo>();
        }
        return this.campo;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="idCampo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="valorCampo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "idCampo",
        "valorCampo"
    })
    public static class Campo {

        @XmlElement(required = true)
        protected String idCampo;
        @XmlElement(required = true)
        protected String valorCampo;

        /**
         * Gets the value of the idCampo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdCampo() {
            return idCampo;
        }

        /**
         * Sets the value of the idCampo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdCampo(String value) {
            this.idCampo = value;
        }

        /**
         * Gets the value of the valorCampo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValorCampo() {
            return valorCampo;
        }

        /**
         * Sets the value of the valorCampo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValorCampo(String value) {
            this.valorCampo = value;
        }

    }

}


package es.caib.notib.logic.wsdl.notificaV2.getcies;

import es.caib.notib.logic.wsdl.notificaV2.getcies.Cie;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Cies complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Cies">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cie" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/getCies}Cie" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cies", propOrder = {
    "cie"
})
public class Cies {

    protected List<es.caib.notib.logic.wsdl.notificaV2.getcies.Cie> cie;

    /**
     * Gets the value of the cie property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cie property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCie().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link es.caib.notib.logic.wsdl.notificaV2.getcies.Cie }
     * 
     * 
     */
    public List<es.caib.notib.logic.wsdl.notificaV2.getcies.Cie> getCie() {
        if (cie == null) {
            cie = new ArrayList<Cie>();
        }
        return this.cie;
    }

}

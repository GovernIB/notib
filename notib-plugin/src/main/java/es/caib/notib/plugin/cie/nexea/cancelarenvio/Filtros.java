
package es.caib.notib.plugin.cie.nexea.cancelarenvio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Filtros complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Filtros">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filtro" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/cancelarEnvio}Filtro" maxOccurs="unbounded" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Filtros", propOrder = {
    "filtro"
})
public class Filtros {

    @XmlElement(required = true)
    protected List<Filtro> filtro;

    /**
     * Gets the value of the filtro property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filtro property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFiltro().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Filtro }
     * 
     * 
     */
    public List<Filtro> getFiltro() {
        if (filtro == null) {
            filtro = new ArrayList<Filtro>();
        }
        return this.filtro;
    }

}


package es.caib.notib.logic.wsdl.seu;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPorOrganismoRemisor2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPorOrganismoRemisor2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="item" type="{https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/}PorOrganismoRemisor2" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPorOrganismoRemisor2", propOrder = {
    "item"
})
public class ArrayOfPorOrganismoRemisor2 {

    protected List<PorOrganismoRemisor2> item;

    /**
     * Gets the value of the item property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PorOrganismoRemisor2 }
     * 
     * 
     */
    public List<PorOrganismoRemisor2> getItem() {
        if (item == null) {
            item = new ArrayList<PorOrganismoRemisor2>();
        }
        return this.item;
    }

}


package es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Datados complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Datados">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datado" type="{http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/infoEnvioLigero}Datado" maxOccurs="100"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Datados", propOrder = {
    "datado"
})
public class Datados {

    @XmlElement(required = true)
    protected List<Datado> datado;

    /**
     * Gets the value of the datado property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datado property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatado().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Datado }
     * 
     * 
     */
    public List<Datado> getDatado() {
        if (datado == null) {
            datado = new ArrayList<Datado>();
        }
        return this.datado;
    }

}

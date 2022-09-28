
package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Opciones complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Opciones">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="opcion" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Opcion" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Opciones", propOrder = {
    "opcion"
})
public class Opciones {

    protected List<es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion> opcion;

    /**
     * Gets the value of the opcion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the opcion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOpcion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion }
     * 
     * 
     */
    public List<es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Opcion> getOpcion() {
        if (opcion == null) {
            opcion = new ArrayList<Opcion>();
        }
        return this.opcion;
    }

}


package es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2;

import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Destinatarios complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Destinatarios">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="destinatario" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2}Destinatario" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Destinatarios", propOrder = {
    "destinatario"
})
public class Destinatarios {

    protected List<es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario> destinatario;

    /**
     * Gets the value of the destinatario property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinatario property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinatario().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario }
     * 
     * 
     */
    public List<es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Destinatario> getDestinatario() {
        if (destinatario == null) {
            destinatario = new ArrayList<Destinatario>();
        }
        return this.destinatario;
    }

}


package es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios;

import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Envios complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Envios">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="100">
 *         &lt;element name="envio" type="{https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios}Envio"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Envios", propOrder = {
    "envio"
})
public class Envios {

    @XmlElement(required = true)
    protected List<es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envio> envio;

    /**
     * Gets the value of the envio property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the envio property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnvio().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envio }
     * 
     * 
     */
    public List<es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envio> getEnvio() {
        if (envio == null) {
            envio = new ArrayList<Envio>();
        }
        return this.envio;
    }

}

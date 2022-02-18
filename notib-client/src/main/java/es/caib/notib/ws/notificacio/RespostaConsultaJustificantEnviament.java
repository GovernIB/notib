
package es.caib.notib.ws.notificacio;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for respostaConsultaJustificantEnviament complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="respostaConsultaJustificantEnviament">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.caib.es/notib/ws/notificacio}respostaBase">
 *       &lt;sequence>
 *         &lt;element name="justificant" type="{http://www.caib.es/notib/ws/notificacio}fitxer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respostaConsultaJustificantEnviament", propOrder = {
    "justificant"
})
public class RespostaConsultaJustificantEnviament
    extends RespostaBase
{

    protected Fitxer justificant;

    /**
     * Gets the value of the justificant property.
     * 
     * @return
     *     possible object is
     *     {@link Fitxer }
     *     
     */
    public Fitxer getJustificant() {
        return justificant;
    }

    /**
     * Sets the value of the justificant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fitxer }
     *     
     */
    public void setJustificant(Fitxer value) {
        this.justificant = value;
    }

}

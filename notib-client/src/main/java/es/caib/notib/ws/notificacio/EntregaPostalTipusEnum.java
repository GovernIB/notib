
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entregaPostalTipusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="entregaPostalTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NACIONAL"/>
 *     &lt;enumeration value="ESTRANGER"/>
 *     &lt;enumeration value="APARTAT_CORREUS"/>
 *     &lt;enumeration value="SENSE_NORMALITZAR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "entregaPostalTipusEnum")
@XmlEnum
public enum EntregaPostalTipusEnum {

    NACIONAL,
    ESTRANGER,
    APARTAT_CORREUS,
    SENSE_NORMALITZAR;

    public String value() {
        return name();
    }

    public static EntregaPostalTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

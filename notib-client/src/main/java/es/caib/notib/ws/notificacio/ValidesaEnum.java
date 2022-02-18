
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validesaEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="validesaEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COPIA"/>
 *     &lt;enumeration value="COPIA_AUTENTICA"/>
 *     &lt;enumeration value="ORIGINAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "validesaEnum")
@XmlEnum
public enum ValidesaEnum {

    COPIA,
    COPIA_AUTENTICA,
    ORIGINAL;

    public String value() {
        return name();
    }

    public static ValidesaEnum fromValue(String v) {
        return valueOf(v);
    }

}

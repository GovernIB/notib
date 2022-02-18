
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for origenEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="origenEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CIUTADA"/>
 *     &lt;enumeration value="ADMINISTRACIO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "origenEnum")
@XmlEnum
public enum OrigenEnum {

    CIUTADA,
    ADMINISTRACIO;

    public String value() {
        return name();
    }

    public static OrigenEnum fromValue(String v) {
        return valueOf(v);
    }

}


package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enviamentTipusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enviamentTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOTIFICACIO"/>
 *     &lt;enumeration value="COMUNICACIO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "enviamentTipusEnum")
@XmlEnum
public enum EnviamentTipusEnum {

    NOTIFICACIO,
    COMUNICACIO;

    public String value() {
        return name();
    }

    public static EnviamentTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

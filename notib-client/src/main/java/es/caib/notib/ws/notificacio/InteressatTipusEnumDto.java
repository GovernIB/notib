
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for interessatTipusEnumDto.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="interessatTipusEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADMINISTRACIO"/>
 *     &lt;enumeration value="FISICA"/>
 *     &lt;enumeration value="FISICA_SENSE_NIF"/>
 *     &lt;enumeration value="JURIDICA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "interessatTipusEnumDto")
@XmlEnum
public enum InteressatTipusEnumDto {

    ADMINISTRACIO,
    FISICA,
    FISICA_SENSE_NIF,
    JURIDICA;

    public String value() {
        return name();
    }

    public static InteressatTipusEnumDto fromValue(String v) {
        return valueOf(v);
    }

}

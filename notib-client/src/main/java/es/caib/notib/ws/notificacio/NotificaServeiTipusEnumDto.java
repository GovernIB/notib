
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificaServeiTipusEnumDto.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="notificaServeiTipusEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NORMAL"/>
 *     &lt;enumeration value="URGENT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "notificaServeiTipusEnumDto")
@XmlEnum
public enum NotificaServeiTipusEnumDto {

    NORMAL,
    URGENT;

    public String value() {
        return name();
    }

    public static NotificaServeiTipusEnumDto fromValue(String v) {
        return valueOf(v);
    }

}


package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificacioEstatEnumDto.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="notificacioEstatEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDENT"/>
 *     &lt;enumeration value="ENVIADA_NOTIFICA"/>
 *     &lt;enumeration value="PROCESSADA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "notificacioEstatEnumDto")
@XmlEnum
public enum NotificacioEstatEnumDto {

    PENDENT,
    ENVIADA_NOTIFICA,
    PROCESSADA;

    public String value() {
        return name();
    }

    public static NotificacioEstatEnumDto fromValue(String v) {
        return valueOf(v);
    }

}

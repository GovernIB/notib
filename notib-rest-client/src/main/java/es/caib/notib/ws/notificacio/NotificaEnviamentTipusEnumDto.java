
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notificaEnviamentTipusEnumDto.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="notificaEnviamentTipusEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOTIFICACIO"/>
 *     &lt;enumeration value="COMUNICACIO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "notificaEnviamentTipusEnumDto")
@XmlEnum
public enum NotificaEnviamentTipusEnumDto {

    NOTIFICACIO,
    COMUNICACIO;

    public String value() {
        return name();
    }

    public static NotificaEnviamentTipusEnumDto fromValue(String v) {
        return valueOf(v);
    }

}

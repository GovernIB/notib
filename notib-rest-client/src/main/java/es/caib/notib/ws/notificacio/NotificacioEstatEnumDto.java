
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notificacioEstatEnumDto.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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

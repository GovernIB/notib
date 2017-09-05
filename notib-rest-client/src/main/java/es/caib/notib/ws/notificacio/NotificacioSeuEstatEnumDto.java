
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notificacioSeuEstatEnumDto.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="notificacioSeuEstatEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDENT"/>
 *     &lt;enumeration value="ENVIADA"/>
 *     &lt;enumeration value="ERROR_ENVIAMENT"/>
 *     &lt;enumeration value="LLEGIDA"/>
 *     &lt;enumeration value="REBUTJADA"/>
 *     &lt;enumeration value="ERROR_PROCESSAMENT"/>
 *     &lt;enumeration value="LLEGIDA_NOTIFICA"/>
 *     &lt;enumeration value="REBUTJADA_NOTIFICA"/>
 *     &lt;enumeration value="ERROR_NOTIFICA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "notificacioSeuEstatEnumDto")
@XmlEnum
public enum NotificacioSeuEstatEnumDto {

    PENDENT,
    ENVIADA,
    ERROR_ENVIAMENT,
    LLEGIDA,
    REBUTJADA,
    ERROR_PROCESSAMENT,
    LLEGIDA_NOTIFICA,
    REBUTJADA_NOTIFICA,
    ERROR_NOTIFICA;

    public String value() {
        return name();
    }

    public static NotificacioSeuEstatEnumDto fromValue(String v) {
        return valueOf(v);
    }

}

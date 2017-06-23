
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificacioEstatEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="notificacioEstatEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AUSENT"/>
 *     &lt;enumeration value="DESCONEGUT"/>
 *     &lt;enumeration value="ADRESA_INCORRECTA"/>
 *     &lt;enumeration value="EDITANT"/>
 *     &lt;enumeration value="ENVIADA_CENTRE_IMPRESSIO"/>
 *     &lt;enumeration value="ENVIADA_DEH"/>
 *     &lt;enumeration value="LLEGIDA"/>
 *     &lt;enumeration value="ERROR_ENVIAMENT"/>
 *     &lt;enumeration value="EXTRAVIADA"/>
 *     &lt;enumeration value="MORT"/>
 *     &lt;enumeration value="NOTIFICADA"/>
 *     &lt;enumeration value="PENDENT_ENVIAMENT"/>
 *     &lt;enumeration value="PENDENT_COMPAREIXENSA"/>
 *     &lt;enumeration value="REBUTJADA"/>
 *     &lt;enumeration value="DATA_ENVIAMENT_PROGRAMAT"/>
 *     &lt;enumeration value="SENSE_INFORMACIO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "notificacioEstatEnum")
@XmlEnum
public enum NotificacioEstatEnum {

    AUSENT,
    DESCONEGUT,
    ADRESA_INCORRECTA,
    EDITANT,
    ENVIADA_CENTRE_IMPRESSIO,
    ENVIADA_DEH,
    LLEGIDA,
    ERROR_ENVIAMENT,
    EXTRAVIADA,
    MORT,
    NOTIFICADA,
    PENDENT_ENVIAMENT,
    PENDENT_COMPAREIXENSA,
    REBUTJADA,
    DATA_ENVIAMENT_PROGRAMAT,
    SENSE_INFORMACIO;

    public String value() {
        return name();
    }

    public static NotificacioEstatEnum fromValue(String v) {
        return valueOf(v);
    }

}

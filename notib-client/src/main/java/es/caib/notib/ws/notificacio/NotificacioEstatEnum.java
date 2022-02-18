
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
 *     &lt;enumeration value="PENDENT"/>
 *     &lt;enumeration value="ENVIADA"/>
 *     &lt;enumeration value="REGISTRADA"/>
 *     &lt;enumeration value="FINALITZADA"/>
 *     &lt;enumeration value="PROCESSADA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "notificacioEstatEnum")
@XmlEnum
public enum NotificacioEstatEnum {

    PENDENT,
    ENVIADA,
    REGISTRADA,
    FINALITZADA,
    PROCESSADA;

    public String value() {
        return name();
    }

    public static NotificacioEstatEnum fromValue(String v) {
        return valueOf(v);
    }

}

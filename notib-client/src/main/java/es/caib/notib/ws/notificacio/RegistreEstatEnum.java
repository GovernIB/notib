
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for registreEstatEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="registreEstatEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VALID"/>
 *     &lt;enumeration value="RESERVA"/>
 *     &lt;enumeration value="PENDENT"/>
 *     &lt;enumeration value="OFICI_EXTERN"/>
 *     &lt;enumeration value="OFICI_INTERN"/>
 *     &lt;enumeration value="OFICI_ACCEPTAT"/>
 *     &lt;enumeration value="DISTRIBUIT"/>
 *     &lt;enumeration value="ANULAT"/>
 *     &lt;enumeration value="RECTIFICAT"/>
 *     &lt;enumeration value="REBUTJAT"/>
 *     &lt;enumeration value="REENVIAT"/>
 *     &lt;enumeration value="DISTRIBUINT"/>
 *     &lt;enumeration value="OFICI_SIR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "registreEstatEnum")
@XmlEnum
public enum RegistreEstatEnum {

    VALID,
    RESERVA,
    PENDENT,
    OFICI_EXTERN,
    OFICI_INTERN,
    OFICI_ACCEPTAT,
    DISTRIBUIT,
    ANULAT,
    RECTIFICAT,
    REBUTJAT,
    REENVIAT,
    DISTRIBUINT,
    OFICI_SIR;

    public String value() {
        return name();
    }

    public static RegistreEstatEnum fromValue(String v) {
        return valueOf(v);
    }

}

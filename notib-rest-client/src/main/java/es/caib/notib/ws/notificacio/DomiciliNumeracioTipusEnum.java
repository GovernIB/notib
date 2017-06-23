
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for domiciliNumeracioTipusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="domiciliNumeracioTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NUMERO"/>
 *     &lt;enumeration value="PUNT_KILOMETRIC"/>
 *     &lt;enumeration value="SENSE_NUMERO"/>
 *     &lt;enumeration value="APARTAT_CORREUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "domiciliNumeracioTipusEnum")
@XmlEnum
public enum DomiciliNumeracioTipusEnum {

    NUMERO,
    PUNT_KILOMETRIC,
    SENSE_NUMERO,
    APARTAT_CORREUS;

    public String value() {
        return name();
    }

    public static DomiciliNumeracioTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

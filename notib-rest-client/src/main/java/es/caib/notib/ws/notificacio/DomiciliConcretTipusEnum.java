
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for domiciliConcretTipusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="domiciliConcretTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NACIONAL"/>
 *     &lt;enumeration value="ESTRANGER"/>
 *     &lt;enumeration value="SENSE_NORMALITZAR"/>
 *     &lt;enumeration value="APARTAT_CORREUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "domiciliConcretTipusEnum")
@XmlEnum
public enum DomiciliConcretTipusEnum {

    NACIONAL,
    ESTRANGER,
    SENSE_NORMALITZAR,
    APARTAT_CORREUS;

    public String value() {
        return name();
    }

    public static DomiciliConcretTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

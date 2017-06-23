
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for certificacioTipusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="certificacioTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="JUSTIFICANT"/>
 *     &lt;enumeration value="SOBRE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "certificacioTipusEnum")
@XmlEnum
public enum CertificacioTipusEnum {

    JUSTIFICANT,
    SOBRE;

    public String value() {
        return name();
    }

    public static CertificacioTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

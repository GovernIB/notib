
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for idiomaEnumDto.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="idiomaEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CA"/>
 *     &lt;enumeration value="ES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "idiomaEnumDto")
@XmlEnum
public enum IdiomaEnumDto {

    CA,
    ES;

    public String value() {
        return name();
    }

    public static IdiomaEnumDto fromValue(String v) {
        return valueOf(v);
    }

}


package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para certificacioArxiuTipusEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="certificacioArxiuTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PDF"/>
 *     &lt;enumeration value="XML"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "certificacioArxiuTipusEnum")
@XmlEnum
public enum CertificacioArxiuTipusEnum {

    PDF,
    XML;

    public String value() {
        return name();
    }

    public static CertificacioArxiuTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}


package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para domiciliTipusEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="domiciliTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FISCAL"/>
 *     &lt;enumeration value="CONCRET"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "domiciliTipusEnum")
@XmlEnum
public enum DomiciliTipusEnum {

    FISCAL,
    CONCRET;

    public String value() {
        return name();
    }

    public static DomiciliTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

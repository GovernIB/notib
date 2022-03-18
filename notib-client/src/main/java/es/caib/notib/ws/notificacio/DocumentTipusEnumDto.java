/**
 * 
 */
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for documentTipusEnumDto.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="documentTipusEnumDto">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PASSAPORT"/>
 *     &lt;enumeration value="ESTRANGER"/>
 *     &lt;enumeration value="ALTRE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "interessatTipusEnumDto")
@XmlEnum
public enum DocumentTipusEnumDto {
	PASSAPORT,
	ESTRANGER,
	ALTRE
}

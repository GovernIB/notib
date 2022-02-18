
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entregaPostalViaTipusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="entregaPostalViaTipusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALAMEDA"/>
 *     &lt;enumeration value="CALLE"/>
 *     &lt;enumeration value="CAMINO"/>
 *     &lt;enumeration value="CARRER"/>
 *     &lt;enumeration value="CARRETERA"/>
 *     &lt;enumeration value="GLORIETA"/>
 *     &lt;enumeration value="KALEA"/>
 *     &lt;enumeration value="PASAJE"/>
 *     &lt;enumeration value="PASEO"/>
 *     &lt;enumeration value="PLAÇA"/>
 *     &lt;enumeration value="PLAZA"/>
 *     &lt;enumeration value="RAMBLA"/>
 *     &lt;enumeration value="RONDA"/>
 *     &lt;enumeration value="RUA"/>
 *     &lt;enumeration value="SECTOR"/>
 *     &lt;enumeration value="TRAVESIA"/>
 *     &lt;enumeration value="URBANIZACION"/>
 *     &lt;enumeration value="AVENIDA"/>
 *     &lt;enumeration value="AVINGUDA"/>
 *     &lt;enumeration value="BARRIO"/>
 *     &lt;enumeration value="CALLEJA"/>
 *     &lt;enumeration value="CAMI"/>
 *     &lt;enumeration value="CAMPO"/>
 *     &lt;enumeration value="CARRERA"/>
 *     &lt;enumeration value="CUESTA"/>
 *     &lt;enumeration value="EDIFICIO"/>
 *     &lt;enumeration value="ENPARANTZA"/>
 *     &lt;enumeration value="ESTRADA"/>
 *     &lt;enumeration value="JARDINES"/>
 *     &lt;enumeration value="JARDINS"/>
 *     &lt;enumeration value="PARQUE"/>
 *     &lt;enumeration value="PASSEIG"/>
 *     &lt;enumeration value="PRAZA"/>
 *     &lt;enumeration value="PLAZUELA"/>
 *     &lt;enumeration value="PLACETA"/>
 *     &lt;enumeration value="POBLADO"/>
 *     &lt;enumeration value="VIA"/>
 *     &lt;enumeration value="TRAVESSERA"/>
 *     &lt;enumeration value="PASSATGE"/>
 *     &lt;enumeration value="BULEVAR"/>
 *     &lt;enumeration value="POLIGONO"/>
 *     &lt;enumeration value="OTROS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "entregaPostalViaTipusEnum")
@XmlEnum
public enum EntregaPostalViaTipusEnum {

    ALAMEDA,
    CALLE,
    CAMINO,
    CARRER,
    CARRETERA,
    GLORIETA,
    KALEA,
    PASAJE,
    PASEO,
    PLAÇA,
    PLAZA,
    RAMBLA,
    RONDA,
    RUA,
    SECTOR,
    TRAVESIA,
    URBANIZACION,
    AVENIDA,
    AVINGUDA,
    BARRIO,
    CALLEJA,
    CAMI,
    CAMPO,
    CARRERA,
    CUESTA,
    EDIFICIO,
    ENPARANTZA,
    ESTRADA,
    JARDINES,
    JARDINS,
    PARQUE,
    PASSEIG,
    PRAZA,
    PLAZUELA,
    PLACETA,
    POBLADO,
    VIA,
    TRAVESSERA,
    PASSATGE,
    BULEVAR,
    POLIGONO,
    OTROS;

    public String value() {
        return name();
    }

    public static EntregaPostalViaTipusEnum fromValue(String v) {
        return valueOf(v);
    }

}

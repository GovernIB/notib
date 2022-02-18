
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tipusDocumentalEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tipusDocumentalEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RESSOLUCIO"/>
 *     &lt;enumeration value="ACORD"/>
 *     &lt;enumeration value="CONTRACTE"/>
 *     &lt;enumeration value="CONVENI"/>
 *     &lt;enumeration value="DECLARACIO"/>
 *     &lt;enumeration value="COMUNICACIO"/>
 *     &lt;enumeration value="NOTIFICACIO"/>
 *     &lt;enumeration value="PUBLICACIO"/>
 *     &lt;enumeration value="JUSTIFICANT_RECEPCIO"/>
 *     &lt;enumeration value="ACTA"/>
 *     &lt;enumeration value="CERTIFICAT"/>
 *     &lt;enumeration value="DILIGENCIA"/>
 *     &lt;enumeration value="INFORME"/>
 *     &lt;enumeration value="SOLICITUD"/>
 *     &lt;enumeration value="DENUNCIA"/>
 *     &lt;enumeration value="ALEGACIO"/>
 *     &lt;enumeration value="RECURS"/>
 *     &lt;enumeration value="COMUNICACIO_CIUTADA"/>
 *     &lt;enumeration value="FACTURA"/>
 *     &lt;enumeration value="ALTRES_INCAUTATS"/>
 *     &lt;enumeration value="ALTRES"/>
 *     &lt;enumeration value="LLEI"/>
 *     &lt;enumeration value="MOCIO"/>
 *     &lt;enumeration value="INSTRUCCIO"/>
 *     &lt;enumeration value="CONVOCATORIA"/>
 *     &lt;enumeration value="ORDRE_DIA"/>
 *     &lt;enumeration value="INFORME_PONENCIA"/>
 *     &lt;enumeration value="DICTAMEN_COMISSIO"/>
 *     &lt;enumeration value="INICIATIVA_LEGISLATIVA"/>
 *     &lt;enumeration value="PREGUNTA"/>
 *     &lt;enumeration value="INTERPELACIO"/>
 *     &lt;enumeration value="RESPOSTA"/>
 *     &lt;enumeration value="PROPOSICIO_NO_LLEI"/>
 *     &lt;enumeration value="ESQUEMA"/>
 *     &lt;enumeration value="PROPOSTA_RESOLUCIO"/>
 *     &lt;enumeration value="COMPAREIXENSA"/>
 *     &lt;enumeration value="SOLICITUD_INFORMACIO"/>
 *     &lt;enumeration value="ESCRIT"/>
 *     &lt;enumeration value="INICIATIVA_LEGISLATIVA2"/>
 *     &lt;enumeration value="PETICIO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tipusDocumentalEnum")
@XmlEnum
public enum TipusDocumentalEnum {

    RESSOLUCIO("RESSOLUCIO"),
    ACORD("ACORD"),
    CONTRACTE("CONTRACTE"),
    CONVENI("CONVENI"),
    DECLARACIO("DECLARACIO"),
    COMUNICACIO("COMUNICACIO"),
    NOTIFICACIO("NOTIFICACIO"),
    PUBLICACIO("PUBLICACIO"),
    JUSTIFICANT_RECEPCIO("JUSTIFICANT_RECEPCIO"),
    ACTA("ACTA"),
    CERTIFICAT("CERTIFICAT"),
    DILIGENCIA("DILIGENCIA"),
    INFORME("INFORME"),
    SOLICITUD("SOLICITUD"),
    DENUNCIA("DENUNCIA"),
    ALEGACIO("ALEGACIO"),
    RECURS("RECURS"),
    COMUNICACIO_CIUTADA("COMUNICACIO_CIUTADA"),
    FACTURA("FACTURA"),
    ALTRES_INCAUTATS("ALTRES_INCAUTATS"),
    ALTRES("ALTRES"),
    LLEI("LLEI"),
    MOCIO("MOCIO"),
    INSTRUCCIO("INSTRUCCIO"),
    CONVOCATORIA("CONVOCATORIA"),
    ORDRE_DIA("ORDRE_DIA"),
    INFORME_PONENCIA("INFORME_PONENCIA"),
    DICTAMEN_COMISSIO("DICTAMEN_COMISSIO"),
    INICIATIVA_LEGISLATIVA("INICIATIVA_LEGISLATIVA"),
    PREGUNTA("PREGUNTA"),
    INTERPELACIO("INTERPELACIO"),
    RESPOSTA("RESPOSTA"),
    PROPOSICIO_NO_LLEI("PROPOSICIO_NO_LLEI"),
    ESQUEMA("ESQUEMA"),
    PROPOSTA_RESOLUCIO("PROPOSTA_RESOLUCIO"),
    COMPAREIXENSA("COMPAREIXENSA"),
    SOLICITUD_INFORMACIO("SOLICITUD_INFORMACIO"),
    ESCRIT("ESCRIT"),
    @XmlEnumValue("INICIATIVA_LEGISLATIVA2")
    INICIATIVA_LEGISLATIVA_2("INICIATIVA_LEGISLATIVA2"),
    PETICIO("PETICIO");
    private final String value;

    TipusDocumentalEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TipusDocumentalEnum fromValue(String v) {
        for (TipusDocumentalEnum c: TipusDocumentalEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

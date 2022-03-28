
package es.caib.notib.ws.notificacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enviamentEstatEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enviamentEstatEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOTIB_PENDENT"/>
 *     &lt;enumeration value="NOTIB_ENVIADA"/>
 *     &lt;enumeration value="ABSENT"/>
 *     &lt;enumeration value="ADRESA_INCORRECTA"/>
 *     &lt;enumeration value="DESCONEGUT"/>
 *     &lt;enumeration value="ENVIADA_CI"/>
 *     &lt;enumeration value="ENVIADA_DEH"/>
 *     &lt;enumeration value="ENVIAMENT_PROGRAMAT"/>
 *     &lt;enumeration value="ENTREGADA_OP"/>
 *     &lt;enumeration value="ERROR_ENTREGA"/>
 *     &lt;enumeration value="EXPIRADA"/>
 *     &lt;enumeration value="EXTRAVIADA"/>
 *     &lt;enumeration value="MORT"/>
 *     &lt;enumeration value="LLEGIDA"/>
 *     &lt;enumeration value="NOTIFICADA"/>
 *     &lt;enumeration value="PENDENT"/>
 *     &lt;enumeration value="PENDENT_ENVIAMENT"/>
 *     &lt;enumeration value="PENDENT_SEU"/>
 *     &lt;enumeration value="PENDENT_CIE"/>
 *     &lt;enumeration value="PENDENT_DEH"/>
 *     &lt;enumeration value="REBUTJADA"/>
 *     &lt;enumeration value="SENSE_INFORMACIO"/>
 *     &lt;enumeration value="FINALITZADA"/>
 *     &lt;enumeration value="ENVIADA"/>
 *     &lt;enumeration value="REGISTRADA"/>
 *     &lt;enumeration value="PROCESSADA"/>
 *     &lt;enumeration value="ANULADA"/>
 *     &lt;enumeration value="ENVIAT_SIR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "enviamentEstatEnum")
@XmlEnum
public enum EnviamentEstatEnum {

    NOTIB_PENDENT,
    NOTIB_ENVIADA,
    ABSENT,
    ADRESA_INCORRECTA,
    DESCONEGUT,
    ENVIADA_CI,
    ENVIADA_DEH,
    ENVIAMENT_PROGRAMAT,
    ENTREGADA_OP,
    ERROR_ENTREGA,
    EXPIRADA,
    EXTRAVIADA,
    MORT,
    LLEGIDA,
    NOTIFICADA,
    PENDENT,
    PENDENT_ENVIAMENT,
    PENDENT_SEU,
    PENDENT_CIE,
    PENDENT_DEH,
    REBUTJADA,
    SENSE_INFORMACIO,
    FINALITZADA,
    ENVIADA,
    REGISTRADA,
    PROCESSADA,
    ANULADA,
    ENVIAT_SIR,
    ENVIADA_AMB_ERRORS,
    FINALITZADA_AMB_ERRORS;

    public String value() {
        return name();
    }

    public static EnviamentEstatEnum fromValue(String v) {
        return valueOf(v);
    }

}

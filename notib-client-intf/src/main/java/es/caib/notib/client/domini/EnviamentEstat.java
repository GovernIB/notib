
package es.caib.notib.client.domini;

import lombok.Getter;

import java.io.Serializable;


/**
 * Enumerat que indica l'estat d'una notificació per a un destinatari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 *
 *     En tramitació --> Encara no s'ha enviat al destinatari (NOTIB_PENDENT, REGISTRADA, NOTIB_ENVIADA, ENVIAMENT_PROGRAMAT)
 *      gris #777; fa fa-clock-o
 * - Pendent de compareixença --> Ja s'ha enviat, i està pendent que l'usuari la llegeixi (ENVIADA, ENVIADA_CI,  ENVIADA_DEH, PENDENT, ENTREGADA_OP, PENDENT_ENVIAMENT,  PENDENT_SEU,  PENDENT_CIE,  PENDENT_DEH)
        taronja #e67e22; fa-envelope-o
 * - Llegida (LLEGIDA, NOTIFICADA)
 *      verd #5cb85c fa-check-circle
 * - Rebutjada (REBUTJADA)
 *      marro #6F5647  fa-times
 * - Expirada (EXPIRADA)
 *      groc #F1D629 fa-asterisk
 * - Anulada (ANULADA)
 *      blau #337ab7 fa-window-close-o
 * - Error (La resta)
 *      vermell #d9534f fa-exclamation-circle
 */
@Getter
public enum EnviamentEstat implements Serializable {

    NOTIB_PENDENT("#777", "fa fa-clock-o"),
    NOTIB_ENVIADA("#777", "fa fa-clock-o"),
    ABSENT("#d9534f", "fa-exclamation-circle"),
    ADRESA_INCORRECTA("#d9534f", "fa fa-exclamation-circle"),
    DESCONEGUT("#d9534f", "fa fa-exclamation-circle"),
    ENVIADA_CI("#e67e22", "fa fa-envelope-o"),
    ENVIADA_DEH("#e67e22", "fa fa-envelope-o"),
    ENVIAMENT_PROGRAMAT("#777", "fa-clock-o"),
    ENTREGADA_OP("#e67e22", "fa fa-envelope-o"),
    ERROR_ENTREGA("#d9534f", "fa fa-exclamation-circle"),
    EXPIRADA("#F1D629", "fa fa-asterisk"),
    EXTRAVIADA("#d9534f", "fa fa-exclamation-circle"),
    MORT("#d9534f", "fa fa-exclamation-circle"),
    LLEGIDA("#5cb85c", "fa fa-check-circle"),
    NOTIFICADA("#5cb85c", "fa fa-check-circle"),
    PENDENT("#e67e22", "fa fa-envelope-o"),
    PENDENT_ENVIAMENT("#e67e22", "fa fa-envelope-o"),
    PENDENT_SEU("#e67e22", "fa fa-envelope-o"),
    PENDENT_CIE("#e67e22", "fa fa-envelope-o"),
    PENDENT_DEH("#e67e22", "fa fa-envelope-o"),
    REBUTJADA("#6F5647", "fa fa-times"),
    SENSE_INFORMACIO("#d9534f", "fa fa-exclamation-circle"),
    FINALITZADA("", "fa fa-send-o"),
    ENVIADA("#e67e22", "fa fa-envelope-o"),
    REGISTRADA("#777", "fa fa-envelope-o"),
    PROCESSADA("", "fa fa-send-o"),
    ANULADA("#337ab7", "fa fa-window-close-o"),
    ENVIAT_SIR("#e67e22", "fa fa-envelope-o"),
    ENVIADA_AMB_ERRORS("", "fa fa-send-o"),
    FINALITZADA_AMB_ERRORS("", "fa fa-send-o");

    private String color;
    private String icona;

    EnviamentEstat(String color, String icona) {
        this.color = color;
        this.icona = icona;
    }
}

package es.caib.notib.client.domini;

import lombok.Getter;

import java.io.Serializable;


/**
 * Enumerat que indica l'estat d'una enviament per a un destinatari.
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

    NOTIB_PENDENT(EnviamentEstatGrup.TRAMITACIO.getColor(), EnviamentEstatGrup.TRAMITACIO.getIcona()), // 0
    NOTIB_ENVIADA(EnviamentEstatGrup.TRAMITACIO.getColor(), EnviamentEstatGrup.TRAMITACIO.getIcona()),
    ABSENT(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    ADRESA_INCORRECTA(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    DESCONEGUT(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    ENVIADA_CI(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()), // 5
    ENVIADA_DEH(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    ENVIAMENT_PROGRAMAT(EnviamentEstatGrup.TRAMITACIO.getColor(), EnviamentEstatGrup.TRAMITACIO.getIcona()),
    ENTREGADA_OP(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    ERROR_ENTREGA(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    EXPIRADA(EnviamentEstatGrup.EXPIRADA.getColor(), EnviamentEstatGrup.EXPIRADA.getIcona()), // 10
    EXTRAVIADA(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    MORT(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    LLEGIDA(EnviamentEstatGrup.LLEGIDA.getColor(), EnviamentEstatGrup.LLEGIDA.getIcona()),
    NOTIFICADA(EnviamentEstatGrup.LLEGIDA.getColor(), EnviamentEstatGrup.LLEGIDA.getIcona()),
    PENDENT(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()), // 15
    PENDENT_ENVIAMENT(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    PENDENT_SEU(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    PENDENT_CIE(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    PENDENT_DEH(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    REBUTJADA(EnviamentEstatGrup.REBUTJADA.getColor(), EnviamentEstatGrup.REBUTJADA.getIcona()), // 20
    SENSE_INFORMACIO(EnviamentEstatGrup.ERROR.getColor(), EnviamentEstatGrup.ERROR.getIcona()),
    FINALITZADA(EnviamentEstatGrup.ESTAT_FICTICI.getColor(), EnviamentEstatGrup.ESTAT_FICTICI.getIcona()),
    ENVIADA(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    REGISTRADA(EnviamentEstatGrup.TRAMITACIO.getColor(), EnviamentEstatGrup.TRAMITACIO.getIcona()),
    PROCESSADA(EnviamentEstatGrup.ESTAT_FICTICI.getColor(), EnviamentEstatGrup.ESTAT_FICTICI.getIcona()), // 25
    ANULADA(EnviamentEstatGrup.ANULADA.getColor(), EnviamentEstatGrup.ANULADA.getIcona()),
    ENVIAT_SIR(EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getColor(), EnviamentEstatGrup.PENDENT_COMPAREIXENCA.getIcona()),
    ENVIADA_AMB_ERRORS(EnviamentEstatGrup.ESTAT_FICTICI.getColor(), EnviamentEstatGrup.ESTAT_FICTICI.getIcona()),
    FINALITZADA_AMB_ERRORS(EnviamentEstatGrup.ESTAT_FICTICI.getColor(), EnviamentEstatGrup.ESTAT_FICTICI.getIcona()); // 29

    private String color;
    private String icona;

    EnviamentEstat(String color, String icona) {
        this.color = color;
        this.icona = icona;
    }
}
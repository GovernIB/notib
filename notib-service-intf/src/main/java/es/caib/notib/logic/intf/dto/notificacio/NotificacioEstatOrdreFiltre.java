package es.caib.notib.logic.intf.dto.notificacio;

import java.io.Serializable;

public enum NotificacioEstatOrdreFiltre implements Serializable {

    PENDENT,
    ENVIANT,
    REGISTRADA,
    ENVIADA,
    ENVIAT_SIR,
    OFICI_ACCEPTAT,
    REBUTJADA_SIR,
    ENVIADA_AMB_ERRORS,
    NOTIFICADA,
    REBUTJADA,
    EXPIRADA,
    FINALITZADA,
    FINALITZADA_AMB_ERRORS,
    PROCESSADA;

}

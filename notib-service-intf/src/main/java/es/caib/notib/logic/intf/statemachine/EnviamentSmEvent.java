package es.caib.notib.logic.intf.statemachine;

public enum EnviamentSmEvent {
    // ReGistrar
    RG_ENVIAR,
    RG_SUCCESS,
    RG_RETRY,
    RG_ERROR,
    // enviar EMail
    EM_ENVIAR,
    EM_SUCCESS,
    EM_RETRY,
    EM_ERROR,
    // NoTificar
    NT_ENVIAR,
    NT_SUCCESS,
    NT_RETRY,
    NT_ERROR,
    // CoNsultar
    CN_CONSULTAR,
    CN_SUCCESS,
    CN_RETRY,
    CN_ERROR,
    // SIR
    SR_CONSULTAR,
    SR_SUCCESS,
    SR_RETRY,
    SR_ERROR
}

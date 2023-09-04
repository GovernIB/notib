package es.caib.notib.logic.intf.statemachine;

public enum EnviamentSmEvent {
    // ReGistrar
    RG_ENVIAR,
    RG_SUCCESS,
    RG_RESET,
    RG_RETRY,
    RG_ERROR,
    RG_FORWARD,
    // enviar EMail
    EM_ENVIAR,
    EM_SUCCESS,
    EM_RETRY,
    EM_ERROR,
    EM_FORWARD,
    // NoTificar
    NT_ENVIAR,
    NT_SUCCESS,
    NT_RESET,
    NT_RETRY,
    NT_ERROR,
    NT_FORWARD,
    // CoNsultar
    CN_CONSULTAR,
    CN_SUCCESS,
    CN_RETRY,
    CN_ERROR,
    CN_FORWARD,
    // SIR
    SR_CONSULTAR,
    SR_SUCCESS,
    SR_RESET,
    SR_RETRY,
    SR_ERROR,
    SR_FORWARD,
}

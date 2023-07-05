package es.caib.notib.logic.intf.statemachine;

public enum EnviamentSmEstat {
    // ReGistrar
    NOU,                    // Notificació donada d'alta al sistema
    REGISTRE_PENDENT,       // Pendent d'enviar a registre
    REGISTRE_RETRY,         // Reintent de registre
    REGISTRE_ERROR,         // Error de registre,
    REGISTRAT,              // Enviament registrat
    // enviar EMail
    EMAIL_PENDENT,              // Enviament per enviar avís per email
    EMAIL_RETRY,            // Reintent d'enviament per email
    EMAIL_ERROR,            // Error al intentar enviar avís per email
    // NoTificar
    NOTIFICA_PENDENT,           // Enviament per enviar a Notifica
    NOTIFICA_RETRY,         // Reintent d'enviament a Notifica
    NOTIFICA_ERROR,         // Notificació amb error al intentar notificar
    NOTIFICA_SENT,          // Enviament enviat a notifica
    // CoNsultar
    CONSULTA_RETRY,         // Reintent de consulta de l'estat de l'enviament a Notifica
    CONSULTA_ERROR,         // Error al consultar l'estat de l'enviament a Notifica
    CONSULTA_ESTAT,         // Estat consultat correctament
    // SIR
    SIR_PENDENT,                // Enviament per enviar a SIR
    SIR_RETRY,              // Reintent de consulta de l'estat de l'enviament a SIR
    SIR_ERROR,              // Error al consultar l'estat de l'enviament a SIR
    SIR_ESTAT,              // Estat consultat correctament
    FI                      // Enviament en estat final
}

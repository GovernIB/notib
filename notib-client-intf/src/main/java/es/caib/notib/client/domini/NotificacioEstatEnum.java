
package es.caib.notib.client.domini;

import java.io.Serializable;


/**
 * Enumerat que indica l'estat de la notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnum implements Serializable {
//    PENDENT,
//    ENVIADA,
//    REGISTRADA,
//    FINALITZADA,
//    PROCESSADA,
//    ENVIADA_AMB_ERRORS,
//    FINALITZADA_AMB_ERRORS

    PENDENT,
    ENVIADA,
    REGISTRADA,
    FINALITZADA,
    PROCESSADA,
    EXPIRADA,
    NOTIFICADA,
    REBUTJADA,
    ENVIAT_SIR,
    ENVIADA_AMB_ERRORS,
    FINALITZADA_AMB_ERRORS,
    ENVIANT;
}

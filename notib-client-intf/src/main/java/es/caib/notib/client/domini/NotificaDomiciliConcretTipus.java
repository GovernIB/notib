
package es.caib.notib.client.domini;

import java.io.Serializable;


/**
 * Enumerat que indica el tipus de domicili de l'entrega postal de l'enviament de Notific@.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliConcretTipus implements Serializable {
    NACIONAL,
    ESTRANGER,
    APARTAT_CORREUS,
    SENSE_NORMALITZAR
}

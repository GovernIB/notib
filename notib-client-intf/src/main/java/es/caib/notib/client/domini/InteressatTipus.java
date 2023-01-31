
package es.caib.notib.client.domini;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus d'interessar de l'enviament de Notific@.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum InteressatTipus implements Serializable {

    ADMINISTRACIO(1L),
    FISICA(2L),
    JURIDICA(3L),
    FISICA_SENSE_NIF(4L);

    private final Long val;

    InteressatTipus(Long val) {
        this.val = val;
    }
    public Long getLongVal() {
        return val;
    }

    public static boolean isAdministracio(InteressatTipus interessatTipus) {
        return ADMINISTRACIO.equals(interessatTipus);
    }
}

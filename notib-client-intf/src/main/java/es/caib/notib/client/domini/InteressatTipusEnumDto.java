
package es.caib.notib.client.domini;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus d'interessar de l'enviament de Notific@.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum InteressatTipusEnumDto implements Serializable {

    ADMINISTRACIO(1L),
    FISICA(2L),
    JURIDICA(3L),
    FISICA_SENSE_NIF(4L);

    private final Long val;

    InteressatTipusEnumDto(Long val) {
        this.val = val;
    }
    public Long getLongVal() {
        return val;
    }

    public static boolean isAdministracio(InteressatTipusEnumDto interessatTipus) {
        return ADMINISTRACIO.equals(interessatTipus);
    }
}

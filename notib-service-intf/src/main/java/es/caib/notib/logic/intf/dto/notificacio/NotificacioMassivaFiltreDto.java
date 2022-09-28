package es.caib.notib.logic.intf.dto.notificacio;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Filtre de la taula de notificacions massives
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class NotificacioMassivaFiltreDto implements Serializable {
    private Date dataInici;
    private Date dataFi;
    private NotificacioMassivaEstatDto estatValidacio;
    private NotificacioMassivaEstatDto estatProces;
    private String createdByCodi;
    private boolean nomesAmbErrors;
}

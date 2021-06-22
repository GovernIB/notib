package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Data;

import java.util.Date;

@Data
public class NotificacioMassivaFiltreCommand {
    private Date dataInici;
    private Date dataFi;
    private NotificacioMassivaEstatDto estat;
    private String createdByCodi;
    private boolean nomesAmbErrors;

    public NotificacioMassivaFiltreDto asDto() {
        NotificacioMassivaFiltreDto dto = ConversioTipusHelper.convertir(
                this,
                NotificacioMassivaFiltreDto.class);
        return dto;
    }
}

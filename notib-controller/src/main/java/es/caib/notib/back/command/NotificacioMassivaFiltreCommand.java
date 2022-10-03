package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Data;

import java.util.Date;

@Data
public class NotificacioMassivaFiltreCommand {
    private Date dataInici;
    private Date dataFi;
    private NotificacioMassivaEstatDto estatValidacio;
    private NotificacioMassivaEstatDto estatProces;
    private String createdByCodi;
    private boolean nomesAmbErrors;

    public NotificacioMassivaFiltreDto asDto() {
        NotificacioMassivaFiltreDto dto = ConversioTipusHelper.convertir(
                this,
                NotificacioMassivaFiltreDto.class);
        return dto;
    }
}

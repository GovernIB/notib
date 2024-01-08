package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.back.helper.ConversioTipusHelper;
import lombok.Data;

import java.util.Date;

@Data
public class NotificacioMassivaFiltreCommand extends FiltreCommand {

    private Date dataInici;
    private Date dataFi;
    private NotificacioMassivaEstatDto estatValidacio;
    private NotificacioMassivaEstatDto estatProces;
    private String createdByCodi;
    private boolean nomesAmbErrors;

    public void setDataInici(Date dataInici) {

        validarData(dataInici, "notificacio.list.filtre.camp.datainici");
        this.dataInici = dataInici;
    }

    public void setDataFi(Date dataFi) {

        validarData(dataFi, "notificacio.list.filtre.camp.datafi");
        this.dataFi = dataFi;
    }

    public NotificacioMassivaFiltreDto asDto() {
        return ConversioTipusHelper.convertir(this, NotificacioMassivaFiltreDto.class);
    }
}

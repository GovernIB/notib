package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.notificacio.ColumnesRemeses;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Data
public class ColumnesRemesesCommand {

    private Long id;
    private boolean dataCreacio;
    private boolean dataEnviament;
    private boolean numRegistre;
    private boolean organEmisor;
    private boolean procSerCodi;
    private boolean numExpedient;
    private boolean concepte;
    private boolean creadaPer;
    private boolean interessats;
    private boolean estat;


    public static ColumnesRemesesCommand asCommand(ColumnesRemeses dto) {
        return ConversioTipusHelper.convertir(dto, ColumnesRemesesCommand.class);
    }
    public static ColumnesRemeses asDto(ColumnesRemesesCommand command) {
        return ConversioTipusHelper.convertir(command, ColumnesRemeses.class);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnularCommand {

    private Long notificacioId;
    private Long enviamentId;
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
    private boolean massiu;
    private SeleccioTipus seleccioTipus;
}

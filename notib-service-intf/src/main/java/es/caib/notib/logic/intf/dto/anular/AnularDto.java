package es.caib.notib.logic.intf.dto.anular;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnularDto {

    private Long notificacioId;
    private Long enviamentId;
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
    private boolean massiu;
    private Long accioMassiva;
}

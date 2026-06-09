package es.caib.notib.logic.intf.dto.anular;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AnularDto implements Serializable {

    private Long notificacioId;
    private Long enviamentId;
	@NotNull
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
    private boolean massiu;
    private Long accioMassiva;
}

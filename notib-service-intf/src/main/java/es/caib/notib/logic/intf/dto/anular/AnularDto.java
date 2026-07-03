package es.caib.notib.logic.intf.dto.anular;

import es.caib.notib.logic.intf.model.AccioMassivaParams;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AnularDto extends AccioMassivaParams implements Serializable {

    private Long notificacioId;
    private Long enviamentId;
	@NotNull
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
    private boolean massiu;
    private Long accioMassiva;
}

package es.caib.notib.logic.intf.dto;

import es.caib.notib.logic.intf.AccioMassivaParams;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AmpliacionPlazoDto extends AccioMassivaParams implements Serializable {

    private Long notificacioId;
    private Long enviamentId;
    private int dies;
	@NotNull
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
    private Long accioMassiva;

	private Date caducitat;
}

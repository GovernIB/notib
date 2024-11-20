package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmpliacionPlazoDto {

    private Long notificacioId;
    private Long enviamentId;
    private int dies;
    private String motiu;
}

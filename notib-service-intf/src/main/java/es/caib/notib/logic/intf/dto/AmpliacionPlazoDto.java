package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AmpliacionPlazoDto {

    private Long notificacioId;
    private Long enviamentId;
    private int dies;
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
}

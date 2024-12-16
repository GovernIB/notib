package es.caib.notib.back.command;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AmpliacionPlazoCommand {

    private Long notificacioId;
    private Long enviamentId;
    private int dies;
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
}

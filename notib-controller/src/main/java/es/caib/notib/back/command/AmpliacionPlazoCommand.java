package es.caib.notib.back.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmpliacionPlazoCommand {

    private Long notificacioId;
    private Long enviamentId;
    private int dies;
    private String motiu;
}

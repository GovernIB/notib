package es.caib.notib.logic.intf.dto.organisme;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Resposta {

    private String msg;
    private boolean error;
}

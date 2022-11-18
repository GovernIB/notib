package es.caib.notib.core.api.dto.missatges;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Missatge {

    private boolean ok;
    private String msg;
}

package es.caib.notib.core.api.dto.organisme;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Resposta {

    public String msg;
    public boolean error;
}

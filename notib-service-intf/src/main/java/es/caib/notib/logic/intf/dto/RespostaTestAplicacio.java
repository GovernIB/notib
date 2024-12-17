package es.caib.notib.logic.intf.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RespostaTestAplicacio {

    private boolean ok;
    private String error;
}

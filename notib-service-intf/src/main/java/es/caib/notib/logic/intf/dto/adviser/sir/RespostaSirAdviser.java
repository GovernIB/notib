package es.caib.notib.logic.intf.dto.adviser.sir;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespostaSirAdviser {

    private boolean ok;
    private String errorDescripcio;
}

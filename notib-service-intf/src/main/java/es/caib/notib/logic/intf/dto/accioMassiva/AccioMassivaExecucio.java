package es.caib.notib.logic.intf.dto.accioMassiva;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccioMassivaExecucio {

    private Long accioId;
    private Long entitatId;
    private Collection<Long> seleccio;
    private String format;

}

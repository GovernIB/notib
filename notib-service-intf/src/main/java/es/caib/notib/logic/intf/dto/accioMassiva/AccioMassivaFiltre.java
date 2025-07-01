package es.caib.notib.logic.intf.dto.accioMassiva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccioMassivaFiltre {

    private Long id;
    private Long entitatId;
    private String usuariCodi;
    private String dataInici;
    private String dataFi;
}

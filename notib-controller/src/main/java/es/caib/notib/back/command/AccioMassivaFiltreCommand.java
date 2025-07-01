package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.logic.intf.dto.callback.CallbackFiltre;
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
public class AccioMassivaFiltreCommand {

    private Long id;
    private Long entitatId;
    private String usuariCodi;
    private String dataInici;
    private String dataFi;

    public AccioMassivaFiltre asDto() {
        return AccioMassivaFiltre.builder().usuariCodi(usuariCodi).dataInici(dataInici).dataFi(dataFi).build();
    }
}

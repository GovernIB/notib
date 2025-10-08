package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElementEstat;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccioMassivaFiltreCommand {

    private Long id;
    private AccioMassivaTipus tipus;
    private Long entitatId;
    private String usuariCodi;
    private String dataInici;
    private String dataFi;
    private AccioMassivaElementEstat estat;

    public AccioMassivaFiltre asDto() {
        return AccioMassivaFiltre.builder()
                .tipus(tipus)
                .usuariCodi(usuariCodi)
                .estat(estat)
                .estatString(estat != null ? estat.name() : null)
                .dataInici(dataInici)
                .dataFi(dataFi).build();
    }
}

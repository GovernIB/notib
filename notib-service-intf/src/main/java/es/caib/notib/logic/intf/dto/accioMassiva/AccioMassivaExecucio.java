package es.caib.notib.logic.intf.dto.accioMassiva;


import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
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

    private AccioMassivaTipus tipus;
    private Long accioId;
    private Long entitatId;
    private SeleccioTipus seleccioTipus;
    private Collection<Long> seleccio;
    private String format;
    private boolean isAdminEntitat;
    private String motiu;
    private AmpliacionPlazoDto ampliacionPlazo;

}

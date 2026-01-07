package es.caib.notib.logic.intf.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FitxerContingut extends FitxerInfo {

    private byte[] contingut;


}

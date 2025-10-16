package es.caib.notib.logic.intf.dto.anular;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Anulacio {

    protected List<String> identificadors;
    protected String motiu;
    protected Long accioMassiva;
}

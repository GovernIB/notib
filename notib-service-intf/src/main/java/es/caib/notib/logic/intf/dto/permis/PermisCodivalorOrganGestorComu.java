package es.caib.notib.logic.intf.dto.permis;

import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermisCodivalorOrganGestorComu {

    private CodiValorOrganGestorComuDto codiValor;
    private PermisDto permis;
}

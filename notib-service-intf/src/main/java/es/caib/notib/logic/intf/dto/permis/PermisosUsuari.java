package es.caib.notib.logic.intf.dto.permis;

import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermisosUsuari {

    private String permisosOrgans;
    private String permisosProcediment;
    private String organsFills;
    private List<CodiValorOrganGestorComuDto> procSerOrgan;
}

package es.caib.notib.core.api.dto.organisme;

import es.caib.notib.core.api.dto.PermisDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrganOrganigrama {

    private OrganDetall organ;
    private List<PermisDto> permisos;
}

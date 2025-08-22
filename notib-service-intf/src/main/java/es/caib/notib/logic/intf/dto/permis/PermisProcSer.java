package es.caib.notib.logic.intf.dto.permis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PermisProcSer {

    private List<String> usuarisAmbPermis;
    private List<String> rolsAmbPermis;
}

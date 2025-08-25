package es.caib.notib.logic.intf.dto.permis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
public class PermisProcSer implements Serializable {

    private List<String> usuarisAmbPermis;
    private List<String> rolsAmbPermis;
}

package es.caib.notib.logic.intf.dto.permis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisosUsuarisFiltre {

    private String usuariCodi;
    private String organGestor;

    public boolean usuariCodiNull() {
        return StringUtils.isEmpty(usuariCodi);
    }
}

package es.caib.notib.logic.intf.dto.permis;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisosUsuarisFiltre {

    private String usuariCodi;

    public boolean usuariCodiNull() {
        return Strings.isNullOrEmpty(usuariCodi);
    }
}

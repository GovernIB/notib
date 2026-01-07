package es.caib.notib.logic.intf.dto.permis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.util.Strings;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisosUsuarisFiltre {

    private String usuariCodi;
    private String organGestor;

    public boolean usuariCodiNull() {
        return Strings.isNullOrEmpty(usuariCodi);
    }
}

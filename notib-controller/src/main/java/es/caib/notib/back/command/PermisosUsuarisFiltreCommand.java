package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.permis.PermisosUsuarisFiltre;
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
public class PermisosUsuarisFiltreCommand {

    private String usuariCodi;

    public PermisosUsuarisFiltre asDto() {
        return PermisosUsuarisFiltre.builder().usuariCodi(usuariCodi).build();
    }
}

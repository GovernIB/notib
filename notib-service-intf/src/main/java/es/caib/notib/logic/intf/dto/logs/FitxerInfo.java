package es.caib.notib.logic.intf.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FitxerInfo {

    private String nom;
    private String mida;
    private String dataCreacio;
    private String dataModificacio;
}

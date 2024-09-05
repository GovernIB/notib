package es.caib.notib.plugin.cie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentificadorCie {

    private String identificador;
    private String nifTitular;
    private boolean error;
}

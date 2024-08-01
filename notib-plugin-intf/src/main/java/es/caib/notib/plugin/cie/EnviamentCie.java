package es.caib.notib.plugin.cie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnviamentCie {

    private TipusRemesa tipus;
    private ProveidorCie proveidor;
    private DestinatariCie destinatari;
    private DocumentCie document;
}

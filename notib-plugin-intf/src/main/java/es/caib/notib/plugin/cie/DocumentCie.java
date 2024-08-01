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
public class DocumentCie {

    private String nom;
    private String contingut;
    private String hash;
    private Boolean normalitzat;
    private TipusImpressio tipusImpressio;
}

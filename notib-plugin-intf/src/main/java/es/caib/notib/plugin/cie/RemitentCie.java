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
public class RemitentCie {

    private String id;
    private String nom;
    private String adressa;
    private String codiPostal;
    private String localitat;
    private String provincia;
    private String logotip;
}

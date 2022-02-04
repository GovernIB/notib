package es.caib.notib.core.api.dto.organisme;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganDetall {

    private String nom;
    private String codi;
    private String oficinaEntitat;
    private String estat;
    private boolean cie;
}

package es.caib.notib.core.api.dto.organisme;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganDetall {

    private Long id;
    private String nom;
    private String codi;
    private String oficinaEntitat;
    private String estat;
    private boolean cie;

    public static OrganDetall toOrganDetall(OrganGestorDto o) {
        return OrganDetall.builder().id(o.getId()).nom(o.getNom()).codi(o.getCodi())
                .oficinaEntitat(o.getOficina() != null ? o.getOficina().getCodi() : null)
                .estat(o.getEstat() != null ? o.getEstat().name() : null).cie(o.isEntregaCieActiva()).build();
    }
}

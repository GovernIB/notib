package es.caib.notib.plugin.cie;

import es.caib.notib.logic.intf.dto.notificacio.EntregaPostal;
import es.caib.notib.logic.intf.dto.notificacio.Persona;
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
public class DestinatariCie {

    private Persona titular;
    private EntregaPostal entregaPostal;
}

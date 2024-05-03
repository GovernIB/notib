package es.caib.notib.logic.intf.statemachine.dto;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaSirDto {

    // Entitat
    private String entitatNom;
    private String entitatDir3Codi;

    // Notificacio
    private Long notificacioId;
    private String concepte;
    private NotificacioEstatEnumDto notificacioEstat;
    private TipusUsuariEnumDto tipusUsuari;
    private String motiu;

    // Procediment
    private String procedimentNom;

    // Enviament
    private Long id;
    private String uuid;
    private EnviamentEstat estat;
    private String registreNumeroFormatat;
    private String usuari;
    private boolean deleted;

}

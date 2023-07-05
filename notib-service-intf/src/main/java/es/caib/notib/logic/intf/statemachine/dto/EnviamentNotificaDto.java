package es.caib.notib.logic.intf.statemachine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviamentNotificaDto {

    // Entitat
    private String entitatCodi;

    // Notificacio
    private String notificacioUuid;

    // Enviament
    private String uuid;

    // Enviamenta
    private List<String> enviamentsUuid;

}

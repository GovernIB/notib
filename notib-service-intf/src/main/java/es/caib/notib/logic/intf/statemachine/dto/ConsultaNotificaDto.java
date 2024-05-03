package es.caib.notib.logic.intf.statemachine.dto;

import es.caib.notib.client.domini.EnviamentEstat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaNotificaDto {

    // Entitat
    private String entitatCodi;
    private String entitatApiKey;

    // Enviament
    private Long id;
    private String uuid;
    private Date notificaDataCreacio;
    private String notificaIdentificador;
    private EnviamentEstat estat;
    private Date notificaCertificacioData;
    private String notificaCertificacioArxiuId;
    private boolean deleted;

}

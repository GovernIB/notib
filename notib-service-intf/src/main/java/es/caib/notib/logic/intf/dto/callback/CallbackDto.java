package es.caib.notib.logic.intf.dto.callback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallbackDto {

    private Long id;
    private String usuariCodi;
    private String notificacioReferencia;
    private Long notificacioId;
    private String endpoint;
    private Date dataCreacio;
    private Date ultimIntent;
    private String estat;
    private int intents;
    private int maxIntents;
    private Date properIntent;
    private boolean pausat;
}

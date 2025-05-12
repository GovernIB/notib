package es.caib.notib.logic.intf.dto.callback;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
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

    private String usuariCodi;
    private Long notificacioId;
    private Long enviamentId;
    private Date data;
    private boolean error;
    private String errorDesc;
    private CallbackEstatEnumDto estat;
    private int intents;
}

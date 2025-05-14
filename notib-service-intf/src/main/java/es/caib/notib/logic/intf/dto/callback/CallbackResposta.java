package es.caib.notib.logic.intf.dto.callback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackResposta {

    private boolean ok;
    private String errorMsg;
}

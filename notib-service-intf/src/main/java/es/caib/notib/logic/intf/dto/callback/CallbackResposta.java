package es.caib.notib.logic.intf.dto.callback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackResposta implements Serializable {

    private boolean ok;
    private String errorMsg;
}

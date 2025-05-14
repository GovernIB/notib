package es.caib.notib.logic.intf.dto.callback;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackFiltre {

    private String usuariCodi;
    private String referenciaRemesa;
    private Date dataInici;
    private Date dataFi;
    private CallbackEstatEnumDto estat;
    private Boolean fiReintents;
}

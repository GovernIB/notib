package es.caib.notib.back.command;

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
public class CallbackFiltreCommand {

    private String usuariCodi;
    private String uuidRemesa;
    private Date dataInici;
    private Date dataFi;
    private CallbackEstatEnumDto  estat;
}

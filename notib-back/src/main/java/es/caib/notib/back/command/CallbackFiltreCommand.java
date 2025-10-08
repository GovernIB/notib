package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.SiNo;
import es.caib.notib.logic.intf.dto.callback.CallbackFiltre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackFiltreCommand extends FiltreCommand {

    private Long id;
    private String usuariCodi;
    private String referenciaRemesa;
    private String dataInici;
    private String dataFi;
    private String dataIniciUltimIntent;
    private String dataFiUltimIntent;
    private CallbackEstatEnumDto estat;
    private SiNo fiReintents;

    public void setDataInici(String dataInici) {

        validarData(dataInici, "enviament.list.dataenviament.inici");
        this.dataInici = dataInici;
    }

    public void setDataFi(String dataEnviamentFi) {

        validarData(dataEnviamentFi, "enviament.list.dataenviament.fi");
        this.dataFi = dataEnviamentFi;
    }

    public CallbackFiltre asDto() {
        return CallbackFiltre.builder()
                .usuariCodi(usuariCodi)
                .estat(estat)
                .dataInici(dataInici)
                .dataFi(dataFi)
                .dataIniciUltimIntent(dataIniciUltimIntent)
                .dataFiUltimIntent((dataFiUltimIntent))
                .referenciaRemesa(referenciaRemesa)
                .fiReintents(fiReintents).build();
    }


}

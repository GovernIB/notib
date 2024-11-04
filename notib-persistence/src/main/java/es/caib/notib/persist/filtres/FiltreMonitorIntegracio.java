package es.caib.notib.persist.filtres;

import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class FiltreMonitorIntegracio {

    private IntegracioCodi codi;
    private boolean codiEntitatNull;
    private String codiEntitat;
    private boolean aplicacioNull;
    private String aplicacio;
    private boolean descripcioNull;
    private String descripcio;
    private boolean dataIniciNull;
    private Date dataInici;
    private boolean dataFiNull;
    private Date dataFi;
    private boolean tipusNull;
    private IntegracioAccioTipusEnumDto tipus;
    private boolean estatNull;
    private IntegracioAccioEstatEnumDto estat;
}

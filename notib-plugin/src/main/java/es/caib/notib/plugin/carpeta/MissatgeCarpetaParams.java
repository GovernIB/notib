package es.caib.notib.plugin.carpeta;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissatgeCarpetaParams {

    private String nifDestinatari;
    private String nomCompletDestinatari;
    private String codiDir3Entitat;
    private String nomEntitat;
    private String codiOrganEmisor;
    private String concepteNotificacio;
    private String descNotificacio;
    private String uuIdNotificacio;
    private NotificaEnviamentTipusEnumDto tipus;
    private VincleInteressat vincleInteressat;
    private String codiSiaProcediment;
    private String nomProcediment;
    private Date caducitatNotificacio;
    private Date dataDisponibleCompareixenca;
    private String numExpedient;

}

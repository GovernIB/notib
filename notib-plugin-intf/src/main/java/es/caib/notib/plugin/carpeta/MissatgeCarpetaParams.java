package es.caib.notib.plugin.carpeta;

import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.ws.rs.core.MultivaluedMap;
import java.text.SimpleDateFormat;
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
    private String nomOrganEmisor;
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

    public void setQueryParams(MultivaluedMap<String, String> params) {

        var df = new SimpleDateFormat("dd-MM-yyyy");
        var caducitat = caducitatNotificacio != null ? df.format(caducitatNotificacio) : "";
        var compareixenca = dataDisponibleCompareixenca != null ? df.format(dataDisponibleCompareixenca) : "";
        var notificacioParametres = "notificationParameters";
        params.add(notificacioParametres, nomCompletDestinatari != null ? nomCompletDestinatari : "");
        params.add(notificacioParametres, codiDir3Entitat != null ? codiDir3Entitat : "");
        params.add(notificacioParametres, nomEntitat != null ? nomEntitat : "") ;
        params.add(notificacioParametres, nomOrganEmisor != null ? nomOrganEmisor : "");
        params.add(notificacioParametres, codiOrganEmisor != null ? codiOrganEmisor : "");
        params.add(notificacioParametres, concepteNotificacio != null ? concepteNotificacio : "");
        params.add(notificacioParametres, descNotificacio != null ? descNotificacio : "");
        params.add(notificacioParametres, uuIdNotificacio != null ? uuIdNotificacio : "");
        params.add(notificacioParametres, tipus != null ? tipus.name() : "");
        params.add(notificacioParametres, vincleInteressat != null ? vincleInteressat.name() : "");
        params.add(notificacioParametres, codiSiaProcediment != null ? codiSiaProcediment : "");
        params.add(notificacioParametres, nomProcediment != null ? nomProcediment : "");
        params.add(notificacioParametres, caducitat);
        params.add(notificacioParametres, compareixenca);
        params.add(notificacioParametres, numExpedient != null ? numExpedient : "");
    }
}

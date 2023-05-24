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

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String caducitat = caducitatNotificacio != null ? df.format(caducitatNotificacio) : "";
        String compareixenca = dataDisponibleCompareixenca != null ? df.format(dataDisponibleCompareixenca) : "";

        params.add("notificationParameters", nomCompletDestinatari != null ? nomCompletDestinatari : "");
        params.add("notificationParameters", codiDir3Entitat != null ? codiDir3Entitat : "");
        params.add("notificationParameters", nomEntitat != null ? nomEntitat : "") ;
        params.add("notificationParameters", nomOrganEmisor != null ? nomOrganEmisor : "");
        params.add("notificationParameters", codiOrganEmisor != null ? codiOrganEmisor : "");
        params.add("notificationParameters", concepteNotificacio != null ? concepteNotificacio : "");
        params.add("notificationParameters", descNotificacio != null ? descNotificacio : "");
        params.add("notificationParameters", uuIdNotificacio != null ? uuIdNotificacio : "");
        params.add("notificationParameters", tipus != null ? tipus.name() : "");
        params.add("notificationParameters", vincleInteressat != null ? vincleInteressat.name() : "");
        params.add("notificationParameters", codiSiaProcediment != null ? codiSiaProcediment : "");
        params.add("notificationParameters", nomProcediment != null ? nomProcediment : "");
        params.add("notificationParameters", caducitat);
        params.add("notificationParameters", compareixenca);
        params.add("notificationParameters", numExpedient != null ? numExpedient : "");
    }
}

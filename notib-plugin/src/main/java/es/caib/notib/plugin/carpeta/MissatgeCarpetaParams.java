package es.caib.notib.plugin.carpeta;

import com.sun.jersey.api.client.WebResource;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<String> getParams() {

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String caducitat = caducitatNotificacio != null ? df.format(caducitatNotificacio) : "";
        String compareixenca = dataDisponibleCompareixenca != null ? df.format(dataDisponibleCompareixenca) : "";
        List<String> params = new ArrayList<>();
        params.add(nomCompletDestinatari != null ? nomCompletDestinatari : "");
        params.add(codiDir3Entitat != null ? codiDir3Entitat : "");
        params.add(nomEntitat != null ? nomEntitat : "") ;
        params.add(nomOrganEmisor != null ? nomOrganEmisor : "");
        params.add(codiOrganEmisor != null ? codiOrganEmisor : "");
        params.add(concepteNotificacio != null ? concepteNotificacio : "");
        params.add(descNotificacio != null ? descNotificacio : "");
        params.add(uuIdNotificacio != null ? uuIdNotificacio : "");
        params.add(tipus != null ? tipus.name() : "");
        params.add(vincleInteressat != null ? vincleInteressat.name() : "");
        params.add(codiSiaProcediment != null ? codiSiaProcediment : "");
        params.add(nomProcediment != null ? nomProcediment : "");
        params.add(caducitat);
        params.add(compareixenca);
        params.add(numExpedient != null ? numExpedient : "");
        return  params;
//        resource.queryParam("notificationParameters", nomCompletDestinatari != null ? nomCompletDestinatari : "");
//        resource.queryParam("notificationParameters", codiDir3Entitat != null ? codiDir3Entitat : "");
//        resource.queryParam("notificationParameters", nomEntitat != null ? nomEntitat : "") ;
//        resource.queryParam("notificationParameters", nomOrganEmisor != null ? nomOrganEmisor : "");
//        resource.queryParam("notificationParameters", codiOrganEmisor != null ? codiOrganEmisor : "");
//        resource.queryParam("notificationParameters", concepteNotificacio != null ? concepteNotificacio : "");
//        resource.queryParam("notificationParameters", descNotificacio != null ? descNotificacio : "");
//        resource.queryParam("notificationParameters", uuIdNotificacio != null ? uuIdNotificacio : "");
//        resource.queryParam("notificationParameters", tipus != null ? tipus.name() : "");
//        resource.queryParam("notificationParameters", vincleInteressat != null ? vincleInteressat.name() : "");
//        resource.queryParam("notificationParameters", codiSiaProcediment != null ? codiSiaProcediment : "");
//        resource.queryParam("notificationParameters", nomProcediment != null ? nomProcediment : "");
//        resource.queryParam("notificationParameters", caducitat);
//        resource.queryParam("notificationParameters", compareixenca);
//        resource.queryParam("notificationParameters", numExpedient != null ? numExpedient : "");

//        return "&notificationParameters=" + nomCompletDestinatari + "&notificationParameters=" + codiDir3Entitat + "&notificationParameters=" + nomEntitat
//                + "&notificationParameters=" + nomOrganEmisor + "&notificationParameters=" + codiOrganEmisor + "&notificationParameters=" + concepteNotificacio
//                + "&notificationParameters=" + descNotificacio + "&notificationParameters=" + uuIdNotificacio
//                + "&notificationParameters=" + tipus + "&notificationParameters=" + vincleInteressat + "&notificationParameters=" + codiSiaProcediment
//                + "&notificationParameters=" + nomProcediment + "&notificationParameters=" + caducitat
//                + "&notificationParameters=" + compareixenca + "&notificationParameters=" + numExpedient;
    }
}
